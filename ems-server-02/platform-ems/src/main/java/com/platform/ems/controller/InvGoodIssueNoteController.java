package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
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
import com.platform.ems.domain.InvGoodIssueNote;
import com.platform.ems.domain.InvInventoryTransfer;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvIssueNoteReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvGoodIssueNoteExReponse;
import com.platform.ems.domain.dto.response.InvIssueNoteReportResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IInvGoodIssueNoteService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
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
 * 发货单Controller
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@RestController
@RequestMapping("/invGoodIssueNote")
@Api(tags = "发货单")
public class InvGoodIssueNoteController extends BaseController {

    @Autowired
    private IInvGoodIssueNoteService invGoodIssueNoteService;
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
     * 查询发货单列表
     */
    @PreAuthorize(hasPermi = "ems:note:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询发货单列表", notes = "查询发货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvGoodIssueNote.class))
    public TableDataInfo list(@RequestBody InvGoodIssueNote invGoodIssueNote) {
        startPage(invGoodIssueNote);
        List<InvGoodIssueNote> list = invGoodIssueNoteService.selectInvGoodIssueNoteList(invGoodIssueNote);
        return getDataTable(list);
    }

    /**
     * 查询发货单明细报表
     */
    @PreAuthorize(hasPermi = "ems::inv:IssueNote:report")
    @PostMapping("/report")
    @ApiOperation(value = "查询发货单明细报表", notes = "查询发货单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvIssueNoteReportResponse.class))
    public TableDataInfo report(@RequestBody InvIssueNoteReportRequest request) {
        startPage(request);
        List<InvIssueNoteReportResponse> list = invGoodIssueNoteService.report(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "明细报表-生成库存预留", notes = "明细报表-生成库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/create/reserve")
    public AjaxResult freeReserve(@RequestBody List<Long> noteSids) {
        return AjaxResult.success(invGoodIssueNoteService.create(noteSids));
    }

    @ApiOperation(value = "明细报表-释放库存预留", notes = "明细报表-释放库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    @PostMapping("/free/reserve")
    public AjaxResult free(@RequestBody  List<Long> noteSids) {
        return AjaxResult.success(invGoodIssueNoteService.reportFreeInv(noteSids));
    }

    /**
     * 导出发货单列表
     */
    @PreAuthorize(hasPermi = "ems:note:export")
    @Log(title = "发货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出发货单明细报表", notes = "导出发货单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvIssueNoteReportRequest request) throws IOException {
        List<InvIssueNoteReportResponse> list = invGoodIssueNoteService.report(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvIssueNoteReportResponse> util = new ExcelUtil<>(InvIssueNoteReportResponse.class, dataMap);
        util.exportExcel(response,list, "发货单明细报表");
    }

    /**
     * 导出发货单列表
     */
    @PreAuthorize(hasPermi = "ems:note:export")
    @Log(title = "发货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出发货单列表", notes = "导出发货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvGoodIssueNote invGoodIssueNote) throws IOException {
        List<InvGoodIssueNote> list = invGoodIssueNoteService.selectInvGoodIssueNoteList(invGoodIssueNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvGoodIssueNoteExReponse> util = new ExcelUtil<>(InvGoodIssueNoteExReponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,InvGoodIssueNoteExReponse::new), "发货单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取发货单详细信息
     */
    @ApiOperation(value = "获取发货单详细信息", notes = "获取发货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvGoodIssueNote.class))
    @PreAuthorize(hasPermi = "ems:note:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long noteSid) {
        if (noteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invGoodIssueNoteService.selectInvGoodIssueNoteById(noteSid));
    }

    @ApiOperation(value = "物料需求测算报表-发货单", notes = "物料需求测算报表-发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:purchase:order:add")
    @Log(title = "发货单", businessType = BusinessType.INSERT)
    @PostMapping("/get")
    public AjaxResult getOrder(@RequestBody List<TecBomItemReport> orderList) {
        return AjaxResult.success(invGoodIssueNoteService.getGoodIssueNote(orderList));
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
        return AjaxResult.success(invGoodIssueNoteService.processCheck(request));
    }
    /**
     * 复制发货单详细信息
     */
    @ApiOperation(value = "复制发货单详细信息", notes = "复制发货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvGoodIssueNote.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long noteSid) {
        if (noteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invGoodIssueNoteService.getCopy(noteSid));
    }

    /**
     * 新增发货单
     */
    @ApiOperation(value = "新增发货单", notes = "新增发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:note:add")
    @Log(title = "发货单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvGoodIssueNote invGoodIssueNote) {
        return toAjax(invGoodIssueNoteService.insertInvGoodIssueNote(invGoodIssueNote));
    }

    /**
     * 修改发货单
     */
    @ApiOperation(value = "修改发货单", notes = "修改发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:note:edit")
    @Log(title = "发货单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvGoodIssueNote invGoodIssueNote) {
        return toAjax(invGoodIssueNoteService.updateInvGoodIssueNote(invGoodIssueNote));
    }

    /**
     * 变更发货单
     */
    @ApiOperation(value = "变更发货单", notes = "变更发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:note:change")
    @Log(title = "发货单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid InvGoodIssueNote invGoodIssueNote) {
        return toAjax(invGoodIssueNoteService.updateInvGoodIssueNote(invGoodIssueNote));
    }

    /**
     * 删除发货单
     */
    @ApiOperation(value = "删除发货单", notes = "删除发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:note:remove")
    @Log(title = "发货单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> goodIssueNoteSids) {
        if (ArrayUtil.isEmpty(goodIssueNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invGoodIssueNoteService.deleteInvGoodIssueNoteByIds(goodIssueNoteSids));
    }
    /**
     * 关闭发货单
     */
    @ApiOperation(value = "关闭发货单", notes = "关闭发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:note:remove")
    @Log(title = "发货单", businessType = BusinessType.DELETE)
    @PostMapping("/close")
    public AjaxResult close(@RequestBody List<Long> goodIssueNoteSids) {
        if (ArrayUtil.isEmpty(goodIssueNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(invGoodIssueNoteService.close(goodIssueNoteSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发货单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:note:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvGoodIssueNote invGoodIssueNote) {
        return AjaxResult.success(invGoodIssueNoteService.changeStatus(invGoodIssueNote));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:note:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发货单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvGoodIssueNote invGoodIssueNote) {
        invGoodIssueNote.setConfirmDate(new Date());
        invGoodIssueNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        invGoodIssueNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(invGoodIssueNoteService.check(invGoodIssueNote));
    }

    /**
     * 导入发货单
     */
//    @PreAuthorize(hasPermi = "ems:note:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入发货单", notes = "导入发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invGoodIssueNoteService.importData(file);
    }

    /**
     * 上传发货单导入模板
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传发货单导入模板", notes = "上传发货单导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_发货单_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载发货单导入模板", notes = "下载发货单导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_发货单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_发货单_V0.1.xlsx", "UTF-8"));
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
