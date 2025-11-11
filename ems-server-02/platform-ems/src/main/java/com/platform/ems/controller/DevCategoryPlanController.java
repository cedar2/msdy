package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.form.DevCategoryPlanItemFormRequest;
import com.platform.ems.domain.dto.response.form.DevCategoryPlanItemFormResponse;
import com.platform.ems.service.IDevCategoryPlanItemService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.DevCategoryPlan;
import com.platform.ems.service.IDevCategoryPlanService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 品类规划Controller
 *
 * @author chenkw
 * @date 2022-12-09
 */
@RestController
@RequestMapping("/dev/category/plan")
@Api(tags = "品类规划")
public class DevCategoryPlanController extends BaseController {

    @Autowired
    private IDevCategoryPlanService devCategoryPlanService;
    @Autowired
    private IDevCategoryPlanItemService devCategoryPlanItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询品类规划列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_CATEGORY_PLAN_ALL)
    @ApiOperation(value = "查询品类规划列表", notes = "查询品类规划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevCategoryPlan.class))
    public TableDataInfo list(@RequestBody DevCategoryPlan devCategoryPlan) {
        startPage(devCategoryPlan);
        List<DevCategoryPlan> list = devCategoryPlanService.selectDevCategoryPlanList(devCategoryPlan);
        return getDataTable(list);
    }

    /**
     * 导出品类规划列表
     */
    @Log(title = "品类规划", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_CATEGORY_PLAN_ALL, loc = 1)
    @ApiOperation(value = "导出品类规划列表", notes = "导出品类规划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevCategoryPlan devCategoryPlan) throws IOException {
        List<DevCategoryPlan> list = devCategoryPlanService.selectDevCategoryPlanList(devCategoryPlan);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevCategoryPlan> util = new ExcelUtil<>(DevCategoryPlan.class, dataMap);
        util.exportExcel(response, list, "品类规划");
    }

    /**
     * 获取品类规划详细信息
     */
    @ApiOperation(value = "获取品类规划详细信息", notes = "获取品类规划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevCategoryPlan.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long categoryPlanSid) {
        if (categoryPlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devCategoryPlanService.selectDevCategoryPlanById(categoryPlanSid));
    }

    /**
     * 复制品类规划详细信息
     */
    @ApiOperation(value = "复制品类规划详细信息", notes = "复制品类规划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevCategoryPlan.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long categoryPlanSid) {
        if (categoryPlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devCategoryPlanService.copyDevCategoryPlanById(categoryPlanSid));
    }

    /**
     * 新增品类规划
     */
    @ApiOperation(value = "新增品类规划", notes = "新增品类规划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid DevCategoryPlan devCategoryPlan) {
        // 校验明细
        if (!devCategoryPlan.isJumpJudge()) {
            EmsResultEntity judgeItem = devCategoryPlanService.judgeItemUnique(devCategoryPlan);
            if (!EmsResultEntity.SUCCESS_TAG.equals(judgeItem.getTag())) {
                return AjaxResult.success(judgeItem);
            }
        }
        int row = devCategoryPlanService.insertDevCategoryPlan(devCategoryPlan);
        if (row > 0) {
            return AjaxResult.success(devCategoryPlanService.selectDevCategoryPlanById(devCategoryPlan.getCategoryPlanSid()));
        } else {
            return toAjax(row);
        }
    }

    /**
     * 批量新增品类规划(导入后)
     */
    @ApiOperation(value = "批量新增品类规划(导入后)", notes = "批量新增品类规划(导入后)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addList")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody List<DevCategoryPlan> devCategoryPlanList) {
        if (CollectionUtils.isNotEmpty(devCategoryPlanList)) {
            for (DevCategoryPlan devCategoryPlan : devCategoryPlanList) {
                devCategoryPlanService.insertDevCategoryPlan(devCategoryPlan);
            }
            return toAjax(devCategoryPlanList.size());
        }
        else {
            return AjaxResult.error("数据为空，操作失败");
        }
    }

    @ApiOperation(value = "修改品类规划", notes = "修改品类规划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid DevCategoryPlan devCategoryPlan) {
        // 校验明细
        if (!devCategoryPlan.isJumpJudge()) {
            EmsResultEntity judgeItem = devCategoryPlanService.judgeItemUnique(devCategoryPlan);
            if (!EmsResultEntity.SUCCESS_TAG.equals(judgeItem.getTag())) {
                return AjaxResult.success(judgeItem);
            }
        }
        return toAjax(devCategoryPlanService.updateDevCategoryPlan(devCategoryPlan));
    }

    /**
     * 变更品类规划
     */
    @ApiOperation(value = "变更品类规划", notes = "变更品类规划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid DevCategoryPlan devCategoryPlan) {
        // 校验明细
        if (!devCategoryPlan.isJumpJudge()) {
            EmsResultEntity judgeItem = devCategoryPlanService.judgeItemUnique(devCategoryPlan);
            if (!EmsResultEntity.SUCCESS_TAG.equals(judgeItem.getTag())) {
                return AjaxResult.success(judgeItem);
            }
        }
        return toAjax(devCategoryPlanService.changeDevCategoryPlan(devCategoryPlan));
    }

    /**
     * 删除品类规划
     */
    @ApiOperation(value = "删除品类规划", notes = "删除品类规划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> categoryPlanSids) {
        if (CollectionUtils.isEmpty(categoryPlanSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devCategoryPlanService.deleteDevCategoryPlanByIds(categoryPlanSids));
    }

    /**
     * 修改品类规划处理状态（确认）
     */
    @ApiOperation(value = "修改品类规划处理状态（确认）", notes = "修改品类规划处理状态（确认）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody DevCategoryPlan devCategoryPlan) {
        if (ArrayUtil.isEmpty(devCategoryPlan.getCategoryPlanSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(devCategoryPlan.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devCategoryPlanService.check(devCategoryPlan));
    }

    /**
     * 作废
     */
    @ApiOperation(value = "作废品类规划", notes = "作废品类规划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作废品类规划", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(@RequestBody DevCategoryPlan devCategoryPlan) {
        if (devCategoryPlan.getCategoryPlanSid() == null) {
            throw new BaseException("参数缺失");
        }
        if (StrUtil.isBlank(devCategoryPlan.getCancelRemark())) {
            throw new BaseException("请填写作废说明再操作");
        }
        return toAjax(devCategoryPlanService.cancellationDevCategoryPlanById(devCategoryPlan));
    }

    /**
     * 删除品类规划明细前的校验
     */
    @ApiOperation(value = "删除品类规划明细前的校验", notes = "删除品类规划明细前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/delete/judge")
    public AjaxResult itemDeleteJudge(@RequestBody List<Long> categoryPlanItemSids) {
        if (CollectionUtils.isEmpty(categoryPlanItemSids)) {
            throw new CheckedException("参数缺失");
        }
        devCategoryPlanService.deleteDevCategoryPlanItemByIdsJudge(categoryPlanItemSids);
        return AjaxResult.success();
    }

    /**
     * 查询品类规划明细报表
     */
    @PostMapping("/item/form")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_CATEGORY_PLAN_ALL)
    @ApiOperation(value = "查询品类规划明细报表", notes = "查询品类规划明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevCategoryPlan.class))
    public TableDataInfo itemListForm(@RequestBody DevCategoryPlanItemFormRequest request) {
        startPage(request);
        List<DevCategoryPlanItemFormResponse> list = devCategoryPlanItemService.selectDevCategoryPlanItemForm(request);
        return getDataTable(list);
    }

    /**
     * 导出品类规划明细报表
     */
    @ApiOperation(value = "导出品类规划明细报表", notes = "导出品类规划明细报表")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_CATEGORY_PLAN_ALL, loc = 1)
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/form/export")
    public void export(HttpServletResponse response, DevCategoryPlanItemFormRequest devCategoryPlan) throws IOException {
        List<DevCategoryPlanItemFormResponse> list = devCategoryPlanItemService.selectDevCategoryPlanItemForm(devCategoryPlan);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevCategoryPlanItemFormResponse> util = new ExcelUtil<>(DevCategoryPlanItemFormResponse.class, dataMap);
        util.exportExcel(response, list, "品类规划明细报表");
    }

    /**
     * 品类规划导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "品类规划导入", notes = "品类规划导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importCategory(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(devCategoryPlanService.importCategory(file));
    }

    /**
     * 下载品类规划导入模板
     */
    @ApiOperation(value = "下载品类规划导入模板", notes = "下载品类规划导入模板")
    @PostMapping("/import/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "协服SCM_导入模板_品类规划_V1.0-CMB.xlsx");
    }

    /**
     * 下载模板
     */
    private void downloadTemplate(HttpServletResponse response, HttpServletRequest request, String target) {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/" + target;
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(target, "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }
}
