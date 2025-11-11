package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FinVendorMonthAccountBillAttach;
import com.platform.ems.service.IFinVendorMonthAccountBillAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商月对账单-附件Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/vendor/month/account/bill/attach")
@Api(tags = "供应商月对账单-附件")
public class FinVendorMonthAccountBillAttachController extends BaseController {

    @Autowired
    private IFinVendorMonthAccountBillAttachService finVendorMonthAccountBillAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商月对账单-附件列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商月对账单-附件列表", notes = "查询供应商月对账单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorMonthAccountBillAttach.class))
    public TableDataInfo list(@RequestBody FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        startPage(finVendorMonthAccountBillAttach);
        List<FinVendorMonthAccountBillAttach> list = finVendorMonthAccountBillAttachService.selectFinVendorMonthAccountBillAttachList(finVendorMonthAccountBillAttach);
        return getDataTable(list);
    }

    /**
     * 导出供应商月对账单-附件列表
     */
    @ApiOperation(value = "导出供应商月对账单-附件列表", notes = "导出供应商月对账单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) throws IOException {
        List<FinVendorMonthAccountBillAttach> list = finVendorMonthAccountBillAttachService.selectFinVendorMonthAccountBillAttachList(finVendorMonthAccountBillAttach);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorMonthAccountBillAttach> util = new ExcelUtil<FinVendorMonthAccountBillAttach>(FinVendorMonthAccountBillAttach.class,dataMap);
        util.exportExcel(response, list, "供应商月对账单-附件");
    }


    /**
     * 获取供应商月对账单-附件详细信息
     */
    @ApiOperation(value = "获取供应商月对账单-附件详细信息", notes = "获取供应商月对账单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorMonthAccountBillAttach.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long monthAccountBillAttachmentSid) {
                    if(monthAccountBillAttachmentSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(finVendorMonthAccountBillAttachService.selectFinVendorMonthAccountBillAttachById(monthAccountBillAttachmentSid));
    }

    /**
     * 新增供应商月对账单-附件
     */
    @ApiOperation(value = "新增供应商月对账单-附件", notes = "新增供应商月对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        return toAjax(finVendorMonthAccountBillAttachService.insertFinVendorMonthAccountBillAttach(finVendorMonthAccountBillAttach));
    }

    /**
     * 修改供应商月对账单-附件
     */
    @ApiOperation(value = "修改供应商月对账单-附件", notes = "修改供应商月对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        return toAjax(finVendorMonthAccountBillAttachService.updateFinVendorMonthAccountBillAttach(finVendorMonthAccountBillAttach));
    }

    /**
     * 变更供应商月对账单-附件
     */
    @ApiOperation(value = "变更供应商月对账单-附件", notes = "变更供应商月对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        return toAjax(finVendorMonthAccountBillAttachService.changeFinVendorMonthAccountBillAttach(finVendorMonthAccountBillAttach));
    }

    /**
     * 删除供应商月对账单-附件
     */
    @ApiOperation(value = "删除供应商月对账单-附件", notes = "删除供应商月对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  monthAccountBillAttachmentSids) {
        if(CollectionUtils.isEmpty( monthAccountBillAttachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorMonthAccountBillAttachService.deleteFinVendorMonthAccountBillAttachByIds(monthAccountBillAttachmentSids));
    }

}
