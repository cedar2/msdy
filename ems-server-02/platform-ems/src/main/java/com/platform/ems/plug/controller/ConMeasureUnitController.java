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
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.service.IConMeasureUnitService;
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
 * 计量单位Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/measure/unit")
@Api(tags = "计量单位")
public class ConMeasureUnitController extends BaseController {

    @Autowired
    private IConMeasureUnitService conMeasureUnitService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询计量单位列表
     */
    @PreAuthorize(hasPermi = "ems:con:measure:unit:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询计量单位列表", notes = "查询计量单位列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMeasureUnit.class))
    public TableDataInfo list(@RequestBody ConMeasureUnit conMeasureUnit) {
        startPage(conMeasureUnit);
        List<ConMeasureUnit> list = conMeasureUnitService.selectConMeasureUnitList(conMeasureUnit);
        return getDataTable(list);
    }

    /**
     * 导出计量单位列表
     */
    @Log(title = "计量单位", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出计量单位列表", notes = "导出计量单位列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConMeasureUnit conMeasureUnit) throws IOException {
        List<ConMeasureUnit> list = conMeasureUnitService.selectConMeasureUnitList(conMeasureUnit);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConMeasureUnit> util = new ExcelUtil<>(ConMeasureUnit.class, dataMap);
        util.exportExcel(response, list, "计量单位");
    }

    /**
     * 导入计量单位
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入计量单位", notes = "导入计量单位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConMeasureUnit> util = new ExcelUtil<>(ConMeasureUnit.class);
        List<ConMeasureUnit> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conMeasureUnit -> {
                conMeasureUnitService.insertConMeasureUnit(conMeasureUnit);
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


    @ApiOperation(value = "下载计量单位导入模板", notes = "下载计量单位导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConMeasureUnit> util = new ExcelUtil<>(ConMeasureUnit.class);
        util.importTemplateExcel(response, "计量单位导入模板");
    }


    /**
     * 获取计量单位详细信息
     */
    @ApiOperation(value = "获取计量单位详细信息", notes = "获取计量单位详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMeasureUnit.class))
    @PreAuthorize(hasPermi = "ems:con:measure:unit:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conMeasureUnitService.selectConMeasureUnitById(sid));
    }

    /**
     * 新增计量单位
     */
    @ApiOperation(value = "新增计量单位", notes = "新增计量单位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:measure:unit:add")
    @Log(title = "计量单位", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConMeasureUnit conMeasureUnit) {
        return toAjax(conMeasureUnitService.insertConMeasureUnit(conMeasureUnit));
    }

    /**
     * 修改计量单位
     */
    @ApiOperation(value = "修改计量单位", notes = "修改计量单位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "计量单位", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConMeasureUnit conMeasureUnit) {
        return toAjax(conMeasureUnitService.updateConMeasureUnit(conMeasureUnit));
    }

    /**
     * 变更计量单位
     */
    @ApiOperation(value = "变更计量单位", notes = "变更计量单位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:measure:unit:change")
    @Log(title = "计量单位", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConMeasureUnit conMeasureUnit) {
        return toAjax(conMeasureUnitService.changeConMeasureUnit(conMeasureUnit));
    }

    /**
     * 删除计量单位
     */
    @ApiOperation(value = "删除计量单位", notes = "删除计量单位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:measure:unit:remove")
    @Log(title = "计量单位", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conMeasureUnitService.deleteConMeasureUnitByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "计量单位", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:measure:unit:enableordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConMeasureUnit conMeasureUnit) {
        return AjaxResult.success(conMeasureUnitService.changeStatus(conMeasureUnit));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:measure:unit:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "计量单位", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConMeasureUnit conMeasureUnit) {
        conMeasureUnit.setConfirmDate(new Date());
        conMeasureUnit.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conMeasureUnit.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conMeasureUnitService.check(conMeasureUnit));
    }

    @PostMapping("/getConMeasureUnitList")
    @ApiOperation(value = "计量单位下拉框列表", notes = "计量单位下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMeasureUnit.class))
    public AjaxResult getConMeasureUnitList() {
        return AjaxResult.success(conMeasureUnitService.getConMeasureUnitList());
    }
}
