package ee.ria.tara.configuration.providers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Configuration
@ConfigurationProperties("tara.admin.data-file")
public class ClientSecretDataFileConfigurationProvider {
    @NotNull
    private String encryptedFileName;
    @NotNull
    private String textFileName;
}
