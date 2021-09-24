package ee.ria.tara.service;

import ee.ria.tara.model.Alert;
import ee.ria.tara.repository.AlertRepository;
import ee.ria.tara.service.helper.AlertHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AlertsService {
    private final AlertRepository repository;

    public AlertsService(AlertRepository repository) {
        this.repository = repository;
    }

    public Alert addAlert(Alert alert) {
        Alert savedAlert = AlertHelper.convertFromEntity(this.saveAlert(alert));

        log.info(String.format("Added alert: %s.", alert.getTitle()));

        return savedAlert;
    }

    public Alert updateAlert(Alert alert) {
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
