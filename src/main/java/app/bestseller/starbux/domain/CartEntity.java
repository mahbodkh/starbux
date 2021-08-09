package app.bestseller.starbux.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.ObjectUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */

@Table(name = "\"best_cart\"")
@Data
@NoArgsConstructor
@Entity
public class CartEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userId")
    private Long user;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartProductDetailEntity> detailEntities = Collections.emptyList();

    @Builder(toBuilder = true)
    public CartEntity(Long id, Long user, Status status, Date created, Date changed, List<CartProductDetailEntity> detailEntities) {
        setId(id);
        setUser(user);
        setStatus(status);
        setDetailEntities(detailEntities);
        setCreated(created);
        setChanged(changed);
    }

    public enum Status {
        OPEN,
        DONE,
        CANCEL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartEntity that = (CartEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(created, that.created) && Objects.equals(changed, that.changed) && Objects.equals(detailEntities, that.detailEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, created, changed, detailEntities);
    }


    @Transient
    public static CartEntity getBasicCart(Long user, List<CartProductDetailEntity> detailEntities) {
        return CartEntity.builder()
            .user(user)
            .detailEntities(detailEntities)
            .status(Status.OPEN)
            .build();
    }


    @Transient
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        detailEntities.forEach(productDetail -> {
            total.add(productDetail.getTotal());
        });
        return total;
    }


//    public void prepareValues() {
//        setTotalAmount(getCartItemPrices());
//        if (ObjectUtils.isEmpty(discount)) {
//            setAmount(totalAmount);
//            setDiscountAmount(BigDecimal.ZERO);
//        }
//        calculateTotalDrinkProductNumber();
//    }

//    public int calculateTotalDrinkProductNumber() {
//        int totalDrinkProductNumber = 0;
//        for (CartItem cartItem : cartItems) {
//            if (ObjectUtils.isEmpty(cartItem.getDrinkProduct())) {
//                continue;
//            }
//            totalDrinkProductNumber += cartItem.getQuantity();
//        }
//        return totalDrinkProductNumber;
//    }
//
//    public BigDecimal getCartItemPrices() {
//        totalAmount = BigDecimal.ZERO;
//        for (CartItem cartItem : cartItems) {
//            cartItem.calculatePrice();
//            totalAmount = totalAmount.add(cartItem.getPrice());
//        }
//        return totalAmount;
//    }
//
//    public void addCartItem(CartItem cartItem) {
//        if (ObjectUtils.isEmpty(cartItems)) {
//            cartItems = new ArrayList<>();
//        }
//        cartItems.add(cartItem);
//    }
//




}
