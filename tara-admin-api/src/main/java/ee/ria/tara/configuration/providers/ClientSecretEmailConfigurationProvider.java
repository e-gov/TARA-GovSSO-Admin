package ee.ria.tara.configuration.providers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("tara.admin.email")
public class ClientSecretEmailConfigurationProvider {
    @NotNull
    private String fromEmail;
    @NotNull
    private String fromName;
    @NotNull
    private String subject;
    @NotNull
    private String attachmentFileName;
    @NotNull
    private String templateName;
}
