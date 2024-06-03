package ee.ria.tara.configuration.providers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("tara.admin")
public class AdminConfigurationProvider {

    boolean ssoMode = false;
    Duration maxAccessTokenLifespan = Duration.ofMinutes(15);

}
