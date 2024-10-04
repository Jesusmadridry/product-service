package com.product.persist.audit;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.hibernate.envers.RevisionType;

import static com.product.persist.model.Constant.AUDIT_SCHEMA;
import static com.product.persist.model.Constant.AUDIT_SCHEMA_GENERATOR;

@Data
@Entity
@RevisionEntity(AuditorProductListener.class)
@Table(schema = AUDIT_SCHEMA)
public class AuditorProduct {
    @Id
    @RevisionNumber
    @SequenceGenerator(
            name= AUDIT_SCHEMA_GENERATOR,
            sequenceName = "product_auditor_seq",
            schema = AUDIT_SCHEMA,
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = AUDIT_SCHEMA_GENERATOR
    )
    private long revisionId;

    @RevisionTimestamp
    private long revisionTs;

    private String affectedEntities;

    public void addAuditedEntities(String entityName, RevisionType revisionType) {
        if (affectedEntities == null) {
            affectedEntities = "";
        }
        else {
            affectedEntities +=", ";
        }
        affectedEntities += entityName + ":" + revisionType.name();
    }
}
