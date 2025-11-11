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
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.domain.SalServiceAcceptanceItem;
import com.platform.ems.service.ISalServiceAcceptanceItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 服务销售验收单-明细Controller
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@RestController
@RequestMapping("/acceptance/item")
@Api(tags = "服务销售验收单-明细")
public class SalServiceAcceptanceItemController extends BaseController {

    @Autowired
    private ISalServiceAcceptanceItemService salServiceAcceptanceItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询服务销售验收单-明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询服务销售验收单-明细列表", notes = "查询服务销售验收单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalServiceAcceptanceItem.class))
    public TableDataInfo list(@RequestBody SalServiceAcceptanceItem salServiceAcceptanceItem) {
        startPage();
        List<SalServiceAcceptanceItem> list = salServiceAcceptanceItemService.selectSalServiceAcceptanceItemList(salServiceAcceptanceItem);
        return getDataTable(list);
    }

    /**
     * 导出服务销售验收单-明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:export")
    @Log(title = "服务销售验收单-明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出服务销售验收单-明细列表", notes = "导出服务销售验收单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalServiceAcceptanceItem salServiceAcceptanceItem) throws IOException {
        List<SalServiceAcceptanceItem> list = salServiceAcceptanceItemService.selectSalServiceAcceptanceItemList(salServiceAcceptanceItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SalServiceAcceptanceItem> util = new ExcelUtil<SalServiceAcceptanceItem>(SalServiceAcceptanceItem.class,dataMap);
        util.exportExcel(response, list, "服务销售验收单-明细"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取服务销售验收单-明细详细信息
     */
    @ApiOperation(value = "获取服务销售验收单-明细详细信息", notes = "获取服务销售验收单-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalServiceAcceptanceItem.class))
    @PreAuthorize(hasPermi = "ems:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long serviceAcceptanceItemSid) {
                    if(serviceAcceptanceItemSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(salServiceAcceptanceItemService.selectSalServiceAcceptanceItemById(serviceAcceptanceItemSid));
    }

    /**
     * 新增服务销售验收单-明细
     */
    @ApiOperation(value = "新增服务销售验收单-明细", notes = "新增服务销售验收单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:add")
    @Log(title = "服务销售验收单-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SalServiceAcceptanceItem salServiceAcceptanceItem) {
        return toAjax(salServiceAcceptanceItemService.insertSalServiceAcceptanceItem(salServiceAcceptanceItem));
    }

    /**
     * 修改服务销售验收单-明细
     */
    @ApiOperation(value = "修改服务销售验收单-明细", notes = "修改服务销售验收单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:edit")
    @Log(title = "服务销售验收单-明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SalServiceAcceptanceItem salServiceAcceptanceItem) {
        return toAjax(salServiceAcceptanceItemService.updateSalServiceAcceptanceItem(salServiceAcceptanceItem));
    }

    /**
     * 删除服务销售验收单-明细
     */
    @ApiOperation(value = "删除服务销售验收单-明细", notes = "删除服务销售验收单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:remove")
    @Log(title = "服务销售验收单-明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  serviceAcceptanceItemSids) {
        if(ArrayUtil.isEmpty( serviceAcceptanceItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(salServiceAcceptanceItemService.deleteSalServiceAcceptanceItemByIds(serviceAcceptanceItemSids));
    }
}
