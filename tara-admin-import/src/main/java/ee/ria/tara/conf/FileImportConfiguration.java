package ee.ria.tara.conf;

import ee.ria.tara.configuration.providers.TlsConfigurationProvider;
import ee.ria.tara.service.ImportService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.sql.DataSource;

@Data
@Validated
@Configuration
@Profile("importFromFile")
@Import({ImportService.class, ee.ria.tara.configuration.Configuration.class, TlsConfigurationProvider.class})
public class FileImportConfiguration {

    @Value("${file-import.file-name}")
    private String fileName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String pass;
    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Bean
    public DataSource datasource() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(user)
                .password(pass)
                .build();
    }

    @Builder
    @Data
    public static class ImportResult {
        private String status;
        private int clientsFound;
        private int clientsMigrateSuccessful;
        private int clientsMigrateFailed;
        private String clientsNotMigrated;
    }
}
