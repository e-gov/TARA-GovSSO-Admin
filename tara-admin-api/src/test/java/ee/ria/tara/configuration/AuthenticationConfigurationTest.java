package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static ee.ria.tara.configuration.AuthenticationProfiles.IN_MEMORY_AUTH;
import static ee.ria.tara.configuration.AuthenticationProfiles.OIDC_AUTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AuthenticationConfiguration.class)
            .withBean(AuthenticationConfigurationProvider.class, AuthenticationConfigurationTest::properties);

    @Test
    void noAuthenticationProfileActivatesLdapAuthentication() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ActiveDirectoryLdapAuthenticationProvider.class);
            assertThat(context).doesNotHaveBean(InMemoryUserDetailsManager.class);
        });
    }

    @Test
    void inMemoryProfileActivatesOnlyInMemoryAuthentication() {
        contextRunner
                .withPropertyValues("spring.profiles.active=" + IN_MEMORY_AUTH)
                .run(context -> {
                    assertThat(context).hasSingleBean(InMemoryUserDetailsManager.class);
                    assertThat(context).doesNotHaveBean(ActiveDirectoryLdapAuthenticationProvider.class);
                });
    }

    @Test
    void oidcProfileDisablesUsernamePasswordAuthenticationProviders() {
        contextRunner
                .withPropertyValues("spring.profiles.active=" + OIDC_AUTH)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(InMemoryUserDetailsManager.class);
                    assertThat(context).doesNotHaveBean(ActiveDirectoryLdapAuthenticationProvider.class);
                });
    }

    @Test
    void inMemoryAndOidcProfilesKeepLdapAuthenticationDisabled() {
        contextRunner
                .withPropertyValues("spring.profiles.active=" + IN_MEMORY_AUTH + "," + OIDC_AUTH)
                .run(context -> {
                    assertThat(context).hasSingleBean(InMemoryUserDetailsManager.class);
                    assertThat(context).doesNotHaveBean(ActiveDirectoryLdapAuthenticationProvider.class);
                });
    }

    private static AuthenticationConfigurationProvider properties() {
        AuthenticationConfigurationProvider properties = mock(AuthenticationConfigurationProvider.class);
        when(properties.getLdapDomain()).thenReturn("example.com");
        when(properties.getLdapUrl()).thenReturn("ldap://ldap.example.com");
        when(properties.getInMemoryUsername()).thenReturn("admin");
        when(properties.getInMemoryPassword()).thenReturn("admin");
        when(properties.getInMemoryAuthority()).thenReturn("admin-authority");
        return properties;
    }
}
