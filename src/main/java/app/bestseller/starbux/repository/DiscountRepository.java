package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
}
