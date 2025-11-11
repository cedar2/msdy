package com.platform.ems.plug.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
import com.platform.ems.plug.domain.ConPurchaseOrderCategory;
import com.platform.ems.plug.service.IConPurchaseOrderCategoryService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采购订单类别Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/purchase/order/category")
@Api(tags = "采购订单类别")
public class ConPurchaseOrderCategoryController extends BaseController {

    @Autowired
    private IConPurchaseOrderCategoryService conPurchaseOrderCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询采购订单类别列表
     */
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询采购订单类别列表", notes = "查询采购订单类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseOrderCategory.class))
    public TableDataInfo list(@RequestBody ConPurchaseOrderCategory conPurchaseOrderCategory) {
        startPage(conPurchaseOrderCategory);
        List<ConPurchaseOrderCategory> list = conPurchaseOrderCategoryService.selectConPurchaseOrderCategoryList(conPurchaseOrderCategory);
        return getDataTable(list);
    }

    /**
     * 导出采购订单类别列表
     */
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:export")
    @Log(title = "采购订单类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购订单类别列表", notes = "导出采购订单类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPurchaseOrderCategory conPurchaseOrderCategory) throws IOException {
        List<ConPurchaseOrderCategory> list = conPurchaseOrderCategoryService.selectConPurchaseOrderCategoryList(conPurchaseOrderCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPurchaseOrderCategory> util = new ExcelUtil<ConPurchaseOrderCategory>(ConPurchaseOrderCategory.class, dataMap);
        util.exportExcel(response, list, "采购订单类别" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入采购订单类别
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购订单类别", notes = "导入采购订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConPurchaseOrderCategory> util = new ExcelUtil<ConPurchaseOrderCategory>(ConPurchaseOrderCategory.class);
        List<ConPurchaseOrderCategory> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conPurchaseOrderCategory -> {
                conPurchaseOrderCategoryService.insertConPurchaseOrderCategory(conPurchaseOrderCategory);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载采购订单类别导入模板", notes = "下载采购订单类别导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConPurchaseOrderCategory> util = new ExcelUtil<ConPurchaseOrderCategory>(ConPurchaseOrderCategory.class);
        util.importTemplateExcel(response, "采购订单类别导入模板");
    }


    /**
     * 获取采购订单类别详细信息
     */
    @ApiOperation(value = "获取采购订单类别详细信息", notes = "获取采购订单类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseOrderCategory.class))
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPurchaseOrderCategoryService.selectConPurchaseOrderCategoryById(sid));
    }

    /**
     * 新增采购订单类别
     */
    @ApiOperation(value = "新增采购订单类别", notes = "新增采购订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:add")
    @Log(title = "采购订单类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPurchaseOrderCategory conPurchaseOrderCategory) {
        return toAjax(conPurchaseOrderCategoryService.insertConPurchaseOrderCategory(conPurchaseOrderCategory));
    }

    /**
     * 修改采购订单类别
     */
    @ApiOperation(value = "修改采购订单类别", notes = "修改采购订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:edit")
    @Log(title = "采购订单类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConPurchaseOrderCategory conPurchaseOrderCategory) {
        return toAjax(conPurchaseOrderCategoryService.updateConPurchaseOrderCategory(conPurchaseOrderCategory));
    }

    /**
     * 变更采购订单类别
     */
    @ApiOperation(value = "变更采购订单类别", notes = "变更采购订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:change")
    @Log(title = "采购订单类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPurchaseOrderCategory conPurchaseOrderCategory) {
        return toAjax(conPurchaseOrderCategoryService.changeConPurchaseOrderCategory(conPurchaseOrderCategory));
    }

    /**
     * 删除采购订单类别
     */
    @ApiOperation(value = "删除采购订单类别", notes = "删除采购订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:remove")
    @Log(title = "采购订单类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPurchaseOrderCategoryService.deleteConPurchaseOrderCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单类别", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPurchaseOrderCategory conPurchaseOrderCategory) {
        return AjaxResult.success(conPurchaseOrderCategoryService.changeStatus(conPurchaseOrderCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:purchaseOrderCategory:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPurchaseOrderCategory conPurchaseOrderCategory) {
        conPurchaseOrderCategory.setConfirmDate(new Date());
        conPurchaseOrderCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPurchaseOrderCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPurchaseOrderCategoryService.check(conPurchaseOrderCategory));
    }

}
