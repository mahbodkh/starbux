package app.bestseller.starbux.controller;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.CartRepository;
import app.bestseller.starbux.repository.ProductRepository;
import app.bestseller.starbux.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Ebrahim Kh.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CartControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper
        objectMapper = new ObjectMapper();

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .build();

        userFirst = userRepository.save(buildUserEntityFirst());
        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());
    }


    @Test
    @Transactional
    void testPostCreateCart_whenValidInput_thenReturn() throws Exception {
        var cartRequest = new CartController.CartRequest();
        ReflectionTestUtils.setField(cartRequest, "product", productFirst.getId());
        ReflectionTestUtils.setField(cartRequest, "quantity", 2);

        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/cart/create/" + userFirst.getId() + "/user/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cartRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void testGetCart_whenValidInput_thenReturnAndExpectedResponses() throws Exception {
//        "/v1/cart/{id}/"


    }

    @Test
    @Transactional
    void testGetCartByUser_whenValidInput_thenReturn() throws Exception {
//        "/v1/cart/admin/{id}/user/"


    }

    @Test
    @Transactional
    void testDeleteCart_whenValidInput_thenReturn() throws Exception {

        var save = cartRepository.save(buildCartEntity());

        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/product/admin/" + save.getId() + "/delete/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private CartEntity buildCartEntity() {
        var cart = new CartEntity();
        cart.setId(100L);
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(buildUserEntityFirst().getId());
        cart.setProductItems(buildProductItemEntity());
        return cart;
    }

    private Set<PropertyItemEntity> buildProductItemEntity() {
        var productDetails = new HashSet<PropertyItemEntity>();
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(buildUserEntityFirst().getId());
        entityFirst.setQuantity(4);
        entityFirst.setType(buildProductEntityFirst().getType());
        entityFirst.setPrice(buildProductEntityFirst().getPrice());
        productDetails.add(entityFirst);

        var entitySecond = new PropertyItemEntity();
        entitySecond.setProduct(buildUserEntitySecond().getId());
        entitySecond.setQuantity(2);
        entitySecond.setType(buildProductEntitySecond().getType());
        entitySecond.setPrice(buildProductEntitySecond().getPrice());
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
