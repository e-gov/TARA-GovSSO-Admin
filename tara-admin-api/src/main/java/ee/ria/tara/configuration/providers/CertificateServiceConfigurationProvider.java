package ee.ria.tara.configuration.providers;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("tara.admin.esteid-ldap")
public class CertificateServiceConfigurationProvider {
    @NotNull
    private String url;
    @NotNull
    private Integer port;
}
