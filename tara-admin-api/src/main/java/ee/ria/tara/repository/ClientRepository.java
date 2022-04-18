package ee.ria.tara.repository;

import ee.ria.tara.repository.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ClientRepository extends JpaRepository<Client, Long> {
    void deleteByClientIdAndInstitution_RegistryCode(String clientId, String registryCode);
    Client findByClientId(String clientId);
    Client findByEidasRequesterId(String eidasRequesterId);
    List<Client> findAllByInstitution_RegistryCode(String registryCode);
}
