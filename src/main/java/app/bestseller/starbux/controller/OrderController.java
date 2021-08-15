package app.bestseller.starbux.controller;

import app.bestseller.starbux.controller.validator.UserUtils;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.service.DiscountService;
import app.bestseller.starbux.service.OrderService;
import app.bestseller.starbux.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/order", produces = "application/json")
public class OrderController {
    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OrderReply> getOrder(OrderRequest request) {
        var user = userService.loadByUsername(UserUtils.getAuthenticatedUsername());
        if (ObjectUtils.isEmpty(user))
            throw new NotFoundException("Your account has not found.");
        var order = orderService.loadCurrentOrderByUser(user.getId());
        return ResponseEntity.ok(
            new OrderReply(order.getId(),
                order.getUser(),
                order.getCart(),
                order.getPrice().doubleValue(),
                order.getDiscount().doubleValue(),
                order.getTotal().doubleValue(),
                order.getStatus().name(),
                order.getCreated(),
                order.getChanged()
            ));
    }

    @PostMapping("/create/")
    @ResponseStatus(HttpStatus.OK)
    public void createOrder(OrderRequest request) {
        var user = userService.loadByUsername(UserUtils.getAuthenticatedUsername());
        if (ObjectUtils.isEmpty(user))
            throw new NotFoundException("Your account has not found.");
        orderService.createOrder(user, request.getCart());
    }

    @PutMapping("/{id}/edit/")
    @ResponseStatus(HttpStatus.OK)
    public void editOrder(@PathVariable("id") Long order, @Valid @RequestBody OrderRequest request) {
//        orderService.updateOrder(order, request.getCart());
    }


    // ==============================================
    //                     ADMIN
    // ==============================================
    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@PathVariable("id") Long product) {
//        productService.deleteProduct(product);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class OrderRequest {
        private Long cart;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class OrderReply {
        private Long id;
        private Long user;
        private Long card;
        private Double price;
        private Double total;
        private Double discount;
        private String status;
        private Date created;
        private Date changed;
    }

    private final OrderService orderService;
    private final UserService userService;
}
