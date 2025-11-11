package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepPurchaseStatusOutsourceProcess;
import com.platform.ems.service.IRepPurchaseStatusOutsourceProcessService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购状况-外发加工结算Controller
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/purchase/status/outsource/process")
@Api(tags = "采购状况-外发加工结算")
public class RepPurchaseStatusOutsourceProcessController extends BaseController {

    @Autowired
    private IRepPurchaseStatusOutsourceProcessService repPurchaseStatusOutsourceProcessService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购状况-外发加工结算列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购状况-外发加工结算列表", notes = "查询采购状况-外发加工结算列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepPurchaseStatusOutsourceProcess.class))
    public TableDataInfo list(@RequestBody RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess) {
        startPage(repPurchaseStatusOutsourceProcess);
        List<RepPurchaseStatusOutsourceProcess> list = repPurchaseStatusOutsourceProcessService.selectRepPurchaseStatusOutsourceProcessList(repPurchaseStatusOutsourceProcess);
        return getDataTable(list);
    }

    /**
     * 导出采购状况-外发加工结算列表
     */
    @Log(title = "采购状况-外发加工结算", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购状况-外发加工结算列表", notes = "导出采购状况-外发加工结算列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess) throws IOException {
        List<RepPurchaseStatusOutsourceProcess> list = repPurchaseStatusOutsourceProcessService.selectRepPurchaseStatusOutsourceProcessList(repPurchaseStatusOutsourceProcess);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepPurchaseStatusOutsourceProcess> util = new ExcelUtil<>(RepPurchaseStatusOutsourceProcess.class, dataMap);
        util.exportExcel(response, list, "采购状况-外发加工结算" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取采购状况-外发加工结算详细信息
     */
    @ApiOperation(value = "获取采购状况-外发加工结算详细信息", notes = "获取采购状况-外发加工结算详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepPurchaseStatusOutsourceProcess.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repPurchaseStatusOutsourceProcessService.selectRepPurchaseStatusOutsourceProcessById(dataRecordSid));
    }

    /**
     * 新增采购状况-外发加工结算
     */
    @ApiOperation(value = "新增采购状况-外发加工结算", notes = "新增采购状况-外发加工结算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购状况-外发加工结算", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess) {
        return toAjax(repPurchaseStatusOutsourceProcessService.insertRepPurchaseStatusOutsourceProcess(repPurchaseStatusOutsourceProcess));
    }

    /**
     * 删除采购状况-外发加工结算
     */
    @ApiOperation(value = "删除采购状况-外发加工结算", notes = "删除采购状况-外发加工结算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购状况-外发加工结算", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repPurchaseStatusOutsourceProcessService.deleteRepPurchaseStatusOutsourceProcessByIds(dataRecordSids));
    }

}
