package com.platform.ems.controller;

import java.util.List;

import com.platform.common.exception.TokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import com.platform.ems.domain.SysUserFavoriteMenu;
import com.platform.ems.service.ISysUserFavoriteMenuService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;

/**
 * 用户收藏菜单Controller
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@RestController
@RequestMapping("/user/favorite/menu")
@Api(tags = "用户收藏菜单")
public class SysUserFavoriteMenuController extends BaseController {

    @Autowired
    private ISysUserFavoriteMenuService sysUserFavoriteMenuService;

    /**
     * 查询用户收藏菜单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询用户收藏菜单列表", notes = "查询用户收藏菜单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysUserFavoriteMenu.class))
    public TableDataInfo list(@RequestBody SysUserFavoriteMenu sysUserFavoriteMenu) {
        startPage(sysUserFavoriteMenu);
        List<SysUserFavoriteMenu> list = sysUserFavoriteMenuService.selectSysUserFavoriteMenuList(sysUserFavoriteMenu);
        return getDataTable(list);
    }


    /**
     * 获取用户收藏菜单详细信息
     */
    @ApiOperation(value = "获取用户收藏菜单详细信息", notes = "获取用户收藏菜单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysUserFavoriteMenu.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String id) {
        if (StrUtil.isEmpty(id)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysUserFavoriteMenuService.selectSysUserFavoriteMenuById(id));
    }

    /**
     * 新增用户收藏菜单
     */
    @ApiOperation(value = "新增用户收藏菜单", notes = "新增用户收藏菜单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "用户收藏菜单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysUserFavoriteMenu sysUserFavoriteMenu) {
        Long userId=ApiThreadLocalUtil.get().getUserid();
        if(userId==null){
            throw new TokenException();
        }
        sysUserFavoriteMenu.setUserId(userId);
        return toAjax(sysUserFavoriteMenuService.insertSysUserFavoriteMenu(sysUserFavoriteMenu));
    }

    /**
     * 修改用户收藏菜单
     */
    @ApiOperation(value = "修改用户收藏菜单", notes = "修改用户收藏菜单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "用户收藏菜单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysUserFavoriteMenu sysUserFavoriteMenu) {
        return toAjax(sysUserFavoriteMenuService.updateSysUserFavoriteMenu(sysUserFavoriteMenu));
    }


    /**
     * 删除用户收藏菜单
     */
    @ApiOperation(value = "删除用户收藏菜单", notes = "删除用户收藏菜单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "用户收藏菜单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> ids) {
        if (ArrayUtil.isEmpty(ids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysUserFavoriteMenuService.deleteSysUserFavoriteMenuByIds(ids));
    }


}
