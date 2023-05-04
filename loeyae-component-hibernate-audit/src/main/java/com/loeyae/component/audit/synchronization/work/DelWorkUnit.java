package com.loeyae.component.audit.synchronization.work;

import cn.hutool.core.util.ArrayUtil;
import com.loeyae.component.audit.boot.AuditService;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * DelWorkUnit
 *
 * @author ZhangYi
 */
public class DelWorkUnit extends AbstractAuditWorkUnit {

    private final Object[] state;
    private final EntityPersister entityPersister;
    private final Map<String, Object> data;

    public DelWorkUnit(SessionImplementor sessionImplementor,
                          AuditService auditService,
                          Serializable id,
                          String entityName,
                          EntityPersister entityPersister,
                          Object[] state
                          ) {
        super(sessionImplementor, auditService, id, entityName);
        this.state = state;
        this.entityPersister = entityPersister;
        this.data = new HashMap<>();
        auditService.getConfiguration().get(entityName).map(sessionImplementor, data, entityPersister.getPropertyNames(), state);
    }

    @Override
    public boolean containsWork() {
        return true;
    }

    @Override
    public AuditWorkUnit dispatch(AuditWorkUnit other) {
        return other.merge(this);
    }

    @Override
    public AuditWorkUnit merge(AuditWorkUnit other) {
        return this;
    }

    @Override
    public Map<String, Object> generateChanges() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> generateChangedData() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> generateOriginalData() {
        return data;
    }

    @Override
    public String getModel() {
        return "delete";
    }
}
