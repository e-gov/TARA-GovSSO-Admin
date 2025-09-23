package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.ClientSecretDataFileConfigurationProvider;
import ee.ria.tara.model.Client;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.openeid.cdoc4j.DataFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientSecretDataFileGenerator {

    private final ClientSecretDataFileConfigurationProvider configurationProvider;
    private final Configuration freemarkerConfiguration;

    public DataFile generate(Client client, String secret) throws IOException, TemplateException {
        return new DataFile(configurationProvider.getEncryptedFileName(), getSigningSecretBody(client, secret));
    }

    private byte[] getSigningSecretBody(Client client, String secret) throws IOException, TemplateException {
        Template template = freemarkerConfiguration.getTemplate(configurationProvider.getTextFileName());
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, buildTemplateModel(client, secret)).getBytes();
    }

    private Map<String, String> buildTemplateModel(Client client, String secret) {
        Map<String, String> model = new HashMap<>();
        model.put("clientId", client.getClientId());
        model.put("clientSecret", secret);
        return model;
    }
}
