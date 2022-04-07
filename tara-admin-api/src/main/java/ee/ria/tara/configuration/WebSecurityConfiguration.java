package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.SecurityConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String COOKIE_NAME_XSRF_TOKEN = "__Host-XSRF-TOKEN";
    public static final String COOKIE_NAME_SESSION = "__Host-SESSION";

    private final SecurityConfigurationProperties securityConfProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
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
                    .antMatchers(HttpMethod.GET, "/alerts")
                        .permitAll()
                    .antMatchers("/*.js", "/*.css", "/*.woff2", "/*.woff", "/*.ttf")
                        .permitAll()
                    .antMatchers("/index.html", "/assets/ria-logo.png", "/favicon.ico")
                        .permitAll()
                    .antMatchers("/**")
                        .authenticated();
    }

    private CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName(COOKIE_NAME_XSRF_TOKEN);
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
