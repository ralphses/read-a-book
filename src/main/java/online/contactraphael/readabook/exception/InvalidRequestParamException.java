package online.contactraphael.readabook.exception;

public class InvalidRequestParamException extends RuntimeException {

    public InvalidRequestParamException(String message) {
        super(message);
    }

    public InvalidRequestParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
