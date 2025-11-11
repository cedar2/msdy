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
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ConCheckStandardItem;
import com.platform.ems.service.IConCheckStandardItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 检测标准/项目关联Controller
 *
 * @author qhq
 * @date 2021-11-01
 */
@RestController
@RequestMapping("/check/standard/item")
@Api(tags = "检测标准/项目关联")
public class ConCheckStandardItemController extends BaseController {

    @Autowired
    private IConCheckStandardItemService conCheckStandardItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询检测标准/项目关联列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询检测标准/项目关联列表", notes = "查询检测标准/项目关联列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckStandardItem.class))
    public TableDataInfo list(@RequestBody ConCheckStandardItem conCheckStandardItem) {
        startPage(conCheckStandardItem);
        List<ConCheckStandardItem> list = conCheckStandardItemService.selectConCheckStandardItemList(conCheckStandardItem);
        return getDataTable(list);
    }

    /**
     * 导出检测标准/项目关联列表
     */
    @Log(title = "检测标准/项目关联", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出检测标准/项目关联列表", notes = "导出检测标准/项目关联列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCheckStandardItem conCheckStandardItem) throws IOException {
        List<ConCheckStandardItem> list = conCheckStandardItemService.selectConCheckStandardItemList(conCheckStandardItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConCheckStandardItem> util = new ExcelUtil<ConCheckStandardItem>(ConCheckStandardItem.class,dataMap);
        util.exportExcel(response, list, "检测标准-项目关联");
    }


    /**
     * 获取检测标准/项目关联详细信息
     */
    @ApiOperation(value = "获取检测标准/项目关联详细信息", notes = "获取检测标准/项目关联详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckStandardItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long checkStandardItemSid) {
                    if(checkStandardItemSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conCheckStandardItemService.selectConCheckStandardItemById(checkStandardItemSid));
    }

    /**
     * 新增检测标准/项目关联
     */
    @ApiOperation(value = "新增检测标准/项目关联", notes = "新增检测标准/项目关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:add")
    @Log(title = "检测标准/项目关联", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCheckStandardItem conCheckStandardItem) {
        return toAjax(conCheckStandardItemService.insertConCheckStandardItem(conCheckStandardItem));
    }

    /**
     * 修改检测标准/项目关联
     */
    @ApiOperation(value = "修改检测标准/项目关联", notes = "修改检测标准/项目关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "检测标准/项目关联", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCheckStandardItem conCheckStandardItem) {
        return toAjax(conCheckStandardItemService.updateConCheckStandardItem(conCheckStandardItem));
    }

    /**
     * 变更检测标准/项目关联
     */
    @ApiOperation(value = "变更检测标准/项目关联", notes = "变更检测标准/项目关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "检测标准/项目关联", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConCheckStandardItem conCheckStandardItem) {
        return toAjax(conCheckStandardItemService.changeConCheckStandardItem(conCheckStandardItem));
    }

    /**
     * 删除检测标准/项目关联
     */
    @ApiOperation(value = "删除检测标准/项目关联", notes = "删除检测标准/项目关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准/项目关联", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  checkStandardItemSids) {
        if(CollectionUtils.isEmpty( checkStandardItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCheckStandardItemService.deleteConCheckStandardItemByIds(checkStandardItemSids));
    }

}
