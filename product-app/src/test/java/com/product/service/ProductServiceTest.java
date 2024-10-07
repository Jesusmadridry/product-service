package com.product.service;

import com.product.BaseIntegration;
import com.product.model.ProductResponse;
import com.product.model.ProductServiceException;
import com.product.model.ProductView;
import com.product.persist.ProductEntity;
import com.product.persist.common.Category;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.product.persist.model.Constant.EXISTING_PRODUCT_MESSAGE;
import static com.product.persist.model.Constant.MANDATORY_FIELD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * 1a. success_Product_Create -> it must receive a product view, convert into product entity, and be saved successfully
 * 1b. trying_to_create_alreadyExistingProduct -> this case will catch the code received is already associated in the product table
 * 1c. failed_createProcess_dueTo_NullPayload -> The service will throw an error, because it can deal with null payloads.
 *
 * 2a. success_Product_Update -> it must receive a product view, convert into product entity, and update already existing product
 */
@Slf4j
class ProductServiceTest extends BaseIntegration {
    private static final String UPDATE_NAME = "Laptop Top";
    private static final String UPDATE_DESCRIPTION = "Laptop Top Amazing";

    // 1a
    @Test
    void success_Product_Create() throws InterruptedException {
        // GIVEN
        prepareCategoryRecords();
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var productView = prepareProductView();

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
        verify(productRepository, times(1)).save(any(ProductEntity.class));
        log.info("Product Created {}", productCreated);
    }

    // 1b
    @Test
    void trying_to_create_alreadyExistingProduct() throws InterruptedException {
        // GIVEN
        prepareCategoryRecords();
        var propertyCreated = prepareProductEntity();
        reset(productRepository); // Cleaning the repository to avoid false positive inside the register new product method
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var productResponseCapturer = new AtomicReference<ProductResponse>();
        var productView = prepareProductView();

        // WHEN
        productService.registerNewProduct(productView)
            .doOnTerminate(countDownLatch::countDown)
            .subscribe(productResponseCapturer::set);
        countDownLatch.await(3, TimeUnit.SECONDS);

        // THEN
        assertNotNull(productResponseCapturer.get());
        assertEquals(EXISTING_PRODUCT_MESSAGE, productResponseCapturer.get().getMessage());
        assertEquals(propertyCreated.getExternalRef(), productResponseCapturer.get().getId());
        verify(productRepository, times(0)).save(any(ProductEntity.class));
    }

    // 1c
    @Test
    void failed_createProcess_dueTo_NullPayload() throws InterruptedException {
        // GIVEN
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var productResponseCapturer = new AtomicReference<String>();

        // WHEN
        productService.registerNewProduct(null)
            .doOnTerminate(countDownLatch::countDown)
            .doOnError(errorResponse -> productResponseCapturer.set(errorResponse.getMessage()))
            .subscribe();
        countDownLatch.await(3, TimeUnit.SECONDS);

        // THEN
        assertEquals(MANDATORY_FIELD, productResponseCapturer.get());
        verify(productRepository, times(0)).save(any(ProductEntity.class));
    }

    // 2a
    @Test
    void success_Product_Update() throws InterruptedException {
        // GIVEN
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        prepareCategoryRecords();
        var createProperty = prepareProductEntity();
        var productView = ProductView. builder()
                .name(UPDATE_NAME)
                .categoryType(1)
                .code("AAB")
                .companyOwner("ABC Company Records")
                .description(UPDATE_DESCRIPTION)
                .build();
        // THEN
        productService.modifyProduct(productView)
            .doOnTerminate(countDownLatch::countDown)
            .subscribe();
        countDownLatch.await(3, TimeUnit.SECONDS);

        // THEN
        var productUpdated = productRepository.findByCode(productView.getCode());
        assertTrue(productUpdated.isPresent());
        var productEntity  = productUpdated.get();
        assertEquals(UPDATE_NAME, productEntity.getName());
        assertEquals(UPDATE_DESCRIPTION, productEntity.getDescription());
        assertTrue(createProperty.getModifiedDateTs().isBefore(productEntity.getModifiedDateTs()));
    }

    // Saving categories
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
    private ProductView prepareProductView(){
        return ProductView. builder()
                .name("Laptop")
                .categoryType(1)
                .code("AAB")
                .companyOwner("ABC Company Records")
                .description("Laptop 1234f Amazing")
                .build();
    }
    // Saving product for cases which must have this before running the asserts
    private ProductEntity prepareProductEntity(){
        var productView = prepareProductView();
        var productEntity = productMapper.fromProductView(productView);
        return productRepository.save(productEntity);
    }

}
