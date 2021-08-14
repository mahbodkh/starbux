package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

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


    public Optional<DiscountEntity> applyPromotion(CartEntity cart) {
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
            discount.setRate(rateDiscountAmount);
            discount.setType(DiscountEntity.Type.PERCENTAGE);
            return Optional.of(discount);
        }

        discount.setRate(amountDiscountAmount);
        discount.setType(DiscountEntity.Type.PRICE);
        return Optional.of(discount);
    }

    private BigDecimal getDiscountPercentage(CartEntity cart) {
        if (cart.calculateTotal().compareTo(RATE_DISCOUNT_CART_LIMIT) < 0) {
            return BigDecimal.ZERO;
        }
        return cart.calculateTotal().multiply(RATE_DISCOUNT_VALUE);
    }

    private BigDecimal getDiscountByLowestProduct(CartEntity cart) {
        if (cart.countTotalSide() < DISCOUNT_BY_AMOUNT_LIMIT) {
            return BigDecimal.ZERO;
        }
        return cart.minItemPrice();
    }

    @NoArgsConstructor
    @Data
    public static class DiscountEntity {

        private Type type;
        private BigDecimal rate;

        public enum Type {
            PERCENTAGE,
            PRICE
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var that = (DiscountEntity) o;
            return type == that.type && Objects.equals(rate, that.rate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, rate);
        }
    }
}
