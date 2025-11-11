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

import com.platform.ems.plug.domain.ConDeductionTypeVendor;
import com.platform.ems.plug.service.IConDeductionTypeVendorService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 扣款类型_供应商Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/deduction/type/vendor")
@Api(tags = "扣款类型_供应商")
public class ConDeductionTypeVendorController extends BaseController {

    @Autowired
    private IConDeductionTypeVendorService conDeductionTypeVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询扣款类型_供应商列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询扣款类型_供应商列表", notes = "查询扣款类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeductionTypeVendor.class))
    public TableDataInfo list(@RequestBody ConDeductionTypeVendor conDeductionTypeVendor) {
        startPage(conDeductionTypeVendor);
        List<ConDeductionTypeVendor> list = conDeductionTypeVendorService.selectConDeductionTypeVendorList(conDeductionTypeVendor);
        return getDataTable(list);
    }

    /**
     * 导出扣款类型_供应商列表
     */
    @ApiOperation(value = "导出扣款类型_供应商列表", notes = "导出扣款类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDeductionTypeVendor conDeductionTypeVendor) throws IOException {
        List<ConDeductionTypeVendor> list = conDeductionTypeVendorService.selectConDeductionTypeVendorList(conDeductionTypeVendor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDeductionTypeVendor> util = new ExcelUtil<>(ConDeductionTypeVendor.class, dataMap);
        util.exportExcel(response, list, "扣款类型_供应商");
    }

    /**
     * 获取扣款类型_供应商详细信息
     */
    @ApiOperation(value = "获取扣款类型_供应商详细信息", notes = "获取扣款类型_供应商详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeductionTypeVendor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDeductionTypeVendorService.selectConDeductionTypeVendorById(sid));
    }

    /**
     * 新增扣款类型_供应商
     */
    @ApiOperation(value = "新增扣款类型_供应商", notes = "新增扣款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDeductionTypeVendor conDeductionTypeVendor) {
        return toAjax(conDeductionTypeVendorService.insertConDeductionTypeVendor(conDeductionTypeVendor));
    }

    /**
     * 修改扣款类型_供应商
     */
    @ApiOperation(value = "修改扣款类型_供应商", notes = "修改扣款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDeductionTypeVendor conDeductionTypeVendor) {
        return toAjax(conDeductionTypeVendorService.updateConDeductionTypeVendor(conDeductionTypeVendor));
    }

    /**
     * 变更扣款类型_供应商
     */
    @ApiOperation(value = "变更扣款类型_供应商", notes = "变更扣款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDeductionTypeVendor conDeductionTypeVendor) {
        return toAjax(conDeductionTypeVendorService.changeConDeductionTypeVendor(conDeductionTypeVendor));
    }

    /**
     * 删除扣款类型_供应商
     */
    @ApiOperation(value = "删除扣款类型_供应商", notes = "删除扣款类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDeductionTypeVendorService.deleteConDeductionTypeVendorByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDeductionTypeVendor conDeductionTypeVendor) {
        return AjaxResult.success(conDeductionTypeVendorService.changeStatus(conDeductionTypeVendor));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDeductionTypeVendor conDeductionTypeVendor) {
        conDeductionTypeVendor.setConfirmDate(new Date());
        conDeductionTypeVendor.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDeductionTypeVendor.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDeductionTypeVendorService.check(conDeductionTypeVendor));
    }

    @PostMapping("/getConDeductionTypeVendorList")
    @ApiOperation(value = "扣款类型-供应商下拉列表", notes = "扣款类型-供应商订单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeductionTypeVendor.class))
    public AjaxResult getConDeductionTypeVendorList() {
        return AjaxResult.success(conDeductionTypeVendorService.getConDeductionTypeVendorList());
    }
}
