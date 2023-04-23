package com.loeyae.component.audit.entities;

import java.util.Map;

/**
 * PersistentCollectionData
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
 */
public class PersistentCollectionData {
    private final String entityName;
    private final Map<String, Object> data;
    private final Object changedElement;

    public PersistentCollectionData(String entityName, Map<String, Object> data, Object changedElement) {
        this.entityName = entityName;
        this.data = data;
        this.changedElement = changedElement;
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getChangedElement() {
        if ( changedElement instanceof Pair ) {
            return ( (Pair) changedElement ).getSecond();
        }

        if ( changedElement instanceof Map.Entry ) {
            return ( (Map.Entry) changedElement ).getValue();
        }

        return changedElement;
    }

    public Object getChangedElementIndex() {
        if ( changedElement instanceof Pair) {
            return ( (Pair) changedElement ).getFirst();
        }

        if ( changedElement instanceof Map.Entry ) {
            return ( (Map.Entry) changedElement ).getKey();
        }

        return null;
    }
}
