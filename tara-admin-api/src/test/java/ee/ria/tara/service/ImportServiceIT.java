package ee.ria.tara.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.model.ClientImportResponse;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
import wiremock.org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableConfigurationProperties
@ActiveProfiles({"integrationtest"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImportServiceIT {
    private static final WireMockServer hydraWireMockServer =
            new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private ImportService importService;
    @Autowired
    private TaraOidcConfigurationProvider taraOidcConfigurationProvider;

    @BeforeEach
    public void setUp() {
        taraOidcConfigurationProvider.setUrl(hydraWireMockServer.baseUrl());
    }

    @BeforeAll
    public static void genericSetUp() {
        hydraWireMockServer.start();
        configureFor("localhost", hydraWireMockServer.port());
    }

    @AfterAll
    public static void genericTearDown() {
        hydraWireMockServer.stop();
    }

    @AfterEach
    public void tearDown() {
        hydraWireMockServer.resetAll();
        clientRepository.deleteAll();
        institutionRepository.deleteAll();
    }

    @Test
    public void importFromExcel_EmptyFile() {
        Exception expectedEx = assertThrows(EmptyFileException.class, () -> {
            importService.importFromExcelFile(InputStream.nullInputStream());
        });
        assertEquals("The supplied file was empty (zero bytes long)", expectedEx.getMessage());
    }

    @Test
    public void importFromExcel_InvalidFile_NotAnExcelFile() {
        Exception expectedEx = assertThrows(NotOfficeXmlFileException.class, () -> {
            importService.importFromExcelFile(IOUtils.toInputStream("<invalid-file>", "UTF-8"));
        });
        assertEquals("No valid entries or contents found, this is not a valid OOXML (Office Open XML) file", expectedEx.getMessage());
    }

    @Test
    public void importFromExcel_InvalidFile_InvalidExcelFile() {
        Exception expectedEx = assertThrows(IllegalArgumentException.class, () -> {
            importService.importFromExcelFile(new FileInputStream("src/test/resources/import_files/invalid_content.xlsx"));
        });
        assertEquals("Invalid header row. Expecting following header columns: [Institution name, Institution registry code, Client ID, Redirect URI, Secret, Return URL (legacy), Client name (et), Client name (en), Client name (ru), Client shortname (et), Client shortname (en), Client shortname (ru), Contacts, eIDAS RequesterID, Description, Token request allowed IP addresses]", expectedEx.getMessage());
    }

    @Test
    @Order(999)
    @Sql({"classpath:fixtures/ADD_institution_9999.sql"})
    public void importFromExcel_validFile() throws Exception {
        List<String> expectedSuccessfulClientIds = List.of(
                "openIdDemo-1",
                "openIdDemo-2",
                "openIdDemo-4",
                "openIdDemo-7"
        );
        List<String> invalidClientIds = List.of(
                "openIdDemo-3",
                "openIdDemo-5",
                "openIdDemo-6"
        );

        hydraWireMockServer.stubFor(put(urlPathTemplate("/admin/clients/{clientId}")).willReturn(ok()));
        hydraWireMockServer.stubFor(get(urlPathTemplate("/admin/clients/{clientId}")).willReturn(ok()));

        ClientImportResponse actual =
                importService.importFromExcelFile(new FileInputStream("src/test/resources/import_files/clients.xlsx"));

        for (String clientId : expectedSuccessfulClientIds) {
            String expectedBody = Files.readString(
                    Paths.get(getClass().getResource("/import_client_bodies/" + clientId + ".json").toURI()),
                    StandardCharsets.UTF_8
            );
            hydraWireMockServer.verify(putRequestedFor(urlPathEqualTo("/admin/clients/" + clientId))
                    .withRequestBody(equalToJson(expectedBody)));
        }

        ClientImportResponse expected = new ClientImportResponse();
        expected.setClientsCount(expectedSuccessfulClientIds.size() + invalidClientIds.size());
        expected.setClientsImportFailedCount(invalidClientIds.size());
        expected.setClientsImportSuccessCount(expectedSuccessfulClientIds.size());
        expected.setClientsNotImported(invalidClientIds);
        expected.setStatus("FINISHED_WITH_ERRORS");

        assertEquals(expected, actual);
    }

    @Test
    @Order(999)
    @Sql({"classpath:fixtures/ADD_institution_9999.sql"})
    public void importFromExcel_oneHydraRequestFails() throws Exception {
        List<String> expectedSuccessfulClientIds = List.of(
                "openIdDemo-1",
                "openIdDemo-4",
                "openIdDemo-7"
        );
        List<String> hydraRequestFailsClientIds = List.of(
                "openIdDemo-2"
        );
        List<String> invalidClientIds = List.of(
                "openIdDemo-3",
                "openIdDemo-5",
                "openIdDemo-6"
        );
        List<String> importFailedClientIds = Stream.of(hydraRequestFailsClientIds, invalidClientIds)
                .flatMap(Collection::stream)
                .sorted()
                .toList();

        hydraWireMockServer.stubFor(put(urlPathTemplate("/admin/clients/{clientId}")).willReturn(ok()));
        hydraWireMockServer.stubFor(get(urlPathTemplate("/admin/clients/{clientId}")).willReturn(ok()));
        for (String clientId : hydraRequestFailsClientIds) {
            hydraWireMockServer.stubFor(put(urlPathTemplate("/admin/clients/" + clientId)).atPriority(1).willReturn(serverError()));
        }

        ClientImportResponse actual =
                importService.importFromExcelFile(new FileInputStream("src/test/resources/import_files/clients.xlsx"));

        for (String clientId : expectedSuccessfulClientIds) {
            String expectedBody = Files.readString(
                    Paths.get(getClass().getResource("/import_client_bodies/" + clientId + ".json").toURI()),
                    StandardCharsets.UTF_8
            );
            hydraWireMockServer.verify(putRequestedFor(urlPathEqualTo("/admin/clients/" + clientId))
                    .withRequestBody(equalToJson(expectedBody)));
        }

        ClientImportResponse expected = new ClientImportResponse();
        expected.setClientsCount(expectedSuccessfulClientIds.size() + importFailedClientIds.size());
        expected.setClientsImportFailedCount(importFailedClientIds.size());
        expected.setClientsImportSuccessCount(expectedSuccessfulClientIds.size());
        expected.setClientsNotImported(importFailedClientIds);
        expected.setStatus("FINISHED_WITH_ERRORS");

        assertEquals(expected, actual);

        for (String clientId : expectedSuccessfulClientIds) {
            assertNotNull(clientRepository.findByClientId(clientId));
        }
        for (String clientId : importFailedClientIds) {
            assertNull(clientRepository.findByClientId(clientId));
        }
    }

}
