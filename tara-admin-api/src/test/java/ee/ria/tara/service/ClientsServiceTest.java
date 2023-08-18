package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.repository.model.Institution;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.ScopeFilter;
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

import java.time.OffsetDateTime;
import java.util.List;

import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;
import static ee.ria.tara.service.helper.ClientTestHelper.compareClientWithHydraClient;
import static ee.ria.tara.service.helper.ClientTestHelper.validTARAClient;
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
    private ClientRepository repository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private AdminConfigurationProvider adminConfigurationProvider;

    @Mock
    private ClientValidator clientValidator;

    @Mock
    private ScopeFilter scopeFilter;

    @Mock
    private TaraOidcConfigurationProvider taraOidcConfigurationProvider;

    @InjectMocks
    private ClientsService clientsService;

    @BeforeEach
    public void setUp() {
        client = validTARAClient();
    }

    @Test
    public void testGetAllClients() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);
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
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);
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
        String registryCode = "10101010005";
        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.addClientToInstitution(registryCode, client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testAddClientToInstitutionWhenHydraRequestFails() throws ApiException {
        String registryCode = "1";
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage)).when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.addClientToInstitution(registryCode, client));

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
    public void testAddClientToInstitutionWithoutNewSecretGeneration() throws ApiException {
        String registryCode = "10101010005";
        client.setClientSecretExportSettings(null);

        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.addClientToInstitution(registryCode, client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));
        verify(oidcService, times(1)).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testAddClientToInstitutionAndSendSingingSecretEmail() throws ApiException {
        String registryCode = "10101010005";
        ClientSecretExportSettings secretExportSettings = new ClientSecretExportSettings();
        secretExportSettings.setRecipientEmail("email");
        secretExportSettings.setRecipientIdCode(registryCode);
        client.setClientSecretExportSettings(secretExportSettings);

        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.addClientToInstitution(registryCode, client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(1)).sendSigningSecretByEmail(any(Client.class));
        verify(oidcService, times(2)).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testAddClientToInstitutionWhenValidationFails() throws ApiException {
        String registryCode = "1";
        String errorMessage = "Oops.";
        doThrow(new InvalidDataException(errorMessage)).when(clientValidator).validateClient(client, PRIVATE);
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientsService.addClientToInstitution(registryCode, client));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
        verify(clientValidator, times(1)).validateClient(client, PRIVATE);
    }

    @Test
    public void testUpdateClient() throws ApiException {
        String registryCode = "1";
        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.updateClient(registryCode, "10101010005", client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testUpdateClientAndSendSigningSecretEmail() throws ApiException {
        String registryCode = "10101010005";
        ClientSecretExportSettings secretExportSettings = new ClientSecretExportSettings();
        secretExportSettings.setRecipientEmail("email");
        secretExportSettings.setRecipientIdCode("10101010005");
        client.setClientSecretExportSettings(secretExportSettings);

        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doNothing().when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.updateClient(registryCode, "10101010005", client);

        verify(repository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(1)).sendSigningSecretByEmail(any(Client.class));

        compareClientWithHydraClient(ClientHelper.convertToClient(hydraClient), hydraClient);
    }

    @Test
    public void testUpdateClientWhenHydraRequestFails() throws ApiException {
        String registryCode = "1";
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage))
                .when(oidcService).saveClient(any(HydraClient.class), anyString(), any(HttpMethod.class));
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.updateClient(registryCode, "10101010005", client));

        Assertions.assertTrue(exception.getMessage()
                .contains(errorMessage));
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class));
    }

    @Test
    public void testUpdateClientWhenValidationFails() throws ApiException {
        String registryCode = "1";
        String errorMessage = "Oops.";
        doThrow(new InvalidDataException(errorMessage)).when(clientValidator).validateClient(client, PRIVATE);
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(privateInstitution()).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientsService.updateClient(registryCode, "10101010005", client));

        Assertions.assertTrue(exception.getMessage().contains(errorMessage));
        verify(clientValidator, times(1)).validateClient(client, PRIVATE);
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

    private Institution privateInstitution() {
        Institution institution = new Institution();
        institution.setType(InstitutionType.TypeEnum.PRIVATE);
        return institution;
    }
}
