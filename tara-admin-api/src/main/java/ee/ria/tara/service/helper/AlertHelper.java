package ee.ria.tara.service.helper;

import ee.ria.tara.model.EmailAlert;
import ee.ria.tara.model.LoginAlert;
import ee.ria.tara.model.MessageTemplate;
import ee.ria.tara.repository.model.Alert;

public class AlertHelper {
    public static Alert convertToEntity(ee.ria.tara.model.Alert alert) {
        Alert entity = new Alert();
        Long id = alert.getId() != null ? Long.valueOf(alert.getId()) : null;

        entity.setId(id);
        entity.setTitle(alert.getTitle());
        entity.setStartTime(alert.getStartTime());
        entity.setEndTime(alert.getEndTime());

        LoginAlert loginAlert = alert.getLoginAlert();
        String loginAlertMessage = loginAlert.getMessageTemplates().stream()
                .filter(m -> m.getLocale().equals("et"))
                .map(MessageTemplate::getMessage)
                .findFirst().orElse(null);
        entity.setNotificationText(loginAlertMessage);
        entity.setDisplayOnlyForAuthmethods(loginAlert.getAuthMethods());
        entity.setNotifyClientsOnTaraLoginPage(loginAlert.getEnabled());

        EmailAlert emailAlert = alert.getEmailAlert();

        if (emailAlert != null) {
            String emailAlertMessage = emailAlert.getMessageTemplates().stream()
                    .filter(m -> m.getLocale().equals("et"))
                    .map(MessageTemplate::getMessage)
                    .findFirst().orElse(null);
            entity.setNotificationText(loginAlertMessage);
            entity.setEmailTemplate(emailAlertMessage);
            entity.setSendAt(emailAlert.getSendAt());
            entity.setNotifyClientsByEmail(emailAlert.getEnabled());
        }

        if (alert.getCreatedAt() != null) {
            entity.setCreatedAt(alert.getCreatedAt());
        }

        if (alert.getUpdatedAt() != null) {
            entity.setUpdatedAt(alert.getUpdatedAt());
        }

        return entity;
    }

    public static ee.ria.tara.model.Alert convertFromEntity(Alert entity) {
        ee.ria.tara.model.Alert alert = new ee.ria.tara.model.Alert();
        LoginAlert loginAlert = new LoginAlert();
        EmailAlert emailAlert = new EmailAlert();

        MessageTemplate loginMessage = new MessageTemplate();
        loginMessage.setMessage(entity.getNotificationText());
        loginMessage.setLocale("et");
        loginAlert.setEnabled(entity.getNotifyClientsOnTaraLoginPage());
        loginAlert.setAuthMethods(entity.getDisplayOnlyForAuthmethods());
        loginAlert.addMessageTemplatesItem(loginMessage);

        MessageTemplate emailMessage = new MessageTemplate();
        emailMessage.setMessage(entity.getEmailTemplate());
        emailMessage.setLocale("et");
        emailAlert.setSendAt(entity.getSendAt());
        emailAlert.setEnabled(entity.getNotifyClientsByEmail());
        emailAlert.addMessageTemplatesItem(emailMessage);

        alert.setId(entity.getId().toString());
        alert.setTitle(entity.getTitle());
        alert.setEmailAlert(emailAlert);
        alert.setLoginAlert(loginAlert);

        alert.setStartTime(entity.getStartTime());
        alert.setEndTime(entity.getEndTime());
        alert.setCreatedAt(entity.getCreatedAt());
        alert.setUpdatedAt(entity.getUpdatedAt());

        return alert;
    }
}
