package com.product.persist.common;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class CommonEntity {
    @CreationTimestamp
    protected LocalDateTime createdDateTs;
    protected String createdByUser;
    @UpdateTimestamp
    protected LocalDateTime updatedDateTs;
    protected String modifiedByUser;
}
