package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.CartRepository;
import app.bestseller.starbux.repository.ProductRepository;
import app.bestseller.starbux.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by Ebrahim Kh.
 */


@SpringBootTest
public class CartServiceTest {
    private @Autowired
    CartService cartService;
    private @Autowired
    CartRepository cartRepository;
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
    @Transactional
    public void createCart() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(user, productFirst.getId(), quantity);

        var save = cartRepository.findById(cart.getId()).get();

        var propertyItemEntity = save.getProductItems().stream().findAny().get();
        assertEquals(cart.getId(), save.getId());
        assertEquals(user.getId(), save.getUser());
        assertEquals(quantity, propertyItemEntity.getQuantity());
        assertEquals(productFirst.getType(), propertyItemEntity.getType());
        assertEquals(productFirst.getPrice(), propertyItemEntity.getPrice());
        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), propertyItemEntity.getTotal());
        assertEquals(1, save.getProductItems().size());
        assertEquals(cart.getProductItems().stream().findFirst().get().getProduct(),
            propertyItemEntity.getProduct());
    }


    @Test
    @Transactional
    public void createDuplicateCart() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(user, productFirst.getId(), quantity);
        quantity = 4;
        var cartSecond = cartService.createOrUpdateCart(user, productFirst.getId(), quantity);

//        assertEquals(cart.getId(), cartSecond.getId());
//        assertEquals(user.getId(), cartSecond.getUser());
//        assertEquals(quantity, cartSecond.getProductProperties().get(0).getQuantity());
//        assertEquals(productFirst.getType(), cartSecond.getProductProperties().get(0).getType());
//        assertEquals(productFirst.getPrice(), cartSecond.getProductProperties().get(0).getPrice());
//        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), cartSecond.getProductProperties().get(0).getTotal());
//        assertEquals(1, cartSecond.getProductProperties().size());
//        assertEquals(cart.getProductProperties().get(0).getProduct(), cartSecond.getProductProperties().get(0).getProduct());
    }


    @Test
    @Transactional
    public void loadCart() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(user, productFirst.getId(), quantity);

        var cartLoad = cartService.loadCart(cart.getId());

//        assertEquals(cart.getId(), cartLoad.getId());
//        assertEquals(user.getId(), cartLoad.getUser());
//        assertEquals(quantity, cartLoad.getProductProperties().get(0).getQuantity());
//        assertEquals(productFirst.getType(), cartLoad.getProductProperties().get(0).getType());
//        assertEquals(productFirst.getPrice(), cartLoad.getProductProperties().get(0).getPrice());
//        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), cartLoad.getProductProperties().get(0).getTotal());
//        assertEquals(1, cartLoad.getProductProperties().size());
//        assertEquals(cart.getProductProperties().get(0).getProduct(), cartLoad.getProductProperties().get(0).getProduct());
    }

    @Test
//    @Transactional
    public void testEditCart() throws Exception {
        user = userRepository.save(buildUserEntity());
        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());

        var quantityFirst = 2;
        var cart = cartService.createOrUpdateCart(user, productFirst.getId(), quantityFirst);

        var quantitySecond = 4;
        var cartSecond = cartService.createOrUpdateCart(user, productSecond.getId(), quantitySecond);

//        assertEquals(cart.getId(), cartSecond.getId());
//        assertEquals(user.getId(), cartSecond.getUser());
//
//        var properties = cartSecond.getProductProperties();
//        assertEquals(quantityFirst, properties.get(0).getQuantity());
//        assertEquals(quantitySecond, properties.get(1).getQuantity());
//
//        assertEquals(productFirst.getType(), cartSecond.getProductProperties().get(0).getType());
//        assertEquals(productSecond.getType(), cartSecond.getProductProperties().get(1).getType());
//
//        assertEquals(productFirst.getPrice(), cartSecond.getProductProperties().get(0).getPrice());
//        assertEquals(productSecond.getPrice(), cartSecond.getProductProperties().get(1).getPrice());
//
//        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantityFirst)), cartSecond.getProductProperties().get(0).getTotal());
//        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantitySecond)), cartSecond.getProductProperties().get(1).getTotal());
//
//        assertEquals(productFirst.getId(), cartSecond.getProductProperties().get(0).getProduct());
//        assertEquals(productSecond.getId(), cartSecond.getProductProperties().get(1).getProduct());
//
//        assertEquals(2, cartSecond.getProductProperties().size());
    }


    @Test
    @Transactional
    public void loadCartsByUser() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(user, productFirst.getId(), quantity);

        var cartLoadByUser = cartService.loadCartsByUser(user.getId());

//        assertEquals(cart.getId(), cartLoadByUser.getId());
//        assertEquals(user.getId(), cartLoadByUser.getUser());
//        assertEquals(quantity, cartLoadByUser.getProductProperties().get(0).getQuantity());
//        assertEquals(productFirst.getType(), cartLoadByUser.getProductProperties().get(0).getType());
//        assertEquals(productFirst.getPrice(), cartLoadByUser.getProductProperties().get(0).getPrice());
//        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), cartLoadByUser.getProductProperties().get(0).getTotal());
//        assertEquals(1, cartLoadByUser.getProductProperties().size());
//        assertEquals(cart.getProductProperties().get(0).getProduct(), cartLoadByUser.getProductProperties().get(0).getProduct());

    }

    @Test
    @Transactional
    public void testDeleteCart() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(user, productFirst.getId(), quantity);

        cartService.deleteCart(cart.getId());

        var cartDeleted = cartRepository.existsById(cart.getId());
        assertFalse(cartDeleted);
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
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setEmail("email@email.com");
        return user;
    }

    private UserEntity user;
    private ProductEntity productFirst;
    private ProductEntity productSecond;
}
