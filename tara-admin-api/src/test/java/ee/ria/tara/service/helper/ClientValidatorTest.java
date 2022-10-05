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
}
