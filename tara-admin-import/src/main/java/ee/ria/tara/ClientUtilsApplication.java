package ee.ria.tara;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import ee.ria.tara.conf.FileImportConfiguration;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.Institution;
import ee.ria.tara.repository.helper.PropertyFilterMixIn;
import ee.ria.tara.service.ImportService;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.ScopeFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({
        FileImportConfiguration.class,
        ClientValidator.class,
        AdminConfigurationProvider.class,
        ScopeFilter.class,
})
public class ClientUtilsApplication implements CommandLineRunner {

    @Autowired (required = false)
    private ImportService importService;
    @Autowired (required = false)
    private FileImportConfiguration fileImportConfigurationProvider;

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
}


