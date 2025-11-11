package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.domain.dto.response.ModelSystemDetailResponse;
import com.platform.ems.domain.dto.response.ModelSystemListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.domain.TecModel;
import com.platform.ems.service.ITecModelService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 版型档案Controller
 *
 * @author linhongwei
 * @date 2021-05-13
 */
@RestController
@RequestMapping("/model")
@Api(tags = "版型档案")
public class TecModelController extends BaseController {

    @Autowired
    private ITecModelService tecModelService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询版型档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询版型档案列表", notes = "查询版型档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModel.class))
    public TableDataInfo list(@RequestBody TecModel tecModel) {
        startPage(tecModel);
        List<TecModel> list = tecModelService.selectTecModelList(tecModel);
        return getDataTable(list);
    }

    /**
     * 导出版型档案列表
     */
    @ApiOperation(value = "导出版型档案列表", notes = "导出版型档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecModel tecModel) throws IOException {
        List<TecModel> list = tecModelService.selectTecModelList(tecModel);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecModel> util = new ExcelUtil<>(TecModel.class, dataMap);
        util.exportExcel(response, list, "版型");
    }


    /**
     * 获取版型档案详细信息
     */
    @ApiOperation(value = "获取版型档案详细信息", notes = "获取版型档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModel.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long modelSid) {
        if (modelSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecModelService.selectTecModelById(modelSid));
    }

    /**
     * 新增版型档案
     */
    @ApiOperation(value = "新增版型档案", notes = "新增版型档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecModel tecModel) {
        int row = tecModelService.insertTecModel(tecModel);
        return AjaxResult.success(tecModel);
    }

    /**
     * 修改版型档案
     */
    @ApiOperation(value = "修改版型档案", notes = "修改版型档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TecModel tecModel) {
        return toAjax(tecModelService.updateTecModel(tecModel));
    }

    /**
     * 变更版型档案
     */
    @ApiOperation(value = "变更版型档案", notes = "变更版型档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody TecModel tecModel) {
        return toAjax(tecModelService.changeTecModel(tecModel));
    }

    /**
     * 删除版型档案
     */
    @ApiOperation(value = "删除版型档案", notes = "删除版型档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody TecModel tecModel) {
        if (ArrayUtil.isEmpty(tecModel.getModelSidList())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecModelService.deleteTecModelByIds(tecModel));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecModel tecModel) {
        return AjaxResult.success(tecModelService.changeStatus(tecModel));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型档案", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecModel tecModel) {
        return toAjax(tecModelService.check(tecModel));
    }

    @ApiOperation(value = "确认操作前校验相应附件是否上传", notes = "确认操作前校验相应附件是否上传")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody TecModel tecModel) {
        return tecModelService.checkAttach(tecModel);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "获取版型档案下拉列表", notes = "获取版型档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ModelSystemListResponse.class))
    public AjaxResult getList() {
        return AjaxResult.success(tecModelService.getList());
    }

    @PostMapping("/getModelList")
    @ApiOperation(value = "获取版型档案下拉列表", notes = "获取版型档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ModelSystemListResponse.class))
    public AjaxResult getModelList(@RequestBody TecModel tecModel) {
        return AjaxResult.success(tecModelService.getList(tecModel));
    }

    @PostMapping("/getDetail")
    @ApiOperation(value = "获取某个版型档案尺寸详情", notes = "获取某个版型档案尺寸详情")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ModelSystemDetailResponse.class))
    public AjaxResult getDetail(Long modelSid) {
        if (modelSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecModelService.getDetail(modelSid));
    }

}
