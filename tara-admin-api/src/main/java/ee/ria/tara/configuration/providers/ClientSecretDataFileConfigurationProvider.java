package ee.ria.tara.configuration.providers;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties("tara.admin.data-file")
public class ClientSecretDataFileConfigurationProvider {
    @NotNull
    private String encryptedFileName;
    @NotNull
    private String textFileName;
}
