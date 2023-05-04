package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.synchronization.AuditProcess;
import com.loeyae.component.audit.synchronization.work.AddWorkUnit;
import com.loeyae.component.audit.synchronization.work.AuditWorkUnit;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * AuditPostInsertEventListener
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditPostInsertEventListener extends BaseAuditEventListener implements PostInsertEventListener {

    public AuditPostInsertEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        String entityName = event.getPersister().getEntityName();
        if (auditService.getConfiguration().isAuditable(entityName)) {
            checkIfTransactionInProgress(event.getSession());
            log.debug("[{}] On Post Insert [{}]", entityName, event);

            final AuditProcess auditProcess = auditService.getAuditProcessManager().get(event.getSession());

            final AuditWorkUnit workUnit = new AddWorkUnit(
                    event.getSession(),
                    auditService,
                    event.getId(),
                    event.getPersister().getEntityName(),
                    event.getPersister(),
                    event.getState()
            );
            auditProcess.addWorkUnit(workUnit);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return auditService.getConfiguration().isAuditable(persister.getEntityName());
    }
}
