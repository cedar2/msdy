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
import com.platform.ems.plug.domain.ConSaleOrg;
import com.platform.ems.plug.service.IConSaleOrgService;
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
 * 销售组织Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/sale/org")
@Api(tags = "销售组织")
public class ConSaleOrgController extends BaseController {

    @Autowired
    private IConSaleOrgService conSaleOrgService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询销售组织列表
     */
    @PreAuthorize(hasPermi = "ems:org:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询销售组织列表", notes = "查询销售组织列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleOrg.class))
    public TableDataInfo list(@RequestBody ConSaleOrg conSaleOrg) {
        startPage(conSaleOrg);
        List<ConSaleOrg> list = conSaleOrgService.selectConSaleOrgList(conSaleOrg);
        return getDataTable(list);
    }

    /**
     * 导出销售组织列表
     */
    @PreAuthorize(hasPermi = "ems:org:export")
    @Log(title = "销售组织", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售组织列表", notes = "导出销售组织列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSaleOrg conSaleOrg) throws IOException {
        List<ConSaleOrg> list = conSaleOrgService.selectConSaleOrgList(conSaleOrg);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConSaleOrg> util = new ExcelUtil<ConSaleOrg>(ConSaleOrg.class, dataMap);
        util.exportExcel(response, list, "销售组织" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入销售组织
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入销售组织", notes = "导入销售组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConSaleOrg> util = new ExcelUtil<ConSaleOrg>(ConSaleOrg.class);
        List<ConSaleOrg> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conSaleOrg -> {
                conSaleOrgService.insertConSaleOrg(conSaleOrg);
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


    @ApiOperation(value = "下载销售组织导入模板", notes = "下载销售组织导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConSaleOrg> util = new ExcelUtil<ConSaleOrg>(ConSaleOrg.class);
        util.importTemplateExcel(response, "销售组织导入模板");
    }


    /**
     * 获取销售组织详细信息
     */
    @ApiOperation(value = "获取销售组织详细信息", notes = "获取销售组织详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleOrg.class))
    @PreAuthorize(hasPermi = "ems:org:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conSaleOrgService.selectConSaleOrgById(sid));
    }

    /**
     * 新增销售组织
     */
    @ApiOperation(value = "新增销售组织", notes = "新增销售组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:org:add")
    @Log(title = "销售组织", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConSaleOrg conSaleOrg) {
        return toAjax(conSaleOrgService.insertConSaleOrg(conSaleOrg));
    }

    /**
     * 修改销售组织
     */
    @ApiOperation(value = "修改销售组织", notes = "修改销售组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:org:edit")
    @Log(title = "销售组织", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConSaleOrg conSaleOrg) {
        return toAjax(conSaleOrgService.updateConSaleOrg(conSaleOrg));
    }

    /**
     * 变更销售组织
     */
    @ApiOperation(value = "变更销售组织", notes = "变更销售组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:org:change")
    @Log(title = "销售组织", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConSaleOrg conSaleOrg) {
        return toAjax(conSaleOrgService.changeConSaleOrg(conSaleOrg));
    }

    /**
     * 删除销售组织
     */
    @ApiOperation(value = "删除销售组织", notes = "删除销售组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:org:remove")
    @Log(title = "销售组织", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSaleOrgService.deleteConSaleOrgByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售组织", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:org:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSaleOrg conSaleOrg) {
        return AjaxResult.success(conSaleOrgService.changeStatus(conSaleOrg));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:org:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售组织", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConSaleOrg conSaleOrg) {
        conSaleOrg.setConfirmDate(new Date());
        conSaleOrg.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conSaleOrg.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conSaleOrgService.check(conSaleOrg));
    }

    @PostMapping("/getConSaleOrgList")
    @ApiOperation(value = "销售组织下拉列表", notes = "销售组织下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleOrg.class))
    public AjaxResult getConSaleOrgList() {
        return AjaxResult.success(conSaleOrgService.getConSaleOrgList());
    }
}
