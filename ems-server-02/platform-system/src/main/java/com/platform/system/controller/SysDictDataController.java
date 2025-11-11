package com.platform.system.controller;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysDictType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.system.mapper.SysDictTypeMapper;
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
import com.platform.common.core.domain.entity.SysDictData;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.service.ISysDictDataService;
import com.platform.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 *
 * @author platform
 */
@RestController
@RequestMapping("/dict/data")
public class SysDictDataController extends BaseController
{
    @Autowired
    private ISysDictDataService dictDataService;
    @Resource
    private SysDictTypeMapper dictTypeMapper;
    @Autowired
    private ISysDictTypeService dictTypeService;

    private final static String LEVEL_YH = "YH";

    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData) {
        SysDictType dictType = dictTypeMapper.selectDictTypeByType(dictData.getDictType());
        if (dictType != null) {
            dictData.setDictLevel(dictType.getDictLevel());
        }
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData dictData) throws IOException {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<>(SysDictData.class);
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细
     */
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode) {
        return AjaxResult.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType) {
        String clientId;
        try{
            clientId= ApiThreadLocalUtil.get().getSysUser().getClientId();
        }catch (Exception e){
            clientId= UserConstants.ADMIN_CLIENTID;
        }
        return AjaxResult.success(dictTypeService.selectDictDataByType(dictType,clientId));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @PostMapping(value = "/type/getList")
    public AjaxResult dictType(@RequestBody SysDictType dictType) {
        String clientId;
        try{
            clientId= ApiThreadLocalUtil.get().getSysUser().getClientId();
        }catch (Exception e){
            clientId= UserConstants.ADMIN_CLIENTID;
        }
        return AjaxResult.success(dictTypeService.selectDictDataByTypeList(dictType,clientId));
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict) {
        dict.setCreateBy(ApiThreadLocalUtil.get().getUsername());
        int row = dictDataService.insertDictData(dict);
        dictTypeService.clearCache();
        return toAjax(row);
    }

    /**
     * 修改保存字典类型
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SysDictData dict) {
        int row = dictDataService.updateDictData(dict);
        dictTypeService.clearCache();
        return toAjax(row);
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes) {
        return toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }

    /**
     * 更改字典数据为已确认
     * @param dictCodes
     * @return
     */
    @PostMapping("/check")
    public AjaxResult check(@RequestBody Long[] dictCodes) {
        return toAjax(dictDataService.checkData(dictCodes));
    }

    @PostMapping("/dict/data")
    public R<SysDictData> dictData(@RequestBody SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return R.ok(list.get(0));
    }
}
