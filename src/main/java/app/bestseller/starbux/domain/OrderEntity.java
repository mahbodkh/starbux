package app.bestseller.starbux.domain;

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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */


@Table(name = "\"best_order\"")
@Data
@NoArgsConstructor
@Entity
public class OrderEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private BigDecimal price = BigDecimal.ZERO;
    @Column
    private BigDecimal total = BigDecimal.ZERO;
    @Column
    private BigDecimal discount = total.subtract(price);
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "cart_id")
    private Long cart;
    @Column(name = "user_id")
    private Long user;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;


    public enum Status {
        OPEN,
        DONE,
        CANCEL
    }

    @Builder(toBuilder = true)
    public OrderEntity(Long id, BigDecimal price, BigDecimal discount, BigDecimal total, Status status, Long cart, Long user, Date created, Date changed) {
        setId(id);
        setPrice(price);
        setDiscount(discount);
        setTotal(total);
        setStatus(status);
        setCart(cart);
        setUser(user);
        setCreated(created);
        setChanged(changed);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(price, that.price) && Objects.equals(discount, that.discount) && Objects.equals(total, that.total) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, discount, total, status);
    }

    @Transient
    public OrderEntity getBasicOrder(Long user, Long cart, BigDecimal discount, BigDecimal total) {
        return OrderEntity.builder()
            .user(user)
            .cart(cart)
            .discount(discount)
            .total(total)
            .price(total.subtract(discount))
            .status(Status.OPEN)
            .build();
    }
}
