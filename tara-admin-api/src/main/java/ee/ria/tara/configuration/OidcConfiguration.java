package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.TlsConfigurationProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.springframework.util.ResourceUtils.getFile;

@Configuration
public class OidcConfiguration {

    @Bean
    public SSLContext trustContext(TlsConfigurationProvider tlsConfigurationProperties) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return SSLContextBuilder
                .create().setKeyStoreType(tlsConfigurationProperties.getTlsTruststoreType())
                .loadTrustMaterial(
                        getFile(tlsConfigurationProperties.getTlsTruststorePath()),
                        tlsConfigurationProperties.getTlsTruststorePassword().toCharArray())
                .build();
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder, SSLContext sslContext) {
        HttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
                .build();
    }
}
