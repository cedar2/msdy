package com.platform.web.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.security.utils.dingtalk.GetDingtalkCode;
import com.platform.web.handler.OpenIdHandler;

import java.util.function.BiConsumer;

/**
 * @author Straw
 * @date 2023/1/10
 */
public class DingTalkOpenIdHandler implements OpenIdHandler {

    /**
     * 钉钉，获取 userId（openId）
     */
    private static String getDingTalkUserId(String appId, String appSecret, String code) {
        SysUser temp = new SysUser();
        temp.setDingtalkAppkey(appId);
        temp.setDingtalkAppsecret(appSecret);
        temp.setCode(code);
        JSONObject userInfo = GetDingtalkCode.getDdUserInfo(temp);
        return userInfo.getString("userid");
    }

    @Override
    public String getOpenId(String appId, String appSecret, String code, SysUser user) {
        // 获取钉钉的 openId
        String dingTalkUserId = getDingTalkUserId(appId, appSecret, code);
        // 如果 openId 不为空，且不与原来的 openId 相同
        if (dingTalkUserId != null && !dingTalkUserId.equals(user.getDingtalkOpenid())) {
            return dingTalkUserId;
        }

        return null;
    }

    @Override
    public BiConsumer<Long, String> saveOpenIdMethod(RemoteUserService remoteUserService) {
        // 保存 openId
        return remoteUserService::platformSaveDingTalkUserId;
    }
}
