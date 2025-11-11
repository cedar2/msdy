package com.platform.ems.controller;

import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.TecModelPosition;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecModelPositionService;
import com.platform.ems.task.BasMaterialWarningTask;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 版型部位档案Controller
 *
 * @author ChenPinzhen
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/model/pos")
@Api(tags = "版型部位档案")
public class TecModelPositionController extends BaseController {

    @Autowired
    private ITecModelPositionService tecModelPositionService;
    @Autowired
    private BasMaterialWarningTask task;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 新增版型部位
     */
    @PreAuthorize(hasPermi = "ems:model:pos:add")
    @Log(title = "版型部位", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation(value = "新增版型部位", notes = "新增版型部位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult add(@RequestBody @Valid TecModelPosition tecModelPosition) {
        return AjaxResult.success(tecModelPositionService.insertTecModelPosition(tecModelPosition));
    }

    /**
     * 查询版型部位列表
     */
    @PreAuthorize(hasPermi = "ems:model:pos:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询版型部位列表", notes = "查询版型部位列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPosition.class))
    public TableDataInfo list(@RequestBody TecModelPosition tecModelPosition) {
        startPage(tecModelPosition);
        List<TecModelPosition> list = tecModelPositionService.selectTecModelPositionList(tecModelPosition);
        return getDataTable(list);
    }

    /**
     * 获取版型部位详细信息
     */
    @PreAuthorize(hasPermi = "ems:model:pos:query")
    @PostMapping("/getInfo")
    @ApiOperation(value = "获取版型部位详细信息", notes = "获取版型部位详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPosition.class))
    public AjaxResult getInfo(Long modelPositionSid) {
        return AjaxResult.success(tecModelPositionService.selectTecModelPositionById(modelPositionSid));
    }

    /**
     * 修改版型部位
     */
    @PreAuthorize(hasPermi = "ems:model:pos:edit")
    @Log(title = "版型部位", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ApiOperation(value = "修改版型部位", notes = "修改版型部位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult edit(@RequestBody @Valid TecModelPosition tecModelPosition) {
        return AjaxResult.success(tecModelPositionService.updateTecModelPosition(tecModelPosition));
    }

    /**
     * 删除版型部位
     */
    @PreAuthorize(hasPermi = "ems:model:pos:remove")
    @Log(title = "版型部位", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除版型部位", notes = "删除版型部位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult remove(@RequestBody String[] modelPositionSids) {
        return AjaxResult.success(tecModelPositionService.deleteTecModelPositionByIds(modelPositionSids));
    }

    /**
     * 版型部位确认
     */
    @PreAuthorize(hasPermi = "ems:model:pos:check")
    @Log(title = "版型部位", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "版型部位确认", notes = "版型部位确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody TecModelPosition tecModelPosition) {
        return AjaxResult.success(tecModelPositionService.confirm(tecModelPosition));
    }

    /**
     * 版型部位变更
     */
    @PreAuthorize(hasPermi = "ems:model:pos:change")
    @Log(title = "版型部位", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "版型部位变更", notes = "版型部位变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid TecModelPosition tecModelPosition) {
        return AjaxResult.success(tecModelPositionService.change(tecModelPosition));
    }

    /**
     * 批量启用/停用版型部位
     */
    @PreAuthorize(hasPermi = "ems:model:pos:enbleordisable")
    @Log(title = "版型部位", businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    @ApiOperation(value = "版型部位启用/停用", notes = "版型部位启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody TecModelPosition tecModelPosition) {
        return AjaxResult.success(tecModelPositionService.status(tecModelPosition));
    }

    /**
     * 导出版型部位档案列表
     */
    @PreAuthorize(hasPermi = "ems:model:pos:export")
    @Log(title = "版型部位档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出版型部位档案列表", notes = "导出版型部位档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, TecModelPosition tecModelPosition) throws IOException {
        List<TecModelPosition> list = tecModelPositionService.selectTecModelPositionList(tecModelPosition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecModelPosition> util = new ExcelUtil<>(TecModelPosition.class, dataMap);
        util.exportExcel(response, list, "版型部位");
    }

    /**
     * 版型部位下拉框列表
     */
    @PostMapping("/getModelPositionList")
    @ApiOperation(value = "版型部位下拉框列表", notes = "版型部位下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPosition.class))
    public AjaxResult getModelPositionList() {
        return AjaxResult.success(tecModelPositionService.getModelPositionList());
    }

    /**
     * 导入版型部位
     */
    @PreAuthorize(hasPermi = "ems:model:pos:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入版型部位", notes = "导入版型部位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(tecModelPositionService.importData(file));
    }

    /**
     * 上传版型部位导入模板
     */
    @PostMapping("/uploadTemplate")
    @PreAuthorize(hasPermi = "ems:model:pos:uploadTemplate")
    @ApiOperation(value = "上传版型部位导入模板", notes = "上传版型部位导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_版型部位_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载版型部位导入模板", notes = "下载版型部位导入模板")
    @PostMapping("/downloadTemplate")
    @PreAuthorize(hasPermi = "ems:model:pos:downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_版型部位_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_版型部位_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
