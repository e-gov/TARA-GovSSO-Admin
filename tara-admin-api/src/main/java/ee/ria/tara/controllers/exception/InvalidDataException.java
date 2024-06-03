package ee.ria.tara.controllers.exception;

public class InvalidDataException extends ApiException {
    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Object... args) {
        super(message, args);
    }

    public InvalidDataException(Exception exception) {
        super(exception);
    }

    public InvalidDataException(String message, Exception exception) {
        super(message, exception);
    }
}
