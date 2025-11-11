package com.platform.common.core.experimental.annotation;

import java.lang.annotation.*;

/**
 * 表明某个类或者方法支持使用【实验性功能】
 *
 * @author Straw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface PlatformExperimental {

    /**
     * 表示 本方法或类 支持的【实验性功能】的类
     */
    Class<?>[] enableExperimentalClass() default {};


    /**
     * 表示 本方法或类 对 PlatformExperimental 注解的描述信息
     */
    String description() default "";

}
