package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.ClientSecretEmailConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.service.model.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class ClientSecretEmailHelperTest {
    @Autowired
    private ClientSecretEmailConfigurationProvider configurationProvider;

    @Autowired
    private ClientSecretEmailHelper clientSecretEmailHelper;

    @Test
    void testComposesEmail() throws ApiException {
        String to = "to-test@example.com";
        String fileContent = "cdoc";
        byte[] cdoc = fileContent.getBytes();

        Email email = clientSecretEmailHelper.compose(to, cdoc);

        assertEquals(configurationProvider.getFromEmail(), email.getFrom().getEmail());
        assertEquals(configurationProvider.getFromName(), email.getFrom().getName());
        assertEquals(to, email.getTo());
        assertEquals(configurationProvider.getSubject(), email.getSubject());
        assertEquals(getExpectedHtml(), email.getHtml());
        assertEquals(configurationProvider.getAttachmentFileName(), email.getAttachment().getAttachmentFileName());
        assertEquals(fileContent, new String(email.getAttachment().getAttachmentFile().getByteArray()));
    }

    private String getExpectedHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<p>Lugupeetud partner</p>\n" +
                "<p>Registreerisime Teie teenuse autentimisteenuses. Manusest leiate krüpteeritud faili teenuse kasutamiseks vajalike parameetritega.</p>\n" +
                "<p>Dekrüpteerimiseks on Teil vaja DigiDoc4 Klient kõige viimast versiooni. Faili saab dekrüpteerida liitumistaotluses nimetatud kontaktisik.</p>\n" +
                "<p>Juhendid liidestumise tehniliseks teostamiseks leiate: <a href=\"https://e-gov.github.io/TARA-Doku/\">https://e-gov.github.io/TARA-Doku/</a>.</p>\n" +
                "<p>Lisaküsimuste või murede korral võtke meiega julgelt ühendust.</p>\n" +
                "</body>\n" +
                "</html>\n";
    }

}
