package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConInvoiceCategory;
import com.platform.ems.plug.service.IConInvoiceCategoryService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发票类别Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/invoice/category")
@Api(tags = "发票类别")
public class ConInvoiceCategoryController extends BaseController {

    @Autowired
    private IConInvoiceCategoryService conInvoiceCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询发票类别列表
     */
    @PreAuthorize(hasPermi = "ems:invoice:category:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询发票类别列表", notes = "查询发票类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceCategory.class))
    public TableDataInfo list(@RequestBody ConInvoiceCategory conInvoiceCategory) {
        startPage(conInvoiceCategory);
        List<ConInvoiceCategory> list = conInvoiceCategoryService.selectConInvoiceCategoryList(conInvoiceCategory);
        return getDataTable(list);
    }

    /**
     * 导出发票类别列表
     */
    @PreAuthorize(hasPermi = "ems:invoice:category:export")
    @Log(title = "发票类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出发票类别列表", notes = "导出发票类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInvoiceCategory conInvoiceCategory) throws IOException {
        List<ConInvoiceCategory> list = conInvoiceCategoryService.selectConInvoiceCategoryList(conInvoiceCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConInvoiceCategory> util = new ExcelUtil<>(ConInvoiceCategory.class, dataMap);
        util.exportExcel(response, list, "发票类别");
    }

    /**
     * 获取发票类别详细信息
     */
    @ApiOperation(value = "获取发票类别详细信息", notes = "获取发票类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceCategory.class))
    @PreAuthorize(hasPermi = "ems:invoice:category:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInvoiceCategoryService.selectConInvoiceCategoryById(sid));
    }

    /**
     * 新增发票类别
     */
    @ApiOperation(value = "新增发票类别", notes = "新增发票类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:category:add")
    @Log(title = "发票类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInvoiceCategory conInvoiceCategory) {
        return toAjax(conInvoiceCategoryService.insertConInvoiceCategory(conInvoiceCategory));
    }

    /**
     * 修改发票类别
     */
    @ApiOperation(value = "修改发票类别", notes = "修改发票类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:category:edit")
    @Log(title = "发票类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConInvoiceCategory conInvoiceCategory) {
        return toAjax(conInvoiceCategoryService.updateConInvoiceCategory(conInvoiceCategory));
    }

    /**
     * 变更发票类别
     */
    @ApiOperation(value = "变更发票类别", notes = "变更发票类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:category:change")
    @Log(title = "发票类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConInvoiceCategory conInvoiceCategory) {
        return toAjax(conInvoiceCategoryService.changeConInvoiceCategory(conInvoiceCategory));
    }

    /**
     * 删除发票类别
     */
    @ApiOperation(value = "删除发票类别", notes = "删除发票类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:category:remove")
    @Log(title = "发票类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInvoiceCategoryService.deleteConInvoiceCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发票类别", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:invoice:category:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInvoiceCategory conInvoiceCategory) {
        return AjaxResult.success(conInvoiceCategoryService.changeStatus(conInvoiceCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:invoice:category:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发票类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInvoiceCategory conInvoiceCategory) {
        conInvoiceCategory.setConfirmDate(new Date());
        conInvoiceCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInvoiceCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInvoiceCategoryService.check(conInvoiceCategory));
    }

    @PostMapping("/getConInvoiceCategoryList")
    @ApiOperation(value = "发票类别下拉列表", notes = "发票类别下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceCategory.class))
    public AjaxResult getConInvoiceCategoryList() {
        return AjaxResult.success(conInvoiceCategoryService.getConInvoiceCategoryList());
    }
}
