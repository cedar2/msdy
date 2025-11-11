package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;
import com.platform.ems.domain.PurRecordVendorConsign;
import com.platform.ems.service.IPurRecordVendorConsignService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商寄售待结算台账Controller
 *
 * @author linhongwei
 * @date 2021-06-23
 */
@RestController
@RequestMapping("/consign")
@Api(tags = "供应商寄售待结算台账")
public class PurRecordVendorConsignController extends BaseController {

    @Autowired
    private IPurRecordVendorConsignService purRecordVendorConsignService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 供应商寄售待结算台账报表
     */
//    @PreAuthorize(hasPermi = "ems:consign:list")
    @PostMapping("/list")
    @ApiOperation(value = "供应商寄售待结算台账报表", notes = "供应商寄售待结算台账报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurRecordVendorConsign.class))
    public TableDataInfo list(@RequestBody PurRecordVendorConsign purRecordVendorConsign) {
        startPage(purRecordVendorConsign);
        List<PurRecordVendorConsign> list = purRecordVendorConsignService.selectPurRecordVendorConsignList(purRecordVendorConsign);
        return getDataTable(list);
    }

    /**
     * 导出供应商寄售待结算台账列表
     */
//    @PreAuthorize(hasPermi = "ems:consign:export")
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商寄售待结算台账列表", notes = "导出供应商寄售待结算台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurRecordVendorConsign purRecordVendorConsign) throws IOException {
        List<PurRecordVendorConsign> list = purRecordVendorConsignService.selectPurRecordVendorConsignList(purRecordVendorConsign);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurRecordVendorConsign> util = new ExcelUtil<PurRecordVendorConsign>(PurRecordVendorConsign.class,dataMap);
        util.exportExcel(response, list, "供应商寄售待结算台账"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取供应商寄售待结算台账详细信息
     */
    @ApiOperation(value = "获取供应商寄售待结算台账详细信息", notes = "获取供应商寄售待结算台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurRecordVendorConsign.class))
//    @PreAuthorize(hasPermi = "ems:consign:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordVendorConsignSid) {
                    if(recordVendorConsignSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(purRecordVendorConsignService.selectPurRecordVendorConsignById(recordVendorConsignSid));
    }

    /**
     * 新增供应商寄售待结算台账
     */
    @ApiOperation(value = "新增供应商寄售待结算台账", notes = "新增供应商寄售待结算台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:consign:add")
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurRecordVendorConsign purRecordVendorConsign) {
        return toAjax(purRecordVendorConsignService.insertPurRecordVendorConsign(purRecordVendorConsign));
    }

    /**
     * 修改供应商寄售待结算台账
     */
    @ApiOperation(value = "修改供应商寄售待结算台账", notes = "修改供应商寄售待结算台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:consign:edit")
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurRecordVendorConsign purRecordVendorConsign) {
        return toAjax(purRecordVendorConsignService.updatePurRecordVendorConsign(purRecordVendorConsign));
    }

    /**
     * 变更供应商寄售待结算台账
     */
    @ApiOperation(value = "变更供应商寄售待结算台账", notes = "变更供应商寄售待结算台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:consign:change")
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurRecordVendorConsign purRecordVendorConsign) {
        return toAjax(purRecordVendorConsignService.changePurRecordVendorConsign(purRecordVendorConsign));
    }

    /**
     * 删除供应商寄售待结算台账
     */
    @ApiOperation(value = "删除供应商寄售待结算台账", notes = "删除供应商寄售待结算台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:consign:remove")
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  recordVendorConsignSids) {
        if(ArrayUtil.isEmpty( recordVendorConsignSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purRecordVendorConsignService.deletePurRecordVendorConsignByIds(recordVendorConsignSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.UPDATE)
//    @PreAuthorize(hasPermi = "ems:consign:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PurRecordVendorConsign purRecordVendorConsign) {
        return AjaxResult.success(purRecordVendorConsignService.changeStatus(purRecordVendorConsign));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:consign:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商寄售待结算台账", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurRecordVendorConsign purRecordVendorConsign) {
        purRecordVendorConsign.setConfirmDate(new Date());
        purRecordVendorConsign.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        return toAjax(purRecordVendorConsignService.check(purRecordVendorConsign));
    }

}
