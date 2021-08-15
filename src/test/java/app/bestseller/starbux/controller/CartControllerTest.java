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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
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

    private @Autowired
    CartRepository cartRepository;
    private @Autowired
    UserRepository userRepository;
    private @Autowired
    ProductRepository productRepository;

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
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(4);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());

        var entitySecond = new PropertyItemEntity();
        entitySecond.setProduct(productSecond.getId());
        entitySecond.setQuantity(2);
        entitySecond.setType(productSecond.getType());
        entitySecond.setPrice(productSecond.getPrice());

        var save = cartRepository.save(buildCartEntity(Set.of(entityFirst, entitySecond)));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cart/" + save.getId() + "/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(save.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(save.getUser()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(save.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts").isNotEmpty())

            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]product").value(entityFirst.getProduct()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]quantity").value(entityFirst.getQuantity()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]price").value(entityFirst.getPrice().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]type").value(entityFirst.getType().name()))

            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[1]product").value(entitySecond.getProduct()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[1]quantity").value(entitySecond.getQuantity()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[1]price").value(entitySecond.getPrice().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[1]type").value(entitySecond.getType().name()));
    }

    @Test
    @Transactional
    void testGetCartByUser_whenValidInput_thenReturn() throws Exception {
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(4);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());

        var save = cartRepository.save(buildCartEntity(Set.of(entityFirst)));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/cart/admin/" + userFirst.getId() + "/user/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(save.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(save.getUser()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(save.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts").isNotEmpty())

            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]product").value(entityFirst.getProduct()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]quantity").value(entityFirst.getQuantity()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]price").value(entityFirst.getPrice().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderProducts.[0]type").value(entityFirst.getType().name()));
    }

    @Test
    @Transactional
    void testDeleteCart_whenValidInput_thenReturn() throws Exception {
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(productFirst.getId());
        entityFirst.setQuantity(4);
        entityFirst.setType(productFirst.getType());
        entityFirst.setPrice(productFirst.getPrice());

        var save = cartRepository.save(buildCartEntity(Set.of(entityFirst)));

        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/product/admin/" + save.getId() + "/delete/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private CartEntity buildCartEntity(Set<PropertyItemEntity> items) {
        var cart = new CartEntity();
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(userFirst.getId());
        cart.setProductItems(items);
        return cart;
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
