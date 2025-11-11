package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.BasImage;
import com.platform.ems.service.IBasImageService;
import com.platform.ems.service.ISystemDictDataService;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 图案档案Controller
 *
 * @author chenkw
 * @date 2022-12-14
 */
@RestController
@RequestMapping("/image")
@Api(tags = "图案档案")
public class BasImageController extends BaseController {

    @Autowired
    private IBasImageService basImageService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询图案档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询图案档案列表",
                  notes = "查询图案档案列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = BasImage.class))
    public TableDataInfo list(@RequestBody BasImage basImage) {
        startPage(basImage);
        List<BasImage> list = basImageService.selectBasImageList(basImage);
        return getDataTable(list);
    }

    /**
     * 导出图案档案列表
     */
    @Log(title = "图案档案",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出图案档案列表",
                  notes = "导出图案档案列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasImage basImage) throws IOException {
        List<BasImage> list = basImageService.selectBasImageList(basImage);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasImage> util = new ExcelUtil<>(BasImage.class, dataMap);
        util.exportExcel(response, list, "图案档案");
    }

    /**
     * 获取图案档案详细信息
     */
    @ApiOperation(value = "获取图案档案详细信息",
                  notes = "获取图案档案详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = BasImage.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long imageSid) {
        if (imageSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basImageService.selectBasImageById(imageSid));
    }

    /**
     * 新增图案档案
     */
    @ApiOperation(value = "新增图案档案",
                  notes = "新增图案档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "图案档案",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid BasImage basImage) {
        int affectRowCount = basImageService.insertBasImage(basImage);
        if (affectRowCount != 1) {
            throw new CheckedException("插入异常，影响行数不等于1：" + affectRowCount);
        }

        return AjaxResult.success(basImageService.selectBasImageById(basImage.getImageSid()));
    }

    @ApiOperation(value = "修改图案档案",
                  notes = "修改图案档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "图案档案",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",
                interval = 3000)
    public AjaxResult edit(@RequestBody @Valid BasImage basImage) {
        return toAjax(basImageService.updateBasImage(basImage));
    }

    /**
     * 变更图案档案
     */
    @ApiOperation(value = "变更图案档案",
                  notes = "变更图案档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "图案档案",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasImage basImage) {
        return toAjax(basImageService.changeBasImage(basImage));
    }

    /**
     * 删除图案档案
     */
    @ApiOperation(value = "删除图案档案",
                  notes = "删除图案档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "图案档案",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> imageSids) {
        if (CollectionUtils.isEmpty(imageSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basImageService.deleteBasImageByIds(imageSids));
    }

    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "图案档案",
         businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasImage basImage) {
        if (ArrayUtil.isEmpty(basImage.getImageSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(basImage.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basImageService.changeStatus(basImage));
    }

    @ApiOperation(value = "确认",
                  notes = "确认")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "图案档案",
         businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody BasImage basImage) {
        if (ArrayUtil.isEmpty(basImage.getImageSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(basImage.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basImageService.check(basImage));
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
        return AjaxResult.success(basImageService.importData(file));
    }

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/PDM_图案导入模板_V0.1.xlsx";
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
