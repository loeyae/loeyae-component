package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.synchronization.AuditProcess;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

/**
 * AuditPreUpdateEventListener
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditPreUpdateEventListener extends BaseAuditEventListener implements PreUpdateEventListener {

    private static final long serialVersionUID = -9068990078682257950L;

    public AuditPreUpdateEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        String entityName = event.getPersister().getEntityName();
        if (auditService.getConfiguration().isAuditable(entityName)) {
            checkIfTransactionInProgress(event.getSession());
            log.debug("[{}] On Post Insert [{}]", entityName, event);
            if ( isDetachedEntityUpdate( entityName, event.getOldState() ) ) {
                final AuditProcess auditProcess = auditService.getAuditProcessManager().get( event.getSession() );
                auditProcess.cacheEntityState(
                        event.getId(),
                        entityName,
                        event.getPersister().getDatabaseSnapshot( event.getId(), event.getSession() )
                );
            }
        }
        return false;
    }
}
