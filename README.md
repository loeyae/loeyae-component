#loeyae-component
java组件库

## loeyae-component-hibernate-audit
基于hibernate事件，生成审核日志

使用方式:  
maven引入依赖  
```xml
<dependency>  
    <groupId>com.loeyae.component</groupId>  
    <artifactId>loeyae-component-hibernate-audit</artifactId>  
    <version>1.0.2</version>  
</dependency>
```
数据实体增加对应注解  
```java
import com.loeyae.component.audit.annotation.Audited;
import com.loeyae.component.audit.annotation.ColumnComment;
import javax.persistence.*;

@Audited(moduleName = "测试模块", tableName = "测试表")   //审核日志注解，moduleName：用于日志展示的模块名称，tableName: 用于日志展示的表名
@Entity
@Table(name = "test")
public class TestEntity {

    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 名称
     */
    @ColumnComment("名称")                            //用于日志展示的字段名称；如果不设置，将直接取字段名
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 说明
     */
    @ColumnComment("说明")
    @Column(name = "notes")
    private String notes;


}
```

默认日志记录策略为log形式，因此需要自定义日志记录策略  
策略示例：  
```java
package com.loeyae.component.test.jpa.core;

import cn.hutool.json.JSONUtil;
import com.loeyae.component.audit.boot.AuditService;
import com.loeyae.component.audit.strategy.DefaultAuditStrategy;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.io.Serializable;

@Slf4j
public class CustomAuditStrategy extends DefaultAuditStrategy {
    @Override
    public void perform(Session session, String entityName, AuditService auditService, String model, Serializable id, Object originalData, Object changes, Object changedData) {
        log.warn("Custom Strategy");
        Object bizId = getBizId(auditService, entityName, id, originalData, changedData);  //业务id的值。业务id的字段名可在注解中指定，默认为id
        log.debug("[{}] [{}] [{}({})] of BizId: [{}] Audit Log: Form [{}] Changes [{}] To [{}]",
                auditService.getConfiguration().get(entityName).getModuleName(),   //注解中定义的模块名称
                model,                                                             //操作模式：insert、update、delete
                entityName,                                                        //实体全名   
                auditService.getConfiguration().get(entityName).getTableName(),    //注解中定义的表名
                bizId,                                                                       
                JSONUtil.toJsonStr(buildDataColumnComment(auditService, entityName, originalData)),    //原始数据
                JSONUtil.toJsonStr(replaceColumnComment(auditService, entityName, changes)),           //更新的数据
                JSONUtil.toJsonStr(buildDataColumnComment(auditService, entityName, changedData)));    //更新后的数据

    }
}
```
在resource中添加hibernate.properties，并设置策略
```properties
com.loeyae.hibernate.audit.audit_strategy=com.loeyae.component.test.jpa.core.CustomAuditStrategy
```
