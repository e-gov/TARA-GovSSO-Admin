package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.SecurityConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ee.ria.tara.configuration.CookieConfiguration.COOKIE_NAME_XSRF_TOKEN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final SecurityConfigurationProperties securityConfProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .requestCache()
                    .requestCache(httpSessionRequestCache())
                    .and()
                .cors().disable()
                .csrf()
                    .csrfTokenRepository(csrfTokenRepository())
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .and()
                .headers()
                    .xssProtection().xssProtectionEnabled(false)
                        .and()
                    .frameOptions().deny()
                    .contentSecurityPolicy(securityConfProperties.getContentSecurityPolicy())
                         /*
                         *  Prevents browser from blocking functionality if views do not meet CSP requirements.
                         *  Problems are still displayed at browser console.
                         *  TODO: Remove this once given problems are fixed.
                         */
                        .reportOnly()
                        .and()
                    .httpStrictTransportSecurity()
                    .maxAgeInSeconds(186 * 24 * 60 * 60)
                        .and()
                    .and()
                .logout()
                    .logoutSuccessHandler(new HttpLogoutSuccessHandler())
                    .and()
                .sessionManagement()
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
                        .and()
                    .and()
                .authorizeRequests()
                    .antMatchers("/", "/login", "/ssoMode", "/actuator/**")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/alerts", "/clients/tokenrequestallowedipaddresses")
                        .permitAll()
                    .antMatchers("/*.js", "/*.css", "/*.woff2", "/*.woff", "/*.ttf")
                        .permitAll()
                    .antMatchers("/index.html", "/assets/ria-logo.png", "/favicon.ico")
                        .permitAll()
                    .antMatchers("/**")
                        .authenticated();
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
