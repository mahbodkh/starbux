package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.CartProductDetailEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "order")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CartService {

    @Transactional
    public void createCart(UserEntity user, Long productId, Integer quantity) {
        var orderEntity = cartRepository.findByUserAndStatusIn(user.getId(), List.of(CartEntity.Status.OPEN));
        if (ObjectUtils.isEmpty(orderEntity)) {
            orderEntity = CartEntity.getBasicCart(user.getId(), List.of());
        }
        var product = productService.loadProduct(productId);
        var productDetailEntity = new CartProductDetailEntity();
        productDetailEntity.setProduct(product.getId());
        productDetailEntity.setPrice(product.getPrice());
        productDetailEntity.setType(product.getType());
        productDetailEntity.setQuantity(quantity);

        var detailEntities = orderEntity.getDetailEntities();
        detailEntities.add(productDetailEntity);
        orderEntity.setDetailEntities(detailEntities);

        var save = cartRepository.save(orderEntity);
        log.debug("The cart has been persisted: ({})", save);
    }


    @Transactional(readOnly = true)
    @Cacheable(key = "'cartById/' + #cart.toString()")
    public CartEntity loadCart(Long cart) {
        return Optional.of(cartRepository.findById(cart))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException(String.format("Your cart ( %s ) has not been found.", cart)));
    }

    @Caching(evict = {
        @CacheEvict(key = "'cartById/' + #cart.toString()")
    })
    @Transactional
    public Optional<CartEntity> editCart(Long cart, Long product, Integer quantity, Boolean isAdmin) {
        return Optional.of(cartRepository.findById(cart))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                if (!CartEntity.Status.OPEN.equals(reply.getStatus())) {
                    throw new NotFoundException(String.format("Your cart (%s) has been expired, you should create new cart.", cart));
                }
                var productEntity = productService.loadProduct(product);
                reply.getDetailEntities().stream()
                    .filter(p -> product.equals(p.getProduct()))
                    .forEach(p -> {
                        if (!quantity.equals(p.getQuantity())) p.setQuantity(quantity);
                        if (!productEntity.getPrice().equals(p.getPrice())) p.setPrice(productEntity.getPrice());
                    });

                var save = cartRepository.save(reply);
                log.debug("The cart has updated: {}", save);
                return save;
            });
    }


    public CartEntity loadCartsByUser(Long user) {
        return null;
    }

    public CartEntity loadCurrentCartByUser(UserEntity user) {
        return cartRepository.findByUserAndStatusIn(user.getId(), List.of(CartEntity.Status.OPEN));
    }

    @CacheEvict(key = "'cartById/' + #cart.toString()")
    @Transactional
    public void deleteCart(Long cart) {
        cartRepository
            .findById(cart)
            .ifPresent(
                entity -> {
                    cartRepository.delete(entity);
                    log.debug("Deleted User: {}", entity);
                }
            );
    }


    private final ProductService productService;
    private final CartRepository cartRepository;
}
