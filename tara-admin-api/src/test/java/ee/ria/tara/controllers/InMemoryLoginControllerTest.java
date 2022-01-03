package ee.ria.tara.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import ee.ria.tara.controllers.handler.ErrorHandler;
import ee.ria.tara.model.LoginRequest;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(MockitoExtension.class)
public class InMemoryLoginControllerTest {
    private MockMvc mvc;
    private LoginRequest loginRequest;
    private JacksonTester<LoginRequest> jsonLoginRequest;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ErrorHandler errorHandler;
    @Mock
    private AuthenticationConfigurationProvider configurationProvider;
    @Mock
    private UserDetailsManager inMemoryUserDetailsManager;
    @Mock
    private UserDetails userDetails;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InMemoryLoginController controller;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(errorHandler, "messageSource", messageSource);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    public void testUserLoginValidLogin() throws Exception {
        doReturn(userDetails).when(inMemoryUserDetailsManager).loadUserByUsername(eq(loginRequest.getUsername()));
        doReturn("authority").when(configurationProvider).getInMemoryAuthority();
        doReturn(loginRequest.getUsername()).when(userDetails).getUsername();
        doReturn(loginRequest.getPassword()).when(userDetails).getPassword();
        doReturn(true).when(passwordEncoder).matches(eq(loginRequest.getPassword()), eq(loginRequest.getPassword()));

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains(loginRequest.getUsername()));
    }

    @Test
    public void testUserLoginInvalidLogin() throws Exception {
        doCallRealMethod().when(errorHandler).handleAuthenticationException(any());
        doReturn(userDetails).when(inMemoryUserDetailsManager).loadUserByUsername(anyString());
        doReturn("").when(userDetails).getPassword();
        doReturn(false).when(passwordEncoder).matches(anyString(), anyString());

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(401, response.getStatus());
    }

    @Test
    public void testUserLoginInvalidUsernameFormat() throws Exception {
        loginRequest.username(null);

        doCallRealMethod().when(errorHandler).handleMethodArgumentNotValidException(any());

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testUserLoginInvalidPasswordFormat() throws Exception {
        loginRequest.password(null);

        doCallRealMethod().when(errorHandler).handleMethodArgumentNotValidException(any());

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }
}
