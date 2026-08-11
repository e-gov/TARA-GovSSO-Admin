package ee.ria.tara.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static ee.ria.tara.configuration.AuthenticationProfiles.OIDC_AUTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"test", OIDC_AUTH})
class OidcAuthenticationFlowTest {

    private static final String CLIENT_ID = "tara-admin-test";
    private static final String KEY_ID = "test-key";
    private static final RSAKey SIGNING_KEY = signingKey();
    private static final WireMockServer OIDC_PROVIDER = oidcProvider();

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void oidcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.oidc.issuer-uri",
                OidcAuthenticationFlowTest::issuer);
        registry.add("spring.security.oauth2.client.registration.oidc.provider", () -> "oidc");
        registry.add("spring.security.oauth2.client.registration.oidc.client-id", () -> CLIENT_ID);
        registry.add("spring.security.oauth2.client.registration.oidc.client-secret", () -> "test-secret");
        registry.add("spring.security.oauth2.client.registration.oidc.authorization-grant-type",
                () -> "authorization_code");
        registry.add("spring.security.oauth2.client.registration.oidc.redirect-uri",
                () -> "{baseUrl}/login/oauth2/code/oidc");
        registry.add("spring.security.oauth2.client.registration.oidc.scope", () -> "openid");
    }

    @AfterAll
    static void stopProvider() {
        OIDC_PROVIDER.stop();
    }

    @Test
    void authorizationCodeFlowCreatesAuthenticatedSession() throws Exception {
        MvcResult authorization = startAuthorization();
        String location = authorization.getResponse().getRedirectedUrl();
        String state = queryParameter(location, "state");
        String nonce = queryParameter(location, "nonce");
        HttpSession session = authorization.getRequest().getSession(false);

        assertThat(location).startsWith(issuer() + "/authorize?");
        assertThat(state).isNotBlank();
        assertThat(nonce).isNotBlank();
        stubTokenResponse(nonce);

        Assertions.assertNotNull(session);
        mockMvc.perform(get("/login/oauth2/code/oidc")
                        .param("code", "valid-code")
                        .param("state", state)
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/whoami")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.username").value("test-user"));
    }

    @Test
    void providerErrorDoesNotCreateAuthenticatedSession() throws Exception {
        MvcResult authorization = startAuthorization();
        String state = queryParameter(authorization.getResponse().getRedirectedUrl(), "state");
        HttpSession session = authorization.getRequest().getSession(false);

        Assertions.assertNotNull(session);
        mockMvc.perform(get("/login/oauth2/code/oidc")
                        .param("error", "access_denied")
                        .param("state", state)
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

        mockMvc.perform(get("/whoami")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isUnauthorized());
    }

    private MvcResult startAuthorization() throws Exception {
        return mockMvc.perform(get("/oauth2/authorization/oidc"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    private static WireMockServer oidcProvider() {
        WireMockServer server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        String issuer = server.baseUrl();
        server.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/.well-known/openid-configuration"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "issuer": "%s",
                                  "authorization_endpoint": "%s/authorize",
                                  "token_endpoint": "%s/token",
                                  "jwks_uri": "%s/jwks",
                                  "response_types_supported": ["code"],
                                  "subject_types_supported": ["public"],
                                  "id_token_signing_alg_values_supported": ["RS256"],
                                  "scopes_supported": ["openid"],
                                  "token_endpoint_auth_methods_supported": ["client_secret_basic"],
                                  "grant_types_supported": ["authorization_code"]
                                }
                                """.formatted(issuer, issuer, issuer, issuer))));
        server.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/jwks"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(new JWKSet(SIGNING_KEY.toPublicJWK()).toString())));
        return server;
    }

    private static void stubTokenResponse(String nonce) throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer())
                .subject("test-user")
                .audience(CLIENT_ID)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .claim("nonce", nonce)
                .build();
        SignedJWT idToken = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KEY_ID).build(), claims);
        idToken.sign(new RSASSASigner(SIGNING_KEY));

        OIDC_PROVIDER.stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "access_token": "test-access-token",
                                  "token_type": "Bearer",
                                  "expires_in": 300,
                                  "id_token": "%s"
                                }
                                """.formatted(idToken.serialize()))));
    }

    private static String queryParameter(String uri, String name) {
        String value = UriComponentsBuilder.fromUriString(uri).build().getQueryParams().getFirst(name);
        Assertions.assertNotNull(value);
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String issuer() {
        return OIDC_PROVIDER.baseUrl();
    }

    private static RSAKey signingKey() {
        try {
            return new RSAKeyGenerator(2048).keyID(KEY_ID).generate();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create OIDC test signing key", e);
        }
    }
}
