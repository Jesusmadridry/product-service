package com.product.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
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
