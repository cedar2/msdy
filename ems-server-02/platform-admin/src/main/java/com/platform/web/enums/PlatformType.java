package com.platform.web.enums;

import com.platform.web.handler.*;
import com.platform.web.handler.impl.DingTalkOpenIdHandler;
import com.platform.web.handler.impl.FeishuOpenIdHandler;
import com.platform.web.handler.impl.WechatGzhOpenIdHandler;
import com.platform.web.handler.impl.WorkWechatOpenIdHandler;

/**
 * @author Straw
 * @date 2023/1/9
 */
public enum PlatformType {

    Feishu(FeishuOpenIdHandler.class),

    DingTalk(DingTalkOpenIdHandler.class),

    WorkWechat(WorkWechatOpenIdHandler.class),

    WechatGzh(WechatGzhOpenIdHandler.class);

    // Wechat(WechatGzhOpenIdHandler.class),


    public final OpenIdHandler openIdHandler;

    PlatformType(Class<? extends OpenIdHandler> handlerClass) {
        try {
            this.openIdHandler = handlerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
