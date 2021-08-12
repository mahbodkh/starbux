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

@Table(name = "\"best_product\"")
@Data
@NoArgsConstructor
@Entity
public class ProductEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private BigDecimal price;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;


    @Builder(toBuilder = true)
    public ProductEntity(Long id, String name, String description, Type type,
                         BigDecimal price, Status status, Date created, Date changed) {
        setId(id);
        setName(name);
        setDescription(description);
        setPrice(price);
        setType(type);
        setStatus(status);
        setCreated(created);
        setChanged(changed);
    }

    public enum Type {
        MAIN,
        SIDE
    }


    public enum Status {
        AVAILABLE,      // in Stock
        DISCONTINUE,    //
        PENDING,        // Out Of Stock
        BANNED          //
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return
            Objects.equals(name, that.name) &&
                Objects.equals(price, that.price) &&
                Objects.equals(created, that.created) &&
                Objects.equals(changed, that.changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, created, changed);
    }

    @Transient
    public static ProductEntity getBasicProduct(String name, String description, BigDecimal price,Status status, Type type) {
        return ProductEntity.builder()
            .name(name)
            .description(description)
            .price(price)
            .type(type)
            .status(status)
            .build();
    }


}
