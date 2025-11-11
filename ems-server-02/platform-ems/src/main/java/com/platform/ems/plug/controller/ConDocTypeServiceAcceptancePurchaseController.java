package com.platform.ems.plug.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptancePurchase;
import com.platform.ems.plug.service.IConDocTypeServiceAcceptancePurchaseService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单据类型_服务采购验收单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/acceptance/servicePurchase")
@Api(tags = "单据类型_服务采购验收单")
public class ConDocTypeServiceAcceptancePurchaseController extends BaseController {

    @Autowired
    private IConDocTypeServiceAcceptancePurchaseService conDocTypeServiceAcceptancePurchaseService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询单据类型_服务采购验收单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_服务采购验收单列表", notes = "查询单据类型_服务采购验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeServiceAcceptancePurchase.class))
    public TableDataInfo list(@RequestBody ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        startPage(conDocTypeServiceAcceptancePurchase);
        List<ConDocTypeServiceAcceptancePurchase> list = conDocTypeServiceAcceptancePurchaseService.selectConDocTypeServiceAcceptancePurchaseList(conDocTypeServiceAcceptancePurchase);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_服务采购验收单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:export")
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_服务采购验收单列表", notes = "导出单据类型_服务采购验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) throws IOException {
        List<ConDocTypeServiceAcceptancePurchase> list = conDocTypeServiceAcceptancePurchaseService.selectConDocTypeServiceAcceptancePurchaseList(conDocTypeServiceAcceptancePurchase);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeServiceAcceptancePurchase> util = new ExcelUtil<ConDocTypeServiceAcceptancePurchase>(ConDocTypeServiceAcceptancePurchase.class, dataMap);
        util.exportExcel(response, list, "单据类型_服务采购验收单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入单据类型_服务采购验收单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_服务采购验收单", notes = "导入单据类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConDocTypeServiceAcceptancePurchase> util = new ExcelUtil<ConDocTypeServiceAcceptancePurchase>(ConDocTypeServiceAcceptancePurchase.class);
        List<ConDocTypeServiceAcceptancePurchase> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conDocTypeServiceAcceptancePurchase -> {
                conDocTypeServiceAcceptancePurchaseService.insertConDocTypeServiceAcceptancePurchase(conDocTypeServiceAcceptancePurchase);
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


    @ApiOperation(value = "下载单据类型_服务采购验收单导入模板", notes = "下载单据类型_服务采购验收单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeServiceAcceptancePurchase> util = new ExcelUtil<ConDocTypeServiceAcceptancePurchase>(ConDocTypeServiceAcceptancePurchase.class);
        util.importTemplateExcel(response, "单据类型_服务采购验收单导入模板");
    }


    /**
     * 获取单据类型_服务采购验收单详细信息
     */
    @ApiOperation(value = "获取单据类型_服务采购验收单详细信息", notes = "获取单据类型_服务采购验收单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeServiceAcceptancePurchase.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeServiceAcceptancePurchaseService.selectConDocTypeServiceAcceptancePurchaseById(sid));
    }

    /**
     * 新增单据类型_服务采购验收单
     */
    @ApiOperation(value = "新增单据类型_服务采购验收单", notes = "新增单据类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:add")
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        return toAjax(conDocTypeServiceAcceptancePurchaseService.insertConDocTypeServiceAcceptancePurchase(conDocTypeServiceAcceptancePurchase));
    }

    /**
     * 修改单据类型_服务采购验收单
     */
    @ApiOperation(value = "修改单据类型_服务采购验收单", notes = "修改单据类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:edit")
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        return toAjax(conDocTypeServiceAcceptancePurchaseService.updateConDocTypeServiceAcceptancePurchase(conDocTypeServiceAcceptancePurchase));
    }

    /**
     * 变更单据类型_服务采购验收单
     */
    @ApiOperation(value = "变更单据类型_服务采购验收单", notes = "变更单据类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:change")
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        return toAjax(conDocTypeServiceAcceptancePurchaseService.changeConDocTypeServiceAcceptancePurchase(conDocTypeServiceAcceptancePurchase));
    }

    /**
     * 删除单据类型_服务采购验收单
     */
    @ApiOperation(value = "删除单据类型_服务采购验收单", notes = "删除单据类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:remove")
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeServiceAcceptancePurchaseService.deleteConDocTypeServiceAcceptancePurchaseByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        return AjaxResult.success(conDocTypeServiceAcceptancePurchaseService.changeStatus(conDocTypeServiceAcceptancePurchase));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptPurchase:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_服务采购验收单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        conDocTypeServiceAcceptancePurchase.setConfirmDate(new Date());
        conDocTypeServiceAcceptancePurchase.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeServiceAcceptancePurchase.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeServiceAcceptancePurchaseService.check(conDocTypeServiceAcceptancePurchase));
    }

}
