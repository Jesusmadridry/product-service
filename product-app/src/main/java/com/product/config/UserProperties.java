package com.product.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "application.security")
public class UserProperties {
    @NotEmpty
    @NestedConfigurationProperty
    private List<SecurityProperties.User> users = new ArrayList<>();
}
