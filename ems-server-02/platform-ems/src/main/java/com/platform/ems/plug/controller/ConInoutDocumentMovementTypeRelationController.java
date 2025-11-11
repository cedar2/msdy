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
import com.platform.ems.plug.domain.ConInoutDocumentMovementTypeRelation;
import com.platform.ems.plug.service.IConInoutDocumentMovementTypeRelationService;
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
 * 出入库作业类型&单据作业类型对照Controller
 *
 * @author c
 * @date 2022-03-11
 */
@RestController
@RequestMapping("/relation")
@Api(tags = "出入库作业类型&单据作业类型对照")
public class ConInoutDocumentMovementTypeRelationController extends BaseController {

    @Autowired
    private IConInoutDocumentMovementTypeRelationService conInoutDocumentMovementTypeRelationService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询出入库作业类型&单据作业类型对照列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询出入库作业类型&单据作业类型对照列表", notes = "查询出入库作业类型&单据作业类型对照列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInoutDocumentMovementTypeRelation.class))
    public TableDataInfo list(@RequestBody ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        startPage(conInoutDocumentMovementTypeRelation);
        List<ConInoutDocumentMovementTypeRelation> list = conInoutDocumentMovementTypeRelationService.selectConInoutDocumentMovementTypeRelationList(conInoutDocumentMovementTypeRelation);
        return getDataTable(list);
    }

    /**
     * 导出出入库作业类型&单据作业类型对照列表
     */
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出出入库作业类型&单据作业类型对照列表", notes = "导出出入库作业类型&单据作业类型对照列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) throws IOException {
        List<ConInoutDocumentMovementTypeRelation> list = conInoutDocumentMovementTypeRelationService.selectConInoutDocumentMovementTypeRelationList(conInoutDocumentMovementTypeRelation);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConInoutDocumentMovementTypeRelation> util = new ExcelUtil<>(ConInoutDocumentMovementTypeRelation.class, dataMap);
        util.exportExcel(response, list, "出入库作业类型&单据作业类型对照");
    }


    /**
     * 获取出入库作业类型&单据作业类型对照详细信息
     */
    @ApiOperation(value = "获取出入库作业类型&单据作业类型对照详细信息", notes = "获取出入库作业类型&单据作业类型对照详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInoutDocumentMovementTypeRelation.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInoutDocumentMovementTypeRelationService.selectConInoutDocumentMovementTypeRelationById(sid));
    }

    /**
     * 新增出入库作业类型&单据作业类型对照
     */
    @ApiOperation(value = "新增出入库作业类型&单据作业类型对照", notes = "新增出入库作业类型&单据作业类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        return toAjax(conInoutDocumentMovementTypeRelationService.insertConInoutDocumentMovementTypeRelation(conInoutDocumentMovementTypeRelation));
    }

    /**
     * 修改出入库作业类型&单据作业类型对照
     */
    @ApiOperation(value = "修改出入库作业类型&单据作业类型对照", notes = "修改出入库作业类型&单据作业类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        return toAjax(conInoutDocumentMovementTypeRelationService.updateConInoutDocumentMovementTypeRelation(conInoutDocumentMovementTypeRelation));
    }

    /**
     * 变更出入库作业类型&单据作业类型对照
     */
    @ApiOperation(value = "变更出入库作业类型&单据作业类型对照", notes = "变更出入库作业类型&单据作业类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        return toAjax(conInoutDocumentMovementTypeRelationService.changeConInoutDocumentMovementTypeRelation(conInoutDocumentMovementTypeRelation));
    }

    /**
     * 删除出入库作业类型&单据作业类型对照
     */
    @ApiOperation(value = "删除出入库作业类型&单据作业类型对照", notes = "删除出入库作业类型&单据作业类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInoutDocumentMovementTypeRelationService.deleteConInoutDocumentMovementTypeRelationByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        return AjaxResult.success(conInoutDocumentMovementTypeRelationService.changeStatus(conInoutDocumentMovementTypeRelation));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库作业类型&单据作业类型对照", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        conInoutDocumentMovementTypeRelation.setConfirmDate(new Date());
        conInoutDocumentMovementTypeRelation.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInoutDocumentMovementTypeRelation.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInoutDocumentMovementTypeRelationService.check(conInoutDocumentMovementTypeRelation));
    }

}
