package ee.ria.tara.controllers;

import ee.ria.tara.api.LogoutApi;
import ee.ria.tara.api.SsoModeApi;
import ee.ria.tara.api.WhoamiApi;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.SsoModeResponse;
import ee.ria.tara.model.WhoAmIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Objects;

import java.util.Optional;

@Slf4j
@RestController
public class ApiController implements WhoamiApi, LogoutApi, SsoModeApi {
    private final NativeWebRequest request;

    private final AdminConfigurationProvider configurationProvider;

    public ApiController(NativeWebRequest request, AdminConfigurationProvider configurationProvider) {
        this.request = request;
        this.configurationProvider = configurationProvider;
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        log.info(String.format("User %s successfully logged out.",
                SecurityContextHolder.getContext().getAuthentication().getName()));
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(200).build();
    }

    @Override
    public ResponseEntity<WhoAmIResponse> whoAmI() {
        log.info("Incoming request: GET /whoami.");

        SecurityContext context = SecurityContextHolder.getContext();
        WhoAmIResponse response = new WhoAmIResponse();
        Authentication authentication = Objects.requireNonNull(
                context.getAuthentication(), "Authenticated user is missing from the security context");
        response.setUsername(getDisplayName(authentication));

        return ResponseEntity.ok(response);
    }

    private static String getDisplayName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            String fullName = oidcUser.getFullName();
            if (StringUtils.hasText(fullName)) {
                return fullName;
            }
            String preferredUsername = oidcUser.getPreferredUsername();
            if (StringUtils.hasText(preferredUsername)) {
                return preferredUsername;
            }
        }
        return authentication.getName();
    }

    @Override
    public ResponseEntity<SsoModeResponse> ssoMode() {
        log.info("Incoming request: GET /ssoMode.");

        SsoModeResponse response = new SsoModeResponse();
        response.setSsoMode(configurationProvider.isSsoMode());
        return ResponseEntity.ok(response);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
}
