package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.IFinBookReceiptEstimationItemService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.IFinBookReceiptEstimationService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应收暂估Service业务层处理
 *
 * @author qhq
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinBookReceiptEstimationServiceImpl extends ServiceImpl<FinBookReceiptEstimationMapper,FinBookReceiptEstimation>  implements IFinBookReceiptEstimationService {
    @Autowired
    private FinBookReceiptEstimationMapper finBookReceiptEstimationMapper;
    @Autowired
    private IFinBookReceiptEstimationItemService itemService;
    @Autowired
    private FinBookReceiptEstimationItemMapper itemMapper;
    @Autowired
    private FinBookReceiptEstimationAttachmentMapper atmMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasMaterialMapper materialMapper;
    @Autowired
    private BasSkuMapper skuMapper;
    @Autowired
    private BasMaterialBarcodeMapper materialBarcodeMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "财务流水账-应收暂估";

    /**
     * 新增财务流水账-应收暂估
     * 需要注意编码重复校验
     * @param finBookReceiptEstimation 财务流水账-应收暂估
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinBookReceiptEstimation(FinBookReceiptEstimation finBookReceiptEstimation) {
        confirmedInfo(finBookReceiptEstimation);
        finBookReceiptEstimation.setBookType(ConstantsFinance.BOOK_TYPE_YSZG);
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        finBookReceiptEstimation.setPaymentMonth(Long.valueOf(month));
        finBookReceiptEstimation.setPaymentYear(Long.valueOf(year));
        if (finBookReceiptEstimation.getBookFeature() == null){
            finBookReceiptEstimation.setBookFeature(ConstantsFinance.BOOK_FEATURE_ZC);
        }
        int row= finBookReceiptEstimationMapper.insert(finBookReceiptEstimation);
        if(row>0){
        	if(CollectionUtils.isNotEmpty(finBookReceiptEstimation.getItemList())) {
        	    for (FinBookReceiptEstimationItem item : finBookReceiptEstimation.getItemList()){
                    item.setIsFinanceVerify(ConstantsEms.NO);
                    if (item.getQuantity() == null) {
                        item.setQuantity(BigDecimal.ZERO);
                    }
                    if (item.getPriceTax() == null) {
                        item.setPriceTax(BigDecimal.ZERO);
                    }
                    if (item.getTaxRate() == null){
                        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>()
                                .lambda().eq(ConTaxRate::getIsDefault, ConstantsEms.YES).last("limit 1"));
                        if (taxRate != null) {
                            item.setTaxRate(taxRate.getTaxRateValue());
                        }
                    }
                    if (item.getPrice() == null){
                        item.setPrice(item.getPriceTax().multiply(BigDecimal.ONE.subtract(item.getTaxRate())));
                    }
                    if (item.getCurrencyAmountTax() == null){
                        item.setCurrencyAmountTax(item.getPriceTax().multiply(item.getQuantity()));
                    }
                    if (ConstantsFinance.BOOK_FEATURE_CX.equals(finBookReceiptEstimation.getBookFeature())){
                        item.setCurrencyAmountTaxYhx(item.getCurrencyAmountTax())
                                .setCurrencyAmountTaxHxz(BigDecimal.ZERO)
                                .setQuantityYhx(item.getQuantity())
                                .setQuantityHxz(BigDecimal.ZERO)
                                .setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX).setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX).setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
                    else{
                        item.setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                                .setCurrencyAmountTaxHxz(BigDecimal.ZERO)
                                .setQuantityYhx(BigDecimal.ZERO)
                                .setQuantityHxz(BigDecimal.ZERO)
                                .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX).setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_WHX).setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    item.setBookReceiptEstimationSid(finBookReceiptEstimation.getBookReceiptEstimationSid());
        			itemMapper.insert(item);
        		}
        	}
        	if(CollectionUtils.isNotEmpty(finBookReceiptEstimation.getAtmList())) {
        		finBookReceiptEstimation.getAtmList().forEach(atm->{
        			atm.setBookReceiptEstimationSid(finBookReceiptEstimation.getBookReceiptEstimationSid());
        			atmMapper.insert(atm);
        		});
        	}
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finBookReceiptEstimation.getBookReceiptEstimationSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public void confirmedInfo(FinBookReceiptEstimation entity) {
        if (HandleStatus.CONFIRMED.getCode().equals(entity.getHandleStatus())) {
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
        }
    }

    /**
     * 查报表
     * @param entity
     * @return
     */
    public List<FinBookReceiptEstimation> getReportForm(FinBookReceiptEstimation entity){
        List<FinBookReceiptEstimation> list = finBookReceiptEstimationMapper.getReportForm(entity);
        return list;
    }


    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<FinBookReceiptEstimation> responseList = new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            // 数据字典是否
            List<DictData> yesnoList = sysDictDataService.selectDictData("s_yesno_flag");
            yesnoList = yesnoList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yesnoMaps = yesnoList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 校重
            HashMap<String, String> uniqueMap = new HashMap<>();
            // 遍历
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                int num = i + 1;
                /**
                 * 凭证日期
                 */
                String documentDateS = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Date documentDate = null;
                if (StrUtil.isBlank(documentDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("凭证日期不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDate(documentDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("凭证日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        documentDate = new Date();
                        try {
                            documentDate = DateUtil.parse(documentDateS);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("凭证日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            documentDate = null;
                        }
                    }
                }
                /**
                 * 客户编码
                 */
                String customerShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                String customerCode = null;
                String customerName = null;
                Long customerSid = null;
                if (StrUtil.isBlank(customerShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客户简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getShortName, customerShortName));
                        if (basCustomer == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户简称为"+ customerShortName +"没有对应的客户，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(customerShortName + "对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                customerSid = basCustomer.getCustomerSid();
                                customerName = basCustomer.getCustomerName();
                                customerCode = String.valueOf(basCustomer.getCustomerCode());
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName + "客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 公司编码
                 */
                String companyShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Long companySid = null;
                String companyName = null;
                String companyCode = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName, companyShortName));
                        if (basCompany == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为"+ companyShortName +"没有对应的公司，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "对应的公司必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                companySid = basCompany.getCompanySid();
                                companyName = basCompany.getCompanyName();
                                companyCode = basCompany.getCompanyCode();
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司档案存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 物料/商品编码
                 */
                String materialCode = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                Long materialSid = null;
                String materialName = null;
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料/商品编码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    BasMaterial material = materialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, materialCode));
                    if (material == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料/商品编码不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        if (!ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus())) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料/商品编码" + materialCode +"必须为已确认状态！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialSid = material.getMaterialSid();
                            materialName = material.getMaterialName();
                        }
                    }
                }
                // 提示信息
                String sku1 = "", sku2 = "";
                /*
                 * SKU1名称
                 */
                String sku1Name = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                if (StrUtil.isNotBlank(sku1Name)) {
                    sku1 = "+SKU1名称(" + sku1Name + ")";
                }
                /*
                 * SKU2名称
                 */
                String sku2Name = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                if (StrUtil.isNotBlank(sku2Name)) {
                    sku2 = "+SKU2名称(" + sku2Name + ")";
                }
                /*
                 * 校验商品条码
                 */
                String barcodeCode = null;
                Long barcodeSid = null;
                Long sku1Sid = null, sku2Sid = null;
                String sku1Code = null, sku2Code = null, sku1Type = null, sku2Type = null;
                if (materialSid != null) {
                    try {
                        BasMaterialBarcode materialBarcode = materialBarcodeMapper.selectBasMaterialBarcodeListByInvImport(
                                new BasMaterialBarcode().setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                        if (materialBarcode == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料/商品编码" + materialCode + "对应的商品条码不存在！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            barcodeCode = materialBarcode.getBarcodeCode();
                            barcodeSid = materialBarcode.getBarcodeSid();
                            sku1Sid = materialBarcode.getSku1Sid();
                            sku1Code = materialBarcode.getSku1Code();
                            sku1Type = materialBarcode.getSku1Type();
                            sku2Sid = materialBarcode.getSku2Sid();
                            sku2Code = materialBarcode.getSku2Code();
                            sku2Type = materialBarcode.getSku2Type();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料/商品编码(" + materialCode + ")" + sku1 + sku2 + "的组合存在多笔商品条码，请先核实！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 销售价(含税)
                 */
                String priceTaxS = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal priceTax = null;
                if (StrUtil.isBlank(priceTaxS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售价(含税)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(priceTaxS,8,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售价(含税)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        priceTax = new BigDecimal(priceTaxS);
                        if (BigDecimal.ZERO.compareTo(priceTax) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("销售价(含税)不能小于等于0，导入失败！");
                            errMsgList.add(errMsg);
                            priceTax = null;
                        }
                    }
                }

                /**
                 * 数量
                 */
                String quantityS = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal quantity = null;
                if (StrUtil.isBlank(quantityS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("数量不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(quantityS,4,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("数量格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        quantity = new BigDecimal(quantityS);
                        if (BigDecimal.ZERO.compareTo(quantity) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("数量不能小于等于0，导入失败！");
                            errMsgList.add(errMsg);
                            quantity = null;
                        }
                    }
                }

                /**
                 * 已核销金额
                 */
                BigDecimal currencyAmountTaxHxz = BigDecimal.ZERO;
                String yhx = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                BigDecimal currencyAmountTaxYhx = null;
                if (StrUtil.isBlank(yhx)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("已核销金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yhx,10,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("已核销金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxYhx = new BigDecimal(yhx);
                        if (BigDecimal.ZERO.compareTo(currencyAmountTaxYhx) > 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("已核销金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                            currencyAmountTaxYhx = null;
                        }
                    }
                }

                /**
                 * 校验“销售价(含税) X 数量 （乘积保留两位小数） >=已核销金额 + 核销中金额“是否成立，
                 * 若不成立，提示：出入库金额(销售价(含税) X 数量)不能小于已核销金额+核销中金额！
                 */
                BigDecimal currencyAmountTax = null;
                if (priceTax != null && quantity != null) {
                    currencyAmountTax = quantity.multiply(priceTax).setScale(2, BigDecimal.ROUND_HALF_UP);
                    if (currencyAmountTaxYhx != null && currencyAmountTaxHxz != null) {
                        if (currencyAmountTax.compareTo(currencyAmountTaxYhx.add(currencyAmountTaxHxz)) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("销售价(含税) X 数量不能小于已核销金额，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 是否退货
                 */
                String isTuihuoS = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                String isTuihuo = null;
                if (StrUtil.isBlank(isTuihuoS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("是否退货不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    isTuihuo = yesnoMaps.get(isTuihuoS);
                    if (StrUtil.isBlank(isTuihuo)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否退货填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 销售价单位
                 */
                String unitPriceName = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                String unitPrice = null;
                if (StrUtil.isBlank(unitPriceName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售价单位不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    ConMeasureUnit measureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, unitPriceName)
                            .eq(ConMeasureUnit::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .eq(ConMeasureUnit::getStatus, ConstantsEms.ENABLE_STATUS));
                    if (measureUnit == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售价单位错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        unitPrice = measureUnit.getCode();
                    }
                }

                /**
                 * 流水来源类别
                 *
                 * 若“是否退货“为”是“，默认”流水来源类别“为”销售退货入库“；若“是否退货“为”否“，默认”流水来源类别“为”销售出库“
                 */
                String bookType = ConstantsFinance.BOOK_TYPE_YSZG;
                String bookSourceCategory = null;
                if (ConstantsEms.YES.equals(isTuihuo)) {
                    bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_RSOGR;
                }
                else if (ConstantsEms.NO.equals(isTuihuo)) {
                    bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_SOGI;
                }
                /**
                 * 税率
                 */
                String taxRateValue = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                BigDecimal taxRate = null;
                if (StrUtil.isNotBlank(taxRateValue)){
                    ConTaxRate conTaxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                            .eq(ConTaxRate::getTaxRateValue, taxRateValue)
                            .eq(ConTaxRate::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .eq(ConTaxRate::getStatus, ConstantsEms.ENABLE_STATUS));
                    if (conTaxRate == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("税率格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        taxRate = conTaxRate.getTaxRateValue();
                    }
                }
                // 销售价(不含税)=销售价(含税)/（1+税率）
                BigDecimal price = null;
                if (priceTax != null) {
                    BigDecimal rate = taxRate == null ? BigDecimal.ZERO : taxRate;
                    price = priceTax.divide(rate.add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP);
                }

                /**
                 * 备注
                 */
                String remark =  objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }


                // 校验 根据“凭证日期+客户+公司”校重，若存在重复，提示：组合“凭证日期+客户+公司”已存在，导入失败！
                if (documentDate != null && customerSid != null && companySid != null && barcodeSid != null && isTuihuo != null) {
                    String key = documentDateS + "-" + customerSid + "-" + companySid+ "-" + barcodeSid+ "-" + isTuihuo;
                    // 判断是否与表格内的编码重复
                    if (uniqueMap.get(key) == null) {
                        uniqueMap.put(key, String.valueOf(num));
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，“凭证日期+客户简称+公司简称+物料/商品编码+ SKU1名称+ SKU2名称+是否退货”组合已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (CollectionUtils.isEmpty(errMsgList)){
                    String clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    if (currencyAmountTaxHxz.compareTo(BigDecimal.ZERO) != 0 || currencyAmountTaxYhx.compareTo(BigDecimal.ZERO) != 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    }
                    if (currencyAmountTax != null && currencyAmountTax.compareTo(currencyAmountTaxYhx) == 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    }
                    //收款流水主表
                    FinBookReceiptEstimation bookEstimation = new FinBookReceiptEstimation();
                    bookEstimation
                            .setCustomerSid(customerSid).setCustomerName(customerName)
                            .setCustomerCode(customerCode).setCustomerShortName(customerShortName)
                            .setCompanySid(companySid).setCompanyCode(companyCode)
                            .setCompanyName(companyName).setCompanyShortName(companyShortName)
                            .setBarcodeSid(barcodeSid).setUnitBase(unitPrice)
                            .setMaterialSid(materialSid).setMaterialCode(materialCode).setMaterialName(materialName)
                            .setSku1Sid(sku1Sid).setSku1Name(sku1Name).setSku2Sid(sku2Sid).setSku2Name(sku2Name)
                            .setDocumentDate(documentDate).setPriceTax(priceTax).setPrice(price).setQuantity(quantity)
                            .setBookType(bookType).setUnitPrice(unitPrice).setUnitPriceName(unitPriceName)
                            .setBookSourceCategory(bookSourceCategory).setIsTuihuo(isTuihuo)
                            .setCurrency(ConstantsFinance.CURRENCY_CNY).setBookFeature(ConstantsFinance.BOOK_FEATURE_CG)
                            .setIsBusinessVerify(ConstantsEms.NO).setIsFinanceVerify(ConstantsEms.NO)
                            .setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(remark);
                    bookEstimation.setCurrencyAmountTax(currencyAmountTax).setQuantityYhx(BigDecimal.ZERO)
                            .setQuantityHxz(BigDecimal.ZERO).setClearStatus(clearStatus).setClearStatusMoney(clearStatus)
                            .setCurrencyAmountTaxHxz(currencyAmountTaxHxz)
                            .setCurrencyAmountTaxYhx(currencyAmountTaxYhx).setTaxRate(taxRate);
                    LocalDate localDate = documentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    bookEstimation.setPaymentYear((long) localDate.getYear())
                            .setPaymentMonth((long) localDate.getMonthValue());
                    responseList.add(bookEstimation);
                }
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        } catch (IndexOutOfBoundsException e) {
            throw new BaseException(e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(errMsgList)){
            return AjaxResult.error("导入失败",errMsgList);
        }else {
            return AjaxResult.success(responseList);
        }
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }

    @Override
    public int addForm(List<FinBookReceiptEstimation> request) {
        if (CollectionUtils.isEmpty(request)) {
            return 0;
        }
        Map<String, List<FinBookReceiptEstimation>> map = request.stream()
                .collect(Collectors.groupingBy(estimation ->
                        estimation.getDocumentDate() + "-" + estimation.getCustomerSid() + "-" + estimation.getCompanySid() + "-" + estimation.getIsTuihuo()));
        for (String key : map.keySet()) {
            List<FinBookReceiptEstimation> estimationList = map.get(key);
            FinBookReceiptEstimation estimation = estimationList.get(0);
            // 写入主表
            insertEstimation(estimation);
            // 明细
            List<FinBookReceiptEstimationItem> estimationItemList = BeanCopyUtils.copyListProperties(estimationList, FinBookReceiptEstimationItem::new);
            estimation.setItemList(estimationItemList);
            // 写明细
            itemService.updateByList(estimation);

        }
        return map.size();
    }

    /**
     * 新增财务流水账-应收暂估
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertEstimation(FinBookReceiptEstimation finBookReceiptEstimation) {
        int row = finBookReceiptEstimationMapper.insert(finBookReceiptEstimation);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinBookReceiptEstimation(), finBookReceiptEstimation);
            MongodbUtil.insertUserLog(finBookReceiptEstimation.getBookReceiptEstimationSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }


}
