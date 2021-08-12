package app.bestseller.starbux.domain;

import app.bestseller.starbux.exception.AccountFrozenException;
import app.bestseller.starbux.exception.InsufficientFundsException;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */

@Table(name = "\"best_account\""
    , uniqueConstraints = @UniqueConstraint(columnNames = {"user"}, name = "account_entity_constraint"))
@Data
@NoArgsConstructor
@Entity
public class AccountEntity {

    public enum Status {
        ACTIVE,
        DISABLED,
        PENDING,
        FROZEN,
        DELETED
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "\"user\"")
    @NotNull
    private Long userEntity;
    @Column(name = "available")
    @Digits(integer = 18, fraction = 18)
    @NotNull
    private BigDecimal available;
    @Column(name = "credit")
    @Digits(integer = 18, fraction = 18)
    @NotNull
    private BigDecimal credit;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @Version
    private Date changed;


    @Builder(toBuilder = true)
    public AccountEntity(Long id, Status status, Long userEntity, Date created,
                         Date changed, BigDecimal available, BigDecimal credit) {
        setId(id);
        setStatus(status);
        setUserEntity(userEntity);
        setCreated(created);
        setChanged(changed);
        setAvailable(available);
        setCredit(credit);
    }

    @Transient
    public static AccountEntity getBasicAccount(Long userEntity) {
        return AccountEntity.builder()
            .userEntity(userEntity)
            .status(Status.ACTIVE)
            .available(BigDecimal.ZERO)
            .credit(BigDecimal.ZERO)
            .build();
    }

    @Transient
    public void creditFromAvailable(BigDecimal value) throws AccountFrozenException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountFrozenException("cannot block negative amount (" + value + ")");
        }
        checkTransferFromPossible(value); // checks isActive and enough money
        available = available.subtract(value);
        credit = credit.add(value);
    }

    @Transient
    public void credit(BigDecimal value) throws AccountFrozenException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountFrozenException("cannot block negative amount (" + value + ")");
        }
        checkTransferFromPossible(value); // checks isActive and enough money
        available = available.subtract(value);
        credit = credit.add(value);
    }

    @Transient
    public void spendFromCredit(BigDecimal value) throws InsufficientFundsException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("cannot release negative amount (" + value + ")");
        }
        // NB: skip "account is active" check
        if (credit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("unable to release (" + value + ") from account (" + getId() + ") having only (" + getCredit() + ") credited.");
        }
        credit = credit.subtract(value);
    }

    @Transient
    public void unlockIntoAvailable(BigDecimal value) throws InsufficientFundsException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("cannot unlock negative amount (" + value + ")");
        }
        // NB: skip "account is active" check
        if (credit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("unable to unlock (" + value + ") from account (" + getId() + ") having only (" + getCredit() + ") credited.");
        }
        credit = credit.subtract(value);
        available = available.add(value);
    }

    @Transient
    public void addAvailable(BigDecimal value) throws InsufficientFundsException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("cannot deposit negative amount (" + value + ")");
        }
        available = available.add(value);
    }

    @Transient
    public void subAvailable(BigDecimal value) throws InsufficientFundsException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("cannot sub negative amount (" + value.toPlainString() + ")");
        } else if (available.compareTo(value) < 0) {
            throw new InsufficientFundsException("you don't have enough funds, you need (" + value.toPlainString() + ")");
        }
        available = available.subtract(value);
    }


    @Transient
    public void checkTransferFromPossible(BigDecimal value) throws AccountFrozenException, InsufficientFundsException {
        if (!isActiveOrPending()) {
            throw new AccountFrozenException("unable to transfer (" + value + ") funds from/to not active (" + getStatus() + ") account (" + getId() + ")");
        } else if (available.compareTo(value) < 0) {
            throw new InsufficientFundsException("unable to transfer (" + value + ") from account which has only (" + getAvailable() + ") available");
        }
    }

    @Transient
    public void checkAvailableToSpend(BigDecimal value) {
        if (available.compareTo(value) < 0) {
            throw new InsufficientFundsException("Your Balance is not enough (" + value + ") from account (" + getId() + ") having only (" + getAvailable() + ") available");
        }
    }

    @Transient
    public boolean isActiveOrPending() {
        return getStatus() == Status.ACTIVE || getStatus() == Status.PENDING;
    }

    @Transient
    public void checkTransferStatusToPossible(BigDecimal value) throws AccountFrozenException {
        if (!isActiveOrPending()) {
            throw new AccountFrozenException("unable to transfer (" + value + ") funds to not active (" + getStatus() + ") account (" + getId() + ")");
        }
    }

    @Transient
    public boolean isFrozen() {
        return getStatus() == Status.FROZEN;
    }


}
