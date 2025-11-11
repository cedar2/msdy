package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.constant.ConstantsEms;
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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConDocTypeMaterialRequisition;
import com.platform.ems.plug.service.IConDocTypeMaterialRequisitionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_领退料单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/material/requisition")
@Api(tags = "单据类型_领退料单")
public class ConDocTypeMaterialRequisitionController extends BaseController {

    @Autowired
    private IConDocTypeMaterialRequisitionService conDocTypeMaterialRequisitionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_领退料单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_领退料单列表", notes = "查询单据类型_领退料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeMaterialRequisition.class))
    public TableDataInfo list(@RequestBody ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        startPage(conDocTypeMaterialRequisition);
        List<ConDocTypeMaterialRequisition> list = conDocTypeMaterialRequisitionService.selectConDocTypeMaterialRequisitionList(conDocTypeMaterialRequisition);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_领退料单列表
     */
    @Log(title = "单据类型_领退料单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_领退料单列表", notes = "导出单据类型_领退料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) throws IOException {
        List<ConDocTypeMaterialRequisition> list = conDocTypeMaterialRequisitionService.selectConDocTypeMaterialRequisitionList(conDocTypeMaterialRequisition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeMaterialRequisition> util = new ExcelUtil<>(ConDocTypeMaterialRequisition.class, dataMap);
        util.exportExcel(response, list, "单据类型_领退料单");
    }

    /**
     * 获取单据类型_领退料单详细信息
     */
    @ApiOperation(value = "获取单据类型_领退料单详细信息", notes = "获取单据类型_领退料单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeMaterialRequisition.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeMaterialRequisitionService.selectConDocTypeMaterialRequisitionById(sid));
    }

    /**
     * 新增单据类型_领退料单
     */
    @ApiOperation(value = "新增单据类型_领退料单", notes = "新增单据类型_领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_领退料单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        return toAjax(conDocTypeMaterialRequisitionService.insertConDocTypeMaterialRequisition(conDocTypeMaterialRequisition));
    }

    /**
     * 修改单据类型_领退料单
     */
    @ApiOperation(value = "修改单据类型_领退料单", notes = "修改单据类型_领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_领退料单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        return toAjax(conDocTypeMaterialRequisitionService.updateConDocTypeMaterialRequisition(conDocTypeMaterialRequisition));
    }

    /**
     * 变更单据类型_领退料单
     */
    @ApiOperation(value = "变更单据类型_领退料单", notes = "变更单据类型_领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_领退料单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        return toAjax(conDocTypeMaterialRequisitionService.changeConDocTypeMaterialRequisition(conDocTypeMaterialRequisition));
    }

    /**
     * 删除单据类型_领退料单
     */
    @ApiOperation(value = "删除单据类型_领退料单", notes = "删除单据类型_领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_领退料单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeMaterialRequisitionService.deleteConDocTypeMaterialRequisitionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_领退料单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        return AjaxResult.success(conDocTypeMaterialRequisitionService.changeStatus(conDocTypeMaterialRequisition));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_领退料单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        conDocTypeMaterialRequisition.setConfirmDate(new Date());
        conDocTypeMaterialRequisition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeMaterialRequisition.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeMaterialRequisitionService.check(conDocTypeMaterialRequisition));
    }

    /**
     * 获取单据类型_领退料单列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询单据类型_领退料单列表", notes = "查询单据类型_领退料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeMaterialRequisition.class))
    public AjaxResult getList() {
        ConDocTypeMaterialRequisition conDocTypeMaterialRequisition = new ConDocTypeMaterialRequisition();
        conDocTypeMaterialRequisition.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conDocTypeMaterialRequisitionService.getList(conDocTypeMaterialRequisition));
    }

}
