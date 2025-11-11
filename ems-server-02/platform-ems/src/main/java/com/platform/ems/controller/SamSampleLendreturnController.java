package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.SamSampleLendreturn;
import com.platform.ems.domain.SamSampleLendreturnItem;
import com.platform.ems.domain.dto.request.SamSampleLendreturnItemRequest;
import com.platform.ems.domain.dto.response.SamSampleLendreturnExResponse;
import com.platform.ems.domain.dto.response.SamSampleLendreturnReportResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.ISamSampleLendreturnService;
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
 * 样品借还单-主Controller
 *
 * @author linhongwei
 * @date 2021-12-20
 */
@RestController
@RequestMapping("/lendreturn")
@Api(tags = "样品借还单-主")
public class SamSampleLendreturnController extends BaseController {

    @Autowired
    private ISamSampleLendreturnService samSampleLendreturnService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询样品借还单-主列表
     */
   // @PreAuthorize(hasPermi = "ems:lendreturn:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询样品借还单-主列表", notes = "查询样品借还单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamSampleLendreturn.class))
    public TableDataInfo list(@RequestBody SamSampleLendreturn samSampleLendreturn) {
        startPage(samSampleLendreturn);
        List<SamSampleLendreturn> list = samSampleLendreturnService.selectSamSampleLendreturnList(samSampleLendreturn);
        return getDataTable(list);
    }

    @PostMapping("/report")
    @ApiOperation(value = "查询样品借还单-明细报表", notes = "查询样品借还单-明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamSampleLendreturn.class))
    public TableDataInfo report(@RequestBody SamSampleLendreturn samSampleLendreturn) {
        startPage(samSampleLendreturn);
        List<SamSampleLendreturnReportResponse> list = samSampleLendreturnService.getReport(samSampleLendreturn);
        return getDataTable(list);
    }

    @PostMapping("/get/item")
    @ApiOperation(value = "获取添加明细 信息", notes = "获取添加明细 信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamSampleLendreturn.class))
    public AjaxResult list(@RequestBody SamSampleLendreturnItem samSampleLendreturn) {
        List<SamSampleLendreturnItem> list = samSampleLendreturnService.getSamSampleLendreturnItem(samSampleLendreturn);
        return AjaxResult.success(list);
    }

    @PostMapping("/get/price")
    @ApiOperation(value = "获取添加明细价格 信息", notes = "获取添加明细价格 信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamSampleLendreturn.class))
    public AjaxResult getPrice(@RequestBody SamSampleLendreturnItemRequest request) {
        List<SamSampleLendreturnItem> list = samSampleLendreturnService.getPrice(request);
        return AjaxResult.success(list);
    }
    /**
     * 导出样品借还单-主列表
     */
   // @PreAuthorize(hasPermi = "ems:lendreturn:export")
    @Log(title = "样品借还单-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出样品借还单-主列表", notes = "导出样品借还单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamSampleLendreturn samSampleLendreturn) throws IOException {
        List<SamSampleLendreturn> list = samSampleLendreturnService.selectSamSampleLendreturnList(samSampleLendreturn);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SamSampleLendreturnExResponse> util = new ExcelUtil<>(SamSampleLendreturnExResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,SamSampleLendreturnExResponse::new), "样品借还单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    @ApiOperation(value = "导出样品借还单明细报表", notes = "导出样品借还单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/item")
    public void exportItem(HttpServletResponse response, SamSampleLendreturn samSampleLendreturn) throws IOException {
        List<SamSampleLendreturnReportResponse> list = samSampleLendreturnService.getReport(samSampleLendreturn);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SamSampleLendreturnReportResponse> util = new ExcelUtil<>(SamSampleLendreturnReportResponse.class, dataMap);
        util.exportExcel(response,list , "样品借还单明细报表" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取样品借还单-主详细信息
     */
    @ApiOperation(value = "获取样品借还单-主详细信息", notes = "获取样品借还单-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamSampleLendreturn.class))
 //   @PreAuthorize(hasPermi = "ems:lendreturn:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long lendreturnSid) {
        if (lendreturnSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(samSampleLendreturnService.selectSamSampleLendreturnById(lendreturnSid));
    }

    /**
     * 新增样品借还单-主
     */
    @ApiOperation(value = "新增样品借还单-主", notes = "新增样品借还单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
  //  @PreAuthorize(hasPermi = "ems:lendreturn:add")
    @Log(title = "样品借还单-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SamSampleLendreturn samSampleLendreturn) {
        return toAjax(samSampleLendreturnService.insertSamSampleLendreturn(samSampleLendreturn));
    }

    /**
     * 修改样品借还单-主
     */
    @ApiOperation(value = "修改样品借还单-主", notes = "修改样品借还单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
  //  @PreAuthorize(hasPermi = "ems:lendreturn:edit")
    @Log(title = "样品借还单-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SamSampleLendreturn samSampleLendreturn) {
        return toAjax(samSampleLendreturnService.updateSamSampleLendreturn(samSampleLendreturn));
    }

    /**
     * 变更样品借还单-主
     */
    @ApiOperation(value = "变更样品借还单-主", notes = "变更样品借还单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:lendreturn:change")
    @Log(title = "样品借还单-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SamSampleLendreturn samSampleLendreturn) {
        return toAjax(samSampleLendreturnService.changeSamSampleLendreturn(samSampleLendreturn));
    }

    /**
     * 删除样品借还单-主
     */
    @ApiOperation(value = "删除样品借还单-主", notes = "删除样品借还单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
 //   @PreAuthorize(hasPermi = "ems:lendreturn:remove")
    @Log(title = "样品借还单-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> lendreturnSids) {
        if (CollectionUtils.isEmpty(lendreturnSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(samSampleLendreturnService.deleteSamSampleLendreturnByIds(lendreturnSids));
    }


    @ApiOperation(value = "提交/审批时-校验", notes = "提交时-校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/processCheck")
    public AjaxResult get(@RequestBody List<Long> lendreturnSids) {
        if (CollectionUtils.isEmpty(lendreturnSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(samSampleLendreturnService.processCheck(lendreturnSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品借还单-主", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:lendreturn:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SamSampleLendreturn samSampleLendreturn) {
        return AjaxResult.success(samSampleLendreturnService.changeStatus(samSampleLendreturn));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:lendreturn:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品借还单-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SamSampleLendreturn samSampleLendreturn) {
        samSampleLendreturn.setConfirmDate(new Date());
        samSampleLendreturn.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        samSampleLendreturn.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(samSampleLendreturnService.check(samSampleLendreturn));
    }

}
