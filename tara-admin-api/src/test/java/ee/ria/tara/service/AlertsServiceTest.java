package ee.ria.tara.service;

import ee.ria.tara.model.Alert;
import ee.ria.tara.model.EmailAlert;
import ee.ria.tara.model.LoginAlert;
import ee.ria.tara.model.MessageTemplate;
import ee.ria.tara.repository.AlertRepository;
import ee.ria.tara.service.helper.AlertHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AlertsServiceTest {
    @Mock
    private LoginAlert loginAlert;

    @Mock
    private EmailAlert emailAlert;

    @Mock
    private MessageTemplate messageTemplate;

    @Mock
    private Alert alert;

    @Mock
    private AlertRepository repository;

    @InjectMocks
    private AlertsService service;

    @Test
    public void testGetAllAlerts() {
        mockAlert();

        doReturn(List.of(AlertHelper.convertToEntity(alert))).when(repository).findAll();

        List<Alert> alerts = service.getAlerts();

        assertEquals(1, alerts.size());
        this.compare(alert, alerts.get(0));
    }

    @Test
    public void testAddAlert() {
        mockAlert();

        doReturn(AlertHelper.convertToEntity(alert)).when(repository).save(any());

        Alert entity = service.addAlert(alert);

        verify(repository, times(1)).save(any());
        this.compare(alert, entity);
    }


    @Test
    public void testUpdateAlert() {
        mockAlert();

        doReturn(AlertHelper.convertToEntity(alert)).when(repository).save(any());

        Alert entity = service.updateAlert(alert);

        verify(repository, times(1)).save(any());
        this.compare(alert, entity);
    }


    @Test
    public void testDeleteAlert() {
        String id = "1";

        doReturn(id).when(alert).getId();

        service.deleteAlert(alert.getId());

        verify(repository, times(1)).deleteById(Long.valueOf(id));
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

    private void mockAlert() {
        doReturn(OffsetDateTime.now()).when(alert).getCreatedAt();
        doReturn(OffsetDateTime.now()).when(alert).getUpdatedAt();
        doReturn(OffsetDateTime.now()).when(alert).getStartTime();
        doReturn(OffsetDateTime.now()).when(alert).getEndTime();
        doReturn(OffsetDateTime.now()).when(emailAlert).getSendAt();

        doReturn("et").when(messageTemplate).getLocale();
        doReturn("message").when(messageTemplate).getMessage();
        doReturn(List.of(messageTemplate)).when(emailAlert).getMessageTemplates();
        doReturn(List.of(messageTemplate)).when(loginAlert).getMessageTemplates();

        doReturn("1").when(alert).getId();
        doReturn(List.of("123")).when(loginAlert).getAuthMethods();

        doReturn(emailAlert).when(alert).getEmailAlert();
        doReturn(loginAlert).when(alert).getLoginAlert();
    }
}

