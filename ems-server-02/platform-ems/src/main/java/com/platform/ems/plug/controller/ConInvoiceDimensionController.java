package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConInvoiceDimension;
import com.platform.ems.plug.service.IConInvoiceDimensionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 发票维度Controller
 *
 * @author chenkw
 * @date 2021-08-11
 */
@RestController
@RequestMapping("/con/invoice/dimension")
@Api(tags = "发票维度")
public class ConInvoiceDimensionController extends BaseController {

    @Autowired
    private IConInvoiceDimensionService conInvoiceDimensionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询发票维度列表
     */
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询发票维度列表", notes = "查询发票维度列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceDimension.class))
    public TableDataInfo list(@RequestBody ConInvoiceDimension conInvoiceDimension) {
        startPage(conInvoiceDimension);
        List<ConInvoiceDimension> list = conInvoiceDimensionService.selectConInvoiceDimensionList(conInvoiceDimension);
        return getDataTable(list);
    }

    /**
     * 导出发票维度列表
     */
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:export")
    @Log(title = "发票维度", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出发票维度列表", notes = "导出发票维度列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInvoiceDimension conInvoiceDimension) throws IOException {
        List<ConInvoiceDimension> list = conInvoiceDimensionService.selectConInvoiceDimensionList(conInvoiceDimension);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConInvoiceDimension> util = new ExcelUtil<>(ConInvoiceDimension.class,dataMap);
        util.exportExcel(response, list, "开票维度");
    }


    /**
     * 获取发票维度详细信息
     */
    @ApiOperation(value = "获取发票维度详细信息", notes = "获取发票维度详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceDimension.class))
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if(sid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInvoiceDimensionService.selectConInvoiceDimensionById(sid));
    }

    /**
     * 新增发票维度
     */
    @ApiOperation(value = "新增发票维度", notes = "新增发票维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:add")
    @Log(title = "发票维度", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInvoiceDimension conInvoiceDimension) {
        return toAjax(conInvoiceDimensionService.insertConInvoiceDimension(conInvoiceDimension));
    }

    /**
     * 修改发票维度
     */
    @ApiOperation(value = "修改发票维度", notes = "修改发票维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:edit")
    @Log(title = "发票维度", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConInvoiceDimension conInvoiceDimension) {
        return toAjax(conInvoiceDimensionService.updateConInvoiceDimension(conInvoiceDimension));
    }

    /**
     * 变更发票维度
     */
    @ApiOperation(value = "变更发票维度", notes = "变更发票维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:change")
    @Log(title = "发票维度", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConInvoiceDimension conInvoiceDimension) {
        return toAjax(conInvoiceDimensionService.changeConInvoiceDimension(conInvoiceDimension));
    }

    /**
     * 删除发票维度
     */
    @ApiOperation(value = "删除发票维度", notes = "删除发票维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:remove")
    @Log(title = "发票维度", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(CollectionUtils.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInvoiceDimensionService.deleteConInvoiceDimensionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发票维度", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInvoiceDimension conInvoiceDimension) {
        return AjaxResult.success(conInvoiceDimensionService.changeStatus(conInvoiceDimension));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:invoice:dimension:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发票维度", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInvoiceDimension conInvoiceDimension) {
        conInvoiceDimension.setConfirmDate(new Date());
        conInvoiceDimension.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInvoiceDimension.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInvoiceDimensionService.check(conInvoiceDimension));
    }

    /**
     * 发票维度下拉框列表
     */
    @PostMapping("/getInvoiceDimensionList")
    @ApiOperation(value = "发票维度下拉框列表", notes = "发票维度下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceDimension.class))
    public AjaxResult getConAccountCategoryList() {
        return AjaxResult.success(conInvoiceDimensionService.getInvoiceDimensionList());
    }

}
