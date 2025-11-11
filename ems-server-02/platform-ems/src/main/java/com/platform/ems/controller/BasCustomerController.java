package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.mapper.BasCustomerBrandMapper;
import com.platform.ems.service.IBasCustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 客户档案Controller
 *
 * @author qhq
 * @date 2021-03-22
 */
@RestController
@RequestMapping("/customer")
@Api(tags = "客户档案")
public class BasCustomerController extends BaseController {

    @Autowired
    private IBasCustomerService basCustomerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasCustomerBrandMapper basCustomerBrandMapper;

    /**
     * 查询客户档案列表
     */
    @PostMapping("/list")
    @PreAuthorize(hasPermi = "ems:customer:list")
    @ApiOperation(value = "查询客户档案列表", notes = "查询客户档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomer.class))
    public TableDataInfo list(@RequestBody BasCustomer basCustomer) {
        startPage(basCustomer);
        List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer);
        return getDataTable(list);
    }

    /**
     * 导出客户档案列表
     */
    @PreAuthorize(hasPermi = "ems:customer:export")
    @Log(title = "客户档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出客户档案列表", notes = "导出客户档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasCustomer basCustomer) throws IOException {
        List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasCustomer> util = new ExcelUtil<>(BasCustomer.class, dataMap);
        util.exportExcel(response, list, "客户");
    }

    /**
     * 获取客户档案详细信息
     */
    @PreAuthorize(hasPermi = "ems:customer:query")
    @ApiOperation(value = "获取客户档案详细信息", notes = "获取客户档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomer.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerSid) {
        return AjaxResult.success(basCustomerService.selectBasCustomerById(customerSid));
    }

    /**
     * 新增客户档案
     */
    @PreAuthorize(hasPermi = "ems:customer:add")
    @ApiOperation(value = "新增客户档案", notes = "新增客户档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasCustomer basCustomer) {
        int row = basCustomerService.insertBasCustomer(basCustomer);
        return AjaxResult.success(basCustomer);
    }

    /**
     * 修改客户档案
     */
    @PreAuthorize(hasPermi = "ems:customer:edit")
    @ApiOperation(value = "修改客户档案", notes = "修改客户档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasCustomer basCustomer) {
        return toAjax(basCustomerService.updateBasCustomer(basCustomer));
    }

    /**
     * 删除客户档案
     */
    @PreAuthorize(hasPermi = "ems:customer:remove")
    @ApiOperation(value = "删除客户档案", notes = "删除客户档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> customerSids) {
        return toAjax(basCustomerService.deleteBasCustomerByIds(customerSids));
    }

    /**
     * 批量启用停用
     */
    @PreAuthorize(hasPermi = "ems:customer:enbleordisable")
    @ApiOperation(value = "批量启用停用客户档案", notes = "批量启用停用客户档案")
    @Log(title = "客户档案", businessType = BusinessType.ENBLEORDISABLE)
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/status")
    public AjaxResult editStatus(@RequestBody BasCustomer basCustomer) {
        return toAjax(basCustomerService.editStatus(basCustomer));
    }

    /**
     * 批量确认
     */
    @PreAuthorize(hasPermi = "ems:customer:check")
    @ApiOperation(value = "批量确认客户档案", notes = "批量确认客户档案")
    @Log(title = "客户档案", businessType = BusinessType.HANDLE)
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/handleStatus")
    public AjaxResult editHandleStatus(@RequestBody BasCustomer basCustomer) {
        return toAjax(basCustomerService.editHandleStatus(basCustomer));
    }

    /**
     * 查询客户档案sid及名称，用于下拉框
     */
    @PostMapping("/getCustomerList")
    @ApiOperation("获取客户档案SID及名称、简称，用于下拉框,前端不用传参，默认查询启用和已确认数据")
    public AjaxResult getCustomerList() {
        return AjaxResult.success(basCustomerService.getCustomerList(new BasCustomer().setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS)));
    }

    /**
     * 查询客户档案sid及名称，用于下拉框
     */
    @PostMapping("/getList")
    @ApiOperation("获取客户档案SID及名称、简称，用于下拉框，前端需要按需求传参数过滤数据")
    public AjaxResult getList(@RequestBody BasCustomer basCustomer) {
        return AjaxResult.success(basCustomerService.getCustomerList(basCustomer));
    }


    /**
     * 查询客户品牌sid及名称，用于下拉框
     */
    @PostMapping("/getCustomerBrandList")
    @ApiOperation("获取客户品牌")
    public AjaxResult getCustomerBrandList(@RequestBody BasCustomer basCustomer) {
        BasCustomerBrand brand = new BasCustomerBrand();
        brand.setCustomerSid(basCustomer.getCustomerSid());
        List<BasCustomerBrand> response = basCustomerBrandMapper.selectBasCustomerBrandList(brand);
        response = response.stream().filter(item -> ConstantsEms.ENABLE_STATUS.equals(item.getStatus())).collect(Collectors.toList());
        return AjaxResult.success(response);
    }


    @PostMapping("/getCustomerBrandMarkList")
    @ApiOperation(value = "客户档案品牌品标信息下拉框列表", notes = "客户档案品牌品标信息下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomerBrandMark.class))
    public List<BasCustomerBrandMark> getCustomerBrandMarkList(Long brandSid) {
        if (brandSid == null) {
            throw new CheckedException("参数缺失");
        }
        return basCustomerService.getCustomerBrandMarkList(brandSid);
    }

    /**
     * 设置我方跟单员
     */
    @PreAuthorize(hasPermi = "ems:customer:setOperator")
    @ApiOperation(value = "设置我方跟单员", notes = "设置我方跟单员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperator")
    public AjaxResult setOperator(@RequestBody BasCustomer basCustomer) {
        return toAjax(basCustomerService.setOperator(basCustomer));
    }

    /**
     * 设置客方业务员
     */
    @PreAuthorize(hasPermi = "ems:customer:setOperatorCustomer")
    @ApiOperation(value = "设置客方业务员", notes = "设置客方业务员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperatorCustomer")
    public AjaxResult setOperatorCustomer(@RequestBody BasCustomer basCustomer) {
        return toAjax(basCustomerService.setOperatorCustomer(basCustomer));
    }

    /**
     * 设置合作状态
     */
    @ApiOperation(value = "设置合作状态", notes = "设置合作状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setCooperate")
    public AjaxResult setCooperate(@RequestBody BasCustomer basCustomer) {
        return toAjax(basCustomerService.setCooperate(basCustomer));
    }

    /**
     * 查询客户档案联系人列表
     */
    @PostMapping("/addr/list")
    @ApiOperation(value = "查询客户档案联系人列表", notes = "查询客户档案联系人列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomerAddr.class))
    public TableDataInfo addrList(@RequestBody BasCustomer basCustomer) {
        BasCustomerAddr addr = new BasCustomerAddr();
        BeanUtil.copyProperties(basCustomer, addr);
        startPage(addr);
        List<BasCustomerAddr> list = basCustomerService.selectBasCustomerAddrList(addr);
        return getDataTable(list);
    }
}
