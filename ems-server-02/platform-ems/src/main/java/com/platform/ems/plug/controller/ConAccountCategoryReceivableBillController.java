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
import com.platform.ems.plug.domain.ConAccountCategoryReceivableBill;
import com.platform.ems.plug.service.IConAccountCategoryReceivableBillService;
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
 * 款项类别_收款单Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/account/receivable/bill")
@Api(tags = "款项类别_收款单")
public class ConAccountCategoryReceivableBillController extends BaseController {

    @Autowired
    private IConAccountCategoryReceivableBillService conAccountCategoryReceivableBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询款项类别_收款单列表
     */
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询款项类别_收款单列表", notes = "查询款项类别_收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountCategoryReceivableBill.class))
    public TableDataInfo list(@RequestBody ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        startPage(conAccountCategoryReceivableBill);
        List<ConAccountCategoryReceivableBill> list = conAccountCategoryReceivableBillService.selectConAccountCategoryReceivableBillList(conAccountCategoryReceivableBill);
        return getDataTable(list);
    }

    /**
     * 导出款项类别_收款单列表
     */
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:export")
    @Log(title = "款项类别_收款单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出款项类别_收款单列表", notes = "导出款项类别_收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) throws IOException {
        List<ConAccountCategoryReceivableBill> list = conAccountCategoryReceivableBillService.selectConAccountCategoryReceivableBillList(conAccountCategoryReceivableBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConAccountCategoryReceivableBill> util = new ExcelUtil<ConAccountCategoryReceivableBill>(ConAccountCategoryReceivableBill.class, dataMap);
        util.exportExcel(response, list, "款项类别_收款单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入款项类别_收款单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入款项类别_收款单", notes = "导入款项类别_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConAccountCategoryReceivableBill> util = new ExcelUtil<ConAccountCategoryReceivableBill>(ConAccountCategoryReceivableBill.class);
        List<ConAccountCategoryReceivableBill> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conAccountCategoryReceivableBill -> {
                conAccountCategoryReceivableBillService.insertConAccountCategoryReceivableBill(conAccountCategoryReceivableBill);
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


    @ApiOperation(value = "下载款项类别_收款单导入模板", notes = "下载款项类别_收款单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConAccountCategoryReceivableBill> util = new ExcelUtil<ConAccountCategoryReceivableBill>(ConAccountCategoryReceivableBill.class);
        util.importTemplateExcel(response, "款项类别_收款单导入模板");
    }


    /**
     * 获取款项类别_收款单详细信息
     */
    @ApiOperation(value = "获取款项类别_收款单详细信息", notes = "获取款项类别_收款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountCategoryReceivableBill.class))
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conAccountCategoryReceivableBillService.selectConAccountCategoryReceivableBillById(sid));
    }

    /**
     * 新增款项类别_收款单
     */
    @ApiOperation(value = "新增款项类别_收款单", notes = "新增款项类别_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:add")
    @Log(title = "款项类别_收款单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        return toAjax(conAccountCategoryReceivableBillService.insertConAccountCategoryReceivableBill(conAccountCategoryReceivableBill));
    }

    /**
     * 修改款项类别_收款单
     */
    @ApiOperation(value = "修改款项类别_收款单", notes = "修改款项类别_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:edit")
    @Log(title = "款项类别_收款单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        return toAjax(conAccountCategoryReceivableBillService.updateConAccountCategoryReceivableBill(conAccountCategoryReceivableBill));
    }

    /**
     * 变更款项类别_收款单
     */
    @ApiOperation(value = "变更款项类别_收款单", notes = "变更款项类别_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:change")
    @Log(title = "款项类别_收款单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        return toAjax(conAccountCategoryReceivableBillService.changeConAccountCategoryReceivableBill(conAccountCategoryReceivableBill));
    }

    /**
     * 删除款项类别_收款单
     */
    @ApiOperation(value = "删除款项类别_收款单", notes = "删除款项类别_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:remove")
    @Log(title = "款项类别_收款单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conAccountCategoryReceivableBillService.deleteConAccountCategoryReceivableBillByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "款项类别_收款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        return AjaxResult.success(conAccountCategoryReceivableBillService.changeStatus(conAccountCategoryReceivableBill));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:account:receivable:bill:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "款项类别_收款单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConAccountCategoryReceivableBill conAccountCategoryReceivableBill) {
        conAccountCategoryReceivableBill.setConfirmDate(new Date());
        conAccountCategoryReceivableBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conAccountCategoryReceivableBill.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conAccountCategoryReceivableBillService.check(conAccountCategoryReceivableBill));
    }

}
