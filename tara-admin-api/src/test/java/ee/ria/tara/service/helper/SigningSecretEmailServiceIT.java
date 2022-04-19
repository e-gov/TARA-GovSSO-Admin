package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.ClientSecretEmailConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.service.ClientSecretEmailService;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SigningSecretEmailServiceIT {
    private static final String TO = "to@example.com";
    private static final String ID_CODE = "12345678910";

    @RegisterExtension
    static SmtpServerExtension smtpServer = new SmtpServerExtension();
    @Autowired
    private ClientSecretEmailConfigurationProvider configurationProvider;
    @Autowired
    private ClientSecretEmailService clientSecretEmailService;


    @Test
    @Disabled("TODO AUT-740 Add test certificate")
    void testSendSigningSecretByEmail() throws Exception {
        Client client = ClientTestHelper.createValidTARAClient();
        ClientSecretExportSettings clientSecretExportSettings = new ClientSecretExportSettings();
        clientSecretExportSettings.setRecipientEmail(TO);
        clientSecretExportSettings.setRecipientIdCode(ID_CODE);
        client.setClientSecretExportSettings(clientSecretExportSettings);
        client.setSecret("secret");

        clientSecretEmailService.sendSigningSecretByEmail(client);

        MimeMessageParser parser = new MimeMessageParser(smtpServer.getMessages()[0]).parse();

        Assertions.assertEquals(configurationProvider.getFromEmail(), parser.getFrom());
        Assertions.assertEquals(TO, parser.getTo().get(0).toString());
        Assertions.assertEquals(configurationProvider.getSubject(), parser.getSubject());
        Assertions.assertEquals(configurationProvider.getAttachmentFileName(), parser.getAttachmentList().get(0).getName());
    }
}
