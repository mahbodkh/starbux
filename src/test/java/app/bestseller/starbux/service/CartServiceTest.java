package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
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
        userFirst = userRepository.save(buildUserEntityFirst());
        userSecond = userRepository.save(buildUserEntitySecond());
        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());
    }


    @Test
    @Transactional
    public void createCart() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantity);

        var save = cartRepository.findById(cart.getId()).get();

        var propertyItemEntity = save.getProductItems().stream().findAny().get();
        assertEquals(cart.getId(), save.getId());
        assertEquals(userFirst.getId(), save.getUser());
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
        var cart = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantity);
        quantity = 4;
        var cartSecond = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantity);

        assertEquals(cart.getId(), cartSecond.getId());
        assertEquals(userFirst.getId(), cartSecond.getUser());
        var propertyItemEntity = cartSecond.getProductItems().stream().findFirst().get();

        assertEquals(quantity, propertyItemEntity.getQuantity());
        assertEquals(productFirst.getType(), propertyItemEntity.getType());
        assertEquals(productFirst.getPrice(), propertyItemEntity.getPrice());
        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), propertyItemEntity.getTotal());
        assertEquals(1, cartSecond.getProductItems().size());
        assertEquals(cart.getProductItems().stream().findFirst().get().getProduct(),
            propertyItemEntity.getProduct());
    }


    @Test
    @Transactional
    public void testLoadCart_ByCartId() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantity);

        var cartLoad = cartService.loadCart(cart.getId());
        var item = cartLoad.getProductItems().stream().findFirst().get();

        assertEquals(cart.getId(), cartLoad.getId());
        assertEquals(userFirst.getId(), cartLoad.getUser());
        assertEquals(quantity, item.getQuantity());
        assertEquals(productFirst.getType(), item.getType());
        assertEquals(productFirst.getPrice(), item.getPrice());
        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), item.getTotal());
        assertEquals(cart.getProductItems().stream().findFirst().get().getProduct(), item.getProduct());
        assertEquals(1, cartLoad.getProductItems().size());
    }

    @Test
    @Transactional
    public void testCreateOrUpdateCart_AddItemToCart() throws Exception {
        var quantityFirst = 2;
        var cart = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantityFirst);
        var quantitySecond = 4;
        var cartSecond = cartService.createOrUpdateCart(userFirst, productSecond.getId(), quantitySecond);

        var items = new ArrayList<PropertyItemEntity>(cartSecond.getProductItems());
        assertEquals(cart.getId(), cartSecond.getId());
        assertEquals(userFirst.getId(), cartSecond.getUser());
        assertEquals(2, items.size());

        var itemFirst = items.stream().filter(i -> i.getProduct().equals(productFirst.getId())).findAny().get();
        var itemSecond = items.stream().filter(i -> i.getProduct().equals(productSecond.getId())).findAny().get();

        assertEquals(quantityFirst, itemFirst.getQuantity());
        assertEquals(quantitySecond, itemSecond.getQuantity());

        assertEquals(productFirst.getType(), itemFirst.getType());
        assertEquals(productSecond.getType(), itemSecond.getType());

        assertEquals(productFirst.getPrice(), itemFirst.getPrice());
        assertEquals(productSecond.getPrice(), itemSecond.getPrice());

        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantityFirst)), itemFirst.getTotal());
        assertEquals(productSecond.getPrice().multiply(BigDecimal.valueOf(quantitySecond)), itemSecond.getTotal());

        assertEquals(productFirst.getId(), itemFirst.getProduct());
        assertEquals(productSecond.getId(), itemSecond.getProduct());
    }

    @Test
    @Transactional
    public void testCreateOrUpdateCart_AddItemToCart_ThenUpdateItem() throws Exception {
        var quantityFirst = 2;
        var cartEntityFirst = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantityFirst);
        var quantitySecond = 4;
        var cartEntitySecond = cartService.createOrUpdateCart(userFirst, productSecond.getId(), quantitySecond);

        var quantityFirstUpdate = 5;
        var cartEntityFirstUpdated = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantityFirstUpdate);

        var items = new ArrayList<PropertyItemEntity>(cartEntitySecond.getProductItems());
        assertEquals(cartEntityFirst.getId(), cartEntitySecond.getId());
        assertEquals(cartEntityFirst.getId(), cartEntityFirstUpdated.getId());
        assertEquals(userFirst.getId(), cartEntityFirst.getUser());
        assertEquals(userFirst.getId(), cartEntitySecond.getUser());
        assertEquals(userFirst.getId(), cartEntityFirstUpdated.getUser());
        assertEquals(2, items.size());

        var itemFirst = items.stream().filter(i -> i.getProduct().equals(productFirst.getId())).findAny().get();
        var itemSecond = items.stream().filter(i -> i.getProduct().equals(productSecond.getId())).findAny().get();

        assertEquals(quantityFirstUpdate, itemFirst.getQuantity());
        assertEquals(quantitySecond, itemSecond.getQuantity());

        assertEquals(productFirst.getType(), itemFirst.getType());
        assertEquals(productSecond.getType(), itemSecond.getType());

        assertEquals(productFirst.getPrice(), itemFirst.getPrice());
        assertEquals(productSecond.getPrice(), itemSecond.getPrice());

        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantityFirstUpdate)), itemFirst.getTotal());
        assertEquals(productSecond.getPrice().multiply(BigDecimal.valueOf(quantitySecond)), itemSecond.getTotal());

        assertEquals(productFirst.getId(), itemFirst.getProduct());
        assertEquals(productSecond.getId(), itemSecond.getProduct());
    }


    @Test
    @Transactional
    public void loadCartsByUser() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantity);

        var cartLoadByUser = cartService.loadCartsByUser(userFirst.getId());
        var item = cartLoadByUser.getProductItems().stream().findFirst().get();

        assertEquals(cart.getId(), cartLoadByUser.getId());
        assertEquals(userFirst.getId(), cartLoadByUser.getUser());
        assertEquals(quantity, item.getQuantity());
        assertEquals(productFirst.getType(), item.getType());
        assertEquals(productFirst.getPrice(), item.getPrice());
        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantity)), item.getTotal());
        assertEquals(1, cartLoadByUser.getProductItems().size());
        assertEquals(cart.getProductItems().stream().findAny().get().getProduct(), item.getProduct());
    }

    @Test
    @Transactional
    public void testLoadCartsByUser_twoUser() throws Exception {
        var quantityFirstUser = 2;
        var cartFirstUser = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantityFirstUser);
        var quantitySecondUser = 5;
        var cartSecondUser = cartService.createOrUpdateCart(userSecond, productSecond.getId(), quantitySecondUser);

        var cartLoadByFirstUser = cartService.loadCartsByUser(userFirst.getId());
        var cartLoadBySecondUser = cartService.loadCartsByUser(userSecond.getId());

        var itemFirstUser = cartLoadByFirstUser.getProductItems().stream().findFirst().get();
        var itemSecondUser = cartLoadBySecondUser.getProductItems().stream().findFirst().get();

        assertEquals(cartFirstUser.getId(), cartLoadByFirstUser.getId());
        assertEquals(cartSecondUser.getId(), cartLoadBySecondUser.getId());

        assertEquals(userFirst.getId(), cartLoadByFirstUser.getUser());
        assertEquals(userSecond.getId(), cartLoadBySecondUser.getUser());

        assertEquals(quantityFirstUser, itemFirstUser.getQuantity());
        assertEquals(quantitySecondUser, itemSecondUser.getQuantity());

        assertEquals(productFirst.getType(), itemFirstUser.getType());
        assertEquals(productSecond.getType(), itemSecondUser.getType());

        assertEquals(productFirst.getPrice(), itemFirstUser.getPrice());
        assertEquals(productSecond.getPrice(), itemSecondUser.getPrice());

        assertEquals(productFirst.getPrice().multiply(BigDecimal.valueOf(quantityFirstUser)), itemFirstUser.getTotal());
        assertEquals(productSecond.getPrice().multiply(BigDecimal.valueOf(quantitySecondUser)), itemSecondUser.getTotal());

        assertEquals(1, cartLoadByFirstUser.getProductItems().size());
        assertEquals(1, cartLoadBySecondUser.getProductItems().size());

        assertEquals(cartFirstUser.getProductItems().stream().findAny().get().getProduct(), itemFirstUser.getProduct());
        assertEquals(cartSecondUser.getProductItems().stream().findAny().get().getProduct(), itemSecondUser.getProduct());
    }

    @Test
    @Transactional
    public void testDeleteCart() throws Exception {
        var quantity = 2;
        var cart = cartService.createOrUpdateCart(userFirst, productFirst.getId(), quantity);

        cartService.deleteCart(cart.getId());

        var cartDeleted = cartRepository.existsById(cart.getId());
        assertFalse(cartDeleted);
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
