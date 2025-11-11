package com.platform.web.domain;

import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Straw
 * @date 2023/1/9
 */
@Data
@Accessors(chain = true)
public class OpenIdRequest {

    /**
     * 系统用户，openId存入此实体类
     */
    SysUser sysUser;

    /**
     * 租户信息，通过此租户获取appId和appSecret
     */
    SysClient sysClient;

    /**
     * 临时授权码
     */
    String code;

    public static OpenIdRequest of(LoginUser loginUser, SysClient client, String code) {
        return new OpenIdRequest().setSysUser(loginUser.getSysUser())
                .setSysClient(client)
                .setCode(code);
    }

}
