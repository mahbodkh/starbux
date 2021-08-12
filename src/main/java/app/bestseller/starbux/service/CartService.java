package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.CartRepository;
import app.bestseller.starbux.repository.ProductPropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    @Cacheable(key = "'cartByUser/' + #user.toString()")
    public CartEntity createOrUpdateCart(UserEntity user, Long productId, Integer quantity) {
        var cartEntity = cartRepository.findByUserAndStatusIn(user.getId(), List.of(CartEntity.Status.OPEN));
        var product = productService.loadProduct(productId);
        if (ObjectUtils.isEmpty(cartEntity)) cartEntity = CartEntity.getBasicCart(user.getId());
        var productDetailLists = new HashSet<PropertyItemEntity>();

        final var cart = cartEntity;
        if (cartEntity.getProductItems().size() > 0) {
            cartEntity.getProductItems().stream()
                .map(props -> {
                    return productDetailLists.add(
                        productPropertyPersistence(props.getId(),
                            product.getId(), quantity, product.getPrice(), product.getType(), cart
                        ));
                });
        } else {
            productDetailLists.add(productPropertyPersistence(
                null, product.getId(), quantity, product.getPrice(), product.getType(), cart));
        }

        cartEntity.setProductItems(productDetailLists);
        var save = cartRepository.save(cartEntity);
        log.debug("The cart has been persisted: ({})", save);
        return save;
    }

    private PropertyItemEntity productPropertyPersistence(Long productProperty, Long product, Integer quantity, BigDecimal price, ProductEntity.Type type, CartEntity cart) {
        PropertyItemEntity propertyItemEntity = null;
        if (ObjectUtils.isEmpty(productProperty)) {
            propertyItemEntity = PropertyItemEntity.getBasicProperty(product, quantity, price, type, cart);
        }
//            propertyItemEntity = Optional.of(productPropertyRepository.findById(Objects.requireNonNull(productProperty)))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .filter(props -> product.equals(props.getProduct()))
//                .map(props -> {
//                    if (!ObjectUtils.isEmpty(quantity) && quantity > 0)
//                        props.setQuantity(quantity);
//                    if (!ObjectUtils.isEmpty(type))
//                        props.setType(type);
//                    if (!ObjectUtils.isEmpty(price) && price.compareTo(BigDecimal.ZERO) > 0)
//                        props.setPrice(price);
//                    props.setTotal(props.getTotal());
//                    return props;
//                }).get();

//        var save = productPropertyRepository.save(Objects.requireNonNull(propertyItemEntity));
//        log.debug("The cart details has been persisted: ({})", save);
//        return save;
        return propertyItemEntity;
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
                reply.getProductItems().stream()
                    .filter(p -> product.equals(p.getProduct()))
                    .forEach(p -> {
                        if (!quantity.equals(p.getQuantity())) p.setQuantity(quantity);
                        if (!productEntity.getPrice().equals(p.getPrice())) p.setPrice(productEntity.getPrice());
                    });

                clearCartCaches("'cartByUser/'" + reply.getUser().toString());
                var save = cartRepository.save(reply);
                log.debug("The cart has updated: {}", save);
                return save;
            });
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'cartById/' + #user.toString()")
    public CartEntity loadCartsByUser(Long user) {
        return cartRepository.findByUserAndStatusIn(user, List.of(CartEntity.Status.OPEN));
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
                    clearCartCaches("'cartByUser/'" + entity.getUser().toString());
                }
            );
    }

    private void clearCartCaches(String cache) {
        Objects.requireNonNull(cacheManager.getCache(cache)).evict(cache);
        log.debug("Cashes has been evicted by ({})", cache);
    }

    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CacheManager cacheManager;
    private final ProductPropertyRepository productPropertyRepository;
}
