package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
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
import com.platform.ems.domain.PrjTask;
import com.platform.ems.service.IPrjTaskService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 任务节点Controller
 *
 * @author chenkw
 * @date 2022-12-07
 */
@RestController
@RequestMapping("/prj/task")
@Api(tags = "任务节点")
public class PrjTaskController extends BaseController {

    @Autowired
    private IPrjTaskService prjTaskService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询任务节点列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询任务节点列表", notes = "查询任务节点列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjTask.class))
    public TableDataInfo list(@RequestBody PrjTask prjTask) {
        startPage(prjTask);
        List<PrjTask> list = prjTaskService.selectPrjTaskList(prjTask);
        return getDataTable(list);
    }

    /**
     * 导出任务节点列表
     */
    @Log(title = "任务节点", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出任务节点列表", notes = "导出任务节点列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PrjTask prjTask) throws IOException {
        List<PrjTask> list = prjTaskService.selectPrjTaskList(prjTask);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjTask> util = new ExcelUtil<>(PrjTask.class, dataMap);
        util.exportExcel(response, list, "任务节点");
    }

    /**
     * 获取任务节点详细信息
     */
    @ApiOperation(value = "获取任务节点详细信息", notes = "获取任务节点详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjTask.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long taskSid) {
        if (taskSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjTaskService.selectPrjTaskById(taskSid));
    }

    /**
     * 复制任务节点详细信息
     */
    @ApiOperation(value = "复制任务节点详细信息", notes = "复制任务节点详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjTask.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long taskSid) {
        if (taskSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjTaskService.copyPrjTaskById(taskSid));
    }

    /**
     * 新增任务节点
     */
    @ApiOperation(value = "新增任务节点", notes = "新增任务节点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务节点", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid PrjTask prjTask) {
        int row = prjTaskService.insertPrjTask(prjTask);
        if (row > 0) {
            return AjaxResult.success(prjTaskService.selectPrjTaskById(prjTask.getTaskSid()));
        } else {
            return toAjax(row);
        }
    }

    /**
     * 编辑任务节点
     */
    @ApiOperation(value = "修改任务节点", notes = "修改任务节点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务节点", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid PrjTask prjTask) {
        return toAjax(prjTaskService.updatePrjTask(prjTask));
    }

    /**
     * 变更任务节点
     */
    @ApiOperation(value = "变更任务节点", notes = "变更任务节点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务节点", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PrjTask prjTask) {
        return toAjax(prjTaskService.changePrjTask(prjTask));
    }

    /**
     * 删除任务节点
     */
    @ApiOperation(value = "删除任务节点", notes = "删除任务节点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务节点", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> taskSids) {
        if (CollectionUtils.isEmpty(taskSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(prjTaskService.deletePrjTaskByIds(taskSids));
    }

    /**
     * 启用停用接口
     */
    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务节点", businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PrjTask prjTask) {
        if (ArrayUtil.isEmpty(prjTask.getTaskSidList())) {
            throw new CheckedException("请勾选行" );
        }
        if (StrUtil.isBlank(prjTask.getStatus())) {
            throw new CheckedException("参数缺失" );
        }
        return AjaxResult.success(prjTaskService.changeStatus(prjTask));
    }

    /**
     * 修改任务节点处理状态（确认）
     */
    @ApiOperation(value = "查询页面的确认", notes = "查询页面的确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务节点", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody PrjTask prjTask) {
        if (ArrayUtil.isEmpty(prjTask.getTaskSidList())) {
            throw new CheckedException("请勾选行" );
        }
        if (StrUtil.isBlank(prjTask.getHandleStatus())) {
            throw new CheckedException("参数缺失" );
        }
        return toAjax(prjTaskService.check(prjTask));
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(prjTaskService.importData(file));
    }

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/PDM_任务节点导入模板_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料(辅料)_V0.1.xlsx", "UTF-8"));
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
