package ee.ria.tara.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.handler.ErrorHandler;
import ee.ria.tara.model.Client;
import ee.ria.tara.service.ClientsService;
import ee.ria.tara.service.helper.ClientTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class ClientsControllerTest {
    private MockMvc mvc;
    private JacksonTester<Client> jsonClient;
    private Client client;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ErrorHandler errorHandler;
    @Mock
    private ClientsService service;

    @InjectMocks
    private ClientsController controller;


    @BeforeEach
    public void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest(), new MockHttpServletResponse()));
        ReflectionTestUtils.setField(errorHandler, "messageSource", messageSource);
        JacksonTester.initFields(this, new ObjectMapper());

        client = ClientTestHelper.validTARAClient();

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    public void testGetAllClients() throws Exception {
        List<Client> clientList = List.of(client);

        doReturn(clientList).when(service).getAllClients(nullable(String.class));

        MockHttpServletResponse response = mvc.perform(
                get("/clients")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains(client.getClientId()));
        verify(service, times(1)).getAllClients(null);
    }

    @Test
    public void testGetAllClientsWhenExceptionThrown() throws Exception {
        doCallRealMethod().when(errorHandler).handleFatalApiException(any());
        doThrow(new FatalApiException("")).when(service).getAllClients(nullable(String.class));

        MockHttpServletResponse response = mvc.perform(
                get("/clients")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
        verify(service, times(1)).getAllClients(null);
    }

    @Test
    public void testNonExistingEndpointReturns404() throws Exception {
        doCallRealMethod().when(errorHandler).handleNoResourceException(any());
        doThrow(new NoResourceFoundException(HttpMethod.GET, "")).when(service).getAllClients(nullable(String.class));

        MockHttpServletResponse response = mvc.perform(
                get("/non-existing-endpoint")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }
}
