package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConCashPledgeTypeCustomer;
import com.platform.ems.plug.service.IConCashPledgeTypeCustomerService;
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
 * 押金类型_客户Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/pledge/type/customer")
@Api(tags = "押金类型_客户")
public class ConCashPledgeTypeCustomerController extends BaseController {

    @Autowired
    private IConCashPledgeTypeCustomerService conCashPledgeTypeCustomerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询押金类型_客户列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询押金类型_客户列表", notes = "查询押金类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCashPledgeTypeCustomer.class))
    public TableDataInfo list(@RequestBody ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        startPage(conCashPledgeTypeCustomer);
        List<ConCashPledgeTypeCustomer> list = conCashPledgeTypeCustomerService.selectConCashPledgeTypeCustomerList(conCashPledgeTypeCustomer);
        return getDataTable(list);
    }

    /**
     * 导出押金类型_客户列表
     */
    @ApiOperation(value = "导出押金类型_客户列表", notes = "导出押金类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) throws IOException {
        List<ConCashPledgeTypeCustomer> list = conCashPledgeTypeCustomerService.selectConCashPledgeTypeCustomerList(conCashPledgeTypeCustomer);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConCashPledgeTypeCustomer> util = new ExcelUtil<>(ConCashPledgeTypeCustomer.class, dataMap);
        util.exportExcel(response, list, "押金类型_客户");
    }


    /**
     * 获取押金类型_客户详细信息
     */
    @ApiOperation(value = "获取押金类型_客户详细信息", notes = "获取押金类型_客户详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCashPledgeTypeCustomer.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conCashPledgeTypeCustomerService.selectConCashPledgeTypeCustomerById(sid));
    }

    /**
     * 新增押金类型_客户
     */
    @ApiOperation(value = "新增押金类型_客户", notes = "新增押金类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return toAjax(conCashPledgeTypeCustomerService.insertConCashPledgeTypeCustomer(conCashPledgeTypeCustomer));
    }

    /**
     * 修改押金类型_客户
     */
    @ApiOperation(value = "修改押金类型_客户", notes = "修改押金类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return toAjax(conCashPledgeTypeCustomerService.updateConCashPledgeTypeCustomer(conCashPledgeTypeCustomer));
    }

    /**
     * 变更押金类型_客户
     */
    @ApiOperation(value = "变更押金类型_客户", notes = "变更押金类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return toAjax(conCashPledgeTypeCustomerService.changeConCashPledgeTypeCustomer(conCashPledgeTypeCustomer));
    }

    /**
     * 删除押金类型_客户
     */
    @ApiOperation(value = "删除押金类型_客户", notes = "删除押金类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCashPledgeTypeCustomerService.deleteConCashPledgeTypeCustomerByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return AjaxResult.success(conCashPledgeTypeCustomerService.changeStatus(conCashPledgeTypeCustomer));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        conCashPledgeTypeCustomer.setConfirmDate(new Date());
        conCashPledgeTypeCustomer.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCashPledgeTypeCustomer.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCashPledgeTypeCustomerService.check(conCashPledgeTypeCustomer));
    }

    /**
     * 押金类型_客户下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "押金类型_客户下拉框列表", notes = "押金类型_客户下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCashPledgeTypeCustomer.class))
    public AjaxResult getList(@RequestBody ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return AjaxResult.success(conCashPledgeTypeCustomerService.getList(conCashPledgeTypeCustomer));
    }
}
