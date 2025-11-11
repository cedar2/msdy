package com.platform.ems.plug.controller;

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
import com.platform.ems.plug.domain.ConFundsFreezeTypeVendor;
import com.platform.ems.plug.service.IConFundsFreezeTypeVendorService;
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
 * 暂押款类型_供应商Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/freeze/type/vendor")
@Api(tags = "暂押款类型_供应商")
public class ConFundsFreezeTypeVendorController extends BaseController {

    @Autowired
    private IConFundsFreezeTypeVendorService conFundsFreezeTypeVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询暂押款类型_供应商列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询暂押款类型_供应商列表", notes = "查询暂押款类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFundsFreezeTypeVendor.class))
    public TableDataInfo list(@RequestBody ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        startPage(conFundsFreezeTypeVendor);
        List<ConFundsFreezeTypeVendor> list = conFundsFreezeTypeVendorService.selectConFundsFreezeTypeVendorList(conFundsFreezeTypeVendor);
        return getDataTable(list);
    }

    /**
     * 导出暂押款类型_供应商列表
     */
    @ApiOperation(value = "导出暂押款类型_供应商列表", notes = "导出暂押款类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) throws IOException {
        List<ConFundsFreezeTypeVendor> list = conFundsFreezeTypeVendorService.selectConFundsFreezeTypeVendorList(conFundsFreezeTypeVendor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConFundsFreezeTypeVendor> util = new ExcelUtil<>(ConFundsFreezeTypeVendor.class, dataMap);
        util.exportExcel(response, list, "暂押款类型_供应商");
    }


    /**
     * 获取暂押款类型_供应商详细信息
     */
    @ApiOperation(value = "获取暂押款类型_供应商详细信息", notes = "获取暂押款类型_供应商详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFundsFreezeTypeVendor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conFundsFreezeTypeVendorService.selectConFundsFreezeTypeVendorById(sid));
    }

    /**
     * 新增暂押款类型_供应商
     */
    @ApiOperation(value = "新增暂押款类型_供应商", notes = "新增暂押款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return toAjax(conFundsFreezeTypeVendorService.insertConFundsFreezeTypeVendor(conFundsFreezeTypeVendor));
    }

    /**
     * 修改暂押款类型_供应商
     */
    @ApiOperation(value = "修改暂押款类型_供应商", notes = "修改暂押款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return toAjax(conFundsFreezeTypeVendorService.updateConFundsFreezeTypeVendor(conFundsFreezeTypeVendor));
    }

    /**
     * 变更暂押款类型_供应商
     */
    @ApiOperation(value = "变更暂押款类型_供应商", notes = "变更暂押款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return toAjax(conFundsFreezeTypeVendorService.changeConFundsFreezeTypeVendor(conFundsFreezeTypeVendor));
    }

    /**
     * 删除暂押款类型_供应商
     */
    @ApiOperation(value = "删除暂押款类型_供应商", notes = "删除暂押款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conFundsFreezeTypeVendorService.deleteConFundsFreezeTypeVendorByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return AjaxResult.success(conFundsFreezeTypeVendorService.changeStatus(conFundsFreezeTypeVendor));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        conFundsFreezeTypeVendor.setConfirmDate(new Date());
        conFundsFreezeTypeVendor.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conFundsFreezeTypeVendor.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conFundsFreezeTypeVendorService.check(conFundsFreezeTypeVendor));
    }

    /**
     * 暂押款类型_供应商下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "暂押款类型_供应商下拉框列表", notes = "暂押款类型_供应商下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFundsFreezeTypeVendor.class))
    public AjaxResult getList(@RequestBody ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return AjaxResult.success(conFundsFreezeTypeVendorService.getList(conFundsFreezeTypeVendor));
    }
}
