package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.OrderEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.CartRepository;
import app.bestseller.starbux.repository.OrderRepository;
import app.bestseller.starbux.repository.ProductRepository;
import app.bestseller.starbux.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class ReportServiceTest {
    private @Autowired
    ReportService reportService;
    private @Autowired
    OrderRepository orderRepository;
    private @Autowired
    DiscountService discountService;
    private @Autowired
    UserRepository userRepository;
    private @Autowired
    ProductRepository productRepository;
    private @Autowired
    CartRepository cartRepository;
    private @Autowired
    CartService cartService;

    @BeforeEach
    public void setup() {
        user = userRepository.save(buildUserEntity());

        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());
        productThird = productRepository.save(buildProductEntityThird());

        cart = cartService.createOrUpdateCart(user, productFirst.getId(), 4);
        cart = cartService.createOrUpdateCart(user, productSecond.getId(), 5);
        cart = cartService.createOrUpdateCart(user, productThird.getId(), 2);

        discount = discountService.applyPromotion(cart);
        order = orderRepository.save(buildOrderEntity());
    }


    @Test
    @Transactional
    public void testReport_mostFrequencyOrderedSideProduct() throws Exception {
        var product = reportService.loadTopSideProduct();

        assertEquals(productSecond.getId(), ((BigInteger) product.get("productId")).longValue());
        assertEquals(5, ((BigInteger) product.get("productCount")).intValue());
        assertEquals(productSecond.getType(), ProductEntity.Type.valueOf(product.get("productType", String.class)));
    }


    private OrderEntity buildOrderEntity() {
        var order = new OrderEntity();
        order.setStatus(OrderEntity.Status.DONE);
        order.setUser(user.getId());
        order.setCart(cart.getId());
        order.setTotal(cart.calculateTotal());
        order.setPrice(order.getTotal().subtract(discount.getRate()));
        order.setDiscount(discount.getRate());
        return order;
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
        product.setType(ProductEntity.Type.SIDE);
        product.setPrice(BigDecimal.valueOf(4));
        product.setName("product_second_name");
        product.setDescription("product_second_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        return product;
    }

    private ProductEntity buildProductEntityThird() {
        var product = new ProductEntity();
        product.setType(ProductEntity.Type.SIDE);
        product.setPrice(BigDecimal.valueOf(2));
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
        user.setStatus(UserEntity.Status.ACTIVE);
        return user;
    }

    private UserEntity user;
    private ProductEntity productFirst;
    private ProductEntity productSecond;
    private ProductEntity productThird;
    private CartEntity cart;
    private DiscountService.DiscountEntity discount;
    private OrderEntity order;
}
