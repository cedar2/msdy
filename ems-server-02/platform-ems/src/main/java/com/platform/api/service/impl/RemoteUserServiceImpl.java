package com.platform.api.service.impl;

import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.system.controller.SysUserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class RemoteUserServiceImpl implements RemoteUserService {

    @Autowired
    private SysUserController systemUserController;

    @Override
    public R<LoginUser> getUserInfo(String username) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemUserController = context.getBean(SysUserController.class);
        return systemUserController.info(username);
    }

    @Override
    public R<LoginUser> autoRegister(SysUser user) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemUserController = context.getBean(SysUserController.class);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return systemUserController.autoRegister(user);
    }

    @Override
    public R<?> platformSaveFeishuOpenId(Long userId, String feishuOpenId) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemUserController = context.getBean(SysUserController.class);
        return systemUserController.platformSaveFeishuOpenId(userId, feishuOpenId);
    }

    @Override
    public R<?> platformSaveDingTalkUserId(Long userId, String dingTalkUserId) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemUserController = context.getBean(SysUserController.class);
        return systemUserController.platformSaveDingTalkUserId(userId, dingTalkUserId);
    }

    @Override
    public R<?> platformSaveWorkWechatUserId(Long userId, String workWechatOpenId) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemUserController = context.getBean(SysUserController.class);
        return systemUserController.platformSaveWorkWechatUserId(userId, workWechatOpenId);
    }

    @Override
    public R<?> platformSaveWechatGzhUserId(Long userId, String wechatGzhOpenId) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemUserController = context.getBean(SysUserController.class);
        return systemUserController.platformSaveWechatGzhUserId(userId, wechatGzhOpenId);
    }
}
