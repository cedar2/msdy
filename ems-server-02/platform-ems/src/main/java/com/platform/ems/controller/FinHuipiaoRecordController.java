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

import com.platform.common.annotation.Idempotent;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.service.ISysDictDataService;
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
import com.platform.ems.domain.FinHuipiaoRecord;
import com.platform.ems.service.IFinHuipiaoRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 汇票台账表Controller
 *
 * @author platform
 * @date 2024-03-12
 */
@Api(tags = "汇票台账表")
@RestController
@RequestMapping("/fin/huipiao/record")
public class FinHuipiaoRecordController extends BaseController {

    @Autowired
    private IFinHuipiaoRecordService finHuipiaoRecordService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询汇票台账表列表
     */
    @ApiOperation(value = "查询汇票台账表列表", notes = "查询汇票台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinHuipiaoRecord.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinHuipiaoRecord finHuipiaoRecord) {
        startPage(finHuipiaoRecord);
        List<FinHuipiaoRecord> list = finHuipiaoRecordService.selectFinHuipiaoRecordList(finHuipiaoRecord);
        return getDataTable(list);
    }

    /**
     * 导出汇票台账表列表
     */
    @ApiOperation(value = "导出汇票台账表列表", notes = "导出汇票台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinHuipiaoRecord finHuipiaoRecord) throws IOException {
        List<FinHuipiaoRecord> list = finHuipiaoRecordService.selectFinHuipiaoRecordList(finHuipiaoRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinHuipiaoRecord> util = new ExcelUtil<>(FinHuipiaoRecord.class, dataMap);
        util.exportExcel(response, list, "汇票台账表");
    }

    /**
     * 获取汇票台账表详细信息
     */
    @ApiOperation(value = "获取汇票台账表详细信息", notes = "获取汇票台账表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinHuipiaoRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long huipiaoRecordSid) {
        if (huipiaoRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finHuipiaoRecordService.selectFinHuipiaoRecordById(huipiaoRecordSid));
    }

    /**
     * 新增汇票台账表
     */
    @ApiOperation(value = "新增汇票台账表", notes = "新增汇票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinHuipiaoRecord finHuipiaoRecord) {
        int row = finHuipiaoRecordService.insertFinHuipiaoRecord(finHuipiaoRecord);
        if (row > 0) {
            return AjaxResult.success(new FinHuipiaoRecord().setHuipiaoRecordSid(finHuipiaoRecord.getHuipiaoRecordSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改汇票台账表", notes = "修改汇票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinHuipiaoRecord finHuipiaoRecord) {
        return toAjax(finHuipiaoRecordService.updateFinHuipiaoRecord(finHuipiaoRecord));
    }

    /**
     * 更改状态信息
     */
    @ApiOperation(value = "更改状态信息", notes = "更改状态信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/setDateStatus")
    public AjaxResult setDateStatus(@RequestBody @Valid FinHuipiaoRecord finHuipiaoRecord) {
        return toAjax(finHuipiaoRecordService.setDateStatusByid(finHuipiaoRecord));
    }

    /**
     * 变更汇票台账表
     */
    @ApiOperation(value = "变更汇票台账表", notes = "变更汇票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinHuipiaoRecord finHuipiaoRecord) {
        return toAjax(finHuipiaoRecordService.changeFinHuipiaoRecord(finHuipiaoRecord));
    }

    /**
     * 删除汇票台账表
     */
    @ApiOperation(value = "删除汇票台账表", notes = "删除汇票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> huipiaoRecordSids) {
        if (CollectionUtils.isEmpty(huipiaoRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finHuipiaoRecordService.deleteFinHuipiaoRecordByIds(huipiaoRecordSids));
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinHuipiaoRecord finHuipiaoRecord) {
        return AjaxResult.success(finHuipiaoRecordService.check(finHuipiaoRecord));
    }

    @ApiOperation(value = "修改汇票台账表当前公司", notes = "修改汇票台账表当前公司")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/update/companyNew")
    public AjaxResult updateRecordCompanyNew(@RequestBody FinHuipiaoRecord finHuipiaoRecord) {
        return AjaxResult.success(finHuipiaoRecordService.updateRecordCompanyNew(finHuipiaoRecord));
    }

    @ApiOperation(value = "修改汇票台账表使用状态", notes = "修改汇票台账表使用状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/update/userStatus")
    public AjaxResult updateRecordUseStatus(@RequestBody FinHuipiaoRecord finHuipiaoRecord) {
        return AjaxResult.success(finHuipiaoRecordService.updateRecordUseStatus(finHuipiaoRecord));
    }

    @ApiOperation(value = "修改汇票台账表使用状态", notes = "修改汇票台账表使用状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/update/userRecord")
    public AjaxResult updateRecordItem(@RequestBody FinHuipiaoRecord finHuipiaoRecord) {
        return AjaxResult.success(finHuipiaoRecordService.updateRecordUseRecord(finHuipiaoRecord));
    }

    /**
     * 导入汇票台账
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入汇票台账信息", notes = "导入汇票台账信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = finHuipiaoRecordService.importData(file);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }


    @ApiOperation(value = "下载汇票台账导入模板", notes = "下载汇票台账导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_汇票台账_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_汇票台账_V0.1.xlsx", "UTF-8"));
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
