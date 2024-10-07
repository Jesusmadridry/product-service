package com.product.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String message;
}
