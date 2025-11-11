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
import com.platform.ems.domain.DevSampleReviewForm;
import com.platform.ems.service.IDevSampleReviewFormService;
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
 * 样品评审单Controller
 *
 * @author linhongwei
 * @date 2022-03-23
 */
@RestController
@RequestMapping("/sample/review/form")
@Api(tags = "样品评审单")
public class DevSampleReviewFormController extends BaseController {

    @Autowired
    private IDevSampleReviewFormService devSampleReviewFormService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询样品评审单列表
     */
    @PreAuthorize(hasPermi = "ems:sample:review:form:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询样品评审单列表", notes = "查询样品评审单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevSampleReviewForm.class))
    public TableDataInfo list(@RequestBody DevSampleReviewForm devSampleReviewForm) {
        startPage(devSampleReviewForm);
        List<DevSampleReviewForm> list = devSampleReviewFormService.selectDevSampleReviewFormList(devSampleReviewForm);
        return getDataTable(list);
    }

    /**
     * 导出样品评审单列表
     */
    @PreAuthorize(hasPermi = "ems:sample:review:form:export")
    @Log(title = "样品评审单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出样品评审单列表", notes = "导出样品评审单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevSampleReviewForm devSampleReviewForm) throws IOException {
        List<DevSampleReviewForm> list = devSampleReviewFormService.selectDevSampleReviewFormList(devSampleReviewForm);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevSampleReviewForm> util = new ExcelUtil<>(DevSampleReviewForm.class, dataMap);
        util.exportExcel(response, list, "样品评审单");
    }


    /**
     * 获取样品评审单详细信息
     */
    @ApiOperation(value = "获取样品评审单详细信息", notes = "获取样品评审单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevSampleReviewForm.class))
    @PreAuthorize(hasPermi = "ems:sample:review:form:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sampleReviewFormSid) {
        if (sampleReviewFormSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devSampleReviewFormService.selectDevSampleReviewFormById(sampleReviewFormSid));
    }

    /**
     * 新增样品评审单
     */
    @ApiOperation(value = "新增样品评审单", notes = "新增样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:sample:review:form:add")
    @Log(title = "样品评审单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DevSampleReviewForm devSampleReviewForm) {
        return AjaxResult.success(devSampleReviewFormService.insertDevSampleReviewForm(devSampleReviewForm));
    }

    /**
     * 修改样品评审单
     */
    @ApiOperation(value = "修改样品评审单", notes = "修改样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:sample:review:form:edit")
    @Log(title = "样品评审单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid DevSampleReviewForm devSampleReviewForm) {
        return AjaxResult.success(devSampleReviewFormService.updateDevSampleReviewForm(devSampleReviewForm));
    }

    /**
     * 变更样品评审单
     */
    @ApiOperation(value = "变更样品评审单", notes = "变更样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:sample:review:form:change")
    @Log(title = "样品评审单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid DevSampleReviewForm devSampleReviewForm) {
        return toAjax(devSampleReviewFormService.changeDevSampleReviewForm(devSampleReviewForm));
    }

    /**
     * 删除样品评审单
     */
    @ApiOperation(value = "删除样品评审单", notes = "删除样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:sample:review:form:remove")
    @Log(title = "样品评审单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sampleReviewFormSids) {
        if (CollectionUtils.isEmpty(sampleReviewFormSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devSampleReviewFormService.deleteDevSampleReviewFormByIds(sampleReviewFormSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:sample:review:form:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品评审单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody DevSampleReviewForm devSampleReviewForm) {
        return toAjax(devSampleReviewFormService.check(devSampleReviewForm));
    }

}
