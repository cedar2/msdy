package com.platform.system.dingding.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DingdingConfig {

    private String appkey = "dinge9rozhgtuishgt8i";

    private String secret = "PGYtV9Qh6KTg4HyIJFK-icPQ2KKq4Rys-wnj2CA6fzMdEK0jfyMOlOFIpLMfR-tn";

    private String url = "https://oapi.dingtalk.com";

    private String tokenSuffix = "/gettoken";

    private String userInfoSuffix = "/user/getuserinfo";

    public String getUserInfoSuffix() {
        return url + userInfoSuffix;
    }

    public void setUserInfoSuffix(String userInfoSuffix) {
        this.userInfoSuffix = userInfoSuffix;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTokenSuffix() {
        return url + tokenSuffix;
    }

    public void setTokenSuffix(String tokenSuffix) {
        this.tokenSuffix = tokenSuffix;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
