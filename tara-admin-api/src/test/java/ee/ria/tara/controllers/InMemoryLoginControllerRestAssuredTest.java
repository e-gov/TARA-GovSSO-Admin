package ee.ria.tara.controllers;

import ee.ria.tara.model.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static ee.ria.tara.configuration.CookieConfiguration.COOKIE_NAME_SESSION;
import static ee.ria.tara.configuration.CookieConfiguration.COOKIE_NAME_XSRF_TOKEN;
import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({BuildProperties.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InMemoryLoginControllerRestAssuredTest {

    private static final String MOCK_CSRF_TOKEN = "d1341bfc-052d-448b-90f0-d7a7a9e4b842";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void beforeEachTest() {
        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectHeaders(ControllerTestData.EXPECTED_RESPONSE_HEADERS)
                .build();
        RestAssured.port = port;
    }

    @Test
    void loginWithValidCredentials_ReturnsSuccessResponse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin");

        ExtractableResponse<Response> response = given()
                .cookie(COOKIE_NAME_XSRF_TOKEN, MOCK_CSRF_TOKEN)
                .queryParam("_csrf", MOCK_CSRF_TOKEN)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(200)
                .extract();

        Assertions.assertEquals(1, response.cookies().size());
        assertSessionCookiePresent(response);
    }

    @Test
    void loginWithInvalidCredentials_Returns401() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("invalid");

        ExtractableResponse<Response> response = given()
                .cookie(COOKIE_NAME_XSRF_TOKEN, MOCK_CSRF_TOKEN)
                .queryParam("_csrf", MOCK_CSRF_TOKEN)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(401)
                .extract();

        Assertions.assertEquals(0, response.cookies().size());
    }

    @Test
    void loginWithInvalidCredentialsWithoutCsrf_Returns401AndCsrfCookieSet() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("invalid");

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(401)
                .extract();

        Assertions.assertEquals(1, response.cookies().size());
        assertCsrfCookiePresent(response);
    }

    private void assertCsrfCookiePresent(ExtractableResponse<Response> response) {
        Cookie cookie = ((RestAssuredResponseImpl) response).getDetailedCookies().get(COOKIE_NAME_XSRF_TOKEN);
        Assertions.assertNotNull(cookie);
        Assertions.assertEquals(-1, cookie.getMaxAge());
        Assertions.assertNull(cookie.getExpiryDate());
        Assertions.assertEquals("/", cookie.getPath());
        Assertions.assertEquals("Strict", cookie.getSameSite());
        Assertions.assertFalse(cookie.isHttpOnly());
        Assertions.assertTrue(cookie.isSecured());
        Assertions.assertNull(cookie.getDomain());
        Assertions.assertNull(cookie.getComment());
    }

    private void assertSessionCookiePresent(ExtractableResponse<Response> response) {
        Cookie cookie = ((RestAssuredResponseImpl) response).getDetailedCookies().get(COOKIE_NAME_SESSION);
        Assertions.assertNotNull(cookie);
        Assertions.assertEquals(-1, cookie.getMaxAge());
        Assertions.assertNull(cookie.getExpiryDate());
        Assertions.assertEquals("/", cookie.getPath());
        Assertions.assertEquals("Strict", cookie.getSameSite());
        Assertions.assertTrue(cookie.isHttpOnly());
        /*
         * TODO: Use HTTPS
         * Since tests are not over HTTPS and session cookie is configured and created by Spring Security then
         * session cookie secured tag is always missing.
         * Assertions.assertTrue(cookie.isSecured());
         */
        Assertions.assertNull(cookie.getDomain());
        Assertions.assertNull(cookie.getComment());
    }
}
