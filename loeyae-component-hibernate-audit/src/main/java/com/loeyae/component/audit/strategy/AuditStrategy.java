package com.loeyae.component.audit.strategy;

import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.entities.PersistentCollectionData;
import org.hibernate.Incubating;
import org.hibernate.Session;
import org.hibernate.service.ServiceRegistry;

import java.io.Serializable;

/**
 * AuditStrategy
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Incubating
public interface AuditStrategy {

    void postInitialize(ServiceRegistry serviceRegistry);

    /**
     *
     * @param session
     * @param entityName
     * @param auditService
     * @param id
     * @param originalData
     * @param changes
     * @param changedData
     */
    void perform(
            Session session,
            String entityName,
            AuditService auditService,
            String model,
            Serializable id,
            Object originalData,
            Object changes,
            Object changedData);


    /**
     *
     * @param session
     * @param entityName
     * @param propertyName
     * @param auditService
     * @param originalData
     * @param originalData
     */
    void performCollectionChange(
            Session session,
            String entityName,
            String propertyName,
            AuditService auditService,
            String model,
            PersistentCollectionData originalData,
            PersistentCollectionData changes,
            PersistentCollectionData changedData);
}
