package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsPrice;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.PurQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurQuoteBargainResponse;
import com.platform.ems.domain.dto.response.PurQuoteBargainExportResponse;
import com.platform.ems.domain.dto.response.export.PurQuoteBargainBaojExport;
import com.platform.ems.domain.dto.response.export.PurQuoteBargainHejExport;
import com.platform.ems.service.IPurQuoteBargainItemService;
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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.IPurQuoteBargainService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 报议价单主(价/报价/核价/议价)Controller
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@RestController
@RequestMapping("/quotation")
@Api(tags = "报议价单主(报价/核价/议价)")
public class PurQuoteBargainController extends BaseController {

    @Autowired
    private IPurQuoteBargainService purQuoteBargainService;
    @Autowired
    private IPurQuoteBargainItemService purQuoteBargainItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询采购报核议价查询  -----报表
     */
    @PostMapping("/report/new")
    @ApiOperation(value = "查询报议价单主(报价/核价/议价)明细报表", notes = "查询采购报核议价查询--新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurQuoteBargain.class))
    public TableDataInfo reportNew(@RequestBody PurQuoteBargainRequest request) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            List<PurQuoteBargainResponse> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null){
                request.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                startPage(request);
                list = purQuoteBargainService.getReport(request);
                return getDataTable(list);
            }else {
                return getDataTable(list);
            }
        }else {
            startPage(request);
            List<PurQuoteBargainResponse> list = purQuoteBargainService.getReport(request);
            return getDataTable(list);
        }
    }

    /**
     * 查询采购报核议价报表  -----查询页面
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询采购报核议价查询--新", notes = "查询采购报核议价查询--新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurQuoteBargainReportResponse.class))
    public TableDataInfo report(@RequestBody PurQuoteBargainReportResponse request) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            List<PurQuoteBargainReportResponse> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null){
                request.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                startPage(request);
                list = purQuoteBargainService.report(request);
                return getDataTable(list);
            }else {
                return getDataTable(list);
            }
        }else {
            startPage(request);
            List<PurQuoteBargainReportResponse> list = purQuoteBargainService.report(request);
            return getDataTable(list);
        }
    }

    /**
     * 导出报议价单主(报价/核价/议价)列表
     */
    @ApiOperation(value = "导出报议价单主(报价/核价/议价)列表", notes = "导出报议价单主(报价/核价/议价)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurQuoteBargainReportResponse request) throws IOException {
        List<PurQuoteBargainReportResponse> list = new ArrayList<>();
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null){
                request.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                list = purQuoteBargainService.report(request);
            }
        }else {
            list = purQuoteBargainService.report(request);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Object> dataMap = sysDictDataService.getDictDataList();
            // 采购报价单
            if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getStage())) {
                List<PurQuoteBargainBaojExport> exportList = BeanCopyUtils.copyListProperties(list, PurQuoteBargainBaojExport::new);
                ExcelUtil<PurQuoteBargainBaojExport> util = new ExcelUtil<>(PurQuoteBargainBaojExport.class, dataMap);
                util.exportExcel(response, exportList, "采购报价单");
            }
            // 采购核价单
            else if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(request.getStage())) {
                List<PurQuoteBargainHejExport> exportList = BeanCopyUtils.copyListProperties(list, PurQuoteBargainHejExport::new);
                ExcelUtil<PurQuoteBargainHejExport> util = new ExcelUtil<>(PurQuoteBargainHejExport.class, dataMap);
                util.exportExcel(response, exportList, "采购核价单");
            }
            // 采购议价单
            else if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getStage())) {
                List<PurQuoteBargainExportResponse> exportList = BeanCopyUtils.copyListProperties(list, PurQuoteBargainExportResponse::new);
                ExcelUtil<PurQuoteBargainExportResponse> util = new ExcelUtil<>(PurQuoteBargainExportResponse.class, dataMap);
                util.exportExcel(response, BeanCopyUtils.copyListProperties(exportList, PurQuoteBargainExportResponse::new), "采购议价单");
            }
        }
    }

    /**
     * 获取报议价单主(报价/核价/议价)详细信息
     */
    @ApiOperation(value = "获取报议价单主(报价/核价/议价)详细信息", notes = "获取报议价单主(报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurQuoteBargain.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long quoteBargainSid) {
        if (quoteBargainSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purQuoteBargainService.selectPurRequestQuotationById(quoteBargainSid));
    }

    /**
     * 获取报议价单明细(报价/核价/议价)详细信息
     */
    @ApiOperation(value = "获取报议价单明细(报价/核价/议价)详细信息", notes = "获取报议价单明细报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurQuoteBargainItem.class))
    @PostMapping("/item/getInfo")
    public AjaxResult getItemInfo(Long quoteBargainItemSid) {
        if (quoteBargainItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purQuoteBargainItemService.selectPurRequestQuotationItemById(quoteBargainItemSid));
    }

    /**
     * 编辑暂存报议价单明细(报价/核价/议价)详细信息：提交、流转
     */
    @ApiOperation(value = "编辑暂存报议价单明细(报价/核价/议价)详细信息", notes = "编辑暂存报议价单明细报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurQuoteBargainItem.class))
    @PostMapping("/item/edit")
    public AjaxResult updateItem(@RequestBody @Valid PurQuoteBargainItem purQuoteBargainItem) {
        return AjaxResult.success(purQuoteBargainItemService.updatePurRequestQuotationItem(purQuoteBargainItem));
    }

    /**
     * 新增报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "新增报议价单主(报价/核价/议价)", notes = "新增报议价单主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurQuoteBargain purQuoteBargain) {
        if (ConstantsEms.CHECK_STATUS.equals(purQuoteBargain.getHandleStatus())) {
            purQuoteBargain.setConfirmDate(new Date());
            purQuoteBargain.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return toAjax(purQuoteBargainService.insertPurRequestQuotation(purQuoteBargain));
    }

    /**
     * 修改报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "修改报议价单主(报价/核价/议价)", notes = "修改报议价单主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurQuoteBargain purQuoteBargain) {
        if (ConstantsEms.CHECK_STATUS.equals(purQuoteBargain.getHandleStatus())) {
            purQuoteBargain.setConfirmDate(new Date());
            purQuoteBargain.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return toAjax(purQuoteBargainService.updatePurRequestQuotation(purQuoteBargain));
    }

    /**
     * 删除报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "删除报议价单主(报价/核价/议价)", notes = "删除报议价单主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> requestQuotationSids) {
        if (ArrayUtil.isEmpty(requestQuotationSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purQuoteBargainService.deletePurRequestQuotationByIds(requestQuotationSids));
    }

    /**
     * 删除报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "删除明细行)", notes = "删除明细行")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete/item")
    public AjaxResult removeItem(@RequestBody List<Long> quoteBargainItemSidList) {
        if (ArrayUtil.isEmpty(quoteBargainItemSidList)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purQuoteBargainService.deleteItem(quoteBargainItemSidList));
    }

    /**
     * 明细提交，流转时校验
     * @author chenkw
     * request: quoteBargainItemSid
     */
    @ApiOperation(value = "明细提交时校验", notes = "明细提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkPrice")
    public AjaxResult checkPrice(@RequestBody PurQuoteBargainItem request) {
        if (request.getQuoteBargainItemSidList() == null || request.getQuoteBargainItemSidList().length == 0) {
            throw new CheckedException("参数缺失");
        }
        Long code = null;
        try {
            for (Long sid : request.getQuoteBargainItemSidList()) {
                PurQuoteBargainItem item = purQuoteBargainItemService.selectPurRequestQuotationItemById(sid);
                PurQuoteBargain bargain = purQuoteBargainService.selectPurRequestQuotationById(item.getQuoteBargainSid());
                code = item.getQuoteBargainCode();
                purQuoteBargainService.checkUnique(bargain);
                purQuoteBargainItemService.checkPrice(item);
                if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                    purQuoteBargainService.checkDateRange(bargain);
                }
            }
        }catch (Exception e){
            return AjaxResult.success(code + "中" + e.getMessage());
        }
        return AjaxResult.success(true);
    }

    /**
     * 报价提交到核价
     * 核价到议价
     * 议价审批
     * 审批驳回
     * @author chenkw
     */
    @ApiOperation(value = "提交/流转/审批", notes = "提交/流转/审批")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody PurQuoteBargainItem purQuoteBargainItem) {
        return toAjax(purQuoteBargainService.submit(purQuoteBargainItem));
    }

    @ApiOperation(value = "复制报价单/议价", notes = "复制报价/议价单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/copy")
    public AjaxResult copyInfo(@RequestBody PurQuoteBargainItem request) {
        if (request.getQuoteBargainItemSid() == null || StrUtil.isBlank(request.getStage())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purQuoteBargainService.copy(request));
    }

    /**
     * 导入采购议价
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购议价", notes = "导入采购议价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataM(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purQuoteBargainService.importDataPur(file));
    }

    @ApiOperation(value = "下载采购议价导入模板", notes = "下载下载采购议价导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_采购议价单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_采购议价单_V0.1.xlsx", "UTF-8"));
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
