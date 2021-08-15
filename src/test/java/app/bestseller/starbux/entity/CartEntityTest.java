package app.bestseller.starbux.entity;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.UserEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Ebrahim Kh.
 */


public class CartEntityTest {


    @Test
    void testCartEntity_calculateTotal() throws Exception {
        var cartEntity = buildCartEntity();

        var total = cartEntity.calculateTotal();

        assertEquals(BigDecimal.valueOf(33), total);
    }

    @Test
    void testCartEntity_countTotalSide() throws Exception {
        var cartEntity = buildCartEntity();

        var sides = cartEntity.countTotalByType(ProductEntity.Type.SIDE);
        assertEquals(5, sides);

        var mains = cartEntity.countTotalByType(ProductEntity.Type.MAIN);
        assertEquals(4, mains);
    }

    @Test
    void testCartEntity_calculateTotalSidePrice() throws Exception {
        var cartEntity = buildCartEntity();

        var totalSidePrice = cartEntity.calculateTotalSidePrice();

        assertEquals(BigDecimal.valueOf(13), totalSidePrice);
    }

    @Test
    void testCartEntity_calculateTotalMainPrice() throws Exception {
        var cartEntity = buildCartEntity();

        var totalMainPrice = cartEntity.calculateTotalMainPrice();

        assertEquals(BigDecimal.valueOf(20), totalMainPrice);
    }

    @Test
    void testCartEntity_calculateMinimumItemPrice() throws Exception {
        var cartEntity = buildCartEntity();

        var minItemPrice = cartEntity.minItemPrice();

        assertEquals(BigDecimal.valueOf(2), minItemPrice);
    }


    private CartEntity buildCartEntity() {
        var cart = new CartEntity();
        cart.setStatus(CartEntity.Status.OPEN);
        cart.setUser(buildUserEntity().getId());
        cart.setProductItems(buildProductDetailEntity());
        return cart;
    }

    private Set<PropertyItemEntity> buildProductDetailEntity() {
        var productDetails = new HashSet<PropertyItemEntity>();
        var entityFirst = new PropertyItemEntity();
        entityFirst.setProduct(buildProductEntityFirst().getId());
        entityFirst.setQuantity(4);
        entityFirst.setType(buildProductEntityFirst().getType());
        entityFirst.setPrice(buildProductEntityFirst().getPrice());
        productDetails.add(entityFirst);

        var entitySecond = new PropertyItemEntity();
        entitySecond.setProduct(buildProductEntitySecond().getId());
        entitySecond.setQuantity(2);
        entitySecond.setType(buildProductEntitySecond().getType());
        entitySecond.setPrice(buildProductEntitySecond().getPrice());
        productDetails.add(entitySecond);

        var entityThird = new PropertyItemEntity();
        entityThird.setProduct(buildProductEntityThird().getId());
        entityThird.setQuantity(3);
        entityThird.setType(buildProductEntityThird().getType());
        entityThird.setPrice(buildProductEntityThird().getPrice());
        productDetails.add(entityThird);

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
        product.setType(ProductEntity.Type.SIDE);
        product.setPrice(BigDecimal.valueOf(2));
        product.setName("product_second_name");
        product.setDescription("product_second_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        return product;
    }

    private ProductEntity buildProductEntityThird() {
        var product = new ProductEntity();
        product.setType(ProductEntity.Type.SIDE);
        product.setPrice(BigDecimal.valueOf(3));
        product.setName("product_third_name");
        product.setDescription("product_third_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        return product;
    }

    private UserEntity buildUserEntity() {
        var user = new UserEntity();
        user.setId(10L);
        user.setUsername("username");
        user.setName("first_name");
        user.setFamily("last_family");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setEmail("email@email.com");
        return user;
    }

}
