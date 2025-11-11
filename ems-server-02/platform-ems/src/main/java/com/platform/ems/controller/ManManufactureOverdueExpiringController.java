package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.ManManufactureOrder;
import com.platform.ems.domain.ManManufactureOrderConcernTask;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.dto.response.export.ManManufactureOrderConcernOverAndToex;
import com.platform.ems.domain.dto.response.export.ManManufactureOrderOverAndToex;
import com.platform.ems.domain.dto.response.export.ManManufactureOrderProcessOverAndToex;
import com.platform.ems.service.IManManufactureOrderConcernTaskService;
import com.platform.ems.service.IManManufactureOrderProcessService;
import com.platform.ems.service.IManManufactureOrderService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.service.ISysDictDataService;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manManufactureOE")
@Api(tags = "已逾期&即将到期报表")
public class ManManufactureOverdueExpiringController extends BaseController {

    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private IManManufactureOrderService manManufactureOrderService;
    @Autowired
    private IManManufactureOrderProcessService manManufactureOrderProcessService;
    @Autowired
    private IManManufactureOrderConcernTaskService manManufactureOrderConcernTaskService;



    ////////////////////////////////////////////即将到期报表///////////////////////////////////////////////////////

    /**
     * 即将到期生产报表-按订单
     * @param manManufactureOrder ManManufactureOrder
     * @return ManManufactureOrder
     */
    @PostMapping("/form/expiring/order")
    @ApiOperation(value = "即将到期报表-按订单", notes = "即将到期报表-按订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public TableDataInfo getExpiringOrderForm(@RequestBody ManManufactureOrder manManufactureOrder) {
        Integer pageNum = manManufactureOrder.getPageNum();
        manManufactureOrder.setPageNum(null);
        manManufactureOrder.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManufactureOrder> total = manManufactureOrderService.selectExpiringOrderForm(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrder.setPageNum(pageNum);
            List<ManManufactureOrder> list = manManufactureOrderService.selectExpiringOrderForm(manManufactureOrder);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, 0);
    }

    @PostMapping("/form/expiring/order/export")
    @ApiOperation(value = "即将到期报表-按订单-导出表格", notes = "即将到期报表-按订单-导出表格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public void exportExpiringOrder(HttpServletResponse response, ManManufactureOrder manManufactureOrder) throws IOException {
        List<ManManufactureOrder> list = manManufactureOrderService.selectExpiringOrderForm(manManufactureOrder);
        List<ManManufactureOrderOverAndToex> andToexList = BeanCopyUtils.copyListProperties(list, ManManufactureOrderOverAndToex::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderOverAndToex> util = new ExcelUtil<>(ManManufactureOrderOverAndToex.class, dataMap);
        util.exportExcel(response, andToexList, "即将到期报表-按订单");
    }

    /**
     * 即将到期生产报表-按工序
     * @param manManufactureOrderProcess ManManufactureOrderProcess
     * @return ManManufactureOrderProcess
     */
    @PostMapping("/form/expiring/process")
    @ApiOperation(value = "即将到期报表-按工序", notes = "即将到期报表-按工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public TableDataInfo getExpiringProcessForm(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        Integer pageNum = manManufactureOrderProcess.getPageNum();
        manManufactureOrderProcess.setPageNum(null);
        manManufactureOrderProcess.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManufactureOrderProcess> total = manManufactureOrderProcessService.selectExpiringProcessForm(manManufactureOrderProcess);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrderProcess.setPageNum(pageNum);
            List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.selectExpiringProcessForm(manManufactureOrderProcess);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, 0);
    }

    @PostMapping("/form/expiring/process/export")
    @ApiOperation(value = "即将到期报表-按工序-导出表格", notes = "即将到期报表-按工序-导出表格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public void exportExpiringProcess(HttpServletResponse response, ManManufactureOrderProcess manManufactureOrderProcess) throws IOException {
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.selectExpiringProcessForm(manManufactureOrderProcess);
        List<ManManufactureOrderProcessOverAndToex> andToexList = BeanCopyUtils.copyListProperties(list, ManManufactureOrderProcessOverAndToex::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderProcessOverAndToex> util = new ExcelUtil<>(ManManufactureOrderProcessOverAndToex.class, dataMap);
        util.exportExcel(response, andToexList, "即将到期报表-按工序");
    }

    /**
     * 即将到期报表-按事项
     * @param manManufactureOrderConcernTask ManManufactureOrderConcernTask
     * @return ManManufactureOrderConcernTask
     */
    @PostMapping("/form/expiring/task")
    @ApiOperation(value = "即将到期报表-按事项", notes = "即将到期报表-按事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTask.class))
    public TableDataInfo getExpiringTaskForm(@RequestBody ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        Integer pageNum = manManufactureOrderConcernTask.getPageNum();
        manManufactureOrderConcernTask.setPageNum(null);
        manManufactureOrderConcernTask.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManufactureOrderConcernTask> total = manManufactureOrderConcernTaskService.selectExpiringTaskForm(manManufactureOrderConcernTask);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrderConcernTask.setPageNum(pageNum);
            List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskService.selectExpiringTaskForm(manManufactureOrderConcernTask);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, 0);
    }

    @PostMapping("/form/expiring/task/export")
    @ApiOperation(value = "即将到期报表-按事项-导出表格", notes = "即将到期报表-按事项-导出表格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTask.class))
    public void exportExpiringTask(HttpServletResponse response, ManManufactureOrderConcernTask manManufactureOrderConcernTask) throws IOException {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskService.selectExpiringTaskForm(manManufactureOrderConcernTask);
        List<ManManufactureOrderConcernOverAndToex> andToexList = BeanCopyUtils.copyListProperties(list, ManManufactureOrderConcernOverAndToex::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderConcernOverAndToex> util = new ExcelUtil<>(ManManufactureOrderConcernOverAndToex.class, dataMap);
        util.exportExcel(response, andToexList, "即将到期报表-按事项");
    }

    ////////////////////////////////////////////已逾期///////////////////////////////////////////////////////

    /**
     * 已逾期生产报表-按订单
     * @param manManufactureOrder ManManufactureOrder
     * @return ManManufactureOrder
     */
    @PostMapping("/form/overdue/order")
    @ApiOperation(value = "已逾期生产报表-按订单", notes = "已逾期生产报表-按订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public TableDataInfo getOverdueOrderForm(@RequestBody ManManufactureOrder manManufactureOrder) {
        Integer pageNum = manManufactureOrder.getPageNum();
        manManufactureOrder.setPageNum(null);
        manManufactureOrder.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManufactureOrder> total = manManufactureOrderService.selectOverdueOrderForm(manManufactureOrder);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrder.setPageNum(pageNum);
            List<ManManufactureOrder> list = manManufactureOrderService.selectOverdueOrderForm(manManufactureOrder);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, 0);
    }

    @PostMapping("/form/overdue/order/export")
    @ApiOperation(value = "已逾期生产报表-按订单-导出表格", notes = "已逾期生产报表-按订单-导出表格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public void exportOverdueOrder(HttpServletResponse response, ManManufactureOrder manManufactureOrder) throws IOException {
        List<ManManufactureOrder> list = manManufactureOrderService.selectOverdueOrderForm(manManufactureOrder);
        List<ManManufactureOrderOverAndToex> andToexList = BeanCopyUtils.copyListProperties(list, ManManufactureOrderOverAndToex::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderOverAndToex> util = new ExcelUtil<>(ManManufactureOrderOverAndToex.class, dataMap);
        util.exportExcel(response, andToexList, "已逾期生产报表-按订单");
    }

    /**
     * 已逾期生产报表-按工序
     * @param manManufactureOrderProcess ManManufactureOrderProcess
     * @return ManManufactureOrderProcess
     */
    @PostMapping("/form/overdue/process")
    @ApiOperation(value = "已逾期生产报表-按工序", notes = "已逾期生产报表-按工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public TableDataInfo getOverdueProcessForm(@RequestBody ManManufactureOrderProcess manManufactureOrderProcess) {
        Integer pageNum = manManufactureOrderProcess.getPageNum();
        manManufactureOrderProcess.setPageNum(null);
        manManufactureOrderProcess.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManufactureOrderProcess> total = manManufactureOrderProcessService.selectOverdueProcessForm(manManufactureOrderProcess);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrderProcess.setPageNum(pageNum);
            List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.selectOverdueProcessForm(manManufactureOrderProcess);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, 0);
    }

    @PostMapping("/form/overdue/process/export")
    @ApiOperation(value = "已逾期生产报表-按工序-导出表格", notes = "已逾期生产报表-按工序-导出表格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public void exportOverdueProcess(HttpServletResponse response, ManManufactureOrderProcess manManufactureOrderProcess) throws IOException {
        List<ManManufactureOrderProcess> list = manManufactureOrderProcessService.selectOverdueProcessForm(manManufactureOrderProcess);
        List<ManManufactureOrderProcessOverAndToex> andToexList = BeanCopyUtils.copyListProperties(list, ManManufactureOrderProcessOverAndToex::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderProcessOverAndToex> util = new ExcelUtil<>(ManManufactureOrderProcessOverAndToex.class, dataMap);
        util.exportExcel(response, andToexList, "已逾期生产报表-按工序");
    }

    /**
     * 已逾期生产报表-按事项
     * @param manManufactureOrderConcernTask ManManufactureOrderConcernTask
     * @return ManManufactureOrderConcernTask
     */
    @PostMapping("/form/overdue/task")
    @ApiOperation(value = "已逾期生产报表-按事项", notes = "已逾期生产报表-按事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTask.class))
    public TableDataInfo getOverdueTaskForm(@RequestBody ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        Integer pageNum = manManufactureOrderConcernTask.getPageNum();
        manManufactureOrderConcernTask.setPageNum(null);
        manManufactureOrderConcernTask.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManufactureOrderConcernTask> total = manManufactureOrderConcernTaskService.selectOverdueTaskForm(manManufactureOrderConcernTask);
        if (CollectionUtil.isNotEmpty(total)) {
            manManufactureOrderConcernTask.setPageNum(pageNum);
            List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskService.selectOverdueTaskForm(manManufactureOrderConcernTask);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, 0);
    }

    @PostMapping("/form/overdue/task/export")
    @ApiOperation(value = "已逾期生产报表-按事项-导出表格", notes = "已逾期生产报表-按事项-导出表格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTask.class))
    public void exportOverdueTask(HttpServletResponse response, ManManufactureOrderConcernTask manManufactureOrderConcernTask) throws IOException {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskService.selectOverdueTaskForm(manManufactureOrderConcernTask);
        List<ManManufactureOrderConcernOverAndToex> andToexList = BeanCopyUtils.copyListProperties(list, ManManufactureOrderConcernOverAndToex::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderConcernOverAndToex> util = new ExcelUtil<>(ManManufactureOrderConcernOverAndToex.class, dataMap);
        util.exportExcel(response, andToexList, "已逾期生产报表-按事项");
    }


}
