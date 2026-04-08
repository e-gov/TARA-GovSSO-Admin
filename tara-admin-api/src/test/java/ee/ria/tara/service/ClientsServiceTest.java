package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.repository.model.Institution;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.ScopeFilter;
import ee.ria.tara.service.helper.SecureRandomAlphaNumericStringGenerator;
import ee.ria.tara.service.model.HydraClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static ee.ria.tara.service.helper.ClientTestHelper.compareClientWithHydraClient;
import static ee.ria.tara.service.helper.ClientTestHelper.validTARAClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClientsServiceTest {

    private Client client;
    private Institution institution;

    @Captor
    private ArgumentCaptor<ee.ria.tara.repository.model.Client> clientEntityCaptor;

    @Captor
    private ArgumentCaptor<HydraClient> hydraClientCaptor;

    @Mock
    private OidcService oidcService;

    @Mock
    private ClientSecretEmailService clientSecretEmailService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private ClientValidator clientValidator;

    @Mock
    private ScopeFilter scopeFilter;

    @Mock
    private SecureRandomAlphaNumericStringGenerator secureRandomAlphaNumericStringGenerator;

    private ClientsService clientsService;

    @BeforeEach
    public void setUp() {
        AdminConfigurationProvider adminConfigurationProvider = new AdminConfigurationProvider();
        clientsService = new ClientsService(
                clientRepository,
                institutionRepository,
                clientSecretEmailService,
                oidcService,
                adminConfigurationProvider,
                clientValidator,
                scopeFilter,
                secureRandomAlphaNumericStringGenerator,
                new ResourcelessTransactionManager()
        );
        client = validTARAClient();
        institution = institution(client.getInstitutionMetainfo());
    }

    @Test
    public void getAllClients_allClientsReturned() {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false, null);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(List.of()).when(clientRepository).findAll();
        doReturn(List.of(hydraClient)).when(oidcService).getAllClients();

        List<Client> clientList = clientsService.getAllClients();

        assertEquals(1, clientList.size());
        verify(clientRepository, times(1)).findAll();
        verify(clientRepository, times(0)).findByClientId(anyString());
        compareClientWithHydraClient(hydraClient, clientList.get(0));
    }

    @Test
    public void getAllClients_whenOidcServiceFails_exceptionRethrown() {
        String errorMessage = "Oops";
        doThrow(new FatalApiException(errorMessage)).when(oidcService).getAllClients();

        FatalApiException exception = assertThrows(FatalApiException.class,
                () -> clientsService.getAllClients());

        assertTrue(exception.getMessage().contains(errorMessage));
        verify(clientRepository, times(0)).save(any());
    }

    @Test
    public void getClient_clientReturned(){
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false, null);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        ee.ria.tara.repository.model.Client entity = ClientHelper.convertToEntity(client, institution);
        entity.setId(7L);

        doReturn(entity).when(clientRepository).findByClientId(client.getClientId());
        doReturn(hydraClient).when(oidcService).getClient(eq(client.getClientId()));

        Client actual = clientsService.getClient(client.getClientId());

        verify(clientRepository, times(0)).findAll();
        verify(clientRepository, times(1)).findByClientId(client.getClientId());
        compareClientWithHydraClient(hydraClient, actual);
    }

    @Test
    public void getClient_whenOidcServiceFails_exceptionRethrown() {
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage))
                .when(oidcService).getClient(eq("clientId"));

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.getClient("clientId"));

        assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    public void addClientToInstitution_whenSecretRecipientNotSet_clientAddedAndSecretNotEmailed() {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        client.setClientSecretExportSettings(null);

        doNothing().when(oidcService).createClient(any(HydraClient.class));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.addClientToInstitution(registryCode, client);

        verify(oidcService, times(1)).createClient(hydraClientCaptor.capture());
        verify(clientRepository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class), anyString());

        HydraClient savedHydraClient = hydraClientCaptor.getValue();
        ee.ria.tara.repository.model.Client savedEntity = clientEntityCaptor.getValue();

        assertEquals(ClientHelper.convertToHydraClient(client, false, null), savedHydraClient);
        assertEquals(ClientHelper.convertToEntity(client, institution), savedEntity);
    }

    @Test
    public void addClientToInstitution_whenOidcServiceFails_apiExceptionThrowAndSecretNotEmailed() {
        String registryCode = "1";
        String errorMessage = "Oops.";

        doThrow(new ApiException(errorMessage)).when(oidcService).createClient(any(HydraClient.class));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.addClientToInstitution(registryCode, client));

        assertTrue(exception.getMessage()
                .contains(errorMessage));

        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class), anyString());
    }

    @Test
    public void getClientsByInstitution_clientsReturned() {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        HydraClient hydraClient = ClientHelper.convertToHydraClient(client, false, null);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        ee.ria.tara.repository.model.Client entity = ClientHelper.convertToEntity(client, institution);
        entity.setId(7L);

        doReturn(List.of(entity)).when(clientRepository).findAllByInstitution_RegistryCode(registryCode);
        doReturn(List.of(hydraClient)).when(oidcService).getAllClients();

        List<Client> clientList = clientsService.getClientsByInstitution(registryCode);

        assertEquals(1, clientList.size());
        verify(clientRepository, times(1)).findAllByInstitution_RegistryCode(registryCode);
        compareClientWithHydraClient(hydraClient, clientList.get(0));
    }

    @Test
    public void getClientsByInstitution_whenOidcServiceFails_exceptionRethrown() {
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage)).when(oidcService).getAllClients();

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.getClientsByInstitution("1"));

        assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    public void addClientToInstitution_whenSecretRecipientNotDefined_secretNotEmailed() {
        String registryCode = "10101010005";
        client.setClientSecretExportSettings(null);

        doNothing().when(oidcService).createClient(any(HydraClient.class));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.addClientToInstitution(registryCode, client);

        verify(clientRepository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class), anyString());
        verify(oidcService, times(1)).createClient(hydraClientCaptor.capture());

        HydraClient savedHydraClient = hydraClientCaptor.getValue();
        ee.ria.tara.repository.model.Client savedEntity = clientEntityCaptor.getValue();

        assertEquals(ClientHelper.convertToHydraClient(client, false, null), savedHydraClient);
        assertEquals(ClientHelper.convertToEntity(client, institution), savedEntity);
    }

    @Test
    public void addClientToInstitution_whenSecretRecipientSet_secretEmailedAndClientUpdatedAfterEmailSent() {
        String secret = "a".repeat(ClientsService.SIGNING_SECRET_LENGTH);
        String registryCode = "10101010005";
        ClientSecretExportSettings secretExportSettings = new ClientSecretExportSettings();
        secretExportSettings.setRecipientEmail("email");
        secretExportSettings.setRecipientIdCode(registryCode);
        client.setClientSecretExportSettings(secretExportSettings);

        doReturn(secret).when(secureRandomAlphaNumericStringGenerator).generate(ClientsService.SIGNING_SECRET_LENGTH);
        doNothing().when(oidcService).createClient(any(HydraClient.class));
        doNothing().when(oidcService).setSecret(eq(client.getClientId()), eq(secret));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.addClientToInstitution(registryCode, client);

        verify(clientRepository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(1)).sendSigningSecretByEmail(eq(client), eq(secret));
        verify(oidcService, times(1)).createClient(hydraClientCaptor.capture());
        verify(oidcService, times(1)).setSecret(client.getClientId(), secret);

        HydraClient savedHydraClient = hydraClientCaptor.getValue();
        ee.ria.tara.repository.model.Client savedEntity = clientEntityCaptor.getValue();

        assertEquals(ClientHelper.convertToHydraClient(client, false, null), savedHydraClient);
        assertEquals(ClientHelper.convertToEntity(client, institution), savedEntity);
    }

    @Test
    public void addClientToInstitution_whenClientValidationFails_invalidDataExceptionThrown() {
        String errorMessage = "Oops.";
        doThrow(new InvalidDataException(errorMessage)).when(clientValidator).validateClient(client, institution.getType());
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(institution.getRegistryCode());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientsService.addClientToInstitution(institution.getRegistryCode(), client));

        assertTrue(exception.getMessage().contains(errorMessage));
        verify(clientValidator, times(1)).validateClient(client, institution.getType());
    }

    @Test
    public void updateClient_whenSecretRecipientNotSet_secretNotEmailedAndClientUpdatedOnce() {
        String secret = "a".repeat(ClientsService.SIGNING_SECRET_LENGTH);
        String registryCode = "1";
        String clientId = "10101010005";
        client.setClientSecretExportSettings(null);

        doNothing().when(oidcService).updateClient(eq(clientId), any(HydraClient.class));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.updateClient(registryCode, clientId, client);

        verify(clientRepository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class), eq(secret));
        verify(oidcService, times(1)).updateClient(eq(clientId), hydraClientCaptor.capture());

        HydraClient savedHydraClient = hydraClientCaptor.getValue();
        ee.ria.tara.repository.model.Client savedEntity = clientEntityCaptor.getValue();

        assertEquals(ClientHelper.convertToHydraClient(client, false, null), savedHydraClient);
        assertEquals(ClientHelper.convertToEntity(client, institution), savedEntity);
    }

    @Test
    public void updateClient_whenSecretRecipientSet_newSecretEmailedAndClientUpdated() {
        String secret = "a".repeat(ClientsService.SIGNING_SECRET_LENGTH);
        String registryCode = "10101010005";
        String clientId = client.getClientId();
        ClientSecretExportSettings secretExportSettings = new ClientSecretExportSettings();
        secretExportSettings.setRecipientEmail("email");
        secretExportSettings.setRecipientIdCode("30303039914");
        client.setClientSecretExportSettings(secretExportSettings);

        doReturn(secret).when(secureRandomAlphaNumericStringGenerator).generate(ClientsService.SIGNING_SECRET_LENGTH);
        doNothing().when(oidcService).updateClient(eq(clientId), any(HydraClient.class));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        clientsService.updateClient(registryCode, clientId, client);

        verify(clientRepository, times(1)).save(clientEntityCaptor.capture());
        verify(institutionRepository, times(1)).findInstitutionByRegistryCode(registryCode);
        verify(clientSecretEmailService, times(1)).sendSigningSecretByEmail(any(Client.class), eq(secret));
        verify(oidcService, times(1)).updateClient(eq(clientId), hydraClientCaptor.capture());
        verify(oidcService, times(1)).setSecret(client.getClientId(), secret);

        HydraClient savedHydraClient = hydraClientCaptor.getValue();
        ee.ria.tara.repository.model.Client savedEntity = clientEntityCaptor.getValue();

        assertEquals(ClientHelper.convertToHydraClient(client, false, null), savedHydraClient);
        assertEquals(ClientHelper.convertToEntity(client, institution), savedEntity);
    }

    @Test
    public void updateClient_whenClientServiceFails_exceptionRethrown() {
        String registryCode = "1";
        String clientId = "10101010005";
        String errorMessage = "Oops.";

        doThrow(new ApiException(errorMessage))
                .when(oidcService).updateClient(eq(clientId), any(HydraClient.class));
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(registryCode);

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.updateClient(registryCode, clientId, client));

        assertTrue(exception.getMessage().contains(errorMessage));
        verify(clientSecretEmailService, times(0)).sendSigningSecretByEmail(any(Client.class), anyString());
    }

    @Test
    public void updateClient_whenClientValidationFails_invalidDataExceptionThrown() {
        String errorMessage = "Oops.";
        doThrow(new InvalidDataException(errorMessage)).when(clientValidator).validateClient(client, institution.getType());
        doReturn(institution).when(institutionRepository).findInstitutionByRegistryCode(institution.getRegistryCode());

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> clientsService.updateClient(institution.getRegistryCode(), client.getClientId(), client));

        assertTrue(exception.getMessage().contains(errorMessage));
        verify(clientValidator, times(1)).validateClient(client, institution.getType());
    }

    @Test
    public void deleteClient_clientDeleted() {
        String clientId = "1";
        String registryCode = "1";

        doNothing().when(oidcService).deleteClient(anyString());

        clientsService.deleteClient(registryCode, clientId);

        verify(clientRepository, times(1)).deleteByClientIdAndInstitution_RegistryCode(clientId, registryCode);
    }

    @Test
    public void deleteClient_whenOidcServiceFails_exceptionRethrown() {
        String errorMessage = "Oops.";
        doThrow(new ApiException(errorMessage)).when(oidcService).deleteClient(anyString());

        ApiException exception = assertThrows(ApiException.class,
                () -> clientsService.deleteClient("1", "1"));

        assertTrue(exception.getMessage().contains(errorMessage));
    }

    private Institution institution(InstitutionMetainfo metainfo) {
        Institution institution = new Institution();
        institution.setName(metainfo.getName());
        institution.setRegistryCode(metainfo.getRegistryCode());
        institution.setType(metainfo.getType().getType());
        return institution;
    }

}
