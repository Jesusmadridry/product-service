package com.product.model;

import com.product.model.validation.RegisterInfo;
import com.product.model.validation.UpdateInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductView {
    @NotEmpty(groups = UpdateInfo.class)
    private UUID id;
    @NotEmpty(groups = {RegisterInfo.class, UpdateInfo.class})
    private String code;

    @Min(groups = {RegisterInfo.class, UpdateInfo.class}, value = 1)
    private Long categoryType;

    @NotEmpty(groups = {RegisterInfo.class, UpdateInfo.class})
    private String name;
    private String description;

    @Min(groups = {RegisterInfo.class, UpdateInfo.class}, value = 1)
    private Double price;
    private String measurement;

    @NotEmpty(groups = {RegisterInfo.class, UpdateInfo.class})
    private String companyOwner;
}
