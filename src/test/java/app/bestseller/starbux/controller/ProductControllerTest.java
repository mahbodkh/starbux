package app.bestseller.starbux.controller;

import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.repository.ProductRepository;
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
import java.math.RoundingMode;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Ebrahim Kh.
 */


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper
        objectMapper = new ObjectMapper();

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .build();
    }


    @Test
    @Transactional
    void testGetProduct_whenValidInput_thenReturnsAndExpectResponse() throws Exception {
        var save = productRepository.save(buildProductEntityFirst());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/product/" + save.getId() + "/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(save.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(save.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(save.getDescription()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(save.getPrice().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(save.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(save.getType().name()));
    }

    @Test
    @Transactional
    void testGetAllProducts_whenValidInput_thenReturnsAndExpectResponses() throws Exception {
        var saveFirst = productRepository.save(buildProductEntityFirst());
        var saveSecond
            = productRepository.save(buildProductEntitySecond());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/product/all/")
            .contentType(MediaType.APPLICATION_JSON)
            .param("size", "20")
            .param("page", "1")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())

            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(20))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(2))

            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]id").value(saveFirst.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].name").value(saveFirst.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].description").value(saveFirst.getDescription()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].price").value(saveFirst.getPrice().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].status").value(saveFirst.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].type").value(saveFirst.getType().name()))

            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].id").value(saveSecond.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].name").value(saveSecond.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].description").value(saveSecond.getDescription()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].price").value(saveSecond.getPrice().doubleValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].status").value(saveSecond.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].type").value(saveSecond.getType().name()));
    }


    @Test
    @Transactional
    void testPostCreateProduct_whenValidInput_thenReturnsAndExpectResponse() throws Exception {
        var productEntity = buildProductEntityFirst();

        var productRequest = new ProductController.ProductRequest();
        ReflectionTestUtils.setField(productRequest, "name", productEntity.getName());
        ReflectionTestUtils.setField(productRequest, "description", productEntity.getDescription());
        ReflectionTestUtils.setField(productRequest, "price", productEntity.getPrice().doubleValue());
        ReflectionTestUtils.setField(productRequest, "status", productEntity.getStatus().name());
        ReflectionTestUtils.setField(productRequest, "type", productEntity.getType().name());

        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/product/admin/create/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void testPutEditProduct_whenValidInput_thenReturns() throws Exception {
        var save = productRepository.save(buildProductEntityFirst());

        var editRequest = new ProductController.ProductRequest();
        ReflectionTestUtils.setField(editRequest, "name", "name_edited");
        ReflectionTestUtils.setField(editRequest, "description", "description_edited");

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/product/admin/" + save.getId() + "/edit/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testDeleteProduct_whenValidInput_thenReturns() throws Exception {
        var save = productRepository.save(buildProductEntityFirst());

        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/product/admin/" + save.getId() + "/delete/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }


    private ProductEntity buildProductEntityFirst() {
        var product = new ProductEntity();
        product.setName("product_name_fist");
        product.setDescription("product_description_second");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        product.setPrice(BigDecimal.valueOf(4).setScale(2, RoundingMode.HALF_UP));
        product.setType(ProductEntity.Type.MAIN);
        return product;
    }

    private ProductEntity buildProductEntitySecond() {
        var product = new ProductEntity();
        product.setName("product_name_second");
        product.setDescription("product_description_second");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        product.setPrice(BigDecimal.valueOf(5).setScale(2, RoundingMode.HALF_UP));
        product.setType(ProductEntity.Type.MAIN);
        return product;
    }
}
