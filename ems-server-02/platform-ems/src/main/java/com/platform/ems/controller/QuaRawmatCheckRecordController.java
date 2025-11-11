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
import com.platform.ems.domain.QuaRawmatCheckRecord;
import com.platform.ems.service.IQuaRawmatCheckRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物料检测问题台账Controller
 *
 * @author admin
 * @date 2024-03-06
 */
@RestController
@RequestMapping("/qua/rawmat/check/record")
@Api(tags = "物料检测问题台账")
public class QuaRawmatCheckRecordController extends BaseController {

    @Autowired
    private IQuaRawmatCheckRecordService quaRawmatCheckRecordService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询物料检测问题台账列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询物料检测问题台账列表", notes = "查询物料检测问题台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaRawmatCheckRecord.class))
    public TableDataInfo list(@RequestBody QuaRawmatCheckRecord quaRawmatCheckRecord) {
        startPage(quaRawmatCheckRecord);
        List<QuaRawmatCheckRecord> list = quaRawmatCheckRecordService.selectQuaRawmatCheckRecordList(quaRawmatCheckRecord);
        return getDataTable(list);
    }

    /**
     * 导出物料检测问题台账列表
     */
    @ApiOperation(value = "导出物料检测问题台账列表", notes = "导出物料检测问题台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuaRawmatCheckRecord quaRawmatCheckRecord) throws IOException {
        List<QuaRawmatCheckRecord> list = quaRawmatCheckRecordService.selectQuaRawmatCheckRecordList(quaRawmatCheckRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<QuaRawmatCheckRecord> util = new ExcelUtil<>(QuaRawmatCheckRecord.class, dataMap);
        util.exportExcel(response, list, "物料检测问题台账");
    }

    /**
     * 获取物料检测问题台账详细信息
     */
    @ApiOperation(value = "获取物料检测问题台账详细信息", notes = "获取物料检测问题台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaRawmatCheckRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long rawmatCheckRecordSid) {
        if (rawmatCheckRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(quaRawmatCheckRecordService.selectQuaRawmatCheckRecordById(rawmatCheckRecordSid));
    }

    /**
     * 新增物料检测问题台账
     */
    @ApiOperation(value = "新增物料检测问题台账", notes = "新增物料检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid QuaRawmatCheckRecord quaRawmatCheckRecord) {
        int row = quaRawmatCheckRecordService.insertQuaRawmatCheckRecord(quaRawmatCheckRecord);
        if (row > 0) {
            return AjaxResult.success("操作成功", new QuaRawmatCheckRecord()
                    .setRawmatCheckRecordSid(quaRawmatCheckRecord.getRawmatCheckRecordSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改物料检测问题台账", notes = "修改物料检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody QuaRawmatCheckRecord quaRawmatCheckRecord) {
        return toAjax(quaRawmatCheckRecordService.updateQuaRawmatCheckRecord(quaRawmatCheckRecord));
    }

    /**
     * 变更物料检测问题台账
     */
    @ApiOperation(value = "变更物料检测问题台账", notes = "变更物料检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid QuaRawmatCheckRecord quaRawmatCheckRecord) {
        return toAjax(quaRawmatCheckRecordService.changeQuaRawmatCheckRecord(quaRawmatCheckRecord));
    }

    /**
     * 删除物料检测问题台账
     */
    @ApiOperation(value = "删除物料检测问题台账", notes = "删除物料检测问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> rawmatCheckRecordSids) {
        if (CollectionUtils.isEmpty(rawmatCheckRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(quaRawmatCheckRecordService.deleteQuaRawmatCheckRecordByIds(rawmatCheckRecordSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody QuaRawmatCheckRecord quaRawmatCheckRecord) {
        return toAjax(quaRawmatCheckRecordService.check(quaRawmatCheckRecord));
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
        return AjaxResult.success(quaRawmatCheckRecordService.importRecord(file));
    }

    /**
     * 下载物料检测问题台账导入模板
     */
    @ApiOperation(value = "下载物料检测问题台账导入模板", notes = "下载物料检测问题台账导入模板")
    @PostMapping("/import/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String fileName = FILLE_PATH + "/" + "SCM_导入模板_物料检测问题台账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            InputStream inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("SCM_导入模板_物料检测问题台账_V1.0.xlsx", "UTF-8"));
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
