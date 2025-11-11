package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConBuTypeVendorFundsFreeze;
import com.platform.ems.plug.service.IConBuTypeVendorFundsFreezeService;
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
 * 业务类型_供应商暂押款Controller
 *
 * @author linhongwei
 * @date 2021-09-27
 */
@RestController
@RequestMapping("/buType/vendor/funds/freeze")
@Api(tags = "业务类型_供应商暂押款")
public class ConBuTypeVendorFundsFreezeController extends BaseController {

    @Autowired
    private IConBuTypeVendorFundsFreezeService conBuTypeVendorFundsFreezeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_供应商暂押款列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_供应商暂押款列表", notes = "查询业务类型_供应商暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorFundsFreeze.class))
    public TableDataInfo list(@RequestBody ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        startPage(conBuTypeVendorFundsFreeze);
        List<ConBuTypeVendorFundsFreeze> list = conBuTypeVendorFundsFreezeService.selectConBuTypeVendorFundsFreezeList(conBuTypeVendorFundsFreeze);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_供应商暂押款列表
     */
    @ApiOperation(value = "导出业务类型_供应商暂押款列表", notes = "导出业务类型_供应商暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) throws IOException {
        List<ConBuTypeVendorFundsFreeze> list = conBuTypeVendorFundsFreezeService.selectConBuTypeVendorFundsFreezeList(conBuTypeVendorFundsFreeze);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeVendorFundsFreeze> util = new ExcelUtil<>(ConBuTypeVendorFundsFreeze.class, dataMap);
        util.exportExcel(response, list, "业务类型_供应商暂押款");
    }


    /**
     * 获取业务类型_供应商暂押款详细信息
     */
    @ApiOperation(value = "获取业务类型_供应商暂押款详细信息", notes = "获取业务类型_供应商暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorFundsFreeze.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeVendorFundsFreezeService.selectConBuTypeVendorFundsFreezeById(sid));
    }

    /**
     * 新增业务类型_供应商暂押款
     */
    @ApiOperation(value = "新增业务类型_供应商暂押款", notes = "新增业务类型_供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return toAjax(conBuTypeVendorFundsFreezeService.insertConBuTypeVendorFundsFreeze(conBuTypeVendorFundsFreeze));
    }

    /**
     * 修改业务类型_供应商暂押款
     */
    @ApiOperation(value = "修改业务类型_供应商暂押款", notes = "修改业务类型_供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return toAjax(conBuTypeVendorFundsFreezeService.updateConBuTypeVendorFundsFreeze(conBuTypeVendorFundsFreeze));
    }

    /**
     * 变更业务类型_供应商暂押款
     */
    @ApiOperation(value = "变更业务类型_供应商暂押款", notes = "变更业务类型_供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return toAjax(conBuTypeVendorFundsFreezeService.changeConBuTypeVendorFundsFreeze(conBuTypeVendorFundsFreeze));
    }

    /**
     * 删除业务类型_供应商暂押款
     */
    @ApiOperation(value = "删除业务类型_供应商暂押款", notes = "删除业务类型_供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeVendorFundsFreezeService.deleteConBuTypeVendorFundsFreezeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return AjaxResult.success(conBuTypeVendorFundsFreezeService.changeStatus(conBuTypeVendorFundsFreeze));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        conBuTypeVendorFundsFreeze.setConfirmDate(new Date());
        conBuTypeVendorFundsFreeze.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeVendorFundsFreeze.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeVendorFundsFreezeService.check(conBuTypeVendorFundsFreeze));
    }

    /**
     * 业务类型_供应商暂押款下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务类型_供应商暂押款下拉框列表", notes = "业务类型_供应商暂押款下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorFundsFreeze.class))
    public AjaxResult getList(@RequestBody ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return AjaxResult.success(conBuTypeVendorFundsFreezeService.getList(conBuTypeVendorFundsFreeze));
    }
}
