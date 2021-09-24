package ee.ria.tara.service.helper;


import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public enum SecureRandomAlphaNumericStringGenerator {
    INSTANCE;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generate(int length) throws ApiException {
        validateLength(length);

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(SECURE_RANDOM.nextInt(CHARS.length())));
        }

        return sb.toString();
    }

    private void validateLength(int length) throws FatalApiException {
        if (length < 1) {
            log.error("Trying to generate secure string with invalid length: " + length);
            throw new FatalApiException("Length < 1 not allowed");
        }
    }

}
