package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class ProductServiceTest {
    private @Autowired
    ProductService productService;
    private @Autowired
    ProductRepository productRepository;
    private @Autowired
    CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();
        cacheManager.getCacheNames().parallelStream().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    @Transactional
    public void testCreateProduct() throws Exception {
        var product = buildProductEntity();
        var save = productService.createProduct(product.getName(), product.getDescription(), product.getPrice(), product.getStatus(), product.getType());

        var productById = productRepository.getById(save.getId());
        assertEquals(save.getId(), productById.getId());
        assertEquals(save.getName(), productById.getName());
        assertEquals(save.getDescription(), productById.getDescription());
        assertEquals(save.getPrice(), productById.getPrice());
        assertEquals(save.getType(), productById.getType());
        assertEquals(save.getStatus(), productById.getStatus());
    }

    @Test
    @Transactional
    public void testLoadProduct() throws Exception {
        var product = buildProductEntity();
        var save = productService.createProduct(product.getName(), product.getDescription(), product.getPrice(), product.getStatus(), product.getType());

        var productLoad = productService.loadProduct(save.getId());
        assertEquals(save.getId(), productLoad.getId());
        assertEquals(save.getName(), productLoad.getName());
        assertEquals(save.getDescription(), productLoad.getDescription());
        assertEquals(save.getPrice(), productLoad.getPrice());
        assertEquals(save.getType(), productLoad.getType());
        assertEquals(save.getStatus(), productLoad.getStatus());
    }

    @Test
    @Transactional
    public void testLoadAllProducts() throws Exception {
        var product = buildProductEntity();
        productService.createProduct(product.getName(), product.getDescription(), product.getPrice(), product.getStatus(), product.getType());

        var products = productService.loadProducts(Pageable.ofSize(20));

        assertEquals(products.getContent().size(), 1);
        assertEquals(products.getTotalElements(), 1);
    }

    @Test
    @Transactional
    public void testEditProduct() throws Exception {
        var product = buildProductEntity();
        var save = productService.createProduct(product.getName(), product.getDescription(), product.getPrice(), product.getStatus(), product.getType());

        var edit = new ProductEntity();
        edit.setStatus(ProductEntity.Status.DISCONTINUE);
        edit.setName("product_edit_name");
        edit.setDescription("product_edit_description");
        edit.setPrice(BigDecimal.valueOf(10));
        edit.setType(ProductEntity.Type.SIDE);
        var editSave = productService.editProduct(save.getId(), edit.getName(), edit.getDescription(), edit.getPrice(), edit.getStatus(), edit.getType()).get();

        assertEquals(save.getId(), editSave.getId());
        assertEquals(edit.getName(), editSave.getName());
        assertEquals(edit.getDescription(), editSave.getDescription());
        assertEquals(edit.getStatus(), editSave.getStatus());
        assertEquals(edit.getType(), editSave.getType());
        assertEquals(edit.getPrice(), editSave.getPrice());
    }

    @Test
    @Transactional
    public void testDeleteProduct() throws Exception {
        var product = buildProductEntity();
        var save = productService.createProduct(product.getName(), product.getDescription(), product.getPrice(), product.getStatus(), product.getType());

        productService.deleteProduct(save.getId());

        var productDeleted = productRepository.existsById(save.getId());
        assertFalse(productDeleted);
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
}
