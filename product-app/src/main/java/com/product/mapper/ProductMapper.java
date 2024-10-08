package com.product.mapper;

import com.product.model.ProductView;
import com.product.persist.ProductEntity;
import com.product.persist.common.Category;
import com.product.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class ProductMapper {
    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalRef", ignore = true)
    @Mapping(target = "createdDateTs", ignore = true)
    @Mapping(target = "modifiedDateTs", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "modifiedByUser", ignore = true)
    @Mapping(target = "categoryType", expression = "java(lookupCategory(productView.getCategoryType()))")
    public abstract ProductEntity fromProductView(ProductView productView);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalRef", ignore = true)
    @Mapping(target = "createdDateTs", ignore = true)
    @Mapping(target = "modifiedDateTs", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "modifiedByUser", ignore = true)
    @Mapping(target = "categoryType", expression = "java(lookupCategory(productView.getCategoryType()))")
    public abstract ProductEntity mergeFromEntity(@MappingTarget ProductEntity productEntity, ProductView productView);

    Category lookupCategory(Long categoryType) {
        if(categoryType>0) {
            var categoryRef =  new AtomicReference<Category>();
            categoryRepository.findById((long) categoryType)
                .map(category -> {
                    categoryRef.set(category);
                    return categoryRef;
                })
                .orElseThrow(() -> new EntityNotFoundException("The category Type does not exists"));

            return categoryRef.get();
        }
        throw new IllegalArgumentException("The category Type must be at least 1");
    }
}
