package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPrice;
import com.platform.ems.domain.PurOutsourceQuoteBargain;
import com.platform.ems.domain.PurOutsourceQuoteBargainItem;
import com.platform.ems.domain.dto.request.PurOutsourceQuotationRequest;
import com.platform.ems.domain.dto.request.PurOutsourceQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainResponse;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainExportResponse;
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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import javax.validation.Valid;

import com.platform.ems.service.IPurOutsourceQuoteBargainService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 加工报议价单主(报价/核价/议价)Controller
 *
 * @author linhongwei
 * @date 2021-05-10
 */
@RestController
@RequestMapping("/outsource/quotation")
@Api(tags = "加工报议价单主(报价/核价/议价)")
public class PurOutsourceQuoteBargainController extends BaseController {

    @Autowired
    private IPurOutsourceQuoteBargainService purOutsourceRequestQuotationService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";


    /**
     * 查询加工报议价单主(报价/核价/议价)列表
     */
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询加工报议价单主(报价/核价/议价)列表", notes = "查询加工报议价单主(报价/核价/议价)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargain.class))
    public TableDataInfo list(@RequestBody PurOutsourceQuoteBargain purOutsourceQuoteBargain) {
        startPage(purOutsourceQuoteBargain);
        List<PurOutsourceQuoteBargain> list = purOutsourceRequestQuotationService.selectPurOutsourceRequestQuotationList(purOutsourceQuoteBargain);
        return getDataTable(list);
    }

    /**
     * 查询加工报议价单主(报价/核价/议价)明细报表
     */
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:report")
    @PostMapping("/report")
    @ApiOperation(value = "查询加工报议价单", notes = "查询加工报议价单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargain.class))
    public TableDataInfo report(@RequestBody PurOutsourceQuotationRequest request) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            List<PurOutsourceQuoteBargainReportResponse> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null){
                request.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                startPage(request);
                list = purOutsourceRequestQuotationService.report(request);
                return getDataTable(list);
            }else {
                return getDataTable(list);
            }
        }else {
            startPage(request);
            List<PurOutsourceQuoteBargainReportResponse> list = purOutsourceRequestQuotationService.report(request);
            return getDataTable(list);
        }
    }

    /**
     * 查询加工报议价单主(报价/核价/议价)明细报表
     */
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:report:new")
    @PostMapping("/report/new")
    @ApiOperation(value = "查询加工报议价单主(报价/核价/议价)明细报表", notes = "查询加工报议价单主(报价/核价/议价)明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargain.class))
    public TableDataInfo reportNew(@RequestBody PurOutsourceQuoteBargainRequest request) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            List<PurOutsourceQuoteBargainResponse> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null){
                request.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                startPage(request);
                list = purOutsourceRequestQuotationService.getReport(request);
                return getDataTable(list);
            }else {
                return getDataTable(list);
            }
        }else {
            startPage(request);
            List<PurOutsourceQuoteBargainResponse> list = purOutsourceRequestQuotationService.getReport(request);
            return getDataTable(list);
        }
    }

    /**
     * 导出加工报议价单主(报价/核价/议价)列表
     */
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:export")
    @Log(title = "加工报议价单主(报价/核价/议价)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出加工报议价单主(报价/核价/议价)列表", notes = "导出加工报议价单主(报价/核价/议价)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOutsourceQuotationRequest request) throws IOException {
        List<PurOutsourceQuoteBargainReportResponse> list = purOutsourceRequestQuotationService.report(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourceQuoteBargainExportResponse> util = new ExcelUtil<>(PurOutsourceQuoteBargainExportResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurOutsourceQuoteBargainExportResponse::new), "加工议价单明细");
    }

    /**
     * 获取加工报议价单主(报价/核价/议价)详细信息
     */
    @ApiOperation(value = "获取加工报议价单主(报价/核价/议价)详细信息", notes = "获取加工报议价单主(报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargain.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long outsourceQuoteBargainSid) {
        if (outsourceQuoteBargainSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceRequestQuotationService.selectPurOutsourceRequestQuotationById(outsourceQuoteBargainSid));
    }

    /**
     * 获取加工报议价单明细(报价/核价/议价)详细信息
     */
    @ApiOperation(value = "获取加工报议价单明细(报价/核价/议价)详细信息", notes = "获取加工报议价单明细(报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargainItem.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:item:query")
    @PostMapping("/item/getInfo")
    public AjaxResult getItemInfo(Long outsourceQuoteBargainItemSid) {
        if (outsourceQuoteBargainItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceRequestQuotationService.selectPurOutsourceRequestQuotationByItemId(outsourceQuoteBargainItemSid));
    }

    /**
     * 明细提交，流转时校验
     * @author chenkw
     * request: outsourceQuoteBargainItemSid
     */
    @ApiOperation(value = "明细提交时校验", notes = "明细提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkPrice")
    public AjaxResult checkPrice(@RequestBody PurOutsourceQuoteBargainItem request) {
        if (request.getOutsourceQuoteBargainItemSidList() == null || request.getOutsourceQuoteBargainItemSidList().length == 0) {
            throw new CheckedException("参数缺失");
        }
        Long code = null;
        try {
            for (Long sid : request.getOutsourceQuoteBargainItemSidList()) {
                PurOutsourceQuoteBargainItem item = purOutsourceRequestQuotationService.selectPurOutsourceRequestQuotationByItemId(sid);
                PurOutsourceQuoteBargain bargain = purOutsourceRequestQuotationService.selectPurOutsourceRequestQuotationById(item.getOutsourceQuoteBargainSid());
                code = item.getOutsourceQuoteBargainCode();
                purOutsourceRequestQuotationService.checkUnique(bargain);
                purOutsourceRequestQuotationService.checkPrice(item);
                if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                    purOutsourceRequestQuotationService.checkDateRange(bargain);
                }
            }
        }catch (Exception e){
            return AjaxResult.success(code + e.getMessage());
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
    @ApiOperation(value = "报价提交到核价", notes = "报价提交到核价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "报价单提交", businessType = BusinessType.SUBMIT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem) {
        return toAjax(purOutsourceRequestQuotationService.submit(purOutsourceQuoteBargainItem));
    }

    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:item:copy")
    @ApiOperation(value = "复制加工报价单/议价", notes = "复制加工报价/议价单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/copy")
    public AjaxResult copyInfo(@RequestBody PurOutsourceQuoteBargainItem request) {
        if (request.getOutsourceQuoteBargainItemSid() == null || StrUtil.isBlank(request.getStage())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceRequestQuotationService.copy(request));
    }

    /**
     * 编辑暂存加工报议价单明细(报价/核价/议价)详细信息：提交、流转
     */
    @ApiOperation(value = "编辑暂存加工报议价单明细(报价/核价/议价)详细信息", notes = "编辑暂存加工报议价单明细报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceQuoteBargainItem.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:item:edit")
    @PostMapping("/item/edit")
    public AjaxResult updateItem(@RequestBody @Valid PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem) {
        return AjaxResult.success(purOutsourceRequestQuotationService.updatePurOutsourceRequestQuotationItem(purOutsourceQuoteBargainItem));
    }

    /**
     * 新增加工报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "新增加工报议价单主(报价/核价/议价)", notes = "新增加工报议价单主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:add")
    @Log(title = "加工报议价单主(报价/核价/议价)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurOutsourceQuoteBargain purOutsourceQuoteBargain) {
        return toAjax(purOutsourceRequestQuotationService.insertPurOutsourceRequestQuotation(purOutsourceQuoteBargain));
    }

    /**
     * 修改加工报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "修改加工报议价单主(报价/核价/议价)", notes = "修改加工报议价单主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:edit")
    @Log(title = "加工报议价单主(报价/核价/议价)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurOutsourceQuoteBargain purOutsourceQuoteBargain) {
        return toAjax(purOutsourceRequestQuotationService.updatePurOutsourceRequestQuotation(purOutsourceQuoteBargain));
    }

    /**
     * 删除加工报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "删除加工报议价单主(报价/核价/议价)", notes = "删除加工报议价单主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:remove")
    @Log(title = "加工报议价单主(报价/核价/议价)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> outsourceRequestQuotationSids) {
        if (ArrayUtil.isEmpty(outsourceRequestQuotationSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourceRequestQuotationService.deletePurOutsourceRequestQuotationByIds(outsourceRequestQuotationSids));
    }

    /**
     * 删除加工报议价单主(报价/核价/议价)
     */
    @ApiOperation(value = "删除明细行)", notes = "删除明细行")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:item:remove")
    @Log(title = "加工报议价单主(报价/核价/议价)", businessType = BusinessType.DELETE)
    @PostMapping("/delete/item")
    public AjaxResult removeItem(@RequestBody List<Long> outsourceRequestQuotationItemSidList) {
        if (ArrayUtil.isEmpty(outsourceRequestQuotationItemSidList)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourceRequestQuotationService.deleteItem(outsourceRequestQuotationItemSidList));
    }

    /**
     * 导入加工采购议价
     */
    @PreAuthorize(hasPermi = "ems:outsource:quote:bargain:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入加工采购议价", notes = "导入加工采购议价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataM(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purOutsourceRequestQuotationService.importDataPur(file));
    }

    @ApiOperation(value = "下载加工采购议价导入模板", notes = "下载加工采购议价导入模板")
    @PostMapping("/importTemplate")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_加工议价单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_加工议价单_V0.1", "UTF-8"));
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
