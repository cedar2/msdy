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
import com.platform.ems.domain.RepInventoryStatus;
import com.platform.ems.service.IRepInventoryStatusService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 库存状况Controller
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/Inventory/status")
@Api(tags = "库存状况")
public class RepInventoryStatusController extends BaseController {

    @Autowired
    private IRepInventoryStatusService repInventoryStatusService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询库存状况列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询库存状况列表", notes = "查询库存状况列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepInventoryStatus.class))
    public TableDataInfo list(@RequestBody RepInventoryStatus repInventoryStatus) {
        startPage(repInventoryStatus);
        List<RepInventoryStatus> list = repInventoryStatusService.selectRepInventoryStatusList(repInventoryStatus);
        return getDataTable(list);
    }

    /**
     * 导出库存状况列表
     */
    @Log(title = "库存状况", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库存状况列表", notes = "导出库存状况列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepInventoryStatus repInventoryStatus) throws IOException {
        List<RepInventoryStatus> list = repInventoryStatusService.selectRepInventoryStatusList(repInventoryStatus);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepInventoryStatus> util = new ExcelUtil<>(RepInventoryStatus.class, dataMap);
        util.exportExcel(response, list, "库存状况" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取库存状况详细信息
     */
    @ApiOperation(value = "获取库存状况详细信息", notes = "获取库存状况详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepInventoryStatus.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repInventoryStatusService.selectRepInventoryStatusById(dataRecordSid));
    }

    /**
     * 新增库存状况
     */
    @ApiOperation(value = "新增库存状况", notes = "新增库存状况")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存状况", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepInventoryStatus repInventoryStatus) {
        return toAjax(repInventoryStatusService.insertRepInventoryStatus(repInventoryStatus));
    }

    /**
     * 删除库存状况
     */
    @ApiOperation(value = "删除库存状况", notes = "删除库存状况")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存状况", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repInventoryStatusService.deleteRepInventoryStatusByIds(dataRecordSids));
    }

}
