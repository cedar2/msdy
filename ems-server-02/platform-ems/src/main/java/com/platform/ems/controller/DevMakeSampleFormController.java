package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.DevMakeSampleForm;
import com.platform.ems.service.IDevMakeSampleFormService;
import com.platform.ems.service.ISystemDictDataService;
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
 * 打样准许单Controller
 *
 * @author linhongwei
 * @date 2022-03-24
 */
@RestController
@RequestMapping("/make/sample/form")
@Api(tags = "打样准许单")
public class DevMakeSampleFormController extends BaseController {

    @Autowired
    private IDevMakeSampleFormService devMakeSampleFormService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询打样准许单列表
     */
    @PreAuthorize(hasPermi = "ems:make:sample:form:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询打样准许单列表", notes = "查询打样准许单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevMakeSampleForm.class))
    public TableDataInfo list(@RequestBody DevMakeSampleForm devMakeSampleForm) {
        startPage(devMakeSampleForm);
        List<DevMakeSampleForm> list = devMakeSampleFormService.selectDevMakeSampleFormList(devMakeSampleForm);
        return getDataTable(list);
    }

    /**
     * 导出打样准许单列表
     */
    @PreAuthorize(hasPermi = "ems:make:sample:form:export")
    @Log(title = "打样准许单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出打样准许单列表", notes = "导出打样准许单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevMakeSampleForm devMakeSampleForm) throws IOException {
        List<DevMakeSampleForm> list = devMakeSampleFormService.selectDevMakeSampleFormList(devMakeSampleForm);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevMakeSampleForm> util = new ExcelUtil<>(DevMakeSampleForm.class, dataMap);
        util.exportExcel(response, list, "打样准许单");
    }


    /**
     * 获取打样准许单详细信息
     */
    @ApiOperation(value = "获取打样准许单详细信息", notes = "获取打样准许单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevMakeSampleForm.class))
    @PreAuthorize(hasPermi = "ems:make:sample:form:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long makeSampleFormSid) {
        if (makeSampleFormSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devMakeSampleFormService.selectDevMakeSampleFormById(makeSampleFormSid));
    }

    /**
     * 新增打样准许单
     */
    @ApiOperation(value = "新增打样准许单", notes = "新增打样准许单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:make:sample:form:add")
    @Log(title = "打样准许单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DevMakeSampleForm devMakeSampleForm) {
        return AjaxResult.success(devMakeSampleFormService.insertDevMakeSampleForm(devMakeSampleForm));
    }

    /**
     * 修改打样准许单
     */
    @ApiOperation(value = "修改打样准许单", notes = "修改打样准许单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:make:sample:form:edit")
    @Log(title = "打样准许单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody DevMakeSampleForm devMakeSampleForm) {
        return toAjax(devMakeSampleFormService.updateDevMakeSampleForm(devMakeSampleForm));
    }

    /**
     * 变更打样准许单
     */
    @ApiOperation(value = "变更打样准许单", notes = "变更打样准许单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:make:sample:form:change")
    @Log(title = "打样准许单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody DevMakeSampleForm devMakeSampleForm) {
        return toAjax(devMakeSampleFormService.changeDevMakeSampleForm(devMakeSampleForm));
    }

    /**
     * 删除打样准许单
     */
    @ApiOperation(value = "删除打样准许单", notes = "删除打样准许单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:make:sample:form:remove")
    @Log(title = "打样准许单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> makeSampleFormSids) {
        if (CollectionUtils.isEmpty(makeSampleFormSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devMakeSampleFormService.deleteDevMakeSampleFormByIds(makeSampleFormSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:make:sample:form:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "打样准许单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody DevMakeSampleForm devMakeSampleForm) {
        return toAjax(devMakeSampleFormService.check(devMakeSampleForm));
    }

}
