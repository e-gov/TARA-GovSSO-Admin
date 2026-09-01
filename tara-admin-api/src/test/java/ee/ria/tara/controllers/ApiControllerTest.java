package ee.ria.tara.controllers;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.WhoAmIResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.context.request.NativeWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiControllerTest {

    private final ApiController controller = new ApiController(
            mock(NativeWebRequest.class), mock(AdminConfigurationProvider.class));

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whoAmIUsesOidcFullName() {
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getFullName()).thenReturn("Jaak-Kristjan Jõeorg");
        setAuthentication(oidcUser, "subject-id");

        ResponseEntity<WhoAmIResponse> response = controller.whoAmI();

        assertNotNull(response.getBody());
        assertEquals("Jaak-Kristjan Jõeorg", response.getBody().getUsername());
    }

    @Test
    void whoAmIFallsBackToAuthenticationNameWhenOidcFullNameIsMissing() {
        OidcUser oidcUser = mock(OidcUser.class);
        setAuthentication(oidcUser, "subject-id");

        ResponseEntity<WhoAmIResponse> response = controller.whoAmI();

        assertNotNull(response.getBody());
        assertEquals("subject-id", response.getBody().getUsername());
    }

    @Test
    void whoAmIUsesOidcPreferredUsernameWhenFullNameIsMissing() {
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getPreferredUsername()).thenReturn("alarkvell");
        setAuthentication(oidcUser, "subject-id");

        ResponseEntity<WhoAmIResponse> response = controller.whoAmI();

        assertNotNull(response.getBody());
        assertEquals("alarkvell", response.getBody().getUsername());
    }

    @Test
    void whoAmIPreservesUsernameForNonOidcAuthentication() {
        setAuthentication(new Object(), "admin");

        ResponseEntity<WhoAmIResponse> response = controller.whoAmI();

        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().getUsername());
    }

    private static void setAuthentication(Object principal, String name) {
        Authentication authentication = mock(Authentication.class);
        doReturn(principal).when(authentication).getPrincipal();
        when(authentication.getName()).thenReturn(name);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
