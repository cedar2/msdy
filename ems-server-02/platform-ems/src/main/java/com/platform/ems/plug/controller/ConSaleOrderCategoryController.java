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
import com.platform.ems.plug.domain.ConSaleOrderCategory;
import com.platform.ems.plug.service.IConSaleOrderCategoryService;
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
 * 销售订单类别Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/sale/order/category")
@Api(tags = "销售订单类别")
public class ConSaleOrderCategoryController extends BaseController {

    @Autowired
    private IConSaleOrderCategoryService conSaleOrderCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询销售订单类别列表
     */
    @PreAuthorize(hasPermi = "ems:category:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询销售订单类别列表", notes = "查询销售订单类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleOrderCategory.class))
    public TableDataInfo list(@RequestBody ConSaleOrderCategory conSaleOrderCategory) {
        startPage(conSaleOrderCategory);
        List<ConSaleOrderCategory> list = conSaleOrderCategoryService.selectConSaleOrderCategoryList(conSaleOrderCategory);
        return getDataTable(list);
    }

    /**
     * 导出销售订单类别列表
     */
    @PreAuthorize(hasPermi = "ems:category:export")
    @Log(title = "销售订单类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售订单类别列表", notes = "导出销售订单类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSaleOrderCategory conSaleOrderCategory) throws IOException {
        List<ConSaleOrderCategory> list = conSaleOrderCategoryService.selectConSaleOrderCategoryList(conSaleOrderCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConSaleOrderCategory> util = new ExcelUtil<ConSaleOrderCategory>(ConSaleOrderCategory.class, dataMap);
        util.exportExcel(response, list, "销售订单类别" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入销售订单类别
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入销售订单类别", notes = "导入销售订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConSaleOrderCategory> util = new ExcelUtil<ConSaleOrderCategory>(ConSaleOrderCategory.class);
        List<ConSaleOrderCategory> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conSaleOrderCategory -> {
                conSaleOrderCategoryService.insertConSaleOrderCategory(conSaleOrderCategory);
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


    @ApiOperation(value = "下载销售订单类别导入模板", notes = "下载销售订单类别导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConSaleOrderCategory> util = new ExcelUtil<ConSaleOrderCategory>(ConSaleOrderCategory.class);
        util.importTemplateExcel(response, "销售订单类别导入模板");
    }


    /**
     * 获取销售订单类别详细信息
     */
    @ApiOperation(value = "获取销售订单类别详细信息", notes = "获取销售订单类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleOrderCategory.class))
    @PreAuthorize(hasPermi = "ems:category:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conSaleOrderCategoryService.selectConSaleOrderCategoryById(sid));
    }

    /**
     * 新增销售订单类别
     */
    @ApiOperation(value = "新增销售订单类别", notes = "新增销售订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:add")
    @Log(title = "销售订单类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConSaleOrderCategory conSaleOrderCategory) {
        return toAjax(conSaleOrderCategoryService.insertConSaleOrderCategory(conSaleOrderCategory));
    }

    /**
     * 修改销售订单类别
     */
    @ApiOperation(value = "修改销售订单类别", notes = "修改销售订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:edit")
    @Log(title = "销售订单类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConSaleOrderCategory conSaleOrderCategory) {
        return toAjax(conSaleOrderCategoryService.updateConSaleOrderCategory(conSaleOrderCategory));
    }

    /**
     * 变更销售订单类别
     */
    @ApiOperation(value = "变更销售订单类别", notes = "变更销售订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:change")
    @Log(title = "销售订单类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConSaleOrderCategory conSaleOrderCategory) {
        return toAjax(conSaleOrderCategoryService.changeConSaleOrderCategory(conSaleOrderCategory));
    }

    /**
     * 删除销售订单类别
     */
    @ApiOperation(value = "删除销售订单类别", notes = "删除销售订单类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:remove")
    @Log(title = "销售订单类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSaleOrderCategoryService.deleteConSaleOrderCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单类别", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:category:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSaleOrderCategory conSaleOrderCategory) {
        return AjaxResult.success(conSaleOrderCategoryService.changeStatus(conSaleOrderCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:category:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConSaleOrderCategory conSaleOrderCategory) {
        conSaleOrderCategory.setConfirmDate(new Date());
        conSaleOrderCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conSaleOrderCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conSaleOrderCategoryService.check(conSaleOrderCategory));
    }

}
