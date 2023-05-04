package com.loeyae.component.audit.synchronization.work;

import com.loeyae.component.audit.boot.AuditService;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ModWorkUnit
 *
 * @author ZhangYi
 */
public class ModWorkUnit extends AbstractAuditWorkUnit{
    private final Map<String, Object> originalData;
    private final Map<String, Object> data;
    private final Map<String, Object> changedData;
    private final boolean changes;

    private final EntityPersister entityPersister;
    private final Object[] oldState;
    private final Object[] newState;

    public ModWorkUnit(SessionImplementor sessionImplementor,
                       AuditService auditService,
                       Serializable id,
                       String entityName,
                       EntityPersister entityPersister,
                       Object[] newState,
                       Object[] oldState) {
        super(sessionImplementor, auditService, id, entityName);
        this.entityPersister = entityPersister;
        this.newState = newState;
        this.oldState = oldState;
        this.data = new HashMap<>();
        auditService.getConfiguration().get(entityName).mapChanges(sessionImplementor, data, entityPersister.getPropertyNames(), newState, oldState);
        this.changes = this.data.size() > 0;
        this.originalData = new HashMap<>();
        auditService.getConfiguration().get(entityName).map(sessionImplementor, originalData, entityPersister.getPropertyNames(), oldState);
        this.changedData = new HashMap<>();
        auditService.getConfiguration().get(entityName).map(sessionImplementor, changedData, entityPersister.getPropertyNames(), newState);
    }

    @Override
    public boolean containsWork() {
        return this.changes;
    }

    @Override
    public AuditWorkUnit dispatch(AuditWorkUnit other) {
        return other.merge(this);
    }

    @Override
    public AuditWorkUnit merge(AuditWorkUnit other) {
        ModWorkUnit modWorkUnit = (ModWorkUnit) other;
        return new ModWorkUnit(
                modWorkUnit.sessionImplementor,
                modWorkUnit.auditService,
                modWorkUnit.id,
                modWorkUnit.getEntityName(),
                modWorkUnit.entityPersister,
                modWorkUnit.newState,
                this.oldState
        );
    }

    @Override
    public Map<String, Object> generateChanges() {
        return data;
    }

    @Override
    public Map<String, Object> generateChangedData() {
        return changedData;
    }

    @Override
    public Map<String, Object> generateOriginalData() {
        return originalData;
    }

    @Override
    public String getModel() {
        return "update";
    }
}
