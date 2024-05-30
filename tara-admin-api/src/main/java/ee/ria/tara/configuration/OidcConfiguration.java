package ee.ria.tara.configuration;

import ee.ria.tara.configuration.providers.TlsConfigurationProvider;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
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
        SSLConnectionSocketFactory socketFactory = SSLConnectionSocketFactoryBuilder.create()
            .setSslContext(sslContext)
            .build();

        HttpClient client = HttpClients.custom()
            .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(socketFactory)
                .build())
            .build();

        return builder
            .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
            .build();
    }
}
