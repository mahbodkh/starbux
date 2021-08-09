package app.bestseller.starbux.controller;

import app.bestseller.starbux.exception.ValidationException;
import app.bestseller.starbux.service.TransactionService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/transaction", produces = "application/json")
public class TransactionController {
    private final TransactionService transactionService;

    // ==============================================
    //                     CLIENT
    // ==============================================
    @PostMapping("/")
    public ResponseEntity<TransactionReply> doTransaction(@Valid @RequestBody TransactionRequest request
    ) throws ValidationException {
        var transaction = transactionService.makeTransaction(
            request.getUser(),
            request.getOrder(),
            BigDecimal.valueOf(request.getAmount())
        );
        return ResponseEntity.ok(
            new TransactionReply(transaction.getId(),
                transaction.getStatus().name(),
                transaction.getUserEntityId(),
                transaction.getUserEntityId(),
                transaction.getAmount().doubleValue(),
                transaction.getRejectReason(),
                transaction.getCreated(),
                transaction.getChanged()
            ));
    }


    @GetMapping("/{id}/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionReply> loadTransfer(@PathVariable("id") Long transaction) {
        return ResponseEntity.of(transactionService.loadTransaction(transaction)
            .map(transfer -> {
                return new TransactionReply(
                    transfer.getId(),
                    transfer.getStatus().name(),
                    transfer.getUserEntityId(),
                    transfer.getOrderEntityId(),
                    transfer.getAmount().doubleValue(),
                    transfer.getRejectReason(),
                    transfer.getCreated(),
                    transfer.getChanged()
                );
            })
        );
    }


    // ==============================================
    //                     ADMIN
    // ==============================================
    @PutMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTransaction(@PathVariable("id") Long transaction) {
        transactionService.deleteTransaction(transaction);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class TransactionRequest {
        @NotNull
        @Size(max = 100)
        @NotBlank
        private Long user;
        @NotNull
        @Size(max = 100)
        @NotBlank
        private Long order;
        @NotNull
        @Size(max = 10)
        @NotBlank
        private Double amount;
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    public static class TransactionReply {
        private Long id;
        private String status;
        private Long user;
        private Long order;
        private Double amount;
        private String rejectReason;
        private Date created;
        private Date changed;
    }
}
