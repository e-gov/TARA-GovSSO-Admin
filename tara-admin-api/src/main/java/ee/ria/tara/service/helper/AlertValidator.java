package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Alert;
import ee.ria.tara.model.LoginAlert;
import ee.ria.tara.model.MessageTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertValidator {

    private final AdminConfigurationProvider adminConfProvider;

    public void validateAlert(Alert alert) {
        validateTitle(alert.getTitle());
        validateLoginAlert(alert.getLoginAlert());
    }

    private void validateTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new InvalidDataException("Alert.title.missing");
        }
    }

    private void validateLoginAlert(LoginAlert loginAlert) {
        validateMessageTemplates(loginAlert.getMessageTemplates());
        validateAuthenticationMethods(loginAlert.getAuthMethods());
    }

    private void validateMessageTemplates(List<MessageTemplate> messageTemplates) {
        if (CollectionUtils.isEmpty(messageTemplates)) {
            throw new InvalidDataException("Alert.message.missing");
        }
        if (messageTemplates.size() > 1) {
            throw new IllegalStateException("Single message template expected");
        }
        if (StringUtils.isBlank(messageTemplates.get(0).getMessage())) {
            throw new InvalidDataException("Alert.message.missing");
        }
    }

    private void validateAuthenticationMethods(List<String> authMethods) {
        if (adminConfProvider.isSsoMode()) {
            if (!CollectionUtils.isEmpty(authMethods)) {
                throw new IllegalStateException("Authentication methods should not be present in SSO mode");
            }
        } else {
            if (CollectionUtils.isEmpty(authMethods)) {
                throw new InvalidDataException("Alert.authMethod.missing");
            }
        }
    }
}
