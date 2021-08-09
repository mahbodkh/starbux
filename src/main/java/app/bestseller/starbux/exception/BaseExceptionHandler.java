package app.bestseller.starbux.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebrahim Kh.
 */

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResult> handleValidationException(ValidationException exception) {
        return this.returnException(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, exception.getParams());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var params = new HashMap<String, String>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            params.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return returnException(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, params);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResult> handleBusinessException(BadRequestException exception) {
        return this.returnException(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, exception.getParams());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleException(Exception exception) {
        String message = "SYSTEM_ERROR";
        // we could check here the profile then throw stackTraceOrElse ;))
        return this.returnException(exception, message, HttpStatus.BAD_REQUEST, Map.of());
    }

    private ResponseEntity<ErrorResult> returnException(Exception exception, String message, HttpStatus httpStatus, Map<String, String> params) {
        log.error("", exception);
        return ResponseEntity.status(httpStatus)
            .body(new ErrorResult(Instant.now(), httpStatus, message, params));
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResult {
        private Instant timestamp;
        private HttpStatus status;
        private String message;
        private Map<String, String> errors;
    }
}
