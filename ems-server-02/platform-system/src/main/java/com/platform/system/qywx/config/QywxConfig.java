package com.platform.system.qywx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QywxConfig {

    private String appId = "ww3e6804c2405881e1";

    private int agentid = 1000003;

    private String secret = "9pgveoM02S86O3hqLtQGIJkimfEP0Rc6sWK_RDqNLX8";

    private String url = "https://qyapi.weixin.qq.com";

    /**
     * 获取token
     */
    private String tokenApi = "/cgi-bin/gettoken";

    /**
     * 获取所有部门
     */
    private String departmentListApi = "/cgi-bin/department/list";

    /**
     * 获取部门成员
     */
    private String simplelistApi = "/cgi-bin/user/simplelist";

    /**
     * 获取访问用户身份
     */
    private String userInfoApi = "/cgi-bin/user/getuserinfo";

    /**
     * 获取用户详细详细
     */
    private String userListApi = "/cgi-bin/user/list";

    /**
     * 根据userId 获取用户信息
     */
    private String getUserApi = "/cgi-bin/user/get";

    /**
     * 修改用户信息
     */
    private String updateUserApi = "/cgi-bin/user/update";

    /**
     * 获取打卡记录
     */
    private String checkinDataApi = "/cgi-bin/checkin/getcheckindata";

    /**
     * 发消息模板
     */
    private String sendApi = "/cgi-bin/message/send";

    public String getTokenApi() {
        return url + tokenApi;
    }

    public String getDepartmentListApi() {
        return url + departmentListApi;
    }

    public String getSimplelistApi() {
        return url + simplelistApi;
    }

    public String getUserInfoApi() {
        return url + userInfoApi;
    }

    public String getUserListApi() {
        return url + userListApi;
    }

    public String getSendApi() {
        return url + sendApi;
    }

    public String getUserApi() {
        return url + getUserApi;
    }

    public String getUserUpdateApi() {
        return url + updateUserApi;
    }

    public String getCheckinDataApi() {
        return url + checkinDataApi;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
