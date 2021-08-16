package app.bestseller.starbux.controller;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.OrderEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.CartRepository;
import app.bestseller.starbux.repository.OrderRepository;
import app.bestseller.starbux.repository.ProductRepository;
import app.bestseller.starbux.repository.UserRepository;
import app.bestseller.starbux.service.CartService;
import app.bestseller.starbux.service.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
public class ReportControllerTest {
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
    private @Autowired
    CartService cartService;

    @BeforeEach
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .build();

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
    void testGetReportTopSoldSide_whenValidInput_thenReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/report/admin/product/top/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.product").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.product").value(productSecond.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(5));
    }

    @Test
    @Transactional
    void testGetReportUsersTotalAmount_whenValidInput_thenReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/report/admin/user/amount/")
            .contentType(MediaType.APPLICATION_JSON)
            .param("size", "20")
            .param("page", "1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].user").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].total").exists())

            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].user").value(
                user.getId()
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].total").value(
                cartFirst.calculateTotal().add(cartSecond.calculateTotal()).doubleValue()
            ));
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
