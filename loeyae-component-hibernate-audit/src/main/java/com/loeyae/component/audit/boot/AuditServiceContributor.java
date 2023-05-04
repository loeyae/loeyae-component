package com.loeyae.component.audit.boot;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceContributor;

/**
 * AuditServiceContributor
 *
 * @author ZhangYi
 */
public class AuditServiceContributor implements ServiceContributor {

    @Override
    public void contribute(StandardServiceRegistryBuilder serviceRegistryBuilder) {
        serviceRegistryBuilder.addInitiator(AuditServiceInitiator.INSTANCE);
    }
}
