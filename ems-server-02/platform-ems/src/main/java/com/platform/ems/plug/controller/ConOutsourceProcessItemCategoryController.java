package com.platform.ems.plug.controller;

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
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.domain.ConOutsourceProcessItemCategory;
import com.platform.ems.plug.service.IConOutsourceProcessItemCategoryService;
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
 * 行类别_外发加工发料单/收货单Controller
 *
 * @author linhongwei
 * @date 2021-06-19
 */
@RestController
@RequestMapping("/outPro/Item/category")
@Api(tags = "行类别_外发加工发料单/收货单")
public class ConOutsourceProcessItemCategoryController extends BaseController {

    @Autowired
    private IConOutsourceProcessItemCategoryService conOutsourceProcessItemCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询行类别_外发加工发料单/收货单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询行类别_外发加工发料单/收货单列表", notes = "查询行类别_外发加工发料单/收货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConOutsourceProcessItemCategory.class))
    public TableDataInfo list(@RequestBody ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        startPage(conOutsourceProcessItemCategory);
        List<ConOutsourceProcessItemCategory> list = conOutsourceProcessItemCategoryService.selectConOutsourceProcessItemCategoryList(conOutsourceProcessItemCategory);
        return getDataTable(list);
    }

    /**
     * 导出行类别_外发加工发料单/收货单列表
     */
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出行类别_外发加工发料单/收货单列表", notes = "导出行类别_外发加工发料单/收货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) throws IOException {
        List<ConOutsourceProcessItemCategory> list = conOutsourceProcessItemCategoryService.selectConOutsourceProcessItemCategoryList(conOutsourceProcessItemCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConOutsourceProcessItemCategory> util = new ExcelUtil<>(ConOutsourceProcessItemCategory.class, dataMap);
        util.exportExcel(response, list, "行类别_外发加工发料单(收货单)");
    }

    /**
     * 导入行类别_外发加工发料单/收货单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入行类别_外发加工发料单/收货单", notes = "导入行类别_外发加工发料单/收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConOutsourceProcessItemCategory> util = new ExcelUtil<>(ConOutsourceProcessItemCategory.class);
        List<ConOutsourceProcessItemCategory> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conOutsourceProcessItemCategory -> {
                conOutsourceProcessItemCategoryService.insertConOutsourceProcessItemCategory(conOutsourceProcessItemCategory);
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


    @ApiOperation(value = "下载行类别_外发加工发料单/收货单导入模板", notes = "下载行类别_外发加工发料单/收货单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConOutsourceProcessItemCategory> util = new ExcelUtil<>(ConOutsourceProcessItemCategory.class);
        util.importTemplateExcel(response, "行类别_外发加工发料单/收货单导入模板");
    }


    /**
     * 获取行类别_外发加工发料单/收货单详细信息
     */
    @ApiOperation(value = "获取行类别_外发加工发料单/收货单详细信息", notes = "获取行类别_外发加工发料单/收货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConOutsourceProcessItemCategory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conOutsourceProcessItemCategoryService.selectConOutsourceProcessItemCategoryById(sid));
    }

    /**
     * 新增行类别_外发加工发料单/收货单
     */
    @ApiOperation(value = "新增行类别_外发加工发料单/收货单", notes = "新增行类别_外发加工发料单/收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        return toAjax(conOutsourceProcessItemCategoryService.insertConOutsourceProcessItemCategory(conOutsourceProcessItemCategory));
    }

    /**
     * 修改行类别_外发加工发料单/收货单
     */
    @ApiOperation(value = "修改行类别_外发加工发料单/收货单", notes = "修改行类别_外发加工发料单/收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        return toAjax(conOutsourceProcessItemCategoryService.updateConOutsourceProcessItemCategory(conOutsourceProcessItemCategory));
    }

    /**
     * 变更行类别_外发加工发料单/收货单
     */
    @ApiOperation(value = "变更行类别_外发加工发料单/收货单", notes = "变更行类别_外发加工发料单/收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        return toAjax(conOutsourceProcessItemCategoryService.changeConOutsourceProcessItemCategory(conOutsourceProcessItemCategory));
    }

    /**
     * 删除行类别_外发加工发料单/收货单
     */
    @ApiOperation(value = "删除行类别_外发加工发料单/收货单", notes = "删除行类别_外发加工发料单/收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conOutsourceProcessItemCategoryService.deleteConOutsourceProcessItemCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        return AjaxResult.success(conOutsourceProcessItemCategoryService.changeStatus(conOutsourceProcessItemCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "行类别_外发加工发料单/收货单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        conOutsourceProcessItemCategory.setConfirmDate(new Date());
        conOutsourceProcessItemCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conOutsourceProcessItemCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conOutsourceProcessItemCategoryService.check(conOutsourceProcessItemCategory));
    }

    @PostMapping("/getConOutProItemCategoryList")
    @ApiOperation(value = "行类别_外发加工发料单/收货单下拉列表", notes = "行类别_外发加工发料单/收货单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDiscountType.class))
    public AjaxResult getConDiscountTypeList() {
        return AjaxResult.success(conOutsourceProcessItemCategoryService.getConOutsourceProcessItemCategoryList());
    }

}
