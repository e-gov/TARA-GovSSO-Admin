package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import ee.ria.tara.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;
import static ee.ria.tara.model.InstitutionType.TypeEnum.PUBLIC;
import static ee.ria.tara.service.helper.ClientTestHelper.validSSOClient;
import static ee.ria.tara.service.helper.ClientTestHelper.validTARAClient;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClientValidatorTest {

    private final static String ALLOWED_CHARS = "-%_!:.~'()*abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final static String ALLOWED_PARAM = ALLOWED_CHARS + "=" + ALLOWED_CHARS;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AdminConfigurationProvider adminConfigurationProvider;

    @InjectMocks
    private ClientValidator clientValidator;

    @Test
    public void validateClient_ssoMode_successfulValidation() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();
        Client client = validSSOClient();
        clientValidator.validateClient(client, PUBLIC);
    }

    @Test
    public void validateClient_taraMode_successfulValidation() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();
        Client client = validTARAClient();
        clientValidator.validateClient(client, PUBLIC);
    }

    @Test
    public void validateClient_ssoClientNameMissing_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setClientName(new NameTranslations());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.sso.clientName"));
    }

    @Test
    public void validateClient_ssoClientShortNameMissing_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setClientShortName(new ShortNameTranslations());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.sso.clientShortName"));
    }

    @Test
    public void validateClient_taraNameMissing_valid() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setClientName(new NameTranslations());
        client.setClientShortName(new ShortNameTranslations());

        clientValidator.validateClient(client, PUBLIC);
    }

    @Test
    public void validateClient_taraShortNameTooLong_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        String fortyOneCharacterString = "12345678901234567890123456789012345678901";
        Client client = validTARAClient();
        client.getClientShortName().setEt(fortyOneCharacterString);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.shortName.tooLong"));
    }

    @Test
    public void validateClient_taraShortNameWithMaxLength_valid() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        String fortyOneCharacterString = "1234567890123456789012345678901234567890";
        Client client = validTARAClient();
        client.getClientShortName().setEt(fortyOneCharacterString);

        clientValidator.validateClient(client, PUBLIC);
    }

    @Test
    public void validateClient_taraShortNameWithNonGsm7CharactersTooLong_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        String twentyOneCharacterUcs2String = "12345678901234567890Õ";
        Client client = validTARAClient();
        client.getClientShortName().setEt(twentyOneCharacterUcs2String);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.shortName.tooLong"));
    }

    @Test
    public void validateClient_taraShortNameWithNonGsm7CharactersMaxLength_valid() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        String fortyOneCharacterString = "1234567890123456789Õ";
        Client client = validTARAClient();
        client.getClientShortName().setEt(fortyOneCharacterString);

        clientValidator.validateClient(client, PUBLIC);
    }

    @Test
    public void validateClient_taraShortNameWithNonUcs2Characters_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        String nonUcs2String = "abc-\uD83D\uDE00";
        Client client = validTARAClient();
        client.getClientShortName().setEt(nonUcs2String);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.shortName.forbiddenCharacters"));
    }

    @Test
    public void validateClient_ssoBackchannelLogoutUriUriMissing_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setBackchannelLogoutUri(null);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.backchannelUri.missing"));
    }

    @Test
    public void validateClient_ssoScopeMissing_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setScope(Collections.emptyList());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.scope.missing"));
    }

    @Test
    public void validateClient_taraScopeMissing_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setScope(Collections.emptyList());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.scope.missing"));
    }

    @Test
    public void validateClient_ssoPostLogoutUriUriMissing_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setPostLogoutRedirectUris(null);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.postLogoutRedirectUri.missing"));
    }

    @Test
    public void validateClient_ssoPostLogoutUriUriInvalid_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setPostLogoutRedirectUris(Arrays.asList("invalid.uri¤", "valid-uri.com"));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.postLogoutRedirectUri.missing"));
    }

    @Test
    public void validateClient_ssoRedirectUriUriInvalid_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setRedirectUris(Arrays.asList("invalid.uri¤", "valid-uri.com"));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.redirectUri.missing"));
    }

    @Test
    public void validateClient_taraPostLogoutRedirectUriPresent_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setPostLogoutRedirectUris(List.of("valid-uri.com"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Post logout redirect uris must not be set in TARA mode"));
    }

    @Test
    public void validateClient_taraBackchannelLogoutUriPresent_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setBackchannelLogoutUri("valid-uri.com");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Backchannel logout uri must not be set in TARA mode"));
    }

    @Test
    public void validateClient_taraRedirectUriUriInvalid_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setRedirectUris(Arrays.asList("invalid.uri¤", "valid-uri.com"));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.redirectUri.missing"));
    }

    @Test
    public void validateClient_ssoEidasRequesterIdSet_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setEidasRequesterId("Some-value");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("eIDAS RequesterID must not be set in SSO mode"));
    }

    @Test
    public void validateClient_taraEidasRequesterIdMissing_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setEidasRequesterId("");

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PRIVATE));
        Assertions.assertTrue(exception.getMessage().contains("Client.eidasRequesterId.missing"));
    }

    @Test
    public void validateClient_taraClientAlreadyExistsWithEidasRequesterId_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        doReturn(new ee.ria.tara.repository.model.Client())
                .when(clientRepository)
                .findByEidasRequesterId(client.getEidasRequesterId());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PRIVATE));
        Assertions.assertTrue(exception.getMessage().contains("Client.eidasRequesterId.exists"));
    }

    @Test
    public void validateClient_ssoClientWithTooLargeLogo_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setClientLogo(new byte[100 * 1024 + 1]);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.logo.tooLarge"));
    }

    @Test
    public void validateClient_taraClientWithLogo_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setClientLogo(new byte[10240]);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PRIVATE));
        Assertions.assertTrue(exception.getMessage().contains("Client logo must not be set in TARA mode"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3.4", "1:2:3:4:5:6:7:8", "1.2.0.0/16", "1.*.1-3.1-4", "1111:222::/64", "1:2:*:4:5-10:6:7"})
    public void validateClient_taraClientIpAddressValid_successfulValidation(String ipAddress) {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setTokenRequestAllowedIpAddresses(List.of(ipAddress));

        clientValidator.validateClient(client, PUBLIC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.1.1.1.1", "1.1.a.1", "1111.1.1.1", "999.1.1.1", "1.1.1.1+1.1.1.1","1:2:3:4:5:6:7", " "})
    public void validateClient_taraClientIpAddressInvalid_exceptionThrown(String ipAddress) {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setTokenRequestAllowedIpAddresses(List.of(ipAddress));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.tokenRequestAllowedIpAddresses.invalidIp"));
    }

    @Test
    public void validateClient_usingJwtAccessTokenStrategyWithAccessTokenAudienceUris_successfulValidation() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setAccessTokenJwtEnabled(true);
        client.setAccessTokenAudienceUris(List.of("https://test"));

        clientValidator.validateClient(client, PUBLIC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", "https://user:password@example.com", "https://test#fragment"})
    public void validateClient_usingJwtAccessTokenStrategyWithInvalidAccessTokenAudienceUris_exceptionThrown(String uri) {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setAccessTokenJwtEnabled(true);
        client.setAccessTokenAudienceUris(List.of(uri));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.accessTokenAudienceUri.missing"));
    }

    @Test
    public void validateClient_usingJwtAccessTokenStrategyWithoutAccessTokenAudienceUris_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setAccessTokenJwtEnabled(true);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.accessTokenAudienceUri.missing"));
    }

    @Test
    public void validateClient_withJwtServiceUrisWithoutSsoMode_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setAccessTokenAudienceUris(List.of("https://test"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("JWT service uris must not be set in TARA mode"));
    }

    @Test
    public void validateClient_usingJwtAccessTokenStrategyWithoutSsoMode_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setAccessTokenJwtEnabled(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Access token JWT enabled must not be set to true in TARA mode"));
    }

    @Test
    public void validateClient_withJwtServiceUrisWhenAccessTokenJwtIsNotEnabled_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setAccessTokenAudienceUris(List.of("https://test"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Access token audience uris must not be set if access token JWT strategy is not enabled"));
    }

    @Test
    public void validateClient_withPaasukeParametersWithoutSsoMode_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Client client = validTARAClient();
        client.setPaasukeParameters("ns=A");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Paasuke parameters must not be set in TARA mode"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"=", "a&", "&a", "a==", "a=a=a", "a&&a", "a\n",
            "\0", "\b", "\t", "\n", "\f", "\r", " ", "\"", "#", "$", "&", "+", ",", "/", ";", "<", "=", ">", "?", "@", "[", "\\", "]", "^", "`", "{", "|", "}", "õ"
    })
    public void validateClient_withPaasukeParameters_exceptionThrown(String queryParameters) {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setScope(List.of("representee.*"));
        client.setPaasukeParameters(queryParameters);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Client.paasukeParameters.invalid"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "a=", "a=a", "a=a&a", "a=a&a=", "a&a=a", "a=a&a=a", "a&a",
            "a&a&a", "a&a&a=", "a&a&a=a",
            ALLOWED_PARAM + "&" +  ALLOWED_PARAM + "&" + ALLOWED_PARAM})
    public void validateClient_withPaasukeParameters_successfulValidation(String queryParameters) {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setScope(List.of("representee.*"));
        client.setPaasukeParameters(queryParameters);

        clientValidator.validateClient(client, PUBLIC);
    }

    @Test
    public void validateClient_withPaasukeParametersWithoutRepresenteeScope_successfulValidation() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Client client = validSSOClient();
        client.setPaasukeParameters("a=a");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> clientValidator.validateClient(client, PUBLIC));
        Assertions.assertTrue(exception.getMessage().contains("Paasuke parameters must not be set without representee.* scope"));
    }
}
