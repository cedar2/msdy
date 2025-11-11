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
import com.platform.common.core.domain.entity.SysUser;
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
import com.platform.ems.plug.domain.ConAdvanceSettleMode;
import com.platform.ems.plug.mapper.ConAdvanceSettleModeMapper;
import com.platform.ems.plug.mapper.ConMaterialTypeMapper;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.JudgeFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.IFinRecordAdvanceReceiptService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户业务台账-预收Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@Service
@SuppressWarnings("all")
public class FinRecordAdvanceReceiptServiceImpl extends ServiceImpl<FinRecordAdvanceReceiptMapper, FinRecordAdvanceReceipt> implements IFinRecordAdvanceReceiptService {
    @Autowired
    private FinRecordAdvanceReceiptMapper finRecordAdvanceReceiptMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private FinRecordAdvanceReceiptItemMapper itemMapper;
    @Autowired
    private FinRecordAdvanceReceiptAttachmentMapper atmMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private BasProductSeasonMapper productSeasonMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private ConAdvanceSettleModeMapper conAdvanceSettleModeMapper;
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;


    private static final String TITLE = "客户业务台账-预收";

    /**
     * 查询客户业务台账-预收
     *
     * @param recordAdvanceReceiptSid 客户业务台账-预收ID
     * @return 客户业务台账-预收
     */
    @Override
    public FinRecordAdvanceReceipt selectFinRecordAdvanceReceiptById(Long recordAdvanceReceiptSid) {
        FinRecordAdvanceReceipt finRecordAdvanceReceipt = finRecordAdvanceReceiptMapper.selectFinRecordAdvanceReceiptById(recordAdvanceReceiptSid);
        MongodbUtil.find(finRecordAdvanceReceipt);
        return finRecordAdvanceReceipt;
    }

    /**
     * 查询客户业务台账-预收列表
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 客户业务台账-预收
     */
    @Override
    public List<FinRecordAdvanceReceipt> selectFinRecordAdvanceReceiptList(FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        return finRecordAdvanceReceiptMapper.selectFinRecordAdvanceReceiptList(finRecordAdvanceReceipt);
    }

    /**
     * 新增客户业务台账-预收
     * 需要注意编码重复校验
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinRecordAdvanceReceipt(FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        int row = finRecordAdvanceReceiptMapper.insert(finRecordAdvanceReceipt);
        if (row > 0) {
            if (CollectionUtils.isNotEmpty(finRecordAdvanceReceipt.getItemList())) {
                finRecordAdvanceReceipt.getItemList().forEach(item -> {
                    item.setRecordAdvanceReceiptSid(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
                    itemMapper.insert(item);
                });
            }
            if (CollectionUtils.isNotEmpty(finRecordAdvanceReceipt.getAtmList())) {
                finRecordAdvanceReceipt.getAtmList().forEach(atm -> {
                    atm.setRecordAdvanceReceiptSid(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
                    atmMapper.insert(atm);
                });
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改客户业务台账-预收
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinRecordAdvanceReceipt(FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        FinRecordAdvanceReceipt response = finRecordAdvanceReceiptMapper.selectFinRecordAdvanceReceiptById(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
        int row = finRecordAdvanceReceiptMapper.updateById(finRecordAdvanceReceipt);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid(), BusinessType.UPDATE.getValue(), response, finRecordAdvanceReceipt, TITLE);
        }
        return row;
    }

    /**
     * 变更客户业务台账-预收
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinRecordAdvanceReceipt(FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        FinRecordAdvanceReceipt response = finRecordAdvanceReceiptMapper.selectFinRecordAdvanceReceiptById(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
        int row = finRecordAdvanceReceiptMapper.updateAllById(finRecordAdvanceReceipt);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid(), BusinessType.CHANGE.getValue(), response, finRecordAdvanceReceipt, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户业务台账-预收
     *
     * @param recordAdvanceReceiptSids 需要删除的客户业务台账-预收ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinRecordAdvanceReceiptByIds(List<Long> recordAdvanceReceiptSids) {
        return finRecordAdvanceReceiptMapper.deleteBatchIds(recordAdvanceReceiptSids);
    }

    /**
     * 更改确认状态
     *
     * @param finRecordAdvanceReceipt
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        int row = 0;
        Long[] sids = finRecordAdvanceReceipt.getRecordAdvanceReceiptSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                finRecordAdvanceReceipt.setRecordAdvanceReceiptSid(id);
                row = finRecordAdvanceReceiptMapper.updateById(finRecordAdvanceReceipt);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    public List<FinRecordAdvanceReceipt> getReportForm(FinRecordAdvanceReceipt entity) {
        List<FinRecordAdvanceReceipt> finRecordAdvanceReceiptList = finRecordAdvanceReceiptMapper.getReportForm(entity);
        return finRecordAdvanceReceiptList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<FinRecordAdvanceReceipt> responseList = new ArrayList<>();
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
                String customerShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString(); //客户简称  (必填)
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
                            errMsg.setMsg("简称为"+ customerShortName +"没有对应的客户，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(customerShortName + "对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                customerSid = basCustomer.getCustomerSid();
                                customerCode = basCustomer.getCustomerCode();
                                customerName = basCustomer.getCustomerName();
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
                }else {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName, companyShortName));
                        if (basCompany == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为"+ companyShortName +"没有对应的公司，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
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
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司档案存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 预收金额,核销中金额,已核销金额
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
                    errMsg.setMsg("预收金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yings,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("预收金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxYings = new BigDecimal(yings);
                        if (BigDecimal.ZERO.compareTo(currencyAmountTaxYings) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("预收金额不能小于等于0，导入失败！");
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
                    errMsg.setMsg("核销中金额加已核销金额不能大于预收金额，导入失败！");
                    errMsgList.add(errMsg);
                }
                /**
                 * 预收款结算方式
                 */
                String settleModeName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString(); //预收款结算方式  (必填)
                String settleMode = null;
                if (StrUtil.isBlank(settleModeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("预收款结算方式不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConAdvanceSettleMode advanceSettleMode = conAdvanceSettleModeMapper.selectOne(new QueryWrapper<ConAdvanceSettleMode>().lambda().eq(ConAdvanceSettleMode::getName, settleModeName));
                        if (advanceSettleMode == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(settleModeName + "预收款结算方式配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(advanceSettleMode.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(advanceSettleMode.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(settleModeName + "对应的预收款结算方式必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                settleMode = advanceSettleMode.getCode();
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(settleModeName + "预收款结算方式存在重复，请先检查该预收款结算方式，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 预收款比例
                 */
                String rate = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString(); //预收款比例  (必填)
                BigDecimal advanceRate = null;
                if (StrUtil.isBlank(rate)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("预收款比例不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(rate,3,3)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("预收款比例格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        advanceRate = new BigDecimal(rate); //预收款比例  (必填)
                        if (BigDecimal.ZERO.compareTo(advanceRate) >= 0 || BigDecimal.ONE.compareTo(advanceRate) < 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("预收款比例应大于0小于等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 合同号
                 */
                String saleContractCode = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();  //合同号
                if (StrUtil.isNotBlank(saleContractCode)){
                    if (saleContractCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 订单号
                 */
                String orderCode = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();  //订单号
                Long saleOrderCode = null;
                if (StrUtil.isNotBlank(orderCode)){
                    try {
                        saleOrderCode = Long.parseLong(orderCode);
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("订单号格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (orderCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("订单号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同金额
                 */
                String contractTax = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();  //合同金额
                BigDecimal currencyAmountTaxContract = null;
                if (StrUtil.isNotBlank(contractTax)){
                    if (!JudgeFormat.isValidDouble(contractTax,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxContract = new BigDecimal(contractTax);  //合同金额
                    }
                }
                /**
                 * 订单金额
                 */
                String orderTax = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();  //订单金额
                BigDecimal currencyAmountTaxSo = null;
                if (StrUtil.isNotBlank(orderTax)){
                    if (!JudgeFormat.isValidDouble(orderTax,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("订单金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxSo =new BigDecimal(orderTax);  //订单金额
                    }
                }
                /**
                 * 年份
                 */
                String paymentYear_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString(); //年份 (数据字典)
                Integer paymentYear = null;
                if (StrUtil.isNotBlank(paymentYear_s)){
                    paymentYear_s = yearMaps.get(paymentYear_s); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(paymentYear_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(paymentYear_s + "年份配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        try {
                            paymentYear = Integer.parseInt(paymentYear_s);
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(paymentYear_s + "年份配置错误，请联系管理员，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 产品季
                 */
                String productSeasonCode = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString(); //产品季（填写编码）
                String productSeasonName = null;
                Long productSeasonSid = null; //表：产品季Sid
                if (StrUtil.isNotBlank(productSeasonCode)){
                    try {
                        BasProductSeason productSeason = productSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>().lambda().eq(BasProductSeason::getProductSeasonCode, productSeasonCode));
                        if (productSeason == null){
                            productSeasonName = productSeasonCode;
                            //  throw new BaseException("第"+ num + "行，产品季编码" + productSeasonCode +"不存在,请重新检查或请联系管理员！");
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(productSeason.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus())){
                                //   throw new BaseException("第"+ num +"行，对应的产品季必须是确认且已启用的状态，导入失败");
                            }else {
                                productSeasonSid = productSeason.getProductSeasonSid();
                                productSeasonName = productSeason.getProductSeasonName();
                            }
                        }
                    }catch (Exception e){
                        productSeasonName = productSeasonCode;
                    }
                }
                /**
                 * 销售员
                 */
                String salePerson = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString(); //销售员 （填写账号）
                String salePersonName = null;
                if (StrUtil.isNotBlank(salePerson)){
                    try {
                        SysUser userInfo = sysUserService.selectSysUserByName(salePerson);
                        if (userInfo == null){
                            salePersonName = salePerson;
                        }
                        else {
                            salePersonName = userInfo.getNickName();
                        }
                    } catch (Exception e){
                        salePersonName = salePerson;
                    }
                }
                /**
                 * 税率
                 */
                String taxRateValue = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString(); //税率  （配置档案）
                if (StrUtil.isNotBlank(taxRateValue)){
                    if (!JudgeFormat.isValidDouble(taxRateValue,3,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("税率格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(15)==null||objects.get(15)==""?null:objects.get(15).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtils.isEmpty(errMsgList)){
                    String bookSourceCategory = ""; // 表：流水来源类别
                    if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(settleMode)){
                        bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_SC;
                    }
                    if (ConstantsEms.ADVANCE_SETTLE_MODE_DD.equals(settleMode)){
                        bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_SO;
                    }
                    if (ConstantsEms.ADVANCE_SETTLE_MODE_WU.equals(settleMode)){
                        bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_SC;
                    }
                    String clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    if (currencyAmountTaxHxz.compareTo(BigDecimal.ZERO) != 0 || currencyAmountTaxYhx.compareTo(BigDecimal.ZERO) != 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    }
                    if (currencyAmountTaxYings.compareTo(currencyAmountTaxYhx) == 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    }
                    //预收主表
                    FinRecordAdvanceReceipt finRecordAdvanceReceipt = new FinRecordAdvanceReceipt();
                    finRecordAdvanceReceipt
                            .setAdvanceRate(advanceRate).setCurrencyAmountTaxContract(currencyAmountTaxContract)
                            .setCurrencyAmountTaxSo(currencyAmountTaxSo)
                            .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                            .setCustomerSid(customerSid)
                            .setCustomerName(customerName)
                            .setCustomerShortName(customerShortName)
                            .setCompanyName(companyName)
                            .setCompanyShortName(companyShortName)
                            .setCompanySid(companySid)
                            .setCustomerCode(customerCode)
                            .setCompanyCode(companyCode)
                            .setDocumentDate(new Date())
                            .setBookType(ConstantsFinance.BOOK_TYPE_YUS)
                            .setBookSourceCategory(bookSourceCategory)
                            .setPaymentYear(paymentYear)
                            .setSaleContractCode(saleContractCode)
                            .setSaleOrderCode(saleOrderCode)
                            .setSettleMode(settleMode)
                            .setRemark(remark);
                    finRecordAdvanceReceipt.setHandleStatus(ConstantsEms.CHECK_STATUS);
                    finRecordAdvanceReceipt.setSalePersonName(salePersonName).setProductSeasonName(productSeasonName)
                            .setSettleModeName(settleModeName).setProductSeasonSid(productSeasonSid).setSalePerson(salePerson);
                    finRecordAdvanceReceipt.setCurrencyAmountTaxYings(currencyAmountTaxYings)
                            .setClearStatus(clearStatus)
                            .setCurrencyAmountTaxHxz(currencyAmountTaxHxz).setCurrencyAmountTaxYhx(currencyAmountTaxYhx).setTaxRate(taxRateValue);
                    responseList.add(finRecordAdvanceReceipt);
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        if (CollectionUtils.isNotEmpty(errMsgList)){
            return AjaxResult.error("导入失败",errMsgList);
        }else {
            return AjaxResult.success(responseList);
        }
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addForm(List<FinRecordAdvanceReceipt> request) {
        if (CollectionUtils.isEmpty(request)) {
            return 0;
        }
        request.forEach(item -> {
            finRecordAdvanceReceiptMapper.insert(item);
            //预付明细表
            FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem = new FinRecordAdvanceReceiptItem();
            BigDecimal taxRate = null;
            if (StrUtil.isNotBlank(item.getTaxRate())){
                taxRate = new BigDecimal(item.getTaxRate());
            }
            finRecordAdvanceReceiptItem
                    .setRecordAdvanceReceiptSid(item.getRecordAdvanceReceiptSid())
                    .setItemNum((long)1)
                    .setCurrencyAmountTaxYings(item.getCurrencyAmountTaxYings())
                    .setClearStatus(item.getClearStatus())
                    .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz())
                    .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx())
                    .setTaxRate(taxRate).setRemark(item.getRemark());
            itemMapper.insert(finRecordAdvanceReceiptItem);
        });
        return request.size();
    }
}
