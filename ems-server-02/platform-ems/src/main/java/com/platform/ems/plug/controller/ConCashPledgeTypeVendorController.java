package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConCashPledgeTypeVendor;
import com.platform.ems.plug.service.IConCashPledgeTypeVendorService;
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
 * 押金类型_供应商Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/pledge/type/vendor")
@Api(tags = "押金类型_供应商")
public class ConCashPledgeTypeVendorController extends BaseController {

    @Autowired
    private IConCashPledgeTypeVendorService conCashPledgeTypeVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询押金类型_供应商列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询押金类型_供应商列表", notes = "查询押金类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCashPledgeTypeVendor.class))
    public TableDataInfo list(@RequestBody ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        startPage(conCashPledgeTypeVendor);
        List<ConCashPledgeTypeVendor> list = conCashPledgeTypeVendorService.selectConCashPledgeTypeVendorList(conCashPledgeTypeVendor);
        return getDataTable(list);
    }

    /**
     * 导出押金类型_供应商列表
     */
    @ApiOperation(value = "导出押金类型_供应商列表", notes = "导出押金类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCashPledgeTypeVendor conCashPledgeTypeVendor) throws IOException {
        List<ConCashPledgeTypeVendor> list = conCashPledgeTypeVendorService.selectConCashPledgeTypeVendorList(conCashPledgeTypeVendor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConCashPledgeTypeVendor> util = new ExcelUtil<>(ConCashPledgeTypeVendor.class, dataMap);
        util.exportExcel(response, list, "押金类型_供应商");
    }


    /**
     * 获取押金类型_供应商详细信息
     */
    @ApiOperation(value = "获取押金类型_供应商详细信息", notes = "获取押金类型_供应商详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCashPledgeTypeVendor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conCashPledgeTypeVendorService.selectConCashPledgeTypeVendorById(sid));
    }

    /**
     * 新增押金类型_供应商
     */
    @ApiOperation(value = "新增押金类型_供应商", notes = "新增押金类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return toAjax(conCashPledgeTypeVendorService.insertConCashPledgeTypeVendor(conCashPledgeTypeVendor));
    }

    /**
     * 修改押金类型_供应商
     */
    @ApiOperation(value = "修改押金类型_供应商", notes = "修改押金类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return toAjax(conCashPledgeTypeVendorService.updateConCashPledgeTypeVendor(conCashPledgeTypeVendor));
    }

    /**
     * 变更押金类型_供应商
     */
    @ApiOperation(value = "变更押金类型_供应商", notes = "变更押金类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return toAjax(conCashPledgeTypeVendorService.changeConCashPledgeTypeVendor(conCashPledgeTypeVendor));
    }

    /**
     * 删除押金类型_供应商
     */
    @ApiOperation(value = "删除押金类型_供应商", notes = "删除押金类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCashPledgeTypeVendorService.deleteConCashPledgeTypeVendorByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return AjaxResult.success(conCashPledgeTypeVendorService.changeStatus(conCashPledgeTypeVendor));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        conCashPledgeTypeVendor.setConfirmDate(new Date());
        conCashPledgeTypeVendor.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCashPledgeTypeVendor.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCashPledgeTypeVendorService.check(conCashPledgeTypeVendor));
    }

    /**
     * 押金类型_供应商下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "押金类型_供应商下拉框列表", notes = "押金类型_供应商下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCashPledgeTypeVendor.class))
    public AjaxResult getList(@RequestBody ConCashPledgeTypeVendor conCashPledgeTypeVendor) {
        return AjaxResult.success(conCashPledgeTypeVendorService.getList(conCashPledgeTypeVendor));
    }
}
