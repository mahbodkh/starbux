package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;


    @Test
    @Transactional
    public void testCreateProduct() throws Exception {

    }

    @Test
    @Transactional
    public void testLoadProduct() throws Exception {

    }

    @Test
    @Transactional
    public void testLoadAllProducts() throws Exception {

    }

    @Test
    @Transactional
    public void testEditProduct() throws Exception {

    }

    @Test
    @Transactional
    public void testDeleteProduct() throws Exception {

    }


    private ProductEntity buildProductEntity() {
        var product = new ProductEntity();
        product.setName("product_name");
        product.setDescription("product_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        product.setPrice(BigDecimal.valueOf(4));
        product.setType(ProductEntity.Type.MAIN);
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

    private ProductEntity product;
}
