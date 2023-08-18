package ee.ria.tara.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import ee.ria.tara.model.Client;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.service.helper.ClientHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static ee.ria.tara.service.helper.ClientTestHelper.validTARAClient;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableConfigurationProperties
@ActiveProfiles({"integrationtest"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ClientsServiceIT {
    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    private final String registryCode = "testcode";

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private ClientsService service;
    @Autowired
    private TaraOidcConfigurationProvider taraOidcConfigurationProvider;

    private Client client;

    @BeforeEach
    public void setUp() {
        client = validTARAClient();
    }

    @BeforeAll
    public static void genericSetUp() {
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    public static void genericTearDown() {
        wireMockServer.stop();
    }

    @AfterEach
    public void tearDown() {
        clientRepository.deleteAll();
        reset();
    }

    @Test
    @Order(1)
    @Sql({"classpath:fixtures/ADD_institution_9999.sql"})
    public void addInstitutionToDatabase() {
        Assertions.assertNotNull(institutionRepository.findInstitutionByRegistryCode(registryCode));
        taraOidcConfigurationProvider.setUrl(wireMockServer.baseUrl());
    }

    @Test
    @Order(2)
    public void testAddClient() throws ApiException {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                post("/clients")
                        .willReturn(ok())
        );

        service.addClientToInstitution(registryCode, client);

        Assertions.assertEquals(1, clientRepository.findAll().size());
    }

    @Test
    @Order(3)
    public void testAddClientWhenClientExists() {
        Assertions.assertEquals(0, clientRepository.findAll().size());
        var client1 = ClientHelper.convertToEntity(client, institutionRepository.findInstitutionByRegistryCode(registryCode));
        clientRepository.save(client1);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () ->  service.addClientToInstitution(registryCode, client));
        Assertions.assertTrue(exception.getMessage()
                .contains("Client.exists"));
    }

    @Test
    @Order(4)
    public void testNotAddClientWhenHydraRequestFailsClientError400() {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                post("/clients")
                        .willReturn(badRequest()
                                .withBody("Oops, something went wrong."))
        );

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> service.addClientToInstitution(registryCode, client));

        Assertions.assertEquals(0, clientRepository.findAll().size());
        Assertions.assertTrue(exception.getMessage().contains("Oidc.clientError.400"));
    }

    @Test
    @Order(5)
    public void testNotAddClientWhenHydraRequestFailsClientError409() {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                post("/clients")
                        .willReturn(status(409)
                                .withBody("Oops, something went wrong."))
        );

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> service.addClientToInstitution(registryCode, client));

        Assertions.assertEquals(0, clientRepository.findAll().size());
        Assertions.assertTrue(exception.getMessage().contains("Oidc.clientError.409"));
    }

    @Test
    @Order(6)
    public void testNotAddClientWhenHydraRequestFailsServerError() {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                post("/clients")
                        .willReturn(serverError()
                                .withBody("Oops, something went wrong."))
        );

        ApiException exception = assertThrows(ApiException.class,
                () -> service.addClientToInstitution(registryCode, client));

        Assertions.assertEquals(0, clientRepository.findAll().size());
        Assertions.assertTrue(exception.getMessage()
                .contains("Oidc.serverError"));
    }

    @Test
    @Order(7)
    public void testUpdateClient() throws ApiException {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                put("/clients/" + client.getClientId())
                        .willReturn(ok())
        );

        service.updateClient(registryCode, client.getClientId(), client);

        Assertions.assertEquals(1, clientRepository.findAll().size());
    }

    @Test
    @Order(8)
    public void testNotUpdateClientWhenHydraRequestFailsServerError() {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                put("/clients/" + client.getClientId())
                        .willReturn(serverError()
                                .withBody("Oops, something went wrong."))
        );

        FatalApiException exception = assertThrows(FatalApiException.class,
                () -> service.updateClient(registryCode, client.getClientId(), client));

        Assertions.assertEquals(0, clientRepository.findAll().size());
        Assertions.assertTrue(exception.getMessage()
                .contains("Oidc.serverError"));
    }


    @Test
    @Order(9)
    public void testDeleteClient() throws ApiException {
        Assertions.assertEquals(0, clientRepository.findAll().size());
        clientRepository.save(ClientHelper.convertToEntity(client, institutionRepository.findInstitutionByRegistryCode(registryCode)));
        Assertions.assertEquals(1, clientRepository.findAll().size());

        stubFor(
                delete("/clients/" + client.getClientId())
                        .willReturn(ok())
        );

        service.deleteClient(registryCode, client.getClientId());

        Assertions.assertEquals(0, clientRepository.findAll().size());
    }

    @Test
    @Order(10)
    public void testDeleteClientWhenNotExists() throws ApiException {
        Assertions.assertEquals(0, clientRepository.findAll().size());

        stubFor(
                delete("/clients/" + client.getClientId())
                        .willReturn(ok())
        );

        service.deleteClient(registryCode, client.getClientId());

        Assertions.assertEquals(0, clientRepository.findAll().size());
    }

    @Test
    @Order(11)
    public void testNotDeleteClientWhenHydraRequestFailsClientError() {
        Assertions.assertEquals(0, clientRepository.findAll().size());
        clientRepository.save(ClientHelper.convertToEntity(client, institutionRepository.findInstitutionByRegistryCode(registryCode)));
        Assertions.assertEquals(1, clientRepository.findAll().size());

        stubFor(
                delete("/clients/" + client.getClientId())
                        .willReturn(badRequest()
                                .withBody("Oops, something went wrong."))
        );

        RecordDoesNotExistException exception = assertThrows(RecordDoesNotExistException.class,
                () -> service.deleteClient(registryCode, client.getClientId()));

        Assertions.assertEquals(1, clientRepository.findAll().size());
        Assertions.assertTrue(exception.getMessage()
                .contains("Client.notExists"));
    }

    @Test
    @Order(12)
    public void testNotDeleteClientWhenHydraRequestFailsServerError() {
        Assertions.assertEquals(0, clientRepository.findAll().size());
        clientRepository.save(ClientHelper.convertToEntity(client, institutionRepository.findInstitutionByRegistryCode(registryCode)));
        Assertions.assertEquals(1, clientRepository.findAll().size());

        stubFor(
                delete("/clients/" + client.getClientId())
                        .willReturn(serverError()
                                .withBody("Oops, something went wrong."))
        );

        FatalApiException exception = assertThrows(FatalApiException.class,
                () -> service.deleteClient(registryCode, client.getClientId()));

        Assertions.assertEquals(1, clientRepository.findAll().size());
        Assertions.assertTrue(exception.getMessage()
                .contains("Oidc.serverError"));
    }

    @Test
    @Order(13)
    public void clearDatabase() {
        clientRepository.deleteAll();
        institutionRepository.deleteAll();

        Assertions.assertEquals(0, institutionRepository.findAll().size());
        Assertions.assertEquals(0, clientRepository.findAll().size());
    }

}
