package ee.ria.tara.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.ria.tara.configuration.providers.AuthenticationConfigurationProvider;
import ee.ria.tara.controllers.handler.ErrorHandler;
import ee.ria.tara.model.Alert;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.LoginRequest;
import ee.ria.tara.model.LoginResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(MockitoExtension.class)
public class LdapLoginControllerTest {
    private MockMvc mvc;
    private LoginRequest loginRequest;
    private JacksonTester<LoginRequest> jsonLoginRequest;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ErrorHandler errorHandler;
    @Mock
    private AuthenticationProvider activeDirectoryLdapAuthenticationProvider;
    @Mock
    private AuthenticationConfigurationProvider configurationProvider;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private LdapLoginController controller;


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
    public void testLoginUser() throws Exception {
        String authorityName = "authority";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityName);

        doReturn(authentication).when(activeDirectoryLdapAuthenticationProvider).authenticate(any(Authentication.class));
        doReturn(authorityName).when(configurationProvider).getLdapAuthority();
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals( 200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains(loginRequest.getUsername()));
    }

    @Test
    public void testLoginUserInvalidUser() throws Exception {
        String authorityName = "required-authority";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityName);

        doCallRealMethod().when(errorHandler).handleAuthenticationException(any());
        doReturn(authentication).when(activeDirectoryLdapAuthenticationProvider).authenticate(any(Authentication.class));
        doReturn("authority").when(configurationProvider).getLdapAuthority();
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals( 401, response.getStatus());
    }

    @Test
    public void testLoginUserBadCredentials() throws Exception {
        doCallRealMethod().when(errorHandler).handleAuthenticationException(any());
        doThrow(new BadCredentialsException(""))
                .when(activeDirectoryLdapAuthenticationProvider).authenticate(any(Authentication.class));

        MockHttpServletResponse response = mvc.perform(
                post("/login")
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals( 401, response.getStatus());
    }
}
