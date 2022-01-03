package ee.ria.tara.repository;

import ee.ria.tara.repository.model.Alert;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
@EnableConfigurationProperties
@ActiveProfiles({"integrationtest"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AlertRepositoryIT {
    private final AlertRepository repository;
    private final String title = "Title";

    @Test
    @Order(1)
    public void testAddAlert() {
        Alert testAlert = createTestAlert();
        List<Alert> alerts = repository.findAll();

        Assertions.assertEquals(0, alerts.size());

        repository.save(testAlert);
        testAlert.setCreatedAt(testAlert.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        testAlert.setUpdatedAt(testAlert.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));

        alerts = repository.findAll();

        alerts.get(0).setCreatedAt(alerts.get(0).getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        alerts.get(0).setUpdatedAt(alerts.get(0).getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));

        Assertions.assertEquals(1, alerts.size());
        Assertions.assertEquals(testAlert, alerts.get(0));
    }

    @Test
    @Order(2)
    public void testUpdateExistingAlert() {
        String newTitle = "Katkestus";
        List<Alert> retrievedAlert = repository.findAll();

        Assertions.assertEquals(1, retrievedAlert.size());

        Alert alert = retrievedAlert.get(0);
        alert.setTitle(newTitle);

        repository.save(alert);

        List<Alert> alerts = repository.findAll();

        Assertions.assertEquals(1, alerts.size());
        Assertions.assertEquals(newTitle, alerts.get(0).getTitle());
    }

    @Test
    @Order(3)
    public void testDeleteAlert() {
        List<Alert> alerts = repository.findAll();

        Assertions.assertEquals(1, alerts.size());

        repository.deleteById(alerts.get(0).getId());

        alerts = repository.findAll();

        Assertions.assertEquals(0, alerts.size());
    }

    private Alert createTestAlert() {
        Alert alert = new Alert();

        alert.setTitle(title);
        alert.setEmailTemplate("Erakorraline katkestus");
        alert.setEndTime(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
        alert.setNotificationText("Erakorraline katkestus");
        alert.setNotifyClientsByEmail(true);
        alert.setSendAt(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
        alert.setNotifyClientsOnTaraLoginPage(true);
        alert.setStartTime(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
        alert.setDisplayOnlyForAuthmethods(List.of("idcard"));

        return alert;
    }
}
