package com.platform.system.controller;

import java.io.IOException;
import java.util.*;

import com.platform.common.constant.Constants;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysTable;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.domain.SysRoleMenu;
import com.platform.system.domain.dto.request.MenuRequest;
import com.platform.system.domain.dto.response.MenuResponse;
import com.platform.system.service.ISysDictDataService;
import com.platform.system.service.ISysPermissionService;
import com.platform.system.service.impl.SysMenuServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * 菜单信息
 *
 * @author platform
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController extends BaseController
{
    private static final String MENU_TYPE = "F";
    @Autowired
    private SysMenuServiceImpl menuService;
    @Autowired
    private ISysPermissionService permissionService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId) {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 根据菜单名称获取详细信息
     */
    @PostMapping(value = "/getInfo/byName")
    public R<SysMenu> getInfoByName(@RequestBody SysMenu sysMenu) {
        return R.ok(menuService.selectMenuByName(sysMenu.getMenuName()));
    }

    @PostMapping("/list/report")
    @ApiOperation(value = "查询角色权限明细报表",
            notes = "查询角色权限明细报表")
    @ApiResponses(@ApiResponse(code = 200,
            message = "请求成功",
            response = MenuResponse.class))
    public TableDataInfo report(@RequestBody MenuRequest menuRequest) {
        startPage(menuRequest);
        List<MenuResponse> list = menuService.getMenuReport(menuRequest);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出角色权限明细报表",
            notes = "查询角色权限明细报表")
    @PostMapping("/report/export")
    @ApiResponses(@ApiResponse(code = 200,
            message = "请求成功",
            response = MenuResponse.class))
    public void exportRe(HttpServletResponse response, MenuRequest menuRequest) throws IOException {
        List<MenuResponse> list = menuService.getMenuReport(menuRequest);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<MenuResponse> util = new ExcelUtil<>(MenuResponse.class, dataMap);
        util.exportExcel(response, list, "角色权限明细报表");
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/get/roleMenuTreeselect/{userId}")
    public AjaxResult getRoleMenuTreeselect(@PathVariable("userId") Long userId) {
        SysMenu menu = new SysMenu();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysMenu menu) {
        if (!MENU_TYPE.equals(menu.getMenuType()) && UserConstants.NOT_UNIQUE_NUM.equals(menuService.checkMenuNameUnique(
                menu))) {
            return AjaxResult.error("菜单名称已存在");
        }
        if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE_NUM.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error("菜单名称已存在");
        }
        if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return AjaxResult.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 查询是否有某个权限标识
     *
     * @param roleMenu 查询参数
     * @return 结果 true 存在 false 不存在
     */
    @PostMapping("/perms")
    public AjaxResult checkRoleExistMenu(@RequestBody SysRoleMenu roleMenu) {
        return AjaxResult.success(menuService.checkRoleExistMenu(roleMenu));
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    /**
     * 获取用户可收藏菜单
     *
     * @return 获取用户可收藏菜单
     */
    @GetMapping("getShowRouters")
    public AjaxResult getShowRouters() {
        Long userId = ApiThreadLocalUtil.get().getUserid();
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(userId);
        List<String> permissionList = new ArrayList<>(permissions);
        SysMenu sysMenu = new SysMenu();
        sysMenu.setPermissionList(permissionList);
        sysMenu.setVisible("0");
        return AjaxResult.success(menuService.getShowRouters(sysMenu));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/mobile/roleMenuTree")
    public AjaxResult roleMenuTree() {
        Long userId = ApiThreadLocalUtil.get().getUserid();
        if (userId == null) {
            return AjaxResult.error("缺失必要的userId参数");
        }

        List<SysMenu> menus = menuService.selectMobileMenuByUserId(userId);
        return AjaxResult.success(this.buildMenusTree(menus));
    }

    private List<MenuTree> buildMenusTree(List<SysMenu> menuList) {
        // 索引
        Map<Long, SysMenu> menuMap = new HashMap<>();
        menuList.forEach(menu -> menuMap.put(menu.getMenuId(), menu));

        // 保存 MenuTree
        HashMap<Long, MenuTree> treeMap = new HashMap<>();
        // 保存 顶层节点
        List<MenuTree> rootList = new ArrayList<>();

        // 递归构建树
        menuList.forEach(menu -> MenuTree.build(menu, menuMap, treeMap, rootList));

        // 返回顶层树节点的列表
        return rootList;
    }

    @Data
    static class MenuTree {
        Long menuId;
        String menuName;
        String component;
        List<MenuTree> nodeList;

        public MenuTree(SysMenu menu) {
            this.menuId = menu.getMenuId();
            this.menuName = menu.getMenuName();
            this.component = menu.getComponent();
            this.nodeList = new ArrayList<>();
        }

        public static MenuTree build(SysMenu menuCur,
                                     Map<Long, SysMenu> menuMap,
                                     Map<Long, MenuTree> treeMap,
                                     List<MenuTree> rootList) {
            MenuTree existTree = treeMap.get(menuCur.getMenuId());
            if (existTree != null) {
                return existTree;
            }

            Long parentId = menuCur.getParentId();
            MenuTree treeCur = new MenuTree(menuCur);

            if (parentId == 0) {
                treeMap.put(menuCur.getMenuId(), treeCur);
                rootList.add(treeCur);
                return treeCur;
            }

            MenuTree parentTree = treeMap.get(parentId);
            if (parentTree == null) {
                parentTree = build(menuMap.get(parentId), menuMap, treeMap, rootList);
            }

            parentTree.nodeList.add(treeCur);
            treeMap.put(menuCur.getMenuId(), treeCur);
            return treeCur;
        }
    }


    /**
     * 数据库表名下拉框
     *
     * @return 数据库表名
     */
    @PostMapping("/table")
    public AjaxResult selectDbTableList(@RequestBody SysTable table) {
        return AjaxResult.success(menuService.selectDbTableList(table));
    }
}
