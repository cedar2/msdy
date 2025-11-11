package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.InvRecordCustomerRepair;
import com.platform.ems.domain.InvRecordVendorRepair;
import com.platform.ems.domain.InvRecordVendorRepairExResponse;
import com.platform.ems.domain.dto.request.InvRecordCustomerRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordCustomerRepairExResponse;
import com.platform.ems.domain.dto.response.InvRecordCustomerRepairResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IInvRecordCustomerRepairService;
import com.platform.ems.service.ISystemDictDataService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 客户返修台账Controller
 *
 * @author linhongwei
 * @date 2021-10-27
 */
@RestController
@RequestMapping("/record/customer/repair")
@Api(tags = "客户返修台账")
public class InvRecordCustomerRepairController extends BaseController {

    @Autowired
    private IInvRecordCustomerRepairService invRecordCustomerRepairService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户返修台账列表
     */
//    @PreAuthorize(hasPermi = "ems:repair:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询客户返修台账列表", notes = "查询客户返修台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvRecordCustomerRepair.class))
    public TableDataInfo list(@RequestBody InvRecordCustomerRepair invRecordCustomerRepair) {
        startPage(invRecordCustomerRepair);
        List<InvRecordCustomerRepair> list = invRecordCustomerRepairService.selectInvRecordCustomerRepairList(invRecordCustomerRepair);
        return getDataTable(list);
    }

    /**
     * 查询客户返修台账明细报表
     */
//    @PreAuthorize(hasPermi = "ems:repair:list")
    @PostMapping("/report")
    @ApiOperation(value = "查询客户返修台账明细报表", notes = "查询客户返修台账明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvRecordCustomerRepair.class))
    public TableDataInfo report(@RequestBody InvRecordCustomerRepairRequest invRecordCustomerRepair) {
        startPage(invRecordCustomerRepair);
        List<InvRecordCustomerRepairResponse> list = invRecordCustomerRepairService.report(invRecordCustomerRepair);
        return getDataTable(list);
    }

    @Log(title = "客户返修台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出客户返修台账明细报表", notes = "导出客户返修台账明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvRecordCustomerRepairRequest invRecordCustomerRepair) throws IOException {
        List<InvRecordCustomerRepairResponse> list = invRecordCustomerRepairService.report(invRecordCustomerRepair);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvRecordCustomerRepairResponse> util = new ExcelUtil<>(InvRecordCustomerRepairResponse.class,dataMap);
        util.exportExcel(response, list, "客户返修台账明细报表");
    }
    /**
     * 导出客户返修台账列表
     */
    @PreAuthorize(hasPermi = "ems::customer:repair:export")
    @Log(title = "客户返修台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出客户返修台账列表", notes = "导出客户返修台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvRecordCustomerRepair invRecordCustomerRepair) throws IOException {
        List<InvRecordCustomerRepair> list = invRecordCustomerRepairService.selectInvRecordCustomerRepairList(invRecordCustomerRepair);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvRecordCustomerRepairExResponse> util = new ExcelUtil<>(InvRecordCustomerRepairExResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvRecordCustomerRepairExResponse::new), "客户返修台账"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取客户返修台账详细信息
     */
    @ApiOperation(value = "获取客户返修台账详细信息", notes = "获取客户返修台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvRecordCustomerRepair.class))
//    @PreAuthorize(hasPermi = "ems:repair:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerRepairSid) {
        if (customerRepairSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invRecordCustomerRepairService.selectInvRecordCustomerRepairById(customerRepairSid));
    }

    /**
     * 新增客户返修台账
     */
    @ApiOperation(value = "新增客户返修台账", notes = "新增客户返修台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:repair:add")
    @Log(title = "客户返修台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvRecordCustomerRepair invRecordCustomerRepair) {
        return toAjax(invRecordCustomerRepairService.insertInvRecordCustomerRepair(invRecordCustomerRepair));
    }

    /**
     * 修改客户返修台账
     */
    @ApiOperation(value = "修改/变更客户返修台账", notes = "修改/变更客户返修台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:repair:edit")
    @Log(title = "客户返修台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody InvRecordCustomerRepair invRecordCustomerRepair) {
        return toAjax(invRecordCustomerRepairService.updateInvRecordCustomerRepair(invRecordCustomerRepair));
    }

    /**
     * 变更客户返修台账
     */
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:repair:change")
    @Log(title = "客户返修台账", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody InvRecordCustomerRepair invRecordCustomerRepair) {
        return toAjax(invRecordCustomerRepairService.changeInvRecordCustomerRepair(invRecordCustomerRepair));
    }

    /**
     * 删除客户返修台账
     */
    @ApiOperation(value = "删除客户返修台账", notes = "删除客户返修台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems::customer:repair:remove")
    @Log(title = "客户返修台账", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  customerRepairSids) {
        if(CollectionUtils.isEmpty( customerRepairSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invRecordCustomerRepairService.deleteInvRecordCustomerRepairByIds(customerRepairSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户返修台账", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:repair:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvRecordCustomerRepair invRecordCustomerRepair) {
        return AjaxResult.success(invRecordCustomerRepairService.changeStatus(invRecordCustomerRepair));
    }

    @ApiOperation(value = "添加明细行物料重复校验", notes = "添加明细行物料重复校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/judge/add")
    public AjaxResult add(@RequestBody List<InvRecordCustomerRepair> list) {
        if(CollectionUtils.isEmpty(list)){
            throw  new CustomException("参数不允许为空");
        }
        return toAjax(invRecordCustomerRepairService.judgeRepeat(list));
    }

    @ApiOperation(value = "确认客户返修台账", notes = "确认客户返修台账")
    @PreAuthorize(hasPermi = "ems::customer:repair:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户返修台账", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvRecordCustomerRepair invRecordCustomerRepair) {
        invRecordCustomerRepair.setConfirmDate(new Date());
        invRecordCustomerRepair.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        invRecordCustomerRepair.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(invRecordCustomerRepairService.check(invRecordCustomerRepair));
    }

}
