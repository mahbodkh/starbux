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
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

        // cart + discount + order -- > First
        cartFirst = cartService.createOrUpdateCart(user, productFirst.getId(), 4);
        cartFirst = cartService.createOrUpdateCart(user, productSecond.getId(), 5);
        cartFirst = cartService.createOrUpdateCart(user, productThird.getId(), 2);
        cartFirst.setStatus(CartEntity.Status.DONE);
        cartFirst = cartRepository.save(cartFirst);
        discountFirst = discountService.applyPromotion(cartFirst);
        orderFirst = orderRepository.save(buildOrderEntityFirst());

        // cart + discount + order -- > Second
        cartSecond = cartService.createOrUpdateCart(user, productFirst.getId(), 3);
        cartSecond.setStatus(CartEntity.Status.DONE);
        cartSecond = cartRepository.save(cartSecond);
        discountSecond = discountService.applyPromotion(cartSecond);
        orderSecond = orderRepository.save(buildOrderEntitySecond());
    }


    @Test
    @Transactional
    public void testReport_mostFrequencyOrderedSideProduct() throws Exception {
        var product = reportService.loadTopSideProduct();

        assertEquals(productSecond.getId(), ((BigInteger) product.get("productId")).longValue());
        assertEquals(5, ((BigInteger) product.get("productCount")).intValue());
    }

    @Test
    @Transactional
    public void testReport_totalAmountPerUser() throws Exception {
        var totalAmountAndUsers = reportService.loadTotalAmountPerUser(Pageable.ofSize(20));

        var tupleUsers = totalAmountAndUsers.getContent();

        assertEquals(user.getId(), tupleUsers.get(0).get("user", Long.class));
        assertEquals(cartFirst.calculateTotal().add(cartSecond.calculateTotal())
                .setScale(2, RoundingMode.HALF_UP),
            tupleUsers.get(0).get("total", BigDecimal.class)
                .setScale(2, RoundingMode.HALF_UP));
    }


    private OrderEntity buildOrderEntityFirst() {
        var order = new OrderEntity();
        order.setStatus(OrderEntity.Status.DONE);
        order.setUser(user.getId());
        order.setCart(cartFirst.getId());
        order.setTotal(cartFirst.calculateTotal());
        order.setPrice(order.getTotal().subtract(discountFirst.getRate()));
        order.setDiscount(discountFirst.getRate());
        return order;
    }

    private OrderEntity buildOrderEntitySecond() {
        var order = new OrderEntity();
        order.setStatus(OrderEntity.Status.DONE);
        order.setUser(user.getId());
        order.setCart(cartSecond.getId());
        order.setTotal(cartSecond.calculateTotal());
        order.setPrice(order.getTotal().subtract(discountSecond.getRate()));
        order.setDiscount(discountSecond.getRate());
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
        product.setName("product_third_name");
        product.setDescription("product_third_description");
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
    private CartEntity cartFirst;
    private CartEntity cartSecond;
    private DiscountService.DiscountEntity discountFirst;
    private DiscountService.DiscountEntity discountSecond;
    private OrderEntity orderFirst;
    private OrderEntity orderSecond;
}
