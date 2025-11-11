package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.config.MinioConfig;
import com.platform.api.service.RemoteFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.TecBomPosition;
import com.platform.ems.service.ITecBomPositionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * BOM部位档案Controller
 *
 * @author zhuangyz
 * @date 2022-07-07
 */
@RestController
@RequestMapping("/bom/position")
@Api(tags = "BOM部位档案")
public class TecBomPositionController extends BaseController {

    @Autowired
    private ITecBomPositionService tecBomPositionService;
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
     * 查询BOM部位档案列表
     */
    @PreAuthorize(hasPermi = "ems:bom:position:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询BOM部位档案列表", notes = "查询BOM部位档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomPosition.class))
    public TableDataInfo list(@RequestBody TecBomPosition tecBomPosition) {
        startPage(tecBomPosition);
        List<TecBomPosition> list = tecBomPositionService.selectTecBomPositionList(tecBomPosition);
        return getDataTable(list);
    }

    /**
     * 查询BOM部位档案下拉列表
     */
//    @PreAuthorize(hasPermi = "ems:bom:position:getList")
    @PostMapping("/getList")
    @ApiOperation(value = "查询BOM部位档案下拉列表", notes = "查询BOM部位档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomPosition.class))
    public AjaxResult getList(@RequestBody TecBomPosition tecBomPosition) {
        startPage(tecBomPosition);
        List<TecBomPosition> list = tecBomPositionService.selectTecBomPositionList(tecBomPosition);
        return AjaxResult.success(list);
    }



    /**
     * 导出BOM部位档案列表
     */
    @PreAuthorize(hasPermi = "ems:bom:position:export")
    @Log(title = "BOM部位档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出BOM部位档案列表", notes = "导出BOM部位档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecBomPosition tecBomPosition) throws IOException {
        List<TecBomPosition> list = tecBomPositionService.selectTecBomPositionList(tecBomPosition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecBomPosition> util = new ExcelUtil<TecBomPosition>(TecBomPosition.class, dataMap);
        util.exportExcel(response, list, "BOM部位档案");
    }



    /**
     * 获取BOM部位档案详细信息
     */
    @ApiOperation(value = "获取BOM部位档案详细信息", notes = "获取BOM部位档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomPosition.class))
    @PreAuthorize(hasPermi = "ems:bom:position:query")
    @GetMapping("/getInfo/{bomPositionSid}")
    public AjaxResult getInfo(@PathVariable String bomPositionSid) {
        if (ObjectUtil.isEmpty(bomPositionSid)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecBomPositionService.selectTecBomPositionById(Long.valueOf(bomPositionSid)));
    }

    /**
     * 新增BOM部位档案
     */
    @ApiOperation(value = "新增BOM部位档案", notes = "新增BOM部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:position:add")
    @Log(title = "BOM部位档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecBomPosition tecBomPosition) {
        return toAjax(tecBomPositionService.insertTecBomPosition(tecBomPosition));
    }

    /**
     * 修改BOM部位档案
     */
    @ApiOperation(value = "修改BOM部位档案", notes = "修改BOM部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:position:edit")
    @Log(title = "BOM部位档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TecBomPosition tecBomPosition) {
        return toAjax(tecBomPositionService.updateTecBomPosition(tecBomPosition));
    }

    /**
     * 变更BOM部位档案
     */
    @ApiOperation(value = "变更BOM部位档案", notes = "变更BOM部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:position:change")
    @Log(title = "BOM部位档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody TecBomPosition tecBomPosition) {
        return toAjax(tecBomPositionService.changeTecBomPosition(tecBomPosition));
    }

    /**
     * 删除BOM部位档案
     */
    @ApiOperation(value = "删除BOM部位档案", notes = "删除BOM部位档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:position:remove")
    @Log(title = "BOM部位档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> tecBomPositionSids) {
        if (CollectionUtils.isEmpty(tecBomPositionSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecBomPositionService.deleteTecBomPositionByIds(tecBomPositionSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM部位档案", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:bom:position:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecBomPosition tecBomPosition) {
        return AjaxResult.success(tecBomPositionService.changeStatus(tecBomPosition));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:bom:position:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM部位档案", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecBomPosition tecBomPosition) {
        tecBomPosition.setConfirmDate(new Date());
        tecBomPosition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        tecBomPosition.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(tecBomPositionService.check(tecBomPosition));
    }

    /**
     * 导入-BOM部位
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入-BOM部位", notes = "导入-BOM部位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(tecBomPositionService.importDataPur(file));
    }

    @ApiOperation(value = "下载bom部位导入模板", notes = "下载bom部位导入模板")
    @PostMapping("/importTemplate")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_BOM部位_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_BOM部位_V1.0.xlsx", "UTF-8"));
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
