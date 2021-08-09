package app.bestseller.starbux.exception;


/**
 * Created by Ebrahim Kh.
 */


public class AccountFrozenException extends BadRequestException {

    public AccountFrozenException(String message) {
        super(message);
    }
}
