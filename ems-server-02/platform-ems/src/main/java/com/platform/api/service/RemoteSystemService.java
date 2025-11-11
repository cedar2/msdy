package com.platform.api.service;

import com.platform.common.core.domain.R;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.system.domain.SysRoleMenu;

/**
 * 系统模块
 *
 * @author c
 */
public interface RemoteSystemService {

    public R<SysDictData> dictData(SysDictData dictData);

    public R<Boolean> isHavePerms(SysRoleMenu roleMenu);

    public AjaxResult get(SysDefaultSettingSystem sysDefaultSettingSystem);
}
