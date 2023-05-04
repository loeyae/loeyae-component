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
 * @author ZhangYi
 */
@Incubating
public interface AuditStrategy {

    void postInitialize(ServiceRegistry serviceRegistry);

    /**
     * perform
     *
     * @param session session
     * @param entityName entity name
     * @param auditService audit service
     * @param model model
     * @param id id
     * @param originalData original data
     * @param changes changes data
     * @param changedData changed data
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
     * performCollectionChange
     *
     * @param session  session
     * @param entityName entity name
     * @param propertyName property name
     * @param auditService audit service
     * @param model model object
     * @param originalData original data
     * @param changes changes data
     * @param changedData changed data
     */
    @SuppressWarnings("unuse")
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
