package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.QuaSpecraftCheckRecord;
import com.platform.ems.service.IQuaSpecraftCheckRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 特殊工艺检测问题台账Controller
 *
 * @author admin
 * @date 2024-03-06
 */
@RestController
@RequestMapping("/qua/specraft/check/record")
@Api(tags = "特殊工艺检测问题台账")
public class QuaSpecraftCheckRecordController extends BaseController {

    @Autowired
    private IQuaSpecraftCheckRecordService quaSpecraftCheckRecordService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";


    /**
     * 查询特殊工艺检测问题台账列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询特殊工艺检测问题台账列表", notes = "查询特殊工艺检测问题台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaSpecraftCheckRecord.class))
    public TableDataInfo list(@RequestBody QuaSpecraftCheckRecord quaSpecraftCheckRecord) {
        startPage(quaSpecraftCheckRecord);
        List<QuaSpecraftCheckRecord> list = quaSpecraftCheckRecordService.selectQuaSpecraftCheckRecordList(quaSpecraftCheckRecord);
        return getDataTable(list);
    }

    /**
     * 导出特殊工艺检测问题台账列表
     */
    @ApiOperation(value = "导出特殊工艺检测问题台账列表", notes = "导出特殊工艺检测问题台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuaSpecraftCheckRecord quaSpecraftCheckRecord) throws IOException {
        List<QuaSpecraftCheckRecord> list = quaSpecraftCheckRecordService.selectQuaSpecraftCheckRecordList(quaSpecraftCheckRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<QuaSpecraftCheckRecord> util = new ExcelUtil<>(QuaSpecraftCheckRecord.class, dataMap);
        util.exportExcel(response, list, "特殊工艺检测问题台账");
    }

    /**
     * 获取特殊工艺检测问题台账详细信息
     */
    @ApiOperation(value = "获取特殊工艺检测问题台账详细信息", notes = "获取特殊工艺检测问题台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaSpecraftCheckRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long specraftCheckRecordSid) {
        if (specraftCheckRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(quaSpecraftCheckRecordService.selectQuaSpecraftCheckRecordById(specraftCheckRecordSid));
    }

    /**
     * 新增特殊工艺检测问题台账
     */
    @ApiOperation(value = "新增特殊工艺检测问题台账", notes = "新增特殊工艺检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid QuaSpecraftCheckRecord quaSpecraftCheckRecord) {
        int row = quaSpecraftCheckRecordService.insertQuaSpecraftCheckRecord(quaSpecraftCheckRecord);
        if (row > 0) {
            return AjaxResult.success("操作成功", new QuaSpecraftCheckRecord()
                    .setSpecraftCheckRecordSid(quaSpecraftCheckRecord.getSpecraftCheckRecordSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改特殊工艺检测问题台账", notes = "修改特殊工艺检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody QuaSpecraftCheckRecord quaSpecraftCheckRecord) {
        return toAjax(quaSpecraftCheckRecordService.updateQuaSpecraftCheckRecord(quaSpecraftCheckRecord));
    }

    /**
     * 变更特殊工艺检测问题台账
     */
    @ApiOperation(value = "变更特殊工艺检测问题台账", notes = "变更特殊工艺检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid QuaSpecraftCheckRecord quaSpecraftCheckRecord) {
        return toAjax(quaSpecraftCheckRecordService.changeQuaSpecraftCheckRecord(quaSpecraftCheckRecord));
    }

    /**
     * 删除特殊工艺检测问题台账
     */
    @ApiOperation(value = "删除特殊工艺检测问题台账", notes = "删除特殊工艺检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> specraftCheckRecordSids) {
        if (CollectionUtils.isEmpty(specraftCheckRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(quaSpecraftCheckRecordService.deleteQuaSpecraftCheckRecordByIds(specraftCheckRecordSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody QuaSpecraftCheckRecord quaSpecraftCheckRecord) {
        return toAjax(quaSpecraftCheckRecordService.check(quaSpecraftCheckRecord));
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importCategory(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(quaSpecraftCheckRecordService.importRecord(file));
    }

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/import/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String fileName = FILLE_PATH + "/" + "SCM_导入模板_特殊工艺检测问题台账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            InputStream inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("SCM_导入模板_特殊工艺检测问题台账_V1.0.xlsx", "UTF-8"));
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
