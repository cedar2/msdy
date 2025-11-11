package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManDayManufactureProgress;
import com.platform.ems.domain.ManDayManufactureProgressDetail;
import com.platform.ems.domain.ManDayManufactureProgressItem;
import com.platform.ems.domain.dto.ManDayProgressMonthForm;
import com.platform.ems.domain.dto.ManDayProgressMonthFormData;
import com.platform.ems.mapper.ManDayManufactureProgressItemMapper;
import com.platform.ems.service.IManDayManufactureProgressItemService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生产进度日报明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@RestController
@RequestMapping("/man/day/progress/item")
@Api(tags = "生产进度日报明细报表")
public class ManDayManufactureProgressItemController extends BaseController {

    @Autowired
    private IManDayManufactureProgressItemService manDayManufactureProgressItemService;
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产进度日报明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产进度日报明细列表", notes = "查询生产进度日报明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgressItem.class))
    public TableDataInfo list(@RequestBody ManDayManufactureProgressItem manDayManufactureProgressItem) {
        startPage(manDayManufactureProgressItem);
        List<ManDayManufactureProgressItem> list = manDayManufactureProgressItemService.selectManDayManufactureProgressItemList(manDayManufactureProgressItem);
        return getDataTable(list);
    }

    /**
     * 查询生产进度日报明细报表
     */
    @PostMapping("/form")
    @ApiOperation(value = "查询生产进度日报明细报表", notes = "查询生产进度日报明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgressItem.class))
    public TableDataInfo form(@RequestBody ManDayManufactureProgressItem manDayManufactureProgressItem) {
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manDayManufactureProgressItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        TableDataInfo rspData = new TableDataInfo();
        long total = new Long(manDayManufactureProgressItemService.selectCount(manDayManufactureProgressItem));
        rspData.setTotal(total);
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        if (total > 0){
            List<ManDayManufactureProgressItem> list = manDayManufactureProgressItemService.selectManDayManufactureProgressItemForm(manDayManufactureProgressItem);
            rspData.setRows(list);
        }
        return rspData;
    }

    /**
     * 导出生产进度日报明细报表列表
     */
    @Log(title = "生产进度日报明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产进度日报明细报表列表", notes = "导出生产进度日报明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManDayManufactureProgressItem manDayManufactureProgressItem) throws IOException {
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manDayManufactureProgressItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        manDayManufactureProgressItem.setPageNum(null).setPageSize(null);
        List<ManDayManufactureProgressItem> list = manDayManufactureProgressItemService.selectManDayManufactureProgressItemForm(manDayManufactureProgressItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManDayManufactureProgressItem> util = new ExcelUtil<>(ManDayManufactureProgressItem.class, dataMap);
        util.exportExcel(response, list, "班组生产日报明细报表");
    }

    /**
     * 获取生产进度日报明细报表详细信息
     */
    @ApiOperation(value = "获取生产进度日报明细报表详细信息", notes = "获取生产进度日报明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgressItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dayManufactureProgressItemSid) {
        if (dayManufactureProgressItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manDayManufactureProgressItemService.selectManDayManufactureProgressItemById(dayManufactureProgressItemSid));
    }

    /**
     * 勾选明细进入尺码完工明细 默认带出 生产订单 根据外层所选择明细行的“生产订单号+商品编码+颜色”，从生产订单产品明细表中，自动带出商品的颜色&尺码明细
     */
    @ApiOperation(value = "勾选明细进入尺码完工明细默认带出", notes = "勾选明细进入尺码完工明细默认带出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgressDetail.class))
    @PostMapping("/detail")
    public AjaxResult skuItem(@RequestBody ManDayManufactureProgressItem manDayManufactureProgressItem) {
        return AjaxResult.success(manDayManufactureProgressItemService.getManDayManufactureProgressDetail(manDayManufactureProgressItem));
    }

    /**
     * 生产日进度报表 查看详情的 行转列
     */
    @ApiOperation(value = "生产日进度报表查看详情的 行转列", notes = "生产日进度报表查看详情的 行转列")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgressDetail.class))
    @PostMapping("/detail/table")
    public AjaxResult detailTable(@RequestBody ManDayProgressMonthForm manDayProgressMonthForm) {
        return AjaxResult.success(manDayManufactureProgressItemService.getManDayManufactureProgressDetailTable(manDayProgressMonthForm));
    }

    /**
     * 根据“所属年月+生产订单号+商品编码+所属生产工序”从“生产进度日报“中获取符合条件的生产进度明细数据（进度日报需为“已确认”状态），将明细行的”当天实际完成量“（quantity）累加得出
     */
    @ApiOperation(value = "获得当天实际完成量累加", notes = "获得当天实际完成量累加")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/quantity")
    public AjaxResult getQuantity(@RequestBody ManDayManufactureProgressItem request) {
        return AjaxResult.success(manDayManufactureProgressItemService.getQuantity(request));
    }

    /**
     *  班组生产日报 “工序进度”页签，新增4个清单列：实裁量、已完成量(工序)、未完成量(计划)、未完成量(实裁)，不可编辑，放置于“完成量(首批)”清单列后
     *    1》已完成量(工序)
     *      根据“工厂(工序)+班组+生产订单+生产订单工序明细sid”从“班组生产日报明细表“中（s_man_day_manufacture_progress_item）
     *      获取所有符合条件的明细行【仅获取”已确认“状态的”班组生产日报“】，然后将所有明细行的“当天实际完成量/收料量”（quantity）累加得出
     *    2》实裁量  根据“生产订单号”从“生产订单工序明细表”中获取“是否第一个工序”为“是”的工序的已完成量，已完成量计算逻辑参照第 1》点
     *    3》未完成量(计划)  = 计划产量(工序) - 已完成量(工序)
     *    4》未完成量(实裁)  = 实裁量 - 已完成量(工序)
     */
    @ApiOperation(value = "班组生产日报切换班组刷新工序进度明细", notes = "班组生产日报切换班组刷新工序进度明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/quantity/flush")
    public AjaxResult getCompleteQuantity(@RequestBody ManDayManufactureProgress manDayManufactureProgress) {
        return AjaxResult.success(manDayManufactureProgressItemService.getCompleteQuantity(manDayManufactureProgress));
    }

    @PostMapping("/form/month")
    @ApiOperation(value = "报表中心生产管理生产月进度", notes = "报表中心生产管理生产月进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayProgressMonthFormData.class))
    public AjaxResult formMonthProgress(@RequestBody ManDayProgressMonthForm manDayProgressMonthFormRequest) {
        if (StrUtil.isBlank(manDayProgressMonthFormRequest.getYearmonth()) ||
                manDayProgressMonthFormRequest.getPlantSidList() == null || manDayProgressMonthFormRequest.getPlantSidList().length == 0) {
            throw new BaseException("请选择月份和工厂！");
        }
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manDayProgressMonthFormRequest.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        ManDayProgressMonthFormData response = new ManDayProgressMonthFormData();
        manDayProgressMonthFormRequest.setIsStageComplete(ConstantsEms.YES);
        manDayProgressMonthFormRequest.setYearmonthday(manDayProgressMonthFormRequest.getYearmonth()+"-01");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate yearmonthdayDate = LocalDate.parse(manDayProgressMonthFormRequest.getYearmonthday(), fmt);
        yearmonthdayDate = yearmonthdayDate.minus(1, ChronoUnit.MONTHS);
        manDayProgressMonthFormRequest.setLastYearmonthday(yearmonthdayDate.toString());
        long total = manDayManufactureProgressItemMapper.countManDayManufactureProgressMonthForm(manDayProgressMonthFormRequest);
        response.setTotal(total);
        response.setDayList(new ArrayList<>());
        response.setFormList(new ArrayList<>());
        if (total > 0){
            response = manDayManufactureProgressItemService.selectManDayManufactureProgressMonthForm(manDayProgressMonthFormRequest);
            response.setTotal(total);
            return AjaxResult.success(response);
        }
        return AjaxResult.success(response);
    }

    @ApiOperation(value = "导出报表中心生产管理生产月进度", notes = "导出报表中心生产管理生产月进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/form/month/export")
    public void exportFormMonth(HttpServletResponse response, ManDayProgressMonthForm manDayProgressMonthFormRequest) throws IOException {
        if (StrUtil.isBlank(manDayProgressMonthFormRequest.getYearmonth()) ||
                manDayProgressMonthFormRequest.getPlantSidList() == null || manDayProgressMonthFormRequest.getPlantSidList().length == 0) {
            throw new BaseException("请选择月份和工厂！");
        }
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manDayProgressMonthFormRequest.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        manDayProgressMonthFormRequest.setIsStageComplete(ConstantsEms.YES);
        manDayProgressMonthFormRequest.setYearmonthday(manDayProgressMonthFormRequest.getYearmonth()+"-01");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate yearmonthdayDate = LocalDate.parse(manDayProgressMonthFormRequest.getYearmonthday(), fmt);
        yearmonthdayDate = yearmonthdayDate.minus(1, ChronoUnit.MONTHS);
        manDayProgressMonthFormRequest.setLastYearmonthday(yearmonthdayDate.toString());
        ManDayProgressMonthFormData data = manDayManufactureProgressItemService.selectManDayManufactureProgressMonthForm(manDayProgressMonthFormRequest);
        manDayManufactureProgressItemService.exportFormMonth(response, data);
    }

    @PostMapping("/form/day")
    @ApiOperation(value = "报表中心生产管理生产日进度", notes = "报表中心生产管理生产日进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayProgressMonthFormData.class))
    public AjaxResult formDayProgress(@RequestBody ManDayProgressMonthForm manDayProgressMonthFormRequest) {
        if (ConstantsEms.YES.equals(manDayProgressMonthFormRequest.getIsRange()) &&
                (manDayProgressMonthFormRequest.getYearmonthdayBegin() == null || manDayProgressMonthFormRequest.getYearmonthdayEnd() == null)) {
            throw new BaseException("请选择日期范围！");
        }
        if (manDayProgressMonthFormRequest.getPlantSidList() == null || manDayProgressMonthFormRequest.getPlantSidList().length == 0) {
            throw new BaseException("请选择工厂！");
        }
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            manDayProgressMonthFormRequest.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        ManDayProgressMonthFormData response = new ManDayProgressMonthFormData();
        long total = manDayManufactureProgressItemMapper.countManDayManufactureProgressMonthForm(manDayProgressMonthFormRequest);
        response.setTotal(total);
        response.setDayList(new ArrayList<>());
        response.setFormList(new ArrayList<>());
        if (total > 0){
            response = manDayManufactureProgressItemService.selectManDayManufactureProgressDayForm(manDayProgressMonthFormRequest);
            response.setTotal(total);
            return AjaxResult.success(response);
        }
        return AjaxResult.success(response);
    }

    @ApiOperation(value = "导出报表中心生产管理生产日进度", notes = "导出报表中心生产管理生产日进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/form/day/export")
    public void exportFormDay(HttpServletResponse response, ManDayProgressMonthForm manDayProgressMonthFormRequest) throws IOException {
        manDayManufactureProgressItemService.exportFormDay(response, manDayProgressMonthFormRequest);
    }
}
