package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.api.service.RemoteFileService;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.InvInventoryTransfer;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvInventoryTransferRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvInventoryTransferResponse;
import com.platform.ems.domain.dto.response.InvTransferExReponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IInvInventoryTransferService;
import com.platform.framework.web.domain.server.SysFile;
import com.platform.system.service.ISysDictDataService;
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
 * 调拨单Controller
 *
 * @author linhongwei
 * @date 2021-06-04
 */
@RestController
@RequestMapping("/invInventoryTransfer")
@Api(tags = "调拨单")
public class InvInventoryTransferController extends BaseController {

    @Autowired
    private IInvInventoryTransferService invInventoryTransferService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;

    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询调拨单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询调拨单列表", notes = "查询调拨单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    public TableDataInfo list(@RequestBody InvInventoryTransfer invInventoryTransfer) {
        startPage(invInventoryTransfer);
        List<InvInventoryTransfer> list = invInventoryTransferService.selectInvInventoryTransferList(invInventoryTransfer);
        return getDataTable(list);
    }

    /**
     * 查询调拨单明细报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询调拨单明细报表", notes = "查询调拨单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransferResponse.class))
    public TableDataInfo report(@RequestBody InvInventoryTransferRequest request) {
        startPage(request);
        List<InvInventoryTransferResponse> list = invInventoryTransferService.report(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出调拨单明细报表", notes = "导出调拨单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvInventoryTransferRequest request) throws IOException {
        List<InvInventoryTransferResponse> list = invInventoryTransferService.report(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryTransferResponse> util = new ExcelUtil<>(InvInventoryTransferResponse.class,dataMap);
        util.exportExcel(response, list, "调拨单明细报表");
    }

    /**
     * 导出调拨单列表
     */
    @ApiOperation(value = "导出调拨单列表", notes = "导出调拨单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvInventoryTransfer invInventoryTransfer) throws IOException {
        List<InvInventoryTransfer> list = invInventoryTransferService.selectInvInventoryTransferList(invInventoryTransfer);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvTransferExReponse> util = new ExcelUtil<>(InvTransferExReponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvTransferExReponse::new), "调拨单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    @ApiOperation(value = "明细报表-生成库存预留", notes = "明细报表-生成库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    @PostMapping("/create/reserve")
    public AjaxResult freeReserve(@RequestBody  Long[] inventoryTransferSids) {
        return AjaxResult.success(invInventoryTransferService.create(inventoryTransferSids));
    }

    @ApiOperation(value = "明细报表-释放库存预留", notes = "明细报表-释放库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    @PostMapping("/free/reserve")
    public AjaxResult free(@RequestBody  List<Long> inventoryTransferSids) {
        return AjaxResult.success(invInventoryTransferService.reportFreeInv(inventoryTransferSids));
    }

    @ApiOperation(value = "获取调拨单详细信息", notes = "获取调拨单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inventoryTransferSid) {
        if (inventoryTransferSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryTransferService.selectInvInventoryTransferById(inventoryTransferSid));
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
        return AjaxResult.success(invInventoryTransferService.processCheck(request));
    }

    /**
     * 复制调拨单详细信息
     */
    @ApiOperation(value = "复制调拨单详细信息", notes = "复制调拨单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryTransfer.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long inventoryTransferSid) {
        if(inventoryTransferSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryTransferService.getCopy(inventoryTransferSid));
    }

    /**
     * 新增调拨单
     */
    @ApiOperation(value = "新增调拨单", notes = "新增调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvInventoryTransfer invInventoryTransfer) {
        int row = invInventoryTransferService.insertInvInventoryTransfer(invInventoryTransfer);
        return AjaxResult.success(invInventoryTransfer);
    }

    @ApiOperation(value = "物料需求测算报表-调拨单", notes = "物料需求测算报表-调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get")
    public AjaxResult getOrder(@RequestBody List<TecBomItemReport> orderList) {
        return AjaxResult.success(invInventoryTransferService.getGoodIssueNote(orderList));
    }

    /**
     * 修改调拨单
     */
    @ApiOperation(value = "修改调拨单", notes = "修改调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvInventoryTransfer invInventoryTransfer) {
        return toAjax(invInventoryTransferService.updateInvInventoryTransfer(invInventoryTransfer));
    }

    /**
     * 变更调拨单
     */
    @ApiOperation(value = "变更调拨单", notes = "变更调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid InvInventoryTransfer invInventoryTransfer) {
        return toAjax(invInventoryTransferService.changeInvInventoryTransfer(invInventoryTransfer));
    }

    /**
     * 删除调拨单
     */
    @ApiOperation(value = "删除调拨单", notes = "删除调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  inventoryTransferSids) {
        if(ArrayUtil.isEmpty( inventoryTransferSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryTransferService.deleteInvInventoryTransferByIds(inventoryTransferSids));
    }

    /**
     * 关闭调拨单
     */
    @ApiOperation(value = "关闭调拨单", notes = "关闭调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/close")
    public AjaxResult close(@RequestBody List<Long>  inventoryTransferSids) {
        if(ArrayUtil.isEmpty( inventoryTransferSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryTransferService.close(inventoryTransferSids));
    }
    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvInventoryTransfer invInventoryTransfer) {
        return AjaxResult.success(invInventoryTransferService.changeStatus(invInventoryTransfer));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvInventoryTransfer invInventoryTransfer) {
        invInventoryTransfer.setConfirmDate(new Date());
        invInventoryTransfer.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        invInventoryTransfer.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(invInventoryTransferService.check(invInventoryTransfer));
    }

    /**
     * 导入调拨单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入调拨单", notes = "导入调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invInventoryTransferService.importData(file);
    }

    /**
     * 上传调拨单导入模板
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传调拨单导入模板", notes = "上传调拨单导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_调拨单_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载调拨单导入模板", notes = "下载调拨单导入模板")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_调拨单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_调拨单_V0.1.xlsx", "UTF-8"));
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
