package app.bestseller.starbux.exception;


import java.util.Collections;
import java.util.Map;

/**
 * Created by Ebrahim Kh.
 */

public class BadRequestException extends ValidationException {

    public BadRequestException(String message, Map<String, String> errors) {
        super(message, errors);
    }

    public BadRequestException(String message) {
        this(message, Collections.emptyMap());
    }
}
