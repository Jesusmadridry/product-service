package com.product.config;

import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProductConfig {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Bean
    public ProductService productService(){
        return new ProductService(productRepository, productMapper);
    }
}
