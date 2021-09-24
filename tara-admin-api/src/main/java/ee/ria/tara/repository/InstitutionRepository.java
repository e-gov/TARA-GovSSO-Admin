package ee.ria.tara.repository;

import ee.ria.tara.repository.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Institution findInstitutionByRegistryCode(String registryCode);
    List<Institution> findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String registryCode, String name);
    void deleteByRegistryCode(String registryCode);
}
