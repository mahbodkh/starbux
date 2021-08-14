package app.bestseller.starbux.repository;

import app.bestseller.starbux.domain.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<TransactionEntity, Long> {

    @Query(
        value = "SELECT * FROM 'best_transaction' t WHERE t.user_id = ?1 ORDER BY t.created DESC",
        countQuery = "SELECT count(*) FROM 'best_transaction' t WHERE t.user_id = ?1 ",
        nativeQuery = true
    )
    Page<TransactionEntity> findAllByUser(Long user, Pageable pageable);

    @Query(
        value = "SELECT * FROM 'best_transaction' t WHERE t.user_id = ?1 " +
            " AND t.created >= ?2 AND t.created <= ?3 ORDER BY t.created DESC",
        countQuery = "SELECT count(*) FROM 'best_transaction' t WHERE t.user_id = ?1",
        nativeQuery = true
    )
    Page<TransactionEntity> findAllByDateUser(Long user, Date fromDate, Date toDate, Pageable pageable);


    @Query(
        value = "SELECT * FROM 'best_transaction' t WHERE " +
            " t.created >= ?1 AND created <= ?2 ORDER BY created DESC",
        countQuery = "SELECT count(*) FROM 'best_transaction' t",
        nativeQuery = true
    )
    Page<TransactionEntity> findAllByDate(Date fromDate, Date toDate, Pageable pageable);
}
