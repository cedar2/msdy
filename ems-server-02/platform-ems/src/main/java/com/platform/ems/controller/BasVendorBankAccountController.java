package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import com.platform.ems.domain.BasVendorBankAccount;
import com.platform.ems.service.IBasVendorBankAccountService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商银行账户信息Controller
 *
 * @author qhq
 * @date 2021-03-12
 */
@RestController
@RequestMapping("/account")
@Api(tags = "供应商银行账户信息")
public class BasVendorBankAccountController extends BaseController {

    @Autowired
    private IBasVendorBankAccountService basVendorBankAccountService;

    /**
     * 查询供应商银行账户信息列表
     */
    @PreAuthorize(hasPermi = "ems:account:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商银行账户信息列表", notes = "查询供应商银行账户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorBankAccount.class))
    public TableDataInfo list(@RequestBody BasVendorBankAccount basVendorBankAccount) {
        startPage();
        List<BasVendorBankAccount> list = basVendorBankAccountService.selectBasVendorBankAccountList(basVendorBankAccount);
        return getDataTable(list);
    }

    /**
     * 导出供应商银行账户信息列表
     */
    @PreAuthorize(hasPermi = "ems:account:export")
    @Log(title = "供应商银行账户信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商银行账户信息列表", notes = "导出供应商银行账户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendorBankAccount basVendorBankAccount) throws IOException {
        List<BasVendorBankAccount> list = basVendorBankAccountService.selectBasVendorBankAccountList(basVendorBankAccount);
        ExcelUtil<BasVendorBankAccount> util = new ExcelUtil<BasVendorBankAccount>(BasVendorBankAccount.class);
        util.exportExcel(response, list, "account");
    }

    /**
     * 获取供应商银行账户信息详细信息
     */
    @ApiOperation(value = "获取供应商银行账户信息详细信息", notes = "获取供应商银行账户信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorBankAccount.class))
    @PreAuthorize(hasPermi = "ems:account:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        return AjaxResult.success(basVendorBankAccountService.selectBasVendorBankAccountById(clientId));
    }

    /**
     * 新增供应商银行账户信息
     */
    @ApiOperation(value = "新增供应商银行账户信息", notes = "新增供应商银行账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:add")
    @Log(title = "供应商银行账户信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasVendorBankAccount basVendorBankAccount) {
        return toAjax(basVendorBankAccountService.insertBasVendorBankAccount(basVendorBankAccount));
    }

    /**
     * 修改供应商银行账户信息
     */
    @ApiOperation(value = "修改供应商银行账户信息", notes = "修改供应商银行账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:edit")
    @Log(title = "供应商银行账户信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasVendorBankAccount basVendorBankAccount) {
        return toAjax(basVendorBankAccountService.updateBasVendorBankAccount(basVendorBankAccount));
    }

    /**
     * 删除供应商银行账户信息
     */
    @ApiOperation(value = "删除供应商银行账户信息", notes = "删除供应商银行账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:remove")
    @Log(title = "供应商银行账户信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(List<String>  clientIds) {
        return toAjax(basVendorBankAccountService.deleteBasVendorBankAccountByIds(clientIds));
    }
}
