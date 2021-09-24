package ee.ria.tara.configuration.providers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("auth")
public class AuthenticationConfigurationProvider {
    @NotNull
    private String ldapDomain;
    @NotNull
    @Value("${spring.ldap.urls}")
    private String ldapUrl;
    @NotNull
    private String ldapAuthority;

    private String inMemoryUsername;
    private String inMemoryPassword;
    private String inMemoryAuthority;
}
