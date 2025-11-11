package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import com.platform.common.annotation.Log;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConMaterialRequisitionBusinessFlag;
import com.platform.ems.plug.service.IConMaterialRequisitionBusinessFlagService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务标识_领退料Controller
 *
 * @author platform
 * @date 2024-11-10
 */
@Api(tags = "业务标识_领退料")
@RestController
@RequestMapping("/materialRBFlag")
public class ConMaterialRequisitionBusinessFlagController extends BaseController {

    @Autowired
    private IConMaterialRequisitionBusinessFlagService conMaterialRequisitionBusinessFlagService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务标识_领退料列表
     */
    @PreAuthorize(hasPermi = "ems:materialRBFlag:list" )
    @ApiOperation(value = "查询业务标识_领退料列表", notes = "查询业务标识_领退料列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialRequisitionBusinessFlag.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        startPage(conMaterialRequisitionBusinessFlag);
        List<ConMaterialRequisitionBusinessFlag> list = conMaterialRequisitionBusinessFlagService.selectConMaterialRequisitionBusinessFlagList(conMaterialRequisitionBusinessFlag);
        return getDataTable(list);
    }

    /**
     * 导出业务标识_领退料列表
     */
    // @PreAuthorize(hasPermi = "ems:materialRBFlag:export" )
    @ApiOperation(value = "导出业务标识_领退料列表", notes = "导出业务标识_领退料列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) throws IOException {
        List<ConMaterialRequisitionBusinessFlag> list = conMaterialRequisitionBusinessFlagService.selectConMaterialRequisitionBusinessFlagList(conMaterialRequisitionBusinessFlag);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConMaterialRequisitionBusinessFlag> util = new ExcelUtil<>(ConMaterialRequisitionBusinessFlag.class, dataMap);
        util.exportExcel(response, list, "业务标识_领退料");
    }

    /**
     * 获取业务标识_领退料详细信息
     */
    @PreAuthorize(hasPermi = "ems:materialRBFlag:query" )
    @ApiOperation(value = "获取业务标识_领退料详细信息", notes = "获取业务标识_领退料详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialRequisitionBusinessFlag.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conMaterialRequisitionBusinessFlagService.selectConMaterialRequisitionBusinessFlagById(sid));
    }

    /**
     * 新增业务标识_领退料
     */
    @PreAuthorize(hasPermi = "ems:materialRBFlag:add")
    @Log(title = "业务标识_领退料", businessType = BusinessType.INSERT)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "新增业务标识_领退料", notes = "新增业务标识_领退料")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        return toAjax(conMaterialRequisitionBusinessFlagService.insertConMaterialRequisitionBusinessFlag(conMaterialRequisitionBusinessFlag));
    }

    @PreAuthorize(hasPermi = "ems:materialRBFlag:edit")
    @Log(title = "业务标识_领退料", businessType = BusinessType.UPDATE)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "修改业务标识_领退料", notes = "修改业务标识_领退料")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        return toAjax(conMaterialRequisitionBusinessFlagService.updateConMaterialRequisitionBusinessFlag(conMaterialRequisitionBusinessFlag));
    }

    /**
     * 变更业务标识_领退料
     */
    @PreAuthorize(hasPermi = "ems:materialRBFlag:change" )
    @Log(title = "业务标识_领退料" , businessType = BusinessType.CHANGE)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "变更业务标识_领退料", notes = "变更业务标识_领退料")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        return toAjax(conMaterialRequisitionBusinessFlagService.changeConMaterialRequisitionBusinessFlag(conMaterialRequisitionBusinessFlag));
    }

    /**
     * 删除业务标识_领退料
     */
    @PreAuthorize(hasPermi = "ems:materialRBFlag:remove" )
    @Log(title = "业务标识_领退料" , businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除业务标识_领退料", notes = "删除业务标识_领退料")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conMaterialRequisitionBusinessFlagService.deleteConMaterialRequisitionBusinessFlagByIds(sids));
    }

    @PreAuthorize(hasPermi = "ems:materialRBFlag:enbleordisable" )
    @Log(title = "业务标识_领退料" , businessType = BusinessType.UPDATE)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        return AjaxResult.success(conMaterialRequisitionBusinessFlagService.changeStatus(conMaterialRequisitionBusinessFlag));
    }

    @PreAuthorize(hasPermi = "ems:materialRBFlag:edit")
    @Log(title = "业务标识_领退料", businessType = BusinessType.CHECK)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        return toAjax(conMaterialRequisitionBusinessFlagService.check(conMaterialRequisitionBusinessFlag));
    }

}
