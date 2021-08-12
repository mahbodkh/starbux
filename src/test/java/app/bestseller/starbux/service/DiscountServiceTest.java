package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.CartProductDetailEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.ProductRepository;
import app.bestseller.starbux.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Ebrahim Kh.
 */


@SpringBootTest
public class DiscountServiceTest {

    private @Autowired
    DiscountService discountService;

    private @Autowired
    UserRepository userRepository;

    private @Autowired
    ProductRepository productRepository;


    @BeforeEach
    public void prepareCartTest() {
        user = userRepository.save(buildUserEntity());
        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());
    }

    @Test
    public void testApplyRule() throws Exception {
        var cart = buildCartEntity();
        var discountEntity = discountService.applyRules(cart);



    }


    private CartEntity buildCartEntity() {
        var cart = new CartEntity();
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(user.getId());
        cart.setDetailEntities(buildProductDetailEntity());
        return cart;
    }

    private List<CartProductDetailEntity> buildProductDetailEntity() {
        var productDetails = new ArrayList<CartProductDetailEntity>();
        var entityFirst = new CartProductDetailEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(2);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());
        productDetails.add(entityFirst);

        var entitySecond = new CartProductDetailEntity();
        entitySecond.setProduct(productSecond.getId());
        entitySecond.setQuantity(1);
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

    private UserEntity buildUserEntity() {
        var user = new UserEntity();
        user.setUsername("username");
        user.setName("first_name");
        user.setFamily("last_family");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setEmail("email@email.com");
        return user;
    }

    private UserEntity user;
    private ProductEntity productFirst;
    private ProductEntity productSecond;
}
