package com.loeyae.component.audit.synchronization.work;

import org.hibernate.Session;

import java.io.Serializable;
import java.util.Map;

/**
 * AuditWorkUnit
 *
 * @author ZhangYi
 */
public interface AuditWorkUnit {

    Serializable getBizId();

    String getEntityName();

    boolean containsWork();

    boolean isPerformed();

    AuditWorkUnit dispatch(AuditWorkUnit other);

    AuditWorkUnit merge(AuditWorkUnit other);

    void perform(Session session);

    void undo(Session session);

    Map<String, Object> generateChanges();

    Map<String, Object> generateChangedData();

    Map<String, Object> generateOriginalData();

    String getModel();
}
