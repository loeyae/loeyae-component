package com.loeyae.component.audit.boot;

import com.loeyae.component.audit.listener.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * AuditIntegrator
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Slf4j
public class AuditIntegrator implements Integrator {

    public static final String AUTO_REGISTER = "hibernate.audit.autoRegisterListeners";

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        final AuditService auditService = serviceRegistry.getService( AuditService.class );

        if ( !auditService.isEnabled() ) {
            log.debug( "Skipping Audit listener registrations : AuditService disabled" );
            return;
        }

        final boolean autoRegister = serviceRegistry.getService( ConfigurationService.class ).getSetting(
                AUTO_REGISTER,
                StandardConverters.BOOLEAN,
                true
        );
        if ( !autoRegister ) {
            log.debug( "Skipping Audit listener registrations : Listener auto-registration disabled" );
            return;
        }

        if ( !auditService.isInitialized() ) {
            throw new HibernateException(
                    "Expecting AuditService to have been initialized prior to call to AuditIntegrator#integrate"
            );
        }

        final EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
        if ( !auditService.getConfiguration().hasAuditedEntities() ) {
            log.debug( "Skipping Audit listener registrations : No audited entities found" );
            return;
        }


        if ( auditService.getConfiguration().hasAuditedEntities() ) {
            listenerRegistry.appendListeners(
                    EventType.POST_DELETE,
                    new AuditPostDeleteEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.POST_INSERT,
                    new AuditPostInsertEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.PRE_UPDATE,
                    new AuditPreUpdateEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.POST_UPDATE,
                    new AuditPostUpdateEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.POST_COLLECTION_RECREATE,
                    new AuditPostCollectionRecreateEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.PRE_COLLECTION_REMOVE,
                    new AuditPreCollectionRemoveEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.PRE_COLLECTION_UPDATE,
                    new AuditPreCollectionUpdateEventListener( auditService )
            );
            listenerRegistry.appendListeners(
                    EventType.POST_COLLECTION_UPDATE,
                    new AuditPostCollectionUpdateEventListener( auditService )
            );
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }
}
