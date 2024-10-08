package com.product.persist;

import com.common.persist.CommonEntity;
import com.product.persist.common.Category;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product", schema = "company_inventory")
@Slf4j
public class ProductEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID externalRef;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="category_type_id")
    private Category categoryType;
    private String name;
    private String description;
    private Double price;
    private String measurement;
    private String companyOwner;

    @PrePersist
    public  void presetValues() {
        if (externalRef == null) {
            externalRef = UUID.randomUUID();
            log.debug("Preparing {} to create {}", externalRef, code);
        }
    }
}
