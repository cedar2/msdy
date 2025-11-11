package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.SysClientMovementType;
import com.platform.ems.service.ISysClientMovementTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 作业类型_租户级Controller
 *
 * @author chenkw
 * @date 2022-06-17
 */
@RestController
@RequestMapping("/type")
@Api(tags = "作业类型_租户级")
public class SysClientMovementTypeController extends BaseController {

    @Autowired
    private ISysClientMovementTypeService sysClientMovementTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String SUPER_CLIENT_ID = "10000";

    /**
     * 查询作业类型_租户级列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询作业类型_租户级列表", notes = "查询作业类型_租户级列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysClientMovementType.class))
    public TableDataInfo list(@RequestBody SysClientMovementType sysClientMovementType) {
        if (SUPER_CLIENT_ID.equals(ApiThreadLocalUtil.get().getClientId()) && SUPER_CLIENT_ID.equals(sysClientMovementType.getClientId())) {
            sysClientMovementType.setClientId(null);
        }
        startPage(sysClientMovementType);
        List<SysClientMovementType> list = sysClientMovementTypeService.selectSysClientMovementTypeList(sysClientMovementType);
        return getDataTable(list);
    }

    /**
     * 导出作业类型_租户级列表
     */
    @Log(title = "作业类型_租户级", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出作业类型_租户级列表", notes = "导出作业类型_租户级列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysClientMovementType sysClientMovementType) throws IOException {
        List<SysClientMovementType> list = sysClientMovementTypeService.selectSysClientMovementTypeList(sysClientMovementType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysClientMovementType> util = new ExcelUtil<>(SysClientMovementType.class, dataMap);
        util.exportExcel(response, list, "作业类型_租户级");
    }


    /**
     * 获取作业类型_租户级详细信息
     */
    @ApiOperation(value = "获取作业类型_租户级详细信息", notes = "获取作业类型_租户级详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysClientMovementType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long clientMovementTypeSid) {
        if (clientMovementTypeSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysClientMovementTypeService.selectSysClientMovementTypeById(clientMovementTypeSid));
    }

    /**
     * 新增作业类型_租户级
     */
    @ApiOperation(value = "新增作业类型_租户级", notes = "新增作业类型_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型_租户级", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysClientMovementType sysClientMovementType) {
        return toAjax(sysClientMovementTypeService.insertSysClientMovementType(sysClientMovementType));
    }

    /**
     * 修改作业类型_租户级
     */
    @ApiOperation(value = "修改作业类型_租户级", notes = "修改作业类型_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型_租户级", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SysClientMovementType sysClientMovementType) {
        return toAjax(sysClientMovementTypeService.updateSysClientMovementType(sysClientMovementType));
    }

    /**
     * 变更作业类型_租户级
     */
    @ApiOperation(value = "变更作业类型_租户级", notes = "变更作业类型_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型_租户级", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysClientMovementType sysClientMovementType) {
        return toAjax(sysClientMovementTypeService.changeSysClientMovementType(sysClientMovementType));
    }

    /**
     * 删除作业类型_租户级
     */
    @ApiOperation(value = "删除作业类型_租户级", notes = "删除作业类型_租户级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型_租户级", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> clientMovementTypeSids) {
        if (CollectionUtils.isEmpty(clientMovementTypeSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysClientMovementTypeService.deleteSysClientMovementTypeByIds(clientMovementTypeSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型_租户级", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SysClientMovementType sysClientMovementType) {
        sysClientMovementType.setConfirmDate(new Date());
        sysClientMovementType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        sysClientMovementType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(sysClientMovementTypeService.check(sysClientMovementType));
    }

}
