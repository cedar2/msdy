package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.domain.dto.request.InvIntransitReportRequest;
import com.platform.ems.domain.dto.response.InvIntransitReportResponse;
import com.platform.common.utils.bean.BeanCopyUtils;
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
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import com.platform.ems.domain.InvIntransitInventory;
import com.platform.ems.service.IInvIntransitInventoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 调拨在途库存Controller
 *
 * @author linhongwei
 * @date 2021-06-04
 */
@RestController
@RequestMapping("/invIntransitInventory")
@Api(tags = "调拨在途库存")
public class InvIntransitInventoryController extends BaseController {

    @Autowired
    private IInvIntransitInventoryService invIntransitInventoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询调拨在途库存列表
     */
//    @PreAuthorize(hasPermi = "ems:inventory:list")
    @PostMapping("/report")
    @ApiOperation(value = "查询调拨在途库存列表", notes = "查询调拨在途库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvIntransitReportResponse.class))
    public TableDataInfo list(@RequestBody InvIntransitReportRequest request) {
        InvIntransitInventory invIntransitInventory = new InvIntransitInventory();
        BeanCopyUtils.copyProperties(request, invIntransitInventory);
        startPage(invIntransitInventory);
        List<InvIntransitInventory> list = invIntransitInventoryService.selectInvIntransitInventoryList(invIntransitInventory);
        return getDataTable(list, InvIntransitReportResponse::new);
    }

    /**
     * 导出调拨在途库存列表
     */
    @PreAuthorize(hasPermi = "ems::Intransinventory:export")
    @Log(title = "调拨在途库存", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出调拨在途库存列表", notes = "导出调拨在途库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvIntransitInventory invIntransitInventory) throws IOException {
        List<InvIntransitInventory> list = invIntransitInventoryService.selectInvIntransitInventoryList(invIntransitInventory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvIntransitReportResponse> util = new ExcelUtil<>(InvIntransitReportResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,InvIntransitReportResponse::new), "调拨在途库存");
    }

    /**
     * 导入调拨在途库存
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入调拨在途库存", notes = "导入调拨在途库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<InvIntransitInventory> util = new ExcelUtil<InvIntransitInventory>(InvIntransitInventory.class);
        List<InvIntransitInventory> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(invIntransitInventory -> {
                invIntransitInventoryService.insertInvIntransitInventory(invIntransitInventory);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载调拨在途库存导入模板", notes = "下载调拨在途库存导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<InvIntransitInventory> util = new ExcelUtil<InvIntransitInventory>(InvIntransitInventory.class);
        util.importTemplateExcel(response, "调拨在途库存导入模板");
    }


    /**
     * 获取调拨在途库存详细信息
     */
    @ApiOperation(value = "获取调拨在途库存详细信息", notes = "获取调拨在途库存详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvIntransitInventory.class))
    @PreAuthorize(hasPermi = "ems:inventory:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long intransitStockSid) {
        if (intransitStockSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invIntransitInventoryService.selectInvIntransitInventoryById(intransitStockSid));
    }

    /**
     * 新增调拨在途库存
     */
    @ApiOperation(value = "新增调拨在途库存", notes = "新增调拨在途库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:add")
    @Log(title = "调拨在途库存", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid InvIntransitInventory invIntransitInventory) {
        return toAjax(invIntransitInventoryService.insertInvIntransitInventory(invIntransitInventory));
    }

    /**
     * 修改调拨在途库存
     */
    @ApiOperation(value = "修改调拨在途库存", notes = "修改调拨在途库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:edit")
    @Log(title = "调拨在途库存", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody InvIntransitInventory invIntransitInventory) {
        return toAjax(invIntransitInventoryService.updateInvIntransitInventory(invIntransitInventory));
    }

    /**
     * 变更调拨在途库存
     */
    @ApiOperation(value = "变更调拨在途库存", notes = "变更调拨在途库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:change")
    @Log(title = "调拨在途库存", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody InvIntransitInventory invIntransitInventory) {
        return toAjax(invIntransitInventoryService.changeInvIntransitInventory(invIntransitInventory));
    }

    /**
     * 删除调拨在途库存
     */
    @ApiOperation(value = "删除调拨在途库存", notes = "删除调拨在途库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:remove")
    @Log(title = "调拨在途库存", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> intransitStockSids) {
        if (ArrayUtil.isEmpty(intransitStockSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invIntransitInventoryService.deleteInvIntransitInventoryByIds(intransitStockSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "调拨在途库存", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:inventory:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvIntransitInventory invIntransitInventory) {
        return AjaxResult.success(invIntransitInventoryService.changeStatus(invIntransitInventory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:inventory:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "调拨在途库存", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvIntransitInventory invIntransitInventory) {
        invIntransitInventory.setConfirmDate(new Date());
        invIntransitInventory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        invIntransitInventory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(invIntransitInventoryService.check(invIntransitInventory));
    }

}
