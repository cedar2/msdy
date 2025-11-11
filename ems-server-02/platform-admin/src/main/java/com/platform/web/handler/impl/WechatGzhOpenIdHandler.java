package com.platform.web.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.security.utils.wx.GetWeiXinCode;
import com.platform.web.handler.OpenIdHandler;

import java.util.function.BiConsumer;

/**
 * @author Straw
 * @date 2023/1/10
 */
public class WechatGzhOpenIdHandler implements OpenIdHandler {

    @Override
    public boolean support(String appId, String appSecret) {
        return true;
    }

    @Override
    public String getOpenId(String appId, String appSecret, String code, SysUser user) {
        JSONObject result = GetWeiXinCode.getOpenId(code);
        String openId = result.getString("openid");

        if (openId != null && !openId.equals(user.getWxGzhOpenid())) {
            return openId;
        }
        return null;
    }

    @Override
    public BiConsumer<Long, String> saveOpenIdMethod(RemoteUserService remoteUserService) {
        return remoteUserService::platformSaveWechatGzhUserId;
    }
}
