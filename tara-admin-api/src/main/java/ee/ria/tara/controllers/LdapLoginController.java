package ee.ria.tara.controllers;

import ee.ria.tara.api.LoginApi;
import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import ee.ria.tara.controllers.exception.AuthenticationException;
import ee.ria.tara.model.LoginRequest;
import ee.ria.tara.model.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Slf4j
@Profile("!inMemoryAuth")
@RestController
public class LdapLoginController implements LoginApi {
    final AuthenticationProvider activeDirectoryLdapAuthenticationProvider;
    final AuthenticationConfigurationProvider configurationProvider;

    public LdapLoginController(AuthenticationProvider activeDirectoryLdapAuthenticationProvider,
                               AuthenticationConfigurationProvider configurationProvider) {
        this.activeDirectoryLdapAuthenticationProvider = activeDirectoryLdapAuthenticationProvider;
        this.configurationProvider = configurationProvider;
    }

    @Override
    public ResponseEntity<LoginResponse> loginUser(@Valid LoginRequest loginRequest) {
        log.info(String.format("Incoming login request for user: %s", loginRequest.getUsername()));
        LoginResponse response = new LoginResponse();
        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication;
        try {
            authentication = activeDirectoryLdapAuthenticationProvider.authenticate(authReq);

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(configurationProvider.getLdapAuthority()))) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info(String.format("User %s logged in.", loginRequest.getUsername()));

                response.setUsername(loginRequest.getUsername());
                return ResponseEntity.ok(response);
            }
        } catch (BadCredentialsException e) {
            log.error("Invalid login for user: " + loginRequest.getUsername(), e);
            throw new AuthenticationException("Auth.badCredentials");
        }

        log.error(String.format("User %s tried to login without required authority.", loginRequest.getUsername()));
        throw new AuthenticationException("Auth.invalidAuthority");
    }
}
