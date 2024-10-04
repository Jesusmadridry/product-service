package com.product.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private String id;
    private UUID internalProductId;
    private String message;
}
