package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PayWorkattendRecordItem;
import com.platform.ems.service.IPayWorkattendRecordItemService;
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
import java.util.List;
import java.util.Map;

/**
 * 考勤信息-明细Controller
 *
 * @author linhongwei
 * @date 2021-11-12
 */
@RestController
@RequestMapping("/workattend/item")
@Api(tags = "考勤信息-明细")
public class PayWorkattendRecordItemController extends BaseController {

    @Autowired
    private IPayWorkattendRecordItemService payWorkattendRecordItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询考勤信息-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询考勤信息-明细列表", notes = "查询考勤信息-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayWorkattendRecordItem.class))
    public TableDataInfo list(@RequestBody PayWorkattendRecordItem payWorkattendRecordItem) {
        startPage(payWorkattendRecordItem);
        List<PayWorkattendRecordItem> list = payWorkattendRecordItemService.selectPayWorkattendRecordItemList(payWorkattendRecordItem);
        return getDataTable(list);
    }

    /**
     * 导出考勤信息-明细列表
     */
    @Log(title = "考勤信息-明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出考勤信息-明细列表", notes = "导出考勤信息-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PayWorkattendRecordItem payWorkattendRecordItem) throws IOException {
        List<PayWorkattendRecordItem> list = payWorkattendRecordItemService.selectPayWorkattendRecordItemList(payWorkattendRecordItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayWorkattendRecordItem> util = new ExcelUtil<>(PayWorkattendRecordItem.class, dataMap);
        util.exportExcel(response, list, "考勤信息-明细");
    }


    /**
     * 获取考勤信息-明细详细信息
     */
    @ApiOperation(value = "获取考勤信息-明细详细信息", notes = "获取考勤信息-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayWorkattendRecordItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordItemSid) {
        if (recordItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payWorkattendRecordItemService.selectPayWorkattendRecordItemById(recordItemSid));
    }

    /**
     * 新增考勤信息-明细
     */
    @ApiOperation(value = "新增考勤信息-明细", notes = "新增考勤信息-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "考勤信息-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PayWorkattendRecordItem payWorkattendRecordItem) {
        return toAjax(payWorkattendRecordItemService.insertPayWorkattendRecordItem(payWorkattendRecordItem));
    }

    /**
     * 修改考勤信息-明细
     */
    @ApiOperation(value = "修改考勤信息-明细", notes = "修改考勤信息-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "考勤信息-明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PayWorkattendRecordItem payWorkattendRecordItem) {
        return toAjax(payWorkattendRecordItemService.updatePayWorkattendRecordItem(payWorkattendRecordItem));
    }

    /**
     * 变更考勤信息-明细
     */
    @ApiOperation(value = "变更考勤信息-明细", notes = "变更考勤信息-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "考勤信息-明细", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PayWorkattendRecordItem payWorkattendRecordItem) {
        return toAjax(payWorkattendRecordItemService.changePayWorkattendRecordItem(payWorkattendRecordItem));
    }

    /**
     * 删除考勤信息-明细
     */
    @ApiOperation(value = "删除考勤信息-明细", notes = "删除考勤信息-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:item:remove")
    @Log(title = "考勤信息-明细", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> recordItemSids) {
        if (CollectionUtils.isEmpty(recordItemSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payWorkattendRecordItemService.deletePayWorkattendRecordItemByIds(recordItemSids));
    }

}
