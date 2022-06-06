package ee.ria.tara;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.model.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = ClientUtilsApplication.class, webEnvironment = NONE)
@ContextConfiguration(initializers = {ImportClientFromExcelMandatoryFieldsOnlyTest.MockOidcServiceInitializer.class})
@TestPropertySource(
        properties = {
                "spring.profiles.active=importFromFile",
                "file-import.file-name=src/test/resources/import_files/client-with-min-fields.xlsx",
                "tara-oidc.url=http://localhost:3799",
                "auth.tls-truststore-path=file:../tara-admin-api/src/main/resources/tls-truststore.p12",
                "auth.tls-truststore-password=changeit",
                "spring.datasource.continue-on-error=false",
                "spring.datasource.platform=h2",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.liquibase.enabled=true",
                "spring.liquibase.parameters.admin-service-user-name=h2"
        })
public class ImportClientFromExcelMandatoryFieldsOnlyTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    public void importClient() {
        List<Client> clients = clientRepository.findAll();
        Assertions.assertEquals(1, clients.size());
        Assertions.assertEquals("mock_client_id", clients.get(0).getClientId());
        Assertions.assertEquals("Example Institution", clients.get(0).getInstitution().getName());
        wireMockServer.verify(getRequestedFor(urlEqualTo("/clients/mock_client_id")));
        wireMockServer.verify(putRequestedFor(urlEqualTo("/clients/mock_client_id"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("{" +
                        "\"client_id\":\"mock_client_id\"," +
                        "\"client_name\":null," +
                        "\"client_secret\":\"2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b\"," +
                        "\"grant_types\":[\"authorization_code\"]," +
                        "\"redirect_uris\":[\"https://oidc-client-mock:8451/oauth/response\"]," +
                        "\"post_logout_redirect_uris\": null," +
                        "\"response_types\":[\"code\"]," +
                        "\"scope\":\"openid eidas eidasonly eidas:country:* idcard mid smartid email phone\"," +
                        "\"subject_type\":\"public\"," +
                        "\"token_endpoint_auth_method\":\"client_secret_basic\"," +
                        "\"backchannel_logout_uri\" : null,\n" +
                        "\"backchannel_logout_session_required\" : true,\n" +
                        "\"metadata\":{" +
                            "\"display_user_consent\":null," +
                            "\"skip_user_consent_client_ids\" : null,\n" +
                            "\"oidc_client\":{" +
                                "\"name\":null," +
                                "\"name_translations\":{\"et\":null,\"en\":null,\"ru\":null}," +
                                "\"short_name\":null,\"short_name_translations\":{\"et\":null,\"en\":null,\"ru\":null}," +
                                "\"legacy_return_url\":null," +
                                "\"eidas_requester_id\":\"urn:uuid:59999382-daa6-11ec-b3dd-8396fd413a71\"," +
                                "\"institution\":{\"registry_code\":\"12345678\",\"sector\":\"public\"}," +
                                "\"mid_settings\":null," +
                                "\"smartid_settings\":null," +
                                "\"logo\" : null\n" +
                            "}" +
                        "}," +
                        "\"created_at\":null," +
                        "\"updated_at\":null}"))
        );
    }

    public static class MockOidcServiceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(3799));
            wireMockServer.start();

            configurableApplicationContext
                    .getBeanFactory()
                    .registerSingleton("wireMockServer", wireMockServer);

            configurableApplicationContext.addApplicationListener(applicationEvent -> {
                if (applicationEvent instanceof ContextClosedEvent) {
                    wireMockServer.stop();
                }
            });

            wireMockServer.stubFor(
                    WireMock.post("/clients")
                            .willReturn(WireMock.aResponse())
            );
            wireMockServer.stubFor(
                    WireMock.get("/clients/mock_client_id")
                            .willReturn(WireMock.aResponse())
            );
            wireMockServer.stubFor(
                    WireMock.put("/clients/mock_client_id")
                            .willReturn(WireMock.aResponse())
            );
        }
    }
}
