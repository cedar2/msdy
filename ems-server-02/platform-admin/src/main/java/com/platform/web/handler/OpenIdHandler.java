package com.platform.web.handler;

import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.utils.StringUtils;
import org.springframework.lang.Nullable;

import java.util.function.BiConsumer;

/**
 * @author Straw
 * @date 2023/1/10
 */
public interface OpenIdHandler {

    @Nullable
    String getOpenId(String appId,
                     String appSecret,
                     String code,
                     SysUser user);


    BiConsumer<Long, String> saveOpenIdMethod(RemoteUserService remoteUserService);

    default boolean support(String appId, String appSecret) {
        // 不支持空的的appId和appSecret
        return !StringUtils.isAnyEmpty(appId, appSecret);
    }

    default R<?> apply(String appId, String appSecret, String code, SysUser user, RemoteUserService remoteUserService) {
        if (!support(appId, appSecret)) {
            return R.fail();
        }
        try {
            // 获取 openId
            String openId = getOpenId(appId, appSecret, code, user);
            // 没有获取到 openId
            if (openId == null) {
                return R.fail();
            }
            // 写入数据库，远程调用
            saveOpenIdMethod(remoteUserService).accept(user.getUserId(), openId);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail();
        }
    }
}
