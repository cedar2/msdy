package com.platform.ems.controller;

import cn.hutool.core.collection.CollUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.PayProcessStep;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.IPayProcessStepService;
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
import java.util.List;
import java.util.Map;

/**
 * 通用道序Controller
 *
 * @author linhongwei
 * @date 2021-09-07
 */
@RestController
@RequestMapping("/process/step")
@Api(tags = "通用道序")
public class PayProcessStepController extends BaseController {

    @Autowired
    private IPayProcessStepService payProcessStepService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询通用道序列表
     */
    @PreAuthorize(hasPermi = "ems:process:step:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询通用道序列表", notes = "查询通用道序列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStep.class))
    public TableDataInfo list(@RequestBody PayProcessStep payProcessStep) {
        startPage(payProcessStep);
        List<PayProcessStep> list = payProcessStepService.selectPayProcessStepList(payProcessStep);
        return getDataTable(list);
    }

    /**
     * 导出通用道序列表
     */
    @PreAuthorize(hasPermi = "ems:process:step:export")
    @Log(title = "通用道序", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出通用道序列表", notes = "导出通用道序列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PayProcessStep payProcessStep) throws IOException {
        List<PayProcessStep> list = payProcessStepService.selectPayProcessStepList(payProcessStep);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProcessStep> util = new ExcelUtil<>(PayProcessStep.class, dataMap);
        util.exportExcel(response, list, "道序");
    }


    /**
     * 获取通用道序详细信息
     */
    @ApiOperation(value = "获取通用道序详细信息", notes = "获取通用道序详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStep.class))
    @PreAuthorize(hasPermi = "ems:process:step:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long processStepSid) {
        if (processStepSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payProcessStepService.selectPayProcessStepById(processStepSid));
    }

    /**
     * 新增通用道序
     */
    @ApiOperation(value = "新增通用道序", notes = "新增通用道序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:process:step:add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "通用道序", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PayProcessStep payProcessStep) {
        int row = payProcessStepService.insertPayProcessStep(payProcessStep);
        return AjaxResult.success(payProcessStep);
    }

    /**
     * 修改通用道序
     */
    @ApiOperation(value = "修改通用道序", notes = "修改通用道序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:process:step:edit")
    @Log(title = "通用道序", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PayProcessStep payProcessStep) {
        return toAjax(payProcessStepService.updatePayProcessStep(payProcessStep));
    }

    /**
     * 变更通用道序
     */
    @ApiOperation(value = "变更通用道序", notes = "变更通用道序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:process:step:change")
    @Log(title = "通用道序", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayProcessStep payProcessStep) {
        return toAjax(payProcessStepService.changePayProcessStep(payProcessStep));
    }

    /**
     * 删除通用道序
     */
    @ApiOperation(value = "删除通用道序", notes = "删除通用道序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:process:step:remove")
    @Log(title = "通用道序", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> processStepSids) {
        if (CollectionUtils.isEmpty(processStepSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payProcessStepService.deletePayProcessStepByIds(processStepSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通用道序", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:process:step:enbleordisable")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PayProcessStep payProcessStep) {
        return AjaxResult.success(payProcessStepService.changeStatus(payProcessStep));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:process:step:check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通用道序", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PayProcessStep payProcessStep) {
        return toAjax(payProcessStepService.check(payProcessStep));
    }

    /**
     * 通用道序停用校验
     */
    @ApiOperation(value = "停用校验", notes = "停用校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通用道序停用校验", businessType = BusinessType.UPDATE)
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/disableVerify")
    public AjaxResult disableVerify(@RequestBody PayProcessStep payProcessStep) {
        if (CollUtil.isEmpty(payProcessStep.getProcessStepSids())) {
            throw new BaseException("参数错误");
        }
        return AjaxResult.success(payProcessStepService.disableVerify(payProcessStep));
    }

    /**
     * 导入通用道序
     */
    @PreAuthorize(hasPermi = "ems:process:step:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入通用道序", notes = "导入通用道序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(payProcessStepService.importData(file));
    }

    @ApiOperation(value = "下载道序导入模板", notes = "下载道序导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_道序_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_道序_V0.1.xlsx", "UTF-8"));
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
