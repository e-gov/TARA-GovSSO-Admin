package ee.ria.tara.service.helper;


import ee.ria.tara.controllers.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientSecretGenerator {

    public static final int SIGNING_SECRET_LENGTH = 32;

    public String generate() throws ApiException {
        return RandomStringUtils.secure().nextAlphanumeric(SIGNING_SECRET_LENGTH);
    }

}
