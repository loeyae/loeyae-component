package com.loeyae.component.audit.synchronization.work;

import org.hibernate.Session;

import java.io.Serializable;
import java.util.Map;

/**
 * AuditWorkUnit
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
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
