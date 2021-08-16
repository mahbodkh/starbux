package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByIdAndStatus(Long id, ProductEntity.Status status);
    Page<ProductEntity> findAllByStatusIn(List<ProductEntity.Status> statuses, Pageable pageable);

    @Query(
        value = "select prod.ID as productId, " +
            "prod.TYPE as productType, " +
            "( count(item.ID) * item.QUANTITY ) as productCount" +
            " from 'best_order' ord " +
            "         left outer join 'best_cart' cart on cart.ID = ord.CART_ID " +
            "         left outer join 'best_property_item' item on ord.CART_ID = item.CART_ID " +
            "         left outer join 'best_product' prod on item.PRODUCT_ID = prod.ID " +
            " where prod.TYPE = 'SIDE' group by item.PRODUCT_ID, item.QUANTITY order by (COUNT(item.PRODUCT_ID) * item.QUANTITY) DESC LIMIT 1 ",
        nativeQuery = true
    )
    Tuple findTheTopSideProduct();
}
