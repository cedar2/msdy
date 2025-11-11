package com.platform.ems.plug.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.platform.ems.plug.domain.ConProduceStage;
import com.platform.ems.plug.service.IConProduceStageService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
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
 * 所属生产阶段Controller
 *
 * @author linhongwei
 * @date 2021-09-26
 */
@RestController
@RequestMapping("/produce/stage")
@Api(tags = "所属生产阶段")
public class ConProduceStageController extends BaseController {

    @Autowired
    private IConProduceStageService conProduceStageService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询所属生产阶段列表
     */
    @PreAuthorize(hasPermi = "ems:produce:stage:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询所属生产阶段列表", notes = "查询所属生产阶段列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProduceStage.class))
    public TableDataInfo list(@RequestBody ConProduceStage conProduceStage) {
        System.out.println(conProduceStage);
        startPage(conProduceStage);
        List<ConProduceStage> list = conProduceStageService.selectConProduceStageList(conProduceStage);
        return getDataTable(list);
    }

    /**
     * 导出所属生产阶段列表
     */
    //@PreAuthorize(hasPermi = "ems:produce:stage:export")
    @Log(title = "所属生产阶段", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出所属生产阶段列表", notes = "导出所属生产阶段列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConProduceStage conProduceStage) throws IOException {
        List<ConProduceStage> list = conProduceStageService.selectConProduceStageList(conProduceStage);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConProduceStage> util = new ExcelUtil<ConProduceStage>(ConProduceStage.class, dataMap);
        util.exportExcel(response, list, "所属生产阶段");
    }


    /**
     * 获取所属生产阶段详细信息
     */
    @ApiOperation(value = "获取所属生产阶段详细信息", notes = "获取所属生产阶段详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProduceStage.class))
    @PreAuthorize(hasPermi = "ems:produce:stage:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conProduceStageService.selectConProduceStageById(sid));
    }

    /**
     * 新增所属生产阶段
     */
    @ApiOperation(value = "新增所属生产阶段", notes = "新增所属生产阶段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:produce:stage:add")
    @Log(title = "所属生产阶段", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConProduceStage conProduceStage) {
        return toAjax(conProduceStageService.insertConProduceStage(conProduceStage));
    }

    /**
     * 修改所属生产阶段
     */
    @ApiOperation(value = "修改所属生产阶段", notes = "修改所属生产阶段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:produce:stage:edit")
    @Log(title = "所属生产阶段", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConProduceStage conProduceStage) {
        return toAjax(conProduceStageService.updateConProduceStage(conProduceStage));
    }

    /**
     * 变更所属生产阶段
     */
    @ApiOperation(value = "变更所属生产阶段", notes = "变更所属生产阶段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:produce:stage:change")
    @Log(title = "所属生产阶段", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConProduceStage conProduceStage) {
        return toAjax(conProduceStageService.changeConProduceStage(conProduceStage));
    }

    /**
     * 删除所属生产阶段
     */
    @ApiOperation(value = "删除所属生产阶段", notes = "删除所属生产阶段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:produce:stage:remove")
    @Log(title = "所属生产阶段", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conProduceStageService.deleteConProduceStageByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "所属生产阶段", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:produce:stage:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConProduceStage conProduceStage) {
        return AjaxResult.success(conProduceStageService.changeStatus(conProduceStage));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:produce:stage:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "所属生产阶段", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConProduceStage conProduceStage) {
        conProduceStage.setConfirmDate(new Date());
        conProduceStage.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conProduceStage.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conProduceStageService.check(conProduceStage));
    }

    /**
     * 所属生产阶段下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "所属生产阶段下拉框列表", notes = "所属生产阶段下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProduceStage.class))
    public AjaxResult getList(@RequestBody ConProduceStage conProduceStage) {
        return AjaxResult.success(conProduceStageService.getList(conProduceStage));
    }
}
