package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.api.service.RemoteFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManProcess;
import com.platform.ems.domain.dto.request.ManProcessActionRequest;
import com.platform.ems.service.IManProcessService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工序Controller
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@RestController
@RequestMapping("/process")
@Api(tags = "工序")
public class ManProcessController extends BaseController {

    @Autowired
    private IManProcessService manProcessService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";
    /**
     * 查询工序列表
     */
    @PreAuthorize(hasPermi = "ems:man:process:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工序列表", notes = "查询工序列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcess.class))
    public TableDataInfo list(@RequestBody ManProcess manProcess) {
        startPage(manProcess);
        List<ManProcess> list = manProcessService.selectManProcessList(manProcess);
        return getDataTable(list);
    }

    /**
     * 导出工序列表
     */
    @PreAuthorize(hasPermi = "ems:man:process:export")
    @Log(title = "工序", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工序列表", notes = "导出工序列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProcess manProcess) throws IOException {
        List<ManProcess> list = manProcessService.selectManProcessList(manProcess);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ManProcess> util = new ExcelUtil<>(ManProcess.class,dataMap);
        util.exportExcel(response, list, "工序");
    }

    /**
     * 获取工序详细信息
     */
    @ApiOperation(value = "获取工序详细信息", notes = "获取工序详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcess.class))
    @PreAuthorize(hasPermi = "ems:man:process:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long processSid) {
        return AjaxResult.success(manProcessService.selectManProcessById(processSid));
    }

    /**
     * 新增工序
     */
    @ApiOperation(value = "新增工序", notes = "新增工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:process:add")
    @Log(title = "工序", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManProcess manProcess) {
        int row = manProcessService.insertManProcess(manProcess);
        return AjaxResult.success(manProcess);
    }

    /**
     * 修改工序
     */
    @ApiOperation(value = "修改工序", notes = "修改工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:process:edit")
    @Log(title = "工序", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManProcess manProcess) {
        return toAjax(manProcessService.updateManProcess(manProcess));
    }

    /**
     * 删除工序
     */
    @ApiOperation(value = "删除工序", notes = "删除工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:process:remove")
    @Log(title = "工序", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  processSids) {
        return toAjax(manProcessService.deleteManProcessByIds(processSids));
    }
    /**
     * 确认工序
     */
    @ApiOperation(value = "确认工序", notes = "确认工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:process:check")
    @Log(title = "工序", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    public AjaxResult confirm(@RequestBody ManProcessActionRequest action) {
        return toAjax(manProcessService.confirm(action));
    }
    /**
     * 启用/停用 工序
     */
    @ApiOperation(value = "启用/停用工序", notes = "启用/停用工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:process:enbleordisable")
    @Log(title = "工序", businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    public AjaxResult status(@RequestBody ManProcessActionRequest action) {
        return AjaxResult.success(manProcessService.status(action));
    }
    /**
     * 变更工序
     */
    @ApiOperation(value = "变更工序", notes = "变更工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:process:change")
    @Log(title = "工序", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    public AjaxResult status(@RequestBody @Valid ManProcess manProcess) {
        return toAjax(manProcessService.change(manProcess));
    }

    /**
     * 工序档案列表
     */
    @ApiOperation(value = "工序档案下拉列表", notes = "工序档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcess.class))
//    @PreAuthorize(hasPermi = "ems:process:remove")
    @Log(title = "工序", businessType = BusinessType.UPDATE)
    @PostMapping("/getList")
    public AjaxResult getList(@RequestBody ManProcess manProcess) {
        return AjaxResult.success(manProcessService.getList(manProcess));
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入-工序", notes = "导入-工序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public EmsResultEntity importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return manProcessService.importDataPur(file);
    }
    @ApiOperation(value = "下载工序导入模板", notes = "下载工序导入模板")
    @PostMapping("/importTemplate")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_工序_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_工序_V1.0.xlsx", "UTF-8"));
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
