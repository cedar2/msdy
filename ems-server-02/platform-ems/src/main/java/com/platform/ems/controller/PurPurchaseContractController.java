package com.platform.ems.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.ContractTemplateAttach;
import com.platform.ems.domain.dto.response.form.PurPurchaseContractFormResponse;
import com.platform.ems.mapper.PurPurchaseContractAttachmentMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.CommonUtil;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static java.util.stream.Collectors.toList;

/**
 * 采购合同信息Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/contract")
@Api(tags = "采购合同信息")
public class PurPurchaseContractController extends BaseController {

    @Autowired
    private IPurPurchaseContractService purPurchaseContractService;
    @Autowired
    private IPurPurchaseContractPayMethodService purPurchaseContractPayMethodService;
    @Autowired
    private IPurPurchaseContractAttachmentService purPurchaseContractAttachmentService;
    @Autowired
    private PurPurchaseContractAttachmentMapper purPurchaseContractAttachmentMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "PURCHASE_CONTRACT_LOCK";

    private static final String FILLE_PATH = "/template";

    /**
     * 查询采购合同信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购合同信息列表", notes = "查询采购合同信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    public TableDataInfo list(@RequestBody PurPurchaseContract purPurchaseContract) {
        startPage(purPurchaseContract);
        List<PurPurchaseContract> list = purPurchaseContractService.selectPurPurchaseContractList(purPurchaseContract);
        return getDataTable(list);
    }

    /**
     * 导出采购合同信息列表
     */
    @Log(title = "采购合同信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购合同信息列表", notes = "导出采购合同信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPurchaseContract purPurchaseContract) throws IOException {
        List<PurPurchaseContract> list = purPurchaseContractService.selectPurPurchaseContractList(purPurchaseContract);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchaseContract> util = new ExcelUtil<>(PurPurchaseContract.class,dataMap);
        util.exportExcel(response, list, "采购合同信息");
    }

    /**
     * 导入采购合同信息
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购合同信息", notes = "导入采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = purPurchaseContractService.importData(file);
        if (response instanceof Collection){
            return AjaxResult.error("导入错误", response);
        }
        else {
            return AjaxResult.success(response);
        }
    }

    @ApiOperation(value = "下载采购合同信息导入模板", notes = "下载采购合同信息导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        OutputStream out =null;
        String fileName = FILLE_PATH + "/采购合同导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("采购合同导入模板.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out  = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(out!=null){
                out.close();
            }
        }
    }

    /**
     * 获取采购合同信息详细信息
     */
    @ApiOperation(value = "获取采购合同信息详细信息", notes = "获取采购合同信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseContractSid) {
        if (purchaseContractSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseContractService.selectPurPurchaseContractById(purchaseContractSid));
    }

    /**
     * 按“商品编码+合同交期”汇总“订单明细”页签的数据
     */
    @ApiOperation(value = "订单明细合计", notes = "订单明细合计")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderItem.class))
    @PostMapping("/order/group")
    public AjaxResult orderGroup(@RequestBody PurPurchaseContract purPurchaseContract) {
        return AjaxResult.success(purPurchaseContractService.groupPurchaseOrderItemList(purPurchaseContract));
    }

    /**
     * 复制采购合同信息详细信息
     */
    @ApiOperation(value = "复制采购合同信息详细信息", notes = "复制采购合同信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long purchaseContractSid) {
        if (purchaseContractSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseContractService.copyPurPurchaseContractById(purchaseContractSid));
    }

    /**
     * 新增/提交采购合同前的校验
     */
    @PostMapping("/check/code")
    public AjaxResult checkCode(@RequestBody PurPurchaseContract purPurchaseContract) {
        if (StrUtil.isBlank(purPurchaseContract.getPurchaseContractCode())){
            throw new BaseException("采购合同号不能为空");
        }
        try {
            purPurchaseContractService.checkCode(purPurchaseContract);
        }catch (CustomException e){
            return AjaxResult.success(e.getMessage(),false);
        }
        return AjaxResult.success(true);
    }

    /**
     * 变更页面提交销售合同前的校验支付方式
     */
    @PostMapping("/check/pay")
    public AjaxResult checkPay(@RequestBody PurPurchaseContract purPurchaseContract) {
        String msg = "";
        String err = "";
        if (StrUtil.isBlank(purPurchaseContract.getContractSigner())){
            err = err + "合同签约人,";
        }
        if (purPurchaseContract.getContractSignDate() == null){
            err = err + "合同签约日期,";
        }
        if (StrUtil.isNotBlank(err) && err.endsWith(",")){
            err = err.substring(0, err.length() - 1);
            err = err + "不能为空";
        }
        if (ConstantsEms.CONTRACT_TYPE_BC.equals(purPurchaseContract.getContractType()) && purPurchaseContract.getOriginalPurchaseContractCode() == null){
            if (StrUtil.isNotBlank(err)){
                err = err + "，且补充协议的原合同号为必填";
            }
            else {
                err = "补充协议的原合同号为必填";
            }
        }
        if (StrUtil.isNotBlank(err)){
            err = "提交时，" + err + "，请填写后再操作！";
            return AjaxResult.error(err);
        }
        if (CollectionUtils.isNotEmpty(purPurchaseContract.getPayMethodListYusf())){
            msg = msg + purPurchaseContractPayMethodService.submitVerify(purPurchaseContract.getPayMethodListYusf(), ConstantsFinance.ACCOUNT_CAT_YSFK);
        }
        if (CollectionUtils.isNotEmpty(purPurchaseContract.getPayMethodListZq())){
            msg = msg + purPurchaseContractPayMethodService.submitVerify(purPurchaseContract.getPayMethodListZq(), ConstantsFinance.ACCOUNT_CAT_ZQK);
        }
        if (CollectionUtils.isNotEmpty(purPurchaseContract.getPayMethodListWq())){
            msg = msg + purPurchaseContractPayMethodService.submitVerify(purPurchaseContract.getPayMethodListWq(), ConstantsFinance.ACCOUNT_CAT_WK);
        }
        try {
            if (StrUtil.isNotBlank(msg) && msg.endsWith(",")) {
                msg = msg.substring(0, msg.length() - 1);
                msg = msg + "支付方式的明细和不为100%，是否确认提交？";
                throw new CustomException(msg);
            }
        }catch (CustomException e){
            return AjaxResult.success(e.getMessage(),false);
        }
        return AjaxResult.success(true);
    }

    /**
     * 新增采购合同信息
     */
    @ApiOperation(value = "新增采购合同信息", notes = "新增采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurPurchaseContract purPurchaseContract) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.isLocked()) {
            throw new CustomException("请勿重复操作");
        }
        lock.lock(15L, TimeUnit.SECONDS);
        Long contractSid = null;
        try {
            contractSid = purPurchaseContractService.insertPurPurchaseContract(purPurchaseContract);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return AjaxResult.success("操作成功",contractSid.toString());
    }

    /**
     * 修改采购合同信息
     */
    @ApiOperation(value = "修改采购合同信息", notes = "修改采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PurPurchaseContract purPurchaseContract) {
        return toAjax(purPurchaseContractService.updatePurPurchaseContract(purPurchaseContract));
    }

    /**
     * 变更采购合同信息
     */
    @ApiOperation(value = "变更采购合同信息", notes = "变更采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PurPurchaseContract purPurchaseContract) {
        int row = purPurchaseContractService.changePurPurchaseContract(purPurchaseContract);
        if (row == 100) {
            return AjaxResult.success("请按需更新后续相关数据！", 1);
        }
        return toAjax(row);
    }

    /**
     * 删除采购合同信息
     */
    @ApiOperation(value = "删除采购合同信息", notes = "删除采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> purchaseContractSids) {
        if (CollectionUtils.isEmpty( purchaseContractSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseContractService.deletePurPurchaseContractByIds(purchaseContractSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurPurchaseContract purPurchaseContract) {
        return toAjax(purPurchaseContractService.check(purPurchaseContract));
    }

    /**
     * 采购合同下拉框列表
     */
    @PostMapping("/getPurPurchaseContractList")
    @ApiOperation(value = "采购合同下拉框列表", notes = "采购合同下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    public List<PurPurchaseContract> getPurPurchaseContractList() {
        return purPurchaseContractService.getPurPurchaseContractList();
    }

    /**
     * 采购合同下拉框列表（带参数）
     */
    @PostMapping("/getPurchaseContractList")
    @ApiOperation(value = "采购合同下拉框列表（带参数）", notes = "采购合同下拉框列表（带参数）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    public List<PurPurchaseContract> getPurchaseContractList(@RequestBody PurPurchaseContract purPurchaseContract) {
        return purPurchaseContractService.getPurchaseContractList(purPurchaseContract);
    }

    /**
     * 原合同号下拉框接口
     */
    @PostMapping("/getOriginalContractList")
    @ApiOperation(value = "原合同号下拉框接口", notes = "原合同号下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    public AjaxResult getList(@RequestBody PurPurchaseContract purPurchaseContract) {
        return AjaxResult.success(purPurchaseContractService.getOriginalContractList(purPurchaseContract));
    }

    /**
     * 作废-采购合同信息
     */
    @ApiOperation(value = "作废销售合同信息", notes = "作废采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(@RequestBody PurPurchaseContract purPurchaseContract) {
        if (purPurchaseContract.getPurchaseContractSid() == null) {
            throw new BaseException("参数缺失");
        }
        if (StrUtil.isBlank(purPurchaseContract.getCancelRemark())){
            throw new BaseException("请填写作废说明");
        }
        return toAjax(purPurchaseContractService.cancellationPurPurchaseContractById(purPurchaseContract));
    }

    /**
     * 结案-采购合同信息
     */
    @ApiOperation(value = "结案采购合同信息", notes = "结案采购合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.HANDLE)
    @PostMapping("/closing")
    public AjaxResult closing(Long purchaseContractSid) {
        if (purchaseContractSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(purPurchaseContractService.closingPurPurchaseContractById(purchaseContractSid));
    }

    /**
     * 纸质合同签收
     */
    @ApiOperation(value = "纸质合同签收", notes = "纸质合同签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同信息", businessType = BusinessType.HANDLE)
    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody PurPurchaseContract purPurchaseContract) {
        if (CollectionUtils.isEmpty(purPurchaseContract.getPurchaseContractSids()) || StrUtil.isBlank(purPurchaseContract.getSignInStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(purPurchaseContractService.signPurPurchaseContractById(purPurchaseContract));
    }

    /**
     * 查询页面 上传附件前的校验
     */
    @ApiOperation(value = "查询页面-上传附件前的校验", notes = "查询页面-上传附件前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody PurPurchaseContractAttachment purPurchaseContractAttachment) {
        return purPurchaseContractAttachmentService.check(purPurchaseContractAttachment);
    }

    @ApiOperation(value = "新增采购合同查询页面上传附件", notes = "新增采购合同查询页面上传附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购合同 - 上传附件", businessType = BusinessType.INSERT)
    @PostMapping("/addAttach")
    public AjaxResult addAttachment(@RequestBody @Valid PurPurchaseContractAttachment purPurchaseContractAttachment) {
        return AjaxResult.success(purPurchaseContractAttachmentService.insertPurPurchaseContractAttachment(purPurchaseContractAttachment));
    }

    /**
     * 设置即将到期提醒天数
     */
    @ApiOperation(value = "设置即将到期提醒天数", notes = "设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setToexpireDays")
    public AjaxResult setToexpireDays(@RequestBody PurPurchaseContract purPurchaseContract) {
        return toAjax(purPurchaseContractService.setToexpireDays(purPurchaseContract));
    }

    /**
     * 查询采购合同统计报表
     */
    @PostMapping("/count")
    @ApiOperation(value = "查询采购合同统计报表", notes = "查询采购合同统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContractFormResponse.class))
    public TableDataInfo count(@RequestBody PurPurchaseContract purPurchaseContract) {
        startPage(purPurchaseContract);
        List<PurPurchaseContractFormResponse> list = purPurchaseContractService.getCountForm(purPurchaseContract);
        TableDataInfo tableDataInfo = getDataTable(list);
        if (CollectionUtils.isNotEmpty(list)){
            list = list.stream().sorted(Comparator.comparing(PurPurchaseContractFormResponse::getYear, Comparator.reverseOrder())
                    .thenComparing(PurPurchaseContractFormResponse::getVendorShortName, Collator.getInstance(Locale.CHINA))
                    .thenComparing(PurPurchaseContractFormResponse::getContractType)).collect(toList());
            tableDataInfo.setRows(list);
        }
        return tableDataInfo;
    }

    /**
     * 查询采购合同统计报表明细
     */
    @PostMapping("/count/item")
    @ApiOperation(value = "查询采购合同统计报表明细", notes = "查询采购合同统计报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContractFormResponse.class))
    public TableDataInfo countItem(@RequestBody PurPurchaseContract purPurchaseContract) {
        startPage(purPurchaseContract);
        List<PurPurchaseContractFormResponse> list = purPurchaseContractService.getCountFormItem(purPurchaseContract);
        TableDataInfo tableDataInfo = getDataTable(list);
        if (CollectionUtils.isNotEmpty(list)){
            list = list.stream().sorted(Comparator.comparing(PurPurchaseContractFormResponse::getCompanyShortName, Collator.getInstance(Locale.CHINA))
                    .thenComparing(PurPurchaseContractFormResponse::getProductSeasonName, Collator.getInstance(Locale.CHINA))).collect(toList());
            tableDataInfo.setRows(list);
        }
        return tableDataInfo;
    }

    /**
     * 导出采购合同统计报表明细
     */
    @Log(title = "导出采购合同统计报表明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购合同统计报表明细", notes = "导出采购合同统计报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/count/export")
    public void exportCountItem(HttpServletResponse response, PurPurchaseContract purPurchaseContract) throws IOException {
        List<PurPurchaseContractFormResponse> list = purPurchaseContractService.getCountForm(purPurchaseContract);
        if (CollectionUtils.isNotEmpty(list)){
            list = list.stream().sorted(Comparator.comparing(PurPurchaseContractFormResponse::getYear, Comparator.reverseOrder())
                    .thenComparing(PurPurchaseContractFormResponse::getVendorShortName, Collator.getInstance(Locale.CHINA))
                    .thenComparing(PurPurchaseContractFormResponse::getContractType)).collect(toList());
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchaseContractFormResponse> util = new ExcelUtil<>(PurPurchaseContractFormResponse.class, dataMap);
        util.exportExcel(response, list, "采购合同统计报表");
    }

    @ApiOperation(value = "合同模板列表", notes = "合同模板列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ContractTemplateAttach.class))
    @PostMapping("/template/list")
    public TableDataInfo contractTemplateList(@RequestBody ContractTemplateAttach request) {
        startPage();
        List<ContractTemplateAttach> list = purPurchaseContractService.selectContractTemplateList(request);
        return getDataTable(list);
    }

    /**
     * 自动生成电子合同
     */
    @ApiOperation(value = "自动生成电子合同", notes = "自动生成电子合同")
    @PostMapping("/template/down")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createPdf(@RequestBody ContractTemplateAttach form){
        if (form.getFormSid() == null){
            throw new BaseException("请选择一份合同");
        }
        if (StrUtil.isBlank(form.getFilePath()) || StrUtil.isBlank(form.getFileName())){
            throw new BaseException("请选择下载模板");
        }
        PurPurchaseContractAttachment attachment = null;
        try {
            attachment = purPurchaseContractAttachmentMapper.selectOne(new QueryWrapper<PurPurchaseContractAttachment>()
                    .lambda()
                    .eq(PurPurchaseContractAttachment::getPurchaseContractSid,form.getFormSid()).eq(PurPurchaseContractAttachment::getFileType,ConstantsEms.FILE_TYPE_XSHT));
        }catch (Exception e){
            throw new CustomException("该合同附件中存在重复电子合同，请确认保留一份正确的电子合同后操作！");
        }
        if (attachment != null){
            throw new CustomException("该合同附件中已存在电子合同");
        }
        //
        String bucketPre = "/minio/" + minioConfig.getBucketName();
        String fileName = form.getFilePath().substring(form.getFilePath().indexOf(bucketPre)+bucketPre.length());
        File pdf ;
        GetObjectResponse pdfResponse = null;
        GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
        try {
            pdfResponse= client.getObject(args);
            // 获取minio上的空白模板作为文件
            String pre = form.getFileName().substring(0, form.getFileName().lastIndexOf("."));
            String suf = form.getFileName().substring(form.getFileName().lastIndexOf("."));
            pdf = CommonUtil.asFile(pdfResponse, pre, suf);
            // 自动生成一份电子合同
            MultipartFile multipartFile = null;
            multipartFile = purPurchaseContractService.autoGenContract(pdf.getPath(), pre, form.getFormSid());
            // 删除临时文件
            pdf.deleteOnExit();
            if (multipartFile != null){
                // 将生成的电子合同上传到minio上
                R<SysFile> resFile = remoteFileService.upload(multipartFile);
                // 上传后的文件地址
                String filePath = resFile.getData().getUrl();
                if (resFile != null){
                    // 保存电子合同信息到附件
                    PurPurchaseContractAttachment purPurchaseContractAttachment = new PurPurchaseContractAttachment();
                    purPurchaseContractAttachment.setPurchaseContractSid(form.getFormSid()).setFileName(resFile.getData().getName())
                            .setFilePath(filePath).setFileType(ConstantsEms.FILE_TYPE_XSHT);
                    int row = purPurchaseContractAttachmentService.insertPurPurchaseContractAttachment(purPurchaseContractAttachment);
                    // 请求下载
                    return AjaxResult.success(null, filePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }
}
