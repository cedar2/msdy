package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.annotation.Idempotent;
import com.platform.common.annotation.Log;
import com.platform.common.constant.HttpStatus;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsProcess;
import com.platform.ems.domain.ManManufactureOrder;
import com.platform.ems.domain.ManManufactureOrderConcernTask;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.dto.ManWorkOrderProgressFormData;
import com.platform.ems.domain.dto.request.ManManufactureOrderConcernTaskSetRequest;
import com.platform.ems.domain.dto.request.ManManufactureOrderProcessSetRequest;
import com.platform.ems.domain.dto.request.ManManufactureOrderSetRequest;
import com.platform.ems.domain.dto.response.form.*;
import com.platform.ems.mapper.ManManufactureOrderMapper;
import com.platform.ems.mapper.ManManufactureOrderProcessMapper;
import com.platform.ems.service.*;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产订单Controller
 *
 * @author qhq
 * @date 2021-04-10
 */
@RestController
@RequestMapping("/manManufactureOrder")
@Api(tags = "生产订单")
public class ManManufactureOrderController extends BaseController {

    @Autowired
    private IManManufactureOrderService manManufactureOrderService;
    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private IManManufactureOrderProductService manManufactureOrderProductService;
    @Autowired
    private IManManufactureOrderProcessService manManufactureOrderProcessService;
    @Autowired
    private IManManufactureOrderConcernTaskService manManufactureOrderConcernTaskService;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;


    /**
     * 查询生产订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产订单列表", notes = "查询生产订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public TableDataInfo list(@RequestBody ManManufactureOrder manManufactureOrder) {
        startPage(manManufactureOrder);
        List<ManManufactureOrder> list = manManufactureOrderService.selectManManufactureOrderList(manManufactureOrder);
        return getDataTable(list);
    }

    /**
     * 导出生产订单列表
     */
    @Log(title = "生产订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产订单列表", notes = "导出生产订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureOrder manManufactureOrder) throws IOException {
        List<ManManufactureOrder> list = manManufactureOrderService.selectManManufactureOrderList(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrder> util = new ExcelUtil<>(ManManufactureOrder.class, dataMap);
        util.exportExcel(response, list, "生产订单");
    }

    /**
     * 获取生产订单详细信息
     */
    @ApiOperation(value = "获取生产订单详细信息", notes = "获取生产订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureOrderSid) {
        if (manufactureOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderService.selectManManufactureOrderById(manufactureOrderSid));
    }

    /**
     * 标签信息
     */
    @ApiOperation(value = "标签信息", notes = "标签信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "获取标签信息", businessType = BusinessType.QUERY)
    @PostMapping("/label")
    public AjaxResult getLabelInfo(Long manufactureOrderSid) {
        return AjaxResult.success(manManufactureOrderService.getLabelInfo(manufactureOrderSid));
    }

    /**
     * 复制生产订单详细信息
     */
    @ApiOperation(value = "复制生产订单详细信息", notes = "复制生产订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long manufactureOrderSid) {
        if (manufactureOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderService.copyManManufactureOrderById(manufactureOrderSid));
    }

    /**
     * 新增生产订单
     */
    @ApiOperation(value = "新增生产订单", notes = "新增生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManManufactureOrder manManufactureOrder) {
        int row = manManufactureOrderService.insertManManufactureOrder(manManufactureOrder);
        if (row > 0) {
            return AjaxResult.success("操作成功", new ManManufactureOrder()
                    .setManufactureOrderSid(manManufactureOrder.getManufactureOrderSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改生产订单
     */
    @ApiOperation(value = "修改生产订单", notes = "修改生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManManufactureOrder manManufactureOrder) {
        return toAjax(manManufactureOrderService.updateManManufactureOrder(manManufactureOrder));
    }

    /**
     * 变更生产订单
     */
    @ApiOperation(value = "变更生产订单", notes = "变更生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManManufactureOrder manManufactureOrder) {
        return toAjax(manManufactureOrderService.changeManManufactureOrder(manManufactureOrder));
    }

    /**
     * 删除生产订单
     */
    @ApiOperation(value = "删除生产订单", notes = "删除生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> manufactureOrderSids) {
        if (CollectionUtils.isEmpty(manufactureOrderSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manManufactureOrderService.deleteManManufactureOrderByIds(manufactureOrderSids));
    }

    /**
     * 确认前校验-生产订单
     */
    @ApiOperation(value = "确认前校验-生产订单", notes = "确认前校验-生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check/verify")
    public AjaxResult verifyCheck(@RequestBody ManManufactureOrder manManufactureOrder) {
        if (manManufactureOrder.getManufactureOrderSidList() == null ||
                manManufactureOrder.getManufactureOrderSidList().length == 0 || StrUtil.isEmpty(manManufactureOrder.getHandleStatus())) {
            throw new BaseException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderService.verifyCheck(manManufactureOrder));
    }

    /**
     * 确认前校验-生产订单
     */
    @ApiOperation(value = "确认前校验-生产订单", notes = "确认前校验-生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check/verify/form")
    public AjaxResult verifyCheckForm(@RequestBody ManManufactureOrder manManufactureOrder) {
        return AjaxResult.success(manManufactureOrderService.verifyCheckForm(manManufactureOrder));
    }

    /**
     * 提交前校验-生产订单
     */
    @ApiOperation(value = "提交前校验-生产订单", notes = "提交前校验-生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long manufactureOrderSid, String handleStatus) {
        if (manufactureOrderSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderService.verify(manufactureOrderSid, handleStatus));
    }

    /**
     * 工序计划产量校验
     */
    @ApiOperation(value = "工序计划产量校验", notes = "工序计划产量校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/quantity/verify")
    public AjaxResult processQuantityVerify(@RequestBody ManManufactureOrder manManufactureOrder) {
        return AjaxResult.success(manManufactureOrderService.processQuantityVerify(manManufactureOrder));
    }

    /**
     * 批量确认
     */
    @ApiOperation(value = "批量确认", notes = "批量确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult editHandleStatus(@RequestBody ManManufactureOrder manManufactureOrder) {
        return toAjax(manManufactureOrderService.handleStatus(manManufactureOrder));
    }

    /**
     * 作废-生产订单
     */
    @ApiOperation(value = "作废生产订单", notes = "作废生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(Long manufactureOrderSid) {
        if (manufactureOrderSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manManufactureOrderService.cancellationManufactureOrderById(manufactureOrderSid));
    }

    /**
     * 一、报表中心-销售管理-销售订单排产进度报表
     * 2、勾选数据，点击“排生产订单”按钮，跳转至“生产订单信息”弹窗，在弹窗填写必填信息，点击“下一步”按钮后，新增如下校验
     *   根据“系统默认设置(租户级)”中字段“生产订单新建是否默认带最近生产订单信息”的值来判断是否需默认带出工序、事项明细
     * 1）若“生产订单新建是否默认带最近生产订单信息”字段的值为“否”或空，则同当前逻辑一致，即不需要默认带工序、事项明细
     * 2）若“生产订单新建是否默认带最近生产订单信息”字段的值为“是”，则需默认带出工序、事项明细，具体逻辑如下
     *》根据“工厂+商品编码”查找出最近的1笔“已确认”状态的生产订单，若未查找到生产订单数据，则保持当前逻辑；若查找到生产订单数据，
     * 则将查找到的生产订单的工序总览明细、关注事项明细默认带入到新建的生产订单中，需带入值如下：
     * > 工序总览：序号、工序名称、工厂(工序)、特殊工序标识、是否第一个工序、是否标志阶段完成的工序、是否标志成品完成的工序、工序编码、操作部门、里程碑
     * > 关注事项：序号、关注事项名称、所属生产阶段、里程碑、事项类型、事项编码
     */
    @ApiOperation(value = "销售订单排产进度报表-排生产订单-下一步", notes = "销售订单排产进度报表-排生产订单-下一步")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/auto/itemList")
    public AjaxResult completion(@RequestBody ManManufactureOrder manManufactureOrder) {
        manManufactureOrder.setManManufactureOrderProcessList(new ArrayList<>());
        manManufactureOrder.setConcernTaskList(new ArrayList<>());
        if (manManufactureOrder.getMaterialSid() != null && manManufactureOrder.getPlantSid() != null) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && ConstantsEms.YES.equals(settingClient.getIsAssociateLatestManufactureOrder())) {
                List<ManManufactureOrder> orderList = manManufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>().lambda()
                        .eq(ManManufactureOrder::getPlantSid, manManufactureOrder.getPlantSid())
                        .eq(ManManufactureOrder::getMaterialSid, manManufactureOrder.getMaterialSid())
                        .eq(ManManufactureOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                        .orderByDesc(ManManufactureOrder::getCreateDate));
                if (CollectionUtil.isNotEmpty(orderList)) {
                    ManManufactureOrder order = manManufactureOrderService.selectManManufactureOrderById(orderList.get(0).getManufactureOrderSid());
                    if (order != null) {
                        if (CollectionUtil.isNotEmpty(order.getManManufactureOrderProcessList())) {
                            order.getManManufactureOrderProcessList().forEach(item->{
                                item.setWorkCenterSid(null).setWorkCenterCode(null).setWorkCenterName(null)
                                        .setDirectorSid(null).setDirectorName(null).setDirectorCode(null)
                                        .setDirectorSidList(null)
                                        .setPlanStartDate(null).setPlanEndDate(null).setEndStatus(ConstantsEms.END_STATUS_WKS)
                                        .setEndFlag(null).setRemark(null).setComment(null).setActualEndDateSp(null)
                                        .setActualEndDateTg(null).setActualEndDate(null).setActualStartDate(null);
                                item.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getSysUser().getUserName())
                                        .setUpdateDate(null).setUpdaterAccount(null).setManufactureOrderProcessSid(null)
                                        .setManufactureOrderSid(null);
                                item.setQuantity(null).setCurrentCompleteQuantity(null);
                            });
                        }
                        manManufactureOrder.setManManufactureOrderProcessList(order.getManManufactureOrderProcessList());
                        if (CollectionUtil.isNotEmpty(order.getConcernTaskList())) {
                            order.getConcernTaskList().forEach(item->{
                                item.setPlanStartDate(null).setPlanEndDate(null).setActualEndDate(null).setActualStartDate(null)
                                        .setActualQuantity(null).setPlanQuantity(null).setHandleComment(null).setRemark(null);
                                item.setEndStatus(ConstantsEms.END_STATUS_WKS);
                                item.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getSysUser().getUserName())
                                        .setUpdateDate(null).setUpdaterAccount(null).setManufactureOrderConcernTaskSid(null)
                                        .setManufactureOrderSid(null);
                            });
                        }
                        manManufactureOrder.setConcernTaskList(order.getConcernTaskList());
                    }
                }
            }
        }
        return AjaxResult.success(manManufactureOrder);
    }


    /**
     * 完工-生产订单
     */
    @ApiOperation(value = "完工生产订单", notes = "完工生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产订单", businessType = BusinessType.COMPLETED)
    @PostMapping("/completion")
    public AjaxResult completion(Long manufactureOrderSid) {
        if (manufactureOrderSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manManufactureOrderService.completionManufactureOrderById(manufactureOrderSid));
    }

    /**
     * 设置即将到期提醒天数
     */
    @ApiOperation(value = "设置即将到期提醒天数", notes = "设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setToexpireDays")
    public AjaxResult setToexpireDays(@RequestBody ManManufactureOrder manManufactureOrder) {
        return toAjax(manManufactureOrderService.setToexpireDays(manManufactureOrder));
    }

    /**
     * 设置
     * 1、基本信息 2、头缸信息 3、首批信息
     */
    @ApiOperation(value = "设置基本信息/头缸信息/首批信息", notes = "设置基本信息/头缸信息/首批信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setDateStatus")
    public AjaxResult setDateStatus(@RequestBody ManManufactureOrderSetRequest manManufactureOrder) {
        return toAjax(manManufactureOrderService.setDateStatus(manManufactureOrder));
    }

    /**
     * 设置完工状态
     */
    @ApiOperation(value = "设置完工状态", notes = "设置完工状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setComplateStatus")
    public AjaxResult setComplateStatus(@RequestBody ManManufactureOrder manManufactureOrder) {
        return toAjax(manManufactureOrderService.setComplateStatus(manManufactureOrder));
    }

    //******************************************************************************************************//

    /**
     * 生产订单工序明细报表 - 按款
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "生产订单工序明细报表-按款", notes = "生产订单工序明细报表-按款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public TableDataInfo list(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        startPage(manManufactureOrderProcess);
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.getItemList(manManufactureOrderProcess);
        return getDataTable(list);
    }


    /**
     * 生产订单工序明细报表 - 按色
     */
    @PostMapping("/getSkuItemList")
    @ApiOperation(value = "生产订单工序明细报表-按色", notes = "生产订单工序明细报表-按色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public TableDataInfo skuList(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        manManufactureOrderProcess.setProgressDimension(ConstantsEms.PRICE_K1);
        startPage(manManufactureOrderProcess);
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.getItemList(manManufactureOrderProcess);
        return getDataTable(list);
    }

    /**
     * 获取BOM明细
     */
    @ApiOperation(value = "获取BOM明细", notes = "获取BOM明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    @PostMapping("/getBomInfo")
    public AjaxResult getMaterialInfo(@RequestBody ManManufactureOrder manManufactureOrder) {
        return AjaxResult.success(manManufactureOrderService.getMaterialInfo(manManufactureOrder));
    }

    /**
     * 生产订单下拉框列表
     */
    @PostMapping("/getManufactureOrderList")
    @ApiOperation(value = "生产订单下拉框列表", notes = "生产订单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public AjaxResult getManufactureOrderList() {
        return AjaxResult.success(manManufactureOrderService.getManufactureOrderList());
    }

    /**
     * 查询页面的需求测算
     */
    @PostMapping("/measure")
    @ApiOperation(value = "查询页面的需求测算", notes = "查询页面的需求测算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public AjaxResult measure(@RequestBody List<Long> manufactureOrderSidList) {
        if (CollectionUtils.isEmpty(manufactureOrderSidList)) {
            throw new BaseException("请选择行");
        }
        return AjaxResult.success(manManufactureOrderProductService.selectManManufactureOrderProductListByOrderSid(manufactureOrderSidList));
    }

    //******************************************************************************************************//

    /**
     * 导出生产订单工序明细报表
     */
    @Log(title = "生产订单工序明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产订单工序明细报表", notes = "导出生产订单工序明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/process/export")
    public void export(HttpServletResponse response, ManManufactureOrderProcess manManufactureOrderProcess) throws IOException {
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.getItemList(manManufactureOrderProcess);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderProcess> util = new ExcelUtil<>(ManManufactureOrderProcess.class, dataMap);
        util.exportExcel(response, list, "生产订单工序明细报表");
    }

    /*
     * 生产进度报表 ，注意是生产进度报表 不是 生产进度日报
     */
    @PostMapping("/form")
    @ApiOperation(value = "查询生产进度报表", notes = "查询生产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SaleManufactureOrderProcessFormResponse.class))
    public TableDataInfo getReportForm(@RequestBody SaleManufactureOrderProcessFormResponse manDayManufactureProgress) {
        startPage(manDayManufactureProgress);
        List<SaleManufactureOrderProcessFormResponse> list = manManufactureOrderService.getProcessForm(manDayManufactureProgress);
        return getDataTable(list);
    }

    /**
     * 导出生产进度报表
     */
    @ApiOperation(value = "导出生产进度报表", notes = "导出生产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/form/export")
    public void export(HttpServletResponse response, SaleManufactureOrderProcessFormResponse manManufactureOrder) throws IOException {
        List<SaleManufactureOrderProcessFormResponse> list = manManufactureOrderService.getProcessForm(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SaleManufactureOrderProcessFormResponse> util = new ExcelUtil<>(SaleManufactureOrderProcessFormResponse.class, dataMap);
        util.exportExcel(response, list, "生产进度报表");
    }

    /**
     * 销售订单进度报表的生产进度报表明细
     */
    @PostMapping("/form/process")
    @ApiOperation(value = "销售订单进度报表的生产进度报表明细", notes = "销售订单进度报表的生产进度报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SaleManufactureOrderProcessFormResponse.class))
    public TableDataInfo getProcessItem(@RequestBody SaleManufactureOrderProcessFormResponse manManufactureOrder) {
        startPage(manManufactureOrder);
        List<SaleManufactureOrderProcessFormResponse> list = manManufactureOrderService.getProcessItem(manManufactureOrder);
        return getDataTable(list);
    }

    /**
     * 销售订单进度报表的生产进度报表明细
     */
    @PostMapping("/form/process/export")
    @ApiOperation(value = "销售订单进度报表的生产进度报表明细", notes = "销售订单进度报表的生产进度报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SaleManufactureOrderProcessFormResponse.class))
    public void getProcessItemExoprt(HttpServletResponse response,  SaleManufactureOrderProcessFormResponse manManufactureOrder) throws IOException {
        List<SaleManufactureOrderProcessFormResponse> list = manManufactureOrderService.getProcessItem(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SaleManufactureOrderProcessFormResponse> util = new ExcelUtil<>(SaleManufactureOrderProcessFormResponse.class, dataMap);
        util.exportExcel(response, list, "销售进度报表-生产进度");
    }

    /**
     * 生产订单事项明细报表
     */
    @PostMapping("/form/concern")
    @ApiOperation(value = "生产订单事项明细报表", notes = "生产订单事项明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTask.class))
    public TableDataInfo getConcernTask(@RequestBody ManManufactureOrderConcernTask ConcernTask) {
        startPage(ConcernTask);
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskService.selectManManufactureOrderConcernTaskForm(ConcernTask);
        return getDataTable(list);
    }

    /**
     * 生产订单事项明细报表 导出
     */
    @PostMapping("/form/concern/export")
    @ApiOperation(value = "导出生产订单事项明细报表", notes = "导出生产订单事项明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTask.class))
    public void getConcernTaskExoprt(HttpServletResponse response,  ManManufactureOrderConcernTask ConcernTask) throws IOException {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskService.selectManManufactureOrderConcernTaskForm(ConcernTask);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderConcernTask> util = new ExcelUtil<>(ManManufactureOrderConcernTask.class, dataMap);
        util.exportExcel(response, list, "生产订单事项明细报表");
    }

    /**
     * 生产订单事项明细报表 设置计划信息
     */
    @PostMapping("/form/concern/set")
    @ApiOperation(value = "设置计划信息", notes = "设置计划信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTaskSetRequest.class))
    public AjaxResult concernSetPlan(@RequestBody @Valid ManManufactureOrderConcernTaskSetRequest request) {
        if (request.getManufactureOrderConcernTaskSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        return toAjax(manManufactureOrderConcernTaskService.concernSet(request));
    }

    @PostMapping("/form/concern/set/status")
    @ApiOperation(value = "进度反馈按钮", notes = "进度反馈按钮")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTaskSetRequest.class))
    public AjaxResult concernSetStatus(@RequestBody ManManufactureOrderConcernTask request) {
        return AjaxResult.success(manManufactureOrderConcernTaskService.setProcessStatus(request));
    }

    /**
     * 生产订单工序明细报表 设置计划信息
     */
    @PostMapping("/form/process/set")
    @ApiOperation(value = "设置计划信息", notes = "设置计划信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTaskSetRequest.class))
    public AjaxResult processSetPlan(@RequestBody @Valid ManManufactureOrderProcessSetRequest request) {
        if (request.getManufactureOrderProcessSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        return toAjax(manManufactureOrderProcessService.concernSet(request));
    }

    @PostMapping("/form/concern/set/toexpireDays")
    @ApiOperation(value = "生产订单事项明细报表设置到期天数", notes = "生产订单事项明细报表设置到期天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTaskSetRequest.class))
    public AjaxResult concernSetToexpireDays(@RequestBody @Valid ManManufactureOrderConcernTask request) {
        return toAjax(manManufactureOrderConcernTaskService.setToexpireDays(request));
    }

    /**
     * 查询班组生产进度报表
     */
    @PostMapping("/form/work")
    @ApiOperation(value = "查询班组生产进度报表", notes = "查询班组生产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkOrderProgressFormData.class))
    public AjaxResult form(@RequestBody ManManufactureOrder manManufactureOrder) {
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manManufactureOrder.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        ManWorkOrderProgressFormData response = new ManWorkOrderProgressFormData();
        ManManufactureOrder request = new ManManufactureOrder();
        BeanCopyUtils.copyProperties(manManufactureOrder, request);
        request.setPageNum(null).setPageSize(null).setPageSize(null);
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessMapper.selectByProcessRouteListGroupByWork(request);
        long total = list.size();
        response.setTotal(total);
        response.setProcessNameList(new ArrayList<>());
        response.setFormList(new ArrayList<>());
        if (total > 0){
            response = manManufactureOrderService.selectManManufactureOrderWorkProgress(manManufactureOrder);
            response.setTotal(total);
            return AjaxResult.success(response);
        }
        return AjaxResult.success(response);
    }

    /**
     * 查询生产进度报表
     */
    @PostMapping("/form/progress")
    @ApiOperation(value = "查询生产进度报表", notes = "查询生产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkOrderProgressFormData.class))
    public AjaxResult formProgress(@RequestBody ManManufactureOrder manManufactureOrder) {
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manManufactureOrder.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        ManWorkOrderProgressFormData response = new ManWorkOrderProgressFormData();
        ManManufactureOrder request = new ManManufactureOrder();
        BeanCopyUtils.copyProperties(manManufactureOrder, request);
        request.setPageNum(null).setPageSize(null).setPageSize(null);
        request.setConcernTaskGroupSid(null).setProcessRouteSid(null);
        List<ManManufactureOrder> list = manManufactureOrderMapper.selectManManufactureOrderProgressForm(request);
        long total = list.size();
        response.setTotal(total);
        response.setConcernNameList(new ArrayList<>());
        response.setProcessNameList(new ArrayList<>());
        response.setFormList(new ArrayList<>());
        if (total > 0){
            response = manManufactureOrderService.selectManManufactureOrderProgress(manManufactureOrder);
            response.setTotal(total);
            return AjaxResult.success(response);
        }
        return AjaxResult.success(response);
    }

    /**
     * 生产进度跟踪报表（商品）
     */
    @PostMapping("/product/tracking")
    @ApiOperation(value = "生产进度跟踪报表（商品）", notes = "生产进度跟踪报表（商品）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManuOrderProductTracking.class))
    @Idempotent(interval = 3000,message = "请勿重复查询")
    public TableDataInfo productTracking(@RequestBody ManManuOrderProductTracking request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        List<ManManuOrderProductTracking> total = manManufactureOrderProductService.selectManufactureOrderProductTrackingList(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<ManManuOrderProductTracking> list = manManufactureOrderProductService.selectManufactureOrderProductTrackingList(request);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 生产进度跟踪报表(商品)
     */
    @ApiOperation(value = "生产进度跟踪报表(商品)", notes = "生产进度跟踪报表(商品)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/product/tracking/export")
    public void exportProductTracking(HttpServletResponse response, ManManuOrderProductTracking manManufactureOrder) throws IOException {
        List<ManManuOrderProductTracking> list = manManufactureOrderProductService.selectManufactureOrderProductTrackingList(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        if (ConstantsProcess.MAN_DOC_ANKU.equals(manManufactureOrder.getDocumentType())) {
            List<ManManuOrderProductTrackingKu> exportList = BeanCopyUtils.copyListProperties(list, ManManuOrderProductTrackingKu::new);
            ExcelUtil<ManManuOrderProductTrackingKu> util = new ExcelUtil<>(ManManuOrderProductTrackingKu.class, dataMap);
            util.exportExcel(response, exportList, "生产进度跟踪报表(商品)(按库生产)");
        }
        else if (ConstantsProcess.MAN_DOC_ANDAN.equals(manManufactureOrder.getDocumentType())) {
            ExcelUtil<ManManuOrderProductTracking> util = new ExcelUtil<>(ManManuOrderProductTracking.class, dataMap);
            util.exportExcel(response, list, "生产进度跟踪报表(商品)(按单生产)");
        }
    }

    /**
     * 生产进度跟踪报表（工序）
     */
    @PostMapping("/process/tracking")
    @ApiOperation(value = "生产进度跟踪报表（工序）", notes = "生产进度跟踪报表（工序）")
    @ApiResponses(@ApiResponse(code = 200, message = "工序", response = ManManuOrderProcessTracking.class))
    @Idempotent(interval = 3000,message = "请勿重复查询")
    public TableDataInfo processTracking(@RequestBody ManManuOrderProcessTracking request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        List<ManManuOrderProcessTracking> total = manManufactureOrderProcessService.selectManufactureOrderProcessTrackingList(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<ManManuOrderProcessTracking> list = manManufactureOrderProcessService.selectManufactureOrderProcessTrackingList(request);
            rspData.setRows(list);
            rspData.setTotal(total.get(0).getPageSize());
        }
        return rspData;
    }

    /**
     * 生产进度跟踪报表(工序)
     */
    @ApiOperation(value = "生产进度跟踪报表(工序)", notes = "生产进度跟踪报表(工序)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/process/tracking/export")
    public void exportProcessTracking(HttpServletResponse response, ManManuOrderProcessTracking manManufactureOrder) throws IOException {
        manManufactureOrder.setPageNum(1).setPageSize(10000);
        List<ManManuOrderProcessTracking> list = manManufactureOrderProcessService.selectManufactureOrderProcessTrackingList(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManuOrderProcessTracking> util = new ExcelUtil<>(ManManuOrderProcessTracking.class, dataMap);
        util.exportExcel(response, list, "生产进度跟踪报表(工序)");
    }

    /**
     * 生产进度跟踪报表（事项）
     */
    @PostMapping("/concern/tracking")
    @ApiOperation(value = "生产进度跟踪报表（事项）", notes = "生产进度跟踪报表（事项）")
    @ApiResponses(@ApiResponse(code = 200, message = "事项", response = ManManuOrderConcernTracking.class))
    @Idempotent(interval = 3000,message = "请勿重复查询")
    public TableDataInfo concernTracking(@RequestBody ManManuOrderConcernTracking request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        List<ManManuOrderConcernTracking> total = manManufactureOrderConcernTaskService.selectManufactureOrderConcernTrackingList(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<ManManuOrderConcernTracking> list = manManufactureOrderConcernTaskService.selectManufactureOrderConcernTrackingList(request);
            rspData.setRows(list);
            rspData.setTotal(total.get(0).getPageSize());
        }
        return rspData;
    }

    /**
     * 生产进度跟踪报表(事项)
     */
    @ApiOperation(value = "生产进度跟踪报表(事项)", notes = "生产进度跟踪报表(事项)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/concern/tracking/export")
    public void exportConcernTracking(HttpServletResponse response, ManManuOrderConcernTracking manManufactureOrder) throws IOException {
        manManufactureOrder.setPageNum(1).setPageSize(10000);
        List<ManManuOrderConcernTracking> list = manManufactureOrderConcernTaskService.selectManufactureOrderConcernTrackingList(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManuOrderConcernTracking> util = new ExcelUtil<>(ManManuOrderConcernTracking.class, dataMap);
        util.exportExcel(response, list, "生产进度跟踪报表(事项)");
    }


    /**
     * 查询商品生产统计报表
     */
    @PostMapping("/production/statistics")
    @ApiOperation(value = "查询商品生产统计报表", notes = "查询商品生产统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProductStatistics.class))
    public TableDataInfo productionStatistics(@RequestBody ManManufactureOrderProductStatistics manManufactureOrder) {
        int pageNum = manManufactureOrder.getPageNum();
        manManufactureOrder.setPageNum(null);
        List<ManManufactureOrderProductStatistics> total = manManufactureOrderProductService.selectManManufactureOrderProductStatistics(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrder.setPageNum(pageNum);
            List<ManManufactureOrderProductStatistics> list = manManufactureOrderProductService.selectManManufactureOrderProductStatistics(manManufactureOrder);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, total.size());
    }


    /**
     * 导出商品生产统计报表
     */
    @ApiOperation(value = "导出商品生产统计报表", notes = "导出商品生产统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/production/statistics/export")
    public void exportproductionStatistics(HttpServletResponse response, ManManufactureOrderProductStatistics manManufactureOrder) throws IOException {
        List<ManManufactureOrderProductStatistics> list = manManufactureOrderProductService.selectManManufactureOrderProductStatistics(manManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderProductStatistics> util = new ExcelUtil<>(ManManufactureOrderProductStatistics.class, dataMap);
        util.exportExcel(response, list, "商品生产统计报表");
    }


    /**
     * 生产进度状态报表
     *
     * 根据查询条件查询出符合条件的所有非“已作废”状态的生产订单，获取该生产订单主表、工序明细表、事项明细表、商品明细表的数据，显示在进度卡片中
     * 查询条件“合同交期”，要根据“生产订单商品明细表”中存的“销售订单明细行sid”（sales_order_item_sid）的“合同交期”关联查询出符合条件的生产订单
     * 进度卡片的“已完工量”，需根据该生产订单的工序明细表信息，从班组生产日报主表、明细表中获取对应工序的每日完成量信息并累计，计算出“已完工量”
     * 查询出的数据按“计划完工日期”升序排列
     *
     */
    @PostMapping("/statistics/report")
    @ApiOperation(value = "导出商品生产统计报表", notes = "导出商品生产统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public TableDataInfo selectStatusReport(@RequestBody ManManufactureOrder manManufactureOrder) {
        startPage(manManufactureOrder);
        List<ManManufactureOrder> list = manManufactureOrderService.selectStatusReport(manManufactureOrder);
        return getDataTable(list);
    }



}
