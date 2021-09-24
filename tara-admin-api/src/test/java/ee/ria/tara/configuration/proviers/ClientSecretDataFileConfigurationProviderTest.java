package ee.ria.tara.configuration.proviers;

import ee.ria.tara.configuration.AbstractValidationTest;
import ee.ria.tara.configuration.providers.ClientSecretDataFileConfigurationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientSecretDataFileConfigurationProviderTest extends AbstractValidationTest {
    private ClientSecretDataFileConfigurationProvider configurationProvider;

    @BeforeEach
    public void setUpConfigurationProvider() {
        configurationProvider = new ClientSecretDataFileConfigurationProvider();

        configurationProvider.setTextFileName("");
        configurationProvider.setEncryptedFileName("");
    }

    @Test
    public void testValidConfigurationProviderValidationSucceeds() {
        validateAndExpectNoErrors(configurationProvider);
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingTextFileName() {
        configurationProvider.setTextFileName(null);

        validateAndExpectOneError(configurationProvider, "textFileName", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingEncryptedFileName() {
        configurationProvider.setEncryptedFileName(null);

        validateAndExpectOneError(configurationProvider, "encryptedFileName", "must not be null");
    }
}
