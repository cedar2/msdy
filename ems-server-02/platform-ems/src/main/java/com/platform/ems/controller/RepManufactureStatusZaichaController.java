package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.RepManufactureStatusZaicha;
import com.platform.ems.service.IRepManufactureStatusZaichaService;
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
 * 生产状况-在产Controller
 *
 * @author c
 * @date 2022-03-17
 */
@RestController
@RequestMapping("/rep/manufacture/status/zaicha")
@Api(tags = "生产状况-在产")
public class RepManufactureStatusZaichaController extends BaseController {

    @Autowired
    private IRepManufactureStatusZaichaService repManufactureStatusZaichaService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产状况-在产列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产状况-在产列表", notes = "查询生产状况-在产列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepManufactureStatusZaicha.class))
    public TableDataInfo list(@RequestBody RepManufactureStatusZaicha repManufactureStatusZaicha) {
        startPage(repManufactureStatusZaicha);
        List<RepManufactureStatusZaicha> list = repManufactureStatusZaichaService.selectRepManufactureStatusZaichaList(repManufactureStatusZaicha);
        return getDataTable(list);
    }

    /**
     * 导出生产状况-在产列表
     */
    @Log(title = "生产状况-在产", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产状况-在产列表", notes = "导出生产状况-在产列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepManufactureStatusZaicha repManufactureStatusZaicha) throws IOException {
        List<RepManufactureStatusZaicha> list = repManufactureStatusZaichaService.selectRepManufactureStatusZaichaList(repManufactureStatusZaicha);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepManufactureStatusZaicha> util = new ExcelUtil<>(RepManufactureStatusZaicha.class, dataMap);
        util.exportExcel(response, list, "生产状况-在产");
    }


    /**
     * 获取生产状况-在产详细信息
     */
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepManufactureStatusZaicha.class))
    @PreAuthorize(hasPermi = "ems:rep:manufacture:status:zaicha:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repManufactureStatusZaichaService.selectRepManufactureStatusZaichaById(dataRecordSid));
    }

    /**
     * 新增生产状况-在产
     */
    @ApiOperation(value = "新增生产状况-在产", notes = "新增生产状况-在产")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产状况-在产", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepManufactureStatusZaicha repManufactureStatusZaicha) {
        return toAjax(repManufactureStatusZaichaService.insertRepManufactureStatusZaicha(repManufactureStatusZaicha));
    }

    /**
     * 删除生产状况-在产
     */
    @ApiOperation(value = "删除生产状况-在产", notes = "删除生产状况-在产")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产状况-在产", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repManufactureStatusZaichaService.deleteRepManufactureStatusZaichaByIds(dataRecordSids));
    }

}
