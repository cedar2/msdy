package com.platform.ems.controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialPackageItem;
import com.platform.ems.domain.dto.request.BasSaleOrderRequest;
import com.platform.ems.service.ISalSalesOrderService;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasMaterialPackage;
import com.platform.ems.domain.dto.request.MaterialPackageAcitonRequest;
import com.platform.ems.service.IBasMaterialPackageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 常规辅料包-主Controller
 *
 * @author shakeflags
 * @date 2021-03-14
 */
@RestController
@RequestMapping("/package")
@Api(tags = "常规辅料包")
public class BasMaterialPackageController extends BaseController {

    @Autowired
    private IBasMaterialPackageService basMaterialPackageService;

    @Autowired
    private ISalSalesOrderService salSalesOrderService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询常规辅料包-主列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询常规辅料包-主列表", notes = "查询常规辅料包-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialPackage.class))
    public TableDataInfo list(@RequestBody BasMaterialPackage basMaterialPackage) {
        startPage(basMaterialPackage);
        List<BasMaterialPackage> list = basMaterialPackageService.selectBasMaterialPackageList(basMaterialPackage);
        return getDataTable(list);
    }

    /**
     * 导出常规辅料包-主列表
     */
    @PreAuthorize(hasPermi = "ems:package:export")
    @Log(title = "常规辅料包-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出常规辅料包-主列表", notes = "导出常规辅料包-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialPackage basMaterialPackage) throws IOException {
        List<BasMaterialPackage> list = basMaterialPackageService.selectBasMaterialPackageList(basMaterialPackage);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialPackage> util = new ExcelUtil<>(BasMaterialPackage.class, dataMap);
        util.exportExcel(response, list, "物料包");
    }

    /**
     * 获取常规辅料包-主详细信息
     */
    @ApiOperation(value = "获取常规辅料包-主详细信息", notes = "获取常规辅料包-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialPackage.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialPackageSid) {
        return AjaxResult.success(basMaterialPackageService.selectBasMaterialPackageById(materialPackageSid));
    }

    /**
     * 新增常规辅料包-主
     */
    @ApiOperation(value = "新增常规辅料包-主", notes = "新增常规辅料包-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:add")
    @Log(title = "常规辅料包-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialPackage basMaterialPackage) {
        return basMaterialPackageService.insertBasMaterialPackage(basMaterialPackage);
    }

    /**
     * 修改常规辅料包-主
     */
    @ApiOperation(value = "修改常规辅料包-主", notes = "修改常规辅料包-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:edit")
    @Log(title = "常规辅料包-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialPackage basMaterialPackage) {
        String checkStatus = ConstantsEms.CHECK_STATUS;
        String handleStatus = basMaterialPackage.getHandleStatus();
        if (checkStatus.equals(handleStatus)) {
            basMaterialPackage.setConfirmDate(new Date());
            basMaterialPackage.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return basMaterialPackageService.updateBasMaterialPackage(basMaterialPackage);
    }

    /**
     * 删除常规辅料包-主
     */
    @ApiOperation(value = "删除常规辅料包-主", notes = "删除常规辅料包-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:remove")
    @Log(title = "常规辅料包-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> materialPackageSid) {
        return basMaterialPackageService.deleteBasMaterialPackageByIds(materialPackageSid);
    }

    /**
     * 变更常规辅料包
     */
    @ApiOperation(value = "变更常规辅料包-主", notes = "变更常规辅料包-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:change")
    @Log(title = "常规辅料包-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasMaterialPackage basMaterialPackage) {
        return basMaterialPackageService.changeBasMaterialPackage(basMaterialPackage);
    }

    /**
     * *确认常规辅料包
     */
    @ApiOperation(value = "确认常规辅料包-主", notes = "确认常规辅料包-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:check")
    @Log(title = "常规辅料包-主", businessType = BusinessType.CHECK)
    @PostMapping("/confirm")
    public AjaxResult confirm(@RequestBody MaterialPackageAcitonRequest materialPackageAcitonRequest) {
        return basMaterialPackageService.confirmBasMaterialPackage(materialPackageAcitonRequest);
    }

    /**
     * *启用/停用 常规辅料包
     */
    @ApiOperation(value = "启用/停用常规辅料包-主", notes = "启用/停用常规辅料包-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:enableordisable")
    @Log(title = "常规辅料包-主", businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/status")
    public AjaxResult status(@RequestBody MaterialPackageAcitonRequest materialPackageAcitonRequest) {
        return basMaterialPackageService.status(materialPackageAcitonRequest);
    }

    /**
     * 查询所有辅料包sid、name用于下拉框
     *
     * @return
     */
    @ApiOperation(value = "询所有辅料包sid、name用于下拉框", notes = "询所有辅料包sid、name用于下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getMaterialPackageList")
    public AjaxResult getMaterialPackageList() {
        return AjaxResult.success(basMaterialPackageService.getMaterialPackageList());
    }

    /**
     * 根据主表sid查询辅料包List
     */
    @ApiOperation(value = "根据主表sid查询辅料包List", notes = "根据主表sid查询辅料包List")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getMaterialPackageItemList")
    public AjaxResult getMaterialPackageItemList(@RequestBody List<Long> materialPackageSids) {
        return AjaxResult.success(basMaterialPackageService.getMaterialPackageItemList(materialPackageSids));
    }

    /**
     * 根据主表sid查询辅料包List   销售订单 / 采购订单  需要同步 自动获取 价格
     */
    @ApiOperation(value = "根据主表sid查询辅料包List", notes = "根据主表sid查询辅料包List")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getMaterialPackageItemList/order")
    public AjaxResult getMaterialPackageItemListOrder(@RequestBody BasSaleOrderRequest basSaleOrderRequest) {
        List<Long> materialPackageSids = Arrays.asList(basSaleOrderRequest.getMaterialPackageSids());
        List<BasMaterialPackageItem> response = basMaterialPackageService.getMaterialPackageItemList(materialPackageSids);
        if (CollectionUtil.isNotEmpty(response)) {
            Long[] barcodeSids = response.stream().map(BasMaterialPackageItem::getBarcodeSid).toArray(Long[]::new);
            basSaleOrderRequest.setMaterialBarcodeSidList(barcodeSids);
            List<BasMaterial> materialList = salSalesOrderService.getMaterialInfo(basSaleOrderRequest);
            return AjaxResult.success(materialList);
        }
        return AjaxResult.success(response);
    }

    /**
     * 复制常规辅料包-主详细信息
     */
    @ApiOperation(value = "复制常规辅料包-主详细信息", notes = "复制常规辅料包-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialPackage.class))
    @PreAuthorize(hasPermi = "ems:package:copy")
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(@RequestBody Long materialPackageSid) {
        return AjaxResult.success(basMaterialPackageService.copyBasMaterialPackageById(materialPackageSid));
    }

}
