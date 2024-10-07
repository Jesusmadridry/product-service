package com.product.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductView {
    @NotEmpty
    private String code;

    @Min(0)
    private int categoryType;

    @NotEmpty
    private String name;
    private String description;

    @Min(0)
    private Double price;
    private String measurement;
    private String companyOwner;
}
