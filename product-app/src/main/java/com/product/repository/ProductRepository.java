package com.product.repository;

import com.product.persist.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByCode(String code);
    Optional<ProductEntity> findByExternalRef(UUID externalRef);
}
