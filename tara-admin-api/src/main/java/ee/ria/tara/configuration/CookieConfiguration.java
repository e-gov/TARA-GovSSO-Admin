package ee.ria.tara.configuration;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.server.servlet.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static ee.ria.tara.configuration.AuthenticationProfiles.OIDC_AUTH;

/*
 NB! Following cookie settings do not work with standalone Tomcat and are configured
 in context.xml instead (look at webapp/META-INF/context.xml).
*/
@Configuration(proxyBeanMethods = false)
public class CookieConfiguration {

    public static final String COOKIE_NAME_XSRF_TOKEN = "__Host-XSRF-TOKEN";
    public static final String COOKIE_NAME_SESSION = "__Host-SESSION";

    @Bean
    @Profile("!" + OIDC_AUTH)
    CookieSameSiteSupplier sessionCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofStrict().whenHasName(COOKIE_NAME_SESSION);
    }

    @Bean
    @Profile(OIDC_AUTH)
    CookieSameSiteSupplier oidcSessionCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofLax().whenHasName(COOKIE_NAME_SESSION);
    }

    @Bean
    CookieSameSiteSupplier csrfCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofStrict().whenHasName(COOKIE_NAME_XSRF_TOKEN);
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> servletContext.getSessionCookieConfig().setName(COOKIE_NAME_SESSION);
    }
}
