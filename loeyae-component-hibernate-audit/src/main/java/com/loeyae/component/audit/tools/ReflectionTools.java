package com.loeyae.component.audit.tools;

import com.loeyae.component.audit.entities.Pair;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.service.ServiceRegistry;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

/**
 * ReflectionTools
 *
 * @author ZhangYi
 */
public abstract class ReflectionTools {
    private static final Map<Pair<Class, String>, Getter> GETTER_CACHE = new ConcurrentReferenceHashMap<>(
            10,
            ConcurrentReferenceHashMap.ReferenceType.SOFT,
            ConcurrentReferenceHashMap.ReferenceType.SOFT
    );
    private static final Map<Pair<Class, String>, Setter> SETTER_CACHE = new ConcurrentReferenceHashMap<>(
            10,
            ConcurrentReferenceHashMap.ReferenceType.SOFT,
            ConcurrentReferenceHashMap.ReferenceType.SOFT
    );

    private static PropertyAccessStrategy getAccessStrategy(Class<?> cls, ServiceRegistry serviceRegistry, String accessorType) {
        return serviceRegistry.getService(PropertyAccessStrategyResolver.class)
                .resolvePropertyAccessStrategy(cls, accessorType, null);
    }

    public static Getter getGetter(Class cls, String propertyName, String accessorType, ServiceRegistry serviceRegistry) {
        final Pair<Class, String> key = Pair.make(cls, propertyName);
        Getter value = GETTER_CACHE.get(key);
        if (value == null) {
            value = getAccessStrategy(cls, serviceRegistry, accessorType).buildPropertyAccess(cls, propertyName).getGetter();
            // It's ok if two getters are generated concurrently
            GETTER_CACHE.put(key, value);
        }

        return value;
    }

    public static Setter getSetter(Class cls, String propertyName, String accessorType, ServiceRegistry serviceRegistry) {
        final Pair<Class, String> key = Pair.make(cls, propertyName);
        Setter value = SETTER_CACHE.get(key);
        if (value == null) {
            value = getAccessStrategy(cls, serviceRegistry, accessorType).buildPropertyAccess(cls, propertyName).getSetter();
            // It's ok if two setters are generated concurrently
            SETTER_CACHE.put(key, value);
        }

        return value;
    }

    /**
     * @param clazz        Source class.
     * @param propertyName Property name.
     * @return Property object or {@code null} if none with expected name has been found.
     */
    public static XProperty getProperty(XClass clazz, String propertyName) {
        XProperty property = getProperty(clazz, propertyName, "field");
        if (property == null) {
            property = getProperty(clazz, propertyName, "property");
        }
        return property;
    }

    /**
     * @param clazz        Source class.
     * @param propertyName Property name.
     * @param accessType   Expected access type. Legal values are <i>field</i> and <i>property</i>.
     * @return Property object or {@code null} if none with expected name and access type has been found.
     */
    public static XProperty getProperty(XClass clazz, String propertyName, String accessType) {
        for (XProperty property : clazz.getDeclaredProperties(accessType)) {
            if (propertyName.equals(property.getName())) {
                return property;
            }
        }
        return null;
    }

    /**
     * Locate class with a given name.
     *
     * @param <T>                typename
     * @param name               Fully qualified class name.
     * @param classLoaderService Class loading service. Passing {@code null} is "allowed", but will result in
     *                           TCCL usage.
     * @return The cass reference.
     * @throws ClassLoadingException Indicates the class could not be found.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String name, ClassLoaderService classLoaderService)
            throws ClassLoadingException {
        try {
            if (classLoaderService != null) {
                return classLoaderService.classForName(name);
            } else {
                return (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(name);
            }
        } catch (Exception e) {
            throw new ClassLoadingException("Unable to load class [" + name + "]", e);
        }
    }
}
