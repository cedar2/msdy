package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.core.experimental.util.ParamsCheckUtil;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.SysAuthorityField;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.impl.SysAuthorityFieldServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 权限字段Controller
 *
 * @author linxq
 * @date 2023-01-12
 */
@RestController
@RequestMapping("/sys/auth/field")
@Api(tags = "权限字段")
public class SysAuthorityFieldController extends BaseController {

    final SysAuthorityFieldServiceImpl sysAuthorityFieldService;

    final ISystemDictDataService sysDictDataService;

    public SysAuthorityFieldController(SysAuthorityFieldServiceImpl sysAuthorityFieldService,
                                       ISystemDictDataService sysDictDataService) {
        this.sysAuthorityFieldService = sysAuthorityFieldService;
        this.sysDictDataService = sysDictDataService;
    }

    /**
     * 查询权限字段列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询权限字段列表",
                  notes = "查询权限字段列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = SysAuthorityField.class))
    public TableDataInfo list(@RequestBody SysAuthorityField sysAuthorityField) {
        startPage(sysAuthorityField);
        List<SysAuthorityField> list = sysAuthorityFieldService.selectSysAuthorityFieldList(sysAuthorityField);
        return getDataTable(list);
    }

    /**
     * 导出权限字段列表
     */
    @Log(title = "权限字段",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出权限字段列表",
                  notes = "导出权限字段列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAuthorityField sysAuthorityField) throws IOException {
        List<SysAuthorityField> list = sysAuthorityFieldService.selectSysAuthorityFieldList(sysAuthorityField);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysAuthorityField> util = new ExcelUtil<>(SysAuthorityField.class, dataMap);
        util.exportExcel(response, list, "权限字段");
    }


    /**
     * 获取权限字段详细信息
     */
    @ApiOperation(value = "获取权限字段详细信息",
                  notes = "获取权限字段详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = SysAuthorityField.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long authorityFieldSid) {
        if (authorityFieldSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysAuthorityFieldService.selectSysAuthorityFieldById(authorityFieldSid));
    }

    /**
     * 新增权限字段
     */
    @ApiOperation(value = "新增权限字段",
                  notes = "新增权限字段")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "权限字段",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid SysAuthorityField sysAuthorityField) {
        return toAjax(sysAuthorityFieldService.insertSysAuthorityField(sysAuthorityField));
    }

    @ApiOperation(value = "修改权限字段",
                  notes = "修改权限字段")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "权限字段",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",
                interval = 3000)
    public AjaxResult edit(@RequestBody SysAuthorityField sysAuthorityField) {
        return toAjax(sysAuthorityFieldService.updateSysAuthorityField(sysAuthorityField));
    }

    /**
     * 变更权限字段
     */
    @ApiOperation(value = "变更权限字段",
                  notes = "变更权限字段")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "权限字段",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysAuthorityField sysAuthorityField) {
        return toAjax(sysAuthorityFieldService.changeSysAuthorityField(sysAuthorityField));
    }

    /**
     * 删除权限字段
     */
    @ApiOperation(value = "删除权限字段",
                  notes = "删除权限字段")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "权限字段",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> authorityFieldSidList) {
        if (CollectionUtils.isEmpty(authorityFieldSidList)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysAuthorityFieldService.deleteSysAuthorityFieldByIds(authorityFieldSidList));
    }

    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "权限字段",
         businessType = BusinessType.UPDATE)

    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysAuthorityField sysAuthorityField) {
        ParamsCheckUtil.checkStatus(
                sysAuthorityField,
                SysAuthorityField::getStatus,
                SysAuthorityField::getAuthorityFieldSidList
        );
        return AjaxResult.success(sysAuthorityFieldService.changeStatus(sysAuthorityField));
    }

    @ApiOperation(value = "确认",
                  notes = "确认")

    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "权限字段",
         businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody SysAuthorityField sysAuthorityField) {
        return toAjax(sysAuthorityFieldService.check(sysAuthorityField));
    }

}
