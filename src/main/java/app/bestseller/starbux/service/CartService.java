package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.CartEntity;
import app.bestseller.starbux.domain.PropertyItemEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.HashSet;
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
    public CartEntity createOrUpdateCart(UserEntity user, Long productId, Integer quantity) {
        var cartEntity = cartRepository.findByUserAndStatusIn(user.getId(), List.of(CartEntity.Status.OPEN));
        var product = productService.loadProduct(productId);
        if (ObjectUtils.isEmpty(cartEntity)) cartEntity = CartEntity.getBasicCart(user.getId());

        var productItemsMap = new HashMap<Long, PropertyItemEntity>();
        cartEntity.getProductItems().forEach(item -> productItemsMap.put(item.getProduct(), item));

        if (productItemsMap.containsKey(product.getId())) {
            var item = productItemsMap.get(product.getId());
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            item.setType(product.getType());
            item.setTotal(item.getTotal());
            productItemsMap.put(product.getId(), item);
        }
        var item = PropertyItemEntity
            .getBasicProperty(product.getId(), quantity, product.getPrice(), product.getType());
        productItemsMap.put(product.getId(), item);

        cartEntity.setProductItems(new HashSet<>(productItemsMap.values()));
        var save = cartRepository.save(cartEntity);
        log.debug("The cart has been persisted: ({})", save);
        return save;
    }

    @Transactional(readOnly = true)
    public CartEntity loadCart(Long cart) {
        return Optional.of(cartRepository.findById(cart))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException(String.format("Your cart ( %s ) has not been found.", cart)));
    }

    @Transactional(readOnly = true)
    public CartEntity loadCartsByUser(Long user) {
        return cartRepository.findByUserAndStatusIn(user, List.of(CartEntity.Status.OPEN));
    }

    @Transactional
    public void deleteCart(Long cart) {
        cartRepository
            .findById(cart)
            .ifPresent(
                entity -> {
                    cartRepository.delete(entity);
                    log.debug("Deleted cart: ({}).", entity);
                }
            );
    }

    private final ProductService productService;
    private final CartRepository cartRepository;
}
