package com.platform.ems.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.PurInquiryVendor;
import com.platform.ems.domain.PurQuoteBargain;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IPurInquiryVendorService;
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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.service.IPurInquiryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 物料询价单主Controller
 *
 * @author chenkw
 * @date 2022-01-11
 */
@RestController
@RequestMapping("/pur/inquiry")
@Api(tags = "物料询价单主")
public class PurInquiryController extends BaseController {

    @Autowired
    private IPurInquiryService purInquiryService;
    @Autowired
    private IPurInquiryVendorService purInquiryVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询物料询价单主列表
     */
    @PreAuthorize(hasPermi = "ems:pur:inquiry:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询物料询价单主列表", notes = "查询物料询价单主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiry.class))
    public TableDataInfo list(@RequestBody PurInquiry purInquiry) {
        List<PurInquiry> list = new ArrayList<>();
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            Long vendorSid = ApiThreadLocalUtil.get().getSysUser().getVendorSid();
            if (vendorSid != null){
                //查询供应商下的询价单
                List<PurInquiryVendor> vendorList = purInquiryVendorService.selectPurInquiryVendorList(new PurInquiryVendor().setVendorSid(vendorSid));
                if (CollectionUtil.isNotEmpty(vendorList)){
                    String vendorName = vendorList.get(0).getVendorName();
                    String vendorshortName = vendorList.get(0).getShortName();
                    Long[] inquirySids = vendorList.stream().map(PurInquiryVendor::getInquirySid).toArray(Long[]::new);
                    purInquiry.setInquirySidList(inquirySids).setHandleStatus(HandleStatus.CONFIRMED.getCode());
                    startPage(purInquiry);
                    list = purInquiryService.selectPurInquiryList(purInquiry);
                    list.forEach(item->{
                        item.setVendorName(vendorName);
                        item.setVendorShortName(vendorshortName);
                    });
                    return getDataTable(list);
                }
            }
        }else {
            if (purInquiry.getVendorSidList() != null && purInquiry.getVendorSidList().length > 0){
                //查询供应商下的询价单
                List<PurInquiryVendor> vendorList = purInquiryVendorService.selectPurInquiryVendorList(new PurInquiryVendor().setVendorSidList(purInquiry.getVendorSidList()));
                if (CollectionUtil.isNotEmpty(vendorList)){
                    Long[] inquirySids = vendorList.stream().map(PurInquiryVendor::getInquirySid).toArray(Long[]::new);
                    purInquiry.setInquirySidList(inquirySids);
                }
            }
            startPage(purInquiry);
            list = purInquiryService.selectPurInquiryList(purInquiry);
        }
        return getDataTable(list);
    }

    /**
     * 导出物料询价单主列表
     */
    @PreAuthorize(hasPermi = "ems:pur:inquiry:export")
    @Log(title = "物料询价单主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料询价单主列表", notes = "导出物料询价单主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurInquiry purInquiry) throws IOException {
        if (purInquiry.getVendorSidList() != null && purInquiry.getVendorSidList().length > 0){
            //查询供应商下的询价单
            List<PurInquiryVendor> vendorList = purInquiryVendorService.selectPurInquiryVendorList(new PurInquiryVendor().setVendorSidList(purInquiry.getVendorSidList()));
            if (CollectionUtil.isNotEmpty(vendorList)){
                Long[] inquirySids = vendorList.stream().map(PurInquiryVendor::getInquirySid).toArray(Long[]::new);
                purInquiry.setInquirySidList(inquirySids);
            }
        }
        List<PurInquiry> list = purInquiryService.selectPurInquiryList(purInquiry);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurInquiry> util = new ExcelUtil<>(PurInquiry.class, dataMap);
        util.exportExcel(response, list, "物料询价单主");
    }


    /**
     * 获取物料询价单主详细信息
     */
    @ApiOperation(value = "获取物料询价单主详细信息", notes = "获取物料询价单主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiry.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inquirySid) {
        if (inquirySid == null) {
            throw new CheckedException("参数缺失");
        }
        PurInquiry inquiry = purInquiryService.selectPurInquiryById(inquirySid);
        inquiry.setVendorName(null);
        return AjaxResult.success(inquiry);
    }

    /**
     * 新增物料询价单主
     */
    @ApiOperation(value = "新增物料询价单主", notes = "新增物料询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:add")
    @Log(title = "物料询价单主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurInquiry purInquiry) {
        return toAjax(purInquiryService.insertPurInquiry(purInquiry));
    }

    /**
     * 修改物料询价单主
     */
    @ApiOperation(value = "修改物料询价单主", notes = "修改物料询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:edit")
    @Log(title = "物料询价单主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurInquiry purInquiry) {
        return toAjax(purInquiryService.updatePurInquiry(purInquiry));
    }

    /**
     * 变更物料询价单主
     */
    @ApiOperation(value = "变更物料询价单主", notes = "变更物料询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:change")
    @Log(title = "物料询价单主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurInquiry purInquiry) {
        return toAjax(purInquiryService.changePurInquiry(purInquiry));
    }

    /**
     * 删除物料询价单主
     */
    @ApiOperation(value = "删除物料询价单主", notes = "删除物料询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:remove")
    @Log(title = "物料询价单主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> inquirySids) {
        if (CollectionUtils.isEmpty(inquirySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purInquiryService.deletePurInquiryByIds(inquirySids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:pur:inquiry:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurInquiry purInquiry) {
        return toAjax(purInquiryService.check(purInquiry));
    }

    @ApiOperation(value = "推送", notes = "推送")
    @PreAuthorize(hasPermi = "ems:pur:inquiry:sent")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单主", businessType = BusinessType.CHECK)
    @PostMapping("/sent")
    public AjaxResult sent(@RequestBody List<Long> inquirySids) {
        return toAjax(purInquiryService.sent(inquirySids));
    }

    @ApiOperation(value = "获取物料询价单供应商详细信息-去报价", notes = "获取物料询价单供应商详细信息-去报价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiryVendor.class))
    @PostMapping("/vendor/getList")
    public AjaxResult getVendor(Long inquirySid) {
        if (inquirySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purInquiryVendorService.selectPurInquiryVendorListById(inquirySid));
    }

    /**
     * 获取物料询价单主详细信息-去报价-前的校验
     */
    @ApiOperation(value = "获取物料询价单主详细信息-去报价-前的校验", notes = "获取物料询价单主详细信息-去报价-前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkQuote")
    public AjaxResult checkQuote(@RequestBody PurInquiry purInquiry) {
        if (purInquiry.getInquirySid() == null) {
            throw new CheckedException("参数缺失");
        }
        Object response = purInquiryService.checkQuote(purInquiry);
        if (response != null && StrUtil.isNotBlank((String)response)){
            return AjaxResult.success("该询价单已存在报价信息，正在跳转页面",response);
        }
        return AjaxResult.success();
    }

    /**
     * 获取物料询价单主详细信息-去报价
     */
    @ApiOperation(value = "获取物料询价单主详细信息-去报价", notes = "获取物料询价单主详细信息-去报价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurQuoteBargain.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:toQuote")
    @PostMapping("/toQuote")
    public AjaxResult toQuote(@RequestBody PurInquiry purInquiry) {
        if (purInquiry.getInquirySid() == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purInquiryService.toQuote(purInquiry));
    }

    @ApiOperation(value = "作废", notes = "作废")
    @PreAuthorize(hasPermi = "ems:pur:inquiry:invalid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单主", businessType = BusinessType.CANCEL)
    @PostMapping("/invalid")
    public AjaxResult invalid(@RequestBody List<Long> inquirySids) {
        PurInquiry purInquiry = new PurInquiry();
        Long[] sids = new Long[inquirySids.size()];
        inquirySids.toArray(sids);
        purInquiry.setInquirySidList(sids).setHandleStatus(HandleStatus.INVALID.getCode());
        return toAjax(purInquiryService.check(purInquiry));
    }

    @PreAuthorize(hasPermi = "ems:pur:inquiry:copy")
    @ApiOperation(value = "复制询价单", notes = "复制询价单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/copy")
    public AjaxResult copyInfo(Long inquirySid) {
        if (inquirySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purInquiryService.copy(inquirySid));
    }

}
