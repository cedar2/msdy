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
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.TecRecordTechtransfer;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecRecordTechtransferService;
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
 * 技术转移记录Controller
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@RestController
@RequestMapping("/techtransfer")
@Api(tags = "技术转移记录")
public class TecRecordTechtransferController extends BaseController {

    @Autowired
    private ITecRecordTechtransferService tecRecordTechtransferService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询技术转移记录列表
     */
    @PreAuthorize(hasPermi = "ems:techtransfer:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询技术转移记录列表", notes = "查询技术转移记录列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecRecordTechtransfer.class))
    public TableDataInfo list(@RequestBody TecRecordTechtransfer tecRecordTechtransfer) {
        startPage(tecRecordTechtransfer);
        List<TecRecordTechtransfer> list = tecRecordTechtransferService.selectTecRecordTechtransferList(tecRecordTechtransfer);
        return getDataTable(list);
    }

    /**
     * 导出技术转移记录列表
     */
    @PreAuthorize(hasPermi = "ems:techtransfer:export")
    @Log(title = "技术转移记录", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出技术转移记录列表", notes = "导出技术转移记录列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecRecordTechtransfer tecRecordTechtransfer) throws IOException {
        List<TecRecordTechtransfer> list = tecRecordTechtransferService.selectTecRecordTechtransferList(tecRecordTechtransfer);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecRecordTechtransfer> util = new ExcelUtil<>(TecRecordTechtransfer.class, dataMap);
        util.exportExcel(response, list, "技术转移记录" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取技术转移记录详细信息
     */
    @ApiOperation(value = "获取技术转移记录详细信息", notes = "获取技术转移记录详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecRecordTechtransfer.class))
    @PreAuthorize(hasPermi = "ems:techtransfer:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordTechtransferSid) {
        if (recordTechtransferSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecRecordTechtransferService.selectTecRecordTechtransferById(recordTechtransferSid));
    }

    /**
     * 新增技术转移记录
     */
    @ApiOperation(value = "新增技术转移记录", notes = "新增技术转移记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:techtransfer:add")
    @Log(title = "技术转移记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecRecordTechtransfer tecRecordTechtransfer) {
        return AjaxResult.success(tecRecordTechtransferService.insertTecRecordTechtransfer(tecRecordTechtransfer));
    }

    /**
     * 修改技术转移记录
     */
    @ApiOperation(value = "修改技术转移记录", notes = "修改技术转移记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:techtransfer:edit")
    @Log(title = "技术转移记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecRecordTechtransfer tecRecordTechtransfer) {
        return toAjax(tecRecordTechtransferService.updateTecRecordTechtransfer(tecRecordTechtransfer));
    }

    /**
     * 变更技术转移记录
     */
    @ApiOperation(value = "变更技术转移记录", notes = "变更技术转移记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:techtransfer:change")
    @Log(title = "技术转移记录", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid TecRecordTechtransfer tecRecordTechtransfer) {
        return toAjax(tecRecordTechtransferService.changeTecRecordTechtransfer(tecRecordTechtransfer));
    }

    /**
     * 删除技术转移记录
     */
    @ApiOperation(value = "删除技术转移记录", notes = "删除技术转移记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:techtransfer:remove")
    @Log(title = "技术转移记录", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> recordTechtransferSids) {
        if (CollectionUtils.isEmpty(recordTechtransferSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecRecordTechtransferService.deleteTecRecordTechtransferByIds(recordTechtransferSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:techtransfer:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "技术转移记录", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecRecordTechtransfer tecRecordTechtransfer) {
        return toAjax(tecRecordTechtransferService.check(tecRecordTechtransfer));
    }

}
