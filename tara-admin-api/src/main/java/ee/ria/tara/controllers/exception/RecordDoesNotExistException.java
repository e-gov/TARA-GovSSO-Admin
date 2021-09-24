package ee.ria.tara.controllers.exception;

public class RecordDoesNotExistException extends ApiException {
    public RecordDoesNotExistException(String message) {
        super(message);
    }

    public RecordDoesNotExistException(Exception exception) {
        super(exception);
    }

    public RecordDoesNotExistException(String message, Exception exception) {
        super(message, exception);
    }
}
