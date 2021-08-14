package app.bestseller.starbux.service;

import app.bestseller.starbux.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by Ebrahim Kh.
 */


@SpringBootTest
public class OrderServiceTest {
    private @Autowired
    OrderService orderService;
    private @Autowired
    OrderRepository orderRepository;


}
