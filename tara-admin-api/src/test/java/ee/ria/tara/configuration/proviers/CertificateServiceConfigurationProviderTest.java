package ee.ria.tara.configuration.proviers;

import ee.ria.tara.configuration.AbstractValidationTest;
import ee.ria.tara.configuration.providers.CertificateServiceConfigurationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CertificateServiceConfigurationProviderTest extends AbstractValidationTest {
    private CertificateServiceConfigurationProvider configurationProvider;

    @BeforeEach
    public void setUpConfigurationProvider() {
        configurationProvider = new CertificateServiceConfigurationProvider();

        configurationProvider.setPort(1);
        configurationProvider.setUrl("Url");
    }

    @Test
    public void testValidConfigurationProviderValidationSucceeds() {
        validateAndExpectNoErrors(configurationProvider);
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingUrl() {
        configurationProvider.setUrl(null);

        validateAndExpectOneError(configurationProvider, "url", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingPort() {
        configurationProvider.setPort(null);

        validateAndExpectOneError(configurationProvider, "port", "must not be null");
    }
}
