package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.dto.request.ManProductDefectRequest;
import com.platform.ems.domain.dto.response.ManProductDefectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ManProductDefect;
import com.platform.ems.service.IManProductDefectService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产产品缺陷登记Controller
 *
 * @author zhuangyz
 * @date 2022-08-04
 */
@RestController
@RequestMapping("/manProductDefect")
@Api(tags = "生产产品缺陷登记")
public class ManProductDefectController extends BaseController {

    @Autowired
    private IManProductDefectService manProductDefectService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产产品缺陷登记列表
     */

    @PostMapping("/list")
    @ApiOperation(value = "查询生产产品缺陷登记列表", notes = "查询生产产品缺陷登记列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProductDefect.class))
    public TableDataInfo list(@RequestBody ManProductDefect manProductDefect) {
        startPage(manProductDefect);
        List<ManProductDefect> list = manProductDefectService.selectManProductDefectList(manProductDefect);
        return getDataTable(list);
    }

    /**
     * 导出生产产品缺陷登记列表
     */
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产产品缺陷登记列表", notes = "导出生产产品缺陷登记列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProductDefect manProductDefect) throws IOException {
        List<ManProductDefect> list = manProductDefectService.selectManProductDefectList(manProductDefect);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProductDefectResponse> util = new ExcelUtil<>(ManProductDefectResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,ManProductDefectResponse::new) , "生产产品缺陷登记");
    }


    /**
     * 获取生产产品缺陷登记详细信息
     */
    @ApiOperation(value = "获取生产产品缺陷登记详细信息", notes = "获取生产产品缺陷登记详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProductDefect.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productDefectSid) {
        if (productDefectSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProductDefectService.selectManProductDefectById(productDefectSid));
    }

    /**
     * 新增生产产品缺陷登记
     */
    @ApiOperation(value = "新增生产产品缺陷登记", notes = "新增生产产品缺陷登记")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProductDefect manProductDefect) {
        return toAjax(manProductDefectService.insertManProductDefect(manProductDefect));
    }

    @ApiOperation(value = "修改生产产品缺陷登记", notes = "修改生产产品缺陷登记")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManProductDefect manProductDefect) {
        return toAjax(manProductDefectService.updateManProductDefect(manProductDefect));
    }

    @ApiOperation(value = "查询页面弹出框-设置值", notes = "查询页面弹出框-设置值")
    @Log(title = "生产产品缺陷登记-", businessType = BusinessType.INSERT)
    @PostMapping("/update/status")
    public AjaxResult updateStatus(@RequestBody  ManProductDefectRequest request) {
        return toAjax(manProductDefectService.updateStatus(request));
    }

    /**
     * 变更生产产品缺陷登记
     */
    @ApiOperation(value = "变更生产产品缺陷登记", notes = "变更生产产品缺陷登记")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProductDefect manProductDefect) {
        return toAjax(manProductDefectService.changeManProductDefect(manProductDefect));
    }

    /**
     * 删除生产产品缺陷登记
     */
    @ApiOperation(value = "删除生产产品缺陷登记", notes = "删除生产产品缺陷登记")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> productDefectSids) {
        if (CollectionUtils.isEmpty(productDefectSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProductDefectService.deleteManProductDefectByIds(productDefectSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ManProductDefect manProductDefect) {
        return AjaxResult.success(manProductDefectService.changeStatus(manProductDefect));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManProductDefect manProductDefect) {
        return toAjax(manProductDefectService.check(manProductDefect));
    }

}
