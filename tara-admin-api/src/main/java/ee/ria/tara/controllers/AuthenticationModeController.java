package ee.ria.tara.controllers;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static ee.ria.tara.configuration.AuthenticationProfiles.IN_MEMORY_AUTH;
import static ee.ria.tara.configuration.AuthenticationProfiles.OIDC_AUTH;

@RestController
public class AuthenticationModeController {
    private final Environment environment;

    public AuthenticationModeController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/authMode")
    public Map<String, Boolean> authenticationMode() {
        boolean oidc = environment.acceptsProfiles(Profiles.of(OIDC_AUTH));
        boolean inMemory = environment.acceptsProfiles(Profiles.of(IN_MEMORY_AUTH));
        return Map.of(
                "oidc", oidc,
                "usernamePassword", inMemory || !oidc
        );
    }
}
