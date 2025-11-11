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
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.domain.SalServiceAcceptancePartner;
import com.platform.ems.service.ISalServiceAcceptancePartnerService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 服务销售验收单-合作伙伴Controller
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@RestController
@RequestMapping("/acceptance/partner")
@Api(tags = "服务销售验收单-合作伙伴")
public class SalServiceAcceptancePartnerController extends BaseController {

    @Autowired
    private ISalServiceAcceptancePartnerService salServiceAcceptancePartnerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询服务销售验收单-合作伙伴列表
     */
    @PreAuthorize(hasPermi = "ems:partner:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询服务销售验收单-合作伙伴列表", notes = "查询服务销售验收单-合作伙伴列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalServiceAcceptancePartner.class))
    public TableDataInfo list(@RequestBody SalServiceAcceptancePartner salServiceAcceptancePartner) {
        startPage();
        List<SalServiceAcceptancePartner> list = salServiceAcceptancePartnerService.selectSalServiceAcceptancePartnerList(salServiceAcceptancePartner);
        return getDataTable(list);
    }

    /**
     * 导出服务销售验收单-合作伙伴列表
     */
    @PreAuthorize(hasPermi = "ems:partner:export")
    @Log(title = "服务销售验收单-合作伙伴", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出服务销售验收单-合作伙伴列表", notes = "导出服务销售验收单-合作伙伴列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalServiceAcceptancePartner salServiceAcceptancePartner) throws IOException {
        List<SalServiceAcceptancePartner> list = salServiceAcceptancePartnerService.selectSalServiceAcceptancePartnerList(salServiceAcceptancePartner);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SalServiceAcceptancePartner> util = new ExcelUtil<SalServiceAcceptancePartner>(SalServiceAcceptancePartner.class,dataMap);
        util.exportExcel(response, list, "服务销售验收单-合作伙伴"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取服务销售验收单-合作伙伴详细信息
     */
    @ApiOperation(value = "获取服务销售验收单-合作伙伴详细信息", notes = "获取服务销售验收单-合作伙伴详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalServiceAcceptancePartner.class))
    @PreAuthorize(hasPermi = "ems:partner:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long serviceAcceptancePartnerSid) {
                    if(serviceAcceptancePartnerSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(salServiceAcceptancePartnerService.selectSalServiceAcceptancePartnerById(serviceAcceptancePartnerSid));
    }

    /**
     * 新增服务销售验收单-合作伙伴
     */
    @ApiOperation(value = "新增服务销售验收单-合作伙伴", notes = "新增服务销售验收单-合作伙伴")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:partner:add")
    @Log(title = "服务销售验收单-合作伙伴", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SalServiceAcceptancePartner salServiceAcceptancePartner) {
        return toAjax(salServiceAcceptancePartnerService.insertSalServiceAcceptancePartner(salServiceAcceptancePartner));
    }

    /**
     * 修改服务销售验收单-合作伙伴
     */
    @ApiOperation(value = "修改服务销售验收单-合作伙伴", notes = "修改服务销售验收单-合作伙伴")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:partner:edit")
    @Log(title = "服务销售验收单-合作伙伴", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SalServiceAcceptancePartner salServiceAcceptancePartner) {
        return toAjax(salServiceAcceptancePartnerService.updateSalServiceAcceptancePartner(salServiceAcceptancePartner));
    }

    /**
     * 删除服务销售验收单-合作伙伴
     */
    @ApiOperation(value = "删除服务销售验收单-合作伙伴", notes = "删除服务销售验收单-合作伙伴")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:partner:remove")
    @Log(title = "服务销售验收单-合作伙伴", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  serviceAcceptancePartnerSids) {
        if(ArrayUtil.isEmpty( serviceAcceptancePartnerSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(salServiceAcceptancePartnerService.deleteSalServiceAcceptancePartnerByIds(serviceAcceptancePartnerSids));
    }
}
