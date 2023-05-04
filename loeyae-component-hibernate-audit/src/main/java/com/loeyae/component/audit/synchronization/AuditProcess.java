package com.loeyae.component.audit.synchronization;

import com.loeyae.component.audit.entities.Pair;
import com.loeyae.component.audit.exception.AuditException;
import com.loeyae.component.audit.synchronization.work.AuditWorkUnit;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * AuditProcess
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditProcess implements BeforeTransactionCompletionProcess {

    private final SessionImplementor session;

    private final LinkedList<AuditWorkUnit> workUnits;
    private final Queue<AuditWorkUnit> undoQueue;
    private final Map<Pair<String, Object>, AuditWorkUnit> usedIds;
    private final Map<Pair<String, Object>, Object[]> entityStateCache;

    public AuditProcess(SessionImplementor sessionImplementor) {
        this.session = sessionImplementor;
        workUnits = new LinkedList<>();
        undoQueue = new LinkedList<>();
        usedIds = new HashMap<>();
        entityStateCache = new HashMap<>();
    }

    @Override
    public void doBeforeTransactionCompletion(SessionImplementor session) {
        if ( workUnits.size() == 0 && undoQueue.size() == 0 ) {
            return;
        }

        if ( !session.getTransactionCoordinator().isActive() ) {
            log.debug( "Skipping envers transaction hook due to non-active (most likely marked-rollback-only) transaction" );
            return;
        }

        if ( FlushMode.MANUAL.equals( session.getHibernateFlushMode() ) || session.isClosed() ) {
            Session temporarySession = null;
            try {
                temporarySession = session.sessionWithOptions()
                        .connection()
                        .autoClose( false )
                        .connectionHandlingMode( PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION )
                        .openSession();
                executeInSession( temporarySession );
                temporarySession.flush();
            }
            finally {
                if ( temporarySession != null ) {
                    temporarySession.close();
                }
            }
        }
        else {
            executeInSession( session );
            session.flush();
        }
    }

    public void cacheEntityState(Serializable id, String entityName, Object[] snapshot) {
        final Pair<String, Object> key = new Pair<>( entityName, id );
        if ( entityStateCache.containsKey( key ) ) {
            throw new AuditException( "The entity [" + entityName + "] with id [" + id + "] is already cached." );
        }
        entityStateCache.put( key, snapshot );
    }

    public Object[] getCachedEntityState(Object id, String entityName) {
        final Pair<String, Object> key = new Pair<>( entityName, id );
        final Object[] entityState = entityStateCache.get( key );
        if ( entityState != null ) {
            entityStateCache.remove( key );
        }
        return entityState;
    }

    private void removeWorkUnit(AuditWorkUnit vwu) {
        workUnits.remove( vwu );
        if ( vwu.isPerformed() ) {
            undoQueue.offer( vwu );
        }
    }

    public void addWorkUnit(AuditWorkUnit vwu) {
        if ( vwu.containsWork() ) {
            final Object entityId = vwu.getBizId();

            if ( entityId == null ) {
                workUnits.offer( vwu );
            }
            else {
                final String entityName = vwu.getEntityName();
                final Pair<String, Object> usedIdsKey = Pair.make( entityName, entityId );

                if ( usedIds.containsKey( usedIdsKey ) ) {
                    final AuditWorkUnit other = usedIds.get( usedIdsKey );
                    final AuditWorkUnit result = vwu.dispatch( other );

                    if ( result != other ) {
                        removeWorkUnit( other );

                        if ( result != null ) {
                            usedIds.put( usedIdsKey, result );
                            workUnits.offer( result );
                        }
                    }
                }
                else {
                    usedIds.put( usedIdsKey, vwu );
                    workUnits.offer( vwu );
                }
            }
        }
    }

    private void executeInSession(Session session) {
        AuditWorkUnit vwu;
        while ( (vwu = undoQueue.poll()) != null ) {
            vwu.undo( session );
        }

        while ( (vwu = workUnits.poll()) != null ) {
            vwu.perform( session);
        }
    }
}
