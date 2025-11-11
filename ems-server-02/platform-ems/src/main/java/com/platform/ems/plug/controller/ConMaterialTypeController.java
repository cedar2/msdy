package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConMaterialType;
import com.platform.ems.plug.service.IConMaterialTypeService;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 物料类型Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/material/type")
@Api(tags = "物料类型")
public class ConMaterialTypeController extends BaseController {

    @Autowired
    private IConMaterialTypeService conMaterialTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询物料类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询物料类型列表", notes = "查询物料类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialType.class))
    public TableDataInfo list(@RequestBody ConMaterialType conMaterialType) {
        startPage(conMaterialType);
        List<ConMaterialType> list = conMaterialTypeService.selectConMaterialTypeList(conMaterialType);
        return getDataTable(list);
    }

    /**
     * 导出物料类型列表
     */
    @Log(title = "物料类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料类型列表", notes = "导出物料类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConMaterialType conMaterialType) throws IOException {
        List<ConMaterialType> list = conMaterialTypeService.selectConMaterialTypeList(conMaterialType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConMaterialType> util = new ExcelUtil<>(ConMaterialType.class, dataMap);
        util.exportExcel(response, list, "物料类型");
    }

    /**
     * 获取物料类型详细信息
     */
    @ApiOperation(value = "获取物料类型详细信息", notes = "获取物料类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conMaterialTypeService.selectConMaterialTypeById(sid));
    }

    /**
     * 新增物料类型
     */
    @ApiOperation(value = "新增物料类型", notes = "新增物料类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConMaterialType conMaterialType) {
        return toAjax(conMaterialTypeService.insertConMaterialType(conMaterialType));
    }

    /**
     * 修改物料类型
     */
    @ApiOperation(value = "修改物料类型", notes = "修改物料类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConMaterialType conMaterialType) {
        return toAjax(conMaterialTypeService.updateConMaterialType(conMaterialType));
    }

    /**
     * 变更物料类型
     */
    @ApiOperation(value = "变更物料类型", notes = "变更物料类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:material:type:change")
    @Log(title = "物料类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConMaterialType conMaterialType) {
        return toAjax(conMaterialTypeService.changeConMaterialType(conMaterialType));
    }

    /**
     * 删除物料类型
     */
    @ApiOperation(value = "删除物料类型", notes = "删除物料类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conMaterialTypeService.deleteConMaterialTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料类型", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConMaterialType conMaterialType) {
        return AjaxResult.success(conMaterialTypeService.changeStatus(conMaterialType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConMaterialType conMaterialType) {
        conMaterialType.setConfirmDate(new Date());
        conMaterialType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conMaterialType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conMaterialTypeService.check(conMaterialType));
    }

    @PostMapping("/getConMaterialTypeList")
    @ApiOperation(value = "物料类型下拉框列表", notes = "物料类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialType.class))
    public AjaxResult getConMaterialTypeList() {
        return AjaxResult.success(conMaterialTypeService.getConMaterialTypeList());
    }

    @PostMapping("/getList")
    @ApiOperation(value = "物料类型下拉框列表", notes = "物料类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialType.class))
    public AjaxResult getList(@RequestBody ConMaterialType conMaterialType) {
        return AjaxResult.success(conMaterialTypeService.getList(conMaterialType));
    }
}
