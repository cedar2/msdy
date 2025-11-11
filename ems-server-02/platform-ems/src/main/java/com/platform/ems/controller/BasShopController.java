package com.platform.ems.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.BasShop;
import com.platform.ems.service.IBasShopService;
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
 * 店铺档案Controller
 *
 * @author c
 * @date 2022-03-31
 */
@RestController
@RequestMapping("/bas/shop")
@Api(tags = "店铺档案")
public class BasShopController extends BaseController {

    @Autowired
    private IBasShopService basShopService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询店铺档案列表
     */
    @PreAuthorize(hasPermi = "ems:shop:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询店铺档案列表", notes = "查询店铺档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasShop.class))
    public TableDataInfo list(@RequestBody BasShop basShop) {
        startPage(basShop);
        List<BasShop> list = basShopService.selectBasShopList(basShop);
        return getDataTable(list);
    }

    /**
     * 导出店铺档案列表
     */
    @PreAuthorize(hasPermi = "ems:shop:export")
    @Log(title = "店铺档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出店铺档案列表", notes = "导出店铺档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasShop basShop) throws IOException {
        List<BasShop> list = basShopService.selectBasShopList(basShop);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasShop> util = new ExcelUtil<>(BasShop.class, dataMap);
        util.exportExcel(response, list, "店铺");
    }


    /**
     * 获取店铺档案详细信息
     */
    @ApiOperation(value = "获取店铺档案详细信息", notes = "获取店铺档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasShop.class))
    @PreAuthorize(hasPermi = "ems:shop:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long shopSid) {
        if (shopSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basShopService.selectBasShopById(shopSid));
    }

    /**
     * 新增店铺档案
     */
    @ApiOperation(value = "新增店铺档案", notes = "新增店铺档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shop:add")
    @Log(title = "店铺档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasShop basShop) {
        return toAjax(basShopService.insertBasShop(basShop));
    }

    /**
     * 修改店铺档案
     */
    @ApiOperation(value = "修改店铺档案", notes = "修改店铺档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shop:edit")
    @Log(title = "店铺档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasShop basShop) {
        return toAjax(basShopService.updateBasShop(basShop));
    }

    /**
     * 变更店铺档案
     */
    @ApiOperation(value = "变更店铺档案", notes = "变更店铺档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shop:change")
    @Log(title = "店铺档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasShop basShop) {
        return toAjax(basShopService.changeBasShop(basShop));
    }

    /**
     * 删除店铺档案
     */
    @ApiOperation(value = "删除店铺档案", notes = "删除店铺档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shop:remove")
    @Log(title = "店铺档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> shopSids) {
        if (CollectionUtils.isEmpty(shopSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basShopService.deleteBasShopByIds(shopSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "店铺档案", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:shop:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasShop basShop) {
        if (ArrayUtil.isEmpty(basShop.getShopSidList()) || CharSequenceUtil.isEmpty(basShop.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basShopService.changeStatus(basShop));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:shop:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "店铺档案", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasShop basShop) {
        if (ArrayUtil.isEmpty(basShop.getShopSidList()) || CharSequenceUtil.isEmpty(basShop.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basShopService.check(basShop));
    }

}
