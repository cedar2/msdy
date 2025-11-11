package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasSkuGroup;
import com.platform.ems.domain.BasSkuGroupItem;
import com.platform.ems.domain.dto.response.export.BasCmGroupReport;
import com.platform.ems.domain.dto.response.export.BasSkuGroupReport;
import com.platform.ems.domain.dto.response.export.BasYsGroupReport;
import com.platform.ems.domain.dto.response.form.BasSkuGroupItemFormResponse;
import com.platform.ems.service.IBasSkuGroupService;
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
 * SKU组档案Controller
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@RestController
@RequestMapping("/sku/group")
@Api(tags = "SKU组档案")
public class BasSkuGroupController extends BaseController {

    @Autowired
    private IBasSkuGroupService basSkuGroupService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询SKU组档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询SKU组档案列表", notes = "查询SKU组档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    public TableDataInfo list(@RequestBody BasSkuGroup basSkuGroup) {
        startPage(basSkuGroup);
        List<BasSkuGroup> list = basSkuGroupService.selectBasSkuGroupList(basSkuGroup);
        return getDataTable(list);
    }

    /**
     * 导出SKU组档案列表
     */
    @ApiOperation(value = "导出SKU组档案列表", notes = "导出SKU组档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasSkuGroup basSkuGroup) throws IOException {
        List<BasSkuGroup> list = basSkuGroupService.selectBasSkuGroupList(basSkuGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        if (ConstantsEms.SKUTYP_YS.equals(basSkuGroup.getSkuType())) {
            ExcelUtil<BasYsGroupReport> util = new ExcelUtil<>(BasYsGroupReport.class, dataMap);
            List<BasYsGroupReport> basYsGroupReports = BeanCopyUtils.copyListProperties(list, BasYsGroupReport::new);
            util.exportExcel(response, basYsGroupReports, "颜色组");
        } else if (ConstantsEms.SKUTYP_CM.equals(basSkuGroup.getSkuType())) {
            ExcelUtil<BasCmGroupReport> util = new ExcelUtil<>(BasCmGroupReport.class, dataMap);
            List<BasCmGroupReport> basCmGroupReports = BeanCopyUtils.copyListProperties(list, BasCmGroupReport::new);
            util.exportExcel(response, basCmGroupReports, "尺码组");
        } else {
            ExcelUtil<BasSkuGroupReport> util = new ExcelUtil<>(BasSkuGroupReport.class, dataMap);
            List<BasSkuGroupReport> basSkuGroupReports = BeanCopyUtils.copyListProperties(list, BasSkuGroupReport::new);
            util.exportExcel(response, basSkuGroupReports, "SKU组");
        }
    }

    /**
     * 获取SKU组档案详细信息
     */
    @ApiOperation(value = "获取SKU组档案详细信息", notes = "获取SKU组档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long skuGroupSid) {
        if (skuGroupSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basSkuGroupService.selectBasSkuGroupById(skuGroupSid));
    }

    /**
     * 新增SKU组档案
     */
    @ApiOperation(value = "新增SKU组档案", notes = "新增SKU组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "SKU组档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasSkuGroup basSkuGroup) {
        int row = basSkuGroupService.insertBasSkuGroup(basSkuGroup);
        return AjaxResult.success(basSkuGroup);
    }

    /**
     * 修改SKU组档案
     */
    @ApiOperation(value = "修改SKU组档案", notes = "修改SKU组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "SKU组档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasSkuGroup basSkuGroup) {
        return toAjax(basSkuGroupService.updateBasSkuGroup(basSkuGroup));
    }

    /**
     * 删除SKU组档案
     */
    @ApiOperation(value = "删除SKU组档案", notes = "删除SKU组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "SKU组档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> skuGroupSids) {
        if (ArrayUtil.isEmpty(skuGroupSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basSkuGroupService.deleteBasSkuGroupByIds(skuGroupSids));
    }

    /**
     * 删除SKU组档案明细前的校验
     */
    @ApiOperation(value = "删除SKU组档案明细前的校验", notes = "删除SKU组档案明细前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/delete/check")
    public AjaxResult removeItemCheck(@RequestBody List<Long> skuGroupItemSidList) {
        if (ArrayUtil.isEmpty(skuGroupItemSidList)) {
            throw new CheckedException("参数缺失");
        }
        try {
            basSkuGroupService.deleteBasSkuGroupItemByIdsCheck(skuGroupItemSidList);
        } catch (CustomException e) {
            return AjaxResult.success(e.getMessage());
        }
        return AjaxResult.success(true);
    }

    @ApiOperation(value = "获取sku组下拉列表自动过滤启用和已确认", notes = "获取sku组下拉列表自动过滤启用和已确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    @PostMapping("/getList")
    public AjaxResult getList(String skuType) {
        if (StrUtil.isBlank(skuType)) {
            throw new CustomException("参数错误");
        }
        BasSkuGroup basSkuGroup = new BasSkuGroup();
        basSkuGroup.setSkuType(skuType).setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(basSkuGroupService.getList(basSkuGroup));
    }

    @ApiOperation(value = "获取sku组下拉列表", notes = "获取sku组下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    @PostMapping("/getAllList")
    public AjaxResult getAllList(@RequestBody BasSkuGroup basSkuGroup) {
        return AjaxResult.success(basSkuGroupService.getList(basSkuGroup));
    }

    @ApiOperation(value = "获取sku组的sku详情", notes = "获取sku组详情")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    @PostMapping("/getDetail")
    public AjaxResult getDetail(Long skuGroupSid) {
        if (skuGroupSid == null) {
            throw new CustomException("参数错误");
        }
        return AjaxResult.success(basSkuGroupService.getDetail(skuGroupSid));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasSkuGroup basSkuGroup) {
        if (ArrayUtil.isEmpty(basSkuGroup.getSkuGroupSidList()) || StrUtil.isBlank(basSkuGroup.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basSkuGroupService.changeStatus(basSkuGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasSkuGroup basSkuGroup) {
        if (ArrayUtil.isEmpty(basSkuGroup.getSkuGroupSidList())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basSkuGroupService.check(basSkuGroup));
    }

    /**
     * 查询SKU组明细报表
     */
    @PostMapping("/item/report")
    @ApiOperation(value = "查询SKU组明细报表", notes = "查询SKU组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    public TableDataInfo report(@RequestBody BasSkuGroupItem basSkuGroupItem) {
        startPage(basSkuGroupItem);
        List<BasSkuGroupItem> list = basSkuGroupService.getReportForm(basSkuGroupItem);
        return getDataTable(list, BasSkuGroupItemFormResponse::new);
    }

    /**
     * 导出SKU组明细报表
     */
    @ApiOperation(value = "导出SKU组明细报表", notes = "导出SKU组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void itemExport(HttpServletResponse response, BasSkuGroupItem basSkuGroupItem) throws IOException {
        List<BasSkuGroupItem> list = basSkuGroupService.getReportForm(basSkuGroupItem);
        List<BasSkuGroupItemFormResponse> responseList = BeanCopyUtils.copyListProperties(list, BasSkuGroupItemFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasSkuGroupItemFormResponse> util = new ExcelUtil<>(BasSkuGroupItemFormResponse.class, dataMap);
        util.exportExcel(response, responseList, "SKU组明细报表");
    }
}
