package app.bestseller.starbux.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */


@Table(name = "\"best_property_item\"")
@Data
@NoArgsConstructor
@Entity
public class PropertyItemEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id")
    private Long product;
    @Column(name = "product_type")
    private ProductEntity.Type type;
    @Column
    private Integer quantity = 1;
    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;
    @Column(name = "total")
    private BigDecimal total = BigDecimal.ZERO;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    private Set<Long> sideProducts = new HashSet<>();


    public BigDecimal getTotal() {
        return getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (PropertyItemEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(product, that.product) &&
            Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, quantity);
    }

    @Builder(toBuilder = true)
    public PropertyItemEntity(Long id, Long product, ProductEntity.Type type, Integer quantity, BigDecimal price, BigDecimal total, CartEntity cart) {
        setId(id);
        setProduct(product);
        setType(type);
        setQuantity(quantity);
        setPrice(price);
        setTotal(total);
        setCart(cart);
    }

    @Transient
    public static PropertyItemEntity getBasicProperty(Long product, Integer quantity, BigDecimal price, ProductEntity.Type type, CartEntity cart) {
        return PropertyItemEntity.builder()
            .product(product)
            .quantity(quantity)
            .price(price)
            .type(type)
            .cart(cart)
            .total(price.multiply(BigDecimal.valueOf(quantity)))
            .build();
    }
}
