package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.ClientSecretDataFileConfigurationProvider;
import ee.ria.tara.model.Client;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.openeid.cdoc4j.DataFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientSecretDataFileGenerator {
    private final ClientSecretDataFileConfigurationProvider configurationProvider;

    private Configuration freemarkerConfiguration;

    @Autowired
    ClientSecretDataFileGenerator(Configuration freemarkerConfiguration, ClientSecretDataFileConfigurationProvider configurationProvider) {
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.configurationProvider = configurationProvider;
    }

    public DataFile generate(Client client) throws IOException, TemplateException {
        return new DataFile(configurationProvider.getEncryptedFileName(), getSigningSecretBody(client));
    }

    private byte[] getSigningSecretBody(Client client) throws IOException, TemplateException {
        Template template = freemarkerConfiguration.getTemplate(configurationProvider.getTextFileName());
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, buildTemplateModel(client)).getBytes();
    }

    private Map<String, String> buildTemplateModel(Client client) {
        Map<String, String> model = new HashMap<>();
        model.put("clientId", client.getClientId());
        model.put("clientSecret", client.getSecret());
        return model;
    }
}
