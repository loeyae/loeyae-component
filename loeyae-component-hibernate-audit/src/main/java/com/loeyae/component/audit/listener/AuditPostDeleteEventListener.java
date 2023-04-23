package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.synchronization.AuditProcess;
import com.loeyae.component.audit.synchronization.work.AuditWorkUnit;
import com.loeyae.component.audit.synchronization.work.DelWorkUnit;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * AuditPostDeleteEventListener
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Slf4j
public class AuditPostDeleteEventListener extends BaseAuditEventListener implements PostDeleteEventListener {
    public AuditPostDeleteEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        String entityName = event.getPersister().getEntityName();
        if (auditService.getConfiguration().isAuditable(entityName)) {
            log.debug("[{}] On Post Delete [{}]", entityName, event);
            checkIfTransactionInProgress(event.getSession());

            final AuditProcess auditProcess = auditService.getAuditProcessManager().get(event.getSession());

            final AuditWorkUnit workUnit = new DelWorkUnit(
                    event.getSession(),
                    auditService,
                    event.getId(),
                    event.getPersister().getEntityName(),
                    event.getPersister(),
                    event.getDeletedState()
            );
            auditProcess.addWorkUnit(workUnit);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return auditService.getConfiguration().isAuditable(persister.getEntityName());
    }
}
