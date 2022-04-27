package ee.ria.tara.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import ee.ria.tara.controllers.handler.ErrorHandler;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.service.ClientsService;
import ee.ria.tara.service.InstitutionsService;
import ee.ria.tara.service.helper.ClientTestHelper;
import ee.ria.tara.service.helper.InstitutionTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@ExtendWith(MockitoExtension.class)
public class InstitutionsControllerTest {
    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private JacksonTester<Client> jsonClient;
    private JacksonTester<Institution> jsonInstitution;
    private Client client;
    private Institution institution;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ErrorHandler errorHandler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ClientsService clientsService;
    @Mock
    private InstitutionsService institutionsService;
    @Mock
    private AdminConfigurationProvider adminConfigurationProvider;
    @InjectMocks
    private InstitutionsController controller;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(errorHandler, "messageSource", messageSource);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        client = ClientTestHelper.validTARAClient();
        institution = InstitutionTestHelper.createTestInstitution();

        JacksonTester.initFields(this, objectMapper);

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    public void testGetAllInstitutionClients() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();

        doReturn(List.of()).when(clientsService).getAllInstitutionsClients(registryCode);

        MockHttpServletResponse response = mvc.perform(
                get(String.format("/institutions/%s/clients", registryCode))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(clientsService, times(1)).getAllInstitutionsClients(registryCode);
    }

    @Test
    public void testAddClient() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();

        doNothing().when(clientsService).addClientToInstitution(eq(registryCode), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                post(String.format("/institutions/%s/clients", registryCode))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(clientsService, times(1)).addClientToInstitution(eq(registryCode), any(Client.class));
    }

    @Test
    public void testUpdateClient() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doNothing().when(clientsService).updateClient(anyString(), anyString(), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(clientsService, times(1)).updateClient(eq(registryCode), eq(clientId), any(Client.class));
    }

    @Test
    public void testDeleteClient() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doNothing().when(clientsService).deleteClient(registryCode, clientId);

        MockHttpServletResponse response = mvc.perform(
                delete(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(clientsService, times(1)).deleteClient(registryCode, clientId);
    }

    @Test
    public void testGetAllInstitutionClientsWhenFatalApiExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();

        doCallRealMethod().when(errorHandler).handleFatalApiException(any());
        doThrow(new FatalApiException("")).when(clientsService).getAllInstitutionsClients(registryCode);

        MockHttpServletResponse response = mvc.perform(
                get(String.format("/institutions/%s/clients", registryCode))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetAllInstitutionClientsWhenRandomExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();

        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new RuntimeException("")).when(clientsService).getAllInstitutionsClients(registryCode);

        MockHttpServletResponse response = mvc.perform(
                get(String.format("/institutions/%s/clients", registryCode))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testAddClientWhenApiExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();

        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new ApiException("Server.error")).when(clientsService).addClientToInstitution(eq(registryCode), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                post(String.format("/institutions/%s/clients", registryCode))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testAddClientWhenRandomExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();

        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new RuntimeException("")).when(clientsService).addClientToInstitution(eq(registryCode), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                post(String.format("/institutions/%s/clients", registryCode))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateClientWhenRecordDoesNotExistExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new RecordDoesNotExistException("")).when(clientsService).updateClient(anyString(), anyString(), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testUpdateClientWhenInvalidDataExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new InvalidDataException("")).when(clientsService).updateClient(anyString(), anyString(), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testUpdateClientWhenApiExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new ApiException("")).when(clientsService).updateClient(anyString(), anyString(), any(Client.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteClientWhenApiExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new ApiException("")).when(clientsService).deleteClient(registryCode, clientId);

        MockHttpServletResponse response = mvc.perform(
                delete(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteClientWhenRecordDoesNotExistExceptionThrown() throws Exception {
        String registryCode = client.getInstitutionMetainfo().getRegistryCode();
        String clientId = client.getClientId();

        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new RecordDoesNotExistException("")).when(clientsService).deleteClient(registryCode, clientId);

        MockHttpServletResponse response = mvc.perform(
                delete(String.format("/institutions/%s/clients/%s", registryCode, clientId))
                        .content(jsonClient.write(client).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testGetInstitutionMetainfo() throws Exception {
        List<InstitutionMetainfo> metainfoList = List.of(new InstitutionMetainfo());

        doReturn(metainfoList).when(institutionsService).getInstitutionsMetainfo();

        MockHttpServletResponse response = mvc.perform(
                get("/institutions/metainfo")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(metainfoList), response.getContentAsString());
    }

    @Test
    public void testGetInstitutions() throws Exception {
        List<Institution> institutionList = List.of(institution);

        doReturn(institutionList).when(institutionsService).getInstitutions(nullable(String.class));

        MockHttpServletResponse response = mvc.perform(
                get("/institutions")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(institutionList), response.getContentAsString());
    }

    @Test
    public void testGetInstitutionsWithFilter() throws Exception {
        List<Institution> institutionList = List.of(institution);

        doReturn(institutionList).when(institutionsService).getInstitutions(anyString());

        MockHttpServletResponse response = mvc.perform(
                get("/institutions")
                        .queryParam("filter_by", "filterBy")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(objectMapper.writeValueAsString(institutionList), response.getContentAsString());
    }

    @Test
    public void testAddInstitution() throws Exception {
        doNothing().when(institutionsService).addInstitution(any());

        MockHttpServletResponse response = mvc.perform(
                post("/institutions")
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(institutionsService, times(1)).addInstitution(any());
    }

    @Test
    public void testAddInstitutionWhenApiExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new ApiException("")).when(institutionsService).addInstitution(any(Institution.class));

        MockHttpServletResponse response = mvc.perform(
                post(String.format("/institutions"))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testAddInstitutionWhenInvalidDataExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new InvalidDataException("")).when(institutionsService).addInstitution(any(Institution.class));

        MockHttpServletResponse response = mvc.perform(
                post("/institutions")
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testUpdateInstitution() throws Exception {
        doNothing().when(institutionsService).updateInstitution(any(Institution.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(institutionsService, times(1)).updateInstitution(any(Institution.class));
    }

    @Test
    public void testUpdateInstitutionWhenApiExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new ApiException("")).when(institutionsService).updateInstitution(any(Institution.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateInstitutionWhenInvalidDataExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new InvalidDataException("")).when(institutionsService).updateInstitution(any(Institution.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testUpdateInstitutionWhenRecordDoesNotExistExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new RecordDoesNotExistException("")).when(institutionsService).updateInstitution(any(Institution.class));

        MockHttpServletResponse response = mvc.perform(
                put(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testDeleteInstitution() throws Exception {
        doNothing().when(institutionsService).deleteInstitution(institution.getRegistryCode());

        MockHttpServletResponse response = mvc.perform(
                delete(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        verify(institutionsService, times(1)).deleteInstitution(institution.getRegistryCode());
    }

    @Test
    public void testDeleteInstitutionWhenApiExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleException(any());
        doThrow(new ApiException("")).when(institutionsService).deleteInstitution(anyString());

        MockHttpServletResponse response = mvc.perform(
                delete(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteInstitutionWhenRecordDoesNotExistExceptionThrow() throws Exception {
        doCallRealMethod().when(errorHandler).handleUserErrors(any());
        doThrow(new RecordDoesNotExistException("")).when(institutionsService).deleteInstitution(anyString());

        MockHttpServletResponse response = mvc.perform(
                delete(String.format("/institutions/%s", institution.getRegistryCode()))
                        .content(jsonInstitution.write(institution).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }
}
