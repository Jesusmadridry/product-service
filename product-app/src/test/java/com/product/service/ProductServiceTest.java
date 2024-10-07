package com.product.service;

import com.product.BaseIntegration;
import com.product.model.ProductResponse;
import com.product.model.ProductView;
import com.product.persist.ProductEntity;
import com.product.persist.common.Category;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.product.persist.model.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * 1a. success_Product_Create -> it must receive a product view, convert into product entity, and be saved successfully
 * 1b. trying_to_create_alreadyExistingProduct -> this case will catch the code received is already associated in the product table
 * 1c. failed_createProcess_dueTo_NullPayload -> The service will throw an error, because it can deal with null payloads.
 * 2a. success_Product_Update -> it must receive a product view, convert into product entity, and update already existing product
 * 2b. failureWhile_itWasTried_ToUpdate_NonExistingProduct -> this case will catch when there was tried to update a non-existing product in the table
 * 3a. success_Product_Delete
 */
@Slf4j
class ProductServiceTest extends BaseIntegration {
    private static final String UPDATE_NAME = "Laptop Top";
    private static final String UPDATE_DESCRIPTION = "Laptop Top Amazing";
    private static final String DEFAULT_DESCRIPTION = "Amazing product";
    private static final String DEFAULT_COMPANY_OWNER= "ABD Company Records";
    private static final String DEFAULT_NAME = "ABCCODE";


    // 1a
    @Test
    void success_Product_Create() throws InterruptedException {
        // GIVEN
        prepareCategoryRecords();
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var productView = prepareProductView(null,DEFAULT_NAME,DEFAULT_COMPANY_OWNER,DEFAULT_DESCRIPTION);

        // WHEN
        productService.registerNewProduct(productView)
            .doOnTerminate(countDownLatch::countDown);

        countDownLatch.await(4, TimeUnit.SECONDS);

        // THEN
        var productCreated = productRepository.findByCode(productView.getCode()).get();

        assertNotNull(productCreated);
        assertEquals("AAB", productCreated.getCode());
        assertNotNull(productCreated.getExternalRef());
        assertEquals(DEFAULT_COMPANY_OWNER, productCreated.getCompanyOwner());
        verify(productRepository, times(1)).save(any(ProductEntity.class));
        log.info("Product Created {}", productCreated);
    }

    // 1b
    @Test
    void trying_to_create_alreadyExistingProduct() throws InterruptedException {
        // GIVEN
        prepareCategoryRecords();
        var simulatingOldRecord = prepareProductView(null, DEFAULT_NAME, DEFAULT_COMPANY_OWNER, DEFAULT_DESCRIPTION);
        var propertyCreated = prepareProductEntity(simulatingOldRecord);
        reset(productRepository); // Cleaning the repository to avoid false positive inside the register new product method
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var productResponseCapturer = new AtomicReference<ProductResponse>();
        var productView = prepareProductView(propertyCreated.getExternalRef(), DEFAULT_NAME, DEFAULT_COMPANY_OWNER, DEFAULT_DESCRIPTION);

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
        prepareCategoryRecords();
        CountDownLatch countDownLatch =  new CountDownLatch(1);
        var simulatingOldRecord = prepareProductView(null, DEFAULT_NAME, DEFAULT_COMPANY_OWNER, DEFAULT_DESCRIPTION);
        var createProperty = prepareProductEntity(simulatingOldRecord);
        var categoryDB = categoryRepository.findAll().get(0);
        var productView = ProductView. builder()
                .id(createProperty.getExternalRef())
                .name(UPDATE_NAME)
                .categoryType(categoryDB.getId())
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

    // 2b
    @Test
    void failureWhile_itWasTried_ToUpdate_NonExistingProduct() throws InterruptedException {
        // GIVEN
        CountDownLatch countDownLatch =  new CountDownLatch(1);

        var productResponseCapturer = new AtomicReference<Throwable>();
        var productView = ProductView. builder()
                .id(UUID.randomUUID())
                .name(UPDATE_NAME)
                .categoryType(1L)
                .code("AAB")
                .companyOwner("ABC Company Records")
                .description(UPDATE_DESCRIPTION)
                .build();
        // THEN
        productService.modifyProduct(productView)
                .doOnTerminate(countDownLatch::countDown)
                .doOnError(productResponseCapturer::set)
                .subscribe();
        countDownLatch.await(3, TimeUnit.SECONDS);

        // THEN
        assertNotNull(productResponseCapturer.get());
        assertEquals(PRODUCT_NOT_FOUND_MESSAGE, productResponseCapturer.get().getMessage());
    }

    // 3c
    @Test
    void success_Product_Delete() throws InterruptedException {
        // GIVEN
        prepareCategoryRecords();
        var simulatingOldRecord = prepareProductView(null, DEFAULT_NAME, DEFAULT_COMPANY_OWNER, DEFAULT_DESCRIPTION);
        var product = prepareProductEntity(simulatingOldRecord);
        var countDownLatch =  new CountDownLatch(1);
        var productResponseCapturer = new AtomicReference<ProductResponse>();

        // WHEN
        productService.deleteProduct(product.getExternalRef())
            .doOnTerminate(countDownLatch::countDown)
            .subscribe(productResponseCapturer::set);
        countDownLatch.await(3, TimeUnit.SECONDS);

        // THEN
        var productEntity = productRepository.findByCode(product.getCode());
        assertFalse(productEntity.isPresent());
        assertEquals(PRODUCT_DELETE_MESSAGE, productResponseCapturer.get().getMessage());
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
    private ProductView prepareProductView(UUID externalRef, String name, String companyOwner, String description){
        var categoryList = categoryRepository.findAll();
        return ProductView. builder()
                .id(externalRef)
                .name(name)
                .categoryType(categoryList.get(0).getId())
                .code("AAB")
                .companyOwner(companyOwner)
                .description(description)
                .build();
    }
    // Saving product for cases which must have this before running the asserts
    private ProductEntity prepareProductEntity(ProductView productView){
        var productEntity = productMapper.fromProductView(productView);
        return productRepository.save(productEntity);
    }

}
