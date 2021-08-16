package app.bestseller.starbux.controller;

import app.bestseller.starbux.controller.validator.PaginationValidator;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.service.ReportService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/report", produces = "application/json")
public class ReportController {
    // ==============================================
    //                     CLIENT
    // ==============================================

    // ==============================================
    //                     ADMIN
    // ==============================================
    @GetMapping("/admin/product/top/")
    public ResponseEntity<ReportTopSideProductReply> loadTopSoldSideProduct() throws BadRequestException {
        var reply = reportService.loadTopSideProduct();
        return ResponseEntity.ok(
            new ReportTopSideProductReply(
                reply.get("productId", Long.class),
                reply.get("productCount", Integer.class)
            ));
    }

    @GetMapping("/admin/user/amount/")
    public ResponseEntity<Page<ReportUsersTotalAmountReply>> loadMostSoldSideProduct(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size) throws BadRequestException {
        var users = reportService.loadTotalAmountPerUser
            (PaginationValidator.validatePaginationOrThrow(page, size));
        var reply = users.map(tuple ->
            new ReportUsersTotalAmountReply(
                tuple.get("user", Long.class),
                tuple.get("total", Double.class)
            ));
        return ResponseEntity.ok(reply);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class ReportTopSideProductReply {
        private Long product;
        private Integer count;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class ReportUsersTotalAmountReply {
        private Long user;
        private Double total;
    }

    private final ReportService reportService;
}
