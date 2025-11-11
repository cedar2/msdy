package com.platform.ems.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManDayManufactureProgressItem;
import com.platform.ems.domain.ManManufactureOrder;
import com.platform.ems.mapper.ManDayManufactureProgressItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.service.IManManufactureOrderProcessService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 生产订单-工序Controller
 *
 * @author qhq
 * @date 2021-04-13
 */
@RestController
@RequestMapping("/manManufactureOrderProcess")
@Api(tags = "生产订单-工序")
public class ManManufactureOrderProcessController extends BaseController {

    @Autowired
    private IManManufactureOrderProcessService manManufactureOrderProcessService;
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产订单-工序列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产订单-工序列表", notes = "查询生产订单-工序列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public TableDataInfo list(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        startPage(manManufactureOrderProcess);
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.selectManManufactureOrderProcessList(manManufactureOrderProcess);
        return getDataTable(list);
    }

    /**
     * 导出生产订单-工序列表
     */
    @Log(title = "生产订单-工序", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产订单-工序列表", notes = "导出生产订单-工序列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureOrderProcess manManufactureOrderProcess) throws IOException {
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.selectManManufactureOrderProcessList(manManufactureOrderProcess);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderProcess> util = new ExcelUtil<ManManufactureOrderProcess>(ManManufactureOrderProcess.class, dataMap);
        util.exportExcel(response, list, "生产订单-工序");
    }

    /**
     * 获取生产订单-工序详细信息
     */
    @ApiOperation(value = "获取生产订单-工序详细信息", notes = "获取生产订单-工序详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureOrderProcessSid) {
        if (manufactureOrderProcessSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderProcessService.selectManManufactureOrderProcessById(manufactureOrderProcessSid));
    }

    /**
     * 新增生产订单-工序
     */
    @ApiOperation(value = "新增生产订单-工序", notes = "新增生产订单-工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单-工序", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManManufactureOrderProcess manManufactureOrderProcess) {
        return toAjax(manManufactureOrderProcessService.insertManManufactureOrderProcess(manManufactureOrderProcess));
    }

    /**
     * 修改生产订单-工序
     */
    @ApiOperation(value = "修改生产订单-工序", notes = "修改生产订单-工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单-工序", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManManufactureOrderProcess manManufactureOrderProcess) {
        return toAjax(manManufactureOrderProcessService.updateManManufactureOrderProcess(manManufactureOrderProcess));
    }

    /**
     * 删除生产订单-工序
     */
    @ApiOperation(value = "删除生产订单-工序", notes = "删除生产订单-工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单-工序", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> manufactureOrderProcessSids) {
        if (ArrayUtil.isEmpty(manufactureOrderProcessSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manManufactureOrderProcessService.deleteManManufactureOrderProcessByIds(manufactureOrderProcessSids));
    }

    @ApiOperation(value = "设置完工量校验参考工序", notes = "设置完工量校验参考工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单-工序", businessType = BusinessType.UPDATE)
    @PostMapping("/setReferProcess")
    public AjaxResult setReferProcess(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess){
        return AjaxResult.success(manManufactureOrderProcessService.setReferProcess(manManufactureOrderProcess));
    }

    @ApiOperation(value = "设置计划开始日期", notes = "设置计划开始日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/planStart")
    public AjaxResult setPlanStart(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        return AjaxResult.success(manManufactureOrderProcessService.setPlanStart(manManufactureOrderProcess));
    }

    @ApiOperation(value = "设置计划完成日期", notes = "设置计划完成日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/planEnd")
    public AjaxResult setPlanEnd(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        return AjaxResult.success(manManufactureOrderProcessService.setPlanEnd(manManufactureOrderProcess));
    }

    @ApiOperation(value = "设置即将到期提醒天数", notes = "设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/toexpireDays")
    public AjaxResult setToexpireDays(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        return toAjax(manManufactureOrderProcessService.setToexpireDays(manManufactureOrderProcess));
    }

    @ApiOperation(value = "班组生成日报添加明细点添加同时获取实裁量和已完成量(工序)", notes = "班组生成日报添加明细点添加同时获取实裁量和已完成量(工序)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getQuantity")
    public AjaxResult getQuantity(@RequestBody ManManufactureOrder manManufactureOrder) {
        List<ManManufactureOrderProcess> list = manManufactureOrder.getManManufactureOrderProcessList();
        ManManufactureOrderProcess process = new ManManufactureOrderProcess();
        if (CollectionUtil.isNotEmpty(list)) {
            // 暂存勾选的明细
            Map<String, ManManufactureOrderProcess> map = new HashMap<>();
            map = list.stream().collect(Collectors.toMap(o -> String.valueOf(o.getManufactureOrderProcessSid()) + "-" +
                    String.valueOf(o.getSku1Sid()), Function.identity(), (t1, t2) -> t1));
            Long[] sids = new Long[list.size()];
            List<Long> sku1SidList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                sids[i] = list.get(i).getManufactureOrderProcessSid();
                if (list.get(i).getSku1Sid() != null) {
                    sku1SidList.add(list.get(i).getSku1Sid());
                }
            }
            if (sku1SidList.size() > 0) {
                process.setSku1SidList(sku1SidList.toArray(new Long[sku1SidList.size()]));
            }
            process.setManufactureOrderProcessSidList(sids);
            process.setWorkCenterSid(manManufactureOrder.getWorkCenterSid()).setProgressDimension(manManufactureOrder.getProgressDimension());
            process.setPlantSid(list.get(0).getPlantSid());
            list = manManufactureOrderProcessService.getItemList(process);
            // 找出来 的 只保留 勾选的
            Iterator<ManManufactureOrderProcess> iterator = list.iterator();
            while (iterator.hasNext()) {
                ManManufactureOrderProcess o = iterator.next();
                if (!map.containsKey(String.valueOf(o.getManufactureOrderProcessSid()) + "-" +
                        String.valueOf(o.getSku1Sid()))) {
                    iterator.remove();
                }
            }
            List<ManDayManufactureProgressItem> fenpei = manDayManufactureProgressItemMapper.getQuantityFenpei(new ManDayManufactureProgressItem().setManufactureOrderProcessSidList(sids));
            if (CollectionUtil.isNotEmpty(fenpei)) {
                Map<String, ManDayManufactureProgressItem> map2 = new HashMap<>();
                map2 = fenpei.stream().collect(Collectors.toMap(o -> String.valueOf(o.getManufactureOrderProcessSid()) + "-" +
                        String.valueOf(o.getSku1Sid()) + "-" +
                        String.valueOf(o.getWorkCenterSid()), Function.identity(), (t1, t2) -> t1));
                for (int i = 0; i < list.size(); i++) {
                    BigDecimal fenpeiQuantity = null;
                    if (map2.containsKey(String.valueOf(list.get(i).getManufactureOrderProcessSid()) + "-" +
                            String.valueOf(list.get(i).getSku1Sid()) + "-" +
                            String.valueOf(manManufactureOrder.getWorkCenterSid()))) {
                        ManDayManufactureProgressItem temp = map2.get(String.valueOf(list.get(i).getManufactureOrderProcessSid()) + "-" +
                                String.valueOf(list.get(i).getSku1Sid()) + "-" +
                                String.valueOf(manManufactureOrder.getWorkCenterSid()));
                        fenpeiQuantity = temp.getQuantityFenpei();
                        list.get(i).setQuantityFenpei(fenpeiQuantity);
                    }
                    else {
                        // 新优化
                        /*
                         * 未获取到数据，根据如下逻辑获取：
                           2.1 通过“班组生产日报明细中的生产订单号、明细行中工序所属的“操作部门”，从“生产订单-工序表”中获取到该“生产订单、操作部门”下的所有工序明细行sid（manufacture_order_process_sid）
                           2.2 对2.1获取到的所有工序明细行sid，按“生产订单工序sid  + 班组生产日报明细sku1颜色sid  + 班组生产日报明细表的班组”维度，获取处理状态是“已确认”的周计划的“分配量”的值
                           2.3 若2.2 获取到多个“分配量”的值，则获取”周计划日期“最大的”分配量“的值
                           2.4 若2.3 获取到多个“分配量”的值，则随机取值其中1个
                         */
                        if (fenpeiQuantity == null) {
                            List<ManManufactureOrderProcess> processList = manManufactureOrderProcessService.selectManManufactureOrderProcessList(
                                    new ManManufactureOrderProcess().setManufactureOrderSid(list.get(i).getManufactureOrderSid())
                                            .setDepartmentSid(list.get(i).getDepartmentSid())
                            );
                            if (CollectionUtil.isNotEmpty(processList)) {
                                Long[] manOrderProSids = processList.stream().map(ManManufactureOrderProcess::getManufactureOrderProcessSid).toArray(Long[]::new);
                                List<ManDayManufactureProgressItem> newFenpei = manDayManufactureProgressItemMapper.getQuantityFenpei(new ManDayManufactureProgressItem()
                                        .setManufactureOrderProcessSidList(manOrderProSids)
                                        .setWorkCenterSid(manManufactureOrder.getWorkCenterSid())
                                        .setSku1Sid(list.get(i).getSku1Sid()).setSku1SidIsNull(ConstantsEms.YES));
                                if (CollectionUtil.isNotEmpty(newFenpei)) {
                                    newFenpei = newFenpei.stream().sorted(Comparator.comparing(ManDayManufactureProgressItem::getDateStart).reversed()).collect(Collectors.toList());
                                    fenpeiQuantity = newFenpei.get(0).getQuantityFenpei();
                                    list.get(i).setQuantityFenpei(fenpeiQuantity);
                                }
                            }
                        }
                    }
                    if (list.get(i).getTotalCompleteQuantity() == null) {
                        list.get(i).setShicaiUnfinishedQuantity(list.get(i).getQuantityFenpei());
                    }
                    else {
                        if (list.get(i).getQuantityFenpei() == null) {
                            list.get(i).setShicaiUnfinishedQuantity(BigDecimal.ZERO.subtract(list.get(i).getTotalCompleteQuantity()));
                        }
                        else {
                            list.get(i).setShicaiUnfinishedQuantity(list.get(i).getQuantityFenpei().subtract(list.get(i).getTotalCompleteQuantity()));
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < list.size(); i++) {
                    BigDecimal fenpeiQuantity = null;
                    // 新优化
                        /*
                         * 未获取到数据，根据如下逻辑获取：
                           2.1 通过“班组生产日报明细中的生产订单号、明细行中工序所属的“操作部门”，从“生产订单-工序表”中获取到该“生产订单、操作部门”下的所有工序明细行sid（manufacture_order_process_sid）
                           2.2 对2.1获取到的所有工序明细行sid，按“生产订单工序sid  + 班组生产日报明细sku1颜色sid  + 班组生产日报明细表的班组”维度，获取处理状态是“已确认”的周计划的“分配量”的值
                           2.3 若2.2 获取到多个“分配量”的值，则获取”周计划日期“最大的”分配量“的值
                           2.4 若2.3 获取到多个“分配量”的值，则随机取值其中1个
                         */
                    List<ManManufactureOrderProcess> processList = manManufactureOrderProcessService.selectManManufactureOrderProcessList(
                            new ManManufactureOrderProcess().setManufactureOrderSid(list.get(i).getManufactureOrderSid())
                                    .setDepartmentSid(list.get(i).getDepartmentSid())
                    );
                    if (CollectionUtil.isNotEmpty(processList)) {
                        Long[] manOrderProSids = processList.stream().map(ManManufactureOrderProcess::getManufactureOrderProcessSid).toArray(Long[]::new);
                        List<ManDayManufactureProgressItem> newFenpei = manDayManufactureProgressItemMapper.getQuantityFenpei(new ManDayManufactureProgressItem()
                                .setManufactureOrderProcessSidList(manOrderProSids)
                                .setWorkCenterSid(manManufactureOrder.getWorkCenterSid())
                                .setSku1Sid(list.get(i).getSku1Sid()).setSku1SidIsNull(ConstantsEms.YES));
                        if (CollectionUtil.isNotEmpty(newFenpei)) {
                            newFenpei = newFenpei.stream().sorted(Comparator.comparing(ManDayManufactureProgressItem::getDateStart).reversed()).collect(Collectors.toList());
                            fenpeiQuantity = newFenpei.get(0).getQuantityFenpei();
                            list.get(i).setQuantityFenpei(fenpeiQuantity);
                        }
                    }
                    if (list.get(i).getTotalCompleteQuantity() == null) {
                        list.get(i).setShicaiUnfinishedQuantity(list.get(i).getQuantityFenpei());
                    }
                    else {
                        if (list.get(i).getQuantityFenpei() == null) {
                            list.get(i).setShicaiUnfinishedQuantity(BigDecimal.ZERO.subtract(list.get(i).getTotalCompleteQuantity()));
                        }
                        else {
                            list.get(i).setShicaiUnfinishedQuantity(list.get(i).getQuantityFenpei().subtract(list.get(i).getTotalCompleteQuantity()));
                        }
                    }
                }
            }
        }
        return AjaxResult.success(list);
    }

}
