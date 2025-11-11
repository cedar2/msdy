package com.platform.ems.controller;

import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.TecLinePosition;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecLinePositionService;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 线部位档案Controller
 *
 * @author hjj
 * @date 2021-08-19
 */
@RestController
@RequestMapping("/line/position")
@Api(tags = "线部位档案")
public class TecLinePositionController extends BaseController {

    @Autowired
    private ITecLinePositionService tecLinePositionService;
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
     * 查询线部位档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询线部位档案列表", notes = "查询线部位档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecLinePosition.class))
    public TableDataInfo list(@RequestBody TecLinePosition tecLinePosition) {
        startPage(tecLinePosition);
        List<TecLinePosition> list = tecLinePositionService.selectTecLinePositionList(tecLinePosition);
        return getDataTable(list);
    }

    /**
     * 导出线部位档案列表
     */
    @ApiOperation(value = "导出线部位档案列表", notes = "导出线部位档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecLinePosition tecLinePosition) throws IOException {
        List<TecLinePosition> list = tecLinePositionService.selectTecLinePositionList(tecLinePosition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecLinePosition> util = new ExcelUtil<>(TecLinePosition.class, dataMap);
        util.exportExcel(response, list, "线部位");
    }


    /**
     * 获取线部位档案详细信息
     */
    @ApiOperation(value = "获取线部位档案详细信息", notes = "获取线部位档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecLinePosition.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long linePositionSid) {
        if (linePositionSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecLinePositionService.selectTecLinePositionById(linePositionSid));
    }

    /**
     * 新增线部位档案
     */
    @ApiOperation(value = "新增线部位档案", notes = "新增线部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecLinePosition tecLinePosition) {
        int row = tecLinePositionService.insertTecLinePosition(tecLinePosition);
        return AjaxResult.success(tecLinePosition);
    }

    /**
     * 修改线部位档案
     */
    @ApiOperation(value = "修改线部位档案", notes = "修改线部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecLinePosition tecLinePosition) {
        return toAjax(tecLinePositionService.updateTecLinePosition(tecLinePosition));
    }

    /**
     * 变更线部位档案
     */
    @ApiOperation(value = "变更线部位档案", notes = "变更线部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid TecLinePosition tecLinePosition) {
        return toAjax(tecLinePositionService.changeTecLinePosition(tecLinePosition));
    }

    /**
     * 删除线部位档案
     */
    @ApiOperation(value = "删除线部位档案", notes = "删除线部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> linePositionSids) {
        if (CollectionUtils.isEmpty(linePositionSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecLinePositionService.deleteTecLinePositionByIds(linePositionSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecLinePosition tecLinePosition) {
        return AjaxResult.success(tecLinePositionService.changeStatus(tecLinePosition));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecLinePosition tecLinePosition) {
        tecLinePosition.setConfirmDate(new Date());
        tecLinePosition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        tecLinePosition.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(tecLinePositionService.check(tecLinePosition));
    }

    /**
     * 导入线部位档案
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入线部位档案", notes = "导入线部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(tecLinePositionService.importData(file));
    }

    /**
     * 上传线部位导入模板
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传线部位导入模板", notes = "上传线部位导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_线部位_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载版型部位导入模板", notes = "下载版型部位导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_线部位_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_线部位_V0.1.xlsx", "UTF-8"));
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
