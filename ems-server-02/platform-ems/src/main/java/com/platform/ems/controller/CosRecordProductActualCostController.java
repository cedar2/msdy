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
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.CosRecordProductActualCost;
import com.platform.ems.service.ICosRecordProductActualCostService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品实际成本台账表Controller
 *
 * @author chenkw
 * @date 2023-04-27
 */
@RestController
@RequestMapping("/cos/record/product/actual/cost")
@Api(tags = "商品实际成本台账表")
public class CosRecordProductActualCostController extends BaseController {

    @Autowired
    private ICosRecordProductActualCostService cosRecordProductActualCostService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询商品实际成本台账表列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品实际成本台账表列表", notes = "查询商品实际成本台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosRecordProductActualCost.class))
    public TableDataInfo list(@RequestBody CosRecordProductActualCost actualCost) {
        startPage(actualCost);
        List<CosRecordProductActualCost> list = cosRecordProductActualCostService.selectCosRecordProductActualCostList(actualCost);
        return getDataTable(list);
    }

    /**
     * 导出商品实际成本台账表列表
     */
    @ApiOperation(value = "导出商品实际成本台账表列表", notes = "导出商品实际成本台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosRecordProductActualCost actualCost) throws IOException {
        List<CosRecordProductActualCost> list = cosRecordProductActualCostService.selectCosRecordProductActualCostList(actualCost);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<CosRecordProductActualCost> util = new ExcelUtil<>(CosRecordProductActualCost.class, dataMap);
        util.exportExcel(response, list, "商品实际成本台账表");
    }

    /**
     * 获取商品实际成本台账表详细信息
     */
    @ApiOperation(value = "获取商品实际成本台账表详细信息", notes = "获取商品实际成本台账表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosRecordProductActualCost.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordCostSid) {
        if (recordCostSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosRecordProductActualCostService.selectCosRecordProductActualCostById(recordCostSid));
    }

    /**
     * 新增商品实际成本台账表
     */
    @ApiOperation(value = "新增商品实际成本台账表", notes = "新增商品实际成本台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid CosRecordProductActualCost actualCost) {
        return AjaxResult.success(cosRecordProductActualCostService.insertCosRecordProductActualCost(actualCost));
    }

    /**
     * 变更商品实际成本台账表
     */
    @ApiOperation(value = "变更商品实际成本台账表", notes = "变更商品实际成本台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid CosRecordProductActualCost actualCost) {
        return toAjax(cosRecordProductActualCostService.changeCosRecordProductActualCost(actualCost));
    }

    /**
     * 删除商品实际成本台账表
     */
    @ApiOperation(value = "删除商品实际成本台账表", notes = "删除商品实际成本台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品实际成本台账表", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> recordCostSids) {
        if (CollectionUtils.isEmpty(recordCostSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(cosRecordProductActualCostService.deleteCosRecordProductActualCostByIds(recordCostSids));
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importShix(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(cosRecordProductActualCostService.importActualCost(file));
    }

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/import/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/" + "协服SCM_导入模板_商品实际成本台账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("协服SCM_导入模板_商品实际成本台账_V1.0.xlsx", "UTF-8"));
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
