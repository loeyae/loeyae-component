package com.loeyae.component.audit.synchronization.work;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.strategy.AuditStrategy;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AddWorkUnit
 *
 * @author ZhangYi
 */
public class AddWorkUnit extends AbstractAuditWorkUnit {
    private final Object[] state;
    private final Map<String, Object> data;

    public AddWorkUnit(SessionImplementor sessionImplementor,
                       AuditService auditService,
                       Serializable id,
                       String entityName,
                       EntityPersister entityPersister,
                       Object[] state) {
        super(sessionImplementor, auditService, id, entityName);
        this.state = state;
        this.data = new HashMap<>();
        auditService.getConfiguration().get(entityName).mapChanges(sessionImplementor, data, entityPersister.getPropertyNames(), state, null);
    }

    @Override
    public boolean containsWork() {
        return true;
    }

    @Override
    public AuditWorkUnit dispatch(AuditWorkUnit other) {
        return other.merge( this );
    }

    @Override
    public AuditWorkUnit merge(AuditWorkUnit other) {
        return other;
    }

    @Override
    public Map<String, Object> generateChanges() {
        return data;
    }

    @Override
    public Map<String, Object> generateChangedData() {
        return data;
    }

    @Override
    public Map<String, Object> generateOriginalData() {
        return Collections.emptyMap();
    }

    @Override
    public String getModel() {
        return "create";
    }
}
