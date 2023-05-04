package com.loeyae.component.audit.listener;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.configuration.AuditConfiguration;
import com.loeyae.component.audit.entities.ClassAuditData;
import com.loeyae.component.audit.exception.AuditException;
import com.loeyae.component.audit.synchronization.AuditProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Set;

/**
 * 事件监听基类
 *
 * @author ZhangYi
 */
public abstract class BaseAuditEventListener implements AuditEventListener {

    protected final AuditService auditService;

    protected BaseAuditEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    protected void checkIfTransactionInProgress(SessionImplementor session) {
        if ( !session.isTransactionInProgress() ) {
            throw new AuditException( "Unable to create revision because of non-active transaction" );
        }
    }

    protected boolean isDetachedEntityUpdate(String entityName, Object[] oldState) {
        final ClassAuditData configuration = auditService.getConfiguration().get( entityName );
        if ( configuration.getAuditedProperties().size() > 0 && oldState == null ) {
            return true;
        }
        return false;
    }
}
