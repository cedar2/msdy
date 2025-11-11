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
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptanceSale;
import com.platform.ems.plug.service.IConDocTypeServiceAcceptanceSaleService;
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
 * 单据类型_服务销售验收单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/acceptance/serviceSale")
@Api(tags = "单据类型_服务销售验收单")
public class ConDocTypeServiceAcceptanceSaleController extends BaseController {

    @Autowired
    private IConDocTypeServiceAcceptanceSaleService conDocTypeServiceAcceptanceSaleService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询单据类型_服务销售验收单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_服务销售验收单列表", notes = "查询单据类型_服务销售验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeServiceAcceptanceSale.class))
    public TableDataInfo list(@RequestBody ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        startPage(conDocTypeServiceAcceptanceSale);
        List<ConDocTypeServiceAcceptanceSale> list = conDocTypeServiceAcceptanceSaleService.selectConDocTypeServiceAcceptanceSaleList(conDocTypeServiceAcceptanceSale);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_服务销售验收单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:export")
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_服务销售验收单列表", notes = "导出单据类型_服务销售验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) throws IOException {
        List<ConDocTypeServiceAcceptanceSale> list = conDocTypeServiceAcceptanceSaleService.selectConDocTypeServiceAcceptanceSaleList(conDocTypeServiceAcceptanceSale);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeServiceAcceptanceSale> util = new ExcelUtil<ConDocTypeServiceAcceptanceSale>(ConDocTypeServiceAcceptanceSale.class, dataMap);
        util.exportExcel(response, list, "单据类型_服务销售验收单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入单据类型_服务销售验收单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_服务销售验收单", notes = "导入单据类型_服务销售验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConDocTypeServiceAcceptanceSale> util = new ExcelUtil<ConDocTypeServiceAcceptanceSale>(ConDocTypeServiceAcceptanceSale.class);
        List<ConDocTypeServiceAcceptanceSale> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conDocTypeServiceAcceptanceSale -> {
                conDocTypeServiceAcceptanceSaleService.insertConDocTypeServiceAcceptanceSale(conDocTypeServiceAcceptanceSale);
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


    @ApiOperation(value = "下载单据类型_服务销售验收单导入模板", notes = "下载单据类型_服务销售验收单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeServiceAcceptanceSale> util = new ExcelUtil<ConDocTypeServiceAcceptanceSale>(ConDocTypeServiceAcceptanceSale.class);
        util.importTemplateExcel(response, "单据类型_服务销售验收单导入模板");
    }


    /**
     * 获取单据类型_服务销售验收单详细信息
     */
    @ApiOperation(value = "获取单据类型_服务销售验收单详细信息", notes = "获取单据类型_服务销售验收单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeServiceAcceptanceSale.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeServiceAcceptanceSaleService.selectConDocTypeServiceAcceptanceSaleById(sid));
    }

    /**
     * 新增单据类型_服务销售验收单
     */
    @ApiOperation(value = "新增单据类型_服务销售验收单", notes = "新增单据类型_服务销售验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:add")
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        return toAjax(conDocTypeServiceAcceptanceSaleService.insertConDocTypeServiceAcceptanceSale(conDocTypeServiceAcceptanceSale));
    }

    /**
     * 修改单据类型_服务销售验收单
     */
    @ApiOperation(value = "修改单据类型_服务销售验收单", notes = "修改单据类型_服务销售验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:edit")
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        return toAjax(conDocTypeServiceAcceptanceSaleService.updateConDocTypeServiceAcceptanceSale(conDocTypeServiceAcceptanceSale));
    }

    /**
     * 变更单据类型_服务销售验收单
     */
    @ApiOperation(value = "变更单据类型_服务销售验收单", notes = "变更单据类型_服务销售验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:change")
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        return toAjax(conDocTypeServiceAcceptanceSaleService.changeConDocTypeServiceAcceptanceSale(conDocTypeServiceAcceptanceSale));
    }

    /**
     * 删除单据类型_服务销售验收单
     */
    @ApiOperation(value = "删除单据类型_服务销售验收单", notes = "删除单据类型_服务销售验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:remove")
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeServiceAcceptanceSaleService.deleteConDocTypeServiceAcceptanceSaleByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        return AjaxResult.success(conDocTypeServiceAcceptanceSaleService.changeStatus(conDocTypeServiceAcceptanceSale));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:docTypeServiceAcceptSale:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_服务销售验收单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale) {
        conDocTypeServiceAcceptanceSale.setConfirmDate(new Date());
        conDocTypeServiceAcceptanceSale.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeServiceAcceptanceSale.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeServiceAcceptanceSaleService.check(conDocTypeServiceAcceptanceSale));
    }

}
