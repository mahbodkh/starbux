package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "product")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

    @Transactional
    @Cacheable(key = "'productByName/' + #name")
    public ProductEntity createProduct(String name, String description, BigDecimal price, ProductEntity.Status status, ProductEntity.Type type) {
        var product = ProductEntity.getBasicProduct(name, description, price, status, type);
        var save = productRepository.save(product);
        log.debug("The product has been persisted: ({})", save);
        return save;
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'productById/' + #product.toString()")
    public ProductEntity loadProduct(Long product) {
        return Optional.of(productRepository.findById(product))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException("The product (" + product + ") not found."));
    }


    @Transactional(readOnly = true)
    public Page<ProductEntity> loadProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Caching(evict = {
        @CacheEvict(key = "'productById/' + #product.toString()"),
        @CacheEvict(key = "'productByName/' + #name"),
    })
    @Transactional
    public Optional<ProductEntity> editProduct(Long product, String name, String description, BigDecimal price, ProductEntity.Status status, ProductEntity.Type type) {
        return Optional.of(productRepository.findById(product))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                if (name != null && !name.isEmpty() && !name.isBlank())
                    reply.setName(name);
                if (description != null && !description.isEmpty() && !description.isBlank())
                    reply.setDescription(description);
                if (price != null && !price.equals(BigDecimal.ZERO))
                    reply.setPrice(price);
                if (status != null)
                    reply.setStatus(status);
                if (type != null)
                    reply.setType(type);
                var save = productRepository.save(reply);
                log.debug("The product has been edited: {}", save);
                return save;
            });
    }


    @CacheEvict(key = "'productById/' + #product.toString()")
    @Transactional
    public void deleteProduct(Long product) {
        productRepository
            .findById(product)
            .ifPresent(
                entity -> {
                    productRepository.delete(entity);
                    log.debug("Deleted product: {}", entity);
                }
            );
    }

    private final ProductRepository productRepository;
}
