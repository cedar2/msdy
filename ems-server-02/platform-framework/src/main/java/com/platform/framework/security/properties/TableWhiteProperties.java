package com.platform.framework.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户插件白名单表
 * @author cwp
 * @date 2021-03-19
 */
@Configuration
@ConfigurationProperties(prefix = "table")
public class TableWhiteProperties {

    /**
     * 租户插件不过滤此处的白名单
     */
    private List<String> whites = new ArrayList<>();

    public List<String> getWhites() {
        return whites;
    }

    public void setWhites(List<String> whites) {
        this.whites = whites;
    }
}
