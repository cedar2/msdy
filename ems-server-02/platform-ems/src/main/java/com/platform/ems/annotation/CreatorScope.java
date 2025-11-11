package com.platform.ems.annotation;

import java.lang.annotation.*;

/**
 * 创建人字段过滤注解
 *
 * @author platform
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreatorScope {

    /**
     * 需要过滤的字段名
     */
    public String fieldName() default "";

    /**
     * 接口参数位置
     */
    public int loc() default 0;

    /**
     * 过滤的权限标识
     */
    public String perms() default "";


    /**
     * 备注
     */
    public String note() default "";
}
