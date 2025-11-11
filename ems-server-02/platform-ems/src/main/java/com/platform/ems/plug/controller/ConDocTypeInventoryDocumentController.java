package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConDocTypeInventoryDocument;
import com.platform.ems.plug.service.IConDocTypeInventoryDocumentService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单据类型_库存凭证Controller
 *
 * @author linhongwei
 * @date 2021-09-17
 */
@RestController
@RequestMapping("/doctype/inventory/document")
@Api(tags = "单据类型_库存凭证")
public class ConDocTypeInventoryDocumentController extends BaseController {

    @Autowired
    private IConDocTypeInventoryDocumentService conDocTypeInventoryDocumentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_库存凭证列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_库存凭证列表", notes = "查询单据类型_库存凭证列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventoryDocument.class))
    public TableDataInfo list(@RequestBody ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        startPage(conDocTypeInventoryDocument);
        List<ConDocTypeInventoryDocument> list = conDocTypeInventoryDocumentService.selectConDocTypeInventoryDocumentList(conDocTypeInventoryDocument);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_库存凭证列表
     */
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_库存凭证列表", notes = "导出单据类型_库存凭证列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeInventoryDocument conDocTypeInventoryDocument) throws IOException {
        List<ConDocTypeInventoryDocument> list = conDocTypeInventoryDocumentService.selectConDocTypeInventoryDocumentList(conDocTypeInventoryDocument);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeInventoryDocument> util = new ExcelUtil<>(ConDocTypeInventoryDocument.class, dataMap);
        util.exportExcel(response, list, "单据类型_库存凭证");
    }


    /**
     * 获取单据类型_库存凭证详细信息
     */
    @ApiOperation(value = "获取单据类型_库存凭证详细信息", notes = "获取单据类型_库存凭证详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventoryDocument.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeInventoryDocumentService.selectConDocTypeInventoryDocumentById(sid));
    }

    /**
     * 新增单据类型_库存凭证
     */
    @ApiOperation(value = "新增单据类型_库存凭证", notes = "新增单据类型_库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        return toAjax(conDocTypeInventoryDocumentService.insertConDocTypeInventoryDocument(conDocTypeInventoryDocument));
    }

    /**
     * 修改单据类型_库存凭证
     */
    @ApiOperation(value = "修改单据类型_库存凭证", notes = "修改单据类型_库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        return toAjax(conDocTypeInventoryDocumentService.updateConDocTypeInventoryDocument(conDocTypeInventoryDocument));
    }

    /**
     * 变更单据类型_库存凭证
     */
    @ApiOperation(value = "变更单据类型_库存凭证", notes = "变更单据类型_库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        return toAjax(conDocTypeInventoryDocumentService.changeConDocTypeInventoryDocument(conDocTypeInventoryDocument));
    }

    /**
     * 删除单据类型_库存凭证
     */
    @ApiOperation(value = "删除单据类型_库存凭证", notes = "删除单据类型_库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeInventoryDocumentService.deleteConDocTypeInventoryDocumentByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        return AjaxResult.success(conDocTypeInventoryDocumentService.changeStatus(conDocTypeInventoryDocument));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_库存凭证", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        conDocTypeInventoryDocument.setConfirmDate(new Date());
        conDocTypeInventoryDocument.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeInventoryDocument.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeInventoryDocumentService.check(conDocTypeInventoryDocument));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_库存凭证下拉接口", notes = "单据类型_库存凭证下拉接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventoryDocument.class))
    public AjaxResult getList(@RequestBody ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        List<ConDocTypeInventoryDocument> list = conDocTypeInventoryDocumentService.selectConDocTypeInventoryDocumentList(conDocTypeInventoryDocument);
        return AjaxResult.success(list);
    }
}
