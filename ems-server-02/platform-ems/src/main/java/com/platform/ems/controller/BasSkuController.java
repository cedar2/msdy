package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.export.BasCmReport;
import com.platform.ems.domain.dto.response.export.BasSkuReport;
import com.platform.ems.domain.dto.response.export.BasYsReport;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import com.platform.ems.domain.BasSku;
import com.platform.ems.service.IBasSkuService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKU档案Controller
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@RestController
@RequestMapping("/sku")
@Api(tags = "SKU档案")
public class BasSkuController extends BaseController {

    @Autowired
    private IBasSkuService basSkuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询SKU档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询SKU档案列表", notes = "查询SKU档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSku.class))
    public TableDataInfo list(@RequestBody BasSku basSku) {
        startPage(basSku);
        List<BasSku> list = basSkuService.selectBasSkuList(basSku);
        return getDataTable(list);
    }

    /**
     * 导出SKU档案列表
     */
    @ApiOperation(value = "导出SKU档案列表", notes = "导出SKU档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasSku basSku) throws IOException {
        List<BasSku> list = basSkuService.selectBasSkuList(basSku);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        if (ConstantsEms.SKUTYP_YS.equals(basSku.getSkuType())) {
            ExcelUtil<BasYsReport> util = new ExcelUtil<>(BasYsReport.class, dataMap);
            List<BasYsReport> basYsReports = BeanCopyUtils.copyListProperties(list, BasYsReport::new);
            util.exportExcel(response, basYsReports, "颜色");
        } else if (ConstantsEms.SKUTYP_CM.equals(basSku.getSkuType())) {
            ExcelUtil<BasCmReport> util = new ExcelUtil<>(BasCmReport.class, dataMap);
            List<BasCmReport> basCmReports = BeanCopyUtils.copyListProperties(list, BasCmReport::new);
            util.exportExcel(response, basCmReports, "尺码");
        } else {
            ExcelUtil<BasSkuReport> util = new ExcelUtil<>(BasSkuReport.class, dataMap);
            List<BasSkuReport> basSkuReports = BeanCopyUtils.copyListProperties(list, BasSkuReport::new);
            util.exportExcel(response, basSkuReports, "SKU");
        }
    }

    /**
     * 获取SKU档案详细信息
     */
    @ApiOperation(value = "获取SKU档案详细信息", notes = "获取SKU档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSku.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long skuSid) {
        return AjaxResult.success(basSkuService.selectBasSkuById(skuSid));
    }

    /**
     * 新增SKU档案
     */
    @ApiOperation(value = "新增SKU档案", notes = "新增SKU档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "SKU档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasSku basSku) {
        int row = basSkuService.insertBasSku(basSku);
        return AjaxResult.success(basSku);
    }

    /**
     * 修改SKU档案
     */
    @ApiOperation(value = "修改SKU档案", notes = "修改SKU档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "SKU档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasSku basSku) {
        return toAjax(basSkuService.updateBasSku(basSku));
    }

    /**
     * 删除SKU档案
     */
    @ApiOperation(value = "删除SKU档案", notes = "删除SKU档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "SKU档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> skuSids) {
        return toAjax(basSkuService.deleteBasSkuByIds(skuSids));
    }

    @ApiOperation(value = "获取sku组下拉列表", notes = "获取sku组下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSku.class))
    @PostMapping("/getList")
    public AjaxResult getList(String skuType) {
        if (StrUtil.isEmpty(skuType)) {
            throw new CheckedException("参数错误");
        }
        return AjaxResult.success(basSkuService.getList(skuType));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasSku basSku) {
        int row = 0;
        try {
            row = basSkuService.changeStatus(basSku);
        } catch (BaseException e) {
            if (e.getModule() != null && EmsResultEntity.WARN_TAG.equals(e.getModule())) {
                List<CommonErrMsgResponse> responseList = new ArrayList<>();
                responseList.add(new CommonErrMsgResponse().setMsg(e.getDefaultMessage()));
                return AjaxResult.success(EmsResultEntity.warning(responseList));
            }
            else {
                throw e;
            }
        }
        return AjaxResult.success(row);
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasSku basSku) {
        basSku.setConfirmDate(new Date());
        basSku.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        basSku.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(basSkuService.check(basSku));
    }

    /**
     * 导入sku档案
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入sku档案", notes = "导入sku档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = basSkuService.importData(file);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    @ApiOperation(value = "下载sku档案导入模板", notes = "下载sku档案导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_SKU_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_SKU_V0.1.xlsx", "UTF-8"));
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
