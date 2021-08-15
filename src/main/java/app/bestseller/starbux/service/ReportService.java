package app.bestseller.starbux.service;

import app.bestseller.starbux.repository.OrderRepository;
import app.bestseller.starbux.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;


/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "report")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportService {

    public Tuple loadTopSideProduct() {
        return productRepository.findTheTopSideProduct();
    }

    public void loadMostSideProducts() {

    }


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
}
