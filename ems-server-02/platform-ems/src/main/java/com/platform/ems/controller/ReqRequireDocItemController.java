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
import com.platform.ems.domain.ReqRequireDocItem;
import com.platform.ems.service.IReqRequireDocItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 需求单明细Controller
 *
 * @author linhongwei
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/doc/item")
@Api(tags = "需求单明细")
public class ReqRequireDocItemController extends BaseController {

    @Autowired
    private IReqRequireDocItemService reqRequireDocItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询需求单明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询需求单明细列表", notes = "查询需求单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqRequireDocItem.class))
    public TableDataInfo list(@RequestBody ReqRequireDocItem reqRequireDocItem) {
        startPage();
        List<ReqRequireDocItem> list = reqRequireDocItemService.selectReqRequireDocItemList(reqRequireDocItem);
        return getDataTable(list);
    }

    /**
     * 导出需求单明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:export")
    @Log(title = "需求单明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出需求单明细列表", notes = "导出需求单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReqRequireDocItem reqRequireDocItem) throws IOException {
        List<ReqRequireDocItem> list = reqRequireDocItemService.selectReqRequireDocItemList(reqRequireDocItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ReqRequireDocItem> util = new ExcelUtil<ReqRequireDocItem>(ReqRequireDocItem.class,dataMap);
        util.exportExcel(response, list, "需求单明细"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取需求单明细详细信息
     */
    @ApiOperation(value = "获取需求单明细详细信息", notes = "获取需求单明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqRequireDocItem.class))
    @PreAuthorize(hasPermi = "ems:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long requireDocItemSid) {
        return AjaxResult.success(reqRequireDocItemService.selectReqRequireDocItemById(requireDocItemSid));
    }

    /**
     * 新增需求单明细
     */
    @ApiOperation(value = "新增需求单明细", notes = "新增需求单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:add")
    @Log(title = "需求单明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ReqRequireDocItem reqRequireDocItem) {
        return toAjax(reqRequireDocItemService.insertReqRequireDocItem(reqRequireDocItem));
    }

    /**
     * 修改需求单明细
     */
    @ApiOperation(value = "修改需求单明细", notes = "修改需求单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:edit")
    @Log(title = "需求单明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ReqRequireDocItem reqRequireDocItem) {
        return toAjax(reqRequireDocItemService.updateReqRequireDocItem(reqRequireDocItem));
    }

    /**
     * 删除需求单明细
     */
    @ApiOperation(value = "删除需求单明细", notes = "删除需求单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:remove")
    @Log(title = "需求单明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  requireDocItemSids) {
        return toAjax(reqRequireDocItemService.deleteReqRequireDocItemByIds(requireDocItemSids));
    }
}
