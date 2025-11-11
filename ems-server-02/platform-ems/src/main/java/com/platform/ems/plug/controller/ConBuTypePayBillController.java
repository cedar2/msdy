package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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

import javax.validation.Valid;
import com.platform.ems.plug.domain.ConBuTypePayBill;
import com.platform.ems.plug.service.IConBuTypePayBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;

import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_付款单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/bill/pay")
@Api(tags = "业务类型_付款单")
public class ConBuTypePayBillController extends BaseController {

    @Autowired
    private IConBuTypePayBillService conBuTypePayBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_付款单列表
     */
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_付款单列表", notes = "查询业务类型_付款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypePayBill.class))
    public TableDataInfo list(@RequestBody ConBuTypePayBill conBuTypePayBill) {
        startPage(conBuTypePayBill);
        List<ConBuTypePayBill> list = conBuTypePayBillService.selectConBuTypePayBillList(conBuTypePayBill);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_付款单列表
     */
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:export")
    @Log(title = "业务类型_付款单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_付款单列表", notes = "导出业务类型_付款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypePayBill conBuTypePayBill) throws IOException {
        List<ConBuTypePayBill> list = conBuTypePayBillService.selectConBuTypePayBillList(conBuTypePayBill);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypePayBill> util = new ExcelUtil<>(ConBuTypePayBill.class,dataMap);
        util.exportExcel(response, list, "业务类型_付款单");
    }

    /**
     * 获取业务类型_付款单详细信息
     */
    @ApiOperation(value = "获取业务类型_付款单详细信息", notes = "获取业务类型_付款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypePayBill.class))
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conBuTypePayBillService.selectConBuTypePayBillById(sid));
    }

    /**
     * 新增业务类型_付款单
     */
    @ApiOperation(value = "新增业务类型_付款单", notes = "新增业务类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:add")
    @Log(title = "业务类型_付款单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypePayBill conBuTypePayBill) {
        return toAjax(conBuTypePayBillService.insertConBuTypePayBill(conBuTypePayBill));
    }

    /**
     * 修改业务类型_付款单
     */
    @ApiOperation(value = "修改业务类型_付款单", notes = "修改业务类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:edit")
    @Log(title = "业务类型_付款单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypePayBill conBuTypePayBill) {
        return toAjax(conBuTypePayBillService.updateConBuTypePayBill(conBuTypePayBill));
    }

    /**
     * 变更业务类型_付款单
     */
    @ApiOperation(value = "变更业务类型_付款单", notes = "变更业务类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:change")
    @Log(title = "业务类型_付款单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypePayBill conBuTypePayBill) {
        return toAjax(conBuTypePayBillService.changeConBuTypePayBill(conBuTypePayBill));
    }

    /**
     * 删除业务类型_付款单
     */
    @ApiOperation(value = "删除业务类型_付款单", notes = "删除业务类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:remove")
    @Log(title = "业务类型_付款单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypePayBillService.deleteConBuTypePayBillByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_付款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypePayBill conBuTypePayBill) {
        return AjaxResult.success(conBuTypePayBillService.changeStatus(conBuTypePayBill));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:bu:type:pay:bill:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_付款单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypePayBill conBuTypePayBill) {
        conBuTypePayBill.setConfirmDate(new Date());
        conBuTypePayBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypePayBill.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypePayBillService.check(conBuTypePayBill));
    }


    @PostMapping("/getConBuTypePayBillList")
    @ApiOperation(value = "业务类型-付款单下拉列表", notes = "业务类型-付款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypePayBill.class))
    public AjaxResult getConBuTypePayBillList(){
        return AjaxResult.success(conBuTypePayBillService.getConBuTypePayBillList());
    }
}
