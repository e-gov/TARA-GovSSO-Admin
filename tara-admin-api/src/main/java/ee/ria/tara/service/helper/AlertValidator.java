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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (hasDuplicateLocales(messageTemplates)) {
            throw new InvalidDataException("Multiple message templates for single locale");
        }
        if (messageTemplates.stream()
                .map(MessageTemplate::getMessage)
                .anyMatch(StringUtils::isBlank)) {
            throw new InvalidDataException("Alert.message.missing");
        }
    }

    private boolean hasDuplicateLocales(List<MessageTemplate> messageTemplates) {
        Set<String> foundLocales = new HashSet<>();
        for (MessageTemplate messageTemplate : messageTemplates) {
            String locale = messageTemplate.getLocale();
            if (foundLocales.contains(locale)) {
                return true;
            }
            foundLocales.add(locale);
        }
        return false;
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
