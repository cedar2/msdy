package com.platform.api.service.impl;

import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.system.controller.SysDefaultSettingSystemController;
import com.platform.system.controller.SysDictDataController;
import com.platform.system.controller.SysMenuController;
import com.platform.system.domain.SysRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@SuppressWarnings("all")
public class RemoteSystemServiceImpl implements RemoteSystemService {

    @Autowired
    private SysDictDataController systemDictDataController;
    @Autowired
    private SysMenuController systemMenuController;
    @Autowired
    private SysDefaultSettingSystemController sysDefaultSettingSystemController;

    @Override
    public R<SysDictData> dictData(SysDictData dictData) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemDictDataController = context.getBean(SysDictDataController.class);
        return systemDictDataController.dictData(dictData);
    }

    @Override
    public R<Boolean> isHavePerms(SysRoleMenu roleMenu) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        systemMenuController = context.getBean(SysMenuController.class);
        boolean response = (boolean) systemMenuController.checkRoleExistMenu(roleMenu).get(AjaxResult.DATA_TAG);
        return R.ok(response);
    }

    @Override
    public AjaxResult get(SysDefaultSettingSystem sysDefaultSettingSystem) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        sysDefaultSettingSystemController = context.getBean(SysDefaultSettingSystemController.class);
        AjaxResult ajaxResult = sysDefaultSettingSystemController.get(sysDefaultSettingSystem);
        Object data = ajaxResult.get(AjaxResult.DATA_TAG);
        // LinkedHashMap map = JSONObject.parseObject(JSONObject.toJSONString(object), LinkedHashMap.class);
        if (data != null) {
            LinkedHashMap map = (LinkedHashMap) data;
            ajaxResult.put(AjaxResult.DATA_TAG, map);
        }
        return ajaxResult;
    }
}
