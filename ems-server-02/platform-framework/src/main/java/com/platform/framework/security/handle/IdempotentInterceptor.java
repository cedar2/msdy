package com.platform.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.platform.common.exception.CustomException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.spring.SpringUtils;
import com.platform.common.annotation.Idempotent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * 对方法上标注了幂等请求注解进行幂等校验
 * @author yangqz
 */
@Component
public class IdempotentInterceptor implements HandlerInterceptor {

    RedissonClient redissonClient = SpringUtils.getBean(RedissonClient.class);

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Idempotent annotation = method.getAnnotation(Idempotent.class);
            if (annotation != null) {
                // 判断是否为重复提交
                if (this.isRepeatSubmit(request, annotation)) {
                    throw new CustomException(annotation.message());
                }
            }
            return true;
        }
        return true;
    }

    /**
     * 判断是否重复提交
     * @param request 请求对象
     * @param annotation 幂等注解
     * @return 重复提交请求返回true
     */
    private boolean isRepeatSubmit(HttpServletRequest request, Idempotent annotation) throws IOException, NoSuchAlgorithmException {
        // 用户ID+URI为Redis的Key,请求参数md5摘要为Value
        Long userId = ApiThreadLocalUtil.get().getUserid();
        String uri = request.getRequestURI();
        String key = REPEAT_SUBMIT_KEY + userId + uri;
        RBucket<String> bucket = redissonClient.getBucket(key);
        // 获取请求体参数
        String requestBody = getRequestBody(request);
        if (StringUtils.isBlank(requestBody)){
            requestBody = JSON.toJSONString(request.getParameterMap());
        }
        // redis查询不为null，并且本次的请求参数md5与val相同则为重复请求
        if (StringUtils.isNotBlank(bucket.get())){
            return bucket.get().equals(jdkMD5(requestBody));
        }
        // 如果redis中没有数据，将本次请求参数存入Redis，考虑到并发情况，trySet 如果已经存在则返回false,代表重复请求
        return !bucket.trySet(jdkMD5(requestBody), annotation.interval(), TimeUnit.MILLISECONDS);
    }

    /**
     * 读取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getReader());
    }

    /**
     * MD5摘要并转换为字符串
     */
    private static String jdkMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] mdBytes = messageDigest.digest(str.getBytes());
        return DatatypeConverter.printHexBinary(mdBytes);
    }

}
