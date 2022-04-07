package ee.ria.tara.controllers;

import java.util.HashMap;
import java.util.Map;

public class ControllerTestData {

    public static final Map<String, Object> EXPECTED_RESPONSE_HEADERS = new HashMap<>() {{
        put("X-XSS-Protection", "0");
        put("X-Content-Type-Options", "nosniff");
        put("X-Frame-Options", "DENY");
        put("Content-Security-Policy-Report-Only", "connect-src 'self'; default-src 'none'; font-src 'self'; img-src 'self'; script-src 'self'; style-src 'self'; base-uri 'none'; frame-ancestors 'none'; block-all-mixed-content");
        put("Pragma", "no-cache");
        put("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        put("Expires", "0");
        // TODO: Use HTTPS for API tests. Given header only returned over https.
        // put("Strict-Transport-Security", "max-age=16070400 ; includeSubDomains");
    }};
}
