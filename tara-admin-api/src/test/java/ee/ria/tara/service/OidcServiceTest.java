package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ee.ria.tara.service.helper.ClientTestHelper.validTARAClient;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OidcServiceTest {

    private Client client;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private TaraOidcConfigurationProvider taraOidcConfigurationProvider;
    @InjectMocks
    private OidcService oidcService;

    @BeforeEach
    public void setUp() {
        client = validTARAClient();
    }

    @Test
    public void testGetAllClients() throws ApiException {
        List<HydraClient> hydraClients = IntStream.rangeClosed(1, 15)
                .mapToObj(i -> ClientHelper.convertToHydraClient(validTARAClient(i), false))
                .collect(Collectors.toList());
        doReturn("http://hydra/admin").when(taraOidcConfigurationProvider).getUrl();
        doReturn(5).when(taraOidcConfigurationProvider).getPageSize();
        HttpHeaders headersPage1 = new HttpHeaders();
        headersPage1.set(HttpHeaders.LINK, "</admin/clients?page_size=5&page_token=pageToken2>; rel=\"next\",</admin/clients?page_size=5&page_token=pageToken3>; rel=\"last\"");
        doReturn(ResponseEntity.ok().headers(headersPage1).body(hydraClients.subList(0, 5)))
                .when(restTemplate).exchange(eq("http://hydra/admin/clients?page_size=5&page_token=1"), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));
        HttpHeaders headersPage2 = new HttpHeaders();
        headersPage2.set(HttpHeaders.LINK, "</admin/clients?page_size=2&page_token=pageToken1>; rel=\"first\",</admin/clients?page_size=2&page_token=pageToken3>; rel=\"next\",</admin/clients?page_size=2&page_token=pageToken1>; rel=\"prev\",</admin/clients?page_size=2&page_token=pageToken3>; rel=\"last\"");
        doReturn(ResponseEntity.ok().headers(headersPage2).body(hydraClients.subList(5, 10)))
                .when(restTemplate).exchange(eq("http://hydra/admin/clients?page_size=5&page_token=pageToken2"), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));
        HttpHeaders headersPage3 = new HttpHeaders();
        headersPage3.set(HttpHeaders.LINK, "</admin/clients?page_size=2&page_token=pageToken1>; rel=\"first\",</admin/clients?page_size=2&page_token=pageToken2>; rel=\"prev\"");
        doReturn(ResponseEntity.ok().headers(headersPage3).body(hydraClients.subList(10, 15)))
                .when(restTemplate).exchange(eq("http://hydra/admin/clients?page_size=5&page_token=pageToken3"), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class));

        List<HydraClient> resultList = oidcService.getAllClients(null);

        Assertions.assertIterableEquals(hydraClients, resultList);
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
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);
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
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);
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
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> oidcService.saveClient(hydraClient, "", HttpMethod.POST));

        Assertions.assertTrue(exception.getMessage().contains("Oidc.clientError.400"));
    }

    @Test
    public void testSaveClientWhenHydraRequestFailsClientError409() {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);

        doThrow(new HttpClientErrorException(HttpStatus.valueOf(409)))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> oidcService.saveClient(hydraClient, "", HttpMethod.POST));

        Assertions.assertTrue(exception.getMessage().contains("Oidc.clientError.409"));
    }

    @Test
    public void testSaveClientWhenHydraRequestFailsServerError() {
        HydraClient hydraClient = ClientHelper.convertToHydraClient(validTARAClient(), false);

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
