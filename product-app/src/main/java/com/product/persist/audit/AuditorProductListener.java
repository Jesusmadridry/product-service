package com.product.persist.audit;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

@Slf4j
public class AuditorProductListener implements EntityTrackingRevisionListener  {
    @Override
    public void newRevision(Object revisionEntity) {
        //There is nothing to customize here
    }

    @Override
    public void entityChanged(Class entityClass, String entityName, Object entityId, RevisionType revisionType, Object revisionEntity) {
        log.debug("Tracking {} change for entity {}", revisionType, entityName);

        if (revisionEntity instanceof AuditorProduct auditorInventoryFlow) {
            auditorInventoryFlow.addAuditedEntities(entityClass.getSimpleName(), revisionType);
        }
    }
}
