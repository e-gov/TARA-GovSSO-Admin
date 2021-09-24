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

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "inMemoryAuth"})
public class WebSecurityConfigurationTest {
    private static final int STATUS_200 = HttpStatus.OK.value();
    private static final int STATUS_401 = HttpStatus.UNAUTHORIZED.value();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUnauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/whoami")).andExpect(status().is(STATUS_401));
    }

    @Test
    public void testAuthenticatedRequestReturns200() throws Exception {
        LdapUserDetailsImpl.Essence userDetails = new LdapUserDetailsImpl.Essence();
        userDetails.setUsername("user");
        userDetails.setDn("dn");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails.createUserDetails(), "pass", Collections.emptyList()));

        mockMvc.perform(get("/whoami").with(csrf()))
                .andExpect(status().is(STATUS_200));
    }

    @Test
    public void testLoginWithInvalidCredentialsReturns401() throws Exception {
        LoginRequest json = new LoginRequest();
        json.setUsername("test");
        json.setPassword("invalid");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().is(STATUS_401));
    }


    @Test
    public void testLoginWithValidCredentialsReturnsSuccessResponse() throws Exception {
        LoginRequest json = new LoginRequest();
        json.setUsername("admin");
        json.setPassword("admin");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().is(STATUS_200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @WithMockUser(authorities = {"test-authority"})
    @Test
    public void testLogoutReturns200_forKnownAuthenticationContext() throws Exception {
        mockMvc.perform(post("/logout").with(csrf())).andExpect(status().is(STATUS_200));
    }

    @Test
    public void testLogoutReturns200_forUnknownAuthenticationContext() throws Exception {
        mockMvc.perform(post("/logout").with(csrf())).andExpect(status().is(STATUS_200));
    }

    @Test
    public void testHealth() throws Exception {
        mockMvc.perform(get("/actuator/health").with(csrf())).andExpect(status().is(STATUS_200));
    }
}
