package ee.ria.tara.configuration;

import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CookieConfiguration {

    public static final String COOKIE_NAME_XSRF_TOKEN = "__Host-XSRF-TOKEN";
    public static final String COOKIE_NAME_SESSION = "__Host-SESSION";

    @Bean
    CookieSameSiteSupplier csrfCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofStrict().whenHasName(COOKIE_NAME_XSRF_TOKEN);
    }

    @Bean
    CookieSameSiteSupplier sessionCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofStrict().whenHasName(COOKIE_NAME_SESSION);
    }
}
