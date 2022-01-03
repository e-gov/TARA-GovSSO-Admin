package ee.ria.tara.repository;

import ee.ria.tara.repository.model.Client;
import ee.ria.tara.repository.model.Institution;
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
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@EnableConfigurationProperties
@ActiveProfiles({"integrationtest"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ClientRepositoryIT {
    @Autowired
    private ClientRepository repository;
    @Autowired
    private InstitutionRepository institutionRepository;

    private final String institutionRegistryCode = "testcode";

    @Test
    @Order(1)
    @Sql({"classpath:fixtures/ADD_institution_9999.sql"})
    public void testAddClient() {
        Client testClient = createTestClient(institutionRepository.findInstitutionByRegistryCode(institutionRegistryCode), "clientId");
        List<Client> clients = repository.findAll();

        Assertions.assertEquals(0, clients.size());

        repository.save(testClient);

        clients = repository.findAll();

        Assertions.assertEquals(1, clients.size());
    }

    @Test
    @Order(2)
    public void testUpdateExistingClient() {
        String clientId = "newClientId";
        List<Client> retrievedClient = repository.findAll();

        Assertions.assertEquals(1, retrievedClient.size());

        Client client = retrievedClient.get(0);
        String oldClientId = client.getClientId();

        client.setClientId(clientId);

        repository.save(client);

        List<Client> clients = repository.findAll();

        Assertions.assertEquals(1, clients.size());
        Assertions.assertEquals(clientId, clients.get(0).getClientId());
        Assertions.assertNotEquals(clientId, oldClientId);
    }

    @Test
    @Order(3)
    public void testDeleteClient() {
        List<Client> clients = repository.findAll();

        Assertions.assertEquals(1, clients.size());

        repository.deleteById(clients.get(0).getId());

        clients = repository.findAll();

        Assertions.assertEquals(0, clients.size());
    }

    @Test
    @Order(4)
    @Transactional
    public void testFindByClientId() {
        Institution institution = institutionRepository.findInstitutionByRegistryCode(institutionRegistryCode);
        Client client1 = createTestClient(institution, "clientId");
        Client client2 = createTestClient(institution, "clientId2");

        repository.save(client1);
        repository.save(client2);

        List<Client> clients = repository.findAll();
        Assertions.assertEquals(2, clients.size());

        Client entity = repository.findByClientId(client2.getClientId());
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(client2.toString(), entity.toString());
    }

    @Test
    @Order(5)
    public void clearDatabase() {
        repository.deleteAll();
        institutionRepository.deleteAll();

        Assertions.assertEquals(0, institutionRepository.findAll().size());
        Assertions.assertEquals(0, repository.findAll().size());
    }

    private Client createTestClient(Institution institution, String clientId) {
        Client client = new Client();

        client.setClientId(clientId);
        client.setInstitution(institution);
        client.setInfoNotificationEmails(List.of());
        client.setSlaNotificationEmails(List.of());

        return client;
    }
}
