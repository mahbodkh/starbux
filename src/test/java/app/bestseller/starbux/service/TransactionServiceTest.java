package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.TransactionEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by Ebrahim Kh.
 */


@SpringBootTest
public class TransactionServiceTest {
    private @Autowired
    TransactionService transactionService;
    private @Autowired
    TransactionRepository transactionRepository;

    @Test
    @Transactional
    public void testMakeTransaction() throws Exception {
    }

    @Test
    @Transactional
    public void testLoadTransaction() throws Exception {
    }

    @Test
    @Transactional
    public void deleteTransaction() throws Exception {

    }

    private TransactionEntity buildTransactionEntity() {
        var transaction = new TransactionEntity();

        return transaction;
    }

    private UserEntity buildUserEntity() {
        var user = new UserEntity();
        user.setUsername("username");
        user.setName("first_name");
        user.setFamily("last_family");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setEmail("email@email.com");
        return user;
    }

    private CartEntity buildOrderEntity() {
        var order = new CartEntity();

        return order;
    }
}
