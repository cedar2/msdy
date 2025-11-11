package com.platform.ems.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author cwp
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScopeFilter {

    /**
     * 字段名
     */
    public String fieldName() default "";

    /**
     * 表别名
     */
    public String Alias() default "";

    /**
     * 数据对象
     */
    public String dataObject() default "";

    /**
     * 权限字段
     */
    public String fieldCode() default "";

}
