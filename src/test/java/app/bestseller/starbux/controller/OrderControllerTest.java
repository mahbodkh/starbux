package app.bestseller.starbux.controller;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.OrderEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.CartRepository;
import app.bestseller.starbux.repository.OrderRepository;
import app.bestseller.starbux.repository.ProductRepository;
import app.bestseller.starbux.repository.UserRepository;
import app.bestseller.starbux.service.DiscountService;
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
import java.util.Date;
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
public class OrderControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper
        objectMapper = new ObjectMapper();

    private @Autowired
    ProductRepository productRepository;
    private @Autowired
    CartRepository cartRepository;
    private @Autowired
    UserRepository userRepository;
    private @Autowired
    OrderRepository orderRepository;
    private @Autowired
    DiscountService discountService;

    @BeforeEach
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .build();

        user = userRepository.save(buildUserEntityFirst());
        productFirst = productRepository.save(buildProductEntityFirst());
        productSecond = productRepository.save(buildProductEntitySecond());
        cart = cartRepository.save(buildCartEntity());
        discount = discountService.applyPromotion(cart);
    }


    @Test
    @Transactional
    void testPostCreateOrder_whenValidInput_thenReturn() throws Exception {
        var orderRequest = new OrderController.OrderRequest();
        ReflectionTestUtils.setField(orderRequest, "cart", cart.getId());

        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/order/create/" + user.getId() + "/user/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void testGetOrder_whenValidInput_thenReturnAndExpectedResponse() throws Exception {
        var save = orderRepository.save(buildOrderEntity());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/order/" + user.getId() + "/user/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(save.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(save.getUser()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cart").value(save.getCart()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(save.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.discount")
                .value(discount.getRate().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total")
                .value(cart.calculateTotal().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.price")
                .value(cart.calculateTotal().subtract(discount.getRate()).doubleValue()));
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
        cart.setProductItems(buildProductItemEntity());
        return cart;
    }


    private Set<PropertyItemEntity> buildProductItemEntity() {
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

    private UserEntity user;
    private ProductEntity productFirst;
    private ProductEntity productSecond;
    private CartEntity cart;
    private DiscountService.DiscountEntity discount;
}
