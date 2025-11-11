package com.platform.ems.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.domain.PurOutsourceInquiryVendor;
import com.platform.ems.domain.PurOutsourceQuoteBargain;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IPurOutsourceInquiryVendorService;
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
import com.platform.ems.domain.PurOutsourceInquiry;
import com.platform.ems.service.IPurOutsourceInquiryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 加工询价单主Controller
 *
 * @author chenkw
 * @date 2022-01-11
 */
@RestController
@RequestMapping("/pur/outsource/inquiry")
@Api(tags = "加工询价单主")
public class PurOutsourceInquiryController extends BaseController {

    @Autowired
    private IPurOutsourceInquiryService purOutsourceInquiryService;
    @Autowired
    private IPurOutsourceInquiryVendorService purOutsourceInquiryVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询加工询价单主列表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询加工询价单主列表", notes = "查询加工询价单主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiry.class))
    public TableDataInfo list(@RequestBody PurOutsourceInquiry purOutsourceInquiry) {
        List<PurOutsourceInquiry> list = new ArrayList<>();
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            Long vendorSid = ApiThreadLocalUtil.get().getSysUser().getVendorSid();
            if (vendorSid != null){
                //查询供应商下的询价单
                List<PurOutsourceInquiryVendor> vendorList = purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorList(new PurOutsourceInquiryVendor().setVendorSid(vendorSid));
                if (CollectionUtil.isNotEmpty(vendorList)) {
                    String vendorName = vendorList.get(0).getVendorName();
                    String vendorshortName = vendorList.get(0).getShortName();
                    Long[] outsourceInquirySid = vendorList.stream().map(PurOutsourceInquiryVendor::getOutsourceInquirySid).toArray(Long[]::new);
                    purOutsourceInquiry.setOutsourceInquirySidList(outsourceInquirySid).setHandleStatus(HandleStatus.CONFIRMED.getCode());
                    startPage(purOutsourceInquiry);
                    list = purOutsourceInquiryService.selectPurOutsourceInquiryList(purOutsourceInquiry);
                    list.forEach(item->{
                        item.setVendorName(vendorName);
                        item.setVendorShortName(vendorshortName);
                    });
                    return getDataTable(list);
                }
            }
        }else {
            if (purOutsourceInquiry.getVendorSidList() != null && purOutsourceInquiry.getVendorSidList().length > 0){
                //查询供应商下的询价单
                List<PurOutsourceInquiryVendor> vendorList = purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorList
                        (new PurOutsourceInquiryVendor().setVendorSidList(purOutsourceInquiry.getVendorSidList()));
                if (CollectionUtil.isNotEmpty(vendorList)){
                    Long[] outsourceInquirySids = vendorList.stream().map(PurOutsourceInquiryVendor::getOutsourceInquirySid).toArray(Long[]::new);
                    purOutsourceInquiry.setOutsourceInquirySidList(outsourceInquirySids);
                }
            }
            startPage(purOutsourceInquiry);
            list = purOutsourceInquiryService.selectPurOutsourceInquiryList(purOutsourceInquiry);
            return getDataTable(list);
        }
        return getDataTable(list);
    }

    /**
     * 导出加工询价单主列表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:export")
    @Log(title = "加工询价单主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出加工询价单主列表", notes = "导出加工询价单主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOutsourceInquiry purOutsourceInquiry) throws IOException {
        if (purOutsourceInquiry.getVendorSidList() != null && purOutsourceInquiry.getVendorSidList().length > 0){
            //查询供应商下的询价单
            List<PurOutsourceInquiryVendor> vendorList = purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorList
                    (new PurOutsourceInquiryVendor().setVendorSidList(purOutsourceInquiry.getVendorSidList()));
            if (CollectionUtil.isNotEmpty(vendorList)){
                Long[] outsourceInquirySids = vendorList.stream().map(PurOutsourceInquiryVendor::getOutsourceInquirySid).toArray(Long[]::new);
                purOutsourceInquiry.setOutsourceInquirySidList(outsourceInquirySids);
            }
        }
        List<PurOutsourceInquiry> list = purOutsourceInquiryService.selectPurOutsourceInquiryList(purOutsourceInquiry);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourceInquiry> util = new ExcelUtil<>(PurOutsourceInquiry.class, dataMap);
        util.exportExcel(response, list, "加工询价单主");
    }


    /**
     * 获取加工询价单主详细信息
     */
    @ApiOperation(value = "获取加工询价单主详细信息", notes = "获取加工询价单主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiry.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long outsourceInquirySid) {
        if (outsourceInquirySid == null) {
            throw new CheckedException("参数缺失");
        }
        PurOutsourceInquiry inquiry = purOutsourceInquiryService.selectPurOutsourceInquiryById(outsourceInquirySid);
        inquiry.setVendorName(null);
        return AjaxResult.success(inquiry);
    }

    /**
     * 新增加工询价单主
     */
    @ApiOperation(value = "新增加工询价单主", notes = "新增加工询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:add")
    @Log(title = "加工询价单主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurOutsourceInquiry purOutsourceInquiry) {
        return toAjax(purOutsourceInquiryService.insertPurOutsourceInquiry(purOutsourceInquiry));
    }

    /**
     * 修改加工询价单主
     */
    @ApiOperation(value = "修改加工询价单主", notes = "修改加工询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:edit")
    @Log(title = "加工询价单主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PurOutsourceInquiry purOutsourceInquiry) {
        return toAjax(purOutsourceInquiryService.updatePurOutsourceInquiry(purOutsourceInquiry));
    }

    /**
     * 变更加工询价单主
     */
    @ApiOperation(value = "变更加工询价单主", notes = "变更加工询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:change")
    @Log(title = "加工询价单主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PurOutsourceInquiry purOutsourceInquiry) {
        return toAjax(purOutsourceInquiryService.changePurOutsourceInquiry(purOutsourceInquiry));
    }

    /**
     * 删除加工询价单主
     */
    @ApiOperation(value = "删除加工询价单主", notes = "删除加工询价单主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:remove")
    @Log(title = "加工询价单主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> outsourceInquirySids) {
        if (CollectionUtils.isEmpty(outsourceInquirySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourceInquiryService.deletePurOutsourceInquiryByIds(outsourceInquirySids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工询价单主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurOutsourceInquiry purOutsourceInquiry) {
        return toAjax(purOutsourceInquiryService.check(purOutsourceInquiry));
    }

    @ApiOperation(value = "推送", notes = "推送")
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:sent")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工询价单主", businessType = BusinessType.CHECK)
    @PostMapping("/sent")
    public AjaxResult sent(@RequestBody List<Long> outsourceInquirySids) {
        return toAjax(purOutsourceInquiryService.sent(outsourceInquirySids));
    }

    @ApiOperation(value = "获取加工询价单供应商详细信息-去报价", notes = "获取加工询价单供应商详细信息-去报价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiryVendor.class))
    @PostMapping("/vendor/getList")
    public AjaxResult getVendor(Long outsourceInquirySid) {
        if (outsourceInquirySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorListById(outsourceInquirySid));
    }

    /**
     * 获取加工物料询价单主详细信息-去报价-前的校验
     */
    @ApiOperation(value = "获取加工物料询价单主详细信息-去报价-前的校验", notes = "获取加工物料询价单主详细信息-去报价-前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkQuote")
    public AjaxResult checkQuote(@RequestBody PurOutsourceInquiry purOutsourceInquiry) {
        if (purOutsourceInquiry.getOutsourceInquirySid() == null) {
            throw new CheckedException("参数缺失");
        }
        Object response = purOutsourceInquiryService.checkQuote(purOutsourceInquiry);
        if (response != null && StrUtil.isNotBlank((String)response)){
            return AjaxResult.success("该加工询价单已存在报价信息，正在跳转页面",response);
        }
        return AjaxResult.success();
    }

    /**
     * 获取加工物料询价单主详细信息-去报价
     */
    @ApiOperation(value = "获取加工物料询价单主详细信息-去报价", notes = "获取加工物料询价单主详细信息-去报价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargain.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:toQuote")
    @PostMapping("/toQuote")
    public AjaxResult toQuote(@RequestBody PurOutsourceInquiry purOutsourceInquiry) {
        if (purOutsourceInquiry.getOutsourceInquirySid() == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceInquiryService.toQuote(purOutsourceInquiry));
    }

    @ApiOperation(value = "作废", notes = "作废")
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:invalid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单主", businessType = BusinessType.CANCEL)
    @PostMapping("/invalid")
    public AjaxResult invalid(@RequestBody List<Long> outsourceInquirySids) {
        PurOutsourceInquiry purOutsourceInquiry = new PurOutsourceInquiry();
        Long[] sids = new Long[outsourceInquirySids.size()];
        outsourceInquirySids.toArray(sids);
        purOutsourceInquiry.setOutsourceInquirySidList(sids).setHandleStatus(HandleStatus.INVALID.getCode());
        return toAjax(purOutsourceInquiryService.check(purOutsourceInquiry));
    }

    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:copy")
    @ApiOperation(value = "复制询价单", notes = "复制询价单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/copy")
    public AjaxResult copyInfo(Long outsourceInquirySid) {
        if (outsourceInquirySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceInquiryService.copy(outsourceInquirySid));
    }
}
