package ee.ria.tara.service.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ria.tara.model.EmailAlert;
import ee.ria.tara.model.LoginAlert;
import ee.ria.tara.model.MessageTemplate;
import ee.ria.tara.repository.model.Alert;

import java.util.List;

public class AlertHelper {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static Alert convertToEntity(ee.ria.tara.model.Alert alert) {
        Alert entity = new Alert();
        Long id = alert.getId() != null ? Long.valueOf(alert.getId()) : null;

        entity.setId(id);
        entity.setTitle(alert.getTitle());
        entity.setStartTime(alert.getStartTime());
        entity.setEndTime(alert.getEndTime());

        LoginAlert loginAlert = alert.getLoginAlert();
        entity.setNotificationTemplates(jsonMapper.valueToTree(loginAlert.getMessageTemplates()));
        entity.setDisplayOnlyForAuthmethods(loginAlert.getAuthMethods());
        entity.setNotifyClientsOnTaraLoginPage(loginAlert.getEnabled());

        EmailAlert emailAlert = alert.getEmailAlert();

        if (emailAlert != null) {
            String emailAlertMessage = emailAlert.getMessageTemplates().stream()
                    .filter(m -> m.getLocale().equals("et"))
                    .map(MessageTemplate::getMessage)
                    .findFirst().orElse(null);
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

        loginAlert.setEnabled(entity.getNotifyClientsOnTaraLoginPage());
        loginAlert.setAuthMethods(entity.getDisplayOnlyForAuthmethods());
        loginAlert.setMessageTemplates(convertNotificationTemplates(entity.getNotificationTemplates()));

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

    private static List<MessageTemplate> convertNotificationTemplates(JsonNode jsonNode) {
        try {
            JavaType targetType = jsonMapper.getTypeFactory()
                    .constructParametricType(List.class, MessageTemplate.class);
            return jsonMapper.treeToValue(jsonNode, targetType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
