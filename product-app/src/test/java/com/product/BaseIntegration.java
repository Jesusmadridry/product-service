package com.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.mapper.ProductMapper;
import com.product.repository.CategoryRepository;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

import static org.mockito.Mockito.reset;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest(classes = ProductServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@Slf4j
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


    @BeforeEach
    void setUp(){
        productRepository.deleteAll();
        reset(productService);
        reset(productRepository);
    }

    protected String jsonContent(String name) {
        String srcPath = String.format("samples/%s.json", name);
        log.info("Loading response from {}", srcPath);

        InputStream src = getClass().getClassLoader().getResourceAsStream(srcPath);
        Objects.requireNonNull(src, "Unable to find " + srcPath);

        Scanner scanner = new Scanner(new InputStreamReader(src)).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}