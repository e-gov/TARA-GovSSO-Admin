package ee.ria.tara.controllers.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Exception exception) {
        super(exception);
    }

    public AuthenticationException(String message, Exception exception) {
        super(message, exception);
    }
}
