package com.product.controller;

import com.product.model.ProductResponse;
import com.product.model.ProductView;
import com.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public Mono<ProductResponse> registerNewProduct(@Valid @RequestBody  ProductView productView){
        return productService.registerNewProduct(productView);
    }

    @PutMapping
    public Mono<ProductResponse> updateProduct(@Valid @RequestBody  ProductView productView){
        return productService.modifyProduct(productView);
    }
}
