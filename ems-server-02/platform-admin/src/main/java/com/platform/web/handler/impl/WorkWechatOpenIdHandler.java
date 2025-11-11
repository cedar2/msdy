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
public class WorkWechatOpenIdHandler implements OpenIdHandler {

    /**
     * 企业微信，获取 UserId（openId）
     */
    private static String getWorkWechatOpenId(String appId, String appSecret, String code) {
        SysUser temp = new SysUser();
        temp.setWorkWechatAppkey(appId);
        temp.setWorkWechatAppsecret(appSecret);
        temp.setCode(code);
        JSONObject qyUserInfo = GetWeiXinCode.getQyUserInfo(temp);
        // qyUserInfo: {"errcode":0,"errmsg":"ok","userid":"ZhangSan"}
        return qyUserInfo.getString("userid");
    }

    public String getOpenId(String appId,
                            String appSecret,
                            String code,
                            SysUser user) {
        // 获取企业微信的 openId
        String workWechatOpenId = getWorkWechatOpenId(appId, appSecret, code);
        // 如果 openId 不为空，且不与原来的 openId 相同
        if (workWechatOpenId != null && !workWechatOpenId.equals(user.getWorkWechatOpenid())) {
            return workWechatOpenId;
        }

        return null;
    }

    @Override
    public BiConsumer<Long, String> saveOpenIdMethod(RemoteUserService remoteUserService) {
        return remoteUserService::platformSaveWorkWechatUserId;
    }
}
