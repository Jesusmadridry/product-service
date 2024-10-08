package com.product.config;

import com.common.persist.filter.ReactiveAuditorProvider;
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


    /**
    *  This bean will activate the feature of capturing the username once the principal is authenticated.
     *  It will update the createdBy and LastModifiedBy fields
    * */
    @Bean
    public ReactiveAuditorProvider reactiveAuditorProvider() {
        return new ReactiveAuditorProvider();
    }
}
