package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConDocTypeCustomerFundsFreeze;
import com.platform.ems.plug.service.IConDocTypeCustomerFundsFreezeService;
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
 * 单据类型_客户暂押款Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/customer/funds/freeze")
@Api(tags = "单据类型_客户暂押款")
public class ConDocTypeCustomerFundsFreezeController extends BaseController {

    @Autowired
    private IConDocTypeCustomerFundsFreezeService conDocTypeCustomerFundsFreezeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_客户暂押款列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_客户暂押款列表", notes = "查询单据类型_客户暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeCustomerFundsFreeze.class))
    public TableDataInfo list(@RequestBody ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        startPage(conDocTypeCustomerFundsFreeze);
        List<ConDocTypeCustomerFundsFreeze> list = conDocTypeCustomerFundsFreezeService.selectConDocTypeCustomerFundsFreezeList(conDocTypeCustomerFundsFreeze);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_客户暂押款列表
     */
    @ApiOperation(value = "导出单据类型_客户暂押款列表", notes = "导出单据类型_客户暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) throws IOException {
        List<ConDocTypeCustomerFundsFreeze> list = conDocTypeCustomerFundsFreezeService.selectConDocTypeCustomerFundsFreezeList(conDocTypeCustomerFundsFreeze);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeCustomerFundsFreeze> util = new ExcelUtil<>(ConDocTypeCustomerFundsFreeze.class, dataMap);
        util.exportExcel(response, list, "单据类型_客户暂押款");
    }


    /**
     * 获取单据类型_客户暂押款详细信息
     */
    @ApiOperation(value = "获取单据类型_客户暂押款详细信息", notes = "获取单据类型_客户暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeCustomerFundsFreeze.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeCustomerFundsFreezeService.selectConDocTypeCustomerFundsFreezeById(sid));
    }

    /**
     * 新增单据类型_客户暂押款
     */
    @ApiOperation(value = "新增单据类型_客户暂押款", notes = "新增单据类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return toAjax(conDocTypeCustomerFundsFreezeService.insertConDocTypeCustomerFundsFreeze(conDocTypeCustomerFundsFreeze));
    }

    /**
     * 修改单据类型_客户暂押款
     */
    @ApiOperation(value = "修改单据类型_客户暂押款", notes = "修改单据类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return toAjax(conDocTypeCustomerFundsFreezeService.updateConDocTypeCustomerFundsFreeze(conDocTypeCustomerFundsFreeze));
    }

    /**
     * 变更单据类型_客户暂押款
     */
    @ApiOperation(value = "变更单据类型_客户暂押款", notes = "变更单据类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return toAjax(conDocTypeCustomerFundsFreezeService.changeConDocTypeCustomerFundsFreeze(conDocTypeCustomerFundsFreeze));
    }

    /**
     * 删除单据类型_客户暂押款
     */
    @ApiOperation(value = "删除单据类型_客户暂押款", notes = "删除单据类型_客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeCustomerFundsFreezeService.deleteConDocTypeCustomerFundsFreezeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return AjaxResult.success(conDocTypeCustomerFundsFreezeService.changeStatus(conDocTypeCustomerFundsFreeze));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        conDocTypeCustomerFundsFreeze.setConfirmDate(new Date());
        conDocTypeCustomerFundsFreeze.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeCustomerFundsFreeze.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeCustomerFundsFreezeService.check(conDocTypeCustomerFundsFreeze));
    }

    /**
     * 单据类型_客户暂押款下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_客户暂押款下拉框列表", notes = "单据类型_客户暂押款下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeCustomerFundsFreeze.class))
    public AjaxResult getList(@RequestBody ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return AjaxResult.success(conDocTypeCustomerFundsFreezeService.getList(conDocTypeCustomerFundsFreeze));
    }
}
