package com.platform.common.annotation;

import java.lang.annotation.*;

/**
 * 标识接口需要保证幂等
 * @author yangqz
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    int interval() default 5000;

    /**
     * 提示消息
     */
    String message() default "不允许重复操作，请稍候再试";
}
