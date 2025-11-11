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

import com.platform.api.service.RemoteFileService;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.InvInventoryTransfer;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvInvQuisitionResponse;
import com.platform.framework.web.domain.server.SysFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.InvMaterialRequisition;
import com.platform.ems.domain.dto.request.InvMaterialRequisitionReportRequest;
import com.platform.ems.domain.dto.response.InvMaterialRequisitionReportResponse;
import com.platform.ems.service.IInvMaterialRequisitionService;
import com.platform.system.service.ISysDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 领退料单Controller
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@RestController
@RequestMapping("/requisition")
@Api(tags = "领退料单")
public class InvMaterialRequisitionController extends BaseController {

    @Autowired
    private IInvMaterialRequisitionService invMaterialRequisitionService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询领退料单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询领退料单列表", notes = "查询领退料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvMaterialRequisition.class))
    public TableDataInfo list(@RequestBody InvMaterialRequisition invMaterialRequisition) {
        startPage(invMaterialRequisition);
        List<InvMaterialRequisition> list = invMaterialRequisitionService.selectInvMaterialRequisitionList(invMaterialRequisition);
        return getDataTable(list);
    }

    /**
     * 查询领退料单明细报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询领退料单明细报表", notes = "查询领退料单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvMaterialRequisitionReportResponse.class))
    public TableDataInfo report(@RequestBody InvMaterialRequisitionReportRequest request) {
        startPage(request);
        List<InvMaterialRequisitionReportResponse> list = invMaterialRequisitionService.reportInvMaterialRequisition(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出领退料单明细报表", notes = "导出领退料单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvMaterialRequisitionReportRequest request) throws IOException {
        List<InvMaterialRequisitionReportResponse> list = invMaterialRequisitionService.reportInvMaterialRequisition(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvMaterialRequisitionReportResponse> util = new ExcelUtil<>(InvMaterialRequisitionReportResponse.class, dataMap);
        util.exportExcel(response, list, "领退料单明细报表");
    }
    /**
     * 导出领退料单列表
     */
    @ApiOperation(value = "导出领退料单列表", notes = "导出领退料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvMaterialRequisition invMaterialRequisition) throws IOException {
        List<InvMaterialRequisition> list = invMaterialRequisitionService.selectInvMaterialRequisitionList(invMaterialRequisition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInvQuisitionResponse> util = new ExcelUtil<>(InvInvQuisitionResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInvQuisitionResponse::new), "领退料单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 物料需求测算报表 领料单
     */
    @ApiOperation(value = "物料需求测算报表-领料单", notes = "物料需求测算报表-领料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get")
    public AjaxResult getOrder(@RequestBody List<TecBomItemReport> orderList) {
        return AjaxResult.success(invMaterialRequisitionService.getMaterialRequisition(orderList));
    }
    /**
     * 获取领退料单详细信息
     */
    @ApiOperation(value = "获取领退料单详细信息", notes = "获取领退料单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvMaterialRequisition.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialRequisitionSid) {
        if (materialRequisitionSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invMaterialRequisitionService.selectInvMaterialRequisitionById(materialRequisitionSid));
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody OrderErrRequest request) {
        if (request == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invMaterialRequisitionService.processCheck(request));
    }

    @ApiOperation(value = "明细报表-生成库存预留", notes = "明细报表-生成库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/create/reserve")
    public AjaxResult freeReserve(@RequestBody List<Long> inventoryTransferSids) {
        return AjaxResult.success(invMaterialRequisitionService.create(inventoryTransferSids));
    }

    @ApiOperation(value = "明细报表-释放库存预留", notes = "明细报表-释放库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    @PostMapping("/free/reserve")
    public AjaxResult free(@RequestBody  List<Long> inventoryTransferSids) {
        return AjaxResult.success(invMaterialRequisitionService.reportFreeInv(inventoryTransferSids));
    }

    /**
     * 复制领退料单详细信息
     */
    @ApiOperation(value = "复制领退料单详细信息", notes = "复制领退料单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvMaterialRequisition.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long materialRequisitionSid) {
        if (materialRequisitionSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invMaterialRequisitionService.getCopy(materialRequisitionSid));
    }

    /**
     * 新增领退料单
     */
    @ApiOperation(value = "新增领退料单", notes = "新增领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvMaterialRequisition invMaterialRequisition) {
        int row = invMaterialRequisitionService.insertInvMaterialRequisition(invMaterialRequisition);
        return AjaxResult.success(invMaterialRequisition);
    }

    /**
     * 修改领退料单
     */
    @ApiOperation(value = "修改领退料单", notes = "修改领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvMaterialRequisition invMaterialRequisition) {
        return toAjax(invMaterialRequisitionService.updateInvMaterialRequisition(invMaterialRequisition));
    }

    /**
     * 删除领退料单
     */
    @ApiOperation(value = "删除领退料单", notes = "删除领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] materialRequisitionSids) {
        if (ArrayUtil.isEmpty(materialRequisitionSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invMaterialRequisitionService.deleteInvMaterialRequisitionByIds(materialRequisitionSids));
    }
    /**
     * 删除领退料单
     */
    @ApiOperation(value = "关闭领退料单", notes = "关闭领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/close")
    public AjaxResult close(@RequestBody Long[] materialRequisitionSids) {
        if (ArrayUtil.isEmpty(materialRequisitionSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invMaterialRequisitionService.close(materialRequisitionSids));
    }

    /**
     * 服务销售验收单确认
     */
    @PostMapping("/confirm")
    @ApiOperation(value = "服务销售验收单确认", notes = "服务销售验收单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody InvMaterialRequisition invMaterialRequisition) {
        return AjaxResult.success(invMaterialRequisitionService.confirm(invMaterialRequisition));
    }

    /**
     * 服务销售验收单变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "服务销售验收单变更", notes = "服务销售验收单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid InvMaterialRequisition invMaterialRequisition) {
        return AjaxResult.success(invMaterialRequisitionService.change(invMaterialRequisition));
    }

    /**
     * 导入领退料单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入领退料单", notes = "导入领退料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invMaterialRequisitionService.importData(file);
    }

    /**
     * 上传领退料单导入模板
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传领退料单导入模板", notes = "上传领退料单导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_领退料单_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载领退料单导入模板", notes = "下载领退料单导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_领退料单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_领退料单_V0.1.xlsx", "UTF-8"));
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

    @ApiOperation(value = "生成PDF文件", notes = "生成PDF文件")
    @PostMapping("/generatePDF")
    public AjaxResult generatePDF(@RequestBody InvMaterialRequisition invMaterialRequisition){
        return invMaterialRequisitionService.generatePDF(invMaterialRequisition);
    }
}
