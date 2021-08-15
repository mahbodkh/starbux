package app.bestseller.starbux.controller;

import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.service.ReportService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
                reply.get("productType", String.class),
                reply.get("productCount", Integer.class)
            ));
    }

    @GetMapping("/admin/")
    public ResponseEntity<ReportReply> loadMostSoldSideProduct() throws BadRequestException {
        var reply = reportService.loadTopSideProduct();
        return ResponseEntity.ok(
            new ReportReply()
        );
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class ReportTopSideProductReply {
        private Long product;
        private String type;
        private Integer count;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class ReportReply {

    }

    private final ReportService reportService;
}
