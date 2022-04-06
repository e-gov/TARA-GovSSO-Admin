package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.SecurityConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityConfigurationProperties securityConfProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .csrfTokenRepository(csrfTokenRepository())
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
                    .antMatchers("/", "/main", "/login", "/ssoMode", "/actuator/health")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/alerts")
                        .permitAll()
                    .antMatchers("/index.html", "/*.js", "/*.css", "/assets/ria-logo.png")
                        .permitAll()
                    .antMatchers("/**")
                        .authenticated();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "GET", "POST", "PUT", "DELETE"));
        configuration.setMaxAge((long) securityConfProperties.getCookieMaxAgeSeconds());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("__Host-XSRF-TOKEN");
        repository.setHeaderName("__Host-X-XSRF-TOKEN");
        repository.setSecure(true);
        repository.setCookiePath("/");
        repository.setCookieMaxAge(securityConfProperties.getCookieMaxAgeSeconds());
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
