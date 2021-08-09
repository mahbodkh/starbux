package app.bestseller.starbux.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */


@Table(name = "\"best_cart_detail\"")
@Data
@NoArgsConstructor
@Entity
public class CartProductDetailEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long product;
    @Column
    private ProductEntity.Type type;
    @Column
    private Integer quantity = 1;
    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;
    @Column(name = "total")
    private BigDecimal total = getPrice().multiply(BigDecimal.valueOf(quantity));

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (CartProductDetailEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(product, that.product) &&
            type == that.type &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(price, that.price) &&
            Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, type, quantity, price, total);
    }
}
