package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import ee.ria.tara.model.Client;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

import static ee.ria.tara.service.helper.ClientTestHelper.createTestClient;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OidcServiceTest {

    private Client client;

    @Captor
    private ArgumentCaptor<ee.ria.tara.repository.model.Client> clientEntityCaptor;

    @Mock
    private ee.ria.tara.repository.model.Client entity;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private OidcService oidcService;

    @BeforeEach
    public void setUp() {
        client = createTestClient();

        ReflectionTestUtils.setField(oidcService, "baseUrl", "http://");
    }

    @Test
    public void testGetAllClients() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(ResponseEntity.ok(List.of(hydraClient)))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));

        List<HydraClient> clientList = oidcService.getAllClients(null);

        Assertions.assertEquals(1, clientList.size());
        Assertions.assertEquals(clientList.get(0), hydraClient);
    }

    @Test
    public void testGetAllClientsWhenHydraRequestFailsServerError() {
        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));

        ApiException exception = assertThrows(ApiException.class,
                () -> oidcService.getAllClients(null));

        Assertions.assertTrue(exception.getMessage()
                .contains("Oidc.serverError"));
    }

    @Test
    public void testGetAllClientsWithFilter() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(ResponseEntity.ok(hydraClient))
                .when(restTemplate).getForEntity(anyString(), eq(HydraClient.class));

        List<HydraClient> clientList = oidcService.getAllClients(client.getClientId());

        Assertions.assertEquals(1, clientList.size());
        Assertions.assertEquals(clientList.get(0), hydraClient);
    }

    @Test
    public void testGetClientsWithFilterWhenHydraRequestFailsClientError() throws ApiException {
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(restTemplate).getForEntity(anyString(), eq(HydraClient.class));

        List<HydraClient> clients = oidcService.getAllClients("clientId");

        Assertions.assertEquals(0, clients.size());
    }

    @Test
    public void testGetClientsWithFilterWhenHydraRequestFailsServerError() {
        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).getForEntity(anyString(), eq(HydraClient.class));

        ApiException exception = assertThrows(ApiException.class,
                () -> oidcService.getAllClients("clientId"));

        Assertions.assertTrue(exception.getMessage()
                .contains("Oidc.serverError"));
    }

    @Test
    public void testSaveClient() throws ApiException {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());

        doReturn(ResponseEntity.ok().build())
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class));

        oidcService.saveClient(hydraClient, "", HttpMethod.POST);

        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    public void testSaveClientWhenHydraRequestFailsClientError400() {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> oidcService.saveClient(hydraClient, "", HttpMethod.POST));

        Assertions.assertTrue(exception.getMessage().contains("Oidc.clientError.400"));
    }

    @Test
    public void testSaveClientWhenHydraRequestFailsClientError409() {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);

        doThrow(new HttpClientErrorException(HttpStatus.valueOf(409)))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> oidcService.saveClient(hydraClient, "", HttpMethod.POST));

        Assertions.assertTrue(exception.getMessage().contains("Oidc.clientError.409"));
    }

    @Test
    public void testSaveClientWhenHydraRequestFailsServerError() {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(createTestClient(), false);

        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class));

        ApiException exception = assertThrows(ApiException.class,
                () -> oidcService.saveClient(hydraClient, "/clients/1", HttpMethod.POST));

        Assertions.assertTrue(exception.getMessage().contains("Oidc.serverError"));
    }

    @Test
    public void testDeleteClient() throws ApiException {
        String clientId = "1";

        doReturn(ResponseEntity.ok().build()).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), nullable(HttpEntity.class), any(ParameterizedTypeReference.class));

        oidcService.deleteClient(clientId);

        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.DELETE), eq(null), any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteClientWhenHydraRequestFailsClientError() {
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));

        RecordDoesNotExistException exception = assertThrows(RecordDoesNotExistException.class,
                () -> oidcService.deleteClient( "1"));

        Assertions.assertTrue(exception.getMessage().contains("Client.notExists"));
    }

    @Test
    public void testDeleteClientWhenHydraRequestFailsServerError() {
        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));

        ApiException exception = assertThrows(ApiException.class,
                () -> oidcService.deleteClient( "1"));

        Assertions.assertTrue(exception.getMessage()
                .contains("Oidc.serverError"));
    }
}
