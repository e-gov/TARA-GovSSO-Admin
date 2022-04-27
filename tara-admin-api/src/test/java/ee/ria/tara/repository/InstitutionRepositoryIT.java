package ee.ria.tara.repository;

import ee.ria.tara.model.Client;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.repository.model.Institution;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.helper.ClientTestHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
@EnableConfigurationProperties
@ActiveProfiles({"integrationtest"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InstitutionRepositoryIT {
    private final String registryCode = "RC12345CIS";
    private final String institutionName = "Example Organization";
    private final Institution testInstitution = createTestInstitution(institutionName, registryCode);

    private final InstitutionRepository repository;
    private final ClientRepository clientRepository;

    @Test
    @Order(1)
    public void clearDatabaseFromDefaultInstitutions() {
        repository.deleteAll();
        Assertions.assertEquals(0, repository.findAll().size());
    }

    @Test
    @Order(2)
    public void testAddInstitution() {
        List<Institution> institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(registryCode, registryCode);

        Assertions.assertEquals(0, institutions.size());

        repository.save(testInstitution);

        institutions = repository.findAll();

        Assertions.assertEquals(1, institutions.size());
    }

    @Test
    @Order(3)
    public void testAddInstitutionWhenAlreadyExists() {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.save(testInstitution));
    }

    @Test
    @Order(4)
    public void testUpdateExistingInstitution() {
        String newRegistryCode = "2";
        List<Institution> retrievedInstitution = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(registryCode, registryCode);

        Assertions.assertEquals(1, retrievedInstitution.size());

        Institution institution = retrievedInstitution.get(0);
        institution.setRegistryCode(newRegistryCode);

        repository.save(institution);

        List<Institution> institutions = repository.findAll();

        Assertions.assertEquals(1, institutions.size());
        Assertions.assertEquals(newRegistryCode, institutions.get(0).getRegistryCode());
    }

    @Test
    @Order(5)
    public void testDeleteInstitution() {
        List<Institution> institutions = repository.findAll();

        Assertions.assertEquals(1, institutions.size());

        repository.deleteById(institutions.get(0).getId());

        institutions = repository.findAll();

        Assertions.assertEquals(0, institutions.size());
    }

    @Test
    @Order(6)
    public void testFindInstitutionByRegistryCodeAndName() {
        String caseSensitiveRegistryCode = "CaseSensitiveRegistryCode";
        String caseSenstiveInstitutionName = "CaseSensitiveName";

        Institution institution = createTestInstitution(caseSenstiveInstitutionName, caseSensitiveRegistryCode);

        repository.save(institution);
        repository.save(testInstitution);

        institution.setCreatedAt(institution.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        institution.setUpdatedAt(institution.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        testInstitution.setCreatedAt(testInstitution.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        testInstitution.setUpdatedAt(testInstitution.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));

        List<Institution> institutions = repository.findAll();
        Assertions.assertEquals(2, institutions.size());

        // FULL REGISTRY CODE AND NAME
        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(institutionName, institutionName);
        institutions.get(0).setCreatedAt(institutions.get(0).getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        institutions.get(0).setUpdatedAt(institutions.get(0).getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(1, institutions.size());
        Assertions.assertEquals(testInstitution.toString(), institutions.get(0).toString());

        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(caseSensitiveRegistryCode, caseSensitiveRegistryCode);
        institutions.get(0).setCreatedAt(institutions.get(0).getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        institutions.get(0).setUpdatedAt(institutions.get(0).getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(1, institutions.size());
        Assertions.assertEquals(institution.toString(), institutions.get(0).toString());

        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(caseSensitiveRegistryCode, institutionName);
        Assertions.assertEquals(2, institutions.size());

        // CASE INSENSITIVE
        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(institutionName, institutionName.toUpperCase());
        Assertions.assertEquals(1, institutions.size());

        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(caseSensitiveRegistryCode.toUpperCase(), caseSensitiveRegistryCode);
        Assertions.assertEquals(1, institutions.size());

        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(caseSensitiveRegistryCode.toUpperCase(), institutionName.toUpperCase());
        Assertions.assertEquals(2, institutions.size());

        String partialName = caseSenstiveInstitutionName.substring(0, caseSenstiveInstitutionName.length() - 2);
        String partialRegistryCode = caseSensitiveRegistryCode.substring(0, caseSensitiveRegistryCode.length() - 2);

        // PARTIAL TEXT
        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(partialName, partialName);
        Assertions.assertEquals(1, institutions.size());

        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(partialRegistryCode, partialRegistryCode);
        Assertions.assertEquals(1, institutions.size());

        institutions = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(registryCode.substring(0, registryCode.length() - 2), partialName);
        Assertions.assertEquals(2, institutions.size());
    }

    @Test
    @Order(7)
    public void testUpdateInstitutionWhenConnectedClient() {
        String newRegistryCode = "newRegistryCode";

        Client client = ClientTestHelper.validTARAClient();
        Institution institution = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(institutionName, institutionName).get(0);

        clientRepository.save(ClientHelper.convertToEntity(client, institution));

        institution.setRegistryCode(newRegistryCode);
        repository.save(institution);

        List<ee.ria.tara.repository.model.Client> clientEntityList = clientRepository.findAll();

        Assertions.assertEquals(1, clientEntityList.size());
        Assertions.assertEquals(newRegistryCode, clientEntityList.get(0).getInstitution().getRegistryCode());
    }

    @Test
    @Order(8)
    public void testDeleteInstitutionWhenConnectedClient() {
        Institution institution = repository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(institutionName, institutionName).get(0);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.delete(institution));
    }

    @Test
    @Order(9)
    public void testDeleteWhenNotExists() {
        clientRepository.deleteAll();
        repository.deleteAll();

        Assertions.assertEquals(0, repository.findAll().size());

        repository.delete(testInstitution);
    }

    @Test
    @Order(9)
    public void clearDatabase() {
        clientRepository.deleteAll();
        repository.deleteAll();

        Assertions.assertEquals(0, repository.findAll().size());
        Assertions.assertEquals(0, clientRepository.findAll().size());
    }

    private Institution createTestInstitution(String name, String registryCode) {
        Institution institution = new Institution();

        institution.setRegistryCode(registryCode);
        institution.setType(InstitutionType.TypeEnum.PUBLIC);
        institution.setContactPhone("37200000001");
        institution.setContactAddress("Lossi plats 1, Tallinn, Harjumaa, Eesti");
        institution.setName(name);
        institution.setBillingEmail("invoice@example.com");
        institution.setContactEmail("info@example.com");
        institution.setClients(List.of());

        return institution;
    }
}
