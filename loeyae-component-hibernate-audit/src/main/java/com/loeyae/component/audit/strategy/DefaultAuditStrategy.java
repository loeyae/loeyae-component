package com.loeyae.component.audit.strategy;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.entities.ClassAuditData;
import com.loeyae.component.audit.entities.Pair;
import com.loeyae.component.audit.entities.PersistentCollectionData;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.service.ServiceRegistry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * DefaultAuditStrategy
 *
 * @author ZhangYi
 */
@Slf4j
public class DefaultAuditStrategy implements AuditStrategy {

    private ServiceRegistry serviceRegistry;

    @Override
    public void postInitialize(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void perform(Session session, String entityName, AuditService auditService, String model, Serializable id, Object originalData, Object changes, Object changedData) {
        Object bizId = getBizId(auditService, entityName, id, originalData, changedData);
        log.debug("[{}] [{}] of BizId [{}] Audit Log: Form [{}] Changes [{}] To [{}]", model, entityName, bizId, JSONUtil.toJsonStr(originalData), JSONUtil.toJsonStr(changes), JSONUtil.toJsonStr(changedData));
    }

    @Override
    public void performCollectionChange(Session session, String entityName, String propertyName, AuditService auditService, String model, PersistentCollectionData originalData, PersistentCollectionData changes, PersistentCollectionData changedData) {
        log.debug("[{}] [{}] Audit Log: Form [{}] Changes [{}] To [{}]", model, entityName, originalData, changes, changedData);
    }

    protected Map<String, Pair<String, Object>> buildDataColumnComment(AuditService auditService, String entityName, Object data) {
        Map<String, Pair<String, Object>> buildData = new HashMap<>();
        if (data instanceof Map) {
            ClassAuditData classAuditData = auditService.getConfiguration().get(entityName);
            ((Map<String, ?>) data).forEach((key, value) -> buildData.put(key, Pair.make(classAuditData.getPropertyComments().getOrDefault( key, key ), value)));
        }
        return buildData;
    }

    protected Map<String,  Object> replaceColumnComment(AuditService auditService, String entityName, Object data) {
        Map<String, Object> buildData = new HashMap<>();
        if (data instanceof Map) {
            ClassAuditData classAuditData = auditService.getConfiguration().get(entityName);
            ((Map<String, ?>) data).forEach((key, value) -> buildData.put(classAuditData.getPropertyComments().getOrDefault( key, key ), value));
        }
        return buildData;
    }

    protected Object getBizId(AuditService auditService, String entityName,  Serializable id, Object originalData, Object changedData) {
        String bizId = auditService.getConfiguration().get(entityName).getBizId();
        if ("id".equals(bizId)) {
            return id;
        }
        if (null != changedData) {
            return getBizIdFromData(changedData, bizId, id);
        }
        return getBizIdFromData(originalData, bizId, id);
    }

    private Object getBizIdFromData(Object data, String bizIdName, Serializable id) {
        if (data instanceof Map) {
            return MapUtil.get((Map<?, ?>) data, bizIdName, Object.class, id);
        }
        return id;
    }
}
