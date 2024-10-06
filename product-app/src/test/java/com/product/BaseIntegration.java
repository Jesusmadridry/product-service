package com.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.mapper.ProductMapper;
import com.product.repository.CategoryRepository;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.reset;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest(classes = ProductServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
public class BaseIntegration {
    @Autowired
    protected ObjectMapper objectMapper;

    @SpyBean
    protected ProductMapper productMapper;

    @SpyBean
    protected ProductService productService;

    @SpyBean
    protected ProductRepository productRepository;

    @SpyBean
    protected CategoryRepository categoryRepository;


    @BeforeAll
    void setUp(){
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        reset(productService);
        reset(productRepository);
        reset(categoryRepository);
    }

}