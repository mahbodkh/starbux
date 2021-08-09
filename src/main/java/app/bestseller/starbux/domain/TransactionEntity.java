package app.bestseller.starbux.domain;

import app.bestseller.starbux.exception.ValidationException;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


/**
 * Created by Ebrahim Kh.
 */

@Table(name = "\"best_transaction\"")
@Data
@NoArgsConstructor
@Entity
public class TransactionEntity {

    public enum Status {
        PENDING,
        COMPLETED,
        REJECTED,
        UNVERIFIED,
        CANCELLED,
    }


    @Transient
    public void validateStatus(Status status, String action) throws ValidationException {
        if (getStatus() != status) {
            throw new ValidationException("Cannot " + action + " transfer in status (" + getStatus() + ")",
                Map.of("status", "must be " + status));
        }
    }

    @Builder(toBuilder = true)
    public TransactionEntity(
        Long id, Status status, Long userEntityId, Long orderEntityId, BigDecimal amount, String rejectReason,
        Date created, Date changed
    ) {
        setId(id);
        setStatus(status);
        setUserEntityId(userEntityId);
        setOrderEntityId(orderEntityId);
        setAmount(amount);
        setRejectReason(rejectReason);
        setCreated(created);
        setChanged(changed);
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "user_id")
    @NotNull
    private Long userEntityId;
    @Column(name = "order_id")
    @NotNull
    private Long orderEntityId;
    @NotNull
    @Digits(integer = 18, fraction = 18)
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "reject_reason")
    @Size(max = 200)
    private String rejectReason;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;


    @Transient
    public static TransactionEntity getBasicTransaction(Long user, Long order,
                                                        TransactionEntity.Status status,
                                                        BigDecimal amount) {
        return TransactionEntity.builder()
            .status(status)
            .userEntityId(user)
            .orderEntityId(order)
            .amount(amount)
            .build();
    }

}
