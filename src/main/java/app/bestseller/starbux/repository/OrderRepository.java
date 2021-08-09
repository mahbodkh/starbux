package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByUserAndStatus(Long user, OrderEntity.Status status);
    Page<OrderEntity> findAllByUser(Long user, Pageable pageable);
    Page<OrderEntity> findAllByUserAndStatusIn(Long user, List<OrderEntity.Status> status, Pageable pageable);
}
