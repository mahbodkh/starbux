package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.ProductEntity;
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
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByIdAndStatus(Long id, ProductEntity.Status status);
    Page<ProductEntity> findAllByStatusIn(List<ProductEntity.Status> statuses, Pageable pageable);
}
