package com.platform.ems.controller;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManManufactureOrderProduct;
import com.platform.ems.domain.RepBusinessRemindPo;
import com.platform.ems.service.IManManufactureOrderProductService;
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

import javax.validation.Valid;
import javax.xml.crypto.Data;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepBusinessRemindMo;
import com.platform.ems.service.IRepBusinessRemindMoService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

import static java.util.stream.Collectors.toList;

/**
 * 已逾期/即将到期-生产订单Controller
 *
 * @author chenkw
 * @date 2022-04-26
 */
@RestController
@RequestMapping("/rep/business/remind/mo")
@Api(tags = "已逾期/即将到期-生产订单")
public class RepBusinessRemindMoController extends BaseController {

    @Autowired
    private IRepBusinessRemindMoService repBusinessRemindMoService;
    @Autowired
    private IManManufactureOrderProductService manManufactureOrderProductService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询已逾期/即将到期-生产订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询已逾期/即将到期-生产订单列表", notes = "查询已逾期/即将到期-生产订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindMo.class))
    public TableDataInfo list(@RequestBody RepBusinessRemindMo repBusinessRemindMo) {
        ManManufactureOrderProduct product = new ManManufactureOrderProduct();
        BeanCopyUtils.copyProperties(repBusinessRemindMo, product);
        startPage(product);
        List<ManManufactureOrderProduct> list = manManufactureOrderProductService.selectManManufactureOrderProductList(product);
        TableDataInfo rsp = getDataTable(list);
        if (CollectionUtils.isNotEmpty(list)) {
            list = list.stream().sorted(
                    Comparator.comparing(ManManufactureOrderProduct::getContractDate, Comparator.nullsLast(String::compareTo))
                            .thenComparing(ManManufactureOrderProduct::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(ManManufactureOrderProduct::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(ManManufactureOrderProduct::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(ManManufactureOrderProduct::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(ManManufactureOrderProduct::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            rsp.setRows(list);
        }
        return rsp;
    }

    /**
     * 导出已逾期/即将到期-生产订单列表
     */
    @Log(title = "已逾期/即将到期-生产订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出已逾期/即将到期-生产订单列表", notes = "导出已逾期/即将到期-生产订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepBusinessRemindMo repBusinessRemindMo) throws IOException {
        String sheetName = "";
        if (ConstantsEms.YYQ.equals(repBusinessRemindMo.getRemindType())){
            sheetName = "已逾期生产订单报表";
        }else if (ConstantsEms.JJDQ.equals(repBusinessRemindMo.getRemindType())){
            sheetName = "即将到期生产订单报表";
        }
        List<RepBusinessRemindMo> list = repBusinessRemindMoService.selectRepBusinessRemindMoList(repBusinessRemindMo);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepBusinessRemindMo> util = new ExcelUtil<>(RepBusinessRemindMo.class, dataMap);
        util.exportExcel(response, list, sheetName);
    }


    /**
     * 获取已逾期/即将到期-生产订单详细信息
     */
    @ApiOperation(value = "获取已逾期/即将到期-生产订单详细信息", notes = "获取已逾期/即将到期-生产订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindMo.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repBusinessRemindMoService.selectRepBusinessRemindMoById(dataRecordSid));
    }

    /**
     * 新增已逾期/即将到期-生产订单
     */
    @ApiOperation(value = "新增已逾期/即将到期-生产订单", notes = "新增已逾期/即将到期-生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "已逾期/即将到期-生产订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepBusinessRemindMo repBusinessRemindMo) {
        return toAjax(repBusinessRemindMoService.insertRepBusinessRemindMo(repBusinessRemindMo));
    }

    /**
     * 删除已逾期/即将到期-生产订单
     */
    @ApiOperation(value = "删除已逾期/即将到期-生产订单", notes = "删除已逾期/即将到期-生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:mo:remove")
    @Log(title = "已逾期/即将到期-生产订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repBusinessRemindMoService.deleteRepBusinessRemindMoByIds(dataRecordSids));
    }

    /**
     * 查询已逾期/即将到期生产订单列表
     */
    @PostMapping("/count")
    @ApiOperation(value = "查询已逾期/即将到期生产订单（计划完成日期+商品编码）分组统计报表", notes = "查询已逾期/即将到期生产订单（计划完成日期+商品编码）分组统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindMo.class))
    public TableDataInfo countOver(@RequestBody RepBusinessRemindMo repBusinessRemindMo) {
        startPage(repBusinessRemindMo);
        List<RepBusinessRemindMo> list = repBusinessRemindMoService.getCountForm(repBusinessRemindMo);
        return getDataTable(list);
    }

}
