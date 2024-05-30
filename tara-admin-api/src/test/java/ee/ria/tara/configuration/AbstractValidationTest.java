package ee.ria.tara.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public abstract class AbstractValidationTest {

    private static ValidatorFactory validatorFactory;

    protected Validator validator;

    @BeforeAll
    public static void setUpDefaultValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @BeforeEach
    public void setUpDefaultValidator() {
        validator = validatorFactory.getValidator();
    }

    protected void validateAndExpectNoErrors(Object objectToValidate) {
        Set<ConstraintViolation<Object>> violations = validator.validate(objectToValidate);
        Assertions.assertTrue(violations.isEmpty());
    }

    protected void validateAndExpectOneError(Object objectToValidate, String path, String message) {
        Set<ConstraintViolation<Object>> violations = validator.validate(objectToValidate);
        Assertions.assertEquals(1, violations.size());

        ConstraintViolation violation = violations.stream().findFirst().get();
        Assertions.assertEquals(path, violation.getPropertyPath().toString());
        Assertions.assertEquals(message, violation.getMessage());
    }

}
