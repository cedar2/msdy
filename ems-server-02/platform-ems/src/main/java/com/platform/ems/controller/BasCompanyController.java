package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasCompany;
import com.platform.ems.domain.BasCompanyBrand;
import com.platform.ems.domain.BasCompanyBrandMark;
import com.platform.ems.service.IBasCompanyService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 公司档案Controller
 *
 * @author hjj
 * @date 2021-01-22
 */
@RestController
@RequestMapping("/company")
@Api(tags = "公司档案")
@SuppressWarnings("all")
public class BasCompanyController extends BaseController {

    @Autowired
    private IBasCompanyService basCompanyService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询公司档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询公司档案列表", notes = "查询公司档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompany.class))
    public TableDataInfo list(@RequestBody BasCompany request) {
        startPage(request);
        List<BasCompany> list = basCompanyService.selectBasCompanyList(request);
        return getDataTable(list);
    }

    /**
     * 导出公司档案列表
     */
    @Log(title = "公司档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出公司档案列表", notes = "导出公司档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompany.class))
    public void export(HttpServletResponse response, BasCompany request) throws IOException {
        List<BasCompany> list = basCompanyService.export(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasCompany> util = new ExcelUtil<>(BasCompany.class, dataMap);
        util.exportExcel(response, list, "公司");
    }


    /**
     * 获取公司档案详细信息
     */
    @PostMapping("/getInfo")
    @ApiOperation(value = "获取公司档案详细信息", notes = "获取公司档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompany.class))
    public AjaxResult getInfo(Long companySid) {
        return AjaxResult.success(basCompanyService.selectBasCompanyById(companySid));
    }


    /**
     * 新增公司档案
     */
    @CacheEvict(value = "basCompanyList", allEntries = true)
    @Log(title = "公司档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation(value = "新增公司档案", notes = "新增公司档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult add(@RequestBody @Valid BasCompany request) {
        int row = basCompanyService.insertBasCompany(request);
        return AjaxResult.success(request);
    }

    /**
     * 修改公司档案
     */
    @Caching(evict = {
            @CacheEvict(value = "basCompanyList", allEntries = true),
            @CacheEvict(value = "basCompanyBrandList", allEntries = true),
            @CacheEvict(value = "basCompanyBrandMarkList", allEntries = true),
            @CacheEvict(value = "basCompany", key = "#request.companySid")
    })
    @Log(title = "公司档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ApiOperation(value = "修改公司档案", notes = "修改公司档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult edit(@RequestBody @Valid BasCompany request) {
        if (ConstantsEms.CHECK_STATUS.equals(request.getHandleStatus())) {
            request.setConfirmDate(new Date());
            request.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return toAjax(basCompanyService.editBasCompany(request));
    }

    /**
     * 变更公司档案
     */
    @Caching(evict = {
            @CacheEvict(value = "basCompanyList", allEntries = true),
            @CacheEvict(value = "basCompany", key = "#request.companySid")
    })
    @Log(title = "公司档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    @ApiOperation(value = "变更公司档案", notes = "变更公司档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid BasCompany request) {
        return toAjax(basCompanyService.editBasCompany(request));
    }

    @CacheEvict(value = "basCompanyList", allEntries = true)
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "公司档案", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasCompany basCompany) {
        return toAjax(basCompanyService.check(basCompany));
    }

    /**
     * 删除公司档案
     */
    @Caching(evict = {
            @CacheEvict(value = "basCompanyList", allEntries = true),
            @CacheEvict(value = "basCompanyBrandList", allEntries = true),
            @CacheEvict(value = "basCompanyBrandMarkList", allEntries = true)
    })
    @Log(title = "公司档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除公司档案", notes = "删除公司档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult remove(@RequestBody List<Long> companySids) {
        if (ArrayUtil.isEmpty(companySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basCompanyService.deleteBasCompanyByIds(companySids));
    }

    @CacheEvict(value = "basCompanyList", allEntries = true)
    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "公司档案", businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasCompany basCompany) {
        return AjaxResult.success(basCompanyService.changeStatus(basCompany));
    }


    /**
     * 公司档案下拉框列表
     */
    @PostMapping("/getCompanyList")
    @ApiOperation(value = "公司档案下拉框列表", notes = "公司档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompany.class))
    public List<BasCompany> getCompanyList() {
        BasCompany company = new BasCompany();
        company.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return basCompanyService.getCompanyList(company);
    }

    /**
     * 公司档案下拉框列表 带参数
     */
    @PostMapping("/getList")
    @ApiOperation(value = "公司档案下拉框列表", notes = "公司档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompany.class))
    public AjaxResult getCompanyList(@RequestBody BasCompany company) {
        return AjaxResult.success(basCompanyService.getCompanyList(company));
    }

    @PostMapping("/getCompanyBrandList")
    @ApiOperation(value = "公司档案品牌信息下拉框列表", notes = "公司档案品牌信息下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompanyBrand.class))
    public List<BasCompanyBrand> getCompanyBrandList(String companySid) {
        if (StrUtil.isEmpty(companySid)) {
            throw new CheckedException("参数缺失");
        }
        return basCompanyService.getCompanyBrandList(companySid);
    }

    @PostMapping("/getBrandList")
    @ApiOperation(value = "公司档案品牌信息下拉框列表", notes = "公司档案品牌信息下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompanyBrand.class))
    public List<BasCompanyBrand> getBrandList(@RequestBody BasCompanyBrand brand) {
        return basCompanyService.getBrandList(brand);
    }

    @PostMapping("/getCompanyBrandMarkList")
    @ApiOperation(value = "公司档案品牌品标信息下拉框列表", notes = "公司档案品牌品标信息下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompanyBrandMark.class))
    public List<BasCompanyBrandMark> getCompanyBrandMarkList(Long brandSid) {
        if (brandSid == null) {
            throw new CheckedException("参数缺失");
        }
        return basCompanyService.getCompanyBrandMarkList(brandSid);
    }

}
