package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.repository.model.Institution;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.model.HydraClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

import static ee.ria.tara.service.helper.ClientTestHelper.compareClientWithHydraClient;
import static ee.ria.tara.service.helper.ClientTestHelper.createTestClient;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClientsServiceTest {

    private Client client;

    @Captor
    private ArgumentCaptor<ee.ria.tara.repository.model.Client> clientEntityCaptor;

    @Mock
    private ee.ria.tara.repository.model.Client entity;

    @Mock
    private Institution institution;

    @Mock
    private OidcService oidcService;

    @Mock
    private ClientSecretEmailService clientSecretEmailService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ClientRepository repository;
    @Mock
    private InstitutionRepository institutionRepository;
    @Mock
    private AdminConfigurationProvider adminConfigurationProvider;

    @InjectMocks
    private ClientsService clientsService;

    @BeforeEach
    public void setUp() {
        client = createTestClient();

        ReflectionTestUtils.setField(clientsService, "baseUrl", "http://");
    }

    @Test
    public void testGetAllClients() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(List.of()).when(repository).findAll();
        doReturn(List.of(hydraClient)).when(oidcService).getAllClients(nullable(String.class));

        List<Client> clientList = clientsService.getAllClients(null);

        Assertions.assertEquals(1, clientList.size());
        verify(repository, times(1)).findAll();
        verify(repository, times(0)).findByClientId(anyString());
        compareClientWithHydraClient(clientList.get(0), hydraClient);
    }

    @Test
    public void testGetAllClientsWhenHydraRequestFails() throws FatalApiException {
        String errorMessage = "Oops";
        doThrow(new FatalApiException(errorMessage)).when(oidcService).getAllClients(nullable(String.class));

        FatalApiException exception = assertThrows(FatalApiException.class,
                () -> clientsService.getAllClients(null));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
        verify(repository, times(0)).save(any());
    }

    @Test
    public void testGetAllClientsWithFilter() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(institution).when(entity).getInstitution();
        doReturn(entity).when(repository).findByClientId(client.getClientId());
        doReturn(List.of(hydraClient)).when(oidcService).getAllClients(nullable(String.class));

        List<Client> clientList = clientsService.getAllClients(client.getClientId());

        Assertions.assertEquals(1, clientList.size());
        verify(repository, times(0)).findAll();
        verify(repository, times(1)).findByClientId(client.getClientId());
        compareClientWithHydraClient(clientList.get(0), hydraClient);
    }

    @Test
    public void testGetClientsWithFilterWhenHydraRequestFails() throws FatalApiException {
        String errorMessage = "Oops.";
        doThrow(new FatalApiException(errorMessage))
                .when(oidcService).getAllClients(anyString());

        FatalApiException exception = assertThrows(FatalApiException.class,
                () -> clientsService.getAllClients("clientId"));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    public void testAddClientToInstitution() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        clientsService.addClientToInsitution("1", client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testAddClientToInstitutionWhenHydraRequestFails() throws ApiException {
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage)).when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.addClientToInsitution("1", client));

        Assertions.assertTrue(exception.getMessage()
                .contains(errorMessage));
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));
    }

    @Test
    public void testGetAllInstitutionsClients() throws ApiException {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(registryCode).when(institution).getRegistryCode();
        doReturn(institution).when(entity).getInstitution();
        doReturn(client.getClientId()).when(entity).getClientId();
        doReturn(List.of(entity)).when(repository).findAllByInstitution_RegistryCode(registryCode);
        doReturn(List.of(hydraClient)).when(oidcService).getAllClients(nullable(String.class));

        List<Client> clientList = clientsService.getAllInstitutionsClients(registryCode);

        Assertions.assertEquals(1, clientList.size());
        verify(repository, times(1)).findAllByInstitution_RegistryCode(registryCode);
        compareClientWithHydraClient(clientList.get(0), hydraClient);
    }

    @Test
    public void testGetAllInstitutionsClientsWhenHydraRequestFails() throws FatalApiException {
        String errorMessage = "Oops.";
        doThrow(new FatalApiException(errorMessage)).when(oidcService).getAllClients(nullable(String.class));

        FatalApiException exception = assertThrows(FatalApiException.class,
                () -> clientsService.getAllInstitutionsClients("1"));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    public void testAddClientToInstitutionAndSendSingingSecretEmail() throws ApiException {
        ClientSecretExportSettings secretExportSettings = new ClientSecretExportSettings();
        secretExportSettings.setRecipientEmail("email");
        secretExportSettings.setRecipientIdCode("10101010005");
        client.setClientSecretExportSettings(secretExportSettings);

        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        clientsService.addClientToInsitution("10101010005", client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(clientSecretEmailService, times(1)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testUpdateClient() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        clientsService.updateClient("1", "10101010005", client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testUpdateClientAndSendSigningSecretEmail() throws ApiException {
        ClientSecretExportSettings secretExportSettings = new ClientSecretExportSettings();
        secretExportSettings.setRecipientEmail("email");
        secretExportSettings.setRecipientIdCode("10101010005");
        client.setClientSecretExportSettings(secretExportSettings);

        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        clientsService.updateClient("1", "10101010005", client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(clientSecretEmailService, times(1)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testUpdateClientWhenHydraRequestFails() throws ApiException {
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage))
                .when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.updateClient("1", "10101010005", client));

        Assertions.assertTrue(exception.getMessage()
                .contains(errorMessage));
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));
    }

    @Test
    public void testDeleteClient() throws ApiException {
        String clientId = "1";
        String registryCode = "1";

        doNothing().when(oidcService).deleteClient(anyString());

        clientsService.deleteClient(registryCode, clientId);

        verify(repository, times(1)).deleteByClientIdAndInstitution_RegistryCode(clientId, registryCode);
    }

    @Test
    public void testDeleteClientWhenHydraRequestFails() throws ApiException {
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage)).when(oidcService).deleteClient(anyString());

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.deleteClient("1", "1"));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
    }
}
