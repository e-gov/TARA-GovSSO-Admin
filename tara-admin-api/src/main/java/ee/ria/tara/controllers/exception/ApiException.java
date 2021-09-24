package ee.ria.tara.controllers.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    public ApiException(Exception exception) {
        super(exception);
    }

    public ApiException(String message, Exception exception) {
        super(message, exception);
    }
}
