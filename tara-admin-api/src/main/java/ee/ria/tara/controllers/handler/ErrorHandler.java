package ee.ria.tara.controllers.handler;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.AuthenticationException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.lang.String.join;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ErrorHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Error handling request", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(formatBindingErrors(e));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        log.warn("Error handling request", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler({ InvalidDataException.class, RecordDoesNotExistException.class })
    public ResponseEntity<String> handleUserErrors(ApiException e) {
        log.warn("Error handling request", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(formatError(e, e.getArgs()));
    }

    @ExceptionHandler(FatalApiException.class)
    public ResponseEntity<String> handleFatalApiException(FatalApiException e) {
        log.error("Error handling request", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(formatError(e));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication error", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(formatError(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Unknown error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Tehniline viga, palun kontakteeruge serveri administraatoriga.");
    }

    private String formatBindingErrors(MethodArgumentNotValidException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();

        List<String> errors = new ArrayList<>();

        List<ObjectError> allErrors = bindingResult.getAllErrors();

        Locale locale = LocaleContextHolder.getLocale();

        for (ObjectError objectError: allErrors) {
            errors.add(format("%s", messageSource.getMessage(objectError, locale)));
        }
        log.info("errors: " + errors);
        Collections.sort(errors);
        return join(" ", errors);
    }

    private String formatError(Exception exception, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        String error = format("%s", messageSource.getMessage(exception.getMessage(), args, locale));

        log.info("Error: " + error);

        return error;
    }


}
