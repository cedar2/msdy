package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.SysAuthorityObject;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.impl.SysAuthorityObjectServiceImpl;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 权限对象Controller
 *
 * @author straw
 * @date 2023-01-16
 */
@RestController
@RequestMapping("/sys/auth/object")
@Api(tags = "权限对象")
public class SysAuthorityObjectController extends BaseController {

    @Autowired
    private SysAuthorityObjectServiceImpl sysAuthorityObjectService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询权限对象列表
     */

    @PostMapping("/list")
    @ApiOperation(value = "查询权限对象列表",
                  notes = "查询权限对象列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = SysAuthorityObject.class))
    public TableDataInfo list(@RequestBody SysAuthorityObject sysAuthorityObject) {
        startPage(sysAuthorityObject);
        List<SysAuthorityObject> list = sysAuthorityObjectService.selectSysAuthorityObjectList(sysAuthorityObject);
        return getDataTable(list);
    }

    /**
     * 导出权限对象列表
     */

    @Log(title = "权限对象",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出权限对象列表",
                  notes = "导出权限对象列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAuthorityObject sysAuthorityObject) throws IOException {
        List<SysAuthorityObject> list = sysAuthorityObjectService.selectSysAuthorityObjectList(sysAuthorityObject);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysAuthorityObject> util = new ExcelUtil<>(SysAuthorityObject.class, dataMap);
        util.exportExcel(response, list, "权限对象");
    }


    /**
     * 获取权限对象详细信息
     */
    @ApiOperation(value = "获取权限对象详细信息",
                  notes = "获取权限对象详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = SysAuthorityObject.class))

    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long authorityObjectSid) {
        if (authorityObjectSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysAuthorityObjectService.selectSysAuthorityObjectById(authorityObjectSid));
    }

    /**
     * 新增权限对象
     */
    @ApiOperation(value = "新增权限对象",
                  notes = "新增权限对象")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "权限对象",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid SysAuthorityObject sysAuthorityObject) {
        return toAjax(sysAuthorityObjectService.insertSysAuthorityObject(sysAuthorityObject));
    }

    @ApiOperation(value = "修改权限对象",
                  notes = "修改权限对象")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "权限对象",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",
                interval = 3000)
    public AjaxResult edit(@RequestBody SysAuthorityObject sysAuthorityObject) {
        return toAjax(sysAuthorityObjectService.updateSysAuthorityObject(sysAuthorityObject));
    }

    /**
     * 变更权限对象
     */
    @ApiOperation(value = "变更权限对象",
                  notes = "变更权限对象")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "权限对象",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysAuthorityObject sysAuthorityObject) {
        return toAjax(sysAuthorityObjectService.changeSysAuthorityObject(sysAuthorityObject));
    }

    /**
     * 删除权限对象
     */
    @ApiOperation(value = "删除权限对象",
                  notes = "删除权限对象")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "权限对象",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> authorityObjectSids) {
        if (CollectionUtils.isEmpty(authorityObjectSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysAuthorityObjectService.deleteSysAuthorityObjectByIds(authorityObjectSids));
    }

    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "权限对象",
         businessType = BusinessType.UPDATE)

    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysAuthorityObject sysAuthorityObject) {
        return AjaxResult.success(sysAuthorityObjectService.changeStatus(sysAuthorityObject));
    }

    @ApiOperation(value = "确认",
                  notes = "确认")

    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "权限对象",
         businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody SysAuthorityObject sysAuthorityObject) {
        return toAjax(sysAuthorityObjectService.check(sysAuthorityObject));
    }

}
