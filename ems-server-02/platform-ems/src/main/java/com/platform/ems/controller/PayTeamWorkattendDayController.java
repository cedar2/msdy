package com.platform.ems.controller;

import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.PayTeamWorkattendDay;
import com.platform.ems.domain.dto.response.export.PayTeamWorkattendDayResponse;
import com.platform.ems.service.IPayTeamWorkattendDayService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
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
import java.util.List;
import java.util.Map;

/**
 * 班组日出勤信息Controller
 *
 * @author linhongwei
 * @date 2022-07-27
 */
@RestController
@RequestMapping("/Pay/Team/Workattend/Day")
@Api(tags = "班组日出勤信息")
public class PayTeamWorkattendDayController extends BaseController {

    @Autowired
    private IPayTeamWorkattendDayService payTeamWorkattendDayService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";
    /**
     * 查询班组日出勤信息列表
     */
//    @PreAuthorize(hasPermi = "ems:day:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询班组日出勤信息列表", notes = "查询班组日出勤信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayTeamWorkattendDay.class))
    public TableDataInfo list(@RequestBody PayTeamWorkattendDay payTeamWorkattendDay) {
        startPage(payTeamWorkattendDay);
        List<PayTeamWorkattendDay> list = payTeamWorkattendDayService.selectPayTeamWorkattendDayList(payTeamWorkattendDay);
        return getDataTable(list);
    }
    @GetMapping("/default")
    @ApiOperation(value = "新建时获取默认值", notes = "新建时获取默认值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayTeamWorkattendDay.class))
    public AjaxResult getMgs() {
        return AjaxResult.success(payTeamWorkattendDayService.getPayTeamWorkattend());
    }
    /**
     * 导出班组日出勤信息列表
     */
//    @PreAuthorize(hasPermi = "ems:day:export")
    @Log(title = "班组日出勤信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出班组日出勤信息列表", notes = "导出班组日出勤信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PayTeamWorkattendDay payTeamWorkattendDay) throws IOException {
        List<PayTeamWorkattendDay> list = payTeamWorkattendDayService.selectPayTeamWorkattendDayList(payTeamWorkattendDay);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayTeamWorkattendDayResponse> util = new ExcelUtil<>(PayTeamWorkattendDayResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,PayTeamWorkattendDayResponse::new), "班组日出勤");
    }


    /**
     * 获取班组日出勤信息详细信息
     */
    @ApiOperation(value = "获取班组日出勤信息详细信息", notes = "获取班组日出勤信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayTeamWorkattendDay.class))
//    @PreAuthorize(hasPermi = "ems:day:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long teamWorkattendDaySid) {
        if (teamWorkattendDaySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payTeamWorkattendDayService.selectPayTeamWorkattendDayById(teamWorkattendDaySid));
    }

    /**
     * 班组日出勤 获取
     */
    @ApiOperation(value = "获取班组日出勤信息详细信息", notes = "获取班组日出勤信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayTeamWorkattendDay.class))
    @PostMapping("/getInfoBy")
    public AjaxResult getInfoBy(@RequestBody PayTeamWorkattendDay payTeamWorkattendDay) {
        return AjaxResult.success(payTeamWorkattendDayService.selectPayTeamWorkattendDayListBy(payTeamWorkattendDay));
    }

    /**
     * 新增班组日出勤信息
     */
    @ApiOperation(value = "新增班组日出勤信息", notes = "新增班组日出勤信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:day:add")
    @Log(title = "班组日出勤信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid PayTeamWorkattendDay payTeamWorkattendDay) {
        return toAjax(payTeamWorkattendDayService.insertPayTeamWorkattendDay(payTeamWorkattendDay));
    }

    @ApiOperation(value = "修改班组日出勤信息", notes = "修改班组日出勤信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:day:edit")
    @Log(title = "班组日出勤信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody PayTeamWorkattendDay payTeamWorkattendDay) {
        return toAjax(payTeamWorkattendDayService.updatePayTeamWorkattendDay(payTeamWorkattendDay));
    }

    /**
     * 变更班组日出勤信息
     */
    @ApiOperation(value = "变更班组日出勤信息", notes = "变更班组日出勤信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:day:change")
    @Log(title = "班组日出勤信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayTeamWorkattendDay payTeamWorkattendDay) {
        return toAjax(payTeamWorkattendDayService.changePayTeamWorkattendDay(payTeamWorkattendDay));
    }

    /**
     * 删除班组日出勤信息
     */
    @ApiOperation(value = "删除班组日出勤信息", notes = "删除班组日出勤信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:day:remove")
    @Log(title = "班组日出勤信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> teamWorkattendDaySids) {
        if (CollectionUtils.isEmpty(teamWorkattendDaySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payTeamWorkattendDayService.deletePayTeamWorkattendDayByIds(teamWorkattendDaySids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "班组日出勤信息", businessType = BusinessType.UPDATE)
//    @PreAuthorize(hasPermi = "ems:day:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PayTeamWorkattendDay payTeamWorkattendDay) {
        return AjaxResult.success(payTeamWorkattendDayService.changeStatus(payTeamWorkattendDay));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:day:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "班组日出勤信息", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody PayTeamWorkattendDay payTeamWorkattendDay) {
        return toAjax(payTeamWorkattendDayService.check(payTeamWorkattendDay));
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入-班组日出勤信息", notes = "导入-班组日出勤信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return payTeamWorkattendDayService.importDataPur(file);
    }

    @ApiOperation(value = "下载班组日出勤信息导入模板", notes = "下载班组日出勤信息导入模板")
    @PostMapping("/importTemplate")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_班组日出勤_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_班组日出勤_V0.1", "UTF-8"));
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
