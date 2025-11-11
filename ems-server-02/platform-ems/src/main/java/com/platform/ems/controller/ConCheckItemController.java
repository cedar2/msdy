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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ConCheckItem;
import com.platform.ems.service.IConCheckItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 检测项目Controller
 *
 * @author qhq
 * @date 2021-11-01
 */
@RestController
@RequestMapping("/check/item")
@Api(tags = "检测项目")
public class ConCheckItemController extends BaseController {

    @Autowired
    private IConCheckItemService conCheckItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询检测项目列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询检测项目列表", notes = "查询检测项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckItem.class))
    public TableDataInfo list(@RequestBody ConCheckItem conCheckItem) {
        startPage(conCheckItem);
        List<ConCheckItem> list = conCheckItemService.selectConCheckItemList(conCheckItem);
        return getDataTable(list);
    }

    /**
     * 导出检测项目列表
     */
    @Log(title = "检测项目", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出检测项目列表", notes = "导出检测项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCheckItem conCheckItem) throws IOException {
        List<ConCheckItem> list = conCheckItemService.selectConCheckItemList(conCheckItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConCheckItem> util = new ExcelUtil<>(ConCheckItem.class, dataMap);
        util.exportExcel(response, list, "检测项目");
    }


    /**
     * 获取检测项目详细信息
     */
    @ApiOperation(value = "获取检测项目详细信息", notes = "获取检测项目详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conCheckItemService.selectConCheckItemById(sid));
    }

    /**
     * 新增检测项目
     */
    @ApiOperation(value = "新增检测项目", notes = "新增检测项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCheckItem conCheckItem) {
        return toAjax(conCheckItemService.insertConCheckItem(conCheckItem));
    }

    /**
     * 修改检测项目
     */
    @ApiOperation(value = "修改检测项目", notes = "修改检测项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCheckItem conCheckItem) {
        return toAjax(conCheckItemService.updateConCheckItem(conCheckItem));
    }

    /**
     * 变更检测项目
     */
    @ApiOperation(value = "变更检测项目", notes = "变更检测项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测项目", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConCheckItem conCheckItem) {
        return toAjax(conCheckItemService.changeConCheckItem(conCheckItem));
    }

    /**
     * 删除检测项目
     */
    @ApiOperation(value = "删除检测项目", notes = "删除检测项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测项目", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCheckItemService.deleteConCheckItemByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测项目", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCheckItem conCheckItem) {
        return AjaxResult.success(conCheckItemService.changeStatus(conCheckItem));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测项目", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCheckItem conCheckItem) {
        conCheckItem.setConfirmDate(new Date());
        conCheckItem.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCheckItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCheckItemService.check(conCheckItem));
    }

}
