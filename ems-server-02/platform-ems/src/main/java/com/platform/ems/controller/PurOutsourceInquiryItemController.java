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
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.PurOutsourceInquiryItem;
import com.platform.ems.service.IPurOutsourceInquiryItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 加工询价单明细Controller
 *
 * @author chenkw
 * @date 2022-01-11
 */
@RestController
@RequestMapping("/pur/outsource/inquiry/item")
@Api(tags = "加工询价单明细")
public class PurOutsourceInquiryItemController extends BaseController {

    @Autowired
    private IPurOutsourceInquiryItemService purOutsourceInquiryItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询加工询价单明细列表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询加工询价单明细列表", notes = "查询加工询价单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiryItem.class))
    public TableDataInfo list(@RequestBody PurOutsourceInquiryItem purOutsourceInquiryItem) {
        startPage(purOutsourceInquiryItem);
        List<PurOutsourceInquiryItem> list = purOutsourceInquiryItemService.selectPurOutsourceInquiryItemList(purOutsourceInquiryItem);
        return getDataTable(list);
    }

    /**
     * 导出加工询价单明细列表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:export")
    @Log(title = "加工询价单明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出加工询价单明细列表", notes = "导出加工询价单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOutsourceInquiryItem purOutsourceInquiryItem) throws IOException {
        List<PurOutsourceInquiryItem> list = purOutsourceInquiryItemService.selectPurOutsourceInquiryItemList(purOutsourceInquiryItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourceInquiryItem> util = new ExcelUtil<>(PurOutsourceInquiryItem.class,dataMap);
        util.exportExcel(response, list, "加工询价单明细");
    }


    /**
     * 获取加工询价单明细详细信息
     */
    @ApiOperation(value = "获取加工询价单明细详细信息", notes = "获取加工询价单明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiryItem.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long outsourceInquiryItemSid) {
                    if(outsourceInquiryItemSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(purOutsourceInquiryItemService.selectPurOutsourceInquiryItemById(outsourceInquiryItemSid));
    }

    /**
     * 新增加工询价单明细
     */
    @ApiOperation(value = "新增加工询价单明细", notes = "新增加工询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:add")
    @Log(title = "加工询价单明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurOutsourceInquiryItem purOutsourceInquiryItem) {
        return toAjax(purOutsourceInquiryItemService.insertPurOutsourceInquiryItem(purOutsourceInquiryItem));
    }

    /**
     * 修改加工询价单明细
     */
    @ApiOperation(value = "修改加工询价单明细", notes = "修改加工询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:edit")
    @Log(title = "加工询价单明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurOutsourceInquiryItem purOutsourceInquiryItem) {
        return toAjax(purOutsourceInquiryItemService.updatePurOutsourceInquiryItem(purOutsourceInquiryItem));
    }

    /**
     * 变更加工询价单明细
     */
    @ApiOperation(value = "变更加工询价单明细", notes = "变更加工询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:change")
    @Log(title = "加工询价单明细", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurOutsourceInquiryItem purOutsourceInquiryItem) {
        return toAjax(purOutsourceInquiryItemService.changePurOutsourceInquiryItem(purOutsourceInquiryItem));
    }

    /**
     * 删除加工询价单明细
     */
    @ApiOperation(value = "删除加工询价单明细", notes = "删除加工询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:remove")
    @Log(title = "加工询价单明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  outsourceInquiryItemSids) {
        if(CollectionUtils.isEmpty( outsourceInquiryItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourceInquiryItemService.deletePurOutsourceInquiryItemByIds(outsourceInquiryItemSids));
    }

    /**
     * 查询加工询价单明细报表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:item:report")
    @PostMapping("/report")
    @ApiOperation(value = "查询加工询价单明细报表", notes = "查询加工询价单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiryItem.class))
    public TableDataInfo report(@RequestBody PurOutsourceInquiryItem purOutsourceInquiryItem) {
        startPage(purOutsourceInquiryItem);
        List<PurOutsourceInquiryItem> list = purOutsourceInquiryItemService.getReportForm(purOutsourceInquiryItem);
        return getDataTable(list);
    }

}
