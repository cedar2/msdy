package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
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
import com.platform.ems.plug.domain.ConAdjustTypeCustomer;
import com.platform.ems.plug.service.IConAdjustTypeCustomerService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 调账类型_客户Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/adjust/type/customer")
@Api(tags = "调账类型_客户")
public class ConAdjustTypeCustomerController extends BaseController {

    @Autowired
    private IConAdjustTypeCustomerService conAdjustTypeCustomerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询调账类型_客户列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询调账类型_客户列表", notes = "查询调账类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdjustTypeCustomer.class))
    public TableDataInfo list(@RequestBody ConAdjustTypeCustomer conAdjustTypeCustomer) {
        startPage(conAdjustTypeCustomer);
        List<ConAdjustTypeCustomer> list = conAdjustTypeCustomerService.selectConAdjustTypeCustomerList(conAdjustTypeCustomer);
        return getDataTable(list);
    }

    /**
     * 导出调账类型_客户列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:export")
    @Log(title = "调账类型_客户", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出调账类型_客户列表", notes = "导出调账类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConAdjustTypeCustomer conAdjustTypeCustomer) throws IOException {
        List<ConAdjustTypeCustomer> list = conAdjustTypeCustomerService.selectConAdjustTypeCustomerList(conAdjustTypeCustomer);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConAdjustTypeCustomer> util = new ExcelUtil<>(ConAdjustTypeCustomer.class, dataMap);
        util.exportExcel(response, list, "调账类型_客户");
    }

    /**
     * 新增调账类型_客户
     */
    @ApiOperation(value = "新增调账类型_客户", notes = "新增调账类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:add")
    @Log(title = "调账类型_客户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConAdjustTypeCustomer conAdjustTypeCustomer) {
        return toAjax(conAdjustTypeCustomerService.insertConAdjustTypeCustomer(conAdjustTypeCustomer));
    }

    /**
     * 修改调账类型_客户
     */
    @ApiOperation(value = "修改调账类型_客户", notes = "修改调账类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:edit")
    @Log(title = "调账类型_客户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConAdjustTypeCustomer conAdjustTypeCustomer) {
        return toAjax(conAdjustTypeCustomerService.updateConAdjustTypeCustomer(conAdjustTypeCustomer));
    }

    /**
     * 变更调账类型_客户
     */
    @ApiOperation(value = "变更调账类型_客户", notes = "变更调账类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:change")
    @Log(title = "调账类型_客户", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConAdjustTypeCustomer conAdjustTypeCustomer) {
        return toAjax(conAdjustTypeCustomerService.changeConAdjustTypeCustomer(conAdjustTypeCustomer));
    }

    /**
     * 删除调账类型_客户
     */
    @ApiOperation(value = "删除调账类型_客户", notes = "删除调账类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:remove")
    @Log(title = "调账类型_客户", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conAdjustTypeCustomerService.deleteConAdjustTypeCustomerByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "调账类型_客户", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConAdjustTypeCustomer conAdjustTypeCustomer) {
        return AjaxResult.success(conAdjustTypeCustomerService.changeStatus(conAdjustTypeCustomer));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:adjust:type:customer:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "调账类型_客户", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConAdjustTypeCustomer conAdjustTypeCustomer) {
        conAdjustTypeCustomer.setConfirmDate(new Date());
        conAdjustTypeCustomer.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conAdjustTypeCustomer.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conAdjustTypeCustomerService.check(conAdjustTypeCustomer));
    }

    /**
     * 调账类别下拉框列表
     */
    @PostMapping("/getConAdjustTypeCustomerList")
    @ApiOperation(value = "调账类别下拉框列表", notes = "调账类别下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdjustTypeCustomer.class))
    public AjaxResult getConAdjustTypeCustomerList() {
        return AjaxResult.success(conAdjustTypeCustomerService.getConAdjustTypeCustomerList());
    }

}
