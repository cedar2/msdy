package com.platform.api.service.impl;

import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.system.controller.SysMenuController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author xfzz
 */
@Service
@SuppressWarnings("all")
public class RemoteMenuServiceImpl implements RemoteMenuService {

    @Autowired
    private SysMenuController systemMenuController;

    @Override
    public R<SysMenu> getInfo(Long menuId) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemMenuController = context.getBean(SysMenuController.class);
        SysMenu sysMenu = (SysMenu) systemMenuController.getInfo(menuId).get(AjaxResult.DATA_TAG);
        return R.ok(sysMenu);
    }

    @Override
    public R<SysMenu> getInfoByName(SysMenu menu) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemMenuController = context.getBean(SysMenuController.class);
        return systemMenuController.getInfoByName(menu);
    }

}
