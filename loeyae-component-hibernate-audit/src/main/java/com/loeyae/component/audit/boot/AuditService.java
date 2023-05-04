package com.loeyae.component.audit.boot;

import com.loeyae.component.audit.configuration.AuditConfiguration;
import com.loeyae.component.audit.configuration.MappingCollector;
import com.loeyae.component.audit.strategy.AuditStrategy;
import com.loeyae.component.audit.synchronization.AuditProcess;
import com.loeyae.component.audit.synchronization.AuditProcessManager;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;

import java.util.Map;

/**
 * AuditService
 *
 * @author ZhangYi
 */
public interface AuditService extends Service {

    String INTEGRATION_ENABLED = "hibernate.integration.audit.enabled";

    boolean isEnabled();

    boolean isInitialized();

    void initialize(MetadataImplementor metadata, MappingCollector mappingCollector);

    ServiceRegistry getServiceRegistry();

    ClassLoaderService getClassLoaderService();

    AuditConfiguration getConfiguration();

    AuditStrategy getAuditStrategy();

    AuditProcessManager getAuditProcessManager();
}
