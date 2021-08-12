package app.bestseller.starbux.controller;

import app.bestseller.starbux.controller.validator.UserUtils;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.service.CartService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/cart", produces = "application/json")
public class CartController {

    // ==============================================
    //                     CLIENT
    // ==============================================
    @PostMapping("/create/")
    @ResponseStatus(HttpStatus.OK)
    public void createCart(@Valid @RequestBody CartController.CartRequest request) throws BadRequestException {
        var user = userService.loadByUsername(UserUtils.getAuthenticatedUsername());
        if (ObjectUtils.isEmpty(user))
            throw new NotFoundException("Your account has not found.");
        cartService.createOrUpdateCart(user, request.getProduct(), request.getQuantity());
    }

    @GetMapping("/")
    public ResponseEntity<CartReply> getCurrentCart() {
        var user = userService.loadByUsername(UserUtils.getAuthenticatedUsername());
        if (ObjectUtils.isEmpty(user))
            throw new NotFoundException("Your account has not found.");
        var reply = cartService.loadCartsByUser(user.getId());
        var orderProductReplies = new ArrayList<OrderProductReply>();
        reply.getProductItems().forEach(p ->
            orderProductReplies.add(new OrderProductReply(
                p.getProduct(),
                p.getQuantity(),
                p.getPrice().doubleValue(),
                p.getType().name())));
        return ResponseEntity.ok(
            new CartReply(reply.getId(),
                reply.getUser(),
                orderProductReplies,
                reply.getStatus().name(),
                reply.getCreated(),
                reply.getChanged()
            ));
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCart(@PathVariable("id") Long cart) {
        cartService.deleteCart(cart);
    }

    @GetMapping("/{id}/")
    public ResponseEntity<CartReply> getCart(@PathVariable("id") Long cart) {
        var reply = cartService.loadCart(cart);
        var orderProductReplies = new ArrayList<OrderProductReply>();
        reply.getProductItems().forEach(p ->
            orderProductReplies.add(new OrderProductReply(
                p.getProduct(),
                p.getQuantity(),
                p.getPrice().doubleValue(),
                p.getType().name())));
        return ResponseEntity.ok(
            new CartReply(reply.getId(),
                reply.getUser(),
                orderProductReplies,
                reply.getStatus().name(),
                reply.getCreated(),
                reply.getChanged()
            ));
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class CartRequest {
        private Long product;
        private Integer quantity;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    public static class CartReply {
        private Long id;
        private Long user;
        private List<OrderProductReply> orderProducts = Collections.emptyList();
        private String status;
        private Date created;
        private Date changed;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    public static class OrderProductReply {
        private Long product;
        private Integer quantity;
        private Double price;
        private String type;
    }

    private final CartService cartService;
    private final UserService userService;
}
