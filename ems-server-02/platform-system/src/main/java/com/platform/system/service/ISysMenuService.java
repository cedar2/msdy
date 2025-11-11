package com.platform.system.service;

import java.util.List;
import java.util.Set;
import com.platform.common.core.domain.TreeSelect;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.entity.SysTable;
import com.platform.system.domain.SysRoleMenu;
import com.platform.system.domain.dto.request.MenuRequest;
import com.platform.system.domain.dto.response.MenuResponse;
import com.platform.system.domain.vo.RouterVo;

/**
 * 菜单 业务层
 *
 * @author platform
 */
public interface ISysMenuService
{
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色id和访问类型查询菜单
     * @param roleId
     * @param pType
     * @return
     */
    Set<String> selectMenuPermsByRoleIdPType(Long roleId, String pType);

    /**
     * 根据用户id和访问类型查询菜单
     * @param userId
     * @param pType
     * @return
     */
    Set<String> selectMenuPermsByUserIdPType(Long userId, String pType);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<Integer> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 根据菜单名称查询信息
     *
     * @param menuName 菜单名称
     * @return 菜单信息
     */
    public SysMenu selectMenuByName(String menuName);

    /**
     * 角色权限明细报表
     */
    public List<MenuResponse> getMenuReport(MenuRequest menuRequest);
    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 查询是否有某个权限标识
     *
     * @param roleMenu 查询参数
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkRoleExistMenu(SysRoleMenu roleMenu);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public String checkMenuNameUnique(SysMenu menu);

    /**
     * 校验菜单权限标识是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public String checkMenuPermsUnique(SysMenu menu);

    List<SysMenu> getShowRouters(SysMenu sysMenu);

    /**
     * 数据库表名下拉框
     *
     * @param table 实体
     * @return 结果
     */
    List<SysTable> selectDbTableList(SysTable table);
}
