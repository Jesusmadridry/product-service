package com.product.service;

import com.product.model.ProductResponse;
import com.product.model.ProductView;
import com.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Mono<ProductResponse> registerNewProduct(ProductView productView){
        return Mono.empty();
    }

    public Mono<ProductResponse> modifyProduct(ProductView productView){
        return Mono.empty();
    }

    public Mono<ProductResponse> deleteProduct(UUID internalProductId){
        return Mono.empty();
    }
}
