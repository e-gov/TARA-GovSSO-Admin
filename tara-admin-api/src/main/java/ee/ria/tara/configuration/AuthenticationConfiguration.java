package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfiguration {

    private final AuthenticationConfigurationProvider configurationProvider;

    @Bean
    @Profile("!inMemoryAuth")
    AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
                configurationProvider.getLdapDomain(),
                configurationProvider.getLdapUrl()
        );
        provider.setConvertSubErrorCodesToExceptions(true);
        return provider;
    }

    @Bean
    @Profile("inMemoryAuth")
    UserDetailsManager inMemoryUserDetailsManager() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(configurationProvider.getInMemoryUsername())
                .password(passwordEncoder().encode(configurationProvider.getInMemoryPassword()))
                .authorities(configurationProvider.getInMemoryAuthority())
                .build());
        return manager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
