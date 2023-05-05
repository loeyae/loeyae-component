package com.loeyae.component.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Audited
 *
 * @author ZhangYi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Audited {

    /**
     * 业务ID
     *
     * @return String
     */
    String bizId() default "id";

    /**
     * 模块名称
     *
     * @return 模块名称
     */
    String moduleName();

    /**
     * 表名
     *
     * @return 表名
     */
    String tableName();

    /**
     * 扩展信息
     *
     * @return 扩展信息
     */
    String[] extra() default {};
}
