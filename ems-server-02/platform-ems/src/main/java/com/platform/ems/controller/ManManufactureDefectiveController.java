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
import com.platform.ems.domain.ManManufactureDefective;
import com.platform.ems.service.IManManufactureDefectiveService;
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
 * 生产次品台账Controller
 *
 * @author c
 * @date 2022-03-02
 */
@RestController
@RequestMapping("/defective")
@Api(tags = "生产次品台账")
public class ManManufactureDefectiveController extends BaseController {

    @Autowired
    private IManManufactureDefectiveService manManufactureDefectiveService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产次品台账列表
     */
    @PreAuthorize(hasPermi = "ems:defective:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产次品台账列表", notes = "查询生产次品台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureDefective.class))
    public TableDataInfo list(@RequestBody ManManufactureDefective manManufactureDefective) {
        startPage(manManufactureDefective);
        List<ManManufactureDefective> list = manManufactureDefectiveService.selectManManufactureDefectiveList(manManufactureDefective);
        return getDataTable(list);
    }

    /**
     * 导出生产次品台账列表
     */
    @PreAuthorize(hasPermi = "ems:defective:export")
    @Log(title = "生产次品台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产次品台账列表", notes = "导出生产次品台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureDefective manManufactureDefective) throws IOException {
        List<ManManufactureDefective> list = manManufactureDefectiveService.selectManManufactureDefectiveList(manManufactureDefective);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureDefective> util = new ExcelUtil<>(ManManufactureDefective.class, dataMap);
        util.exportExcel(response, list, "生产次品台账");
    }


    /**
     * 获取生产次品台账详细信息
     */
    @ApiOperation(value = "获取生产次品台账详细信息", notes = "获取生产次品台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureDefective.class))
    @PreAuthorize(hasPermi = "ems:defective:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureDefectiveSid) {
        if (manufactureDefectiveSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureDefectiveService.selectManManufactureDefectiveById(manufactureDefectiveSid));
    }

    /**
     * 新增生产次品台账
     */
    @ApiOperation(value = "新增生产次品台账", notes = "新增生产次品台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:defective:add")
    @Log(title = "生产次品台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManManufactureDefective manManufactureDefective) {
        return toAjax(manManufactureDefectiveService.insertManManufactureDefective(manManufactureDefective));
    }

    /**
     * 修改生产次品台账
     */
    @ApiOperation(value = "修改生产次品台账", notes = "修改生产次品台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:defective:edit")
    @Log(title = "生产次品台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManManufactureDefective manManufactureDefective) {
        return toAjax(manManufactureDefectiveService.updateManManufactureDefective(manManufactureDefective));
    }

    /**
     * 变更生产次品台账
     */
    @ApiOperation(value = "变更生产次品台账", notes = "变更生产次品台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:defective:change")
    @Log(title = "生产次品台账", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManManufactureDefective manManufactureDefective) {
        return toAjax(manManufactureDefectiveService.changeManManufactureDefective(manManufactureDefective));
    }

    /**
     * 删除生产次品台账
     */
    @ApiOperation(value = "删除生产次品台账", notes = "删除生产次品台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:defective:remove")
    @Log(title = "生产次品台账", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> manufactureDefectiveSids) {
        if (CollectionUtils.isEmpty(manufactureDefectiveSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manManufactureDefectiveService.deleteManManufactureDefectiveByIds(manufactureDefectiveSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:defective:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产次品台账", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManManufactureDefective manManufactureDefective) {
        return toAjax(manManufactureDefectiveService.check(manManufactureDefective));
    }

}
