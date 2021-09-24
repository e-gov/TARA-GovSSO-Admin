package ee.ria.tara.configuration.proviers;

import ee.ria.tara.configuration.AbstractValidationTest;
import ee.ria.tara.configuration.providers.ClientSecretEmailConfigurationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientSecretEmailConfigurationProviderTest extends AbstractValidationTest {
    private ClientSecretEmailConfigurationProvider configurationProvider;

    @BeforeEach
    public void setUpConfigurationProvider() {
        configurationProvider = new ClientSecretEmailConfigurationProvider();

        configurationProvider.setFromEmail("");
        configurationProvider.setFromName("");
        configurationProvider.setSubject("");
        configurationProvider.setTemplateName("");
        configurationProvider.setAttachmentFileName("");
    }

    @Test
    public void testValidConfigurationProviderValidationSucceeds() {
        validateAndExpectNoErrors(configurationProvider);
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingFromEmail() {
        configurationProvider.setFromEmail(null);

        validateAndExpectOneError(configurationProvider, "fromEmail", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingFromName() {
        configurationProvider.setFromName(null);

        validateAndExpectOneError(configurationProvider, "fromName", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingSubject() {
        configurationProvider.setSubject(null);

        validateAndExpectOneError(configurationProvider, "subject", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingTemplateName() {
        configurationProvider.setTemplateName(null);

        validateAndExpectOneError(configurationProvider, "templateName", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingAttachmentFileName() {
        configurationProvider.setAttachmentFileName(null);

        validateAndExpectOneError(configurationProvider, "attachmentFileName", "must not be null");
    }
}
