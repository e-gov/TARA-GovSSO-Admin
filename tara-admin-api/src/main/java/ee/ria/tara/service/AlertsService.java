package ee.ria.tara.service;

import ee.ria.tara.model.Alert;
import ee.ria.tara.repository.AlertRepository;
import ee.ria.tara.service.helper.AlertHelper;
import ee.ria.tara.service.helper.AlertValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertsService {

    private static final List<String> ALLOWED_AUTHENTICATION_METHODS = List.of("idcard", "mid", "smartid", "eidas");

    private final AlertRepository repository;
    private final AlertValidator alertValidator;

    public Alert addAlert(Alert alert) {
        filterAlertAuthenticationMethods(alert);
        alertValidator.validateAlert(alert);
        Alert savedAlert = AlertHelper.convertFromEntity(this.saveAlert(alert));

        log.info(String.format("Added alert: %s.", alert.getTitle()));

        return savedAlert;
    }

    private void filterAlertAuthenticationMethods(Alert alert) {
        List<String> filteredAuthMethods = ALLOWED_AUTHENTICATION_METHODS.stream()
                .filter(alert.getLoginAlert().getAuthMethods()::contains)
                .collect(Collectors.toList());
        alert.getLoginAlert().setAuthMethods(filteredAuthMethods);
    }

    public Alert updateAlert(Alert alert) {
        filterAlertAuthenticationMethods(alert);
        alertValidator.validateAlert(alert);
        Alert updatedAlert = AlertHelper.convertFromEntity(this.saveAlert(alert));

        log.info(String.format("Updating alert with ID: %s.", alert.getId()));

        return updatedAlert;
    }

    public void deleteAlert(String id) {
        log.info("Deleting alert with ID: " + id);

        repository.deleteById(Long.valueOf(id));
    }

    private ee.ria.tara.repository.model.Alert saveAlert(Alert alert) {
        return repository.save(AlertHelper.convertToEntity(alert));
    }

    public List<Alert> getAlerts() {
        List<Alert> alerts = new ArrayList<>();

        repository.findAll().forEach(entity -> alerts.add(AlertHelper.convertFromEntity(entity)));

        return alerts;
    }
}
