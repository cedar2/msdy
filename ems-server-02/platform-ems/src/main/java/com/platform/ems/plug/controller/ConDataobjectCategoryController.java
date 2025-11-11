package com.platform.ems.plug.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.core.domain.R;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.dto.ManWorkOrderProgressFormProcess;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConDataobjectCategory;
import com.platform.ems.plug.mapper.ConDataobjectCategoryMapper;
import com.platform.ems.plug.service.IConDataobjectCategoryService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据对象类别Controller
 *
 * @author c
 * @date 2021-09-06
 */
@RestController
@RequestMapping("/dataobject/category")
@Api(tags = "数据对象类别")
public class ConDataobjectCategoryController extends BaseController {

    @Autowired
    private IConDataobjectCategoryService conDataobjectCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private ConDataobjectCategoryMapper dataobjectCategoryMapper;

    /**
     * 查询数据对象类别列表
     */
    @PreAuthorize(hasPermi = "ems:dataobject:category:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询数据对象类别列表", notes = "查询数据对象类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectCategory.class))
    public TableDataInfo list(@RequestBody ConDataobjectCategory conDataobjectCategory) {
        startPage(conDataobjectCategory);
        List<ConDataobjectCategory> list = conDataobjectCategoryService.selectConDataobjectCategoryList(conDataobjectCategory);
        TableDataInfo response = getDataTable(list);
        // 带入表注释
        getTableComment(list);
        return response;
    }

    private void getTableComment(List<ConDataobjectCategory> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<ConDataobjectCategory> tableList = dataobjectCategoryMapper.selectDbTableList(new ConDataobjectCategory());
            Map<String, ConDataobjectCategory> map = tableList.stream().collect(Collectors.toMap(ConDataobjectCategory::getDatabaseTableName, Function.identity()));
            list.forEach(item->{
                if (item.getDatabaseTableName() != null && map.containsKey(item.getDatabaseTableName())) {
                    item.setDatabaseTableComment(map.get(item.getDatabaseTableName()).getDatabaseTableComment());
                }
            });
        }
    }

    /**
     * 导出数据对象类别列表
     */
    @PreAuthorize(hasPermi = "ems:dataobject:category:export")
    @Log(title = "数据对象类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出数据对象类别列表", notes = "导出数据对象类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDataobjectCategory conDataobjectCategory) throws IOException {
        List<ConDataobjectCategory> list = conDataobjectCategoryService.selectConDataobjectCategoryList(conDataobjectCategory);
        // 带入表注释
        getTableComment(list);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDataobjectCategory> util = new ExcelUtil<>(ConDataobjectCategory.class, dataMap);
        util.exportExcel(response, list, "数据对象类别");
    }


    /**
     * 获取数据对象类别详细信息
     */
    @ApiOperation(value = "获取数据对象类别详细信息", notes = "获取数据对象类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectCategory.class))
    @PreAuthorize(hasPermi = "ems:dataobject:category:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDataobjectCategoryService.selectConDataobjectCategoryById(sid));
    }

    /**
     * 新增数据对象类别
     */
    @ApiOperation(value = "新增数据对象类别", notes = "新增数据对象类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:dataobject:category:add")
    @Log(title = "数据对象类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDataobjectCategory conDataobjectCategory) {
        return toAjax(conDataobjectCategoryService.insertConDataobjectCategory(conDataobjectCategory));
    }

    /**
     * 修改数据对象类别
     */
    @ApiOperation(value = "修改数据对象类别", notes = "修改数据对象类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:dataobject:category:edit")
    @Log(title = "数据对象类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDataobjectCategory conDataobjectCategory) {
        return toAjax(conDataobjectCategoryService.updateConDataobjectCategory(conDataobjectCategory));
    }

    /**
     * 变更数据对象类别
     */
    @ApiOperation(value = "变更数据对象类别", notes = "变更数据对象类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:dataobject:category:change")
    @Log(title = "数据对象类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDataobjectCategory conDataobjectCategory) {
        return toAjax(conDataobjectCategoryService.changeConDataobjectCategory(conDataobjectCategory));
    }

    /**
     * 删除数据对象类别
     */
    @ApiOperation(value = "删除数据对象类别", notes = "删除数据对象类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:dataobject:category:remove")
    @Log(title = "数据对象类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDataobjectCategoryService.deleteConDataobjectCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:dataobject:category:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDataobjectCategory conDataobjectCategory) {
        return AjaxResult.success(conDataobjectCategoryService.changeStatus(conDataobjectCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:dataobject:category:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDataobjectCategory conDataobjectCategory) {
        conDataobjectCategory.setConfirmDate(new Date());
        conDataobjectCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDataobjectCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDataobjectCategoryService.check(conDataobjectCategory));
    }

    /**
     * 数据对象类别下拉接口
     */
    @PostMapping("/getList")
    @ApiOperation(value = "数据对象类别下拉接口", notes = "数据对象类别下拉接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectCategory.class))
    public AjaxResult getDataobjectCategoryList(@RequestBody ConDataobjectCategory conDataobjectCategory) {
        return AjaxResult.success(conDataobjectCategoryService.getDataobjectCategoryList(conDataobjectCategory));
    }
}
