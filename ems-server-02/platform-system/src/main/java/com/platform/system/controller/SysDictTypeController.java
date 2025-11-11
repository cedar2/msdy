package com.platform.system.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.constant.UserConstants;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 *
 * @author platform
 */
@RestController
@RequestMapping("/dict/type")
public class SysDictTypeController extends BaseController
{
    @Autowired
    private ISysDictTypeService dictTypeService;

    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType) {
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())) {
            dictType.setDictLevel("YH");
        }
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType dictType) throws IOException {
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())) {
            dictType.setDictLevel("YH");
        }
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId) {
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictType dict) {
        if (!ApiThreadLocalUtil.get().getSysUser().isAdmin()) {
            return  AjaxResult.error("仅系统管理员可以新增数据字典！");
        }
        if (UserConstants.NOT_UNIQUE_NUM.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return AjaxResult.error("字典类型已存在");
        }
        if (UserConstants.NOT_UNIQUE_NUM.equals(dictTypeService.checkDictNameUnique(dict))) {
            return AjaxResult.error("字典名称已存在");
        }
        dict.setCreateBy(ApiThreadLocalUtil.get().getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictType dict) {
        if (!SecurityUtils.isAdmin()) {
            return  AjaxResult.error("仅系统管理员可以变更数据字典！");
        }
        if (UserConstants.NOT_UNIQUE_NUM.equals(dictTypeService.checkDictNameUnique(dict))) {
            return AjaxResult.error("字典名称已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    @PostMapping("/check")
    public AjaxResult check(@RequestBody SysDictType dict) {
        if (!SecurityUtils.isAdmin()) {
            return  AjaxResult.error("非系统管理员禁止修改字典类型");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds) {
        if (!SecurityUtils.isAdmin()) {
            return  AjaxResult.error("仅系统管理员可以删除数据字典！");
        }
        return toAjax(dictTypeService.deleteDictTypeByIds(dictIds));
    }

    /**
     * 清空缓存
     */
    @DeleteMapping("/clearCache")
    public AjaxResult clearCache() {
        dictTypeService.clearCache();
        return AjaxResult.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return AjaxResult.success(dictTypes);
    }

}
