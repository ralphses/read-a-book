package online.contactraphael.readabook.exception;

public class UnsuccessfulRequestException extends RuntimeException{

    public UnsuccessfulRequestException(String message) {
        super(message);
    }

    public UnsuccessfulRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
