package com.platform.common.redis.generator;

import cn.hutool.core.util.StrUtil;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 自定义key生成器
 * @author c
 */

@Component
@SuppressWarnings("all")
public class MyKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb=new StringBuilder();
        if(StrUtil.isNotEmpty(ApiThreadLocalUtil.get().getClientId())){
            sb.append(ApiThreadLocalUtil.get().getClientId());
        }
        if (params.length == 0) {
            if(StrUtil.isNotEmpty(sb.toString())){
                return sb.toString();
            }
            return SimpleKey.EMPTY;
        }
        //Object param = params[0];
        if(StrUtil.isNotEmpty(sb.toString())){
            sb.append(":");
            sb.append("[");
            Object[] p=params.clone();
            sb.append(StringUtils.arrayToCommaDelimitedString(p));
            sb.append("]");
            return sb.toString();
        }
        return new SimpleKey(params);
    }

}
