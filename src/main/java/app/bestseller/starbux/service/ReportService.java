package app.bestseller.starbux.service;

import app.bestseller.starbux.repository.OrderRepository;
import app.bestseller.starbux.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;


/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "report")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportService {

    @Transactional(readOnly = true)
    public Tuple loadTopSideProduct() {
        return reportRepository.findTheTopSideProduct(1);
    }

    public void loadMostSideProducts() {

    }

    private final ReportRepository reportRepository;
}
