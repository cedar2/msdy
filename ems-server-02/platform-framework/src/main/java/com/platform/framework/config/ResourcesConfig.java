package com.platform.framework.config;

import java.util.concurrent.TimeUnit;

import com.platform.framework.config.properties.WebProperties;
import com.platform.framework.security.handle.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.platform.common.config.PlatformConfig;
import com.platform.common.constant.Constants;
import com.platform.framework.interceptor.RepeatSubmitInterceptor;

import javax.annotation.Resource;

/**
 * 通用配置
 *
 * @author platform
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer
{
    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    @Resource
    private WebProperties webProperties;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 根据不同包匹配表达式，添加各自的统一前缀
        configurePathMatch(configurer, webProperties.getEms());
        configurePathMatch(configurer, webProperties.getSystem());
        configurePathMatch(configurer, webProperties.getFile());
        configurePathMatch(configurer, webProperties.getFlow());
    }

    /**
     * API 前缀：实现指定的controller 提供的 RESTFul API 的统一前缀
     *
     * 意义：通过该前缀，避免Swagger,Actuator 意外通过Nginx暴露出来给外部，带来安全性问题
     *      这样Nginx只需配置转发到 指定统一前缀 的所有接口即可
     * @see org.springframework.util.AntPathMatcher
     * @param configurer
     * @param api
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        // 创建路径匹配类，指定以'.'分隔
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        // 指定匹配前缀
        // 满足：类上有RestController注解 && 该类的包名匹配指定的自定义包的表达式
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getControllerPath(), clazz.getPackage().getName()));
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + PlatformConfig.getProfile() + "/");

        /** swagger配置 */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());;
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new UserFilter()).addPathPatterns("/**");
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter()
    {
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }
}
