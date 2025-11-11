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
import com.platform.ems.plug.domain.ConSaleGroup;
import com.platform.ems.plug.service.IConSaleGroupService;
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
 * 销售组Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/sale/group")
@Api(tags = "销售组")
public class ConSaleGroupController extends BaseController {

    @Autowired
    private IConSaleGroupService conSaleGroupService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询销售组列表
     */
    @PreAuthorize(hasPermi = "ems:group:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询销售组列表", notes = "查询销售组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleGroup.class))
    public TableDataInfo list(@RequestBody ConSaleGroup conSaleGroup) {
        startPage(conSaleGroup);
        List<ConSaleGroup> list = conSaleGroupService.selectConSaleGroupList(conSaleGroup);
        return getDataTable(list);
    }

    /**
     * 导出销售组列表
     */
    @PreAuthorize(hasPermi = "ems:group:export")
    @Log(title = "销售组", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售组列表", notes = "导出销售组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSaleGroup conSaleGroup) throws IOException {
        List<ConSaleGroup> list = conSaleGroupService.selectConSaleGroupList(conSaleGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConSaleGroup> util = new ExcelUtil<ConSaleGroup>(ConSaleGroup.class, dataMap);
        util.exportExcel(response, list, "销售组" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入销售组
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入销售组", notes = "导入销售组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConSaleGroup> util = new ExcelUtil<ConSaleGroup>(ConSaleGroup.class);
        List<ConSaleGroup> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conSaleGroup -> {
                conSaleGroupService.insertConSaleGroup(conSaleGroup);
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


    @ApiOperation(value = "下载销售组导入模板", notes = "下载销售组导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConSaleGroup> util = new ExcelUtil<ConSaleGroup>(ConSaleGroup.class);
        util.importTemplateExcel(response, "销售组导入模板");
    }


    /**
     * 获取销售组详细信息
     */
    @ApiOperation(value = "获取销售组详细信息", notes = "获取销售组详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleGroup.class))
    @PreAuthorize(hasPermi = "ems:group:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conSaleGroupService.selectConSaleGroupById(sid));
    }

    /**
     * 新增销售组
     */
    @ApiOperation(value = "新增销售组", notes = "新增销售组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:group:add")
    @Log(title = "销售组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConSaleGroup conSaleGroup) {
        return toAjax(conSaleGroupService.insertConSaleGroup(conSaleGroup));
    }

    /**
     * 修改销售组
     */
    @ApiOperation(value = "修改销售组", notes = "修改销售组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:group:edit")
    @Log(title = "销售组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConSaleGroup conSaleGroup) {
        return toAjax(conSaleGroupService.updateConSaleGroup(conSaleGroup));
    }

    /**
     * 变更销售组
     */
    @ApiOperation(value = "变更销售组", notes = "变更销售组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:group:change")
    @Log(title = "销售组", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConSaleGroup conSaleGroup) {
        return toAjax(conSaleGroupService.changeConSaleGroup(conSaleGroup));
    }

    /**
     * 删除销售组
     */
    @ApiOperation(value = "删除销售组", notes = "删除销售组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:group:remove")
    @Log(title = "销售组", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSaleGroupService.deleteConSaleGroupByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售组", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:group:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSaleGroup conSaleGroup) {
        return AjaxResult.success(conSaleGroupService.changeStatus(conSaleGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:group:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售组", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConSaleGroup conSaleGroup) {
        conSaleGroup.setConfirmDate(new Date());
        conSaleGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conSaleGroup.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conSaleGroupService.check(conSaleGroup));
    }

    @PostMapping("/getConSaleGroupList")
    @ApiOperation(value = "销售组下拉列表", notes = "销售组下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleGroup.class))
    public AjaxResult getConSaleGroupList() {
        return AjaxResult.success(conSaleGroupService.getConSaleGroupList());
    }
}
