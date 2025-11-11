package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.RepManufactureStatusCipin;
import com.platform.ems.service.IRepManufactureStatusCipinService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
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
 * 生产状况-次品Controller
 *
 * @author c
 * @date 2022-03-17
 */
@RestController
@RequestMapping("/rep/manufacture/status/cipin")
@Api(tags = "生产状况-次品")
public class RepManufactureStatusCipinController extends BaseController {

    @Autowired
    private IRepManufactureStatusCipinService repManufactureStatusCipinService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产状况-次品列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产状况-次品列表", notes = "查询生产状况-次品列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepManufactureStatusCipin.class))
    public TableDataInfo list(@RequestBody RepManufactureStatusCipin repManufactureStatusCipin) {
        startPage(repManufactureStatusCipin);
        List<RepManufactureStatusCipin> list = repManufactureStatusCipinService.selectRepManufactureStatusCipinList(repManufactureStatusCipin);
        return getDataTable(list);
    }

    /**
     * 导出生产状况-次品列表
     */
    @Log(title = "生产状况-次品", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产状况-次品列表", notes = "导出生产状况-次品列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepManufactureStatusCipin repManufactureStatusCipin) throws IOException {
        List<RepManufactureStatusCipin> list = repManufactureStatusCipinService.selectRepManufactureStatusCipinList(repManufactureStatusCipin);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepManufactureStatusCipin> util = new ExcelUtil<>(RepManufactureStatusCipin.class, dataMap);
        util.exportExcel(response, list, "生产状况-次品" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取生产状况-次品详细信息
     */
    @ApiOperation(value = "获取生产状况-次品详细信息", notes = "获取生产状况-次品详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepManufactureStatusCipin.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repManufactureStatusCipinService.selectRepManufactureStatusCipinById(dataRecordSid));
    }

    /**
     * 新增生产状况-次品
     */
    @ApiOperation(value = "新增生产状况-次品", notes = "新增生产状况-次品")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产状况-次品", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepManufactureStatusCipin repManufactureStatusCipin) {
        return toAjax(repManufactureStatusCipinService.insertRepManufactureStatusCipin(repManufactureStatusCipin));
    }

    /**
     * 删除生产状况-次品
     */
    @ApiOperation(value = "删除生产状况-次品", notes = "删除生产状况-次品")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产状况-次品", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repManufactureStatusCipinService.deleteRepManufactureStatusCipinByIds(dataRecordSids));
    }

}
