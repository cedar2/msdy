package com.platform.ems.controller;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.dto.response.export.RepBusinessRemindJJDQSo;
import com.platform.ems.mapper.RepBusinessRemindSoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepBusinessRemindSo;
import com.platform.ems.service.IRepBusinessRemindSoService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

import static java.util.stream.Collectors.toList;

/**
 * 已逾期/即将到期-销售订单Controller
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@RestController
@RequestMapping("/rep/business/remind/so")
@Api(tags = "已逾期/即将到期-销售订单")
public class RepBusinessRemindSoController extends BaseController {

    @Autowired
    private IRepBusinessRemindSoService repBusinessRemindSoService;
    @Autowired
    private RepBusinessRemindSoMapper repBusinessRemindSoMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询已逾期/即将到期-销售订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询已逾期/即将到期-销售订单列表", notes = "查询已逾期/即将到期-销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindSo.class))
    public TableDataInfo list(@RequestBody RepBusinessRemindSo repBusinessRemindSo) {
        startPage(repBusinessRemindSo);
        List<RepBusinessRemindSo> list = repBusinessRemindSoService.selectRepBusinessRemindSoList(repBusinessRemindSo);
        return getDataTable(list);
    }

    @GetMapping("get/report")
    @ApiOperation(value = "查询已逾期/即将到期-销售订单看板", notes = "查询已逾期/即将到期-销售订单看板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindSo.class))
    public AjaxResult get() {
        return AjaxResult.success(repBusinessRemindSoService.getReport());
    }

    @PostMapping("/report/head")
    @ApiOperation(value = "查询已逾期/即将到期报表", notes = "查询已逾期/即将到期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindSo.class))
    public TableDataInfo yyqReport(@RequestBody RepBusinessRemindSo repBusinessRemindSo) {
        TableDataInfo rspData = new TableDataInfo();
        Integer pageNum = repBusinessRemindSo.getPageNum();
        repBusinessRemindSo.setPageNum(null);
        repBusinessRemindSo.setClientId(ApiThreadLocalUtil.get().getClientId());
        int total = repBusinessRemindSoMapper.getYyqCount(repBusinessRemindSo);
        if (total > 0) {
            repBusinessRemindSo.setPageNum(pageNum);
            List<RepBusinessRemindSo> list = repBusinessRemindSoService.yyqReport(repBusinessRemindSo);
            rspData.setRows(list);
        }
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal((long) total);
        return rspData;
    }
    @PostMapping("/report/item")
    @ApiOperation(value = "查询已逾期/即将到期报表明细", notes = "查询已逾期/即将到期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindSo.class))
    public TableDataInfo reportItem(@RequestBody RepBusinessRemindSo repBusinessRemindSo) {
        startPage(repBusinessRemindSo);
        List<RepBusinessRemindSo> list = repBusinessRemindSoService.reportItem(repBusinessRemindSo);
        TableDataInfo dataTable = getDataTable(list);
        if(CollectionUtil.isNotEmpty(list)){
            List<RepBusinessRemindSo> collect = list.stream().sorted(Comparator.comparing(RepBusinessRemindSo::getSalesOrderCode)
                    .thenComparing(RepBusinessRemindSo::getCustomerCode)
            ).collect(Collectors.toList());
            list= repBusinessRemindSoService.sort(collect);
            dataTable.setRows(list);
        }
        return dataTable;
    }
    /**
     * 导出已逾期/即将到期-销售订单列表
     */
    @Log(title = "已逾期/即将到期-销售订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出已逾期/即将到期-销售订单列表", notes = "导出已逾期/即将到期-销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepBusinessRemindSo repBusinessRemindSo) throws IOException {
        List<RepBusinessRemindSo> list =  repBusinessRemindSoService.reportItem(repBusinessRemindSo);
        if(CollectionUtil.isNotEmpty(list)){
            list = list.stream().sorted(
                    Comparator.comparing(RepBusinessRemindSo::getContractDate, Comparator.reverseOrder())
                            .thenComparing(RepBusinessRemindSo::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindSo::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindSo::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindSo::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindSo::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        if(ConstantsEms.YYQ.equals(repBusinessRemindSo.getRemindType())){
            ExcelUtil<RepBusinessRemindSo> util = new ExcelUtil<>(RepBusinessRemindSo.class, dataMap);
            util.exportExcel(response, list, "已逾期销售明细报表");
        }else{
            ExcelUtil<RepBusinessRemindJJDQSo> util = new ExcelUtil<>(RepBusinessRemindJJDQSo.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list,RepBusinessRemindJJDQSo::new), "即将到期销售明细报表");
        }
    }


    /**
     * 获取已逾期/即将到期-销售订单详细信息
     */
    @ApiOperation(value = "获取已逾期/即将到期-销售订单详细信息", notes = "获取已逾期/即将到期-销售订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindSo.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repBusinessRemindSoService.selectRepBusinessRemindSoById(dataRecordSid));
    }

    /**
     * 新增已逾期/即将到期-销售订单
     */
    @ApiOperation(value = "新增已逾期/即将到期-销售订单", notes = "新增已逾期/即将到期-销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "已逾期/即将到期-销售订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepBusinessRemindSo repBusinessRemindSo) {
        return toAjax(repBusinessRemindSoService.insertRepBusinessRemindSo(repBusinessRemindSo));
    }

    /**
     * 删除已逾期/即将到期-销售订单
     */
    @ApiOperation(value = "删除已逾期/即将到期-销售订单", notes = "删除已逾期/即将到期-销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "已逾期/即将到期-销售订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repBusinessRemindSoService.deleteRepBusinessRemindSoByIds(dataRecordSids));
    }

}
