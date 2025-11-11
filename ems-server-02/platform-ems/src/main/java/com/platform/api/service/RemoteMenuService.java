package com.platform.api.service;

import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysMenu;

/**
 * 文件服务
 *
 * @author linhongwei
 */
public interface RemoteMenuService {
    /**
     * 根据菜单编号获取详细信息
     */
    public R<SysMenu> getInfo(Long menuId);

    /**
     * 根据菜单编号获取详细信息
     */
    public R<SysMenu> getInfoByName(SysMenu menu);

}
