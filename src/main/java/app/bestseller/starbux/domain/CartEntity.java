package app.bestseller.starbux.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import java.util.Comparator;
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
    public int compareTo(CartEntity o) {
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
    private Set<PropertyItemEntity> productItems = new HashSet<PropertyItemEntity>();

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
        return getProductItems().stream()
            .map(PropertyItemEntity::getTotal)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public Integer countTotalSide() {
        return (int) getProductItems().stream()
            .filter(p -> p.getType().equals(ProductEntity.Type.SIDE))
            .count();
    }

    @Transient
    public BigDecimal minItemPrice() {
        return getProductItems()
            .stream()
            .map(PropertyItemEntity::getPrice)
            .min(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO);
    }

    @Transient
    public BigDecimal calculateTotalSidePrice() {
        return getProductItems().stream()
            .filter(i -> ProductEntity.Type.SIDE.equals(i.getType()))
            .map(PropertyItemEntity::getTotal)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal calculateTotalMainPrice() {
        return getProductItems().stream()
            .filter(i -> ProductEntity.Type.MAIN.equals(i.getType()))
            .map(PropertyItemEntity::getTotal)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
