package com.product.service;

import com.product.BaseIntegration;
import com.product.model.ProductView;
import com.product.persist.ProductEntity;
import com.product.persist.common.Category;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProductServiceTest extends BaseIntegration {

    @Test
    void successProduct_Create() throws InterruptedException {
        // GIVEN
        prepareCategoryRecords();
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var productView = ProductView. builder()
                        .name("Laptop")
                        .categoryType(1)
                        .code("AAB")
                        .companyOwner("ABC Company Records")
                        .description("Laptop 1234f Amazing")
                        .build();

        // WHEN
        productService.registerNewProduct(productView)
            .doOnTerminate(countDownLatch::countDown);

        countDownLatch.await(4, TimeUnit.SECONDS);

        // THEN
        var productCreated = productRepository.findByCode(productView.getCode()).get();

        assertNotNull(productCreated);
        assertEquals("AAB", productCreated.getCode());
        assertNotNull(productCreated.getExternalRef());
        assertEquals("ABC Company Records", productCreated.getCompanyOwner());
        log.info("Product Created {}", productCreated);
    }

    private void prepareCategoryRecords(){
        var newCategories =
                List.of(Category.builder()
                            .name("Electronics")
                            .build(),
                        Category.builder()
                            .name("Clothing")
                            .build());
        categoryRepository.saveAll(newCategories);
    }

}
