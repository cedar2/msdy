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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import javax.validation.Valid;
import com.platform.ems.domain.PurServiceAcceptance;
import com.platform.ems.service.IPurServiceAcceptanceService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 服务采购验收单Controller
 *
 * @author linhongwei
 * @date 2021-04-07
 */
@RestController
@RequestMapping("/service/acceptance")
@Api(tags = "服务采购验收单")
public class PurServiceAcceptanceController extends BaseController {

    @Autowired
    private IPurServiceAcceptanceService purServiceAcceptanceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询服务采购验收单列表
     */
//    @PreAuthorize(hasPermi = "ems:acceptance:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询服务采购验收单列表", notes = "查询服务采购验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurServiceAcceptance.class))
    public TableDataInfo list(@RequestBody PurServiceAcceptance purServiceAcceptance) {
        startPage(purServiceAcceptance);
        List<PurServiceAcceptance> list = purServiceAcceptanceService.selectPurServiceAcceptanceList(purServiceAcceptance);
        return getDataTable(list);
    }

    /**
     * 导出服务采购验收单列表
     */
//    @PreAuthorize(hasPermi = "ems:acceptance:export")
    @Log(title = "服务采购验收单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出服务采购验收单列表", notes = "导出服务采购验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurServiceAcceptance purServiceAcceptance) throws IOException {
        List<PurServiceAcceptance> list = purServiceAcceptanceService.selectPurServiceAcceptanceList(purServiceAcceptance);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurServiceAcceptance> util = new ExcelUtil<PurServiceAcceptance>(PurServiceAcceptance.class,dataMap);
        util.exportExcel(response, list, "服务采购验收单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取服务采购验收单详细信息
     */
    @ApiOperation(value = "获取服务采购验收单详细信息", notes = "获取服务采购验收单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurServiceAcceptance.class))
//    @PreAuthorize(hasPermi = "ems:acceptance:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long serviceAcceptanceSid) {
        if (serviceAcceptanceSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purServiceAcceptanceService.selectPurServiceAcceptanceById(serviceAcceptanceSid));
    }

    /**
     * 新增服务采购验收单
     */
    @ApiOperation(value = "新增服务采购验收单", notes = "新增服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:acceptance:add")
    @Log(title = "服务采购验收单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurServiceAcceptance purServiceAcceptance) {
        return toAjax(purServiceAcceptanceService.insertPurServiceAcceptance(purServiceAcceptance));
    }

    /**
     * 修改服务采购验收单
     */
    @ApiOperation(value = "修改服务采购验收单", notes = "修改服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:acceptance:edit")
    @Log(title = "服务采购验收单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PurServiceAcceptance purServiceAcceptance) {
        return toAjax(purServiceAcceptanceService.updatePurServiceAcceptance(purServiceAcceptance));
    }

    /**
     * 删除服务采购验收单
     */
    @ApiOperation(value = "删除服务采购验收单", notes = "删除服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:acceptance:remove")
    @Log(title = "服务采购验收单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] serviceAcceptanceSids) {
        if (ArrayUtil.isEmpty( serviceAcceptanceSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purServiceAcceptanceService.deletePurServiceAcceptanceByIds(serviceAcceptanceSids));
    }

    /**
     * 服务采购验收单确认
     */
//    @PreAuthorize(hasPermi = "ems:model:pos:edit")
    @Log(title = "服务采购验收单", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "服务采购验收单确认", notes = "服务采购验收单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody PurServiceAcceptance purServiceAcceptance) {
        return AjaxResult.success(purServiceAcceptanceService.confirm(purServiceAcceptance));
    }

    /**
     * 服务采购验收单变更
     */
//    @PreAuthorize(hasPermi = "ems:model:pos:edit")
    @Log(title = "服务采购验收单", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "服务采购验收单变更", notes = "服务采购验收单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody PurServiceAcceptance purServiceAcceptance) {
        return AjaxResult.success(purServiceAcceptanceService.change(purServiceAcceptance));
    }
}
