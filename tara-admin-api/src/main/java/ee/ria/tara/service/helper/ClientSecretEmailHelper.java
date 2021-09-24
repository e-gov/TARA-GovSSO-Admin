package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.ClientSecretEmailConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.service.model.Email;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

@Slf4j
@Service
public class ClientSecretEmailHelper {

    private final ClientSecretEmailConfigurationProvider emailConfigurationProvider;
    private Configuration freemarkerConfiguration;

    @Autowired
    public ClientSecretEmailHelper(Configuration freemarkerConfiguration, ClientSecretEmailConfigurationProvider emailConfigurationProvider) {
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.emailConfigurationProvider = emailConfigurationProvider;
    }

    public Email compose(String toAddress, byte[] signingSecretCdoc) throws FatalApiException {
        return Email.builder()
                .from(emailConfigurationProvider.getFromEmail(), emailConfigurationProvider.getFromName())
                .to(toAddress)
                .subject(emailConfigurationProvider.getSubject())
                .html(getHtml())
                .attachment(emailConfigurationProvider.getAttachmentFileName(), signingSecretCdoc)
                .build();
    }

    private String getHtml() throws FatalApiException {
        try {
            Template template = freemarkerConfiguration.getTemplate(emailConfigurationProvider.getTemplateName());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, null);
        } catch (IOException | TemplateException e) {
            log.error("Failed to process html template: " + emailConfigurationProvider.getTemplateName(), e);
            throw new FatalApiException(e);
        }
    }

}
