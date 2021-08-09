package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.OrderEntity;
import app.bestseller.starbux.domain.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "transaction")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionService {


    public TransactionEntity makeTransaction(Long user, Long order, BigDecimal amount) {
        OrderEntity orderEntity = orderService.loadOrder(order);
        return null;
    }

    public Optional<TransactionEntity> loadTransaction(Long transaction) {
        return null;
    }

    public void deleteTransaction(Long transaction) {
    }


    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;
}
