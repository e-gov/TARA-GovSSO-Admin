package ee.ria.tara;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import ee.ria.tara.conf.FileImportConfiguration;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.ClientImportResponse;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.repository.helper.PropertyFilterMixIn;
import ee.ria.tara.service.ImportService;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.GovSsoScopeFilter;
import ee.ria.tara.service.helper.TaraScopeFilter;
import ee.ria.tara.service.model.ClientImportItem;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({
        FileImportConfiguration.class,
        ClientValidator.class,
        AdminConfigurationProvider.class,
        TaraScopeFilter.class,
        GovSsoScopeFilter.class
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
            ClientImportResponse result = importFromFile();
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

    private ClientImportResponse importFromFile() throws Exception {

        Path filePath = Paths.get(fileImportConfigurationProvider.getFileName());
        log.info(String.format("Importing from file: %s", filePath.toFile().getAbsolutePath()));
        if (filePath.toFile().getName().endsWith(".xlsx")) {
            InputStream inputStream = Files.newInputStream(filePath);
            return importService.importFromExcelFile(inputStream);
        } else {
            Map<Institution, List<ClientImportItem>> clients = importClientsFromJsonFile(filePath);
            return importService.saveClients(clients);
        }
    }

    private Map<Institution, List<ClientImportItem>> importClientsFromJsonFile(Path path) throws Exception {
        Assert.notNull(fileImportConfigurationProvider, "configuration cannot be null");
        if (Files.exists(path) && Files.isReadable(path)) {
            List<ClientImportItem> clients = Arrays.asList(
                    mapper.readerFor(ClientImportItem[].class)
                            .with(JsonReadFeature.ALLOW_TRAILING_COMMA)
                            .readValue(path.toFile(), ClientImportItem[].class));
            InstitutionMetainfo institutionMetainfo = clients.get(0).client().getInstitutionMetainfo();
            Institution institution = new Institution();
            institution.setRegistryCode(institutionMetainfo.getRegistryCode());
            institution.setName(institutionMetainfo.getName());
            institution.setType(institutionMetainfo.getType());
            return Map.of(institution, clients);
        } else {
            throw new IllegalStateException(String.format("File %s does not exist or is not readable!", fileImportConfigurationProvider.getFileName() ));
        }
    }


}


