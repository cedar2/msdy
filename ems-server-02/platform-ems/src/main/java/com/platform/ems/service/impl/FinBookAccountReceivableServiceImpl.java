package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConBookSourceCategory;
import com.platform.ems.plug.mapper.ConBookSourceCategoryMapper;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.service.IFinBookAccountReceivableService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应收Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-11
 */
@Service
@SuppressWarnings("all")
public class FinBookAccountReceivableServiceImpl extends ServiceImpl<FinBookAccountReceivableMapper, FinBookAccountReceivable> implements IFinBookAccountReceivableService {
    @Autowired
    private FinBookAccountReceivableMapper finBookAccountReceivableMapper;
    @Autowired
    private FinBookAccountReceivableItemMapper finBookAccountReceivableItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConBookSourceCategoryMapper conBookSourceCategoryMapper;

    private static final String TITLE = "财务流水账-应收";

    /**
     * 报表查询
     *
     * @param finBookAccountReceivable
     * @return
     */
    @Override
    public List<FinBookAccountReceivable> getReportForm(FinBookAccountReceivable request) {
        List<FinBookAccountReceivable> responseList = finBookAccountReceivableMapper.getReportForm(request);
        return responseList;
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<FinBookAccountReceivable> responseList = new ArrayList<>();
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
                 * 客户编码
                 */
                String customerShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString(); //客户编码  (必填)
                Long customerSid = null; //表：客户Sid
                String customerCode = null;
                String customerName = null;
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
                                customerCode = basCustomer.getCustomerCode();
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
                String companyShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString(); //公司编码  (必填)
                Long companySid = null; //表：公司Sid
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
                 * 应收金额,核销中金额,已核销金额
                 */
                String yings = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                BigDecimal currencyAmountTaxYings = null;
                String hxz = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal currencyAmountTaxHxz = null;
                String yhx = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal currencyAmountTaxYhx = null;
                if (StrUtil.isBlank(yings)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应收金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yings,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应收金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxYings = new BigDecimal(yings);
                        if (BigDecimal.ZERO.compareTo(currencyAmountTaxYings) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("应收金额不能小于等于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                if (StrUtil.isBlank(hxz)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("核销中金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(hxz,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("核销中金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxHxz = new BigDecimal(hxz);
                        if (BigDecimal.ZERO.compareTo(currencyAmountTaxHxz) > 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("核销中金额不能小于0，导入失败！");
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
                    }
                }
                if (currencyAmountTaxHxz != null && currencyAmountTaxYhx != null && currencyAmountTaxYings != null
                        && currencyAmountTaxHxz.add(currencyAmountTaxYhx).compareTo(currencyAmountTaxYings) > 0){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("核销中金额加已核销金额不能大于应收金额，导入失败！");
                    errMsgList.add(errMsg);
                }
                /**
                 * 流水来源类别
                 */
                String bookSourceCategoryName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                String bookSourceCategory = null;
                if (StrUtil.isBlank(bookSourceCategoryName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("流水来源类别不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConBookSourceCategory conBookSourceCategory = conBookSourceCategoryMapper.selectOne(new QueryWrapper<ConBookSourceCategory>().lambda()
                                .eq(ConBookSourceCategory::getName,bookSourceCategoryName));
                        if (conBookSourceCategory == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("流水来源类别配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(conBookSourceCategory.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conBookSourceCategory.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(bookSourceCategoryName + "对应的流水来源类别必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                bookSourceCategory = conBookSourceCategory.getCode();
                                if ((!ConstantsFinance.BOOK_SOURCE_CAT_SFPYS.equals(bookSourceCategory))
                                        && (!ConstantsFinance.BOOK_SOURCE_CAT_SFPZQ.equals(bookSourceCategory))
                                        && (!ConstantsFinance.BOOK_SOURCE_CAT_SFPWQ.equals(bookSourceCategory))){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("流水来源类别必须是销售发票预收/中期/尾期，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(bookSourceCategoryName + "流水来源类别存在重复，请先检查该流水来源类别，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 月账单所属期间
                 */
                String monthAccountPeriod = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (StrUtil.isBlank(monthAccountPeriod)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("月账单所属期间不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isYearMonth(monthAccountPeriod)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月账单所属期间格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        monthAccountPeriod = monthAccountPeriod.replace("/","-");
                    }
                }
                /**
                 * 年份
                 */
                String paymentYear_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString(); //年份 (数据字典)
                Long paymentYear = null;
                if (StrUtil.isNotBlank(paymentYear_s)){
                    paymentYear_s = yearMaps.get(paymentYear_s); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(paymentYear_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年份配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        try {
                            paymentYear = Long.parseLong(paymentYear_s);
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("年份配置错误，请联系管理员，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 税率
                 */
                String taxRateValue = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString(); //税率  （配置档案）
                BigDecimal taxRate = null;
                if (StrUtil.isNotBlank(taxRateValue)){
                    if (!JudgeFormat.isValidDouble(taxRateValue,3,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("税率格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        taxRate = new BigDecimal(taxRateValue);
                    }
                }
                /**
                 * 备注
                 */
                String remark =  objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString(); //备注
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtils.isEmpty(errMsgList)){
                    String clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    if (currencyAmountTaxHxz.compareTo(BigDecimal.ZERO) != 0 || currencyAmountTaxYhx.compareTo(BigDecimal.ZERO) != 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    }
                    if (currencyAmountTaxYings.compareTo(currencyAmountTaxYhx) == 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    }
                    //应收主表
                    FinBookAccountReceivable finBookAccountReceivable = new FinBookAccountReceivable();
                    finBookAccountReceivable.setMonthAccountPeriod(monthAccountPeriod)
                            .setCustomerSid(customerSid).setCompanySid(companySid).setDocumentDate(new Date()).setBookType(ConstantsFinance.BOOK_TYPE_YINGS)
                            .setBookSourceCategory(bookSourceCategory).setPaymentYear(paymentYear).setBookSourceCategoryName(bookSourceCategoryName)
                            .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(remark);
                    finBookAccountReceivable.setCustomerName(customerName).setCustomerShortName(customerShortName).setCompanyName(companyName).setCompanyShortName(companyShortName)
                            .setCustomerCode(customerCode).setCompanyCode(companyCode);
                    finBookAccountReceivable.setCurrencyAmountTaxYings(currencyAmountTaxYings)
                            .setClearStatus(clearStatus)
                            .setCurrencyAmountTaxHxz(currencyAmountTaxHxz).setCurrencyAmountTaxYhx(currencyAmountTaxYhx).setTaxRate(taxRate).setRemark(remark);
                    responseList.add(finBookAccountReceivable);
                }
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
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
    public int addForm(List<FinBookAccountReceivable> request) {
        if (CollectionUtils.isEmpty(request)) {
            return 0;
        }
        request.forEach(item -> {
            finBookAccountReceivableMapper.insert(item);
            //预付明细表
            FinBookAccountReceivableItem finBookAccountReceivableItem = new FinBookAccountReceivableItem();
            finBookAccountReceivableItem
                    .setBookAccountReceivableSid(item.getBookAccountReceivableSid())
                    .setItemNum((long)1)
                    .setCurrencyAmountTaxYings(item.getCurrencyAmountTaxYings())
                    .setClearStatus(item.getClearStatus())
                    .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz())
                    .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx())
                    .setTaxRate(item.getTaxRate()).setRemark(item.getRemark());
            finBookAccountReceivableItem.setIsFinanceVerify(ConstantsEms.NO);
            finBookAccountReceivableItemMapper.insert(finBookAccountReceivableItem);
        });
        return request.size();
    }
}
