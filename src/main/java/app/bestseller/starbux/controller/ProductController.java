package app.bestseller.starbux.controller;

import app.bestseller.starbux.controller.validator.PaginationValidator;
import app.bestseller.starbux.domain.ProductEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.service.ProductService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/product", produces = "application/json")
public class ProductController {
    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/")
    public ResponseEntity<ProductReply> getProduct(@PathVariable("id") Long product) {
        var reply = productService.loadProduct(product);
        return ResponseEntity.ok(
            new ProductReply(reply.getId(),
                reply.getName(),
                reply.getDescription(),
                reply.getPrice().doubleValue(),
                reply.getStatus().name(),
                reply.getType().name(),
                reply.getCreated(),
                reply.getChanged())
        );
    }

    @GetMapping("/all/")
    public ResponseEntity<Page<ProductReply>> loadAllProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "20") int size
    ) throws BadRequestException {
        var products = productService.loadProducts(PaginationValidator.validatePaginationOrThrow(page, size));
        var reply = products.map(product ->
            new ProductReply(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().doubleValue(),
                product.getStatus().name(),
                product.getType().name(),
                product.getCreated(),
                product.getChanged()
            ));
        return ResponseEntity.ok(reply);
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @PostMapping("/admin/create/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@Valid @RequestBody ProductRequest request) throws BadRequestException {
        productService.createProduct(
            request.getName(),
            request.getDescription(),
            BigDecimal.valueOf(request.getPrice()),
            ProductEntity.Status.valueOf(request.getStatus()),
            ProductEntity.Type.valueOf(request.getType())
        );
    }

    @PutMapping("/admin/{id}/edit/")
    @ResponseStatus(HttpStatus.OK)
    public void editProduct(@PathVariable("id") Long product, @Valid @RequestBody ProductRequest request) {
        productService.editProduct(product,
            request.getName(),
            request.getDescription(),
            BigDecimal.valueOf(request.getPrice()),
            ProductEntity.Status.valueOf(request.getStatus()),
            ProductEntity.Type.valueOf(request.getType())
        );
    }

    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable("id") Long product) {
        productService.deleteProduct(product);
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class ProductRequest {
        private String name;
        private String description;
        private Double price;
        private String status;
        private String type;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class ProductReply {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private String status;
        private String type;
        private Date created;
        private Date changed;
    }

    private final ProductService productService;
}
