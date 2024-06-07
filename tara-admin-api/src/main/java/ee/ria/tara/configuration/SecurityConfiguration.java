package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.SecurityConfigurationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

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
                // Used request handler as described here https://docs.spring.io/spring-security/reference/6.3/servlet/exploits/csrf.html#csrf-integration-javascript-spa
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
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
            /* CsrfCookieFilter is only necessary for when csrfToken is being passed as a form parameter.
            Our application uses X-XSRF-TOKEN request header to pass the CSRF token therefore it is by default not used.
            I added this support just in case we want to start passing the CSRF token as a form parameter. */
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
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

    private static final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
        private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
            /*
             * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
             * the CsrfToken when it is rendered in the response body.
             */
            this.delegate.handle(request, response, csrfToken);
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            /*
             * If the request contains a request header, use CsrfTokenRequestAttributeHandler
             * to resolve the CsrfToken. This applies when a single-page application includes
             * the header value automatically, which was obtained via a cookie containing the
             * raw CsrfToken.
             */
            if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
                return super.resolveCsrfTokenValue(request, csrfToken);
            }
            /*
             * In all other cases (e.g. if the request contains a request parameter), use
             * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
             * when a server-side rendered form includes the _csrf request parameter as a
             * hidden input.
             */
            return this.delegate.resolveCsrfTokenValue(request, csrfToken);
        }
    }

    private static final class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            // Render the token value to a cookie by causing the deferred token to be loaded
            csrfToken.getToken();

            filterChain.doFilter(request, response);
        }
    }
}
