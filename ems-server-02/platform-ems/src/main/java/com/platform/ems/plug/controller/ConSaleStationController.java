package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.Idempotent;
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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConSaleStation;
import com.platform.ems.plug.service.IConSaleStationService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售站点Controller
 *
 * @author chenkw
 * @date 2023-01-02
 */
@RestController
@RequestMapping("/con/sale/station")
@Api(tags = "销售站点")
public class ConSaleStationController extends BaseController {

    @Autowired
    private IConSaleStationService conSaleStationService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售站点列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售站点列表", notes = "查询销售站点列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleStation.class))
    public TableDataInfo list(@RequestBody ConSaleStation conSaleStation) {
        startPage(conSaleStation);
        List<ConSaleStation> list = conSaleStationService.selectConSaleStationList(conSaleStation);
        return getDataTable(list);
    }

    /**
     * 导出销售站点列表
     */
    @Log(title = "销售站点", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售站点列表", notes = "导出销售站点列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSaleStation conSaleStation) throws IOException {
        List<ConSaleStation> list = conSaleStationService.selectConSaleStationList(conSaleStation);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConSaleStation> util = new ExcelUtil<>(ConSaleStation.class, dataMap);
        util.exportExcel(response, list, "销售站点-网店");
    }


    /**
     * 获取销售站点详细信息
     */
    @ApiOperation(value = "获取销售站点详细信息", notes = "获取销售站点详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleStation.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conSaleStationService.selectConSaleStationById(sid));
    }

    /**
     * 新增销售站点
     */
    @ApiOperation(value = "新增销售站点", notes = "新增销售站点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售站点", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ConSaleStation conSaleStation) {
        int row = conSaleStationService.insertConSaleStation(conSaleStation);
        if (row > 0) {
            return AjaxResult.success(conSaleStationService.selectConSaleStationById(conSaleStation.getSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改销售站点", notes = "修改销售站点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售站点", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid ConSaleStation conSaleStation) {
        return toAjax(conSaleStationService.updateConSaleStation(conSaleStation));
    }

    /**
     * 变更销售站点
     */
    @ApiOperation(value = "变更销售站点", notes = "变更销售站点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售站点", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConSaleStation conSaleStation) {
        return toAjax(conSaleStationService.changeConSaleStation(conSaleStation));
    }

    /**
     * 删除销售站点
     */
    @ApiOperation(value = "删除销售站点", notes = "删除销售站点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售站点", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSaleStationService.deleteConSaleStationByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售站点", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSaleStation conSaleStation) {
        return AjaxResult.success(conSaleStationService.changeStatus(conSaleStation));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售站点", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ConSaleStation conSaleStation) {
        if (ArrayUtil.isEmpty(conSaleStation.getSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(conSaleStation.getHandleStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(conSaleStationService.check(conSaleStation));
    }

}
