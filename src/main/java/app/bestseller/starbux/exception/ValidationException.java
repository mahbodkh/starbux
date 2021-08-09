package app.bestseller.starbux.exception;

import lombok.Getter;

import java.util.Map;

/**
 * Created by Ebrahim Kh.
 */

@Getter
public class ValidationException extends RuntimeException {
    private Map<String, String> params;
    private String message;

    public ValidationException(String message, Map<String, String> params) {
        this.params = params;
        this.message = message;
    }

    public ValidationException(String message) {
        super(message);
    }
}
