package ee.ria.tara.configuration.providers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("tara-oidc")
public class TaraOidcConfigurationProvider {
    @NotNull
    private String url;
    @Positive
    @Max(500) // https://www.ory.sh/docs/hydra/reference/api
    private int pageSize = 500;
}
