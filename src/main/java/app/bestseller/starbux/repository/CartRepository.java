package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByUserAndStatusIn(Long user, List<CartEntity.Status> status);
}
