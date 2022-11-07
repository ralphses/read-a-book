package online.contactraphael.readabook.exception;

import online.contactraphael.readabook.utility.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
@ResponseStatus
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({
            UnauthorizedUserException.class,
            UsernameNotFoundException.class,
            UnsuccessfulRequestException.class})
    public ResponseEntity<ResponseMessage> unauthorizedUserExceptionHandler
            (RuntimeException runtimeException, WebRequest webRequest) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage("failed", 1, Map.of("message", runtimeException.getMessage())));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseMessage> notFoundExceptionHandler
            (ResourceNotFoundException resourceNotFoundException, WebRequest webRequest) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage("failed", 1, Map.of("message", resourceNotFoundException.getMessage())));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            InvalidRequestParamException.class,
            FileStorageException.class})
    public ResponseEntity<ResponseMessage> badRequestException(RuntimeException runtimeException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage("failed", 1, Map.of("message", runtimeException.getMessage())));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseMessage> internalServerException(IllegalStateException illegalStateException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage("failed", 1, Map.of("message", illegalStateException.getMessage())));
    }

}
