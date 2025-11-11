package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.annotation.Log;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.ISystemDefaultSettingClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统默认设置_租户级Controller
 *
 * @author chenkw
 * @date 2022-04-22
 */
@RestController
@RequestMapping("/default/setting/client")
@Api(tags = "系统默认设置_租户级")
public class SysDefaultSettingClientController extends BaseController {

    @Autowired
    private ISystemDefaultSettingClientService sysDefaultSettingClientService;

    /**
     * 查询系统默认设置_租户级列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询系统默认设置_租户级列表", notes = "查询系统默认设置_租户级列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDefaultSettingClient.class))
    public TableDataInfo list(@RequestBody SysDefaultSettingClient sysDefaultSettingClient) {
        startPage(sysDefaultSettingClient);
        List<SysDefaultSettingClient> list = sysDefaultSettingClientService.selectSysDefaultSettingClientList(sysDefaultSettingClient);
        return getDataTable(list);
    }

    /**
     * 获取系统默认设置_租户级详细信息
     */
    @ApiOperation(value = "获取系统默认设置_租户级详细信息", notes = "获取系统默认设置_租户级详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDefaultSettingClient.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        if (StrUtil.isEmpty(clientId)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysDefaultSettingClientService.selectSysDefaultSettingClientById(clientId));
    }

    /**
     * 新增系统默认设置_租户级
     */
    @ApiOperation(value = "新增系统默认设置_租户级", notes = "新增系统默认设置_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:default:setting:client:add")
    @Log(title = "系统默认设置_租户级", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysDefaultSettingClient sysDefaultSettingClient) {
        return toAjax(sysDefaultSettingClientService.insertSysDefaultSettingClient(sysDefaultSettingClient));
    }

    /**
     * 修改系统默认设置_租户级
     */
    @ApiOperation(value = "修改系统默认设置_租户级", notes = "修改系统默认设置_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "系统默认设置_租户级", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SysDefaultSettingClient sysDefaultSettingClient) {
        return toAjax(sysDefaultSettingClientService.updateSysDefaultSettingClient(sysDefaultSettingClient));
    }

    /**
     * 变更系统默认设置_租户级
     */
    @ApiOperation(value = "变更系统默认设置_租户级", notes = "变更系统默认设置_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "系统默认设置_租户级", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysDefaultSettingClient sysDefaultSettingClient) {
        return toAjax(sysDefaultSettingClientService.changeSysDefaultSettingClient(sysDefaultSettingClient));
    }

    /**
     * 删除系统默认设置_租户级
     */
    @ApiOperation(value = "删除系统默认设置_租户级", notes = "删除系统默认设置_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:default:setting:client:remove")
    @Log(title = "系统默认设置_租户级", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysDefaultSettingClientService.deleteSysDefaultSettingClientByIds(clientIds));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "system:default:setting:client:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "系统默认设置_租户级", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SysDefaultSettingClient sysDefaultSettingClient) {
        return toAjax(sysDefaultSettingClientService.check(sysDefaultSettingClient));
    }

}
