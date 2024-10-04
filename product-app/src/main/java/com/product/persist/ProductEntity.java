package com.product.persist;

import com.product.persist.common.Category;
import com.product.persist.common.CommonEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product", schema = "company_inventory")
public class ProductEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="product_type_id")
    private Category categoryType;
    private String name;
    private Double price;
    private String measurement;
    private String companyOwner;
}
