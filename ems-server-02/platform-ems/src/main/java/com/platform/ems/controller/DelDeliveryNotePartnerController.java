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
import com.platform.ems.domain.DelDeliveryNotePartner;
import com.platform.ems.service.IDelDeliveryNotePartnerService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 交货单-合作伙伴Controller
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/note/partner")
@Api(tags = "交货单-合作伙伴")
public class DelDeliveryNotePartnerController extends BaseController {

    @Autowired
    private IDelDeliveryNotePartnerService delDeliveryNotePartnerService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询交货单-合作伙伴列表
     */
    @PreAuthorize(hasPermi = "ems:partner:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询交货单-合作伙伴列表", notes = "查询交货单-合作伙伴列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNotePartner.class))
    public TableDataInfo list(@RequestBody DelDeliveryNotePartner delDeliveryNotePartner) {
        startPage();
        List<DelDeliveryNotePartner> list = delDeliveryNotePartnerService.selectDelDeliveryNotePartnerList(delDeliveryNotePartner);
        return getDataTable(list);
    }

    /**
     * 导出交货单-合作伙伴列表
     */
    @PreAuthorize(hasPermi = "ems:partner:export")
    @Log(title = "交货单-合作伙伴", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出交货单-合作伙伴列表", notes = "导出交货单-合作伙伴列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DelDeliveryNotePartner delDeliveryNotePartner) throws IOException {
        List<DelDeliveryNotePartner> list = delDeliveryNotePartnerService.selectDelDeliveryNotePartnerList(delDeliveryNotePartner);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<DelDeliveryNotePartner> util = new ExcelUtil<DelDeliveryNotePartner>(DelDeliveryNotePartner.class,dataMap);
        util.exportExcel(response, list, "交货单-合作伙伴");
    }

    /**
     * 获取交货单-合作伙伴详细信息
     */
    @ApiOperation(value = "获取交货单-合作伙伴详细信息", notes = "获取交货单-合作伙伴详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNotePartner.class))
    @PreAuthorize(hasPermi = "ems:partner:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deliveryNotePartnerSid) {
                    if(deliveryNotePartnerSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(delDeliveryNotePartnerService.selectDelDeliveryNotePartnerById(deliveryNotePartnerSid));
    }

    /**
     * 新增交货单-合作伙伴
     */
    @ApiOperation(value = "新增交货单-合作伙伴", notes = "新增交货单-合作伙伴")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:partner:add")
    @Log(title = "交货单-合作伙伴", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DelDeliveryNotePartner delDeliveryNotePartner) {
        return toAjax(delDeliveryNotePartnerService.insertDelDeliveryNotePartner(delDeliveryNotePartner));
    }

    /**
     * 修改交货单-合作伙伴
     */
    @ApiOperation(value = "修改交货单-合作伙伴", notes = "修改交货单-合作伙伴")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:partner:edit")
    @Log(title = "交货单-合作伙伴", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody DelDeliveryNotePartner delDeliveryNotePartner) {
        return toAjax(delDeliveryNotePartnerService.updateDelDeliveryNotePartner(delDeliveryNotePartner));
    }

    /**
     * 删除交货单-合作伙伴
     */
    @ApiOperation(value = "删除交货单-合作伙伴", notes = "删除交货单-合作伙伴")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:partner:remove")
    @Log(title = "交货单-合作伙伴", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  deliveryNotePartnerSids) {
        if(ArrayUtil.isEmpty( deliveryNotePartnerSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(delDeliveryNotePartnerService.deleteDelDeliveryNotePartnerByIds(deliveryNotePartnerSids));
    }
}
