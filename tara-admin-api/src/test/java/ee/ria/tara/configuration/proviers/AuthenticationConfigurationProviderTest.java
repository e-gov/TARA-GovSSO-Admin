package ee.ria.tara.configuration.proviers;

import ee.ria.tara.configuration.AbstractValidationTest;
import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthenticationConfigurationProviderTest extends AbstractValidationTest {
    private AuthenticationConfigurationProvider configurationProvider;

    @BeforeEach
    public void setUpConfigurationProvider() {
        configurationProvider = new AuthenticationConfigurationProvider();

        configurationProvider.setLdapUrl("url");
        configurationProvider.setLdapDomain("domain");
        configurationProvider.setLdapAuthority("authority");
    }

    @Test
    public void testValidConfigurationProviderValidationSucceeds() {
        validateAndExpectNoErrors(configurationProvider);
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingLdapUrl() {
        configurationProvider.setLdapUrl(null);

        validateAndExpectOneError(configurationProvider, "ldapUrl", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingLdapDomain() {
        configurationProvider.setLdapDomain(null);

        validateAndExpectOneError(configurationProvider, "ldapDomain", "must not be null");
    }

    @Test
    public void testConfigurationProviderValidationFailsOnMissingLdapAuthority() {
        configurationProvider.setLdapAuthority(null);

        validateAndExpectOneError(configurationProvider, "ldapAuthority", "must not be null");
    }
}
