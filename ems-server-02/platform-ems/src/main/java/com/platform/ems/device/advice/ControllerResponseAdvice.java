package com.platform.ems.device.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.exception.CheckedException;
import com.platform.common.core.experimental.advice.AbstractControllerResponseAdvice;
import com.platform.common.core.domain.AjaxResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 返回值包装器
 *
 * @author Straw
 */
@RestControllerAdvice(basePackages = "com.platform.ems.device.controller")
public class ControllerResponseAdvice extends AbstractControllerResponseAdvice implements ResponseBodyAdvice<Object> {

    final static ObjectMapper mapper = new ObjectMapper();

    static {
        addAllowedResultClazz(AjaxResult.class);
    }

    @Override
    public boolean supports(MethodParameter param,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return !allowResultClazz.contains(param.getParameterType());
    }

    public Object beforeBodyWrite(Object data,
                                  @NonNull MethodParameter param,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        return super.beforeBodyWrite(data, param, selectedContentType, selectedConverterType, request, response);
    }

    @Override
    public String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new CheckedException("toJson失败: " + e.getMessage());
        }
    }

    @Override
    public Object toStandardResult(Object o) {
        return AjaxResult.success(o);
    }
}
