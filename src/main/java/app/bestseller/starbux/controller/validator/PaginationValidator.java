package app.bestseller.starbux.controller.validator;

import app.bestseller.starbux.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Created by Ebrahim Kh.
 */

public class PaginationValidator {

    private final static int MAX_PAGE_SIZE = 100;

    public static Pageable validatePaginationOrThrow(int page, int size) throws BadRequestException {
        if (page < 0) page = 0;
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("page size (" + size + ") must be between 1 and " + MAX_PAGE_SIZE);
        }
        return PageRequest.of(page - 1, size);
    }
}
