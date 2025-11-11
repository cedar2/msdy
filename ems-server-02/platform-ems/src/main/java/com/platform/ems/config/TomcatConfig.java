package com.platform.ems.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class TomcatConfig {

/**
    这边也许可以引用网关那边的转发配置
    @Value("${spring.servlet.multipart.max-file-size}")
    private String MaxFileSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String MaxRequestSize;
*/

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小   // KB,MB
        factory.setMaxFileSize(DataSize.parse("50MB"));
        /// 总上传数据大小  // KB,MB
        factory.setMaxRequestSize(DataSize.parse("100MB"));
        return factory.createMultipartConfig();
    }
}
