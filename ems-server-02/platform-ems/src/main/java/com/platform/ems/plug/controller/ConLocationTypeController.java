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
import com.platform.ems.plug.domain.ConLocationType;
import com.platform.ems.plug.service.IConLocationTypeService;
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
 * 库位类型Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/location/type")
@Api(tags = "库位类型")
public class ConLocationTypeController extends BaseController {

    @Autowired
    private IConLocationTypeService conLocationTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询库位类型列表
     */
    @PreAuthorize(hasPermi = "ems:type:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询库位类型列表", notes = "查询库位类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLocationType.class))
    public TableDataInfo list(@RequestBody ConLocationType conLocationType) {
        startPage(conLocationType);
        List<ConLocationType> list = conLocationTypeService.selectConLocationTypeList(conLocationType);
        return getDataTable(list);
    }

    /**
     * 导出库位类型列表
     */
    @PreAuthorize(hasPermi = "ems:type:export")
    @Log(title = "库位类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库位类型列表", notes = "导出库位类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConLocationType conLocationType) throws IOException {
        List<ConLocationType> list = conLocationTypeService.selectConLocationTypeList(conLocationType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConLocationType> util = new ExcelUtil<ConLocationType>(ConLocationType.class, dataMap);
        util.exportExcel(response, list, "库位类型" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入库位类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入库位类型", notes = "导入库位类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConLocationType> util = new ExcelUtil<ConLocationType>(ConLocationType.class);
        List<ConLocationType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conLocationType -> {
                conLocationTypeService.insertConLocationType(conLocationType);
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


    @ApiOperation(value = "下载库位类型导入模板", notes = "下载库位类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConLocationType> util = new ExcelUtil<ConLocationType>(ConLocationType.class);
        util.importTemplateExcel(response, "库位类型导入模板");
    }


    /**
     * 获取库位类型详细信息
     */
    @ApiOperation(value = "获取库位类型详细信息", notes = "获取库位类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLocationType.class))
    @PreAuthorize(hasPermi = "ems:type:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conLocationTypeService.selectConLocationTypeById(sid));
    }

    /**
     * 新增库位类型
     */
    @ApiOperation(value = "新增库位类型", notes = "新增库位类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:add")
    @Log(title = "库位类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConLocationType conLocationType) {
        return toAjax(conLocationTypeService.insertConLocationType(conLocationType));
    }

    /**
     * 修改库位类型
     */
    @ApiOperation(value = "修改库位类型", notes = "修改库位类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:edit")
    @Log(title = "库位类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConLocationType conLocationType) {
        return toAjax(conLocationTypeService.updateConLocationType(conLocationType));
    }

    /**
     * 变更库位类型
     */
    @ApiOperation(value = "变更库位类型", notes = "变更库位类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:change")
    @Log(title = "库位类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConLocationType conLocationType) {
        return toAjax(conLocationTypeService.changeConLocationType(conLocationType));
    }

    /**
     * 删除库位类型
     */
    @ApiOperation(value = "删除库位类型", notes = "删除库位类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:remove")
    @Log(title = "库位类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conLocationTypeService.deleteConLocationTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库位类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:type:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConLocationType conLocationType) {
        return AjaxResult.success(conLocationTypeService.changeStatus(conLocationType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:type:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库位类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConLocationType conLocationType) {
        conLocationType.setConfirmDate(new Date());
        conLocationType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conLocationType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conLocationTypeService.check(conLocationType));
    }

}
