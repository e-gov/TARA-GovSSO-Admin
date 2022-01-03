package ee.ria.tara.configuration.providers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("auth")
public class TlsConfigurationProvider {
    @NotNull
    private String tlsTruststorePath;
    @NotNull
    private String tlsTruststorePassword;
    private String tlsTruststoreType = "PKCS12";
}
