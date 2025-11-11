package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConBuTypeCustomerFundsFreeze;
import com.platform.ems.plug.service.IConBuTypeCustomerFundsFreezeService;
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
 * 业务类型_客户暂押款Controller
 *
 * @author linhongwei
 * @date 2021-09-27
 */
@RestController
@RequestMapping("/buType/customer/funds/freeze")
@Api(tags = "业务类型_客户暂押款")
public class ConBuTypeCustomerFundsFreezeController extends BaseController {

    @Autowired
    private IConBuTypeCustomerFundsFreezeService conBuTypeCustomerFundsFreezeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_客户暂押款列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_客户暂押款列表", notes = "查询业务类型_客户暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerFundsFreeze.class))
    public TableDataInfo list(@RequestBody ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        startPage(conBuTypeCustomerFundsFreeze);
        List<ConBuTypeCustomerFundsFreeze> list = conBuTypeCustomerFundsFreezeService.selectConBuTypeCustomerFundsFreezeList(conBuTypeCustomerFundsFreeze);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_客户暂押款列表
     */
    @ApiOperation(value = "导出业务类型_客户暂押款列表", notes = "导出业务类型_客户暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) throws IOException {
        List<ConBuTypeCustomerFundsFreeze> list = conBuTypeCustomerFundsFreezeService.selectConBuTypeCustomerFundsFreezeList(conBuTypeCustomerFundsFreeze);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeCustomerFundsFreeze> util = new ExcelUtil<>(ConBuTypeCustomerFundsFreeze.class, dataMap);
        util.exportExcel(response, list, "业务类型_客户暂押款");
    }

    /**
     * 获取业务类型_客户暂押款详细信息
     */
    @ApiOperation(value = "获取业务类型_客户暂押款详细信息", notes = "获取业务类型_客户暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerFundsFreeze.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeCustomerFundsFreezeService.selectConBuTypeCustomerFundsFreezeById(sid));
    }

    /**
     * 新增业务类型_客户暂押款
     */
    @ApiOperation(value = "新增业务类型_客户暂押款", notes = "新增业务类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return toAjax(conBuTypeCustomerFundsFreezeService.insertConBuTypeCustomerFundsFreeze(conBuTypeCustomerFundsFreeze));
    }

    /**
     * 修改业务类型_客户暂押款
     */
    @ApiOperation(value = "修改业务类型_客户暂押款", notes = "修改业务类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return toAjax(conBuTypeCustomerFundsFreezeService.updateConBuTypeCustomerFundsFreeze(conBuTypeCustomerFundsFreeze));
    }

    /**
     * 变更业务类型_客户暂押款
     */
    @ApiOperation(value = "变更业务类型_客户暂押款", notes = "变更业务类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return toAjax(conBuTypeCustomerFundsFreezeService.changeConBuTypeCustomerFundsFreeze(conBuTypeCustomerFundsFreeze));
    }

    /**
     * 删除业务类型_客户暂押款
     */
    @ApiOperation(value = "删除业务类型_客户暂押款", notes = "删除业务类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeCustomerFundsFreezeService.deleteConBuTypeCustomerFundsFreezeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return AjaxResult.success(conBuTypeCustomerFundsFreezeService.changeStatus(conBuTypeCustomerFundsFreeze));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        conBuTypeCustomerFundsFreeze.setConfirmDate(new Date());
        conBuTypeCustomerFundsFreeze.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeCustomerFundsFreeze.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeCustomerFundsFreezeService.check(conBuTypeCustomerFundsFreeze));
    }

    /**
     * 业务类型_客户暂押款下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务类型_客户暂押款下拉框列表", notes = "业务类型_客户暂押款下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerFundsFreeze.class))
    public AjaxResult getList(@RequestBody ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return AjaxResult.success(conBuTypeCustomerFundsFreezeService.getList(conBuTypeCustomerFundsFreeze));
    }
}
