package ee.ria.tara.repository;

import ee.ria.tara.repository.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

@Transactional
public interface AlertRepository extends JpaRepository<Alert, Long> {
}
