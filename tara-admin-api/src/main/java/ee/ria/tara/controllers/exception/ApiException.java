package ee.ria.tara.controllers.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private Object[] args;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Object... args) {
        super(message);
        this.args = args;
    }

    public ApiException(Exception exception) {
        super(exception);
    }

    public ApiException(String message, Exception exception) {
        super(message, exception);
    }
}
