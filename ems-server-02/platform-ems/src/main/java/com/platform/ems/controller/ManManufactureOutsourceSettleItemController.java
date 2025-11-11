package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.ems.domain.dto.response.form.ManOutsourceSettleStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.platform.ems.domain.ManManufactureOutsourceSettleItem;
import com.platform.ems.service.IManManufactureOutsourceSettleItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 外发加工费结算单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-15
 */
@RestController
@RequestMapping("/man/outsource/settle/item")
@Api(tags = "外发加工费结算单明细报表")
public class ManManufactureOutsourceSettleItemController extends BaseController {

    @Autowired
    private IManManufactureOutsourceSettleItemService manManufactureOutsourceSettleItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询外发加工费结算单明细报表列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询外发加工费结算单明细报表列表", notes = "查询外发加工费结算单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOutsourceSettleItem.class))
    public TableDataInfo list(@RequestBody ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem) {
        int pageNum = manManufactureOutsourceSettleItem.getPageNum();
        manManufactureOutsourceSettleItem.setPageNum(null);
        List<ManManufactureOutsourceSettleItem> total = manManufactureOutsourceSettleItemService.selectManManufactureOutsourceSettleItemForm(manManufactureOutsourceSettleItem);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOutsourceSettleItem.setPageNum(pageNum);
            List<ManManufactureOutsourceSettleItem> list = manManufactureOutsourceSettleItemService.selectManManufactureOutsourceSettleItemForm(manManufactureOutsourceSettleItem);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, total.size());
    }

    /**
     * 导出外发加工费结算单明细报表列表
     */
    @Log(title = "外发加工费结算单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工费结算单明细报表列表", notes = "导出外发加工费结算单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem) throws IOException {
        List<ManManufactureOutsourceSettleItem> list = manManufactureOutsourceSettleItemService.selectManManufactureOutsourceSettleItemList(manManufactureOutsourceSettleItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOutsourceSettleItem> util = new ExcelUtil<>(ManManufactureOutsourceSettleItem.class, dataMap);
        util.exportExcel(response, list, "外发加工费结算单明细报表");
    }


    /**
     * 获取外发加工费结算单明细报表详细信息
     */
    @ApiOperation(value = "获取外发加工费结算单明细报表详细信息", notes = "获取外发加工费结算单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOutsourceSettleItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureOutsourceSettleItemSid) {
        if (manufactureOutsourceSettleItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOutsourceSettleItemService.selectManManufactureOutsourceSettleItemById(manufactureOutsourceSettleItemSid));
    }

    /**
     * 查询商品外加工费统计报表
     */
    @PostMapping("/statistics")
    @ApiOperation(value = "查询商品外加工费统计报表", notes = "查询商品外加工费统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManOutsourceSettleStatistics.class))
    public TableDataInfo statistics(@RequestBody ManOutsourceSettleStatistics manOutsourceSettleStatistics) {
        startPage(manOutsourceSettleStatistics);
        List<ManOutsourceSettleStatistics> list = manManufactureOutsourceSettleItemService.selectManManufactureOutsourceSettleStatistics(manOutsourceSettleStatistics);
        return getDataTable(list);
    }

    /**
     * 导出商品外加工费统计报表
     */
    @ApiOperation(value = "导出商品外加工费统计报表", notes = "导出商品外加工费统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/statistics/export")
    public void export(HttpServletResponse response, ManOutsourceSettleStatistics manOutsourceSettleStatistics) throws IOException {
        List<ManOutsourceSettleStatistics> list = manManufactureOutsourceSettleItemService.selectManManufactureOutsourceSettleStatistics(manOutsourceSettleStatistics);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManOutsourceSettleStatistics> util = new ExcelUtil<>(ManOutsourceSettleStatistics.class, dataMap);
        util.exportExcel(response, list, "商品外加工费统计报表");
    }
}
