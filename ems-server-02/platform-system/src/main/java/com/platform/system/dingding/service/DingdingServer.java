package com.platform.system.dingding.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.platform.system.dingding.config.DingdingConfig;
import com.platform.system.qywx.TokenCache;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class DingdingServer {

    @Resource
    private DingdingConfig dingdingConfig;

    private TokenCache tokenCache;

    /**
     * 获取token
     */
    public String getToken() throws RuntimeException {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(dingdingConfig.getTokenSuffix());
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(dingdingConfig.getAppkey());
            request.setAppsecret(dingdingConfig.getSecret());
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            log.error("OapiGettokenResponse : {}", response);
            return response.getAccessToken();
        } catch (ApiException e) {
            log.error("getAccessToken failed", e);
            throw new RuntimeException();
        }

    }

    /**
     * 获取用户信息
     */
    public OapiUserGetuserinfoResponse getUserInfo(String requestAuthCode, String access_token) throws RuntimeException {
        log.info("dingdingServer.getUserInfo，参数：code : {} , token : {}", requestAuthCode, access_token);
        // 获取用户信息
        DingTalkClient client = new DefaultDingTalkClient(dingdingConfig.getUserInfoSuffix());
        log.info("dingdingServer.getUserInfo：client : {} ", client);
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(requestAuthCode);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, access_token);
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        log.info("WxCpController.userInfo.dingdingServer.getUserInfo，结果：{}", response);
        return response;
    }

    /**
     * 获取用户信息
     */
    public String getUserId(String requestAuthCode, String access_token) throws RuntimeException {
        OapiUserGetuserinfoResponse response = getUserInfo(requestAuthCode, access_token);
        // 查询得到当前用户的userId
        // 获得到userId之后应用应该处理应用自身的登录会话管理（session）,避免后续的业务交互（前端到应用服务端）每次都要重新获取用户身份，提升用户体验
        return response.getUserid();
    }

}
