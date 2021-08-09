package app.bestseller.starbux.controller;

import app.bestseller.starbux.controller.validator.PaginationValidator;
import app.bestseller.starbux.domain.TransactionEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.service.ReportService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/report", produces = "application/json")
public class ReportController {
    private final ReportService reportService;

    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/")
    public ResponseEntity<ReportReply> loadTurnover(@PathVariable("id") Long transaction) throws NotFoundException {
        return ResponseEntity.of(reportService.loadTransaction(transaction)
            .map(reply ->
                new ReportReply(
                    reply.getId(),
                    reply.getUserEntityId(),
                    reply.getOrderEntityId(),
                    reply.getAmount().doubleValue(),
                    reply.getStatus(),
                    reply.getRejectReason(),
                    reply.getCreated(),
                    reply.getChanged())
            ));
    }

    @GetMapping("/all/")
    public ResponseEntity<Page<ReportReply>> loadTurnovers(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size,
        @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
        @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) throws BadRequestException {
        var transactions =
            reportService.loadTransactionWithDate(null,
                fromDate,
                toDate,
                PaginationValidator.validatePaginationOrThrow(page, size)
//                Boolean.FALSE
            );
        var reply = transactions.map(transaction ->
            new ReportReply(transaction.getId(),
                transaction.getUserEntityId(),
                transaction.getOrderEntityId(),
                transaction.getAmount().doubleValue(),
                transaction.getStatus(),
                transaction.getRejectReason(),
                transaction.getCreated(),
                transaction.getChanged())
        );
        return ResponseEntity.ok(reply);
    }

    // ==============================================
    //                     ADMIN
    // ==============================================

    @GetMapping("/admin/{user}/all/")
    public ResponseEntity<Page<ReportReply>>
    loadTurnoversByAdmin(@PathVariable("user") Long user,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "20") int size,
                         @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                         @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) throws BadRequestException {
        var transactions = reportService.loadTransactionWithDate(
            user,
            fromDate,
            toDate,
            PaginationValidator.validatePaginationOrThrow(page, size)
//            Boolean.TRUE
        );
        var reply = transactions.map(transaction ->
            new ReportReply(transaction.getId(),
                transaction.getUserEntityId(),
                transaction.getOrderEntityId(),
                transaction.getAmount().doubleValue(),
                transaction.getStatus(),
                transaction.getRejectReason(),
                transaction.getCreated(),
                transaction.getChanged())
        );
        return ResponseEntity.ok(reply);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<Page<ReportReply>>
    loadTurnoverByAdmin(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "20") int size,
                        @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                        @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) throws BadRequestException {
        var transactions = reportService.loadAllTransactionWithDate(
            fromDate,
            toDate,
            PaginationValidator.validatePaginationOrThrow(page, size)
        );
        var reply = transactions.map(transaction ->
            new ReportReply(transaction.getId(),
                transaction.getUserEntityId(),
                transaction.getOrderEntityId(),
                transaction.getAmount().doubleValue(),
                transaction.getStatus(),
                transaction.getRejectReason(),
                transaction.getCreated(),
                transaction.getChanged())
        );
        return ResponseEntity.ok(reply);
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    public static class ReportReply {
        private Long id;
        private Long user;
        private Long order;
        private Double amount;
        private TransactionEntity.Status status;
        private String rejectReason;
        private Date created;
        private Date changed;
    }
}
