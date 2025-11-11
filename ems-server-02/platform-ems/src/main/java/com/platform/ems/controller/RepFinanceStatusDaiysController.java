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
import com.platform.ems.domain.RepFinanceStatusDaiys;
import com.platform.ems.service.IRepFinanceStatusDaiysService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 财务状况-客户-待预收Controller
 *
 * @author chenkw
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/finance/status/daiys")
@Api(tags = "财务状况-客户-待预收")
public class RepFinanceStatusDaiysController extends BaseController {

    @Autowired
    private IRepFinanceStatusDaiysService repFinanceStatusDaiysService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询财务状况-客户-待预收列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询财务状况-客户-待预收列表", notes = "查询财务状况-客户-待预收列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepFinanceStatusDaiys.class))
    public TableDataInfo list(@RequestBody RepFinanceStatusDaiys repFinanceStatusDaiys) {
        startPage(repFinanceStatusDaiys);
        List<RepFinanceStatusDaiys> list = repFinanceStatusDaiysService.selectRepFinanceStatusDaiysList(repFinanceStatusDaiys);
        return getDataTable(list);
    }

    /**
     * 导出财务状况-客户-待预收列表
     */
    @Log(title = "财务状况-客户-待预收", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出财务状况-客户-待预收列表", notes = "导出财务状况-客户-待预收列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepFinanceStatusDaiys repFinanceStatusDaiys) throws IOException {
        List<RepFinanceStatusDaiys> list = repFinanceStatusDaiysService.selectRepFinanceStatusDaiysList(repFinanceStatusDaiys);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepFinanceStatusDaiys> util = new ExcelUtil<>(RepFinanceStatusDaiys.class, dataMap);
        util.exportExcel(response, list, "财务状况-客户-待预收" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取财务状况-客户-待预收详细信息
     */
    @ApiOperation(value = "获取财务状况-客户-待预收详细信息", notes = "获取财务状况-客户-待预收详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepFinanceStatusDaiys.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repFinanceStatusDaiysService.selectRepFinanceStatusDaiysById(dataRecordSid));
    }

    /**
     * 新增财务状况-客户-待预收
     */
    @ApiOperation(value = "新增财务状况-客户-待预收", notes = "新增财务状况-客户-待预收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "财务状况-客户-待预收", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepFinanceStatusDaiys repFinanceStatusDaiys) {
        return toAjax(repFinanceStatusDaiysService.insertRepFinanceStatusDaiys(repFinanceStatusDaiys));
    }

    /**
     * 删除财务状况-客户-待预收
     */
    @ApiOperation(value = "删除财务状况-客户-待预收", notes = "删除财务状况-客户-待预收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "财务状况-客户-待预收", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repFinanceStatusDaiysService.deleteRepFinanceStatusDaiysByIds(dataRecordSids));
    }

}
