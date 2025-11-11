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
import com.platform.ems.domain.FinCustomerFundsFreezeBill;
import com.platform.ems.service.IFinCustomerFundsFreezeBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户暂押款Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/customer/funds/freeze/bill")
@Api(tags = "客户暂押款")
public class FinCustomerFundsFreezeBillController extends BaseController {

    @Autowired
    private IFinCustomerFundsFreezeBillService finCustomerFundsFreezeBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户暂押款列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户暂押款列表", notes = "查询客户暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerFundsFreezeBill.class))
    public TableDataInfo list(@RequestBody FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        startPage(finCustomerFundsFreezeBill);
        List<FinCustomerFundsFreezeBill> list = finCustomerFundsFreezeBillService.selectFinCustomerFundsFreezeBillList(finCustomerFundsFreezeBill);
        return getDataTable(list);
    }

    /**
     * 导出客户暂押款列表
     */
    @ApiOperation(value = "导出客户暂押款列表", notes = "导出客户暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) throws IOException {
        List<FinCustomerFundsFreezeBill> list = finCustomerFundsFreezeBillService.selectFinCustomerFundsFreezeBillList(finCustomerFundsFreezeBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerFundsFreezeBill> util = new ExcelUtil<>(FinCustomerFundsFreezeBill.class, dataMap);
        util.exportExcel(response, list, "客户暂押款");
    }


    /**
     * 获取客户暂押款详细信息
     */
    @ApiOperation(value = "获取客户暂押款详细信息", notes = "获取客户暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerFundsFreezeBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long fundsFreezeBillSid) {
        if (fundsFreezeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerFundsFreezeBillService.selectFinCustomerFundsFreezeBillById(fundsFreezeBillSid));
    }

    /**
     * 新增客户暂押款
     */
    @ApiOperation(value = "新增客户暂押款", notes = "新增客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        int row = finCustomerFundsFreezeBillService.insertFinCustomerFundsFreezeBill(finCustomerFundsFreezeBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinCustomerFundsFreezeBill()
                    .setFundsFreezeBillSid(finCustomerFundsFreezeBill.getFundsFreezeBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改客户暂押款
     */
    @ApiOperation(value = "修改客户暂押款", notes = "修改客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        return toAjax(finCustomerFundsFreezeBillService.updateFinCustomerFundsFreezeBill(finCustomerFundsFreezeBill));
    }

    /**
     * 变更客户暂押款
     */
    @ApiOperation(value = "变更客户暂押款", notes = "变更客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        return toAjax(finCustomerFundsFreezeBillService.changeFinCustomerFundsFreezeBill(finCustomerFundsFreezeBill));
    }

    /**
     * 删除客户暂押款
     */
    @ApiOperation(value = "删除客户暂押款", notes = "删除客户暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> fundsFreezeBillSids) {
        if (CollectionUtils.isEmpty(fundsFreezeBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerFundsFreezeBillService.deleteFinCustomerFundsFreezeBillByIds(fundsFreezeBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        return toAjax(finCustomerFundsFreezeBillService.check(finCustomerFundsFreezeBill));
    }

    /**
     * 复制客户暂押款详细信息
     */
    @ApiOperation(value = "复制客户暂押款详细信息", notes = "复制客户暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerFundsFreezeBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long fundsFreezeBillSid) {
        if (fundsFreezeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinCustomerFundsFreezeBill finCustomerFundsFreezeBill = finCustomerFundsFreezeBillService.selectFinCustomerFundsFreezeBillById(fundsFreezeBillSid);
        finCustomerFundsFreezeBill.setFundsFreezeBillSid(null).setFundsFreezeBillCode(null).setHandleStatus(null).setCreatorAccount(null)
                .setCreatorAccountName(null).setCreateDate(null).setRemark(null);
        finCustomerFundsFreezeBill.setDocumentDate(null);
        finCustomerFundsFreezeBill.getItemList().forEach(item -> item.setFundsFreezeBillSid(null).setFundsFreezeBillItemSid(null).setRemark(null));
        return AjaxResult.success(finCustomerFundsFreezeBill);
    }

    /**
     * 作废
     */
    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long fundsFreezeBillSid) {
        return AjaxResult.success(finCustomerFundsFreezeBillService.invalid(fundsFreezeBillSid));
    }
}
