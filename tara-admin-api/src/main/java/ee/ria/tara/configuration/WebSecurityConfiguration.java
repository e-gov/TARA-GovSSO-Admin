package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final AuthenticationConfigurationProvider configurationProvider;

    public WebSecurityConfiguration(AuthenticationConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new SessionCookieFilter(), BasicAuthenticationFilter.class)
                .cors()
                    .and()
                .csrf()
                    .ignoringAntMatchers("/login")
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(new HttpLogoutSuccessHandler())
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                    //.sessionRegistry(sessionRegistry)
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
    @Profile("!inMemoryAuth")
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
                configurationProvider.getLdapDomain(),
                configurationProvider.getLdapUrl()
        );
        provider.setConvertSubErrorCodesToExceptions(true);

        return provider;
    }

    @Bean
    @Profile("inMemoryAuth")
    public UserDetailsManager inMemoryUserDetailsManager() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(configurationProvider.getInMemoryUsername())
                .password(passwordEncoder().encode(configurationProvider.getInMemoryPassword()))
                .authorities(configurationProvider.getInMemoryAuthority())
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //TODO: add logging
    private static class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            response.setStatus(HttpStatus.OK.value());
        }
    }
}
