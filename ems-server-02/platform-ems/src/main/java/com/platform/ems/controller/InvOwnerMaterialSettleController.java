package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.InvOwnerMaterialSettle;
import com.platform.ems.domain.dto.request.InvOwnerMaterialSettleRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvOwnerMaterialSettleReportResponse;
import com.platform.ems.domain.dto.response.InvOwnerMaterialSettleResponse;
import com.platform.ems.service.IInvOwnerMaterialSettleService;
import com.platform.system.service.ISysDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
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
 * 甲供料结算单Controller
 *
 * @author c
 * @date 2021-09-13
 */
@RestController
@RequestMapping("/owner/material/settle")
@Api(tags = "甲供料结算单")
public class InvOwnerMaterialSettleController extends BaseController {

    @Autowired
    private IInvOwnerMaterialSettleService invOwnerMaterialSettleService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询甲供料结算单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询甲供料结算单列表", notes = "查询甲供料结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvOwnerMaterialSettle.class))
    public TableDataInfo list(@RequestBody InvOwnerMaterialSettle invOwnerMaterialSettle) {
        startPage(invOwnerMaterialSettle);
        List<InvOwnerMaterialSettle> list = invOwnerMaterialSettleService.selectInvOwnerMaterialSettleList(invOwnerMaterialSettle);
        return getDataTable(list);
    }

    /**
     * 查询甲供料结算单列表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询甲供料结算单明细报表", notes = "查询甲供料结算单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvOwnerMaterialSettleReportResponse.class))
    public TableDataInfo list(@RequestBody InvOwnerMaterialSettleRequest invOwnerMaterialSettle) {
        startPage(invOwnerMaterialSettle);
        List<InvOwnerMaterialSettleReportResponse> list = invOwnerMaterialSettleService.getReport(invOwnerMaterialSettle);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出甲供料结算单明细报表", notes = "导出甲供料结算单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportR(HttpServletResponse response, InvOwnerMaterialSettleRequest invOwnerMaterialSettle) throws IOException {
        List<InvOwnerMaterialSettleReportResponse> list = invOwnerMaterialSettleService.getReport(invOwnerMaterialSettle);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvOwnerMaterialSettleReportResponse> util = new ExcelUtil<InvOwnerMaterialSettleReportResponse>(InvOwnerMaterialSettleReportResponse.class,dataMap);
        util.exportExcel(response, list, "甲供料结算单明细报表");
    }
    /**
     * 导出甲供料结算单列表
     */
    @ApiOperation(value = "导出甲供料结算单列表", notes = "导出甲供料结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvOwnerMaterialSettle invOwnerMaterialSettle) throws IOException {
        List<InvOwnerMaterialSettle> list = invOwnerMaterialSettleService.selectInvOwnerMaterialSettleList(invOwnerMaterialSettle);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvOwnerMaterialSettleResponse> util = new ExcelUtil<>(InvOwnerMaterialSettleResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvOwnerMaterialSettleResponse::new), "甲供料结算单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取甲供料结算单详细信息
     */
    @ApiOperation(value = "获取甲供料结算单详细信息", notes = "获取甲供料结算单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvOwnerMaterialSettle.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long settleSid) {
        if (settleSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invOwnerMaterialSettleService.selectInvOwnerMaterialSettleById(settleSid));
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
        return AjaxResult.success(invOwnerMaterialSettleService.processCheck(request));
    }
    /**
     * 新增甲供料结算单
     */
    @ApiOperation(value = "新增甲供料结算单", notes = "新增甲供料结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvOwnerMaterialSettle invOwnerMaterialSettle) {
        int row = invOwnerMaterialSettleService.insertInvOwnerMaterialSettle(invOwnerMaterialSettle);
        return AjaxResult.success(invOwnerMaterialSettle);
    }

    /**
     * 修改甲供料结算单
     */
    @ApiOperation(value = "修改甲供料结算单", notes = "修改甲供料结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid InvOwnerMaterialSettle invOwnerMaterialSettle) {
        return toAjax(invOwnerMaterialSettleService.updateInvOwnerMaterialSettle(invOwnerMaterialSettle));
    }

    /**
     * 变更甲供料结算单
     */
    @ApiOperation(value = "变更甲供料结算单", notes = "变更甲供料结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid InvOwnerMaterialSettle invOwnerMaterialSettle) {
        return toAjax(invOwnerMaterialSettleService.changeInvOwnerMaterialSettle(invOwnerMaterialSettle));
    }

    /**
     * 删除甲供料结算单
     */
    @ApiOperation(value = "删除甲供料结算单", notes = "删除甲供料结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  settleSids) {
        if(CollectionUtils.isEmpty( settleSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invOwnerMaterialSettleService.deleteInvOwnerMaterialSettleByIds(settleSids));
    }

    /**
     * 作废甲供料结算单
     */
    @ApiOperation(value = "作废", notes = "作废")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/disuse")
    public AjaxResult disuse(@RequestBody List<Long>  settleSids) {
        if(CollectionUtils.isEmpty( settleSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invOwnerMaterialSettleService.disuse(settleSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody InvOwnerMaterialSettle invOwnerMaterialSettle) {
        return toAjax(invOwnerMaterialSettleService.check(invOwnerMaterialSettle));
    }

    /**
     * 导入甲供料结算单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入甲供料结算单", notes = "导入甲供料结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invOwnerMaterialSettleService.importDataInv(file);
    }

    @ApiOperation(value = "下载甲供料结算单导入模板", notes = "下载甲供料结算单导入模板")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_甲供料结算单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_甲供料结算单_V0.1.xlsx", "UTF-8"));
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
