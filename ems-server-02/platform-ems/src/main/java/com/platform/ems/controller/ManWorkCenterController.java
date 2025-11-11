package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.ManWorkCenter;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManWorkCenterActionRequest;
import com.platform.ems.domain.dto.request.ManWorkCenterReportRequest;
import com.platform.ems.service.IManWorkCenterService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
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
import java.util.List;
import java.util.Map;

/**
 * 工作中心/班组Controller
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@RestController
@RequestMapping("/man/center")
@Api(tags = "工作中心/班组")
public class ManWorkCenterController extends BaseController {

    @Autowired
    private IManWorkCenterService manWorkCenterService;
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
     * 查询工作中心/班组列表
     */
    @PreAuthorize(hasPermi = "ems:man:work:center:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工作中心/班组列表", notes = "查询工作中心/班组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkCenter.class))
    public TableDataInfo list(@RequestBody ManWorkCenter manWorkCenter) {
        startPage(manWorkCenter);
        List<ManWorkCenter> list = manWorkCenterService.selectManWorkCenterList(manWorkCenter);
        return getDataTable(list);
    }

    @PreAuthorize(hasPermi = "ems:man:work:center:report")
    @PostMapping("/reportList")
    @ApiOperation(value = "查询工作中心/班组忙闲报表", notes = "查询工作中心/班组忙闲报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProcess.class))
    public AjaxResult reportList(@RequestBody ManWorkCenterReportRequest reportRequest) {
        List<ManManufactureOrderProcess> list = manWorkCenterService.selectManWorkCenterReportList(reportRequest);
        return AjaxResult.success(list);
    }


    /**
     * 导出工作中心/班组列表
     */
    @PreAuthorize(hasPermi = "ems:man:work:center:export")
    @Log(title = "工作中心/班组", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工作中心/班组列表", notes = "导出工作中心/班组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManWorkCenter manWorkCenter) throws IOException {
        List<ManWorkCenter> list = manWorkCenterService.selectManWorkCenterList(manWorkCenter);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManWorkCenter> util = new ExcelUtil<>(ManWorkCenter.class, dataMap);
        util.exportExcel(response, list, "工作中心(班组)");
    }

    /**
     * 获取工作中心/班组详细信息
     */
    @ApiOperation(value = "获取工作中心/班组详细信息", notes = "获取工作中心/班组详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkCenter.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:query")
    @PostMapping("/getInfo")
    public ManWorkCenter getInfo(Long workCenterSid) {
        return manWorkCenterService.selectManWorkCenterById(workCenterSid);
    }

    /**
     * 新增工作中心/班组
     */
    @ApiOperation(value = "新增工作中心/班组", notes = "新增工作中心/班组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:add")
    @Log(title = "工作中心/班组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManWorkCenter manWorkCenter) {
        int row = manWorkCenterService.insertManWorkCenter(manWorkCenter);
        return AjaxResult.success(manWorkCenter);
    }

    /**
     * 修改工作中心/班组
     */
    @ApiOperation(value = "修改工作中心/班组", notes = "修改工作中心/班组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:edit")
    @Log(title = "工作中心/班组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManWorkCenter manWorkCenter) {
        return toAjax(manWorkCenterService.updateManWorkCenter(manWorkCenter));
    }

    /**
     * 变更工作中心/班组
     */
    @ApiOperation(value = "变更工作中心/班组", notes = "变更工作中心/班组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:change")
    @Log(title = "工作中心/班组", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManWorkCenter manWorkCenter) {
        return toAjax(manWorkCenterService.change(manWorkCenter));
    }

    /**
     * 删除工作中心/班组
     */
    @ApiOperation(value = "删除工作中心/班组", notes = "删除工作中心/班组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:remove")
    @Log(title = "工作中心/班组", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> workCenterSids) {
        return toAjax(manWorkCenterService.deleteManWorkCenterByIds(workCenterSids));
    }

    /**
     * 确认工作中心/班组
     */
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:check")
    @Log(title = "工作中心/班组", businessType = BusinessType.CHECK)
    @PostMapping("/confirm")
    public AjaxResult comfirm(@RequestBody ManWorkCenterActionRequest action) {
        return toAjax(manWorkCenterService.confirm(action));
    }

    /**
     * 启用/停用 工作中心/班组
     */
    @ApiOperation(value = "启用/停用", notes = "启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:work:center:enbleordisable")
    @Log(title = "工作中心/班组", businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    public AjaxResult status(@RequestBody ManWorkCenterActionRequest action) {
        return toAjax(manWorkCenterService.status(action));
    }

    /**
     * 工作中心/班组档案
     */
    @ApiOperation(value = "工作中心/班组档案下拉列表", notes = "工作中心/班组档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkCenter.class))
    @Log(title = "工作中心/班组", businessType = BusinessType.UPDATE)
    @PostMapping("/getList")
    public AjaxResult getList() {
        return AjaxResult.success(manWorkCenterService.getList());
    }

    /**
     * 工作中心/班组下拉列表
     */
    @ApiOperation(value = "工作中心/班组下拉列表", notes = "工作中心/班组下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkCenter.class))
    @Log(title = "工作中心/班组", businessType = BusinessType.UPDATE)
    @PostMapping("/getWorkCenterList")
    public AjaxResult getWorkCenterList(@RequestBody ManWorkCenter manWorkCenter) {
        return AjaxResult.success(manWorkCenterService.getWorkCenterList(manWorkCenter));
    }

    /**
     * 获取工厂+部门下启用&确认班组
     */
    @ApiOperation(value = "获取工厂+部门下启用&确认班组", notes = "获取工厂+部门下启用&确认班组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWorkCenter.class))
    @Log(title = "工作中心/班组", businessType = BusinessType.UPDATE)
    @PostMapping("/getCoDeptList")
    public AjaxResult getCoDeptList(@RequestBody ManWorkCenter manWorkCenter) {
        return AjaxResult.success(manWorkCenterService.getCoDeptList(manWorkCenter));
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入-班组", notes = "导入-班组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public EmsResultEntity importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return manWorkCenterService.importDataPur(file);
    }

    @ApiOperation(value = "下载班组导入模板", notes = "下载班组导入模板")
    @PostMapping("/importTemplate")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_班组_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_班组_V1.0.xlsx", "UTF-8"));
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
