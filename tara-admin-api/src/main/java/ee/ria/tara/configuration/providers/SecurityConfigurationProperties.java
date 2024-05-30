package ee.ria.tara.configuration.providers;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "tara.admin.security")
public class SecurityConfigurationProperties {

    private static final String DEFAULT_CONTENT_SECURITY_POLICY = "connect-src 'self'; " +
            "default-src 'none'; " +
            "font-src 'self'; " +
            "img-src 'self' data:; " +
            "script-src 'self'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "base-uri 'none'; " +
            "frame-ancestors 'none'; " +
            "block-all-mixed-content";

    @NotBlank
    String contentSecurityPolicy = DEFAULT_CONTENT_SECURITY_POLICY;
}
