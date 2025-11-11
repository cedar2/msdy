package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.StaffCompleteSummary;
import com.platform.ems.domain.StaffCompleteSummaryTable;
import com.platform.ems.domain.StepFinishDetail;
import com.platform.ems.domain.dto.request.ManProcessStepCompleteRecordTableRequest;
import com.platform.ems.domain.dto.response.ManProcessStepCompleteRecordTableResponse;
import com.platform.ems.mapper.ManProcessStepCompleteRecordItemMapper;
import com.platform.ems.service.IManProcessStepCompleteRecordItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ManProcessStepCompleteRecord;
import com.platform.ems.service.IManProcessStepCompleteRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品道序完成量台账-主Controller
 *
 * @author chenkw
 * @date 2022-10-20
 */
@RestController
@RequestMapping("/man/process/step/complete/record")
@Api(tags = "商品道序完成量台账-主")
public class ManProcessStepCompleteRecordController extends BaseController {

    @Autowired
    private IManProcessStepCompleteRecordService manProcessStepCompleteRecordService;
    @Autowired
    private IManProcessStepCompleteRecordItemService manProcessStepCompleteRecordItemService;
    @Autowired
    private ManProcessStepCompleteRecordItemMapper manProcessStepCompleteRecordItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询商品道序完成量台账-主列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品道序完成量台账-主列表", notes = "查询商品道序完成量台账-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessStepCompleteRecord.class))
    public TableDataInfo list(@RequestBody ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        startPage(manProcessStepCompleteRecord);
        List<ManProcessStepCompleteRecord> list = manProcessStepCompleteRecordService.selectManProcessStepCompleteRecordList(manProcessStepCompleteRecord);
        return getDataTable(list);
    }

    /**
     * 导出商品道序完成量台账-主列表
     */
    @PostMapping("/export")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "商品道序完成量台账-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品道序完成量台账-主列表", notes = "导出商品道序完成量台账-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, ManProcessStepCompleteRecord manProcessStepCompleteRecord) throws IOException {
        List<ManProcessStepCompleteRecord> list = manProcessStepCompleteRecordService.selectManProcessStepCompleteRecordList(manProcessStepCompleteRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProcessStepCompleteRecord> util = new ExcelUtil<>(ManProcessStepCompleteRecord.class, dataMap);
        util.exportExcel(response, list, "道序完成台账");
    }

    /**
     * 获取商品道序完成量台账-主详细信息
     */
    @PostMapping("/getInfo")
    @ApiOperation(value = "获取商品道序完成量台账-主详细信息", notes = "获取商品道序完成量台账-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessStepCompleteRecord.class))
    public AjaxResult getInfo(Long stepCompleteRecordSid) {
        if (stepCompleteRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProcessStepCompleteRecordService.selectManProcessStepCompleteRecordById(stepCompleteRecordSid));
    }

    /**
     * 复制商品道序完成量台账-主详细信息
     */
    @PostMapping("/copy")
    @ApiOperation(value = "复制商品道序完成量台账-主详细信息", notes = "复制商品道序完成量台账-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessStepCompleteRecord.class))
    public AjaxResult copy(Long stepCompleteRecordSid) {
        if (stepCompleteRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProcessStepCompleteRecordService.copyManProcessStepCompleteRecordById(stepCompleteRecordSid));
    }

    /**
     * 校验台账唯一性
     */
    @PostMapping("/verify/unique")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "校验台账唯一性", notes = "校验台账唯一性")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult verifyUnique(@RequestBody @Valid ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        return AjaxResult.success(manProcessStepCompleteRecordService.verifyUnique(manProcessStepCompleteRecord));
    }

    /**
     * 新增商品道序完成量台账-主
     */
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "商品道序完成量台账-主", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增商品道序完成量台账-主", notes = "新增商品道序完成量台账-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult add(@RequestBody @Valid ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        return AjaxResult.success(manProcessStepCompleteRecordService.insertManProcessStepCompleteRecord(manProcessStepCompleteRecord));
    }

    /**
     * 编辑商品道序完成量台账-主
     */
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "商品道序完成量台账-主", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改商品道序完成量台账-主", notes = "修改商品道序完成量台账-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult edit(@RequestBody ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        return toAjax(manProcessStepCompleteRecordService.updateManProcessStepCompleteRecord(manProcessStepCompleteRecord));
    }

    /**
     * 变更商品道序完成量台账-主
     */
    @PostMapping("/change")
    @Log(title = "商品道序完成量台账-主", businessType = BusinessType.CHANGE)
    @ApiOperation(value = "变更商品道序完成量台账-主", notes = "变更商品道序完成量台账-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        return toAjax(manProcessStepCompleteRecordService.changeManProcessStepCompleteRecord(manProcessStepCompleteRecord));
    }

    /**
     * 删除商品道序完成量台账-主
     */
    @PostMapping("/delete")
    @Log(title = "商品道序完成量台账-主", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除商品道序完成量台账-主", notes = "删除商品道序完成量台账-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult remove(@RequestBody List<Long> stepCompleteRecordSids) {
        if (CollectionUtils.isEmpty(stepCompleteRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProcessStepCompleteRecordService.deleteManProcessStepCompleteRecordByIds(stepCompleteRecordSids));
    }

    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "商品道序完成量台账-主", businessType = BusinessType.CHECK)
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult check(@RequestBody ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        return toAjax(manProcessStepCompleteRecordService.check(manProcessStepCompleteRecord));
    }

    /**
     * 明细按款显示
     */
    @PostMapping("/item/table")
    @ApiOperation(value = "明细按款显示", notes = "明细按款显示")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessStepCompleteRecordTableResponse.class))
    public AjaxResult itemTable(@RequestBody ManProcessStepCompleteRecordTableRequest request) {
        if (CollectionUtil.isEmpty(request.getStepCompleteRecordItemList())) {
            throw new BaseException("获取不到该商品道序完成量台账的明细数据，请联系管理员");
        }
        if (request.getProductSid() == null){
            throw new BaseException("请选择明细行");
        }
        return AjaxResult.success(manProcessStepCompleteRecordItemService.itemTable(request));
    }

    /**
     * 道序完成台账 报表
     * @param stepFinishDetail
     * @return
     * @throws IOException
     */
    @PostMapping("/item/getExportData")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "道序完成台账明细", notes = "导出道序完成台账明细数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public TableDataInfo itemExport( @RequestBody StepFinishDetail stepFinishDetail) throws IOException {
        int num = stepFinishDetail.getPageNum();
        int size = stepFinishDetail.getPageSize();
        stepFinishDetail.setPageSize(null).setPageNum(null);
        long count = manProcessStepCompleteRecordItemMapper.selectManProcessStepFinishDetailListCount(stepFinishDetail);
        stepFinishDetail.setPageSize(size).setPageNum(num);
        List<StepFinishDetail> stepFinishDetails = manProcessStepCompleteRecordService.itemProcessStepCompleteExportData(stepFinishDetail);
        return getDataTable(stepFinishDetails, count);
    }

    /**
     * 道序完成台账报表导出
     * @param response
     * @param stepFinishDetail
     * @throws IOException
     */
    @PostMapping("/item/export")
//    @Idempotent(message = "系统处理中，请勿重复点击按钮")
//    @Log(title = "道序完成台账明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "道序完成台账明细报表", notes = "导出道序完成台账明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void itemExport(HttpServletResponse response, StepFinishDetail stepFinishDetail) throws IOException {
        List<StepFinishDetail> stepFinishDetails = manProcessStepCompleteRecordService.itemProcessStepCompleteExportData(stepFinishDetail);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<StepFinishDetail> util = new ExcelUtil<>(StepFinishDetail.class, dataMap);
        util.exportExcel(response, stepFinishDetails, "道序完成台账明细报表");
    }

    @PostMapping("/staffCompleteSummary")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "员工完成量汇总", notes = "导出员工完成量汇总数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public TableDataInfo getStaffCompletionSummary(@RequestBody StaffCompleteSummary staffCompleteSummary){
        int num = staffCompleteSummary.getPageNum();
        int size = staffCompleteSummary.getPageSize();
        // 取消分页查询总数
        staffCompleteSummary.setPageSize(null).setPageNum(null);
        long count = manProcessStepCompleteRecordItemMapper.selectStaffCompleteSummaryCount(staffCompleteSummary);
        // 分页查询
        staffCompleteSummary.setPageSize(size).setPageNum(num);
        List<StaffCompleteSummary> summaryList = manProcessStepCompleteRecordService.getStaffCompleteSummary(staffCompleteSummary);
        return getDataTable(summaryList, count);
    }

    @PostMapping("/staffCompleteSummary/export")
    @ApiOperation(value = "员工完成量汇总报表打出", notes = "导出员工完成量汇总数据报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void getStaffCompletionSummaryExport(HttpServletResponse response,StaffCompleteSummary staffCompleteSummary) throws IOException {
        List<StaffCompleteSummary> summaryList = manProcessStepCompleteRecordService.getStaffCompleteSummary(staffCompleteSummary);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<StaffCompleteSummary> util = new ExcelUtil<>(StaffCompleteSummary.class, dataMap);
        util.exportExcel(response, summaryList, "员工完成量汇总报表");
    }

    /**
     * 员工完成量汇总的查看详情
     */
    @PostMapping("/staffCompleteSummary/table")
    @ApiOperation(value = "员工完成量汇总的查看详情", notes = "员工完成量汇总的查看详情")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = StaffCompleteSummaryTable.class))
    public AjaxResult staffTable(@RequestBody StaffCompleteSummary request) {
        return AjaxResult.success(manProcessStepCompleteRecordService.getStaffCompleteSummaryTable(request));
    }

}
