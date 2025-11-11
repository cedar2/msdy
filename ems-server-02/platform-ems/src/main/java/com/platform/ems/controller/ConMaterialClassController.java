package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.core.domain.TreeSelect;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IConMaterialClassService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.BuildTreeService;
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
import java.util.stream.Collectors;

/**
 * 物料分类Controller
 *
 * @author linhongwei
 * @date 2021-09-29
 */
@RestController
@RequestMapping("/materialClass")
@Api(tags = "物料分类")
public class ConMaterialClassController extends BaseController {

    @Autowired
    private IConMaterialClassService conMaterialClassService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询物料分类列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询物料分类列表", notes = "查询物料分类列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialClass.class))
    public AjaxResult list(@RequestBody ConMaterialClass conMaterialClass) {
        List<ConMaterialClass> list = conMaterialClassService.selectConMaterialClassList(conMaterialClass);
        BuildTreeService<ConMaterialClass> buildTreeService = new BuildTreeService<>("materialClassSid", "parentCodeSid", "children");
        return AjaxResult.success(buildTreeService.buildTree(list));
    }

    @PostMapping("/getMaterialClassList")
    @ApiOperation(value = "获取物料分类下拉树列表", notes = "获取物料分类下拉树列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TreeSelect.class))
    public AjaxResult getMaterialClassList() {
        ConMaterialClass conMaterialClass = new ConMaterialClass().setStatus(ConstantsEms.ENABLE_STATUS);
        List<ConMaterialClass> list = conMaterialClassService.selectConMaterialClassList(conMaterialClass);
        BuildTreeService<ConMaterialClass> buildTreeService = new BuildTreeService<>("materialClassSid", "parentCodeSid", "children");
        List<ConMaterialClass> trees = buildTreeService.buildTree(list);
        return AjaxResult.success(trees.stream().map(TreeSelect::new).collect(Collectors.toList()));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "获取物料分类下拉树列表", notes = "获取物料分类下拉树列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TreeSelect.class))
    public AjaxResult getList(@RequestBody ConMaterialClass conMaterialClass) {
        List<ConMaterialClass> list = conMaterialClassService.selectConMaterialClassList(conMaterialClass);
        BuildTreeService<ConMaterialClass> buildTreeService = new BuildTreeService<>("materialClassSid", "parentCodeSid", "children");
        List<ConMaterialClass> trees = buildTreeService.buildTree(list);
        return AjaxResult.success(trees.stream().map(TreeSelect::new).collect(Collectors.toList()));
    }

    @PostMapping("/getList/ByMaterialType")
    @ApiOperation(value = "获取物料分类下拉树列表(带物料类型)", notes = "获取物料分类下拉树列表(带物料类型)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TreeSelect.class))
    public AjaxResult getListByMaterialType(@RequestBody ConMaterialClass conMaterialClass) {
        List<ConMaterialClass> list = conMaterialClassService.selectConMaterialClassListByMaterialType(conMaterialClass);
        BuildTreeService<ConMaterialClass> buildTreeService = new BuildTreeService<>("materialClassSid", "parentCodeSid", "children");
        List<ConMaterialClass> trees = buildTreeService.buildTree(list);
        return AjaxResult.success(trees.stream().map(TreeSelect::new).collect(Collectors.toList()));
    }

    /**
     * 导出物料分类列表
     */
    @Log(title = "物料分类", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料分类列表", notes = "导出物料分类列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConMaterialClass conMaterialClass) throws IOException {
        List<ConMaterialClass> list = conMaterialClassService.selectConMaterialClassList(conMaterialClass);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConMaterialClass> util = new ExcelUtil<>(ConMaterialClass.class, dataMap);
        util.exportExcel(response, list, "物料分类");
    }


    /**
     * 获取物料分类详细信息
     */
    @ApiOperation(value = "获取物料分类详细信息", notes = "获取物料分类详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialClass.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialClassSid) {
        if (materialClassSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conMaterialClassService.selectConMaterialClassById(materialClassSid));
    }

    /**
     * 新增物料分类
     */
    @ApiOperation(value = "新增物料分类", notes = "新增物料分类")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料分类", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConMaterialClass conMaterialClass) {
        return toAjax(conMaterialClassService.insertConMaterialClass(conMaterialClass));
    }

    /**
     * 修改物料分类
     */
    @ApiOperation(value = "修改物料分类", notes = "修改物料分类")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料分类", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConMaterialClass conMaterialClass) {
        return toAjax(conMaterialClassService.updateConMaterialClass(conMaterialClass));
    }

    /**
     * 变更物料分类
     */
    @ApiOperation(value = "变更物料分类", notes = "变更物料分类")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料分类", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConMaterialClass conMaterialClass) {
        return toAjax(conMaterialClassService.changeConMaterialClass(conMaterialClass));
    }

    /**
     * 删除物料分类
     */
    @ApiOperation(value = "删除物料分类", notes = "删除物料分类")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料分类", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> materialClassSids) {
        if (CollectionUtils.isEmpty(materialClassSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conMaterialClassService.deleteConMaterialClassByIds(materialClassSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料分类", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConMaterialClass conMaterialClass) {
        return AjaxResult.success(conMaterialClassService.changeStatus(conMaterialClass));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料分类", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConMaterialClass conMaterialClass) {
        conMaterialClass.setConfirmDate(new Date());
        conMaterialClass.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conMaterialClass.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conMaterialClassService.check(conMaterialClass));
    }

    /**
     * 校验非同级是否存在同名
     */
    @ApiOperation(value = "校验非同级是否存在同名", notes = "校验非同级是否存在同名")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialClass.class))
    @PostMapping("/checkUnique")
    public AjaxResult checkUnique(@RequestBody ConMaterialClass conMaterialClass) {
        String nodeName = conMaterialClass.getNodeName();
        Long level = conMaterialClass.getLevel();
        if (StrUtil.isEmpty(nodeName) || level == null) {
            throw new BaseException("参数缺失");
        }
        return AjaxResult.success(conMaterialClassService.selectConMaterialClassByName(conMaterialClass));
    }

    @PostMapping("/getClassList")
    @ApiOperation(value = "获取物料分类下拉列表", notes = "获取物料分类下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMaterialClass.class))
    public AjaxResult getClassList(@RequestBody ConMaterialClass conMaterialClass) {
        return AjaxResult.success(conMaterialClassService.getConMaterialClassList(conMaterialClass));
    }

}
