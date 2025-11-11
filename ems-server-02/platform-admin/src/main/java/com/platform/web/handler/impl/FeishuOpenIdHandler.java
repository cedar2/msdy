package com.platform.web.handler.impl;

import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.security.utils.feishu.FeishuClientService;
import com.platform.web.handler.OpenIdHandler;

import java.util.function.BiConsumer;

/**
 * @author Straw
 * @date 2023/1/10
 */
public class FeishuOpenIdHandler implements OpenIdHandler {

    public String getOpenId(String appId,
                            String appSecret,
                            String code,
                            SysUser user) {
        // 获得飞书的服务
        FeishuClientService feishuClientService = new FeishuClientService(appId, appSecret);
        // 获得 openId
        String openId = feishuClientService.getOpenIdByCode(code);
        // 如果 openId 不为空，且不与原来的 openId 相同
        if (openId != null && !openId.equals(user.feishuOpenId)) {
            return openId;
        }

        return null;
    }

    @Override
    public BiConsumer<Long, String> saveOpenIdMethod(RemoteUserService remoteUserService) {
        return remoteUserService::platformSaveFeishuOpenId;
    }

}
