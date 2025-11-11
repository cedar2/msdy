package com.platform.ems.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "adress")
public class AdressProperties {

    //易码通地址前缀
    private String ymt;

    public String getYmt() {
        return ymt;
    }

    public void setYmt(String ymt) {
        this.ymt = ymt;
    }
}
