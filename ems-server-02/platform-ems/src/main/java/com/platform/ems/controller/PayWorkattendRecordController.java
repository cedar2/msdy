package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.annotation.FieldScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.PayWorkattendRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.PayWorkattendRecordItemResponse;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.service.IPayWorkattendRecordService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
import com.platform.api.service.RemoteSystemService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
 * 考勤信息-主Controller
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@RestController
@RequestMapping("/work/attend/record")
@Api(tags = "考勤信息-主")
public class PayWorkattendRecordController extends BaseController {

    @Autowired
    private IPayWorkattendRecordService payWorkattendRecordService;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询考勤信息-主列表
     */
    @PostMapping("/list")
    @PreAuthorize(hasPermi = "ems:work:attend:record:list")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "查询考勤信息-主列表", notes = "查询考勤信息-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayWorkattendRecord.class))
    public TableDataInfo list(@RequestBody PayWorkattendRecord payWorkattendRecord) {
        startPage(payWorkattendRecord);
        List<PayWorkattendRecord> list = payWorkattendRecordService.selectPayWorkattendRecordList(payWorkattendRecord);
        return getDataTable(list);
    }

    @PostMapping("/report")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "查询考勤信息-明细报表", notes = "查询考勤信息-明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayWorkattendRecord.class))
    public TableDataInfo report(@RequestBody PayWorkattendRecord payWorkattendRecord) {
        startPage(payWorkattendRecord);
        List<PayWorkattendRecordItemResponse> list = payWorkattendRecordService.report(payWorkattendRecord);
        return getDataTable(list);
    }

    @PostMapping("/report/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "导出考勤明细报表", notes = "导出考勤明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void exportReport(HttpServletResponse response, PayWorkattendRecord payWorkattendRecord) throws IOException {
        List<PayWorkattendRecordItemResponse> list = payWorkattendRecordService.report(payWorkattendRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayWorkattendRecordItemResponse> util = new ExcelUtil<>(PayWorkattendRecordItemResponse.class, dataMap);
        util.exportExcel(response, list, "考勤明细");
    }

    /**
     * 导出考勤信息-主列表
     */
    @PostMapping("/export")
    @PreAuthorize(hasPermi = "ems:work:attend:record:export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @Log(title = "考勤信息-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出考勤信息-主列表", notes = "导出考勤信息-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, PayWorkattendRecord payWorkattendRecord) throws IOException {
        List<PayWorkattendRecord> list = payWorkattendRecordService.selectPayWorkattendRecordList(payWorkattendRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayWorkattendRecord> util = new ExcelUtil<>(PayWorkattendRecord.class, dataMap);
        util.exportExcel(response, list, "考勤信息");
    }


    /**
     * 获取考勤信息-主详细信息
     */
    @ApiOperation(value = "获取考勤信息-主详细信息", notes = "获取考勤信息-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayWorkattendRecord.class))
    @PreAuthorize(hasPermi = "ems:work:attend:record:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long workattendRecordSid) {
        if (workattendRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payWorkattendRecordService.selectPayWorkattendRecordById(workattendRecordSid));
    }

    /**
     * 新增考勤信息-主
     */
    @ApiOperation(value = "新增考勤信息-主", notes = "新增考勤信息-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:work:attend:record:add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "考勤信息-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PayWorkattendRecord payWorkattendRecord) {
        return toAjax(payWorkattendRecordService.insertPayWorkattendRecord(payWorkattendRecord));
    }

    /**
     * 修改考勤信息-主
     */
    @ApiOperation(value = "修改考勤信息-主", notes = "修改考勤信息-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:work:attend:record:edit")
    @Log(title = "考勤信息-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PayWorkattendRecord payWorkattendRecord) {
        return toAjax(payWorkattendRecordService.updatePayWorkattendRecord(payWorkattendRecord));
    }

    /**
     * 变更考勤信息-主
     */
    @ApiOperation(value = "变更考勤信息-主", notes = "变更考勤信息-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:work:attend:record:change")
    @Log(title = "考勤信息-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayWorkattendRecord payWorkattendRecord) {
        return toAjax(payWorkattendRecordService.changePayWorkattendRecord(payWorkattendRecord));
    }

    /**
     * 删除考勤信息-主
     */
    @ApiOperation(value = "删除考勤信息-主", notes = "删除考勤信息-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:work:attend:record:remove")
    @Log(title = "考勤信息-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> workattendRecordSids) {
        if (CollectionUtils.isEmpty(workattendRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payWorkattendRecordService.deletePayWorkattendRecordByIds(workattendRecordSids));
    }

    @PreAuthorize(hasPermi = "ems:work:attend:record:check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "考勤信息-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PayWorkattendRecord payWorkattendRecord) {
        return toAjax(payWorkattendRecordService.check(payWorkattendRecord));
    }

    /**
     * 导出某一笔考勤信息记录明细列表
     */
    @Log(title = "导出某一笔考勤信息记录明细列表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出某一笔考勤信息记录明细列表", notes = "导出某一笔考勤信息记录明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void exportItem(HttpServletResponse response, PayWorkattendRecord payWorkattendRecord) throws IOException {
        payWorkattendRecordService.exportItemByRecord(response, payWorkattendRecord);
    }

    /**
     * 导入某一笔考勤的考勤清单明细
     */
    @PostMapping("/item/import")
    @ApiOperation(value = "导入某一笔考勤的考勤清单明细", notes = "导入某一笔考勤的考勤清单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importItemData(@RequestParam MultipartFile file, @RequestParam String workattendRecordCode) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        if (StrUtil.isBlank(workattendRecordCode)) {
            throw new BaseException("请选择一笔考勤单进行导入");
        }
        Object response = payWorkattendRecordService.importItemData(file, workattendRecordCode);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入-考勤（查询页面）", notes = "导入-考勤（查询页面）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public EmsResultEntity importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return payWorkattendRecordService.importDataM(file);
    }

    @ApiOperation(value = "下载考勤（查询页面）导入模板", notes = "下载考勤（查询页面）导入模板")
    @PostMapping("/importTemplate")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_考勤表_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_考勤表_V0.1", "UTF-8"));
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
