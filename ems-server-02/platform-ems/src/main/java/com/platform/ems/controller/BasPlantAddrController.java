package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.BasPlantAddr;
import com.platform.ems.service.IBasPlantAddrService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * 工厂-联系方式信息Controller
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@RestController
@RequestMapping("/addr")
@Api(tags = "工厂-联系方式信息")
public class BasPlantAddrController extends BaseController {

    @Autowired
    private IBasPlantAddrService basPlantAddrService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询工厂-联系方式信息列表
     */
    @PreAuthorize(hasPermi = "ems:addr:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂-联系方式信息列表", notes = "查询工厂-联系方式信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantAddr.class))
    public TableDataInfo list(@RequestBody BasPlantAddr basPlantAddr) {
        startPage();
        List<BasPlantAddr> list = basPlantAddrService.selectBasPlantAddrList(basPlantAddr);
        return getDataTable(list);
    }

    /**
     * 导出工厂-联系方式信息列表
     */
    @PreAuthorize(hasPermi = "ems:addr:export")
    @Log(title = "工厂-联系方式信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂-联系方式信息列表", notes = "导出工厂-联系方式信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPlantAddr basPlantAddr) throws IOException {
        List<BasPlantAddr> list = basPlantAddrService.selectBasPlantAddrList(basPlantAddr);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasPlantAddr> util = new ExcelUtil<BasPlantAddr>(BasPlantAddr.class,dataMap);
        util.exportExcel(response, list, "工厂-联系方式信息");
    }

    /**
     * 获取工厂-联系方式信息详细信息
     */
    @ApiOperation(value = "获取工厂-联系方式信息详细信息", notes = "获取工厂-联系方式信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantAddr.class))
//    @PreAuthorize(hasPermi = "ems:addr:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long plantContactSid) {
        return AjaxResult.success(basPlantAddrService.selectBasPlantAddrById(plantContactSid));
    }

    /**
     * 新增工厂-联系方式信息
     */
    @ApiOperation(value = "新增工厂-联系方式信息", notes = "新增工厂-联系方式信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:addr:add")
    @Log(title = "工厂-联系方式信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPlantAddr basPlantAddr) {
        return toAjax(basPlantAddrService.insertBasPlantAddr(basPlantAddr));
    }

    /**
     * 修改工厂-联系方式信息
     */
    @ApiOperation(value = "修改工厂-联系方式信息", notes = "修改工厂-联系方式信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:addr:edit")
    @Log(title = "工厂-联系方式信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasPlantAddr basPlantAddr) {
        return toAjax(basPlantAddrService.updateBasPlantAddr(basPlantAddr));
    }

    /**
     * 删除工厂-联系方式信息
     */
    @ApiOperation(value = "删除工厂-联系方式信息", notes = "删除工厂-联系方式信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:addr:remove")
    @Log(title = "工厂-联系方式信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  plantContactSids) {
        return toAjax(basPlantAddrService.deleteBasPlantAddrByIds(plantContactSids));
    }
}
