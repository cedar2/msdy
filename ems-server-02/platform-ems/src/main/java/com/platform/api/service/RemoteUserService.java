package com.platform.api.service;

import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;

/**
 * 用户服务
 *
 * @author platform
 */
public interface RemoteUserService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    R<LoginUser> getUserInfo(String username);

    /**
     */
    R<LoginUser> autoRegister(SysUser user);

    R<?> platformSaveFeishuOpenId(Long userId, String feishuOpenId);

    R<?> platformSaveDingTalkUserId(Long userId, String dingTalkUserId);

    R<?> platformSaveWorkWechatUserId(Long userId, String workWechatOpenId);

    R<?> platformSaveWechatGzhUserId(Long userId, String wechatGzhOpenId);

}
