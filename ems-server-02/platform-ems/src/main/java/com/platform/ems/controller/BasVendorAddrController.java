package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.domain.dto.request.BasVendorAddrAddRequest;
import com.platform.ems.domain.dto.request.BasVendorAddrDeleteRequest;
import com.platform.ems.domain.dto.request.BasVendorAddrUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.BasVendorAddr;
import com.platform.ems.service.IBasVendorAddrService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商-联系方式信息Controller
 * 
 * @author linhongwei
 * @date 2021-01-31
 */
@RestController
@RequestMapping("/vendor/addr")
public class BasVendorAddrController extends BaseController {

    @Autowired
    private IBasVendorAddrService basVendorAddrService;

    /**
     * 查询供应商-联系方式信息列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:addr:list")
    @GetMapping("/getList")
    public TableDataInfo getList(@RequestBody BasVendorAddr basVendorAddr) {
        startPage();
        List<BasVendorAddr> list = basVendorAddrService.selectBasVendorAddrList(basVendorAddr);
        return getDataTable(list);
    }

    /**
     * 导出供应商-联系方式信息列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:addr:export")
    @Log(title = "供应商-联系方式信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendorAddr basVendorAddr) throws IOException {
        List<BasVendorAddr> list = basVendorAddrService.selectBasVendorAddrList(basVendorAddr);
        ExcelUtil<BasVendorAddr> util = new ExcelUtil<BasVendorAddr>(BasVendorAddr.class);
        util.exportExcel(response, list, "供应商-联系方式信息");
    }

    /**
     * 获取供应商-联系方式信息详细信息
     */
    @PreAuthorize(hasPermi = "ems:vendor:addr:query")
    @GetMapping(value = "/{clientId}")
    public AjaxResult getInfo(@PathVariable("clientId") String clientId) {
        return AjaxResult.success(basVendorAddrService.selectBasVendorAddrById(clientId));
    }

    /**
     * 新增供应商-联系方式信息
     */
    @PreAuthorize(hasPermi = "ems:vendor:addr:add")
    @Log(title = "供应商-联系方式信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody BasVendorAddrAddRequest basVendorAddr) {
        return basVendorAddrService.insertBasVendorAddr(basVendorAddr);
    }

    /**
     * 修改供应商-联系方式信息
     */
    @PreAuthorize(hasPermi = "ems:vendor:addr:edit")
    @Log(title = "供应商-联系方式信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult update(@RequestBody BasVendorAddrUpdateRequest request) {
        return toAjax(basVendorAddrService.updateBasVendorAddr(request));
    }

    /**
     * 删除供应商-联系方式信息
     */
    @PreAuthorize(hasPermi = "ems:vendor:addr:remove")
    @Log(title = "供应商-联系方式信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult delete(@RequestBody BasVendorAddrDeleteRequest basVendorAddrDeleteRequest) {
        return toAjax(basVendorAddrService.deleteBasVendorAddrByIds(basVendorAddrDeleteRequest));
    }
}
