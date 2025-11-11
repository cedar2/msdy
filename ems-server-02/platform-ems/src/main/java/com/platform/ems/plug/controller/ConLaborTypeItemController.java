package com.platform.ems.plug.controller;

import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.exception.base.BaseException;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConLaborTypeItem;
import com.platform.ems.plug.service.IConLaborTypeItemService;
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
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工价项信息Controller
 *
 * @author c
 * @date 2021-06-10
 */
@RestController
@RequestMapping("/con/laborType/item")
@Api(tags = "工价项信息")
public class ConLaborTypeItemController extends BaseController {

    @Autowired
    private IConLaborTypeItemService conLaborTypeItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工价项信息列表
     */
    @PreAuthorize(hasPermi = "ems:con:laborType:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工价项信息列表", notes = "查询工价项信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborTypeItem.class))
    public TableDataInfo list(@RequestBody ConLaborTypeItem conLaborTypeItem) {
        startPage(conLaborTypeItem);
        List<ConLaborTypeItem> list = conLaborTypeItemService.selectTypeItemList(conLaborTypeItem);
        return getDataTable(list);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "查询工价项信息列表", notes = "查询工价项信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborTypeItem.class))
    public AjaxResult getList(Long laborTypeSid) {
        List<ConLaborTypeItem> list = conLaborTypeItemService.selectConLaborTypeItemList(new ConLaborTypeItem().setLaborTypeSid(laborTypeSid));
        return AjaxResult.success(list);
    }

    /**
     * 导出工价项信息列表
     */
    @PreAuthorize(hasPermi = "ems:con:laborType:item:export")
    @Log(title = "工价项信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工价项信息列表", notes = "导出工价项信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConLaborTypeItem conLaborTypeItem) throws IOException {
        List<ConLaborTypeItem> list = conLaborTypeItemService.selectConLaborTypeItemList(conLaborTypeItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConLaborTypeItem> util = new ExcelUtil<>(ConLaborTypeItem.class, dataMap);
        util.exportExcel(response, list, "工价项信息");
    }


    /**
     * 获取工价项信息详细信息
     */
    @ApiOperation(value = "获取工价项信息详细信息", notes = "获取工价项信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborTypeItem.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long laborTypeItemSid) {
        if (laborTypeItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conLaborTypeItemService.selectConLaborTypeItemById(laborTypeItemSid));
    }

    /**
     * 新增工价项信息
     */
    @ApiOperation(value = "新增工价项信息", notes = "新增工价项信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:item:add")
    @Log(title = "工价项信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConLaborTypeItem conLaborTypeItem) {
        return toAjax(conLaborTypeItemService.insertConLaborTypeItem(conLaborTypeItem));
    }

    /**
     * 修改工价项信息
     */
    @ApiOperation(value = "修改工价项信息", notes = "修改工价项信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:item:edit")
    @Log(title = "工价项信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConLaborTypeItem conLaborTypeItem) {
        if (ConstantsEms.CHECK_STATUS.equals(conLaborTypeItem.getHandleStatus())) {
            return AjaxResult.error("已确认不可编辑");
        }
        return toAjax(conLaborTypeItemService.updateConLaborTypeItem(conLaborTypeItem));
    }

    /**
     * 变更工价项信息
     */
    @ApiOperation(value = "变更工价项信息", notes = "变更工价项信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:item:change")
    @Log(title = "工价项信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConLaborTypeItem conLaborTypeItem) {
        if (ConstantsEms.SAVA_STATUS.equals(conLaborTypeItem.getHandleStatus())) {
            return AjaxResult.error("未确认不可变更");
        }
        return toAjax(conLaborTypeItemService.changeConLaborTypeItem(conLaborTypeItem));
    }

    /**
     * 删除工价项信息
     */
    @ApiOperation(value = "删除工价项信息", notes = "删除工价项信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:item:remove")
    @Log(title = "工价项信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> laborTypeItemSids) {
        if (ArrayUtil.isEmpty(laborTypeItemSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conLaborTypeItemService.deleteConLaborTypeItemByIds(laborTypeItemSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工价项信息", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:laborType:item:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConLaborTypeItem conLaborTypeItem) {
        return AjaxResult.success(conLaborTypeItemService.changeStatus(conLaborTypeItem));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:laborType:item:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工价项信息", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConLaborTypeItem conLaborTypeItem) {
        conLaborTypeItem.setConfirmDate(new Date());
        conLaborTypeItem.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conLaborTypeItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conLaborTypeItemService.check(conLaborTypeItem));
    }

    @PostMapping("/getConLaborTypeItemList")
    @ApiOperation(value = "工价类型下拉列表", notes = "工价类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborTypeItem.class))
    public AjaxResult getConLaborTypeItemList() {
        return AjaxResult.success(conLaborTypeItemService.getConLaborTypeItemList());
    }

    /**
     * 导入工价项信息
     */
    @PostMapping("/import")
    @PreAuthorize(hasPermi = "ems:con:laborType:item:import")
    @ApiOperation(value = "导入工价项信息", notes = "导入工价项信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = conLaborTypeItemService.importData(file);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }

}
