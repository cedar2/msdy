package com.platform.system.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.platform.common.constant.UserConstants;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.system.domain.dto.request.SysRoleRequest;
import com.platform.system.domain.dto.response.SysRoleResponse;
import com.platform.system.service.ISysDictDataService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.service.ISysRoleService;

/**
 * 角色信息
 *
 * @author platform
 */
@RestController
@RequestMapping("/role")
public class SysRoleController extends BaseController
{
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**用户账号类型租户管理员 **/
    private static final String USER_TYPE_CLIENT_ADMIN = "ZHGLY";

    private static final String SUPPER_CLIENT_ID = "10000";

    @GetMapping("/list")
    public TableDataInfo list(SysRole role) {
        startPage();
        LoginUser loginUser= ApiThreadLocalUtil.get();
        if(loginUser!=null&&loginUser.getUserid()!=null&&loginUser.getUserid()!=1L){
            if (StrUtil.isBlank(role.getClientId()) && !SUPPER_CLIENT_ID.equals(loginUser.getClientId())) {
                role.setClientId(loginUser.getClientId());
            }
        }
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    @PostMapping("/list/report")
    @ApiOperation(value = "查询用户角色明细报表", notes = "查询用户角色明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleResponse.class))
    public TableDataInfo report(@RequestBody SysRoleRequest role) {
        startPage(role);
        List<SysRoleResponse> list = roleService.selectRoleReport(role);
        return getDataTable(list);
    }

    @PostMapping("/remove/role")
    @ApiOperation(value = "角色从用户移除", notes = "角色从用户移除")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult report(@RequestBody List<SysRoleResponse> role) {
        return AjaxResult.success(roleService.removeRole(role));
    }

    @PostMapping("/add/role")
    @ApiOperation(value = "给角色分配新用户", notes = "给角色分配新用户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult report(@RequestBody SysRoleResponse role) {
        return AjaxResult.success(roleService.addRole(role));
    }


    @ApiOperation(value = "导出用户角色明细报表", notes = "查询用户角色明细报表")
    @PostMapping("/report/export")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleResponse.class))
    public void exportRe(HttpServletResponse response, SysRoleRequest role) throws IOException {
        List<SysRoleResponse> list = roleService.selectRoleReport(role);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysRoleResponse> util = new ExcelUtil<>(SysRoleResponse.class,dataMap);
        util.exportExcel(response, list, "用户角色明细报表");
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleRequest role) throws IOException {
        SysRole sysRole = new SysRole();
        BeanCopyUtils.copyProperties(role, sysRole);
        LoginUser loginUser=ApiThreadLocalUtil.get();
        if(loginUser!=null&&loginUser.getUserid()!=null&&loginUser.getUserid()!=1L){
            if (StrUtil.isBlank(sysRole.getClientId()) && !SUPPER_CLIENT_ID.equals(loginUser.getClientId())) {
                sysRole.setClientId(loginUser.getClientId());
            }
        }
        List<SysRole> list = roleService.selectRoleList(sysRole);
        ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId) {
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRole role) {
        if (UserConstants.NOT_UNIQUE_NUM.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(roleService.checkRoleKeyUnique(role))) {
            return AjaxResult.error("权限字符已存在");
        }else if(UserConstants.NOT_UNIQUE_NUM.equals(roleService.checkRoleTypeUnique(role))){
            return AjaxResult.error("该租户已存在租户管理员角色，操作失败！");
        }
        role.setCreateBy(ApiThreadLocalUtil.get().getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE_NUM.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(roleService.checkRoleKeyUnique(role))) {
            return AjaxResult.error("权限字符已存在");
        }else if(UserConstants.NOT_UNIQUE_NUM.equals(roleService.checkRoleTypeUnique(role))){
            return AjaxResult.error("该租户已存在租户管理员角色，操作失败！");
        }
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        LoginUser loginUser=ApiThreadLocalUtil.get();
        if(loginUser!=null&&loginUser.getUserid()!=null&&loginUser.getUserid()==1L){
            return AjaxResult.success(roleService.selectRoleAll(null));
        }
        return AjaxResult.success(roleService.selectRoleAll(loginUser.getSysUser().getClientId()));
    }
}
