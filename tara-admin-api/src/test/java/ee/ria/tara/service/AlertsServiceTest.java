package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Alert;
import ee.ria.tara.model.MessageTemplate;
import ee.ria.tara.repository.AlertRepository;
import ee.ria.tara.service.helper.AlertHelper;
import ee.ria.tara.service.helper.AlertValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ee.ria.tara.service.helper.AlertTestHelper.validSSOAlert;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AlertsServiceTest {

    @Mock
    private AlertRepository repository;

    @Mock
    private AlertValidator alertValidator;

    @InjectMocks
    private AlertsService service;

    @Test
    public void testGetAllAlerts() {
        Alert alert = validSSOAlert();

        doReturn(List.of(AlertHelper.convertToEntity(alert))).when(repository).findAll(any(Sort.class));

        List<Alert> alerts = service.getAlerts();

        assertEquals(1, alerts.size());
        this.compare(alert, alerts.get(0));
    }

    @Test
    public void testAddAlert() {
        Alert alert = validSSOAlert();

        doReturn(AlertHelper.convertToEntity(alert)).when(repository).save(any());

        Alert entity = service.addAlert(alert);

        verify(repository, times(1)).save(any());
        this.compare(alert, entity);
    }

    @Test
    public void testAddAlert_validationFails_exceptionThrown() {
        Alert alert = validSSOAlert();

        String errorMessage = "Validation failed";
        doThrow(new InvalidDataException(errorMessage)).when(alertValidator).validateAlert(alert);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> service.addAlert(alert));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
        verify(repository, times(0)).save(any());
    }

    @Test
    public void testUpdateAlert() {
        Alert alert = validSSOAlert();

        doReturn(AlertHelper.convertToEntity(alert)).when(repository).save(any());

        Alert entity = service.updateAlert(alert);

        verify(repository, times(1)).save(any());
        this.compare(alert, entity);
    }

    @Test
    public void testDeleteAlert() {
        Alert alert = validSSOAlert();
        service.deleteAlert(alert.getId());
        verify(repository, times(1)).deleteById(Long.valueOf(alert.getId()));
    }

    private void compare(Alert initial, Alert retrieved) {
        assertEquals(initial.getId(), retrieved.getId());
        assertEquals(initial.getTitle(), retrieved.getTitle());
        assertEquals(initial.getCreatedAt(), retrieved.getCreatedAt());
        assertEquals(initial.getUpdatedAt(), retrieved.getUpdatedAt());
        assertEquals(initial.getStartTime(), retrieved.getStartTime());
        assertEquals(initial.getEndTime(), retrieved.getEndTime());

        String initialEmailAlertMessage = initial.getEmailAlert().getMessageTemplates().stream()
                .filter(m -> m.getLocale().equals("et"))
                .map(MessageTemplate::getMessage)
                .findFirst()
                .orElse(null);
        assertNotNull(initialEmailAlertMessage);
        String retrievedEmailAlertMessage = retrieved.getEmailAlert().getMessageTemplates().stream()
                .filter(m -> m.getLocale().equals("et"))
                .map(MessageTemplate::getMessage)
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedEmailAlertMessage);
        assertEquals(initialEmailAlertMessage, retrievedEmailAlertMessage);
        assertEquals(initial.getEmailAlert().getSendAt(), retrieved.getEmailAlert().getSendAt());
        assertEquals(initial.getEmailAlert().getEnabled(), retrieved.getEmailAlert().getEnabled());

        String initialLoginAlertMessage = initial.getLoginAlert().getMessageTemplates().stream()
                .filter(m -> m.getLocale().equals("et"))
                .map(MessageTemplate::getMessage)
                .findFirst()
                .orElse(null);
        assertNotNull(initialLoginAlertMessage);
        String retrievedLoginAlertMessage = retrieved.getLoginAlert().getMessageTemplates().stream()
                .filter(m -> m.getLocale().equals("et"))
                .map(MessageTemplate::getMessage)
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedLoginAlertMessage);
        assertEquals(initialLoginAlertMessage, retrievedLoginAlertMessage);
        assertEquals(initial.getLoginAlert().getEnabled(), retrieved.getLoginAlert().getEnabled());
        assertEquals(initial.getLoginAlert().getAuthMethods(), retrieved.getLoginAlert().getAuthMethods());
    }
}

