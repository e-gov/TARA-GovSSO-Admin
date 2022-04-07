package ee.ria.tara.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ria.tara.model.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ee.ria.tara.controllers.ControllerTestData.EXPECTED_RESPONSE_HEADERS;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "inMemoryAuth"})
public class WebSecurityConfigurationTest {
    private static final int STATUS_200 = HttpStatus.OK.value();
    private static final int STATUS_401 = HttpStatus.UNAUTHORIZED.value();
    private static final int STATUS_403 = HttpStatus.FORBIDDEN.value();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUnauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/whoami").with(csrf()))
                .andExpect(status().is(STATUS_401))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void testUnauthenticatedRequestWithoutCsrfReturns401() throws Exception {
        mockMvc.perform(get("/whoami"))
                .andExpect(status().is(STATUS_401))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void testAuthenticatedRequestReturns200() throws Exception {
        LdapUserDetailsImpl.Essence userDetails = new LdapUserDetailsImpl.Essence();
        userDetails.setUsername("user");
        userDetails.setDn("dn");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails.createUserDetails(), "pass", Collections.emptyList()));

        mockMvc.perform(get("/whoami").with(csrf()))
                .andExpect(status().is(STATUS_200))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void testLoginWithInvalidCredentialsReturns401() throws Exception {
        LoginRequest json = new LoginRequest();
        json.setUsername("test");
        json.setPassword("invalid");

        mockMvc.perform(post("/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().is(STATUS_401))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void testLoginWithoutCsrfReturns403() throws Exception {
        LoginRequest json = new LoginRequest();
        json.setUsername("test");
        json.setPassword("invalid");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().is(STATUS_403))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void testLoginWithValidCredentialsReturnsSuccessResponse() throws Exception {
        LoginRequest json = new LoginRequest();
        json.setUsername("admin");
        json.setPassword("admin");

        mockMvc.perform(post("/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().is(STATUS_200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @WithMockUser(authorities = {"test-authority"})
    @Test
    public void testLogoutReturns200_forKnownAuthenticationContext() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is(STATUS_200))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void testLogoutReturns200_forUnknownAuthenticationContext() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is(STATUS_200))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    @Test
    public void actuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health").with(csrf()))
                .andExpect(status().is(STATUS_200))
                .andExpectAll(expectedHeaderMatchers(header()));
    }

    private ResultMatcher[] expectedHeaderMatchers(HeaderResultMatchers headerResultMatchers) {
        List<ResultMatcher> resultMatchers = new ArrayList<>();
        EXPECTED_RESPONSE_HEADERS.forEach((k, v) ->
                resultMatchers.add(headerResultMatchers.string(k, (String) v))
        );
        return resultMatchers.toArray(new ResultMatcher[0]);
    }
}
