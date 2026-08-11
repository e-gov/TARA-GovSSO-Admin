package ee.ria.tara.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.Map;

import static ee.ria.tara.configuration.AuthenticationProfiles.IN_MEMORY_AUTH;
import static ee.ria.tara.configuration.AuthenticationProfiles.OIDC_AUTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationModeControllerTest {

    @Test
    void inMemoryAndOidcProfilesEnableBothAuthenticationMethods() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(IN_MEMORY_AUTH, OIDC_AUTH);

        Map<String, Boolean> mode = new AuthenticationModeController(environment).authenticationMode();

        assertEquals(Map.of("oidc", true, "usernamePassword", true), mode);
    }

    @Test
    void oidcAuthEnablesOnlyOidcAuthentication() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(OIDC_AUTH);

        Map<String, Boolean> mode = new AuthenticationModeController(environment).authenticationMode();

        assertEquals(Map.of("oidc", true, "usernamePassword", false), mode);
    }

    @Test
    void inMemoryAuthEnablesOnlyLocalAuthentication() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(IN_MEMORY_AUTH);

        Map<String, Boolean> mode = new AuthenticationModeController(environment).authenticationMode();

        assertEquals(Map.of("oidc", false, "usernamePassword", true), mode);
    }

    @Test
    void implicitLdapAuthEnablesUsernamePasswordAuthentication() {
        MockEnvironment environment = new MockEnvironment();

        Map<String, Boolean> mode = new AuthenticationModeController(environment).authenticationMode();

        assertEquals(Map.of("oidc", false, "usernamePassword", true), mode);
    }
}
