package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBomUsage;
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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConDeliveryCategory;
import com.platform.ems.plug.service.IConDeliveryCategoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 交货类别Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/delivery/category")
@Api(tags = "交货类别")
public class ConDeliveryCategoryController extends BaseController {

    @Autowired
    private IConDeliveryCategoryService conDeliveryCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询交货类别列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询交货类别列表", notes = "查询交货类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeliveryCategory.class))
    public TableDataInfo list(@RequestBody ConDeliveryCategory conDeliveryCategory) {
        startPage(conDeliveryCategory);
        List<ConDeliveryCategory> list = conDeliveryCategoryService.selectConDeliveryCategoryList(conDeliveryCategory);
        return getDataTable(list);
    }

    /**
     * 导出交货类别列表
     */
    @Log(title = "交货类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出交货类别列表", notes = "导出交货类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDeliveryCategory conDeliveryCategory) throws IOException {
        List<ConDeliveryCategory> list = conDeliveryCategoryService.selectConDeliveryCategoryList(conDeliveryCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDeliveryCategory> util = new ExcelUtil<>(ConDeliveryCategory.class, dataMap);
        util.exportExcel(response, list, "交货类别");
    }

    /**
     * 导入交货类别
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入交货类别", notes = "导入交货类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConDeliveryCategory> util = new ExcelUtil<>(ConDeliveryCategory.class);
        List<ConDeliveryCategory> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conDeliveryCategory -> {
                conDeliveryCategoryService.insertConDeliveryCategory(conDeliveryCategory);
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


    @ApiOperation(value = "下载交货类别导入模板", notes = "下载交货类别导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDeliveryCategory> util = new ExcelUtil<>(ConDeliveryCategory.class);
        util.importTemplateExcel(response, "交货类别导入模板");
    }


    /**
     * 获取交货类别详细信息
     */
    @ApiOperation(value = "获取交货类别详细信息", notes = "获取交货类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeliveryCategory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDeliveryCategoryService.selectConDeliveryCategoryById(sid));
    }

    /**
     * 新增交货类别
     */
    @ApiOperation(value = "新增交货类别", notes = "新增交货类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交货类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDeliveryCategory conDeliveryCategory) {
        return toAjax(conDeliveryCategoryService.insertConDeliveryCategory(conDeliveryCategory));
    }

    /**
     * 修改交货类别
     */
    @ApiOperation(value = "修改交货类别", notes = "修改交货类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交货类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDeliveryCategory conDeliveryCategory) {
        return toAjax(conDeliveryCategoryService.updateConDeliveryCategory(conDeliveryCategory));
    }

    /**
     * 变更交货类别
     */
    @ApiOperation(value = "变更交货类别", notes = "变更交货类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交货类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDeliveryCategory conDeliveryCategory) {
        return toAjax(conDeliveryCategoryService.changeConDeliveryCategory(conDeliveryCategory));
    }

    /**
     * 删除交货类别
     */
    @ApiOperation(value = "删除交货类别", notes = "删除交货类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交货类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDeliveryCategoryService.deleteConDeliveryCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交货类别", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDeliveryCategory conDeliveryCategory) {
        return AjaxResult.success(conDeliveryCategoryService.changeStatus(conDeliveryCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交货类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDeliveryCategory conDeliveryCategory) {
        conDeliveryCategory.setConfirmDate(new Date());
        conDeliveryCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDeliveryCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDeliveryCategoryService.check(conDeliveryCategory));
    }

    @ApiOperation(value = "获取下拉框接口", notes = "获取下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDeliveryCategory.class))
    @PostMapping("/getList")
    public AjaxResult getList(@RequestBody ConDeliveryCategory conDeliveryCategory) {
        conDeliveryCategory.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conDeliveryCategoryService.selectConDeliveryCategoryList(conDeliveryCategory));
    }

}
