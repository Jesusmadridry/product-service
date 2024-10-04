package com.product.controller;

import com.product.model.ProductResponse;
import com.product.model.ProductView;
import com.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    public Mono<ProductResponse> registerNewProduct(@RequestBody  ProductView productView){
        return productService.registerNewProduct(productView);
    }
}
