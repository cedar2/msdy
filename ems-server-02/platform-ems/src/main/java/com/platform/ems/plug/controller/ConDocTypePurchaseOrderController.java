package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.plug.service.IConBuTypePurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;
import com.platform.ems.plug.service.IConDocTypePurchaseOrderService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_采购订单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/purchase/order")
@Api(tags = "单据类型_采购订单")
public class ConDocTypePurchaseOrderController extends BaseController {

    @Autowired
    private IConDocTypePurchaseOrderService conDocTypePurchaseOrderService;
    @Autowired
    private IConBuTypePurchaseOrderService conBuTypePurchaseOrderService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询单据类型_采购订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_采购订单列表", notes = "查询单据类型_采购订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePurchaseOrder.class))
    public TableDataInfo list(@RequestBody ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        startPage(conDocTypePurchaseOrder);
        List<ConDocTypePurchaseOrder> list = conDocTypePurchaseOrderService.selectConDocTypePurchaseOrderList(conDocTypePurchaseOrder);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_采购订单列表
     */
    @Log(title = "单据类型_采购订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_采购订单列表", notes = "导出单据类型_采购订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypePurchaseOrder conDocTypePurchaseOrder) throws IOException {
        List<ConDocTypePurchaseOrder> list = conDocTypePurchaseOrderService.selectConDocTypePurchaseOrderList(conDocTypePurchaseOrder);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypePurchaseOrder> util = new ExcelUtil<>(ConDocTypePurchaseOrder.class,dataMap);
        util.exportExcel(response, list, "单据类型_采购订单");
    }

    /**
     * 导入单据类型_采购订单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_采购订单", notes = "导入单据类型_采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDocTypePurchaseOrder> util = new ExcelUtil<ConDocTypePurchaseOrder>(ConDocTypePurchaseOrder.class);
        List<ConDocTypePurchaseOrder> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDocTypePurchaseOrder ->{
                conDocTypePurchaseOrderService.insertConDocTypePurchaseOrder(conDocTypePurchaseOrder);
                i++;
            });
        }catch (Exception e){
            lose=listSize-i;
            msg=StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入",i,lose);
        }
        if(StrUtil.isEmpty(msg)){
            msg="导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载单据类型_采购订单导入模板", notes = "下载单据类型_采购订单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypePurchaseOrder> util = new ExcelUtil<ConDocTypePurchaseOrder>(ConDocTypePurchaseOrder.class);
        util.importTemplateExcel(response, "单据类型_采购订单导入模板");
    }


    /**
     * 获取单据类型_采购订单详细信息
     */
    @ApiOperation(value = "获取单据类型_采购订单详细信息", notes = "获取单据类型_采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePurchaseOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDocTypePurchaseOrderService.selectConDocTypePurchaseOrderById(sid));
    }

    /**
     * 新增单据类型_采购订单
     */
    @ApiOperation(value = "新增单据类型_采购订单", notes = "新增单据类型_采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return toAjax(conDocTypePurchaseOrderService.insertConDocTypePurchaseOrder(conDocTypePurchaseOrder));
    }

    /**
     * 修改单据类型_采购订单
     */
    @ApiOperation(value = "修改单据类型_采购订单", notes = "修改单据类型_采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "单据类型_采购订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return toAjax(conDocTypePurchaseOrderService.updateConDocTypePurchaseOrder(conDocTypePurchaseOrder));
    }

    /**
     * 变更单据类型_采购订单
     */
    @ApiOperation(value = "变更单据类型_采购订单", notes = "变更单据类型_采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "单据类型_采购订单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return toAjax(conDocTypePurchaseOrderService.changeConDocTypePurchaseOrder(conDocTypePurchaseOrder));
    }

    /**
     * 删除单据类型_采购订单
     */
    @ApiOperation(value = "删除单据类型_采购订单", notes = "删除单据类型_采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购订单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypePurchaseOrderService.deleteConDocTypePurchaseOrderByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购订单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return AjaxResult.success(conDocTypePurchaseOrderService.changeStatus(conDocTypePurchaseOrder));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购订单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        conDocTypePurchaseOrder.setConfirmDate(new Date());
        conDocTypePurchaseOrder.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypePurchaseOrder.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypePurchaseOrderService.check(conDocTypePurchaseOrder));
    }

    /**
     * 单据类型_采购订单下拉框
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_采购订单下拉框", notes = "单据类型_采购订单下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePurchaseOrder.class))
    public AjaxResult getList() {
        return AjaxResult.success(conDocTypePurchaseOrderService.getList());
    }

    /**
     * 单据类型_采购订单下拉框
     */
    @PostMapping("/getDocList")
    @ApiOperation(value = "单据类型_采购订单下拉框", notes = "单据类型_采购订单下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePurchaseOrder.class))
    public AjaxResult getDocList(@RequestBody ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return AjaxResult.success(conDocTypePurchaseOrderService.getDocList(conDocTypePurchaseOrder));
    }

    @PostMapping("/getRelevancyBuList")
    @ApiOperation(value = "根据单据类型获取关联业务类型", notes = "根据单据类型获取关联业务类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePurchaseOrder.class))
    public AjaxResult getRelevancyBuList(@RequestBody ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return AjaxResult.success(conBuTypePurchaseOrderService.getRelevancyBuList(conDocTypePurchaseOrder));
    }

}
