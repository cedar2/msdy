package com.platform.ems.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据角色权限字段过滤注解
 *
 * @author chenkw
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleDataScope {

    /**
     * 接口参数位置
     */
    public int loc() default 0;

    /**
     * 权限对象编码
     */
    public String objectCode() default "";


}
