package com.platform.common.core.experimental.advice;

import com.platform.common.core.experimental.annotation.PlatformExperimental;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractControllerResponseAdvice {
    public static final Set<Class<?>> allowResultClazz = new HashSet<>();

    public static void addAllowedResultClazz(Class<?> clazz) {
        allowResultClazz.add(clazz);
    }

    /**
     * 是否支持对某个Controller的方法进行应用
     * <p>
     * 条件1：该方法的返回值不在 allowResultClazz 中 <p>
     * 条件2：该方法上或者该方法所在类上标注了 @Experimental 注解
     */
    public boolean supports(MethodParameter param,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        // 条件1：该方法的返回值不在 allowResultClazz 中 <p>
        boolean isAllowedReturnType = allowResultClazz.contains(param.getParameterType());
        if (isAllowedReturnType) {
            return false;
        }

        // 条件2：该方法上或者该方法所在类上标注了 @Experimental 注解
        return checkEnableExperimental(param);
    }

    private boolean checkEnableExperimental(MethodParameter param) {
        Class<PlatformExperimental> checkedClazz = PlatformExperimental.class;
        // 先检查方法
        PlatformExperimental expAnno = param.getMethodAnnotation(checkedClazz);
        if (expAnno == null) {
            // 方法上没有 @Experimental，尝试获取类上的注解
            expAnno = param.getDeclaringClass().getAnnotation(checkedClazz);
        }

        // 再次判断是否存在 @Experimental 注解
        if (expAnno == null) {
            // 没有注解
            return false;
        }

        // 下面判断本类是否在 @Experimental 注解的 enableExperimentalClass 中
        Class<?> thisClazz = this.getClass();
        Class<?>[] enableExperimentalClass = expAnno.enableExperimentalClass();

        for (Class<?> clazz : enableExperimentalClass) {
            if (thisClazz.equals(clazz)) {
                // 本对象的 class 在 enableExperimentalClass 里，即允许本对象进行处理。
                return true;
            }
        }

        return false;
    }

    public Object beforeBodyWrite(Object data,
                                  MethodParameter param,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 方法声明的返回值类型
        Type declareType = param.getGenericParameterType();

        // 特殊判断，原因在下面
        if (String.class.equals(declareType) || selectedConverterType == StringHttpMessageConverter.class) {
            /*
             * 对于返回值是String的方法，SpringMVC 会使用 StringHttpMessageConverter 对返回值进行转换
             * StringHttpMessageConverter 限制了 ControllerAdvice 的再处理返回值也必须是 String
             * 不然会报错
             */
            return toJson(toStandardResult(data));
        }

        // 用ResultVo包装数据
        return toStandardResult(data);
    }

    public abstract String toJson(Object o);

    public abstract Object toStandardResult(Object o);

}

