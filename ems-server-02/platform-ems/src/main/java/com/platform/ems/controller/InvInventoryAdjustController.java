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
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.dto.request.InvCrossColorReportRequest;
import com.platform.ems.domain.dto.request.InvInventoryAdjustReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvCrossColorReportResponse;
import com.platform.ems.domain.dto.response.InvInvAdjustCrossExReponse;
import com.platform.ems.domain.dto.response.InvInvAdjustExReponse;
import com.platform.ems.domain.dto.response.InvInventoryAdjustReportResponse;
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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.domain.InvInventoryAdjust;
import com.platform.ems.service.IInvInventoryAdjustService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 库存调整单Controller
 *
 * @author linhongwei
 * @date 2021-04-19
 */
@RestController
@RequestMapping("/inventory/adjust")
@Api(tags = "库存调整单")
public class InvInventoryAdjustController extends BaseController {

    @Autowired
    private IInvInventoryAdjustService invInventoryAdjustService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private final String documnet="KCTZ";
    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;
    private static final String FILLE_PATH = "/template";
    /**
     * 查询库存调整单列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询库存调整单列表", notes = "查询库存调整单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryAdjust.class))
    public TableDataInfo list(@RequestBody InvInventoryAdjust invInventoryAdjust) {
        startPage(invInventoryAdjust);
        List<InvInventoryAdjust> list = invInventoryAdjustService.selectInvInventoryAdjustList(invInventoryAdjust);
        return getDataTable(list);
    }

    /**
     * 查询库存调整单明细报表
     */
    @PreAuthorize(hasPermi = "ems::Inv:Inventory:Adjust:report")
    @PostMapping("/report")
    @ApiOperation(value = "查询库存调整单明细报表", notes = "查询库存调整单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryAdjustReportResponse.class))
    public TableDataInfo report(@RequestBody InvInventoryAdjustReportRequest request) {
        startPage(request);
        List<InvInventoryAdjustReportResponse> list = invInventoryAdjustService.reportInvInventoryAdjust(request);
        return getDataTable(list);
    }

    /**
     * 导入-库存调整
     */
//    @PreAuthorize(hasPermi = "ems:material:M:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入-库存调整", notes = "导入-库存调整")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataG(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }

        return invInventoryAdjustService.importDataInv(file);
    }
    //    @PreAuthorize(hasPermi = "ems::bas:material:import")
    @ApiOperation(value = "下载库存调整导入模板", notes = "下载库存调整导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_库存调整_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_库存调整_V0.1.xlsx", "UTF-8"));
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
    /**
     * 导出库存调整单列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:export")
    @Log(title = "库存调整单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库存调整单明细报表", notes = "导出库存调整单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportK(HttpServletResponse response, InvInventoryAdjustReportRequest request) throws IOException {
        List<InvInventoryAdjustReportResponse> list = invInventoryAdjustService.reportInvInventoryAdjust(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryAdjustReportResponse> util = new ExcelUtil<>(InvInventoryAdjustReportResponse.class,dataMap);
        util.exportExcel(response, list, "库存调整单明细报表");

    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:sales:order:query")
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody OrderErrRequest request) {
        if (request == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryAdjustService.processCheck(request));
    }
    /**
     * 查询串色串码明细报表
     */
    @PreAuthorize(hasPermi = "ems::Inv:cross:color:report")
    @PostMapping("/cross/report")
    @ApiOperation(value = "查询串色串码明细报表", notes = "查询串色串码明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvCrossColorReportResponse.class))
    public TableDataInfo reportCrossColor(@RequestBody InvCrossColorReportRequest request) {
        startPage(request);
        List<InvCrossColorReportResponse> list = invInventoryAdjustService.reportCrossColor(request);
        return getDataTable(list);
    }

    /**
     * 导出库存调整单列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:export")
    @Log(title = "串色串码明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出串色串码明细报表", notes = "导出串色串码明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report/color")
    public void exportR(HttpServletResponse response, InvCrossColorReportRequest request) throws IOException {
        List<InvCrossColorReportResponse> list = invInventoryAdjustService.reportCrossColor(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvCrossColorReportResponse> util = new ExcelUtil<>(InvCrossColorReportResponse.class,dataMap);
        util.exportExcel(response, list, "串色串码明细报表"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));

    }

    /**
     * 导出库存调整单列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:export")
    @Log(title = "库存调整单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库存调整单列表", notes = "导出库存调整单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvInventoryAdjust invInventoryAdjust) throws IOException {
        List<InvInventoryAdjust> list = invInventoryAdjustService.selectInvInventoryAdjustList(invInventoryAdjust);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        String documentType = invInventoryAdjust.getDocumentType();
        if(documnet.equals(documentType)){
            ExcelUtil<InvInvAdjustExReponse> util = new ExcelUtil<>(InvInvAdjustExReponse.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInvAdjustExReponse::new), "库存调整单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        }else{
            ExcelUtil<InvInvAdjustCrossExReponse> util = new ExcelUtil<>(InvInvAdjustCrossExReponse.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInvAdjustCrossExReponse::new), "串色串码单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        }
    }

    /**
     * 获取库存调整单详细信息
     */
    @ApiOperation(value = "获取库存调整单详细信息", notes = "获取库存调整单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryAdjust.class))
    @PreAuthorize(hasPermi = "ems:adjust:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inventoryAdjustSid) {
        if (inventoryAdjustSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryAdjustService.selectInvInventoryAdjustById(inventoryAdjustSid));
    }

    /**
     * 复制库存调整单详细信息
     */
    @ApiOperation(value = "复制库存调整单详细信息", notes = "复制库存调整单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryAdjust.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long inventoryAdjustSid) {
        if (inventoryAdjustSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryAdjustService.getCopy(inventoryAdjustSid));
    }

    /**
     * 新增库存调整单
     */
    @ApiOperation(value = "新增库存调整单", notes = "新增库存调整单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:add")
    @Log(title = "库存调整单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvInventoryAdjust invInventoryAdjust) {
        return toAjax(invInventoryAdjustService.insertInvInventoryAdjust(invInventoryAdjust));
    }

    /**
     * 修改库存调整单
     */
    @ApiOperation(value = "修改库存调整单", notes = "修改库存调整单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:edit")
    @Log(title = "库存调整单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvInventoryAdjust invInventoryAdjust) {
        return toAjax(invInventoryAdjustService.updateInvInventoryAdjust(invInventoryAdjust));
    }

    /**
     * 删除库存调整单
     */
    @ApiOperation(value = "删除库存调整单", notes = "删除库存调整单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:remove")
    @Log(title = "库存调整单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] inventoryAdjustSids) {
        if (ArrayUtil.isEmpty( inventoryAdjustSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryAdjustService.deleteInvInventoryAdjustByIds(inventoryAdjustSids));
    }

    /**
     * 库存调整单确认
     */
    @PreAuthorize(hasPermi = "ems:adjust:check")
    @Log(title = "库存调整单", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "库存调整单确认", notes = "库存调整单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult confirm(@RequestBody InvInventoryAdjust invInventoryAdjust) {
        return AjaxResult.success(invInventoryAdjustService.confirm(invInventoryAdjust));
    }

    /**
     * 库存调整单变更
     */
    @PreAuthorize(hasPermi = "ems:adjust:change")
    @Log(title = "库存调整单", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "库存调整单变更", notes = "库存调整单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid InvInventoryAdjust invInventoryAdjust) {
        return AjaxResult.success(invInventoryAdjustService.change(invInventoryAdjust));
    }
}
