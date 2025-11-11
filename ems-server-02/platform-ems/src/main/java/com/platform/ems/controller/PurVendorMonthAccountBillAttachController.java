package com.platform.ems.controller;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.domain.PurVendorMonthAccountBillAttach;
import com.platform.ems.service.IPurVendorMonthAccountBillAttachService;
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
 * 供应商对账单-附件Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/pur/vendor/month/account/bill/attach")
@Api(tags = "供应商对账单-附件")
public class PurVendorMonthAccountBillAttachController extends BaseController {

    @Autowired
    private IPurVendorMonthAccountBillAttachService purVendorMonthAccountBillAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商对账单-附件列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商对账单-附件列表", notes = "查询供应商对账单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurVendorMonthAccountBillAttach.class))
    public TableDataInfo list(@RequestBody PurVendorMonthAccountBillAttach purVendorMonthAccountBillAttach) {
        startPage(purVendorMonthAccountBillAttach);
        List<PurVendorMonthAccountBillAttach> list = purVendorMonthAccountBillAttachService.selectPurVendorMonthAccountBillAttachList(purVendorMonthAccountBillAttach);
        return getDataTable(list);
    }

    /**
     * 导出供应商对账单-附件列表
     */
    @ApiOperation(value = "导出供应商对账单-附件列表", notes = "导出供应商对账单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurVendorMonthAccountBillAttach purVendorMonthAccountBillAttach) throws IOException {
        List<PurVendorMonthAccountBillAttach> list = purVendorMonthAccountBillAttachService.selectPurVendorMonthAccountBillAttachList(purVendorMonthAccountBillAttach);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurVendorMonthAccountBillAttach> util = new ExcelUtil<PurVendorMonthAccountBillAttach>(PurVendorMonthAccountBillAttach.class, dataMap);
        util.exportExcel(response, list, "供应商对账单-附件");
    }


    /**
     * 获取供应商对账单-附件详细信息
     */
    @ApiOperation(value = "获取供应商对账单-附件详细信息", notes = "获取供应商对账单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurVendorMonthAccountBillAttach.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long monthAccountBillAttachmentSid) {
        if (monthAccountBillAttachmentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purVendorMonthAccountBillAttachService.selectPurVendorMonthAccountBillAttachById(monthAccountBillAttachmentSid));
    }

    /**
     * 新增供应商对账单-附件
     */
    @ApiOperation(value = "新增供应商对账单-附件", notes = "新增供应商对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurVendorMonthAccountBillAttach purVendorMonthAccountBillAttach) {
        return toAjax(purVendorMonthAccountBillAttachService.insertPurVendorMonthAccountBillAttach(purVendorMonthAccountBillAttach));
    }

    /**
     * 修改供应商对账单-附件
     */
    @ApiOperation(value = "修改供应商对账单-附件", notes = "修改供应商对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurVendorMonthAccountBillAttach purVendorMonthAccountBillAttach) {
        return toAjax(purVendorMonthAccountBillAttachService.updatePurVendorMonthAccountBillAttach(purVendorMonthAccountBillAttach));
    }

    /**
     * 变更供应商对账单-附件
     */
    @ApiOperation(value = "变更供应商对账单-附件", notes = "变更供应商对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurVendorMonthAccountBillAttach purVendorMonthAccountBillAttach) {
        return toAjax(purVendorMonthAccountBillAttachService.changePurVendorMonthAccountBillAttach(purVendorMonthAccountBillAttach));
    }

    /**
     * 删除供应商对账单-附件
     */
    @ApiOperation(value = "删除供应商对账单-附件", notes = "删除供应商对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> monthAccountBillAttachmentSids) {
        if (CollectionUtils.isEmpty(monthAccountBillAttachmentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purVendorMonthAccountBillAttachService.deletePurVendorMonthAccountBillAttachByIds(monthAccountBillAttachmentSids));
    }

}
