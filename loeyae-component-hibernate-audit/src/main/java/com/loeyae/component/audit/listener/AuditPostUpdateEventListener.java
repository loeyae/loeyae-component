package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.synchronization.AuditProcess;
import com.loeyae.component.audit.synchronization.work.AuditWorkUnit;
import com.loeyae.component.audit.synchronization.work.ModWorkUnit;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * AuditPostUpdateEventListener
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditPostUpdateEventListener extends BaseAuditEventListener implements PostUpdateEventListener {

    public AuditPostUpdateEventListener(AuditService auditService) {
        super(auditService);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        final String entityName = event.getPersister().getEntityName();
        if ( auditService.getConfiguration().isAuditable( entityName ) ) {
            checkIfTransactionInProgress( event.getSession() );
            log.debug("[{}] On Post Update [{}]", entityName, event);

            final AuditProcess auditProcess = auditService.getAuditProcessManager().get( event.getSession() );

            Object[] oldState = getOldDBState( auditProcess, entityName, event );
            final Object[] newDbState = postUpdateDBState( event );
            final AuditWorkUnit workUnit = new ModWorkUnit(
                    event.getSession(),
                    auditService,
                    event.getId(),
                    event.getPersister().getEntityName(),
                    event.getPersister(),
                    newDbState,
                    oldState
            );
            auditProcess.addWorkUnit( workUnit );
        }
    }

    private Object[] getOldDBState(AuditProcess auditProcess, String entityName, PostUpdateEvent event) {
        if ( isDetachedEntityUpdate( entityName, event.getOldState() ) ) {
            return auditProcess.getCachedEntityState( event.getId(), entityName );
        }
        return event.getOldState();
    }

    private Object[] postUpdateDBState(PostUpdateEvent event) {
        final Object[] newDbState = event.getState().clone();
        if ( event.getOldState() != null ) {
            final EntityPersister entityPersister = event.getPersister();
            for ( int i = 0; i < entityPersister.getPropertyNames().length; ++i ) {
                if ( !entityPersister.getPropertyUpdateability()[i] ) {
                    newDbState[i] = event.getOldState()[i];
                }
            }
        }
        return newDbState;
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return auditService.getConfiguration().isAuditable(persister.getEntityName());
    }
}
