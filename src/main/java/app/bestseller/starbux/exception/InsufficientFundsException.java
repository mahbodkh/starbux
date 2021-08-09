package app.bestseller.starbux.exception;


/**
 * Created by Ebrahim Kh.
 */

public class InsufficientFundsException extends BadRequestException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
