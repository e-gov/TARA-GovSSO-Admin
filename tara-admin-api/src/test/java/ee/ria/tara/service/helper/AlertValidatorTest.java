package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Alert;
import ee.ria.tara.model.MessageTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static ee.ria.tara.service.helper.AlertTestHelper.validSSOAlert;
import static ee.ria.tara.service.helper.AlertTestHelper.validTARAAlert;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AlertValidatorTest {

    @Mock
    private AdminConfigurationProvider adminConfigurationProvider;

    @InjectMocks
    private AlertValidator alertValidator;

    @Test
    public void validateAlert_ssoMode_successfulValidation() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();
        alertValidator.validateAlert(validSSOAlert());
    }

    @Test
    public void validateAlert_taraMode_successfulValidation() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();
        alertValidator.validateAlert(validTARAAlert());
    }

    @Test
    public void validateAlert_multipleMessageTemplates_successfulValidation() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();
        Alert alert = validSSOAlert();
        List<MessageTemplate> messageTemplates = List.of(
                new MessageTemplate().locale("et").message("Eesti keeles"),
                new MessageTemplate().locale("ru").message("На русском"),
                new MessageTemplate().locale("en").message("In English"));
        alert.getLoginAlert().setMessageTemplates(messageTemplates);
        alertValidator.validateAlert(alert);
    }

    @Test
    public void validateAlert_ssoAuthMethodsNotEmpty_exceptionThrown() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        Alert alert = validSSOAlert();
        alert.getLoginAlert().setAuthMethods(List.of("idcard"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> alertValidator.validateAlert(alert));
        Assertions.assertTrue(exception.getMessage().contains("Authentication methods should not be present in SSO mode"));
    }

    @Test
    public void validateAlert_taraAuthMethodsEmpty_exceptionThrown() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        Alert alert = validTARAAlert();
        alert.getLoginAlert().setAuthMethods(Collections.emptyList());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> alertValidator.validateAlert(alert));
        Assertions.assertTrue(exception.getMessage().contains("Alert.authMethod.missing"));
    }

    @Test
    public void validateAlert_alertTitleMissing_exceptionThrown() {
        Alert alert = validSSOAlert();
        alert.setTitle("");

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> alertValidator.validateAlert(alert));
        Assertions.assertTrue(exception.getMessage().contains("Alert.title.missing"));
    }

    @Test
    public void validateAlert_alertMessageTemplateMissing_exceptionThrown() {
        Alert alert = validSSOAlert();
        alert.getLoginAlert().setMessageTemplates(null);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> alertValidator.validateAlert(alert));
        Assertions.assertTrue(exception.getMessage().contains("Alert.message.missing"));
    }

    @Test
    public void validateAlert_moreThanOneMessageTemplatesPerLocale_exceptionThrown() {
        Alert alert = validSSOAlert();
        List<MessageTemplate> messageTemplates = List.of(
                new MessageTemplate().locale("et").message("Eesti keeles"),
                new MessageTemplate().locale("et").message("Duplicate locale"));
        alert.getLoginAlert().setMessageTemplates(messageTemplates);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> alertValidator.validateAlert(alert));
        Assertions.assertTrue(exception.getMessage().contains("Multiple message templates for single locale"));
    }

    @Test
    public void validateAlert_alertMessageMissing_exceptionThrown() {
        Alert alert = validSSOAlert();
        alert.getLoginAlert().getMessageTemplates().get(0).setMessage("");

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> alertValidator.validateAlert(alert));
        Assertions.assertTrue(exception.getMessage().contains("Alert.message.missing"));
    }
}
