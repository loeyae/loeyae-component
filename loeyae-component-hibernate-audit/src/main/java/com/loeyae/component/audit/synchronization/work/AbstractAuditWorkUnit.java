package com.loeyae.component.audit.synchronization.work;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.strategy.AuditStrategy;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;

import java.io.Serializable;
import java.util.Map;

/**
 * AbstractAuditWorkUnit
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
public abstract class AbstractAuditWorkUnit implements AuditWorkUnit {
    protected final SessionImplementor sessionImplementor;
    protected final AuditService auditService;
    protected final Serializable id;
    protected final String entityName;
    protected final AuditStrategy auditStrategy;
    private Object performedData;

    protected AbstractAuditWorkUnit(SessionImplementor sessionImplementor,
                                    AuditService auditService,
                                    Serializable id,
                                    String entityName) {
        this.sessionImplementor = sessionImplementor;
        this.auditService = auditService;
        this.id = id;
        this.entityName = entityName;
        this.auditStrategy = auditService.getAuditStrategy();
    }

    @Override
    public Serializable getBizId() {
        return id;
    }

    @Override
    public boolean isPerformed() {
        return performedData != null;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public void perform(Session session) {
        final Map<String, Object> originalData = generateOriginalData();
        final Map<String, Object> changes = generateChanges();
        final Map<String, Object> changedData = generateChangedData();

        auditStrategy.perform( session, getEntityName(), auditService, getModel(), id, originalData, changes, changedData);

        setPerformedData( changes );
    }


    protected void setPerformedData(Object performedData) {
        this.performedData = performedData;
    }

    @Override
    public void undo(Session session) {
        if (isPerformed()) {
            session.flush();
        }
    }

}
