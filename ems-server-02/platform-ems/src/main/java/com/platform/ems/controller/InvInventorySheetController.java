package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvSheetExReponse;
import com.platform.ems.domain.dto.response.InvSheetExYpResponse;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.InvInventorySheet;
import com.platform.ems.domain.dto.request.InvInventorySheetReportRequest;
import com.platform.ems.domain.dto.response.InvInventorySheetReportResponse;
import com.platform.ems.service.IInvInventorySheetService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 盘点单Controller
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@RestController
@RequestMapping("/Inventory/sheet")
@Api(tags = "盘点单")
public class InvInventorySheetController extends BaseController {

    @Autowired
    private IInvInventorySheetService invInventorySheetService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";
    /**
     * 查询盘点单列表
     */
    @PreAuthorize(hasPermi = "ems:sheet:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询盘点单列表", notes = "查询盘点单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheet.class))
    public TableDataInfo list(@RequestBody InvInventorySheet invInventorySheet) {
        startPage(invInventorySheet);
        List<InvInventorySheet> list = invInventorySheetService.selectInvInventorySheetList(invInventorySheet);
        return getDataTable(list);
    }

    /**
     * 查询盘点单明细报表
     */
    @PreAuthorize(hasPermi = "ems::Inv:Inventory:Sheet:report")
    @PostMapping("/report")
    @ApiOperation(value = "查询盘点单明细报表", notes = "查询盘点单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheet.class))
    public TableDataInfo report(@RequestBody InvInventorySheetReportRequest request) {
        startPage(request);
        List<InvInventorySheetReportResponse> list = invInventorySheetService.reportInvInventorySheet(request);
        return getDataTable(list);
    }

    @PreAuthorize(hasPermi = "ems:sheet:export")
    @Log(title = "盘点单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出盘点单明细报表", notes = "导出盘点单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvInventorySheetReportRequest request) throws IOException {
        List<InvInventorySheetReportResponse> list = invInventorySheetService.reportInvInventorySheet(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvInventorySheetReportResponse> util = new ExcelUtil<>(InvInventorySheetReportResponse.class,dataMap);
        util.exportExcel(response, list, "盘点单明细报表");
    }

    /**
     * 导出盘点明细
     */
    @Log(title = "销售订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出盘点明细", notes = "导出盘点明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/item")
    public void exportItem(HttpServletResponse response, Long sid) throws IOException {
        invInventorySheetService.exportGood(response,sid);
    }
    /**
     * 通过仓库库位获取仓库信息
     */
    @PostMapping("/getLocation")
    @ApiOperation(value = "通过仓库库位获取仓库信息", notes = "通过仓库库位获取仓库信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheet.class))
    public AjaxResult getSheet(@RequestBody InvInventorySheet invInventorySheet) {
        return AjaxResult.success(invInventorySheetService.getInvInventorySheet(invInventorySheet));
    }

    /**
     * 导出盘点单列表
     */
    @PreAuthorize(hasPermi = "ems:sheet:export")
    @Log(title = "盘点单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出盘点单列表", notes = "导出盘点单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvInventorySheet invInventorySheet) throws IOException {
        if(ConstantsEms.PROFIT.equals(invInventorySheet.getMovementType())){
            List<InvInventorySheet> list = invInventorySheetService.selectInvInventorySheetList(invInventorySheet);
            Map<String,Object> dataMap=sysDictDataService.getDictDataList();
            ExcelUtil<InvSheetExReponse> util = new ExcelUtil<>(InvSheetExReponse.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list,InvSheetExReponse::new), "盘点单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        }else{
            List<InvInventorySheet> list = invInventorySheetService.selectInvInventorySheetList(invInventorySheet);
            Map<String,Object> dataMap=sysDictDataService.getDictDataList();
            ExcelUtil<InvSheetExYpResponse> util = new ExcelUtil<>(InvSheetExYpResponse.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvSheetExYpResponse::new), "样品盘点单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        }
    }

    /**
     * 获取盘点单详细信息
     */
    @ApiOperation(value = "获取盘点单详细信息", notes = "获取盘点单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheet.class))
    @PreAuthorize(hasPermi = "ems:sheet:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inventorySheetSid) {
        if (inventorySheetSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventorySheetService.selectInvInventorySheetById(inventorySheetSid));
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
        return AjaxResult.success(invInventorySheetService.processCheck(request));
    }
    /**
     * 复制盘点单详细信息
     */
    @ApiOperation(value = "复制盘点单详细信息", notes = "复制盘点单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheet.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long inventorySheetSid) {
        if (inventorySheetSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventorySheetService.getCopy(inventorySheetSid));
    }

    /**
     * 新增盘点单
     */
    @ApiOperation(value = "新增盘点单", notes = "新增盘点单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:sheet:add")
    @Log(title = "盘点单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvInventorySheet invInventorySheet){
        int row = invInventorySheetService.insertInvInventorySheet(invInventorySheet);
        return AjaxResult.success(invInventorySheet);
    }

    /**
     * 修改盘点单
     */
    @ApiOperation(value = "修改盘点单", notes = "修改盘点单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:sheet:edit")
    @Log(title = "盘点单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvInventorySheet invInventorySheet) {
        return toAjax(invInventorySheetService.updateInvInventorySheet(invInventorySheet));
    }

    /**
     * 删除盘点单
     */
    @ApiOperation(value = "删除盘点单", notes = "删除盘点单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:sheet:remove")
    @Log(title = "盘点单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] inventorySheetSids) {
        if (ArrayUtil.isEmpty( inventorySheetSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventorySheetService.deleteInvInventorySheetByIds(inventorySheetSids));
    }

    /**
     * 盘点单确认
     */
//    @PreAuthorize(hasPermi = "ems:model:pos:edit")
    @Log(title = "盘点单", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "盘点单确认", notes = "盘点单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody InvInventorySheet invInventorySheet) {
        return AjaxResult.success(invInventorySheetService.confirm(invInventorySheet));
    }

    @PostMapping("/handle")
    @ApiOperation(value = "盘点单修改处理状态", notes = "盘点单修改处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult handle(@RequestBody InvInventorySheet invInventorySheet) {
        return AjaxResult.success(invInventorySheetService.handle(invInventorySheet));
    }

    /**
     * 盘点单过账
     */
//    @PreAuthorize(hasPermi = "ems:model:pos:edit")
    @Log(title = "盘点单", businessType = BusinessType.UPDATE)
    @PostMapping("/post")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "盘点单过账", notes = "盘点单过账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult post(@RequestBody InvInventorySheet invInventorySheet) {
        return AjaxResult.success(invInventorySheetService.post(invInventorySheet));
    }

    /**
     * 导入-盘点
     */
//    @PreAuthorize(hasPermi = "ems:material:M:import")
    @PostMapping("/import")
    @ApiOperation(value = "新建导入-盘点", notes = "导入-盘点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataG(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invInventorySheetService.importDataInv(file);
    }

    @PostMapping("/import/quantity")
    @ApiOperation(value = "实盘量导入-盘点", notes = "导入-盘点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invInventorySheetService.importData(file);
    }

    /**
     * 盘点单变更
     */
    @PreAuthorize(hasPermi = "ems:model:pos:edit")
    @Log(title = "盘点单", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "盘点单变更", notes = "盘点单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid InvInventorySheet invInventorySheet) {
        return AjaxResult.success(invInventorySheetService.change(invInventorySheet));
    }

    @ApiOperation(value = "下载盘点导入模板", notes = "下载盘点导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_盘点单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_盘点单_V0.1.xlsx", "UTF-8"));
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
