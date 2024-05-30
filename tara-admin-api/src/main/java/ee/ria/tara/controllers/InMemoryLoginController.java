package ee.ria.tara.controllers;

import ee.ria.tara.api.LoginApi;
import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import ee.ria.tara.controllers.exception.AuthenticationException;
import ee.ria.tara.model.LoginRequest;
import ee.ria.tara.model.LoginResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Profile("inMemoryAuth")
@RestController
public class InMemoryLoginController implements LoginApi {
    final UserDetailsManager inMemoryUserDetailsManager;
    final PasswordEncoder passwordEncoder;
    final AuthenticationConfigurationProvider configurationProvider;


    public InMemoryLoginController(UserDetailsManager inMemoryUserDetailsManager, PasswordEncoder passwordEncoder,
                                   AuthenticationConfigurationProvider configurationProvider) {
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.configurationProvider = configurationProvider;
    }

    @SneakyThrows
    @Override
    public ResponseEntity<LoginResponse> loginUser(@Valid LoginRequest loginRequest) {
        log.info(String.format("Incoming login request for user: %s", loginRequest.getUsername()));
        LoginResponse response = new LoginResponse();

        UserDetails userDetails;

        try {
            userDetails = inMemoryUserDetailsManager.loadUserByUsername(loginRequest.getUsername());
        } catch (UsernameNotFoundException e) {
            log.error("Invalid user: " + loginRequest.getUsername());
            throw new AuthenticationException("Auth.invalidUser");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            log.info(String.format("User %s logged in.", loginRequest.getUsername()));
            Authentication authToken = new UsernamePasswordAuthenticationToken(getPrincipal(loginRequest.getUsername()),
                    userDetails.getPassword(), List.of(new SimpleGrantedAuthority(configurationProvider.getInMemoryAuthority())));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            response.setUsername(userDetails.getUsername());

            return ResponseEntity.ok(response);
        } else {
            log.error("Invalid login for user: " + loginRequest.getUsername());
            throw new AuthenticationException("Auth.badCredentials");
        }
    }

    private LdapUserDetails getPrincipal(String username) {
        LdapUserDetailsImpl.Essence userDetails = new LdapUserDetailsImpl.Essence();
        userDetails.setUsername(username);
        userDetails.setDn("dn");
        return userDetails.createUserDetails();
    }
}
