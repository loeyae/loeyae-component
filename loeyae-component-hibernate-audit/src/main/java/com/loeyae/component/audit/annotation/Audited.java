package com.loeyae.component.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Audited
 *
 * @author ZhangYi<loeyae @ gmail.com>
 * @version 1.0
 * @date 2023/4/20
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

}
