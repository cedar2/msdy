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
import com.platform.ems.domain.RepBusinessRemindSo;
import com.platform.ems.domain.dto.response.RepBusinessRemindPoJJDQResponse;
import com.platform.ems.domain.dto.response.RepBusinessRemindPoYYQResponse;
import com.platform.ems.mapper.RepBusinessRemindPoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepBusinessRemindPo;
import com.platform.ems.service.IRepBusinessRemindPoService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

import static java.util.stream.Collectors.toList;

/**
 * 已逾期/即将到期-采购订单Controller
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@RestController
@RequestMapping("/rep/business/remind/po")
@Api(tags = "已逾期/即将到期-采购订单")
public class RepBusinessRemindPoController extends BaseController {

    @Autowired
    private IRepBusinessRemindPoService repBusinessRemindPoService;
    @Autowired
    private RepBusinessRemindPoMapper repBusinessRemindPoMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询已逾期/即将到期-采购订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询已逾期/即将到期-采购订单列表", notes = "查询已逾期/即将到期-采购订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindPo.class))
    public TableDataInfo list(@RequestBody RepBusinessRemindPo repBusinessRemindPo) {
        startPage(repBusinessRemindPo);
        List<RepBusinessRemindPo> list = repBusinessRemindPoService.selectRepBusinessRemindPoList(repBusinessRemindPo);
        return getDataTable(list);
    }
    @PostMapping("/report/head")
    @ApiOperation(value = "查询已逾期/即将到期-报表主", notes = "查询已逾期/即将到期-报表主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindPo.class))
    public TableDataInfo reportHead(@RequestBody RepBusinessRemindPo repBusinessRemindPo) {
        TableDataInfo rspData = new TableDataInfo();
        Integer pageNum = repBusinessRemindPo.getPageNum();
        repBusinessRemindPo.setPageNum(null);
        repBusinessRemindPo.setClientId(ApiThreadLocalUtil.get().getClientId());
        int total = repBusinessRemindPoMapper.getYyqCount(repBusinessRemindPo);
        if (total > 0) {
            repBusinessRemindPo.setPageNum(pageNum);
            List<RepBusinessRemindPo> list = repBusinessRemindPoService.getYYQHead(repBusinessRemindPo);
            rspData.setRows(list);
        }
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal((long) total);
        return rspData;
    }

    @PostMapping("/report/item")
    @ApiOperation(value = "查询已逾期/即将到期-报表明细", notes = "查询已逾期/即将到期-报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindPo.class))
    public TableDataInfo reportItem(@RequestBody RepBusinessRemindPo repBusinessRemindPo) {
        startPage(repBusinessRemindPo);
        List<RepBusinessRemindPo> list = repBusinessRemindPoService.getYYQItem(repBusinessRemindPo);
        TableDataInfo dataTable = getDataTable(list);
        if(CollectionUtil.isNotEmpty(list)){
            List<RepBusinessRemindPo> collect = list.stream().sorted(Comparator.comparing(RepBusinessRemindPo::getPurchaseOrderCode)
                    .thenComparing(RepBusinessRemindPo::getVendorCode)
            ).collect(Collectors.toList());
            list= repBusinessRemindPoService.sort(collect);
            dataTable.setRows(list);
        }
        return dataTable;
    }

    @GetMapping("get/report")
    @ApiOperation(value = "查询已逾期/即将到期-采购订单看板", notes = "查询已逾期/即将到期-采购订单看板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindSo.class))
    public AjaxResult get() {
        return AjaxResult.success(repBusinessRemindPoService.getReport());
    }

    /**
     * 导出已逾期/即将到期-采购订单列表
     */
    @Log(title = "已逾期/即将到期-采购订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出已逾期/即将到期-采购订单列表", notes = "导出已逾期/即将到期-采购订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepBusinessRemindPo repBusinessRemindPo) throws IOException {
        List<RepBusinessRemindPo> list = repBusinessRemindPoService.getYYQItem(repBusinessRemindPo);
        if(CollectionUtil.isNotEmpty(list)){
            list = list.stream().sorted(
                    Comparator.comparing(RepBusinessRemindPo::getContractDate, Comparator.reverseOrder())
                            .thenComparing(RepBusinessRemindPo::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindPo::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindPo::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindPo::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindPo::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        if(ConstantsEms.YYQ.equals(repBusinessRemindPo.getRemindType())){
            ExcelUtil<RepBusinessRemindPoYYQResponse> util = new ExcelUtil<>(RepBusinessRemindPoYYQResponse.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, RepBusinessRemindPoYYQResponse::new) , "已逾期采购明细报表");
        }else{
            ExcelUtil<RepBusinessRemindPoJJDQResponse> util = new ExcelUtil<>(RepBusinessRemindPoJJDQResponse.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, RepBusinessRemindPoJJDQResponse::new) , "即将到期采购明细报表");
        }
    }


    /**
     * 获取已逾期/即将到期-采购订单详细信息
     */
    @ApiOperation(value = "获取已逾期/即将到期-采购订单详细信息", notes = "获取已逾期/即将到期-采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemindPo.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repBusinessRemindPoService.selectRepBusinessRemindPoById(dataRecordSid));
    }

    /**
     * 新增已逾期/即将到期-采购订单
     */
    @ApiOperation(value = "新增已逾期/即将到期-采购订单", notes = "新增已逾期/即将到期-采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "已逾期/即将到期-采购订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepBusinessRemindPo repBusinessRemindPo) {
        return toAjax(repBusinessRemindPoService.insertRepBusinessRemindPo(repBusinessRemindPo));
    }

    /**
     * 删除已逾期/即将到期-采购订单
     */
    @ApiOperation(value = "删除已逾期/即将到期-采购订单", notes = "删除已逾期/即将到期-采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "已逾期/即将到期-采购订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repBusinessRemindPoService.deleteRepBusinessRemindPoByIds(dataRecordSids));
    }

}
