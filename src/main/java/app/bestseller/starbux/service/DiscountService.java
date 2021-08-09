package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.CartProductDetailEntity;
import app.bestseller.starbux.domain.DiscountEntity;
import app.bestseller.starbux.domain.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "discount")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscountService {
    private static final BigDecimal RATE_DISCOUNT_VALUE = BigDecimal.valueOf(0.25D);
    private static final BigDecimal RATE_DISCOUNT_CART_LIMIT = BigDecimal.valueOf(12);
    private static final Integer DISCOUNT_BY_AMOUNT_LIMIT = 3;


    public Optional<DiscountEntity> applyRules(CartEntity cart) {
        var discount = selectDiscountForCart(cart);
        if (ObjectUtils.isEmpty(discount)) {
            return Optional.empty();
        }
        return discount;
    }

    private Optional<DiscountEntity> selectDiscountForCart(CartEntity cart) {
        var rateDiscountAmount = getDiscountPercentage(cart);
        var amountDiscountAmount = getDiscountByLowestProduct(cart);
        var discount = new DiscountEntity();

        if (rateDiscountAmount.compareTo(BigDecimal.ZERO) == 0 && amountDiscountAmount.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("Discount is not available.");
            return Optional.empty();
        }
        if (rateDiscountAmount.compareTo(amountDiscountAmount) > 0) {
            discount.setPrice(rateDiscountAmount);
            discount.setType(DiscountEntity.Type.PERCENTAGE);
            return Optional.of(discount);
        }

        discount.setPrice(amountDiscountAmount);
        discount.setType(DiscountEntity.Type.PRICE);
        return Optional.of(discount);
    }

    protected BigDecimal getDiscountPercentage(CartEntity cart) {
        if (cart.calculateTotal().compareTo(RATE_DISCOUNT_CART_LIMIT) < 0) {
            return BigDecimal.ZERO;
        }
        return cart.calculateTotal().multiply(RATE_DISCOUNT_VALUE);
    }

    protected BigDecimal getDiscountByLowestProduct(CartEntity cart) {
        var mainProduct = cart.getDetailEntities().stream()
            .filter(p -> p.getType().equals(ProductEntity.Type.MAIN))
            .collect(Collectors.toList());
        if (mainProduct.size() < DISCOUNT_BY_AMOUNT_LIMIT) {
            return BigDecimal.ZERO;
        }
        return cart.getDetailEntities().stream()
            .min(Comparator.comparing(CartProductDetailEntity::getPrice))
            .orElseThrow(NoSuchElementException::new)
            .getPrice();
    }
}
