package com.platform.web.service;

import cn.hutool.core.lang.Tuple;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.web.domain.OpenIdRequest;
import com.platform.web.enums.PlatformType;
import com.platform.web.handler.OpenIdHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Straw
 * @date 2023/1/9
 */

@Service
public class PlatformOpenIdService {

    @Resource
    RemoteUserService remoteUserService;

    private static Tuple newTuple(String appId,
                                  String appSecret) {
        return new Tuple(appId, appSecret);
    }

    /**
     * 尝试获取openId，如果获取成功，存入数据库
     *
     * @param platform 平台类型，例如飞书是feishu
     * @param req      请求实体类，封装了服务层一切需要的信息
     */
    public R<?> tryGetOpenIdAndSaveDB(OpenIdRequest req, PlatformType platform) {
        SysClient sysClient = req.getSysClient();
        // 变化的
        Tuple params;
        switch (platform) {
            case Feishu:
                params = newTuple(sysClient.getFeishuAppId(),
                        sysClient.getFeishuAppSecret());
                break;

            case DingTalk:
                params = newTuple(sysClient.getDingtalkAppkey(),
                        sysClient.getDingtalkAppsecret());
                break;
            case WorkWechat:
                params = newTuple(sysClient.getWorkWechatAppkey(),
                        sysClient.getWorkWechatAppsecret());
                break;
            case WechatGzh:
                params = newTuple(sysClient.getWxGzhAppkey(),
                        sysClient.getWxGzhAppsecret());
                break;

            default:
                throw new NotImplementedException(platform.toString());
        }
        // 不变的
        OpenIdHandler openIdHandler = platform.openIdHandler;
        String appId = params.get(0);
        String appSecret = params.get(1);
        return openIdHandler.apply(appId, appSecret, req.getCode(), req.getSysUser(), remoteUserService);
    }

}
