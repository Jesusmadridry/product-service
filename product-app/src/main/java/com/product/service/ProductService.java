package com.product.service;

import com.product.mapper.ProductMapper;
import com.product.model.ProductResponse;
import com.product.model.ProductServiceException;
import com.product.model.ProductView;
import com.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.product.persist.model.Constant.*;

@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * This method must receive a Product View Object, and it will create the record inside the table.
     * @param productView
     * @return
     */
    public Mono<ProductResponse> registerNewProduct(ProductView productView){
        if(!ObjectUtils.isEmpty(productView)){
            try {
                var productProcess =
                        productRepository.findByCode(productView.getCode())
                            .map(productEntity -> new OperationResult(productEntity.getExternalRef(), EXISTING_PRODUCT_MESSAGE))
                            .orElseGet(() -> {
                                var newProduct = productMapper.fromProductView(productView);
                                var productCreated = productRepository.save(newProduct);
                                return new OperationResult(productCreated.getExternalRef(), NEW_PRODUCT_MESSAGE);
                            });
                return Mono.just(ProductResponse.builder()
                                    .id(productProcess.externalRef())
                                    .message(productProcess.message)
                                    .build());
                } catch (Exception ex) {
                   return Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, ERROR_MESSAGE_SERVICE, ex));
                }
        }
        return Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, MANDATORY_FIELD));
    }

    public Mono<ProductResponse> modifyProduct(ProductView productView){
        if(!ObjectUtils.isEmpty(productView)){
            try {
                return
                    productRepository.findByCode(productView.getCode())
                        .map(productEntity -> {
                            var updateProduct = productMapper.mergeFromEntity(productEntity, productView);
                            var productEntityUpdated = productRepository.save(updateProduct);
                            return Mono.just(ProductResponse.builder()
                                        .id(productEntityUpdated.getExternalRef())
                                        .message(PRODUCT_UPDATE_MESSAGE)
                                        .build());
                        })
                        .orElseGet(() -> Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, PRODUCT_NOT_FOUND_MESSAGE)));
            } catch (Exception ex) {
                return Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, ERROR_MESSAGE_SERVICE, ex));
            }
        }
        return Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, MANDATORY_FIELD));
    }

    public Mono<ProductResponse> deleteProduct(UUID externalRef){
        if(!ObjectUtils.isEmpty(externalRef)){
            try {
                return
                    productRepository.findByExternalRef(externalRef)
                            .map(productEntity -> {
                                var externalRefDB = productEntity.getExternalRef();
                                productRepository.deleteById(productEntity.getId());
                                return Mono.just(ProductResponse.builder()
                                        .id(externalRefDB)
                                        .message(PRODUCT_DELETE_MESSAGE)
                                        .build());
                            })
                            .orElseGet(() -> Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, PRODUCT_NOT_FOUND_MESSAGE)));
            } catch (Exception ex) {
                return Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, ERROR_MESSAGE_SERVICE, ex));
            }
        }
        return Mono.error(new ProductServiceException(HttpStatus.BAD_REQUEST, 500, MANDATORY_FIELD));
    }

    private record OperationResult(UUID externalRef, String message){}
}
