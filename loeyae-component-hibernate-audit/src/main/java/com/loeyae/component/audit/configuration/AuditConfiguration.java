package com.loeyae.component.audit.configuration;

import com.loeyae.component.audit.annotation.Audited;
import com.loeyae.component.audit.annotation.ColumnComment;
import com.loeyae.component.audit.annotation.NotAudited;
import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.entities.ClassAuditData;
import com.loeyae.component.audit.entities.Pair;
import com.loeyae.component.audit.strategy.DefaultAuditStrategy;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.ClassLoadingException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.loader.PropertyPath;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;

import javax.persistence.Column;
import javax.persistence.Version;
import java.util.*;
import java.util.function.Function;

/**
 * AuditConfiguration
 *
 * @author ZhangYi
 */
@Slf4j
public class AuditConfiguration {

    private final AuditService auditService;
    private final String auditStrategyName;
    private final Map<String, PersistentClass> auditPersistentClasses = new HashMap<>();
    private final Map<String, ClassAuditData> auditEntityAuditData = new HashMap<>();

    public AuditConfiguration(AuditService auditService, Properties properties) {
        this.auditService = auditService;
        auditStrategyName = ConfigurationHelper.getString(
                AuditSettings.AUDIT_STRATEGY, properties, DefaultAuditStrategy.class.getName()
        );
    }

    public void initAuditData(MetadataImplementor metadata,
                              ReflectionManager reflectionManager
                              ) {
        metadata.getEntityBindings().forEach(binding -> {
            if (binding.getClassName() != null) {
                ClassAuditData classAuditData = getAuditData(reflectionManager, binding);
                auditEntityAuditData.put(binding.getEntityName(), classAuditData);
            }
        });
        log.debug("auditEntityAuditData: [{}]", auditEntityAuditData);
    }

    public ClassAuditData get(String entityName) {
        return auditEntityAuditData.getOrDefault(entityName, new ClassAuditData());
    }

    private ClassAuditData getAuditData(ReflectionManager reflectionManager, PersistentClass persistentClass) {
        ClassAuditData auditData = new ClassAuditData();
        try {
            final XClass xClass = reflectionManager.classForName(persistentClass.getClassName());
            final Audited audited = xClass.getAnnotation(Audited.class);
            if (audited != null) {
                auditData.setBizId(audited.bizId());
                auditData.setModuleName(audited.moduleName());
                auditData.setTableName(audited.tableName());
                auditData.setExtra(auditData.getExtra());
                readPersistentPropertiesAccess(persistentClass, auditData);
                addPropertiesFromClass(xClass, auditData);
            }
        } catch (ClassLoadingException e) {
            throw new MappingException( e );
        }
        return auditData;
    }

    private void readPersistentPropertiesAccess(PersistentClass persistentClass, ClassAuditData classAuditData) {
        final Iterator<Property> propertyIter = persistentClass.getPropertyIterator();
        while (propertyIter.hasNext()) {
            Property property =  propertyIter.next();
            classAuditData.getPropertyMap().put(property.getName(), property);
            if ("field".equals(property.getPropertyAccessorName())) {
                classAuditData.getFields().add(property.getName());
            } else {
                classAuditData.getProperties().put(property.getName(), property.getPropertyAccessorName());
            }
            if ( "embedded".equals( property.getPropertyAccessorName() ) && !PropertyPath.IDENTIFIER_MAPPER_PROPERTY.equals( property.getName() ) ) {
                final Component component = (Component) property.getValue();
                final Iterator<Property> componentProperties = component.getPropertyIterator();
                while ( componentProperties.hasNext() ) {
                    final Property componentProperty = componentProperties.next();
                    classAuditData.getPropertiesGroupMapping().put( componentProperty.getName(), property.getName() );
                }
            }
        }
    }

    private void addPropertiesFromClass(XClass xClass, ClassAuditData classAuditData) {
        addProperties(
                xClass.getDeclaredProperties( "field" ),
                it -> "field",
                classAuditData.getFields(),
                classAuditData
        );
        addProperties(
                xClass.getDeclaredProperties( "property" ),
                classAuditData.getProperties()::get,
                classAuditData.getProperties().keySet(),
                classAuditData
        );
        if (!classAuditData.getAuditedProperties().isEmpty() ) {
            final XClass superclazz = xClass.getSuperclass();
            if ( !xClass.isInterface() && !"java.lang.Object".equals( superclazz.getName() ) ) {
                addPropertiesFromClass( superclazz, classAuditData );
            }
        }
    }

    private void addProperties(Iterable<XProperty> xProperties,
                               Function<String, String> accessTypeProvider,
                               Set<String> persistentProperties, ClassAuditData classAuditData) {
        for (XProperty property : xProperties) {
            classAuditData.getPropertyComments().putIfAbsent(property.getName(), getComment(property));
            final String accessType = accessTypeProvider.apply( property.getName() );
            if ( persistentProperties.contains( property.getName() )
                    && !classAuditData.getAuditedProperties().containsKey( property.getName() ) ) {
                final Value propertyValue = classAuditData.getPropertyMap().get( property.getName() ).getValue();
                if ( propertyValue instanceof Component ) {
                    this.addFromComponentProperty( property, accessType, (Component) propertyValue, classAuditData );
                }
                else {
                    this.addFromNotComponentProperty( property, accessType, classAuditData );
                }
            }
            else if ( classAuditData.getPropertiesGroupMapping().containsKey( property.getName() ) ) {
                final String embeddedName = classAuditData.getPropertiesGroupMapping().get( property.getName() );
                if ( !classAuditData.getAuditedProperties().containsKey( embeddedName ) ) {
                    final Value propertyValue = classAuditData.getPropertyMap().get( embeddedName ).getValue();
                    this.addFromPropertiesGroup(
                            embeddedName,
                            property,
                            accessType,
                            (Component) propertyValue,
                            classAuditData
                    );
                }
            }
        }
    }

    private void addFromPropertiesGroup(String embeddedName, XProperty property, String accessType, Component propertyValue, ClassAuditData classAuditData) {
        final boolean isAudited = validateAuditedColumn( property);
        if ( isAudited ) {
            log.warn("Component will to do!!!!!!!!!!!!");
        }
    }

    private boolean validateAuditedColumn(XProperty property) {
        NotAudited notAudited = property.getAnnotation(NotAudited.class);
        if (notAudited != null) {
            return false;
        }
        final Version jpaVer = property.getAnnotation( Version.class );
        if (jpaVer != null) {
            return false;
        }
        return true;
    }

    private void addFromNotComponentProperty(XProperty property, String accessType, ClassAuditData classAuditData) {
        final boolean isAudited = validateAuditedColumn( property);

        if ( isAudited ) {
            final Pair propertyData = Pair.make(getComment(property), accessType);
            classAuditData.getAuditedProperties().put( property.getName(), propertyData );
        }
    }

    private void addFromComponentProperty(
            XProperty property,
            String accessType,
            Component propertyValue,
            ClassAuditData classAuditData
            ) {
        final boolean isAudited = validateAuditedColumn( property);

        if ( isAudited ) {
            log.warn("Component will to do!!!!!!!!!!!!");
        }
    }

    private String getComment(XProperty property) {
        ColumnComment columnComment = property.getAnnotation(ColumnComment.class);
        Column column = property.getAnnotation(Column.class);
        return columnComment != null ? columnComment.value() : (column != null ? column.name() : property.getName());
    }

    public boolean isAuditable(String entityName) {
        return auditEntityAuditData.containsKey(entityName) && auditEntityAuditData.get(entityName).getAuditedProperties().size() > 0;
    }

    public boolean hasAuditedEntities() {
        return auditEntityAuditData.size() > 0;
    }

    public String getAuditStrategyName() {
        return auditStrategyName;
    }
}
