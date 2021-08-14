package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.OrderEntity;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "order")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderService {

    public void createOrder(UserEntity user, Long cart) {
        var orderEntity = getOrderByUser(user.getId());
        var cartEntity = cartService.loadCart(cart);
        var discount = Optional.of(discountService.applyPromotion(cartEntity))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElse(new DiscountService.DiscountEntity());

        orderEntity.ifPresent(entity -> changeStatusOrder(entity.getId(), OrderEntity.Status.CANCEL));

        var ready = new OrderEntity(user.getId(), cartEntity.calculateTotal().subtract(discount.getRate()),
            discount.getRate(), cartEntity.calculateTotal(), OrderEntity.Status.OPEN,
            cart, user.getId(), new Date(), new Date());
        var save = orderRepository.save(ready);
        log.debug("The order has been persisted: ({}).", save);
    }


    @Transactional(readOnly = true)
    public OrderEntity loadOrder(Long order) {
        return Optional.of(orderRepository.findById(order))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException(String.format("Order id (%s) not found for user.", order)));
    }

    @Transactional(readOnly = true)
    public OrderEntity loadCurrentOrderByUser(Long user) {
        return Optional.of(orderRepository.findByUserAndStatus(user, OrderEntity.Status.OPEN))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException(String.format("Order by user (%s) not found.", user)));
    }


    @Transactional(readOnly = true)
    public Page<OrderEntity> loadAllUserOrder(Long user, Pageable pageable) {
        return orderRepository.findAllByUserAndStatusIn(user, List.of(OrderEntity.Status.OPEN), pageable);
    }

    @Transactional(readOnly = true)
    public Optional<OrderEntity> getOrderByUser(Long user) {
        return orderRepository.findByUserAndStatus(user, OrderEntity.Status.OPEN);
    }


    private void changeStatusOrder(Long order, OrderEntity.Status status) {
        Optional.of(orderRepository.findById(order))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                var current = reply.getStatus();
                reply.setStatus(status);
                var save = orderRepository.save(reply);
                log.debug("The order status (from:{}) -> (to:{}) has been changed.", current, status);
                return save;
            })
            .orElseThrow(() -> new NotFoundException(String.format("Order id (%s) not found for user.", order)));
    }


    public void deleteOrder(Long order) {

    }


    private OrderRepository orderRepository;
    private DiscountService discountService;
    private CartService cartService;
}
