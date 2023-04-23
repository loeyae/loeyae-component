package com.loeyae.component.audit.entities;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.mapping.Property;

import java.util.*;

/**
 * ClassAuditData
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
@Data
public class ClassAuditData {

    private String bizId;

    private String moduleName;

    private final Map<String, Property> propertyMap = new HashMap<>();
    private final Map<String, String> propertyComments = new HashMap<>();

    private final Map<String, String> properties = new HashMap<>();

    private final Set<String> fields = new HashSet<>();

    private final Map<String, String> propertiesGroupMapping = new HashMap<>();

    private final Map<String, Pair<String, String>> auditedProperties = new HashMap<>();

    public void mapChanges(SessionImplementor sessionImplementor, Map<String, Object> data, String[] propertyNames, Object[] newValues, Object[] oldValues) {
        for (int i = 0; i < propertyNames.length; i++) {
            final String propName = propertyNames[i];
            Object oldValue = ArrayUtil.get(oldValues, i);
            Object newValue = ArrayUtil.get(newValues, i);
            if (auditedProperties.containsKey(propName) && ObjectUtil.notEqual(oldValue, newValue)) {
                data.put(propName, newValue);
            }
        }
    }

    public void map(SessionImplementor sessionImplementor, Map<String, Object> data, String[] propertyNames, Object[] state) {
        for (int i = 0; i < propertyNames.length; i++) {
            final String propName = propertyNames[i];
            if (auditedProperties.containsKey(propName)) {
                data.put(propName, ArrayUtil.get(state, i));
            }
        }
    }
}
