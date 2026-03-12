package ee.ria.tara;

import co.elastic.apm.attach.ElasticApmAttacher;
import ee.ria.tara.configuration.HomeController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.security.Security;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = HomeController.class)})
public class Application {

    // Modifying "jdk.tls.disabledAlgorithms" security property can only be done before running main() method
    static {
        String allowTlsRsa = System.getenv("ALLOW_INSECURE_TLS_RSA_KEY_EXCHANGE");
        if ("true".equalsIgnoreCase(allowTlsRsa)) {
            String disabledAlgorithmsProperty = "jdk.tls.disabledAlgorithms";
            String disabledAlgorithmsOriginalValue = Security.getProperty(disabledAlgorithmsProperty);
            if (disabledAlgorithmsOriginalValue != null) {
                // Remove only "TLS_RSA_*" from blacklist
                String updatedAlgorithms = Arrays.stream(disabledAlgorithmsOriginalValue.split(","))
                    .map(String::trim)
                    .filter(cipher -> !cipher.equals("TLS_RSA_*"))
                    .collect(Collectors.joining(", "));
                Security.setProperty(disabledAlgorithmsProperty, updatedAlgorithms);
                log.warn("Insecure TLS_RSA_* key exchange ciphers have been enabled.");
            }
        }
    }

    public static void main(String[] args) {
        ElasticApmAttacher.attach();
        SpringApplication.run(Application.class, args);
    }

}
