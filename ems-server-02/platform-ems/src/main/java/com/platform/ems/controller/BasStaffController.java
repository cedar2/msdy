package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.constant.ConstantsEms;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.BasDepartment;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.dto.response.form.BasStaffConditionForm;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.service.ISystemDictDataService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 员工档案Controller
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/staff")
@Api(tags = "员工档案")
public class BasStaffController extends BaseController {

    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询员工档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询员工档案列表", notes = "查询员工档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaff.class))
    public TableDataInfo list(@RequestBody BasStaff basStaff) {
        startPage(basStaff);
        List<BasStaff> list = basStaffService.selectBasStaffList(basStaff);
        return getDataTable(list);
    }

    /**
     * 导出员工档案列表
     */
    @ApiOperation(value = "导出员工档案列表", notes = "导出员工档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasStaff basStaff) throws IOException {
        List<BasStaff> list = basStaffService.selectBasStaffList(basStaff);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasStaff> util = new ExcelUtil<>(BasStaff.class, dataMap);
        util.exportExcel(response, list, "员工");
    }

    /**
     * 获取员工档案详细信息
     */
    @ApiOperation(value = "获取员工档案详细信息", notes = "获取员工档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaff.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long staffSid) {
        return AjaxResult.success(basStaffService.selectBasStaffById(staffSid));
    }

    /**
     * 新增员工档案
     */
    @ApiOperation(value = "新增员工档案", notes = "新增员工档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasStaff basStaff) {
        int row = basStaffService.insertBasStaff(basStaff);
        return AjaxResult.success(basStaff);
    }

    /**
     * 修改员工档案
     */
    @ApiOperation(value = "修改员工档案", notes = "修改员工档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasStaff basStaff) {
        return toAjax(basStaffService.updateBasStaff(basStaff));
    }

    /**
     * 删除员工档案
     */
    @ApiOperation(value = "删除员工档案", notes = "删除员工档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> staffSids) {
        if (ArrayUtil.isEmpty(staffSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basStaffService.deleteBasStaffByIds(staffSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody @Valid BasStaff basStaff) {
        if (StrUtil.isEmpty(basStaff.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basStaffService.changeStatus(basStaff));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasStaff basStaff) {
        if (ArrayUtil.isEmpty(basStaff.getStaffSidList())) {
            throw new CheckedException("参数缺失");
        }
        basStaff.setConfirmDate(new Date());
        basStaff.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        basStaff.setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(basStaffService.check(basStaff));
    }

    /**
     * 员工档案下拉框列表
     */
    @PostMapping("/getStaffList")
    @ApiOperation(value = "员工档案下拉框列表", notes = "员工档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaff.class))
    public AjaxResult getStaffList(@RequestBody BasStaff basStaff) {
        return AjaxResult.success(basStaffService.getStaffList(basStaff));
    }

    /**
     * 员工档案下拉框列表 适用于件薪那边 取并集
     */
    @PostMapping("/getList")
    @ApiOperation(value = "员工档案下拉框列表 适用于件薪那边 取并集", notes = "员工档案下拉框列表 适用于件薪那边 取并集")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaff.class))
    public AjaxResult getList(@RequestBody BasStaff basStaff) {
        return AjaxResult.success(basStaffService.getStaffAndWorkList(basStaff));
    }

    /**
     * 获取公司下的员工
     */
    @PostMapping("/getCompanyDept")
    @ApiOperation(value = "获取公司下的员工", notes = "获取公司下的员工")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaff.class))
    public AjaxResult getCompanyStaff(Long companySid) {
        return AjaxResult.success(basStaffService.getCompanyStaff(companySid));
    }

    /**
     * 导入员工档案
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入员工档案", notes = "导入员工档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(basStaffService.importData(file));
    }

    @ApiOperation(value = "下载员工导入模板", notes = "下载员工导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_员工_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_员工_V0.1.xlsx", "UTF-8"));
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

    /**
     * 考勤信息/工资单添加员工
     */
    @PostMapping("/addStaff")
    @ApiOperation(value = "考勤信息/工资单添加员工", notes = "考勤信息/工资单添加员工")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasDepartment.class))
    public TableDataInfo addStaff(@RequestBody BasStaff basStaff) {
        if (null == basStaff.getDefaultCompanySid()) {
            throw new BaseException("参数错误！");
        }
        startPage(basStaff);
        return getDataTable(basStaffService.addStaff(basStaff));
    }

    /**
     * 设置在离职状态
     */
    @ApiOperation(value = "设置在离职状态", notes = "设置在离职状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setIsOnJob")
    public AjaxResult setIsOnJob(@RequestBody BasStaff basStaff) {
        return toAjax(basStaffService.setIsOnJob(basStaff));
    }

    @ApiOperation(value = "员工档案新建时重名提醒", notes = "员工档案新建时重名提醒")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check/name")
    public AjaxResult checkName(@RequestBody @Valid BasStaff basStaff) {
        return AjaxResult.success(basStaffService.checkName(basStaff));
    }

    /**
     * 新增员工档案
     */
    @ApiOperation(value = "新增员工档案", notes = "新增员工档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addList")
    public AjaxResult addList(@RequestBody List<BasStaff> basStaffList) {
        return toAjax(basStaffService.insertBasStaff(basStaffList));
    }

    /**
     * 新增收入证明校验
     */
    @ApiOperation(value = "新增收入证明", notes = "新增收入证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkHIC")
    public AjaxResult addHrIncomeCertificate(Long staffSid) {
        return basStaffService.cheackHrIncomeCertificateById(staffSid);
    }

    /**
     * 新增其它人事证明校验
     */
    @ApiOperation(value = "新增其它人事证明", notes = "新增其它人事证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkHOPC")
    public AjaxResult addHrOtherPersonnelCertificate(Long staffSid) {
        return basStaffService.cheackHrOtherPersonnelCertificateById(staffSid);
    }

    /**
     * 员工工作状况报表
     */
    @PostMapping("/condition")
    @ApiOperation(value = "员工工作状况报表", notes = "员工工作状况报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaffConditionForm.class))
    public TableDataInfo conditionList(@RequestBody BasStaffConditionForm basStaff) {
        startPage(basStaff);
        List<BasStaffConditionForm> list = basStaffService.conditionBasStaffList(basStaff);
        return getDataTable(list);
    }

    /**
     * 新增离职证明校验
     */
    @ApiOperation(value = "新增离职证明", notes = "新增离职证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkHDC")
    public AjaxResult addHrDimissionCertificate(Long staffSid) {
        return basStaffService.cheackHrDimissionCertificateById(staffSid);
    }

    /**
     * 导出员工工作状况报表
     */
    @ApiOperation(value = "导出员工工作状况报表", notes = "导出员工工作状况报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/condition/export")
    public void conditionExport(HttpServletResponse response, BasStaffConditionForm basStaff) throws IOException {
        List<BasStaffConditionForm> list = basStaffService.conditionBasStaffList(basStaff);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasStaffConditionForm> util = new ExcelUtil<>(BasStaffConditionForm.class, dataMap);
        util.exportExcel(response, list, "员工工作状况报表");
    }

}
