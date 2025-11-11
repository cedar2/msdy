package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConFundsFreezeTypeCustomer;
import com.platform.ems.plug.service.IConFundsFreezeTypeCustomerService;
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
 * 暂押款类型_客户Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/freeze/type/customer")
@Api(tags = "暂押款类型_客户")
public class ConFundsFreezeTypeCustomerController extends BaseController {

    @Autowired
    private IConFundsFreezeTypeCustomerService conFundsFreezeTypeCustomerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询暂押款类型_客户列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询暂押款类型_客户列表", notes = "查询暂押款类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFundsFreezeTypeCustomer.class))
    public TableDataInfo list(@RequestBody ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        startPage(conFundsFreezeTypeCustomer);
        List<ConFundsFreezeTypeCustomer> list = conFundsFreezeTypeCustomerService.selectConFundsFreezeTypeCustomerList(conFundsFreezeTypeCustomer);
        return getDataTable(list);
    }

    /**
     * 导出暂押款类型_客户列表
     */
    @ApiOperation(value = "导出暂押款类型_客户列表", notes = "导出暂押款类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) throws IOException {
        List<ConFundsFreezeTypeCustomer> list = conFundsFreezeTypeCustomerService.selectConFundsFreezeTypeCustomerList(conFundsFreezeTypeCustomer);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConFundsFreezeTypeCustomer> util = new ExcelUtil<>(ConFundsFreezeTypeCustomer.class, dataMap);
        util.exportExcel(response, list, "暂押款类型_客户");
    }


    /**
     * 获取暂押款类型_客户详细信息
     */
    @ApiOperation(value = "获取暂押款类型_客户详细信息", notes = "获取暂押款类型_客户详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFundsFreezeTypeCustomer.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conFundsFreezeTypeCustomerService.selectConFundsFreezeTypeCustomerById(sid));
    }

    /**
     * 新增暂押款类型_客户
     */
    @ApiOperation(value = "新增暂押款类型_客户", notes = "新增暂押款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return toAjax(conFundsFreezeTypeCustomerService.insertConFundsFreezeTypeCustomer(conFundsFreezeTypeCustomer));
    }

    /**
     * 修改暂押款类型_客户
     */
    @ApiOperation(value = "修改暂押款类型_客户", notes = "修改暂押款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return toAjax(conFundsFreezeTypeCustomerService.updateConFundsFreezeTypeCustomer(conFundsFreezeTypeCustomer));
    }

    /**
     * 变更暂押款类型_客户
     */
    @ApiOperation(value = "变更暂押款类型_客户", notes = "变更暂押款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return toAjax(conFundsFreezeTypeCustomerService.changeConFundsFreezeTypeCustomer(conFundsFreezeTypeCustomer));
    }

    /**
     * 删除暂押款类型_客户
     */
    @ApiOperation(value = "删除暂押款类型_客户", notes = "删除暂押款类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conFundsFreezeTypeCustomerService.deleteConFundsFreezeTypeCustomerByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return AjaxResult.success(conFundsFreezeTypeCustomerService.changeStatus(conFundsFreezeTypeCustomer));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        conFundsFreezeTypeCustomer.setConfirmDate(new Date());
        conFundsFreezeTypeCustomer.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conFundsFreezeTypeCustomer.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conFundsFreezeTypeCustomerService.check(conFundsFreezeTypeCustomer));
    }

    /**
     * 暂押款类型_客户下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "暂押款类型_客户下拉框列表", notes = "暂押款类型_客户下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFundsFreezeTypeCustomer.class))
    public AjaxResult getList(@RequestBody ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return AjaxResult.success(conFundsFreezeTypeCustomerService.getList(conFundsFreezeTypeCustomer));
    }
}
