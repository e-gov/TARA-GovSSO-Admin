package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.service.helper.ClientSecretDataFileGenerator;
import ee.ria.tara.service.helper.ClientSecretEmailHelper;
import ee.ria.tara.service.model.Email;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.openeid.cdoc4j.CDOCBuilder;
import org.openeid.cdoc4j.DataFile;
import org.openeid.cdoc4j.exception.CDOCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;


@Slf4j
@Service
public class ClientSecretEmailService {
    private final JavaMailSender javaMailSender;
    private final CertificateService certificateService;
    private final ClientSecretEmailHelper clientSecretEmailComposer;
    private final ClientSecretDataFileGenerator clientSecretDatafileGenerator;

    @Autowired
    public ClientSecretEmailService(ClientSecretEmailHelper clientSecretEmailHelper, JavaMailSender javaMailSender,
                                    CertificateService certificateService, ClientSecretDataFileGenerator clientSecretDatafileGenerator) {
        this.clientSecretEmailComposer = clientSecretEmailHelper;
        this.javaMailSender = javaMailSender;
        this.certificateService = certificateService;
        this.clientSecretDatafileGenerator = clientSecretDatafileGenerator;
    }

    public void sendSigningSecretByEmail(Client client) throws ApiException {
        Email email = clientSecretEmailComposer.compose(client.getClientSecretExportSettings().getRecipientEmail(), getCdoc(client));

        try {
            log.info(String.format("Sending email with encrypted secret for client: %s (from: '%s' , to: '%s')",
                    client.getClientId(),email.getFrom().getEmail(), email.getTo()));
            javaMailSender.send(createMessage(email));
            log.info(String.format("Email sent successfully to '%s'", email.getTo()));
        } catch (Exception e) {
            log.error(String.format("Failed to send email for user: %s.",
                    client.getClientSecretExportSettings().getRecipientIdCode()), e);
            throw new FatalApiException("Client.secret.email.failed");
        }
    }

    private MimeMessage createMessage(Email email) throws FatalApiException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(email.getFrom().getEmail(), email.getFrom().getName());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getHtml(), true);
            helper.addAttachment(email.getAttachment().getAttachmentFileName(), email.getAttachment().getAttachmentFile());
            return message;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to create MimeMessage.", e);
            throw new FatalApiException("Client.secret.email.failed");
        }
    }

    private byte[] getCdoc(Client client) throws ApiException {
        List<X509Certificate> certificates = certificateService.findAuthenticationCertificates(client.getClientSecretExportSettings().getRecipientIdCode());
        DataFile dataFile;
        try {
            dataFile = clientSecretDatafileGenerator.generate(client);
        } catch (IOException | TemplateException e) {
            log.error("Failed to generate datafile.", e);
            throw new FatalApiException("Client.secret.email.failed");
        }

        return generateCdoc(dataFile, certificates);
    }

    private byte[] generateCdoc(DataFile dataFile, List<X509Certificate> certificates) throws ApiException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (certificates.size() == 0) {
            log.error("No valid recipients found.");
            throw new InvalidDataException("Client.secret.email.invalidIdCode");
        }

        try {
            CDOCBuilder.defaultVersion()
                    .withDataFile(dataFile)
                    .withRecipients(certificates)
                    .buildToOutputStream(baos);
        } catch (CDOCException e) {
            log.error("Failed to generate cdoc file.", e);
            throw new FatalApiException("Client.secret.email.failed");
        }

        return baos.toByteArray();
    }
}
