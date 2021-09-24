package ee.ria.tara.controllers.exception;

public class FatalApiException extends ApiException {
    public FatalApiException(String message) {
        super(message);
    }

    public FatalApiException(Exception exception) {
        super(exception);
    }

    public FatalApiException(String message, Exception exception) {
        super(message, exception);
    }
}
