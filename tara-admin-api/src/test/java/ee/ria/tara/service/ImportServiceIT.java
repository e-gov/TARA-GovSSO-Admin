package ee.ria.tara.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientContact;
import ee.ria.tara.model.Institution;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
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
import org.springframework.test.util.ReflectionTestUtils;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static ee.ria.tara.service.helper.ClientTestHelper.createValidTARAClient;
import static ee.ria.tara.service.helper.ClientTestHelper.createValidPrivateInstitution;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableConfigurationProperties
@ActiveProfiles({"integrationtest"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ImportServiceIT {
    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    private final String registryCode = "testcode";

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private ImportService importService;

    private Client client;

    @BeforeEach
    public void setUp() {
        client = createValidTARAClient();
        ReflectionTestUtils.setField(importService, "baseUrl", wireMockServer.baseUrl());
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
        reset();
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
        assertEquals("Invalid header row. Expecting following header columns: [Institution name, Institution registry code, Client ID, Redirect URI, Secret, Return URL (legacy), Client name (et), Client name (en), Client name (ru), Client shortname (et), Client shortname (en), Client shortname (ru), Contacts, eIDAS RequesterID, Description]", expectedEx.getMessage());
    }

    @Test
    public void importFromExcel_validFile() throws Exception {

        Map<Institution, List<Client>> institutionListMap = importService.importFromExcelFile(new FileInputStream("src/test/resources/import_files/clients.xlsx"));
        assertEquals(2, institutionListMap.size());

        Iterator<Map.Entry<Institution, List<Client>>> iterator = institutionListMap.entrySet().iterator();
        Map.Entry<Institution, List<Client>> entry = iterator.next();
        assertEquals("Example Institution", entry.getKey().getName());
        assertEquals("12345678", entry.getKey().getRegistryCode());
        assertEquals(6, entry.getValue().size());

        assertEquals("openIdDemo-1", entry.getValue().get(0).getClientId());
        assertEquals(List.of("https://localhost:8444/authenticate"), entry.getValue().get(0).getRedirectUris());
        assertEquals("2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b", entry.getValue().get(0).getSecret());
        assertEquals(null, entry.getValue().get(0).getClientUrl());
        assertEquals(null, entry.getValue().get(0).getClientName().getEt());
        assertEquals(null, entry.getValue().get(0).getClientName().getEn());
        assertEquals(null, entry.getValue().get(0).getClientName().getRu());
        assertEquals(null, entry.getValue().get(0).getClientShortName().getEt());
        assertEquals(null, entry.getValue().get(0).getClientShortName().getEn());
        assertEquals(null, entry.getValue().get(0).getClientShortName().getRu());
        assertEquals(List.of(), entry.getValue().get(0).getClientContacts());
        assertEquals(null, entry.getValue().get(0).getDescription());

        assertEquals("openIdDemo-5", entry.getValue().get(4).getClientId());
        assertEquals(List.of("^https://tara-client.example.com:8451/oauth/response.*"), entry.getValue().get(4).getRedirectUris());
        assertEquals("2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b", entry.getValue().get(4).getSecret());
        assertEquals("https://tara-client.example.com:8451/ui", entry.getValue().get(4).getClientUrl());
        assertEquals("Teenusenimi_est", entry.getValue().get(4).getClientName().getEt());
        assertEquals("Servicename_en", entry.getValue().get(4).getClientName().getEn());
        assertEquals("Тестовая система", entry.getValue().get(4).getClientName().getRu());
        assertEquals("Lühinimi_est", entry.getValue().get(4).getClientShortName().getEt());
        assertEquals("Shortname_en", entry.getValue().get(4).getClientShortName().getEn());
        assertEquals("Тест-ru", entry.getValue().get(4).getClientShortName().getRu());
        assertEquals(List.of(getContact("Kalle Kajakas", "kalle.kajakas@kajakas.com", "+37207114725", "example department")), entry.getValue().get(4).getClientContacts());
        assertEquals("openIdDemo3", entry.getValue().get(4).getDescription());
        assertEquals("58ee2267-7864-4e09-958b-b53c3135298e", entry.getValue().get(4).getEidasRequesterId());

        entry = iterator.next();
        assertEquals("Example Institution", entry.getKey().getName());
        assertEquals("00000000", entry.getKey().getRegistryCode());
        assertEquals(1, entry.getValue().size());
    }

    private ClientContact getContact(String name, String email, String phone, String department) {
        ClientContact clientContact = new ClientContact();
        clientContact.setName(name);
        clientContact.setEmail(email);
        clientContact.setPhone(phone);
        clientContact.setDepartment(department);
        return clientContact;
    }

    @Test
    @Order(999)
    @Sql({"classpath:fixtures/ADD_institution_9999.sql"})
    public void saveClientToDatabase() {
        Assertions.assertNotNull(institutionRepository.findInstitutionByRegistryCode(registryCode));

        Institution mockInstiution = createValidPrivateInstitution("1234567890", "Test institution & company");
        Client mockClient = createValidTARAClient();
        stubFor(get("/clients/" + mockClient.getClientId()).willReturn(ok()));
        stubFor(put("/clients/" + mockClient.getClientId()).willReturn(ok()));

        importService.saveClient(mockInstiution, mockClient);
        List<ee.ria.tara.repository.model.Institution> institutions = institutionRepository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(mockInstiution.getRegistryCode(), "null");
        Assertions.assertEquals(1, institutions.size());
        Assertions.assertEquals(mockInstiution.getName(), institutions.get(0).getName());
        Assertions.assertEquals(mockInstiution.getRegistryCode(), institutions.get(0).getRegistryCode());

        ee.ria.tara.repository.model.Client client = clientRepository.findByClientId(mockClient.getClientId());
        Assertions.assertNotNull(client);
        Assertions.assertEquals(mockClient.getClientId(), client.getClientId());

        clientRepository.deleteAll();
        institutionRepository.deleteAll();
        Assertions.assertEquals(0, institutionRepository.findAll().size());
        Assertions.assertEquals(0, clientRepository.findAll().size());
    }
}
