package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;
import com.platform.ems.plug.service.IConBuTypeSalesOrderService;
import com.platform.ems.plug.service.IConDocTypeSalesOrderService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单据类型_销售订单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/salesOrder")
@Api(tags = "单据类型_销售订单")
public class ConDocTypeSalesOrderController extends BaseController {

    @Autowired
    private IConDocTypeSalesOrderService conDocTypeSalesOrderService;
    @Autowired
    private IConBuTypeSalesOrderService conBuTypeSalesOrderService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询单据类型_销售订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_销售订单列表", notes = "查询单据类型_销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSalesOrder.class))
    public TableDataInfo list(@RequestBody ConDocTypeSalesOrder conDocTypeSalesOrder) {
        startPage(conDocTypeSalesOrder);
        List<ConDocTypeSalesOrder> list = conDocTypeSalesOrderService.selectConDocTypeSalesOrderList(conDocTypeSalesOrder);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_销售订单列表
     */
    @Log(title = "单据类型_销售订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_销售订单列表", notes = "导出单据类型_销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeSalesOrder conDocTypeSalesOrder) throws IOException {
        List<ConDocTypeSalesOrder> list = conDocTypeSalesOrderService.selectConDocTypeSalesOrderList(conDocTypeSalesOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeSalesOrder> util = new ExcelUtil<>(ConDocTypeSalesOrder.class, dataMap);
        util.exportExcel(response, list, "单据类型_销售订单");
    }

    /**
     * 导入单据类型_销售订单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_销售订单", notes = "导入单据类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConDocTypeSalesOrder> util = new ExcelUtil<ConDocTypeSalesOrder>(ConDocTypeSalesOrder.class);
        List<ConDocTypeSalesOrder> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conDocTypeSalesOrder -> {
                conDocTypeSalesOrderService.insertConDocTypeSalesOrder(conDocTypeSalesOrder);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载单据类型_销售订单导入模板", notes = "下载单据类型_销售订单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeSalesOrder> util = new ExcelUtil<ConDocTypeSalesOrder>(ConDocTypeSalesOrder.class);
        util.importTemplateExcel(response, "单据类型_销售订单导入模板");
    }


    /**
     * 获取单据类型_销售订单详细信息
     */
    @ApiOperation(value = "获取单据类型_销售订单详细信息", notes = "获取单据类型_销售订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSalesOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeSalesOrderService.selectConDocTypeSalesOrderById(sid));
    }

    /**
     * 新增单据类型_销售订单
     */
    @ApiOperation(value = "新增单据类型_销售订单", notes = "新增单据类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return toAjax(conDocTypeSalesOrderService.insertConDocTypeSalesOrder(conDocTypeSalesOrder));
    }

    /**
     * 修改单据类型_销售订单
     */
    @ApiOperation(value = "修改单据类型_销售订单", notes = "修改单据类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return toAjax(conDocTypeSalesOrderService.updateConDocTypeSalesOrder(conDocTypeSalesOrder));
    }

    /**
     * 变更单据类型_销售订单
     */
    @ApiOperation(value = "变更单据类型_销售订单", notes = "变更单据类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售订单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return toAjax(conDocTypeSalesOrderService.changeConDocTypeSalesOrder(conDocTypeSalesOrder));
    }

    /**
     * 删除单据类型_销售订单
     */
    @ApiOperation(value = "删除单据类型_销售订单", notes = "删除单据类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeSalesOrderService.deleteConDocTypeSalesOrderByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售订单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return AjaxResult.success(conDocTypeSalesOrderService.changeStatus(conDocTypeSalesOrder));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售订单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeSalesOrder conDocTypeSalesOrder) {
        conDocTypeSalesOrder.setConfirmDate(new Date());
        conDocTypeSalesOrder.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeSalesOrder.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeSalesOrderService.check(conDocTypeSalesOrder));
    }

    @PostMapping("/getConDocTypeSalesOrderList")
    @ApiOperation(value = "单据类型_销售订单下拉列表", notes = "单据类型_销售订单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSalesOrder.class))
    public AjaxResult getConDocTypeSalesOrderList() {
        return AjaxResult.success(conDocTypeSalesOrderService.getConDocTypeSalesOrderList());
    }

    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_销售订单下拉列表(带参)", notes = "单据类型_销售订单下拉框列表(带参)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSalesOrder.class))
    public AjaxResult getList(@RequestBody ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return AjaxResult.success(conDocTypeSalesOrderService.getList(conDocTypeSalesOrder));
    }

    @PostMapping("/getRelevancyBuList")
    @ApiOperation(value = "根据单据类型获取关联业务类型", notes = "根据单据类型获取关联业务类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSalesOrder.class))
    public AjaxResult getRelevancyBuList(@RequestBody ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return AjaxResult.success(conBuTypeSalesOrderService.getRelevancyBuList(conDocTypeSalesOrder));
    }
}
