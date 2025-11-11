package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PurPurchaseSource;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.impl.PurPurchaseSourceServiceImpl;
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
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
@RestController
@RequestMapping("/source")
@Api(tags = "采购货源清单")
public class PurPurchaseSourceController extends BaseController {

    @Autowired
    private PurPurchaseSourceServiceImpl purPurchaseSourceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购货源清单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购货源清单列表",
                  notes = "查询采购货源清单列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = PurPurchaseSource.class))
    public TableDataInfo list(@RequestBody PurPurchaseSource purPurchaseSource) {
        startPage(purPurchaseSource);
        List<PurPurchaseSource> list = purPurchaseSourceService.selectPurPurchaseSourceList(purPurchaseSource);
        return getDataTable(list);
    }

    /**
     * 导出采购货源清单列表
     */
    @Log(title = "采购货源清单",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购货源清单列表",
                  notes = "导出采购货源清单列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPurchaseSource purPurchaseSource) throws IOException {
        List<PurPurchaseSource> list = purPurchaseSourceService.selectPurPurchaseSourceList(purPurchaseSource);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchaseSource> util = new ExcelUtil<>(PurPurchaseSource.class, dataMap);
        util.exportExcel(response, list, "货源信息");
    }

    /**
     * 获取采购货源清单详细信息
     */
    @ApiOperation(value = "获取采购货源清单详细信息",
                  notes = "获取采购货源清单详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = PurPurchaseSource.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@Valid @NotNull(message = "参数缺失") Long purchaseSourceSid) {
        return AjaxResult.success(purPurchaseSourceService.selectPurPurchaseSourceById(purchaseSourceSid));
    }

    /**
     * 新增采购货源清单
     */
    @ApiOperation(value = "新增采购货源清单",
                  notes = "新增采购货源清单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "采购货源清单",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurPurchaseSource purPurchaseSource) {
        int row = purPurchaseSourceService.insertPurPurchaseSource(purPurchaseSource);
        if (row > 0) {
            return AjaxResult.success(purPurchaseSourceService.selectPurPurchaseSourceById(purPurchaseSource.getPurchaseSourceSid()));
        } else {
            return toAjax(row);
        }
    }

    /**
     * 修改采购货源清单
     */
    @ApiOperation(value = "修改采购货源清单",
                  notes = "修改采购货源清单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "采购货源清单",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PurPurchaseSource purPurchaseSource) {
        return toAjax(purPurchaseSourceService.updatePurPurchaseSource(purPurchaseSource));
    }

    /**
     * 删除采购货源清单
     */
    @ApiOperation(value = "删除采购货源清单",
                  notes = "删除采购货源清单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "采购货源清单",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] purchaseSourceSids) {
        if (ArrayUtil.isEmpty(purchaseSourceSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseSourceService.deletePurPurchaseSourceByIds(purchaseSourceSids));
    }

    /**
     * 采购货源清单确认
     */
    @Log(title = "采购货源清单",
         businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "采购货源清单确认",
                  notes = "采购货源清单确认")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody PurPurchaseSource purPurchaseSource) {
        return AjaxResult.success(purPurchaseSourceService.confirm(purPurchaseSource));
    }

    /**
     * 采购货源清单变更
     */
    @Log(title = "采购货源清单",
         businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "采购货源清单变更",
                  notes = "采购货源清单变更")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    public AjaxResult change(@RequestBody PurPurchaseSource purPurchaseSource) {
        return AjaxResult.success(purPurchaseSourceService.change(purPurchaseSource));
    }

    /**
     * 批量启用/停用采购货源清单
     */
    @Log(title = "采购货源清单",
         businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    @ApiOperation(value = "采购货源清单启用/停用",
                  notes = "采购货源清单启用/停用")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    public AjaxResult status(@RequestBody PurPurchaseSource purPurchaseSource) {
        return AjaxResult.success(purPurchaseSourceService.status(purPurchaseSource));
    }


    @PostMapping("/setDefault")
    @ApiOperation(value = "设置默认供应商（多选）",
                  notes = "设置默认供应商（多选）")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = PurPurchaseSource.class))
    public AjaxResult setDefault(@RequestBody PurPurchaseSource purPurchaseSource) {
        purPurchaseSourceService.setDefaultBatch(purPurchaseSource);
        return AjaxResult.success();
    }
}
