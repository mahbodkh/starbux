package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.TransactionEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
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

    private CartEntity buildCartEntity_FirstUser() {
        var cart = new CartEntity();
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(userFirst.getId());
        cart.setProductItems(buildProductItemEntity_FirstUser());
        return cart;
    }

    private CartEntity buildCartEntity_SecondUser() {
        var cart = new CartEntity();
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(userSecond.getId());
        cart.setProductItems(buildProductItemEntity_SecondUser());
        return cart;
    }

    private Set<PropertyItemEntity> buildProductItemEntity_FirstUser() {
        var productDetails = new HashSet<PropertyItemEntity>();
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(2);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());
        productDetails.add(entityFirst);

        var entitySecond = new PropertyItemEntity();
        entitySecond.setProduct(productSecond.getId());
        entitySecond.setQuantity(1);
        entitySecond.setType(productSecond.getType());
        entitySecond.setPrice(productSecond.getPrice());
        productDetails.add(entitySecond);

        return productDetails;
    }

    private Set<PropertyItemEntity> buildProductItemEntity_SecondUser() {
        var productDetails = new HashSet<PropertyItemEntity>();
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(5);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());
        productDetails.add(entityFirst);

        var entitySecond = new PropertyItemEntity();
        entitySecond.setProduct(productSecond.getId());
        entitySecond.setQuantity(3);
        entitySecond.setType(productSecond.getType());
        entitySecond.setPrice(productSecond.getPrice());
        productDetails.add(entitySecond);

        return productDetails;
    }

    private ProductEntity buildProductEntityFirst() {
        var product = new ProductEntity();
        product.setType(ProductEntity.Type.MAIN);
        product.setPrice(BigDecimal.valueOf(5));
        product.setName("product_first_name");
        product.setDescription("product_first_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        return product;
    }

    private ProductEntity buildProductEntitySecond() {
        var product = new ProductEntity();
        product.setType(ProductEntity.Type.MAIN);
        product.setPrice(BigDecimal.valueOf(4));
        product.setName("product_second_name");
        product.setDescription("product_second_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        return product;
    }

    private UserEntity buildUserEntityFirst() {
        var user = new UserEntity();
        user.setUsername("username_first");
        user.setName("first_name_first");
        user.setFamily("last_family_first");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setEmail("email_first@email.com");
        return user;
    }

    private UserEntity buildUserEntitySecond() {
        var user = new UserEntity();
        user.setUsername("username_second");
        user.setName("first_name_second");
        user.setFamily("last_family_second");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setEmail("email_second@email.com");
        return user;
    }

    private UserEntity userFirst;
    private UserEntity userSecond;
    private ProductEntity productFirst;
    private ProductEntity productSecond;
}
