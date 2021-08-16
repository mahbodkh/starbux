package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.OrderEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by Ebrahim Kh.
 */


@SpringBootTest
public class OrderServiceTest {
    private @Autowired
    OrderService orderService;
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


    @BeforeEach
    public void setup() {
        user = userRepository.save(buildUserEntity());
        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());
        cart = cartRepository.save(buildCartEntity());
        discount = discountService.applyPromotion(cart);
    }


    @Test
    @Transactional
    public void testCreateOrder() throws Exception {
        var order = orderService.createOrder(user, cart.getId());

        var save = orderRepository.findById(order.getId()).get();

        assertEquals(order.getId(), save.getId());
        assertEquals(order.getUser(), save.getUser());
        assertEquals(order.getCart(), save.getCart());
        assertEquals(cart.calculateTotal(), save.getTotal());
        assertEquals(discount.getRate(), save.getDiscount());
        assertEquals(cart.calculateTotal().subtract(discount.getRate()), save.getPrice());
    }


    @Test
    @Transactional
    public void testLoadOrder() throws Exception {
        var save = orderRepository.save(buildOrderEntity());

        var order = orderService.loadOrder(save.getId());

        assertEquals(save.getId(), order.getId());
        assertEquals(save.getUser(), order.getUser());
        assertEquals(save.getCart(), order.getCart());
        assertEquals(save.getTotal(), order.getTotal());
        assertEquals(save.getDiscount(), order.getDiscount());
        assertEquals(save.getPrice(), order.getPrice());
    }


    @Test
    @Transactional
    public void testLoadOrderByUser() throws Exception {
        var save = orderRepository.save(buildOrderEntity());

        var order = orderService.loadCurrentOrderByUser(save.getUser());

        assertEquals(save.getId(), order.getId());
        assertEquals(save.getUser(), order.getUser());
        assertEquals(save.getCart(), order.getCart());
        assertEquals(save.getTotal(), order.getTotal());
        assertEquals(save.getDiscount(), order.getDiscount());
        assertEquals(save.getPrice(), order.getPrice());
    }


    @Test
    @Transactional
    public void testDeleteOrder() throws Exception {
        var order = orderRepository.save(buildOrderEntity());

        orderService.deleteOrder(order.getId());

        var orderDelete = orderRepository.existsById(order.getId());
        assertFalse(orderDelete);
    }


    private OrderEntity buildOrderEntity() {
        var order = new OrderEntity();
        order.setStatus(OrderEntity.Status.OPEN);
        order.setUser(user.getId());
        order.setCart(cart.getId());
        order.setTotal(cart.calculateTotal());
        order.setPrice(order.getTotal().subtract(discount.getRate()));
        order.setDiscount(discount.getRate());
        return order;
    }

    private CartEntity buildCartEntity() {
        var cart = new CartEntity();
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(user.getId());
        cart.setProductItems(buildProductDetailEntity());
        return cart;
    }

    private Set<PropertyItemEntity> buildProductDetailEntity() {
        var productDetails = new HashSet<PropertyItemEntity>();
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(4);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());
        productDetails.add(entityFirst);

        var entitySecond = new PropertyItemEntity();
        entitySecond.setProduct(productSecond.getId());
        entitySecond.setQuantity(2);
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
    private CartEntity cart;
    private DiscountService.DiscountEntity discount;
}
