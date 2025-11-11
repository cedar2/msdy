package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.dto.request.InvRecordGoodsArrivalRequest;
import com.platform.ems.domain.dto.response.InvRecordGoodsArrivalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.InvRecordGoodsArrival;
import com.platform.ems.service.IInvRecordGoodsArrivalService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购到货台账Controller
 *
 * @author linhongwei
 * @date 2022-06-27
 */
@RestController
@RequestMapping("/inv/record/arrival")
@Api(tags = "采购到货台账")
public class InvRecordGoodsArrivalController extends BaseController {

    @Autowired
    private IInvRecordGoodsArrivalService invRecordGoodsArrivalService;
    @Autowired
    private ISystemDictDataService sysDictDataService;


    /**
     * 查询采购到货台账列表
     */
//    @PreAuthorize(hasPermi = "ems:arrival:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询采购到货台账列表", notes = "查询采购到货台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvRecordGoodsArrival.class))
    public TableDataInfo list(@RequestBody InvRecordGoodsArrival invRecordGoodsArrival) {
        startPage(invRecordGoodsArrival);
        List<InvRecordGoodsArrival> list = invRecordGoodsArrivalService.selectInvRecordGoodsArrivalList(invRecordGoodsArrival);
        return getDataTable(list);
    }

    @PostMapping("/report")
    @ApiOperation(value = "查询采购到货台账明细报表", notes = "查询采购到货台账明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvRecordGoodsArrival.class))
    public TableDataInfo report(@RequestBody InvRecordGoodsArrivalRequest invRecordGoodsArrival) {
        startPage(invRecordGoodsArrival);
        List<InvRecordGoodsArrivalResponse> list = invRecordGoodsArrivalService.getReport(invRecordGoodsArrival);
        return getDataTable(list);
    }
    /**
     * 导出采购到货台账列表
     */
//    @PreAuthorize(hasPermi = "ems:arrival:export")
    @Log(title = "采购到货台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购到货台账列表", notes = "导出采购到货台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvRecordGoodsArrival invRecordGoodsArrival) throws IOException {
        List<InvRecordGoodsArrival> list = invRecordGoodsArrivalService.selectInvRecordGoodsArrivalList(invRecordGoodsArrival);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvRecordGoodsArrival> util = new ExcelUtil<InvRecordGoodsArrival>(InvRecordGoodsArrival.class, dataMap);
        util.exportExcel(response, list, "采购到货台账");
    }


    /**
     * 获取采购到货台账详细信息
     */
    @ApiOperation(value = "获取采购到货台账详细信息", notes = "获取采购到货台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvRecordGoodsArrival.class))
//    @PreAuthorize(hasPermi = "ems:arrival:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long goodsArrivalSid) {
        if (goodsArrivalSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invRecordGoodsArrivalService.selectInvRecordGoodsArrivalById(goodsArrivalSid));
    }

    /**
     * 新增采购到货台账
     */
    @ApiOperation(value = "新增采购到货台账", notes = "新增采购到货台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:arrival:add")
    @Log(title = "采购到货台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvRecordGoodsArrival invRecordGoodsArrival) {
        return toAjax(invRecordGoodsArrivalService.insertInvRecordGoodsArrival(invRecordGoodsArrival));
    }

    /**
     * 修改采购到货台账
     */
    @ApiOperation(value = "修改/变更 采购到货台账", notes = "修改采购到货台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:arrival:edit")
    @Log(title = "采购到货台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody InvRecordGoodsArrival invRecordGoodsArrival) {
        return toAjax(invRecordGoodsArrivalService.updateInvRecordGoodsArrival(invRecordGoodsArrival));
    }


    /**
     * 删除采购到货台账
     */
    @ApiOperation(value = "删除采购到货台账", notes = "删除采购到货台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:arrival:remove")
    @Log(title = "采购到货台账", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> goodsArrivalSids) {
        if (CollectionUtils.isEmpty(goodsArrivalSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invRecordGoodsArrivalService.deleteInvRecordGoodsArrivalByIds(goodsArrivalSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购到货台账", businessType = BusinessType.UPDATE)
//    @PreAuthorize(hasPermi = "ems:arrival:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvRecordGoodsArrival invRecordGoodsArrival) {
        return AjaxResult.success(invRecordGoodsArrivalService.changeStatus(invRecordGoodsArrival));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:arrival:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购到货台账", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvRecordGoodsArrival invRecordGoodsArrival) {
        return toAjax(invRecordGoodsArrivalService.check(invRecordGoodsArrival));
    }

    @ApiOperation(value = "作废", notes = "作废")
//    @PreAuthorize(hasPermi = "ems:arrival:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购到货台账", businessType = BusinessType.CHECK)
    @PostMapping("/invalid")
    public AjaxResult invalid(@RequestBody InvRecordGoodsArrival invRecordGoodsArrival) {
        return toAjax(invRecordGoodsArrivalService.invalid(invRecordGoodsArrival));
    }
}
