package ee.ria.tara.controllers;

import ee.ria.tara.api.AlertsApi;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.Alert;
import ee.ria.tara.service.AlertsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AlertsController implements AlertsApi {
    private final HttpServletRequest request;
    private final AlertsService service;
    private final AdminConfigurationProvider adminConfigurationProvider;

    @Override
    public ResponseEntity<List<Alert>> getAllAlerts() {
        log.info(String.format("Incoming request: GET %s.", request.getRequestURI()));
        return ResponseEntity.ok(service.getAlerts());
    }

    @Override
    public ResponseEntity<Alert> addAlert(Alert alert) {
        log.info(String.format("Incoming request: POST %s, with title: %s.", request.getRequestURI(), alert.getTitle()));

        if (!adminConfigurationProvider.isSsoMode())
            validateAlertAuthenticationMethods(alert);

        return ResponseEntity.ok(service.addAlert(alert));
    }

    @Override
    public ResponseEntity<Alert> updateAlert(String alertId, Alert alert) {
        log.info(String.format("Incoming request: PUT %s.", request.getRequestURI()));

        if (!adminConfigurationProvider.isSsoMode())
            validateAlertAuthenticationMethods(alert);

        return ResponseEntity.ok(service.updateAlert(alert));
    }

    private void validateAlertAuthenticationMethods(Alert alert) {
        if (alert.getLoginAlert().getAuthMethods().size() < 1)
            throw new ValidationException("Vähemalt 1 autentimisvahend peab olema valitud.");
    }

    @Override
    public ResponseEntity<Void> deleteAlert(String alertId) {
        log.info(String.format("Incoming request: DELETE %s.", request.getRequestURI()));

        service.deleteAlert(alertId);

        return ResponseEntity.ok().build();
    }
}
