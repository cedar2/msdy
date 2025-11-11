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
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConInvoiceItemCategoryP;
import com.platform.ems.plug.service.IConInvoiceItemCategoryPService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 类别_采购发票行项目Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/invoice/p")
@Api(tags = "类别_采购发票行项目")
public class ConInvoiceItemCategoryPController extends BaseController {

    @Autowired
    private IConInvoiceItemCategoryPService conInvoiceItemCategoryPService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询类别_采购发票行项目列表
     */
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询类别_采购发票行项目列表", notes = "查询类别_采购发票行项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceItemCategoryP.class))
    public TableDataInfo list(@RequestBody ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        startPage(conInvoiceItemCategoryP);
        List<ConInvoiceItemCategoryP> list = conInvoiceItemCategoryPService.selectConInvoiceItemCategoryPList(conInvoiceItemCategoryP);
        return getDataTable(list);
    }

    /**
     * 导出类别_采购发票行项目列表
     */
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:export")
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出类别_采购发票行项目列表", notes = "导出类别_采购发票行项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInvoiceItemCategoryP conInvoiceItemCategoryP) throws IOException {
        List<ConInvoiceItemCategoryP> list = conInvoiceItemCategoryPService.selectConInvoiceItemCategoryPList(conInvoiceItemCategoryP);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConInvoiceItemCategoryP> util = new ExcelUtil<>(ConInvoiceItemCategoryP.class, dataMap);
        util.exportExcel(response, list, "类别_采购发票行项目");
    }

    /**
     * 获取类别_采购发票行项目详细信息
     */
    @ApiOperation(value = "获取类别_采购发票行项目详细信息", notes = "获取类别_采购发票行项目详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceItemCategoryP.class))
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInvoiceItemCategoryPService.selectConInvoiceItemCategoryPById(sid));
    }

    /**
     * 新增类别_采购发票行项目
     */
    @ApiOperation(value = "新增类别_采购发票行项目", notes = "新增类别_采购发票行项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:add")
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        return toAjax(conInvoiceItemCategoryPService.insertConInvoiceItemCategoryP(conInvoiceItemCategoryP));
    }

    /**
     * 修改类别_采购发票行项目
     */
    @ApiOperation(value = "修改类别_采购发票行项目", notes = "修改类别_采购发票行项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:edit")
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        return toAjax(conInvoiceItemCategoryPService.updateConInvoiceItemCategoryP(conInvoiceItemCategoryP));
    }

    /**
     * 变更类别_采购发票行项目
     */
    @ApiOperation(value = "变更类别_采购发票行项目", notes = "变更类别_采购发票行项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:change")
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        return toAjax(conInvoiceItemCategoryPService.changeConInvoiceItemCategoryP(conInvoiceItemCategoryP));
    }

    /**
     * 删除类别_采购发票行项目
     */
    @ApiOperation(value = "删除类别_采购发票行项目", notes = "删除类别_采购发票行项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:remove")
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInvoiceItemCategoryPService.deleteConInvoiceItemCategoryPByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        return AjaxResult.success(conInvoiceItemCategoryPService.changeStatus(conInvoiceItemCategoryP));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:invoice:item:category:p:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "类别_采购发票行项目", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        conInvoiceItemCategoryP.setConfirmDate(new Date());
        conInvoiceItemCategoryP.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInvoiceItemCategoryP.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInvoiceItemCategoryPService.check(conInvoiceItemCategoryP));
    }

}
