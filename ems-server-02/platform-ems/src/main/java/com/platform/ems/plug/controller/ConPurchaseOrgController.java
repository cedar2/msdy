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
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConPurchaseOrg;
import com.platform.ems.plug.service.IConPurchaseOrgService;
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
 * 采购组织Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/purchase/org")
@Api(tags = "采购组织")
public class ConPurchaseOrgController extends BaseController {

    @Autowired
    private IConPurchaseOrgService conPurchaseOrgService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询采购组织列表
     */
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询采购组织列表", notes = "查询采购组织列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseOrg.class))
    public TableDataInfo list(@RequestBody ConPurchaseOrg conPurchaseOrg) {
        startPage(conPurchaseOrg);
        List<ConPurchaseOrg> list = conPurchaseOrgService.selectConPurchaseOrgList(conPurchaseOrg);
        return getDataTable(list);
    }

    /**
     * 导出采购组织列表
     */
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:export")
    @Log(title = "采购组织", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购组织列表", notes = "导出采购组织列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPurchaseOrg conPurchaseOrg) throws IOException {
        List<ConPurchaseOrg> list = conPurchaseOrgService.selectConPurchaseOrgList(conPurchaseOrg);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPurchaseOrg> util = new ExcelUtil<ConPurchaseOrg>(ConPurchaseOrg.class, dataMap);
        util.exportExcel(response, list, "采购组织" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入采购组织
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购组织", notes = "导入采购组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConPurchaseOrg> util = new ExcelUtil<ConPurchaseOrg>(ConPurchaseOrg.class);
        List<ConPurchaseOrg> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conPurchaseOrg -> {
                conPurchaseOrgService.insertConPurchaseOrg(conPurchaseOrg);
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


    @ApiOperation(value = "下载采购组织导入模板", notes = "下载采购组织导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConPurchaseOrg> util = new ExcelUtil<ConPurchaseOrg>(ConPurchaseOrg.class);
        util.importTemplateExcel(response, "采购组织导入模板");
    }


    /**
     * 获取采购组织详细信息
     */
    @ApiOperation(value = "获取采购组织详细信息", notes = "获取采购组织详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseOrg.class))
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPurchaseOrgService.selectConPurchaseOrgById(sid));
    }

    /**
     * 新增采购组织
     */
    @ApiOperation(value = "新增采购组织", notes = "新增采购组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:add")
    @Log(title = "采购组织", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPurchaseOrg conPurchaseOrg) {
        return toAjax(conPurchaseOrgService.insertConPurchaseOrg(conPurchaseOrg));
    }

    /**
     * 修改采购组织
     */
    @ApiOperation(value = "修改采购组织", notes = "修改采购组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:edit")
    @Log(title = "采购组织", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConPurchaseOrg conPurchaseOrg) {
        return toAjax(conPurchaseOrgService.updateConPurchaseOrg(conPurchaseOrg));
    }

    /**
     * 变更采购组织
     */
    @ApiOperation(value = "变更采购组织", notes = "变更采购组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:change")
    @Log(title = "采购组织", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPurchaseOrg conPurchaseOrg) {
        return toAjax(conPurchaseOrgService.changeConPurchaseOrg(conPurchaseOrg));
    }

    /**
     * 删除采购组织
     */
    @ApiOperation(value = "删除采购组织", notes = "删除采购组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:remove")
    @Log(title = "采购组织", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPurchaseOrgService.deleteConPurchaseOrgByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组织", businessType = BusinessType.UPDATE)
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPurchaseOrg conPurchaseOrg) {
        return AjaxResult.success(conPurchaseOrgService.changeStatus(conPurchaseOrg));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:purchaseOrg:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组织", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPurchaseOrg conPurchaseOrg) {
        conPurchaseOrg.setConfirmDate(new Date());
        conPurchaseOrg.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPurchaseOrg.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPurchaseOrgService.check(conPurchaseOrg));
    }

    @PostMapping("/getConPurchaseOrgList")
    @ApiOperation(value = "采购组织下拉列表", notes = "采购组织下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseOrg.class))
    public AjaxResult getConPurchaseOrgList() {
        return AjaxResult.success(conPurchaseOrgService.getConPurchaseOrgList());
    }
}
