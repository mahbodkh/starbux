package app.bestseller.starbux.controller;

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
    @PostMapping("/create/{id}/user/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCart(@PathVariable("id") Long user, @Valid @RequestBody CartController.CartRequest request) throws BadRequestException {
        var userEntity = userService.loadUser(user);
        if (ObjectUtils.isEmpty(userEntity))
            throw new NotFoundException("Your account has not found.");
        cartService.createOrUpdateCart(userEntity, request.getProduct(), request.getQuantity());
    }

    @GetMapping("/{id}/")
    public ResponseEntity<CartReply> getCurrentCart(@PathVariable("id") Long cart) {
        var reply = cartService.loadCart(cart);
        if (ObjectUtils.isEmpty(cart))
            throw new NotFoundException("Your cart not found.");
        var cartProductItemReplies = new ArrayList<CartProductItemReply>();
        reply.getProductItems().forEach(p ->
            cartProductItemReplies.add(new CartProductItemReply(
                p.getProduct(),
                p.getQuantity(),
                p.getPrice().doubleValue(),
                p.getType().name())));
        return ResponseEntity.ok(
            new CartReply(reply.getId(),
                reply.getUser(),
                cartProductItemReplies,
                reply.getStatus().name(),
                reply.getCreated(),
                reply.getChanged()
            ));
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @GetMapping("/admin/{id}/user/")
    public ResponseEntity<CartReply> getCart(@PathVariable("id") Long user) {
        var reply = cartService.loadCartsByUser(user);
        var cartProductItemReplies = new ArrayList<CartProductItemReply>();
        reply.getProductItems().forEach(p ->
            cartProductItemReplies.add(new CartProductItemReply(
                p.getProduct(),
                p.getQuantity(),
                p.getPrice().doubleValue(),
                p.getType().name())));
        return ResponseEntity.ok(
            new CartReply(reply.getId(),
                reply.getUser(),
                cartProductItemReplies,
                reply.getStatus().name(),
                reply.getCreated(),
                reply.getChanged()
            ));
    }

    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCart(@PathVariable("id") Long cart) {
        cartService.deleteCart(cart);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class CartRequest {
        private Long product;
        private Integer quantity;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class CartReply {
        private Long id;
        private Long user;
        private List<CartProductItemReply> productItems;
        private String status;
        private Date created;
        private Date changed;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class CartProductItemReply {
        private Long product;
        private Integer quantity;
        private Double price;
        private String type;
    }

    private final CartService cartService;
    private final UserService userService;
}
