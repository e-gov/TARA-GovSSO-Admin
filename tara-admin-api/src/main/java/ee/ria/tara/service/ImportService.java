package ee.ria.tara.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientContact;
import ee.ria.tara.model.ClientImportResponse;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.repository.helper.PropertyFilterMixIn;
import ee.ria.tara.repository.model.Institution;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.ScopeFilter;
import ee.ria.tara.service.model.ClientImportItem;
import ee.ria.tara.service.model.HydraClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ee.ria.tara.service.OidcService.getDigest;
import static ee.ria.tara.service.helper.ClientHelper.convertToHydraClient;

@Slf4j
@Service
@RequiredArgsConstructor
// Difficult to maintain code, needs to be reworked or removed.
public class ImportService {
    private final ClientRepository clientRepository;
    private final InstitutionRepository institutionRepository;
    private final ClientValidator clientValidator;
    private final ScopeFilter scopeFilter;
    private final RestTemplate restTemplate;
    private final TaraOidcConfigurationProvider taraOidcConfigurationProvider;
    private final PlatformTransactionManager transactionManager;
    private final AdminConfigurationProvider adminConfigurationProvider;

    private final static ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String[] fieldsToSkip = new String[]{"secret"};
        final SimpleFilterProvider filter = new SimpleFilterProvider();
        filter.addFilter("custom_serializer", SimpleBeanPropertyFilter.serializeAllExcept(fieldsToSkip));
        mapper.setFilters(filter);
        mapper.addMixIn(Object.class, PropertyFilterMixIn.class);
    }


    private final List<String> defaultListOfScopes =
            List.of("openid", "mid", "idcard", "smartid", "eidas", "eidasonly", "eidas:country:*", "email", "phone");

    private final DataFormatter formatter = new DataFormatter();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientImportResponse importFromExcelFile(InputStream inputStream) throws Exception {

        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        assertValidHeader(iterator.next());
        Map<ee.ria.tara.model.Institution, List<ClientImportItem>> clientRows = new LinkedHashMap<>();

        while (iterator.hasNext()) {
            Row row = iterator.next();
            Client client = new Client();

            ee.ria.tara.model.Institution institution = new ee.ria.tara.model.Institution();
            institution.setName(getCellValue(row, 0));
            institution.setRegistryCode(getCellValue(row, 1));

            InstitutionMetainfo institutionMetainfo = new InstitutionMetainfo();
            InstitutionType type = new InstitutionType();
            type.setType(InstitutionType.TypeEnum.PUBLIC);
            institutionMetainfo.setType(type);
            institutionMetainfo.setRegistryCode(institution.getRegistryCode());

            // Why constant type PUBLIC?
            institution.setType(type);

            client.setInstitutionMetainfo(institutionMetainfo);
            client.setClientId(getCellValue(row, 2));
            client.setRedirectUris(List.of(getCellValue(row, 3)));
            String secret = getCellValue(row, 4);
            client.setClientUrl(getNullIfEmpty(getCellValue(row, 5)));

            NameTranslations clientName = new NameTranslations();
            clientName.setEt(getNullIfEmpty(getCellValue(row, 6)));
            clientName.setEn(getNullIfEmpty(getCellValue(row, 7)));
            clientName.setRu(getNullIfEmpty(getCellValue(row, 8)));
            client.setClientName(clientName);

            ShortNameTranslations clientShortName = new ShortNameTranslations();
            clientShortName.setEt(getNullIfEmpty(getCellValue(row, 9)));
            clientShortName.setEn(getNullIfEmpty(getCellValue(row, 10)));
            clientShortName.setRu(getNullIfEmpty(getCellValue(row, 11)));
            client.setClientShortName(clientShortName);

            client.setClientContacts(getContacts(row, getNullIfEmpty(getCellValue(row, 12))));
            client.setEidasRequesterId(getCellValue(row, 13));
            client.setDescription(getNullIfEmpty(getCellValue(row, 14)));
            client.setTokenRequestAllowedIpAddresses(List.of(getCellValue(row, 15)));
            String tokenEndpointAuthMethod = getNullIfEmpty(getCellValue(row, 16));
            client.setTokenEndpointAuthMethod(Client.TokenEndpointAuthMethodEnum.fromValue(tokenEndpointAuthMethod));
            client.setScope(defaultListOfScopes);

            clientRows.computeIfAbsent(institution, ignore -> new ArrayList<>())
                    .add(new ClientImportItem(client, secret));
        }

        return saveClients(clientRows);
    }

    public ClientImportResponse saveClients(Map<ee.ria.tara.model.Institution, List<ClientImportItem>> institutionClients) {
        List<String> importFailedClientIds = new ArrayList<>();
        int clientsTotal = 0;
        int clientsImportedOk = 0;

        for (ee.ria.tara.model.Institution institution : institutionClients.keySet()) {
            for (ClientImportItem clientImportItem : institutionClients.get(institution)) {
                Client client = clientImportItem.client();
                Assert.notNull(client.getInstitutionMetainfo(), "Missing institutionMetaInfo");
                Assert.notNull(client.getInstitutionMetainfo().getRegistryCode(), "Missing institutionMetaInfo.registryCode");
                try {
                    saveClient(institution, clientImportItem);
                    clientsImportedOk++;
                    log.atInfo().setMessage("Client successfully imported")
                            .addKeyValue("clientId", client.getClientId())
                            .log();
                } catch (Exception e) {
                    importFailedClientIds.add(client.getClientId());
                    log.atError().setMessage("Failed to import client")
                            .addKeyValue("clientId", client.getClientId())
                            .setCause(e)
                            .log();
                }
                clientsTotal++;
            }
        }

        ClientImportResponse response = new ClientImportResponse();
        response.setStatus(importFailedClientIds.isEmpty() ? "FINISHED_SUCCESSFULLY" : "FINISHED_WITH_ERRORS");
        response.setClientsImportSuccessCount(clientsImportedOk);
        response.setClientsCount(clientsTotal);
        response.setClientsImportFailedCount(importFailedClientIds.size());
        response.setClientsNotImported(importFailedClientIds);
        return response;
    }

    private List<ClientContact> getContacts(Row row, String cellValue) {
        if (cellValue == null)
            return null;

        try {
            return Arrays.asList(objectMapper.readerFor(ClientContact[].class).with(JsonReadFeature.ALLOW_TRAILING_COMMA).readValue(cellValue, ClientContact[].class));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unexpected value for contacts in row: %s (value = '%s')", (row.getRowNum() + 1), cellValue));
        }
    }

    private String getNullIfEmpty(String cellValue) {
        return StringUtils.isEmpty(cellValue) ? null : cellValue;
    }

    private void assertValidHeader(Row row) {
        List<String> expectedColumnValues = List.of("Institution name", "Institution registry code", "Client ID",
                "Redirect URI", "Secret", "Return URL (legacy)", "Client name (et)", "Client name (en)", "Client name (ru)",
                "Client shortname (et)", "Client shortname (en)", "Client shortname (ru)", "Contacts", "eIDAS RequesterID", "Description", "Token request allowed IP addresses");
        List<String> actualColumnValues = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            actualColumnValues.add(getCellValue(row, i));
        }

        if (!expectedColumnValues.equals(actualColumnValues)) {
            log.error(String.format("Invalid header row. Expecting following header columns: %s, but found: %s", expectedColumnValues, actualColumnValues));
            throw new IllegalArgumentException(String.format("Invalid header row. Expecting following header columns: %s", expectedColumnValues));
        }
    }

    @SneakyThrows
    private void saveClient(ee.ria.tara.model.Institution institution, ClientImportItem clientImportItem) {
        if (adminConfigurationProvider.isSsoMode()) {
            throw new UnsupportedOperationException("Client import is not implemented in GovSSO mode.");
        }
        Client client = clientImportItem.client();

        log.info("Importing client: " + mapper.writer().writeValueAsString(clientImportItem));
        String uri = String.format("%s/admin/clients", taraOidcConfigurationProvider.getUrl());

        client.setScope(scopeFilter.filterInstitutionClientScopes(client.getScope(), institution.getType().getType()));
        clientValidator.validateClient(client, institution.getType().getType());

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.executeWithoutResult(status -> {
            try {
                var existingClient = clientRepository.findByClientId(client.getClientId());
                if (existingClient != null) {
                    log.warn(String.format("Client with client_id: '%s' already exists in database with id: '%s'. Removing", client.getClientId(), existingClient.getId()));
                    clientRepository.delete(existingClient);
                    clientRepository.flush();
                    log.warn(String.format("Client removed (client_id: '%s', id: '%s')", client.getClientId(), existingClient.getId()));
                }

                Institution dbInstitution = institutionRepository.findInstitutionByRegistryCode(institution.getRegistryCode());
                if (dbInstitution == null) {
                    dbInstitution = new Institution();
                    dbInstitution.setName(institution.getName());
                    dbInstitution.setRegistryCode(institution.getRegistryCode());
                    dbInstitution.setType(InstitutionType.TypeEnum.PUBLIC);
                    institutionRepository.save(dbInstitution);
                    institutionRepository.flush();
                    log.info(String.format("Added new institution. Name: '%s', Code: '%s'", institution.getName(), institution.getRegistryCode()));
                }

                ee.ria.tara.repository.model.Client clientEntity = ClientHelper.convertToEntity(client, dbInstitution);
                clientRepository.save(clientEntity);
                log.info("Client added: " + clientEntity);
            } catch (Exception e) {
                throw new IllegalStateException("Something went wrong while saving to admin-service database: " + e.getMessage(), e);
            }

            HydraClient hydraClient = convertToHydraClient(client, false);
            HydraClientWithSecret hydraClientWithSecret =
                    new HydraClientWithSecret(hydraClient, getDigest(clientImportItem.secret()));

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(uri + "/" + client.getClientId(), String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    log.info(String.format("Client with client_id: '%s' already exists in OIDC service. Updating", client.getClientId()));
                    restTemplate.exchange(uri + "/" + client.getClientId(), HttpMethod.PUT, new HttpEntity<>(hydraClientWithSecret), Object.class);
                    log.info(String.format("Client updated", client.getClientId()));
                } else {
                    throw new IllegalStateException("Failed to update client in hydra. Unexpected status was returned: " + response.getStatusCode());
                }

            } catch (HttpClientErrorException ex) {
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    log.info(String.format("Adding client with client_id: '%s' to OIDC service", client.getClientId()));
                    restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(hydraClientWithSecret), Object.class);
                    log.info(String.format("Client added with client_id: '%s'", client.getClientId()));
                } else {
                    throw new IllegalStateException("Failed to add client to hydra. Unexpected status was returned: " + ex.getMessage(), ex);
                }
            }
        });
    }

    private String getCellValue(Row row, int i) {
        return formatter.formatCellValue(row.getCell(i));
    }

    private record HydraClientWithSecret(
            @NonNull @JsonUnwrapped HydraClient client,
            @JsonProperty("client_secret") String secret
    ) {}

}
