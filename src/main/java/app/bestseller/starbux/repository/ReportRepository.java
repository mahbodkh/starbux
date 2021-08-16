package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface ReportRepository extends PagingAndSortingRepository<OrderEntity, Long> {

    @Query(value = "SELECT I.PRODUCT_ID AS PRODUCTID, SUM (I.QUANTITY) AS PRODUCTCOUNT " +
        "FROM \"best_order\" O " +
        "    LEFT JOIN \"best_cart\" C ON O.CART_ID = C.ID " +
        "    LEFT JOIN \"best_property_item\" I ON C.ID = I.CART_ID " +
        "WHERE o.STATUS = 'DONE' and I.PRODUCT_TYPE = 'SIDE' " +
        "GROUP BY I.PRODUCT_ID ORDER BY SUM( I.QUANTITY ) DESC LIMIT ?1",
        nativeQuery = true
    )
    Tuple findTheTopSideProduct(int limit);


    @Query(value = "SELECT o.user as user, SUM(o.total) as total FROM OrderEntity o " +
        "WHERE o.status = 'DONE' GROUP BY o.user ORDER BY SUM(o.total) DESC")
    Page<Tuple> totalAmountPerUser(Pageable pageable);

}
