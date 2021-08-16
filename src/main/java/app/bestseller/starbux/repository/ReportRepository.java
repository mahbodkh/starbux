package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.UUID;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface ReportRepository extends PagingAndSortingRepository<OrderEntity, Long> {

    @Query(value = "SELECT I.PRODUCT_ID AS PRODUCTID, (COUNT(I.PRODUCT_ID) * I.QUANTITY) AS PRODUCTCOUNT, I.PRODUCT_TYPE AS PRODUCTTYPE, I.PRICE AS PRODUCTPRICE " +
        "FROM \"best_order\" O " +
        "    LEFT JOIN \"best_cart\" C ON O.CART_ID = C.ID " +
        "    LEFT JOIN \"best_property_item\" I ON C.ID = I.CART_ID " +
        "WHERE o.STATUS = 'DONE' and I.PRODUCT_TYPE = 'SIDE' " +
        "GROUP BY I.PRODUCT_ID, I.PRODUCT_TYPE, I.PRICE ORDER BY COUNT(I.PRODUCT_ID) DESC LIMIT ?1",
        nativeQuery = true
    )
    Tuple findTheTopSideProduct(int limit);

}
