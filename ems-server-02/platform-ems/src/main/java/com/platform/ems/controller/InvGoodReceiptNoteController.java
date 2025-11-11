package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.InvGoodReceiptNote;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvReceiptNoteReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvGoodReceiptNoteExReponse;
import com.platform.ems.domain.dto.response.InvReceiptNoteReportResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IInvGoodReceiptNoteService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收货单Controller
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@RestController
@RequestMapping("/invGoodReceiptNote")
@Api(tags = "收货单")
public class InvGoodReceiptNoteController extends BaseController {

    @Autowired
    private IInvGoodReceiptNoteService invGoodReceiptNoteService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询收货单列表
     */
    @PreAuthorize(hasPermi = "ems:receipt:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询收货单列表", notes = "查询收货单列表")
    @Idempotent(interval = 3000,message = "请勿重复查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvGoodReceiptNote.class))
    public TableDataInfo list(@RequestBody InvGoodReceiptNote invGoodReceiptNote) {
        startPage(invGoodReceiptNote);
        List<InvGoodReceiptNote> list = invGoodReceiptNoteService.selectInvGoodReceiptNoteList(invGoodReceiptNote);
        return getDataTable(list);
    }
    /**
     * 查询收货单报表
     */
    @PostMapping("/report")
    @PreAuthorize(hasPermi = "ems::inv:Receipt:Note:report")
    @ApiOperation(value = "查询收货单报表", notes = "查询收货单报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvReceiptNoteReportResponse.class))
    public TableDataInfo report(@RequestBody InvReceiptNoteReportRequest request) {
        startPage(request);
        List<InvReceiptNoteReportResponse> list = invGoodReceiptNoteService.reportInvReceiptNote(request);
        return getDataTable(list);
    }

    @PreAuthorize(hasPermi = "ems:receipt:export")
    @Log(title = "收货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出收货单明细报表", notes = "导出收货单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvReceiptNoteReportRequest request) throws IOException {
        List<InvReceiptNoteReportResponse> list = invGoodReceiptNoteService.reportInvReceiptNote(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvReceiptNoteReportResponse> util = new ExcelUtil<>(InvReceiptNoteReportResponse.class, dataMap);
        util.exportExcel(response, list, "收货单明细报表");
    }


    /**
     * 物料需求测算报表 收货单
     */
    @ApiOperation(value = "物料需求测算报表-收货单", notes = "物料需求测算报表-收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:purchase:order:add")
    @Log(title = "收货单", businessType = BusinessType.INSERT)
    @PostMapping("/get")
    public AjaxResult getOrder(@RequestBody List<TecBomItemReport> orderList) {
        return AjaxResult.success(invGoodReceiptNoteService.getGoodReceiptNote(orderList));
    }
    /**
     * 导出收货单列表
     */
    @PreAuthorize(hasPermi = "ems:receipt:export")
    @Log(title = "收货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出收货单列表", notes = "导出收货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvGoodReceiptNote invGoodReceiptNote) throws IOException {
        List<InvGoodReceiptNote> list = invGoodReceiptNoteService.selectInvGoodReceiptNoteList(invGoodReceiptNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvGoodReceiptNoteExReponse> util = new ExcelUtil<>(InvGoodReceiptNoteExReponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvGoodReceiptNoteExReponse::new), "收货单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取收货单详细信息
     */
    @ApiOperation(value = "获取收货单详细信息", notes = "获取收货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvGoodReceiptNote.class))
    @PreAuthorize(hasPermi = "ems:receipt:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long noteSid) {
        if (noteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invGoodReceiptNoteService.selectInvGoodReceiptNoteById(noteSid));
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
        return AjaxResult.success(invGoodReceiptNoteService.processCheck(request));
    }
    /**
     * 复制收货单详细信息
     */
    @ApiOperation(value = "复制收货单详细信息", notes = "复制收货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvGoodReceiptNote.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long noteSid) {
        if (noteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invGoodReceiptNoteService.getCopy(noteSid));
    }

    /**
     * 新增收货单
     */
    @ApiOperation(value = "新增收货单", notes = "新增收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:receipt:add")
    @Log(title = "收货单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvGoodReceiptNote invGoodReceiptNote) {
        return toAjax(invGoodReceiptNoteService.insertInvGoodReceiptNote(invGoodReceiptNote));
    }

    /**
     * 修改收货单
     */
    @ApiOperation(value = "修改收货单", notes = "修改收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:receipt:edit")
    @Log(title = "收货单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvGoodReceiptNote invGoodReceiptNote) {
        return toAjax(invGoodReceiptNoteService.updateInvGoodReceiptNote(invGoodReceiptNote));
    }

    /**
     * 变更收货单
     */
    @ApiOperation(value = "变更收货单", notes = "变更收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:receipt:change")
    @Log(title = "收货单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid InvGoodReceiptNote invGoodReceiptNote) {
        return toAjax(invGoodReceiptNoteService.updateInvGoodReceiptNote(invGoodReceiptNote));
    }

    /**
     * 删除收货单
     */
    @ApiOperation(value = "删除收货单", notes = "删除收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:receipt:remove")
    @Log(title = "收货单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> goodReceiptNoteSids) {
        if (ArrayUtil.isEmpty(goodReceiptNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invGoodReceiptNoteService.deleteInvGoodReceiptNoteByIds(goodReceiptNoteSids));
    }

    /**
     * 关闭收货单
     */
    @ApiOperation(value = "关闭收货单", notes = "关闭收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:receipt:remove")
    @Log(title = "收货单", businessType = BusinessType.DELETE)
    @PostMapping("/close")
    public AjaxResult close(@RequestBody List<Long> goodReceiptNoteSids) {
        if (ArrayUtil.isEmpty(goodReceiptNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invGoodReceiptNoteService.close(goodReceiptNoteSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvGoodReceiptNote invGoodReceiptNote) {
        return AjaxResult.success(invGoodReceiptNoteService.changeStatus(invGoodReceiptNote));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:receipt:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvGoodReceiptNote invGoodReceiptNote) {
        invGoodReceiptNote.setConfirmDate(new Date());
        invGoodReceiptNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        invGoodReceiptNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(invGoodReceiptNoteService.check(invGoodReceiptNote));
    }

    /**
     * 导入收货单
     */
//    @PreAuthorize(hasPermi = "ems:receipt:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入收货单", notes = "导入收货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invGoodReceiptNoteService.importData(file);
    }

    /**
     * 上传收货单导入模板
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传收货单导入模板", notes = "上传收货单导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_收货单_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载收货单导入模板", notes = "下载收货单导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_收货单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_收货单_V0.1.xlsx", "UTF-8"));
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
}
