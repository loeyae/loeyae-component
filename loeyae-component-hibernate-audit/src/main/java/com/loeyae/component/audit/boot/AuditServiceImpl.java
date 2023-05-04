package com.loeyae.component.audit.boot;

import com.loeyae.component.audit.configuration.AuditConfiguration;
import com.loeyae.component.audit.configuration.MappingCollector;
import com.loeyae.component.audit.strategy.AuditStrategy;
import com.loeyae.component.audit.synchronization.AuditProcessManager;
import com.loeyae.component.audit.tools.ReflectionTools;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;

import java.util.Map;
import java.util.Properties;

/**
 * AuditServiceImpl
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditServiceImpl implements AuditService, Configurable, Stoppable {

    private static final long serialVersionUID = -613121737761672867L;

    private boolean integrationEnabled;
    private boolean initialized;

    private ServiceRegistry serviceRegistry;
    private ClassLoaderService classLoaderService;
    private AuditConfiguration auditConfiguration;
    private AuditStrategy auditStrategy;
    private AuditProcessManager auditProcessManager;

    @Override
    public void configure(Map configurationValues) {
        this.integrationEnabled = ConfigurationHelper.getBoolean( INTEGRATION_ENABLED, configurationValues, true );
        log.info( "Audit integration enabled? : {}", integrationEnabled );
    }

    @Override
    public boolean isEnabled() {
        return integrationEnabled;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void initialize(final MetadataImplementor metadata, final MappingCollector mappingCollector) {
        if ( initialized ) {
            throw new UnsupportedOperationException( "AuditService#initialize should be called only once" );
        }

        initialized = true;


        this.serviceRegistry = metadata.getMetadataBuildingOptions().getServiceRegistry();
        this.classLoaderService = serviceRegistry.getService( ClassLoaderService.class );

        doInitialize( metadata, mappingCollector, serviceRegistry );
    }

    private void doInitialize(
            final MetadataImplementor metadata,
            final MappingCollector mappingCollector,
            ServiceRegistry serviceRegistry) {
        final ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
        final Properties properties = new Properties();
        properties.putAll( cfgService.getSettings() );

        this.auditConfiguration = new AuditConfiguration(this, properties);
        this.auditProcessManager = new AuditProcessManager();

        final ReflectionManager reflectionManager = metadata.getMetadataBuildingOptions()
                .getReflectionManager();
        auditConfiguration.initAuditData(metadata, reflectionManager);
        this.auditStrategy = initializeAuditStrategy(
                auditConfiguration.getAuditStrategyName(),
                serviceRegistry
        );
    }

    private static AuditStrategy initializeAuditStrategy(
            String auditStrategyName,
            ServiceRegistry serviceRegistry) {
        AuditStrategy strategy;

        try {
            final Class<?> auditStrategyClass = loadClass( auditStrategyName, serviceRegistry );
            strategy = (AuditStrategy) ReflectHelper.getDefaultConstructor( auditStrategyClass ).newInstance();
        }
        catch (Exception e) {
            throw new MappingException(
                    String.format( "Unable to create AuditStrategy [%s] instance.", auditStrategyName ),
                    e
            );
        }
        strategy.postInitialize(serviceRegistry );

        return strategy;
    }


    private static Class<?> loadClass(String auditStrategyName, ServiceRegistry serviceRegistry) {
        try {
            return AuditServiceImpl.class.getClassLoader().loadClass( auditStrategyName );
        }
        catch (Exception e) {
            return ReflectionTools.loadClass( auditStrategyName, serviceRegistry.getService( ClassLoaderService.class ) );
        }
    }


    @Override
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    @Override
    public ClassLoaderService getClassLoaderService() {
        return classLoaderService;
    }

    @Override
    public AuditConfiguration getConfiguration() {
        return auditConfiguration;
    }

    @Override
    public AuditStrategy getAuditStrategy() {
        if ( !initialized ) {
            throw new IllegalStateException( "Service is not yet initialized" );
        }
        return auditStrategy;
    }

    @Override
    public AuditProcessManager getAuditProcessManager() {
        return auditProcessManager;
    }

    @Override
    public void stop() {

    }
}
