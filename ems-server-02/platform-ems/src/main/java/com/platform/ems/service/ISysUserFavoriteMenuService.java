package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.SysUserFavoriteMenu;

/**
 * 用户收藏菜单Service接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface ISysUserFavoriteMenuService{
    /**
     * 查询用户收藏菜单
     *
     * @param id 用户收藏菜单ID
     * @return 用户收藏菜单
     */
    public SysUserFavoriteMenu selectSysUserFavoriteMenuById(String id);

    /**
     * 查询用户收藏菜单列表
     *
     * @return 用户收藏菜单集合
     */
    public List<SysUserFavoriteMenu> selectSysUserFavoriteMenuList(SysUserFavoriteMenu sysUserFavoriteMenu);

    /**
     * 新增用户收藏菜单
     *
     * @param sysUserFavoriteMenu 用户收藏菜单
     * @return 结果
     */
    public int insertSysUserFavoriteMenu(SysUserFavoriteMenu sysUserFavoriteMenu);

    /**
     * 修改用户收藏菜单
     *
     * @param sysUserFavoriteMenu 用户收藏菜单
     * @return 结果
     */
    public int updateSysUserFavoriteMenu(SysUserFavoriteMenu sysUserFavoriteMenu);


    /**
     * 批量删除用户收藏菜单
     *
     * @param favoriteMenuSids 需要删除的用户收藏菜单ID
     * @return 结果
     */
    public int deleteSysUserFavoriteMenuByIds(List<String> favoriteMenuSids);


}
