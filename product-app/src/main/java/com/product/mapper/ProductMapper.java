package com.product.mapper;

import com.product.model.ProductView;
import com.product.persist.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity fromProductView(ProductView productView);
}
