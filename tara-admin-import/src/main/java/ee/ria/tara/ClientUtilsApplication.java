package ee.ria.tara;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import ee.ria.tara.conf.CasExportConfiguration;
import ee.ria.tara.conf.FileImportConfiguration;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientContact;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import ee.ria.tara.repository.helper.PropertyFilterMixIn;
import ee.ria.tara.service.ImportService;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.ScopeFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({
        CasExportConfiguration.class,
        FileImportConfiguration.class,
        ClientValidator.class,
        AdminConfigurationProvider.class,
        ScopeFilter.class,
})
public class ClientUtilsApplication implements CommandLineRunner {

    public static final List<String> ALLOWED_PROFILES = List.of("importFromFile", "exportFromCas");

    @Autowired
    private Environment environment;
    @Autowired (required = false)
    private ImportService importService;
    @Autowired (required = false)
    private CasExportConfiguration casExportConfigurationProvider;
    @Autowired (required = false)
    private FileImportConfiguration fileImportConfigurationProvider;

    private String defaultInstitutionName = "Example Institution";

    private String defaultInstitutionCode = "12345678";

    private List<String> defaultListOfScopes = List.of("openid", "mid", "idcard", "smartid", "eidas", "eidasonly", "eidas:country:*", "email", "phone");

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String[] fieldsToSkip = new String[] { "secret" };
        final SimpleFilterProvider filter = new SimpleFilterProvider();
        filter.addFilter("custom_serializer", SimpleBeanPropertyFilter.serializeAllExcept(fieldsToSkip));
        mapper.setFilters(filter);
        mapper.addMixIn(Object.class, PropertyFilterMixIn.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientUtilsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {

            List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
            if (Collections.disjoint(activeProfiles, ALLOWED_PROFILES)) {
                log.error("Please specify a valid profile. Hint: -Dspring.profiles.active=(importFromFile|exportFromCas)");
                System.exit(1);
            }

            if (activeProfiles.contains("exportFromCas")) {
                List<Client> clients = exportClientsFromCas();
                exportToExcel(clients);
            }

            if (activeProfiles.contains("importFromFile")) {
                FileImportConfiguration.ImportResult result = importFromFile();
                log.info("\n\n\n" +
                        "  ______ _____ _   _ _____  _____ _    _ ______ _____  \n" +
                        " |  ____|_   _| \\ | |_   _|/ ____| |  | |  ____|  __ \\ \n" +
                        " | |__    | | |  \\| | | | | (___ | |__| | |__  | |  | |\n" +
                        " |  __|   | | | . ` | | |  \\___ \\|  __  |  __| | |  | |\n" +
                        " | |     _| |_| |\\  |_| |_ ____) | |  | | |____| |__| |\n" +
                        " |_|    |_____|_| \\_|_____|_____/|_|  |_|______|_____/ \n" +
                        "                                                       \n" +
                        "                                                       ");
                System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
            }

        } catch (Exception e) {
            log.error("Unexpected error while running import: " + e.getMessage(), e);
            System.exit(1);
        }
    }

    private FileImportConfiguration.ImportResult importFromFile() throws Exception {

        Path filePath = Paths.get(fileImportConfigurationProvider.getFileName());
        log.info(String.format("Importing from file: %s", filePath.toFile().getAbsolutePath()));
        if (filePath.toFile().getName().endsWith(".xlsx")) {
            InputStream inputStream = Files.newInputStream(filePath);
            Map<Institution, List<Client>> clients = importService.importFromExcelFile(inputStream);
            return saveClients(clients);
        } else {
            Map<Institution, List<Client>> clients = importClientsFromJsonFile(filePath);
            return saveClients(clients);
        }
    }

    private void exportToExcel(List<Client> clients) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CAS clients");
        Row header = sheet.createRow(0);

        // create header row
        createCell(header, "Institution name", 0);
        createCell(header, "Institution registry code", 1);
        createCell(header, "Client ID", 2);
        createCell(header, "Redirect URI", 3);
        createCell(header, "Secret", 4);
        createCell(header, "Return URL (legacy)", 5);
        createCell(header, "Client name (et)", 6);
        createCell(header, "Client name (en)", 7);
        createCell(header, "Client name (ru)", 8);
        createCell(header, "Client shortname (et)", 9);
        createCell(header, "Client shortname (en)", 10);
        createCell(header, "Client shortname (ru)", 11);
        createCell(header, "Contacts", 12);
        createCell(header, "Description", 13);

        for (int i = 0; i< clients.size(); i++) {
            Row contentRow = sheet.createRow(i + 1);
            createCell(contentRow, defaultInstitutionName, 0);
            createCell(contentRow, defaultInstitutionCode, 1);
            createCell(contentRow, clients.get(i).getClientId(), 2);
            createCell(contentRow, clients.get(i).getRedirectUris().get(0), 3);
            createCell(contentRow, clients.get(i).getSecret(), 4);
            createCell(contentRow, clients.get(i).getClientUrl(), 5);
            if (clients.get(i).getClientName() != null && !isEmpty(clients.get(i).getClientName().getEt()))
                createCell(contentRow, clients.get(i).getClientName().getEt(), 6);
            if (clients.get(i).getClientName() != null && !isEmpty(clients.get(i).getClientName().getEn()))
                createCell(contentRow, clients.get(i).getClientName().getEn(), 7);
            if (clients.get(i).getClientName() != null && !isEmpty(clients.get(i).getClientName().getRu()))
                createCell(contentRow, clients.get(i).getClientName().getRu(), 8);
            if (clients.get(i).getClientShortName() != null && !isEmpty(clients.get(i).getClientShortName().getEt()))
                createCell(contentRow, clients.get(i).getClientShortName().getEt(), 9);
            if (clients.get(i).getClientShortName() != null && !isEmpty(clients.get(i).getClientShortName().getEn()))
                createCell(contentRow, clients.get(i).getClientShortName().getEn(), 10);
            if (clients.get(i).getClientShortName() != null && !isEmpty(clients.get(i).getClientShortName().getRu()))
                createCell(contentRow, clients.get(i).getClientShortName().getRu(), 11);
            createCell(contentRow, mapper.writeValueAsString(clients.get(i).getClientContacts()), 12);
            createCell(contentRow, clients.get(i).getDescription(), 13);
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "clients.xlsx";
        log.info(String.format("%s clients written to %s",clients.size(), fileLocation));
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

    private Map<Institution, List<Client>> importClientsFromJsonFile(Path path) throws Exception {
        Assert.notNull(fileImportConfigurationProvider, "configuration cannot be null");
        if (Files.exists(path) && Files.isReadable(path)) {
            List<Client> clients = Arrays.asList(mapper.readerFor(Client[].class).with(JsonReadFeature.ALLOW_TRAILING_COMMA).readValue(path.toFile(), Client[].class));
            Institution institution = new Institution();
            institution.setRegistryCode(clients.get(0).getInstitutionMetainfo().getRegistryCode());
            institution.setName(clients.get(0).getInstitutionMetainfo().getName());
            institution.setType(clients.get(0).getInstitutionMetainfo().getType());
            return Map.of(institution, clients);
        } else {
            throw new IllegalStateException(String.format("File %s does not exist or is not readable!", fileImportConfigurationProvider.getFileName() ));
        }
    }

    public List<Client> exportClientsFromCas() throws Exception {
        log.info("Exporting clients from: " + casExportConfigurationProvider.getDbUrl());
        List<Client> clients = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(
                casExportConfigurationProvider.getDbUrl(),
                casExportConfigurationProvider.getUser(),
                casExportConfigurationProvider.getPassword());

             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(casExportConfigurationProvider.getGetCasClientsSql())) {

            while (rs.next()) {
                clients.add(getClientFromCasRecord(rs));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to get clients list from CAS: " + ex.getMessage(), ex);
        }

        return clients;
    }

    private FileImportConfiguration.ImportResult saveClients(Map<Institution, List<Client>> institutionClients) throws Exception {
        List<Client> clientsFailedToMigrate = new ArrayList<>();
        int clientsTotal = 0;
        int clientsImportedOk = 0;

        for (Institution institution : institutionClients.keySet()) {
            for (Client client : institutionClients.get(institution)) {
                Assert.notNull(client.getInstitutionMetainfo(), "Missing institutionMetaInfo");
                Assert.notNull(client.getInstitutionMetainfo().getRegistryCode(), "Missing institutionMetaInfo.registryCode");
                if (importClient(institution, client))
                    clientsImportedOk++;
                else
                    clientsFailedToMigrate.add(client);

                clientsTotal++;
            }
        }

        return FileImportConfiguration.ImportResult.builder()
                .status(clientsFailedToMigrate.size() == 0 ? "FINISHED_SUCCESSFULLY" : "FINISHED_WITH_ERRORS")
                .clientsMigrateSuccessful(clientsImportedOk)
                .clientsFound(clientsTotal)
                .clientsMigrateFailed(clientsFailedToMigrate.size())
                .clientsNotMigrated(mapper.writer().writeValueAsString(clientsFailedToMigrate))
                .build();
    }

    private boolean importClient(Institution institution, Client client) {
        try {
            log.info("Importing client: " + mapper.writer().writeValueAsString(client));
            importService.saveClient(institution, client);
            log.info("Client successfully imported");
            return true;
        } catch (Exception e) {
            log.error("Failed to import client: " + client.getClientId(), e);
            return false;
        }
    }

    private Client getClientFromCasRecord(ResultSet rs) throws Exception {
        Client client = new Client();
        client.setClientId(rs.getString("client_id"));
        client.setRedirectUris(List.of(rs.getString("redirect_url")));
        client.setSecret(rs.getString("client_secret"));
        client.setClientContacts(getContacts(rs.getString("contacts")));
        setLegacyClientUrl(rs.getString("legacy_return_url"), client);

        setClientNameIfExists(
                getObjectValue(rs.getBytes("servicename_et")),
                getObjectValue(rs.getBytes("servicename_en")),
                getObjectValue(rs.getBytes("servicename_ru")), client);

        setClientShortNameIfExists(
                getObjectValue(rs.getBytes("service_shortname_et")),
                getObjectValue(rs.getBytes("service_shortname_en")),
                getObjectValue(rs.getBytes("service_shortname_ru")), client);

        client.setScope(defaultListOfScopes);
        client.setInstitutionMetainfo(getInstitutionMetainfo());
        client.setDescription(rs.getString("description"));
        return client;
    }

    private InstitutionMetainfo getInstitutionMetainfo() {
        InstitutionMetainfo institutionMetainfo = new InstitutionMetainfo();
        InstitutionType type = new InstitutionType();
        type.setType(InstitutionType.TypeEnum.PUBLIC);
        institutionMetainfo.setType(type);
        return institutionMetainfo;
    }

    private List<ClientContact> getContacts(String allContacts) throws SQLException {
        if (isEmpty(allContacts))
            return new ArrayList<>();

        List<ClientContact> clientContacts = new ArrayList<>();
        for (String contact : allContacts.split("\\|", -1)) {

            String[] parts = contact.split(";", -1);
            Assert.isTrue(parts != null && parts.length == 4, "Invalid format when parsing contact - '" + List.of(parts) + "'. All contacts: ' " + allContacts + "'");
            ClientContact clientContact = new ClientContact();
            if (!isEmpty(parts[0]))
                clientContact.setEmail(parts[0]);

            if (!isEmpty(parts[1]))
                clientContact.setPhone(parts[1]);

            if (!isEmpty(parts[2]))
                clientContact.setName(parts[2]);

            if (!isEmpty(parts[3]))
                clientContact.setDepartment(parts[3]);

            clientContacts.add(clientContact);
        }
        return clientContacts;
    }

    private void setLegacyClientUrl(String legacyReturnUrl, Client client) {
        if (!isEmpty(legacyReturnUrl))
            client.setClientUrl(legacyReturnUrl);
    }

    private void setClientShortNameIfExists(String serviceShortNameEt, String serviceShortNameEn, String serviceShortNameRu, Client client) {
        if (isEmpty(serviceShortNameEt)) {
            log.debug("client_short_name not found for " + client.getClientId());
        } else {
            ShortNameTranslations shortNameTranslations = new ShortNameTranslations();
            shortNameTranslations.setEt(serviceShortNameEt);

            if (!isEmpty(serviceShortNameEn))
                shortNameTranslations.setEn(serviceShortNameEn);

            if (!isEmpty(serviceShortNameRu))
                shortNameTranslations.setRu(serviceShortNameRu);

            client.setClientShortName(shortNameTranslations);
        }
    }

    private void setClientNameIfExists(String serviceNameEt, String serviceNameEn, String serviceNameRu, Client client) {
        if (isEmpty(serviceNameEt)) {
            log.debug("client_name not found for " + client.getClientId());
        } else {
            NameTranslations nameTranslations = new NameTranslations();
            nameTranslations.setEt(serviceNameEt);

            if (!isEmpty(serviceNameEn))
                nameTranslations.setEn(serviceNameEn);

            if (!isEmpty(serviceNameRu))
                nameTranslations.setRu(serviceNameRu);

            client.setClientName(nameTranslations);
        }
    }

    private String getObjectValue(byte[] bytes) throws Exception {
        if (bytes != null) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            HashSet hashSet = (HashSet) (objectInputStream.readObject());
            if (hashSet.iterator().hasNext())
                return (String) hashSet.iterator().next();
            else
                return null;
        } else {
            return null;
        }
    }

    private void createCell(Row row, String cellValue, int column) {
        Cell headerCell = row.createCell(column);
        headerCell.setCellValue(cellValue);
    }
}


