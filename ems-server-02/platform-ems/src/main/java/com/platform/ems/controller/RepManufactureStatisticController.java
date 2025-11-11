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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepManufactureStatistic;
import com.platform.ems.service.IRepManufactureStatisticService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产统计报Controller
 *
 * @author chenkw
 * @date 2022-05-11
 */
@RestController
@RequestMapping("/rep/manufacture/statistic")
@Api(tags = "生产统计报")
public class RepManufactureStatisticController extends BaseController {

    @Autowired
    private IRepManufactureStatisticService repManufactureStatisticService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产统计报列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产统计报列表", notes = "查询生产统计报列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepManufactureStatistic.class))
    public TableDataInfo list(@RequestBody RepManufactureStatistic repManufactureStatistic) {
        startPage(repManufactureStatistic);
        List<RepManufactureStatistic> list = repManufactureStatisticService.selectRepManufactureStatisticList(repManufactureStatistic);
        return getDataTable(list);
    }

    /**
     * 导出生产统计报列表
     */
    @Log(title = "生产统计报", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产统计报列表", notes = "导出生产统计报列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepManufactureStatistic repManufactureStatistic) throws IOException {
        List<RepManufactureStatistic> list = repManufactureStatisticService.selectRepManufactureStatisticList(repManufactureStatistic);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepManufactureStatistic> util = new ExcelUtil<>(RepManufactureStatistic.class, dataMap);
        util.exportExcel(response, list, "生产统计报");
    }


    /**
     * 获取生产统计报详细信息
     */
    @ApiOperation(value = "获取生产统计报详细信息", notes = "获取生产统计报详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepManufactureStatistic.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repManufactureStatisticService.selectRepManufactureStatisticById(dataRecordSid));
    }

    /**
     * 新增生产统计报
     */
    @ApiOperation(value = "新增生产统计报", notes = "新增生产统计报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产统计报", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepManufactureStatistic repManufactureStatistic) {
        return toAjax(repManufactureStatisticService.insertRepManufactureStatistic(repManufactureStatistic));
    }

    /**
     * 删除生产统计报
     */
    @ApiOperation(value = "删除生产统计报", notes = "删除生产统计报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产统计报", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repManufactureStatisticService.deleteRepManufactureStatisticByIds(dataRecordSids));
    }

}
