package com.platform.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.SysTable;
import com.platform.system.domain.SysLogininfor;
import com.platform.system.domain.dto.request.MenuRequest;
import com.platform.system.domain.dto.response.MenuResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.platform.common.core.domain.entity.SysMenu;
import org.springframework.stereotype.Repository;

/**
 * 菜单表 数据层
 *
 * @author platform
 */

@Mapper
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 数据库表名下拉框
     *
     * @param table 实体
     * @return 结果
     */
    List<SysTable> selectDbTableList(SysTable table);

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    public List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuListByUserId(SysMenu menu);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色id和访问类型查询菜单
     * @param roleId
     * @param pType
     * @return
     */
    List<String> selectMenuPermsByRoleIdPType(@Param("roleId") Long roleId, @Param("pType") String pType);

    /**
     * 根据用户id和访问类型查询菜单
     * @param userId
     * @param pType
     * @return
     */
    List<String> selectMenuPermsByUserIdPType(@Param("userId") Long userId,@Param("pType") String pType);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAll();

    /**
     * 角色权限明细报表
     */
    public List<MenuResponse> getMenuReport(MenuRequest menuRequest);

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    public List<Integer> selectMenuListByRoleId(@Param("roleId") Long roleId,
                                                @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int hasChildByMenuId(Long menuId);

    /**
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改菜单信息
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
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    public SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    /**
     * 校验菜单权限标识是否唯一
     *
     * @param perms 菜单权限标识
     * @return 结果
     */
    public SysMenu checkMenuPermsUnique(@Param("perms") String perms);

    List<SysMenu> getShowRouters(SysMenu sysMenu);

    int deleteMenuId(@Param("roleList") List<Long> roleList, @Param("roleIds") List<Long> roleIds);

    List<SysMenu> selectMobileMenuByUserId(@Param("user_id") Long userId);
}
