package app.bestseller.starbux.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.jetbrains.annotations.NotNull;

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
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Ebrahim Kh.
 */

@Table(name = "\"best_cart\"")
@Data
@NoArgsConstructor
@Entity
public class CartEntity implements Comparable<CartEntity> {

    @Override
    public int compareTo(@NotNull CartEntity o) {
        return getCreated().compareTo(o.getCreated());
    }

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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Where(clause = "DELETED=0")
    private Set<PropertyItemEntity> productItems = new HashSet<>();


//    @Transient
//    private <T> T getPropertiesByType(Class<T> classType) {
//        var props = Optional.ofNullable(getProperties()).orElseGet(HashMap::new);
//        setProperties(props);
//        return OBJECT_MAPPER.convertValue(props, classType);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Transient
//    private <T> void setPropertiesByType(T t) {
//        var newMap = OBJECT_MAPPER.convertValue(t, Map.class);
//        setProperties(newMap);
//    }

//    @Transient
//    public ProductProperties getProductProperties() {
//        return getPropertiesByType(ProductProperties.class);
//    }
//
//    @Transient
//    public void setProductProperties(ProductProperties emailProperties) {
//        setPropertiesByType(emailProperties);
//    }

//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    @Builder
//    public static class ProductProperties {
//        private Long product;
//        private Integer quantity = 1;
//        private ProductEntity.Type type;
//        private BigDecimal price = BigDecimal.ZERO;
//        private BigDecimal total = BigDecimal.ZERO;
//
//        public BigDecimal getTotal() {
//            return getPrice().multiply(BigDecimal.valueOf(quantity));
//        }
//    }


    @Builder(toBuilder = true)
    public CartEntity(Long id, Long user, Status status, Date created, Date changed, Set<PropertyItemEntity> productItems) {
        setId(id);
        setUser(user);
        setStatus(status);
        setProductItems(productItems);
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
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, created);
    }


    @Transient
    public static CartEntity getBasicCart(Long user) {
        return CartEntity.builder()
            .user(user)
            .productItems(Set.of())
            .status(Status.OPEN)
            .build();
    }


    @Transient
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        getProductItems().forEach(productDetail -> {
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


    public void addOrUpdateProductToCart(PropertyItemEntity property) {
        productItems.add(property);
    }


}
