package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.entity.SysUserDataRole;
import com.platform.common.core.domain.entity.SysUserRole;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.service.ISystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户Controller
 *
 * @author chenkw
 * @date 2023-05-24
 */
@RestController
@RequestMapping("/sys/user")
@Api(tags = "用户管理")
public class SystemUserController extends BaseController {

    @Autowired
    private ISystemUserService sysUserService;

    /**
     * 新增用户
     */
    @ApiOperation(value = "用户新建", notes = "用户新建")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid SysUser user) {
        int row = 0;
        try {
            row = sysUserService.insertSysUser(user);
        } catch (BaseException e) {
            // 员工已存在用户档案中，是否继续
            if ("101".equals(e.getCode())) {
                return AjaxResult.success(e.getDefaultMessage(), null);
            }
            throw e;
        }
        return toAjax(row);
    }

    /**
     * 获取用户详细信息
     */
    @ApiOperation(value = "获取用户详细信息", notes = "获取用户详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysUser.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long userId) {
        if (userId == null) {
            throw new CheckedException("参数缺失");
        }
        SysUser user = sysUserService.selectSysUserById(userId);
        if (user != null) {
            // 数据角色
            List<SysUserDataRole> userDataRoleList = new ArrayList<>();
            user.setUserDataRoleList(userDataRoleList);
            userDataRoleList = sysUserService.selectSysUserDataRoleByUserId(userId);
            if (CollectionUtil.isNotEmpty(userDataRoleList)) {
                user.setUserDataRoleList(userDataRoleList);
            }
            // 操作角色
            List<SysUserRole> userRoleList = new ArrayList<>();
            user.setUserRoleList(userRoleList);
            userRoleList = sysUserService.selectSysUserRoleByUserId(userId);
            if (CollectionUtil.isNotEmpty(userRoleList)) {
                user.setUserRoleList(userRoleList);
            }
        }
        return AjaxResult.success(user);
    }

    /**
     * 变更用户
     */
    @ApiOperation(value = "变更用户", notes = "变更用户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysUser user) {
        int row = 0;
        try {
            row = sysUserService.updateSysUser(user);
        } catch (BaseException e) {
            // 员工已存在用户档案中，是否继续
            if ("101".equals(e.getCode())) {
                return AjaxResult.success(e.getDefaultMessage(), null);
            }
            throw e;
        }
        return toAjax(row);
    }
    /**
     * 维护openid
     */
    @PostMapping("/setOpenid")
    @ApiOperation(value = "维护openid", notes = "维护openid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysUser.class))
    public AjaxResult setOpenid(@RequestBody SysUser user) {
        int row = 0;
        try {
            row = sysUserService.setOpenid(user);
        } catch (BaseException e) {
            if ("101".equals(e.getCode())) {
                return AjaxResult.success(e.getDefaultMessage(), null);
            }
            throw e;
        }
        return toAjax(row);
    }
}
