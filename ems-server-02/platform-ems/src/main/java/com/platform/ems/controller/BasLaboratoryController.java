package com.platform.ems.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.BasLaboratory;
import com.platform.ems.service.IBasLaboratoryService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * 实验室档案Controller
 *
 * @author c
 * @date 2022-03-31
 */
@RestController
@RequestMapping("/bas/laboratory")
@Api(tags = "实验室档案")
public class BasLaboratoryController extends BaseController {

    @Autowired
    private IBasLaboratoryService basLaboratoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询实验室档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询实验室档案列表", notes = "查询实验室档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasLaboratory.class))
    public TableDataInfo list(@RequestBody BasLaboratory basLaboratory) {
        startPage(basLaboratory);
        List<BasLaboratory> list = basLaboratoryService.selectBasLaboratoryList(basLaboratory);
        return getDataTable(list);
    }

    /**
     * 导出实验室档案列表
     */
    @ApiOperation(value = "导出实验室档案列表", notes = "导出实验室档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasLaboratory basLaboratory) throws IOException {
        List<BasLaboratory> list = basLaboratoryService.selectBasLaboratoryList(basLaboratory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasLaboratory> util = new ExcelUtil<>(BasLaboratory.class, dataMap);
        util.exportExcel(response, list, "实验室");
    }


    /**
     * 获取实验室档案详细信息
     */
    @ApiOperation(value = "获取实验室档案详细信息", notes = "获取实验室档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasLaboratory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long laboratorySid) {
        if (laboratorySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basLaboratoryService.selectBasLaboratoryById(laboratorySid));
    }

    /**
     * 新增实验室档案
     */
    @ApiOperation(value = "新增实验室档案", notes = "新增实验室档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasLaboratory basLaboratory) {
        int row = basLaboratoryService.insertBasLaboratory(basLaboratory);
        return AjaxResult.success(basLaboratory);
    }

    /**
     * 修改实验室档案
     */
    @ApiOperation(value = "修改实验室档案", notes = "修改实验室档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasLaboratory basLaboratory) {
        return toAjax(basLaboratoryService.updateBasLaboratory(basLaboratory));
    }

    /**
     * 变更实验室档案
     */
    @ApiOperation(value = "变更实验室档案", notes = "变更实验室档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasLaboratory basLaboratory) {
        return toAjax(basLaboratoryService.changeBasLaboratory(basLaboratory));
    }

    /**
     * 删除实验室档案
     */
    @ApiOperation(value = "删除实验室档案", notes = "删除实验室档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> laboratorySids) {
        if (CollUtil.isEmpty(laboratorySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basLaboratoryService.deleteBasLaboratoryByIds(laboratorySids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasLaboratory basLaboratory) {
        if (ArrayUtil.isEmpty(basLaboratory.getLaboratorySidList()) || CharSequenceUtil.isEmpty(basLaboratory.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basLaboratoryService.changeStatus(basLaboratory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasLaboratory basLaboratory) {
        if (ArrayUtil.isEmpty(basLaboratory.getLaboratorySidList()) || CharSequenceUtil.isEmpty(basLaboratory.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basLaboratoryService.check(basLaboratory));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "实验室档案下拉框列表", notes = "实验室档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasLaboratory.class))
    public AjaxResult getList(@RequestBody BasLaboratory basLaboratory) {
        return AjaxResult.success(basLaboratoryService.getList(basLaboratory));
    }

}
