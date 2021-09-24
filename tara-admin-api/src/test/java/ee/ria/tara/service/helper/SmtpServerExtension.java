package ee.ria.tara.service.helper;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.mail.internet.MimeMessage;

public class SmtpServerExtension implements BeforeEachCallback, AfterEachCallback {

    private GreenMail smtpServer = new GreenMail(new ServerSetup(2525, null, ServerSetup.PROTOCOL_SMTP));

    public SmtpServerExtension() {
        smtpServer.setUser("username", "password");
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        smtpServer.start();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        smtpServer.stop();
    }

    public MimeMessage[] getMessages() {
        return smtpServer.getReceivedMessages();
    }

}
