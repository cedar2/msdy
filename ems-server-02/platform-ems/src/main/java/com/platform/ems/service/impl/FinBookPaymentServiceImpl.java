package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.poi.excel.ExcelReader;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConBookSourceCategory;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConBookSourceCategoryMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.IFinBookPaymentItemService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.service.IFinBookPaymentService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-付款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-07
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class FinBookPaymentServiceImpl extends ServiceImpl<FinBookPaymentMapper,FinBookPayment>  implements IFinBookPaymentService {
    @Autowired
    private FinBookPaymentMapper finBookPaymentMapper;
    @Autowired
    private FinBookPaymentItemMapper finBookPaymentItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConBookSourceCategoryMapper conBookSourceCategoryMapper;
    @Autowired
    private IFinBookPaymentItemService bookPaymentItemService;

    private static final String TITLE = "财务流水账-收款";

    /**
     * 新建
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinBookPayment(FinBookPayment payment) {
        int row = finBookPaymentMapper.insert(payment);
        if (row > 0) {
            // 写入明细
            if (CollectionUtil.isNotEmpty(payment.getItemList())) {
                bookPaymentItemService.insertByList(payment);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinBookReceiptPayment(), payment);
            MongodbDeal.insert(payment.getBookPaymentSid(), payment.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 查报表
     * @param entity
     * @return
     */
    public List<FinBookPayment> getReportForm(FinBookPayment entity){
        List<FinBookPayment> requestList = finBookPaymentMapper.getReportForm(entity);
        return requestList;
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<FinBookPayment> responseList = new ArrayList<>();
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
            //数据字典Map
            List<DictData> yearDict = sysDictDataService.selectDictData("s_year"); //年份
            yearDict = yearDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearMaps = yearDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
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
                 * 供应商编码
                 */
                String vendorShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                String vendorCode = null;
                String vendorName = null;
                Long vendorSid = null;
                if (StrUtil.isBlank(vendorShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>().lambda().eq(BasVendor::getShortName, vendorShortName));
                        if (basVendor == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("供应商简称为"+ vendorShortName +"没有对应的供应商，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(vendorShortName + "对应的供应商必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                vendorSid = basVendor.getVendorSid();
                                vendorName = basVendor.getVendorName();
                                vendorCode = String.valueOf(basVendor.getVendorCode());
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName + "供应商档案存在重复，请先检查该供应商，导入失败！");
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
                /**
                 * 已付金额,核销中金额,已核销金额
                 */
                String yif = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal currencyAmountTaxFk = null;
                BigDecimal currencyAmountTaxHxz = BigDecimal.ZERO;
                String yhx = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal currencyAmountTaxYhx = null;
                if (StrUtil.isBlank(yif)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("已付金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yif,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("已付金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxFk = new BigDecimal(yif);
                        if (BigDecimal.ZERO.compareTo(currencyAmountTaxFk) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("已付金额不能小于等于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                if (StrUtil.isBlank(yhx)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("已核销金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yhx,10,5)){
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
                        }
                        else {
                            if (currencyAmountTaxFk != null) {
                                if (currencyAmountTaxFk.compareTo(currencyAmountTaxYhx) < 0) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("已核销金额不能大于已付款金额，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
                /**
                 * 流水来源类别
                 */
                String bookSourceCategoryName = null;
                String bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_YFK;
                try {
                    ConBookSourceCategory conBookSourceCategory = conBookSourceCategoryMapper.selectOne(new QueryWrapper<ConBookSourceCategory>().lambda()
                            .eq(ConBookSourceCategory::getCode,bookSourceCategory));
                    if (conBookSourceCategory != null){
                        bookSourceCategoryName = conBookSourceCategory.getName();
                    }
                }catch (Exception e){
                    log.info("{} 流水来源类别存在重复，请检查该流水来源类别！", bookSourceCategory);
                }
                /**
                 * 税率
                 */
                String taxRateValue = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
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
                /**
                 * 备注
                 */
                String remark =  objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                // 校验 根据“凭证日期+客户+公司”校重，若存在重复，提示：组合“凭证日期+客户+公司”已存在，导入失败！
                if (documentDate != null && vendorSid != null && companySid != null) {
                    String key = documentDateS + "-" + vendorSid + "-" + companySid;
                    // 判断是否与表格内的编码重复
                    if (uniqueMap.get(key) == null) {
                        uniqueMap.put(key, String.valueOf(num));
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，组合“凭证日期+供应商+公司”已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (CollectionUtils.isEmpty(errMsgList)){
                    String clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    if (currencyAmountTaxHxz.compareTo(BigDecimal.ZERO) != 0 || currencyAmountTaxYhx.compareTo(BigDecimal.ZERO) != 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    }
                    if (currencyAmountTaxFk.compareTo(currencyAmountTaxYhx) == 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    }
                    //付款流水主表
                    FinBookPayment finBookPayment = new FinBookPayment();
                    finBookPayment
                            .setVendorSid(vendorSid)
                            .setVendorName(vendorName)
                            .setVendorCode(vendorCode)
                            .setVendorShortName(vendorShortName)
                            .setCompanySid(companySid)
                            .setCompanyCode(companyCode)
                            .setCompanyName(companyName)
                            .setCompanyShortName(companyShortName)
                            .setDocumentDate(documentDate)
                            .setBookType(ConstantsFinance.BOOK_TYPE_FK)
                            .setBookSourceCategory(bookSourceCategory)
                            .setBookSourceCategoryName(bookSourceCategoryName)
                            .setCurrency(ConstantsFinance.CURRENCY_CNY)
                            .setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(remark);
                    finBookPayment.setCurrencyAmountTaxFk(currencyAmountTaxFk)
                            .setClearStatus(clearStatus).setCurrencyAmountTaxHxz(currencyAmountTaxHxz)
                            .setCurrencyAmountTaxYhx(currencyAmountTaxYhx).setTaxRate(taxRate);
                    LocalDate localDate = documentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    finBookPayment.setPaymentYear((long) localDate.getYear())
                            .setPaymentMonth((long) localDate.getMonthValue());
                    responseList.add(finBookPayment);
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
    public int addForm(List<FinBookPayment> request) {
        if (CollectionUtils.isEmpty(request)) {
            return 0;
        }
        request.forEach(item -> {
            finBookPaymentMapper.insert(item);
            //预付明细表
            FinBookPaymentItem finBookPaymentItem = new FinBookPaymentItem();
            finBookPaymentItem.setCurrencyAmountTaxFk(item.getCurrencyAmountTaxFk())
                    .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz())
                    .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx());
            if (BigDecimal.ZERO.compareTo(item.getCurrencyAmountTaxFk()) < 0){
                finBookPaymentItem.setCurrencyAmountTaxFk(item.getCurrencyAmountTaxFk());
            }
            if (BigDecimal.ZERO.compareTo(item.getCurrencyAmountTaxHxz()) < 0){
                finBookPaymentItem.setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz());
            }
            if (BigDecimal.ZERO.compareTo(item.getCurrencyAmountTaxYhx()) < 0){
                finBookPaymentItem.setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx());
            }
            finBookPaymentItem
                    .setBookPaymentSid(item.getBookPaymentSid())
                    .setItemNum((long)1)
                    .setClearStatus(item.getClearStatus())
                    .setTaxRate(item.getTaxRate()).setRemark(item.getRemark());
            finBookPaymentItem.setIsFinanceVerify(ConstantsEms.NO);
            finBookPaymentItemMapper.insert(finBookPaymentItem);
        });
        return request.size();
    }
}
