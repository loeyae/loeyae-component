package com.loeyae.component.audit.boot;

import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import java.util.Map;

/**
 * AuditServiceInitiator
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
public class AuditServiceInitiator implements StandardServiceInitiator<AuditService> {

    public static final AuditServiceInitiator INSTANCE = new AuditServiceInitiator();

    @Override
    public AuditService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new AuditServiceImpl();
    }

    @Override
    public Class<AuditService> getServiceInitiated() {
        return AuditService.class;
    }
}
