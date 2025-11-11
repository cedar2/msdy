package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.poi.ExcelUtil;
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
import com.platform.ems.domain.QuaPurInspectionRecord;
import com.platform.ems.service.IQuaPurInspectionRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 采购验货问题台账Controller
 *
 * @author platform
 * @date 2024-09-20
 */
@Api(tags = "采购验货问题台账" )
@RestController
@RequestMapping("/qua/pur/inspect/record" )
public class QuaPurInspectionRecordController extends BaseController {

    @Autowired
    private IQuaPurInspectionRecordService quaPurInspectionRecordService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购验货问题台账列表
     */
    // @PreAuthorize(hasPermi = "ems:record:list" )
    @ApiOperation(value = "查询采购验货问题台账列表" , notes = "查询采购验货问题台账列表" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = QuaPurInspectionRecord.class))
    @PostMapping("/list" )
    public TableDataInfo list(@RequestBody QuaPurInspectionRecord quaPurInspectionRecord) {
        startPage(quaPurInspectionRecord);
        List<QuaPurInspectionRecord> list = quaPurInspectionRecordService.selectQuaPurInspectionRecordList(quaPurInspectionRecord);
        return getDataTable(list);
    }

    /**
     * 导出采购验货问题台账列表
     */
    // @PreAuthorize(hasPermi = "ems:record:export" )
    @ApiOperation(value = "导出采购验货问题台账列表" , notes = "导出采购验货问题台账列表" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = void.class))
    @PostMapping("/export" )
    public void export(HttpServletResponse response, QuaPurInspectionRecord quaPurInspectionRecord) throws IOException {
        List<QuaPurInspectionRecord> list = quaPurInspectionRecordService.selectQuaPurInspectionRecordList(quaPurInspectionRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<QuaPurInspectionRecord> util = new ExcelUtil<>(QuaPurInspectionRecord.class, dataMap);
        util.exportExcel(response, list, "采购验货问题台账");
    }

    /**
     * 获取采购验货问题台账详细信息
     */
    // @PreAuthorize(hasPermi = "ems:record:query" )
    @ApiOperation(value = "获取采购验货问题台账详细信息" , notes = "获取采购验货问题台账详细信息" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = QuaPurInspectionRecord.class))
    @PostMapping("/getInfo" )
    public AjaxResult getInfo(Long purInspectionRecordSid) {
        if (purInspectionRecordSid==null){
            throw new CheckedException("参数缺失" );
        }
        return AjaxResult.success(quaPurInspectionRecordService.selectQuaPurInspectionRecordById(purInspectionRecordSid));
    }

    /**
     * 新增采购验货问题台账
     */
    // @PreAuthorize(hasPermi = "ems:record:add")
    // @Log(title = "采购验货问题台账", businessType = BusinessType.INSERT)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "新增采购验货问题台账", notes = "新增采购验货问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid QuaPurInspectionRecord quaPurInspectionRecord) {
        return toAjax(quaPurInspectionRecordService.insertQuaPurInspectionRecord(quaPurInspectionRecord));
    }

    // @PreAuthorize(hasPermi = "ems:record:edit")
    // @Log(title = "采购验货问题台账", businessType = BusinessType.UPDATE)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "修改采购验货问题台账", notes = "修改采购验货问题台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody QuaPurInspectionRecord quaPurInspectionRecord) {
        return toAjax(quaPurInspectionRecordService.updateQuaPurInspectionRecord(quaPurInspectionRecord));
    }

    /**
     * 变更采购验货问题台账
     */
    // @PreAuthorize(hasPermi = "ems:record:change" )
    // @Log(title = "采购验货问题台账" , businessType = BusinessType.CHANGE)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "变更采购验货问题台账" , notes = "变更采购验货问题台账" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PostMapping("/change" )
    public AjaxResult change(@RequestBody @Valid QuaPurInspectionRecord quaPurInspectionRecord) {
        return toAjax(quaPurInspectionRecordService.changeQuaPurInspectionRecord(quaPurInspectionRecord));
    }

    /**
     * 删除采购验货问题台账
     */
    // @PreAuthorize(hasPermi = "ems:record:remove" )
    // @Log(title = "采购验货问题台账" , businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除采购验货问题台账" , notes = "删除采购验货问题台账" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PostMapping("/delete" )
    public AjaxResult remove(@RequestBody List<Long>  purInspectionRecordSids) {
        if (CollectionUtils.isEmpty(purInspectionRecordSids)) {
            throw new CheckedException("参数缺失" );
        }
        return toAjax(quaPurInspectionRecordService.deleteQuaPurInspectionRecordByIds(purInspectionRecordSids));
    }



    // @PreAuthorize(hasPermi = "ems:record:check")
    // @Log(title = "采购验货问题台账", businessType = BusinessType.CHECK)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody QuaPurInspectionRecord quaPurInspectionRecord) {
        return toAjax(quaPurInspectionRecordService.check(quaPurInspectionRecord));
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
        return AjaxResult.success(quaPurInspectionRecordService.importRecord(file));
    }

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/import/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String fileName = FILLE_PATH + "/" + "SCM_导入模板_采购验货问题台账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            InputStream inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("SCM_导入模板_采购验货问题台账_V1.0.xlsx", "UTF-8"));
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
