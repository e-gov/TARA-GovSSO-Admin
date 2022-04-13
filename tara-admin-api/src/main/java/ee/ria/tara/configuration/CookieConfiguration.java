package ee.ria.tara.configuration;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CookieConfiguration {

    public static final String COOKIE_NAME_XSRF_TOKEN = "__Host-XSRF-TOKEN";
    public static final String COOKIE_NAME_SESSION = "__Host-SESSION";

    /*
     This solution and 'server.servlet.session.cookie.name' do not work with standalone Tomcat application.
     If running this application in standalone Tomcat change context.xml:
        <Context sessionCookieName="__Host-SESSION">
        ...
        </Context>
    */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> servletContext.getSessionCookieConfig().setName(COOKIE_NAME_SESSION);
    }

    //Alternative for CookieSameSiteSupplier which does not work in standalone Tomcat application.
    @Bean
    public TomcatContextCustomizer cookiesSameSiteConfiguration() {
        return context -> {
            final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies(SameSiteCookies.STRICT.getValue());
            context.setCookieProcessor(cookieProcessor);
        };
    }
}
