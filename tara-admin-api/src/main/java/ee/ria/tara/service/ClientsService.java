package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.helper.NationalIdCodeValidator;
import ee.ria.tara.service.helper.SecureRandomAlphaNumericStringGenerator;
import ee.ria.tara.service.model.HydraClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.ria.tara.service.helper.ClientHelper.convertToClient;
import static ee.ria.tara.service.helper.ClientHelper.convertToHydraClient;

@Slf4j
@Service
public class ClientsService {
    private static final int SIGNING_SECRET_LENGTH = 32;

    private final ClientRepository clientRepository;
    private final InstitutionRepository institutionRepository;
    private final ClientSecretEmailService clientSecretEmailService;
    private final OidcService oidcService;

    @Value("${tara-oidc.url}")
    private String baseUrl;

    public ClientsService(ClientRepository clientRepository,
                          ClientSecretEmailService clientSecretEmailService,
                          InstitutionRepository institutionRepository, OidcService oidcService) {
        this.clientRepository = clientRepository;
        this.clientSecretEmailService = clientSecretEmailService;
        this.institutionRepository = institutionRepository;
        this.oidcService = oidcService;
    }

    public List<Client> getAllClients(String clientId) throws FatalApiException {
        List<HydraClient> hydraClients = this.oidcService.getAllClients(clientId);

        if (clientId != null) {
            ee.ria.tara.repository.model.Client entity = clientRepository.findByClientId(clientId);

            return hydraClients.stream()
                    .map(hydraClient -> convertToClient(hydraClient, entity))
                    .collect(Collectors.toList());
        } else {
            List<Client> clients = new ArrayList<>();
            Map<String, ee.ria.tara.repository.model.Client> clientMap = clientRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(entity -> entity.getClientId(), entity -> entity));

            hydraClients.forEach(hydraClient ->
                    clients.add(convertToClient(hydraClient, clientMap.get(hydraClient.getClientId()))));

            return clients;
        }
    }

    public List<Client> getAllInstitutionsClients(String registryCode) throws ApiException {
        Map<String, ee.ria.tara.repository.model.Client> clientMap;
        List<Client> clients = new ArrayList<>();

        clientMap = clientRepository.findAllByInstitution_RegistryCode(registryCode)
                .stream()
                .collect(Collectors.toMap(entity -> entity.getClientId(), entity -> entity));

        oidcService.getAllClients(null)
                .stream()
                .filter(hydraClient -> clientMap.containsKey(hydraClient.getClientId()))
                .forEach(hydraClient -> clients.add(convertToClient(hydraClient, clientMap.get(hydraClient.getClientId()))));

        return clients;

    }

    @Transactional(rollbackFor = Exception.class)
    public void addClientToInsitution(String registryCode, Client client) throws ApiException {
        String uri = String.format("%s/clients", baseUrl);

        this.saveClient(client, registryCode, uri, HttpMethod.POST);

        log.info(String.format("Added client with client_id %s.", client.getClientId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateClient(String registryCode, String clientId, Client client) throws ApiException {
        String uri = String.format("%s/clients/%s", baseUrl, clientId);
        this.saveClient(client, registryCode, uri, HttpMethod.PUT);

        log.info(String.format("Updated client with client_id %s.", client.getClientId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteClient(String registryCode, String clientId) throws ApiException {
        this.clientRepository.deleteByClientIdAndInstitution_RegistryCode(clientId, registryCode);

        oidcService.deleteClient(clientId);
    }

    private ee.ria.tara.repository.model.Client saveClientEntity(ee.ria.tara.repository.model.Client client) throws InvalidDataException {
        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            log.error(String.format("Failed to save client: %s.", client.getClientId()), ex);
            throw new InvalidDataException("Client.exists");
        }
    }

    private ee.ria.tara.repository.model.Client saveClientEntity(String registryCode, Client client) throws InvalidDataException {
        log.info("Saving client with client_id: " + client.getClientId());
        return this.saveClientEntity(ClientHelper.convertToEntity(client, institutionRepository.findInstitutionByRegistryCode(registryCode)));
    }

    private boolean shouldSendSecret(Client client) {
        ClientSecretExportSettings settings = client.getClientSecretExportSettings();
        return settings != null && settings.getRecipientEmail() != null && settings.getRecipientIdCode() != null;
    }

    private void saveClient(Client client, String registryCode, String uri, HttpMethod httpMethod) throws ApiException {
        if (shouldSendSecret(client)) {
            assertValidIdCode(client.getClientSecretExportSettings());
            client.setSecret(SecureRandomAlphaNumericStringGenerator.INSTANCE.generate(SIGNING_SECRET_LENGTH));

            clientSecretEmailService.sendSigningSecretByEmail(client);
        }
        this.saveClientEntity(registryCode, client);

        oidcService.saveClient(convertToHydraClient(client), uri, httpMethod);
    }

    private void assertValidIdCode(ClientSecretExportSettings clientSecretExportSettings) throws InvalidDataException {
        if (!NationalIdCodeValidator.isValid(clientSecretExportSettings.getRecipientIdCode())) {
            log.error(String.format("Given CDOC national id code is not valid: %s.", clientSecretExportSettings.getRecipientIdCode()));
            throw new InvalidDataException("Client.secret.invalidIdCode");
        }
    }
}
