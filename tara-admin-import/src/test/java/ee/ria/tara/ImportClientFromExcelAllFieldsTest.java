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
@ContextConfiguration(initializers = {ImportClientFromExcelAllFieldsTest.MockOidcServiceInitializer.class})
@TestPropertySource(
        properties = {
                "spring.profiles.active=importFromFile",
                "file-import.file-name=src/test/resources/import_files/client-with-all-fields.xlsx",
                "tara-oidc.url=http://localhost:3789",
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
public class ImportClientFromExcelAllFieldsTest {

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
        wireMockServer.verify(getRequestedFor(urlEqualTo("/admin/clients/mock_client_id")));
        wireMockServer.verify(putRequestedFor(urlEqualTo("/admin/clients/mock_client_id"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("{" +
                        "\"client_id\":\"mock_client_id\"," +
                        "\"client_name\":\"Eestikeelne nimi\"," +
                        "\"client_secret\":\"3d91b58504a6cc3a159005ee7b16c7ae503ca6ac2a6a3c893837083c236b864a\"," +
                        "\"access_token_strategy\": null," +
                        "\"audience\": [ ]," +
                        "\"authorization_code_grant_access_token_lifespan\" : null,\n" +
                        "\"refresh_token_grant_access_token_lifespan\" : null,\n" +
                        "\"grant_types\":[\"authorization_code\"]," +
                        "\"redirect_uris\":[\"https://oidc-client-mock:8451/oauth/response\"]," +
                        "\"post_logout_redirect_uris\": [ ]," +
                        "\"response_types\":[\"code\"]," +
                        "\"scope\":\"openid eidas eidasonly eidas:country:* idcard mid smartid email phone\"," +
                        "\"subject_type\":\"public\"," +
                        "\"token_endpoint_auth_method\":\"client_secret_basic\"," +
                        "\"backchannel_logout_uri\" : null,\n" +
                        "\"backchannel_logout_session_required\" : true,\n" +
                        "\"metadata\":{" +
                            "\"display_user_consent\":null," +
                            "\"skip_user_consent_client_ids\" : [ ],\n" +
                            "\"oidc_client\":{" +
                                "\"name\":\"Eestikeelne nimi\"," +
                                "\"name_translations\":{\"et\":\"Eestikeelne nimi\",\"en\":\"English name\",\"ru\":\"Русское имя\"}," +
                                "\"short_name\":\"nimi_et\"," +
                                "\"short_name_translations\":{\"et\":\"nimi_et\",\"en\":\"name_en\",\"ru\":\"имя_ru\"}," +
                                "\"legacy_return_url\":\"https://tagasi.ee\"," +
                                "\"eidas_requester_id\":\"urn:uuid:576daff2-daa7-11ec-9aca-9fe50333be49\"," +
                                "\"institution\":{\"registry_code\":\"12345678\",\"sector\":\"public\"}," +
                                "\"mid_settings\":null," +
                                "\"smartid_settings\":null," +
                                "\"logo\" : null\n" +
                            "}," +
                        "   \"paasuke_parameters\" : null" +
                        "}," +
                        "\"created_at\":null," +
                        "\"updated_at\":null}"))
        );
    }

    public static class MockOidcServiceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(3789));
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
                    WireMock.post("/admin/clients")
                            .willReturn(WireMock.aResponse())
            );
            wireMockServer.stubFor(
                    WireMock.get("/admin/clients/mock_client_id")
                            .willReturn(WireMock.aResponse())
            );
            wireMockServer.stubFor(
                    WireMock.put("/admin/clients/mock_client_id")
                            .willReturn(WireMock.aResponse())
            );
        }
    }
}
