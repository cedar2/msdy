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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConDeductionTypeCustomer;
import com.platform.ems.plug.service.IConDeductionTypeCustomerService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 扣款类型_客户Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/deduction/type/customer")
@Api(tags = "扣款类型_客户")
public class ConDeductionTypeCustomerController extends BaseController {

    @Autowired
    private IConDeductionTypeCustomerService conDeductionTypeCustomerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询扣款类型_客户列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询扣款类型_客户列表", notes = "查询扣款类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeductionTypeCustomer.class))
    public TableDataInfo list(@RequestBody ConDeductionTypeCustomer conDeductionTypeCustomer) {
        startPage(conDeductionTypeCustomer);
        List<ConDeductionTypeCustomer> list = conDeductionTypeCustomerService.selectConDeductionTypeCustomerList(conDeductionTypeCustomer);
        return getDataTable(list);
    }

    /**
     * 导出扣款类型_客户列表
     */
    @ApiOperation(value = "导出扣款类型_客户列表", notes = "导出扣款类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDeductionTypeCustomer conDeductionTypeCustomer) throws IOException {
        List<ConDeductionTypeCustomer> list = conDeductionTypeCustomerService.selectConDeductionTypeCustomerList(conDeductionTypeCustomer);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDeductionTypeCustomer> util = new ExcelUtil<>(ConDeductionTypeCustomer.class, dataMap);
        util.exportExcel(response, list, "扣款类型_客户");
    }

    /**
     * 获取扣款类型_客户详细信息
     */
    @ApiOperation(value = "获取扣款类型_客户详细信息", notes = "获取扣款类型_客户详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeductionTypeCustomer.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDeductionTypeCustomerService.selectConDeductionTypeCustomerById(sid));
    }

    /**
     * 新增扣款类型_客户
     */
    @ApiOperation(value = "新增扣款类型_客户", notes = "新增扣款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDeductionTypeCustomer conDeductionTypeCustomer) {
        return toAjax(conDeductionTypeCustomerService.insertConDeductionTypeCustomer(conDeductionTypeCustomer));
    }

    /**
     * 修改扣款类型_客户
     */
    @ApiOperation(value = "修改扣款类型_客户", notes = "修改扣款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDeductionTypeCustomer conDeductionTypeCustomer) {
        return toAjax(conDeductionTypeCustomerService.updateConDeductionTypeCustomer(conDeductionTypeCustomer));
    }

    /**
     * 变更扣款类型_客户
     */
    @ApiOperation(value = "变更扣款类型_客户", notes = "变更扣款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDeductionTypeCustomer conDeductionTypeCustomer) {
        return toAjax(conDeductionTypeCustomerService.changeConDeductionTypeCustomer(conDeductionTypeCustomer));
    }

    /**
     * 删除扣款类型_客户
     */
    @ApiOperation(value = "删除扣款类型_客户", notes = "删除扣款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDeductionTypeCustomerService.deleteConDeductionTypeCustomerByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDeductionTypeCustomer conDeductionTypeCustomer) {
        return AjaxResult.success(conDeductionTypeCustomerService.changeStatus(conDeductionTypeCustomer));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDeductionTypeCustomer conDeductionTypeCustomer) {
        conDeductionTypeCustomer.setConfirmDate(new Date());
        conDeductionTypeCustomer.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDeductionTypeCustomer.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDeductionTypeCustomerService.check(conDeductionTypeCustomer));
    }

    @PostMapping("/getConDeductionTypeCustomerList")
    @ApiOperation(value = "扣款类型-客户下拉列表", notes = "扣款类型-客户订单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeductionTypeCustomer.class))
    public AjaxResult getConDeductionTypeCustomerList() {
        return AjaxResult.success(conDeductionTypeCustomerService.getConDeductionTypeCustomerList());
    }
}
