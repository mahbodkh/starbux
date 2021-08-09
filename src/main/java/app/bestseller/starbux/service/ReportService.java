package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.TransactionEntity;
import app.bestseller.starbux.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;


/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "report")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportService {

    @Transactional(readOnly = true)
    public Page<TransactionEntity> loadTransactionWithDate(Long user, Date fromDate, Date toDate, Pageable pageable) {
        if (fromDate == null && toDate == null) return transactionRepository.findAllByUser(user, pageable);
        return transactionRepository.findAllByDateUser(user, fromDate, toDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> loadAllTransactionWithDate(Date fromDate, Date toDate, Pageable pageable) {
        if (fromDate == null && toDate == null) return transactionRepository.findAll(pageable);
        return transactionRepository.findAllByDate(fromDate, toDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TransactionEntity> loadUserTransactionsWithDate(Long user, Date fromDate, Date toDate, Pageable pageable) {
        if (fromDate == null && toDate == null) return transactionRepository.findAll(pageable);
        return transactionRepository.findAllByDate(fromDate, toDate, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<TransactionEntity> loadTransaction(Long transaction) {
        return transactionRepository.findById(transaction);
    }

    @Transactional
    public void deleteTransaction(Long transaction) {
        transactionRepository.findById(transaction)
            .ifPresent(reply -> {
                transactionRepository.delete(reply);
                log.debug("The transaction has deleted: ({}).", transaction);
            });
    }


    private final TransactionRepository transactionRepository;
    private final UserService userService;
}
