package com.platform.flowable.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author Straw
 * @date 2023/2/28
 */
@Configuration
@ConfigurationProperties(prefix = "interceptor.user")
@Data
public class WebConfig {
    List<String> patterns = Collections.emptyList();
}
