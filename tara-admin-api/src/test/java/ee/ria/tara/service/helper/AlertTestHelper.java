package ee.ria.tara.service.helper;

import ee.ria.tara.model.Alert;
import ee.ria.tara.model.EmailAlert;
import ee.ria.tara.model.LoginAlert;
import ee.ria.tara.model.MessageTemplate;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;

public class AlertTestHelper {

    public static Alert validSSOAlert() {
        Alert alert = new Alert();
        alert.setId("1");
        alert.setTitle("SSO alert title");
        alert.setStartTime(OffsetDateTime.now());
        alert.setEndTime(OffsetDateTime.now());

        LoginAlert loginAlert = new LoginAlert();
        loginAlert.setEnabled(true);

        MessageTemplate loginMessage = new MessageTemplate();
        loginMessage.setMessage("SSO login message");
        loginMessage.setLocale("et");
        loginAlert.addMessageTemplatesItem(loginMessage);
        loginAlert.setAuthMethods(Collections.emptyList());
        alert.setLoginAlert(loginAlert);

        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEnabled(true);

        MessageTemplate emailMessage = new MessageTemplate();
        emailMessage.setMessage("SSO email message");
        emailMessage.setLocale("et");
        emailAlert.addMessageTemplatesItem(emailMessage);
        alert.setEmailAlert(emailAlert);
        return alert;
    }

    public static Alert validTARAAlert() {
        Alert alert = new Alert();
        alert.setId("2");
        alert.setTitle("TARA alert title");
        alert.setStartTime(OffsetDateTime.now());
        alert.setEndTime(OffsetDateTime.now());

        LoginAlert loginAlert = new LoginAlert();
        loginAlert.setEnabled(true);

        MessageTemplate loginMessage = new MessageTemplate();
        loginMessage.setMessage("TARA login message");
        loginMessage.setLocale("et");
        loginAlert.addMessageTemplatesItem(loginMessage);
        loginAlert.setAuthMethods(Arrays.asList("idcard", "mid", "smartid", "eidas"));
        alert.setLoginAlert(loginAlert);

        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEnabled(true);

        MessageTemplate emailMessage = new MessageTemplate();
        emailMessage.setMessage("TARA email message");
        emailMessage.setLocale("et");
        emailAlert.addMessageTemplatesItem(emailMessage);
        alert.setEmailAlert(emailAlert);
        return alert;
    }
}
