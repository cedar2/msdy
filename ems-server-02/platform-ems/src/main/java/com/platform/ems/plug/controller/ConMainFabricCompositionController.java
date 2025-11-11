package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConMainFabricComposition;
import com.platform.ems.plug.service.IConMainFabricCompositionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 主面料成分Controller
 *
 * @author chenkw
 * @date 2022-06-01
 */
@RestController
@RequestMapping("/main/fabric/composition")
@Api(tags = "主面料成分")
public class ConMainFabricCompositionController extends BaseController {

    @Autowired
    private IConMainFabricCompositionService conMainFabricCompositionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询主面料成分列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询主面料成分列表", notes = "查询主面料成分列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMainFabricComposition.class))
    public TableDataInfo list(@RequestBody ConMainFabricComposition conMainFabricComposition) {
        startPage(conMainFabricComposition);
        List<ConMainFabricComposition> list = conMainFabricCompositionService.selectConMainFabricCompositionList(conMainFabricComposition);
        return getDataTable(list);
    }

    /**
     * 导出主面料成分列表
     */
    @Log(title = "主面料成分", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出主面料成分列表", notes = "导出主面料成分列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConMainFabricComposition conMainFabricComposition) throws IOException {
        List<ConMainFabricComposition> list = conMainFabricCompositionService.selectConMainFabricCompositionList(conMainFabricComposition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConMainFabricComposition> util = new ExcelUtil<>(ConMainFabricComposition.class, dataMap);
        util.exportExcel(response, list, "主面料成分");
    }


    /**
     * 获取主面料成分详细信息
     */
    @ApiOperation(value = "获取主面料成分详细信息", notes = "获取主面料成分详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMainFabricComposition.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conMainFabricCompositionService.selectConMainFabricCompositionById(sid));
    }

    /**
     * 新增主面料成分
     */
    @ApiOperation(value = "新增主面料成分", notes = "新增主面料成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "主面料成分", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConMainFabricComposition conMainFabricComposition) {
        return toAjax(conMainFabricCompositionService.insertConMainFabricComposition(conMainFabricComposition));
    }

    /**
     * 修改主面料成分
     */
    @ApiOperation(value = "修改主面料成分", notes = "修改主面料成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "主面料成分", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConMainFabricComposition conMainFabricComposition) {
        return toAjax(conMainFabricCompositionService.updateConMainFabricComposition(conMainFabricComposition));
    }

    /**
     * 变更主面料成分
     */
    @ApiOperation(value = "变更主面料成分", notes = "变更主面料成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "主面料成分", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConMainFabricComposition conMainFabricComposition) {
        return toAjax(conMainFabricCompositionService.changeConMainFabricComposition(conMainFabricComposition));
    }

    /**
     * 删除主面料成分
     */
    @ApiOperation(value = "删除主面料成分", notes = "删除主面料成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "主面料成分", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conMainFabricCompositionService.deleteConMainFabricCompositionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "主面料成分", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConMainFabricComposition conMainFabricComposition) {
        return AjaxResult.success(conMainFabricCompositionService.changeStatus(conMainFabricComposition));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "主面料成分", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConMainFabricComposition conMainFabricComposition) {
        conMainFabricComposition.setConfirmDate(new Date());
        conMainFabricComposition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conMainFabricComposition.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conMainFabricCompositionService.check(conMainFabricComposition));
    }

    /**
     * 查询主面料成分下拉框
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询主面料成分下拉框", notes = "查询主面料成分下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMainFabricComposition.class))
    public AjaxResult getList(@RequestBody ConMainFabricComposition conMainFabricComposition) {
        List<ConMainFabricComposition> list = conMainFabricCompositionService.selectConMainFabricCompositionList(conMainFabricComposition);
        return AjaxResult.success(list);
    }

}
