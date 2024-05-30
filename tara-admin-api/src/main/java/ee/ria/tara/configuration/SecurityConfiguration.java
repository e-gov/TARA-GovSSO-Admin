package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.SecurityConfigurationProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.time.Duration;

import static ee.ria.tara.configuration.CookieConfiguration.COOKIE_NAME_XSRF_TOKEN;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final long STRICT_TRANSPORT_SECURITY_MAX_AGE = Duration.ofDays(186).toSeconds();

    private final SecurityConfigurationProperties securityConfProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .requestCache(cacheConfig -> cacheConfig.requestCache(httpSessionRequestCache()))
            .cors(CorsConfigurer::disable)
            .csrf(csrfConfig -> csrfConfig
                .csrfTokenRepository(csrfTokenRepository())
                // Opt-out of BREACH protection as described in https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-token-request-handler-plain
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .exceptionHandling(exceptionHandlingConfig ->
                exceptionHandlingConfig.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .headers(headersConfig -> headersConfig
                .xssProtection(xXssConfig -> xXssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .contentSecurityPolicy(cspConfig -> cspConfig.policyDirectives(securityConfProperties.getContentSecurityPolicy()))
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig.maxAgeInSeconds(STRICT_TRANSPORT_SECURITY_MAX_AGE))
            )
            .logout(logoutConfig -> logoutConfig
                .logoutSuccessHandler(new HttpLogoutSuccessHandler())
            )
            .sessionManagement(smConfig -> smConfig
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
            )
            /* Switch to the default behaviour of Spring Security 5 where SecurityContext is saved automatically.
             * https://docs.spring.io/spring-security/reference/5.8/migration/servlet/session-management.html#_require_explicit_saving_of_securitycontextrepository
             * TODO https://jira.ria.ee/browse/AUT-1856
             */
            .securityContext(securityContextConfig -> securityContextConfig.requireExplicitSave(false))
            .authorizeHttpRequests(httpRequestsConfig -> httpRequestsConfig
                .requestMatchers(
                    antMatcher("/"),
                    antMatcher("/login"),
                    antMatcher("/ssoMode"),
                    antMatcher("/actuator/**")
                ).permitAll()
                .requestMatchers(
                    antMatcher(HttpMethod.GET, "/alerts"),
                    antMatcher(HttpMethod.GET, "/clients/tokenrequestallowedipaddresses")
                ).permitAll()
                .requestMatchers(
                    antMatcher("/*.js"),
                    antMatcher("/*.css"),
                    antMatcher("/*.woff2"),
                    antMatcher("/*.woff"),
                    antMatcher("/*.ttf")
                ).permitAll()
                .requestMatchers(
                    antMatcher("/index.html"),
                    antMatcher("/assets/ria-logo.png"),
                    antMatcher("/favicon.ico")
                ).permitAll()
                .requestMatchers(antMatcher("/**")).authenticated()
            );
        return http.build();
    }

    private HttpSessionRequestCache httpSessionRequestCache() {
        HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
        // Disables session creation if session does not exist and any request returns 401 unauthorized error.
        httpSessionRequestCache.setCreateSessionAllowed(false);
        return httpSessionRequestCache;
    }

    private CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName(COOKIE_NAME_XSRF_TOKEN);
        repository.setSecure(true);
        repository.setCookiePath("/");
        return repository;
    }

    //TODO: add logging
    private static class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            response.setStatus(HttpStatus.OK.value());
        }
    }
}
