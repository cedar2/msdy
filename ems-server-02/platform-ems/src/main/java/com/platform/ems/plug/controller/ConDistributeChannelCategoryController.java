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
import com.platform.ems.plug.domain.ConDistributeChannelCategory;
import com.platform.ems.plug.service.IConDistributeChannelCategoryService;
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
 * 分销渠道类别Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/distribute/channel/category")
@Api(tags = "分销渠道类别")
public class ConDistributeChannelCategoryController extends BaseController {

    @Autowired
    private IConDistributeChannelCategoryService conDistributeChannelCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询分销渠道类别列表
     */
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询分销渠道类别列表", notes = "查询分销渠道类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDistributeChannelCategory.class))
    public TableDataInfo list(@RequestBody ConDistributeChannelCategory conDistributeChannelCategory) {
        startPage(conDistributeChannelCategory);
        List<ConDistributeChannelCategory> list = conDistributeChannelCategoryService.selectConDistributeChannelCategoryList(conDistributeChannelCategory);
        return getDataTable(list);
    }

    /**
     * 导出分销渠道类别列表
     */
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:export")
    @Log(title = "分销渠道类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出分销渠道类别列表", notes = "导出分销渠道类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDistributeChannelCategory conDistributeChannelCategory) throws IOException {
        List<ConDistributeChannelCategory> list = conDistributeChannelCategoryService.selectConDistributeChannelCategoryList(conDistributeChannelCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDistributeChannelCategory> util = new ExcelUtil<ConDistributeChannelCategory>(ConDistributeChannelCategory.class, dataMap);
        util.exportExcel(response, list, "分销渠道类别" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入分销渠道类别
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入分销渠道类别", notes = "导入分销渠道类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConDistributeChannelCategory> util = new ExcelUtil<ConDistributeChannelCategory>(ConDistributeChannelCategory.class);
        List<ConDistributeChannelCategory> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conDistributeChannelCategory -> {
                conDistributeChannelCategoryService.insertConDistributeChannelCategory(conDistributeChannelCategory);
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


    @ApiOperation(value = "下载分销渠道类别导入模板", notes = "下载分销渠道类别导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDistributeChannelCategory> util = new ExcelUtil<ConDistributeChannelCategory>(ConDistributeChannelCategory.class);
        util.importTemplateExcel(response, "分销渠道类别导入模板");
    }


    /**
     * 获取分销渠道类别详细信息
     */
    @ApiOperation(value = "获取分销渠道类别详细信息", notes = "获取分销渠道类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDistributeChannelCategory.class))
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDistributeChannelCategoryService.selectConDistributeChannelCategoryById(sid));
    }

    /**
     * 新增分销渠道类别
     */
    @ApiOperation(value = "新增分销渠道类别", notes = "新增分销渠道类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:add")
    @Log(title = "分销渠道类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDistributeChannelCategory conDistributeChannelCategory) {
        return toAjax(conDistributeChannelCategoryService.insertConDistributeChannelCategory(conDistributeChannelCategory));
    }

    /**
     * 修改分销渠道类别
     */
    @ApiOperation(value = "修改分销渠道类别", notes = "修改分销渠道类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:edit")
    @Log(title = "分销渠道类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDistributeChannelCategory conDistributeChannelCategory) {
        return toAjax(conDistributeChannelCategoryService.updateConDistributeChannelCategory(conDistributeChannelCategory));
    }

    /**
     * 变更分销渠道类别
     */
    @ApiOperation(value = "变更分销渠道类别", notes = "变更分销渠道类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:change")
    @Log(title = "分销渠道类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDistributeChannelCategory conDistributeChannelCategory) {
        return toAjax(conDistributeChannelCategoryService.changeConDistributeChannelCategory(conDistributeChannelCategory));
    }

    /**
     * 删除分销渠道类别
     */
    @ApiOperation(value = "删除分销渠道类别", notes = "删除分销渠道类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:remove")
    @Log(title = "分销渠道类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDistributeChannelCategoryService.deleteConDistributeChannelCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "分销渠道类别", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDistributeChannelCategory conDistributeChannelCategory) {
        return AjaxResult.success(conDistributeChannelCategoryService.changeStatus(conDistributeChannelCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:distributeChannelCategory:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "分销渠道类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDistributeChannelCategory conDistributeChannelCategory) {
        conDistributeChannelCategory.setConfirmDate(new Date());
        conDistributeChannelCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDistributeChannelCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDistributeChannelCategoryService.check(conDistributeChannelCategory));
    }

    @PostMapping("/getConDistributeChannelCategoryList")
    @ApiOperation(value = "分销渠道类别下拉列表", notes = "分销渠道类别下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDistributeChannelCategory.class))
    public AjaxResult getConDistributeChannelCategoryList() {
        return AjaxResult.success(conDistributeChannelCategoryService.getConDistributeChannelCategoryList());
    }

    @PostMapping("/getList")
    @ApiOperation(value = "分销渠道类别下拉列表(新)", notes = "分销渠道类别下拉框列表(新)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDistributeChannelCategory.class))
    public AjaxResult getList(@RequestBody ConDistributeChannelCategory conDistributeChannelCategory) {
        return AjaxResult.success(conDistributeChannelCategoryService.selectConDistributeChannelCategoryList(conDistributeChannelCategory));
    }
}
