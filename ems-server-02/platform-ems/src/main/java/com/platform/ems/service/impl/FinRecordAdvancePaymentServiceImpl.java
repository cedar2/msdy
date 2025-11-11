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
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConAdvanceSettleMode;
import com.platform.ems.plug.mapper.ConAdvanceSettleModeMapper;
import com.platform.ems.plug.mapper.ConMaterialTypeMapper;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.JudgeFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IFinRecordAdvancePaymentService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商业务台账-预付Service业务层处理
 *
 * @author qhq
 * @date 2021-05-29
 */
@Service
@SuppressWarnings("all")
public class FinRecordAdvancePaymentServiceImpl extends ServiceImpl<FinRecordAdvancePaymentMapper, FinRecordAdvancePayment> implements IFinRecordAdvancePaymentService {
    @Autowired
    private FinRecordAdvancePaymentMapper finRecordAdvancePaymentMapper;
    @Autowired
    private FinRecordAdvancePaymentItemMapper finRecordAdvancePaymentItemMapper;
    @Autowired
    private FinRecordAdvancePaymentAttachmentMapper atmMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private BasProductSeasonMapper productSeasonMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private ConAdvanceSettleModeMapper conAdvanceSettleModeMapper;
    @Autowired
    private PurPurchaseContractMapper purPurchaseContractMapper;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;

    private static final String TITLE = "供应商业务台账-预付";

    /**
     * 查询供应商业务台账-预付
     *
     * @param recordAdvancePaymentSid 供应商业务台账-预付ID
     * @return 供应商业务台账-预付
     */
    @Override
    public FinRecordAdvancePayment selectFinRecordAdvancePaymentById(Long recordAdvancePaymentSid) {
        FinRecordAdvancePayment finRecordAdvancePayment = finRecordAdvancePaymentMapper.selectFinRecordAdvancePaymentById(recordAdvancePaymentSid);
        if (finRecordAdvancePayment == null) {
            return new FinRecordAdvancePayment();
        }
        FinRecordAdvancePaymentItem item = new FinRecordAdvancePaymentItem();
        item.setRecordAdvancePaymentSid(recordAdvancePaymentSid);
        List<FinRecordAdvancePaymentItem> itemList = finRecordAdvancePaymentItemMapper.selectFinRecordAdvancePaymentItemList(item);
        if (itemList != null && itemList.size() > 0) {
            finRecordAdvancePayment.setItemList(itemList);
        }
        FinRecordAdvancePaymentAttachment atm = new FinRecordAdvancePaymentAttachment();
        atm.setRecordAdvancePaymentSid(recordAdvancePaymentSid);
        List<FinRecordAdvancePaymentAttachment> atmList = atmMapper.selectFinRecordAdvancePaymentAttachmentList(atm);
        if (atmList != null && atmList.size() > 0) {
            finRecordAdvancePayment.setAtmList(atmList);
        }
        MongodbUtil.find(finRecordAdvancePayment);
        return finRecordAdvancePayment;
    }

    /**
     * 查询供应商业务台账-预付列表
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 供应商业务台账-预付
     */
    @Override
    public List<FinRecordAdvancePayment> selectFinRecordAdvancePaymentList(FinRecordAdvancePayment finRecordAdvancePayment) {
        return finRecordAdvancePaymentMapper.selectFinRecordAdvancePaymentList(finRecordAdvancePayment);
    }

    /**
     * 新增供应商业务台账-预付
     * 需要注意编码重复校验
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinRecordAdvancePayment(FinRecordAdvancePayment finRecordAdvancePayment) {
        QueryWrapper<FinRecordAdvancePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_advance_payment_code", finRecordAdvancePayment.getRecordAdvancePaymentCode());
        FinRecordAdvancePayment pm = finRecordAdvancePaymentMapper.selectOne(queryWrapper);
        if (pm != null) {
            throw new BaseException("已存在编码，请重新输入！");
        }
        if (HandleStatus.CONFIRMED.getCode().equals(finRecordAdvancePayment.getHandleStatus())) {
            finRecordAdvancePayment.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            finRecordAdvancePayment.setConfirmDate(new Date());
        }
        int row = finRecordAdvancePaymentMapper.insert(finRecordAdvancePayment);
        if (row > 0) {
            List<FinRecordAdvancePaymentItem> itemList = finRecordAdvancePayment.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid());
                    finRecordAdvancePaymentItemMapper.insert(o);
                });
            }
            List<FinRecordAdvancePaymentAttachment> atmList = finRecordAdvancePayment.getAtmList();
            if (CollectionUtils.isNotEmpty(atmList)) {
                atmList.forEach(o -> {
                    o.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid());
                    atmMapper.insert(o);
                });
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finRecordAdvancePayment.getRecordAdvancePaymentSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商业务台账-预付
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinRecordAdvancePayment(FinRecordAdvancePayment finRecordAdvancePayment) {
        QueryWrapper<FinRecordAdvancePayment> queryWrapper = new QueryWrapper<>();
        FinRecordAdvancePayment response = finRecordAdvancePaymentMapper.selectFinRecordAdvancePaymentById(finRecordAdvancePayment.getRecordAdvancePaymentSid());
        if (!response.getRecordAdvancePaymentCode().equals(finRecordAdvancePayment.getRecordAdvancePaymentCode())) {
            queryWrapper.eq("record_advance_payment_code", finRecordAdvancePayment.getRecordAdvancePaymentCode());
            FinRecordAdvancePayment pm = finRecordAdvancePaymentMapper.selectOne(queryWrapper);
            if (pm != null) {
                throw new BaseException("已存在编码，请重新输入！");
            }
        }
        if (HandleStatus.CONFIRMED.getCode().equals(finRecordAdvancePayment.getHandleStatus())) {
            finRecordAdvancePayment.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            finRecordAdvancePayment.setConfirmDate(new Date());
        }
        int row = finRecordAdvancePaymentMapper.updateById(finRecordAdvancePayment);
        if (row > 0) {
            QueryWrapper<FinRecordAdvancePaymentItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("record_advance_payment_sid", finRecordAdvancePayment.getRecordAdvancePaymentSid());
            finRecordAdvancePaymentItemMapper.delete(itemWrapper);
            List<FinRecordAdvancePaymentItem> itemList = finRecordAdvancePayment.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid());
                    finRecordAdvancePaymentItemMapper.insert(o);
                });
            }
            QueryWrapper<FinRecordAdvancePaymentAttachment> atmWrapper = new QueryWrapper<>();
            atmWrapper.eq("record_advance_payment_sid", finRecordAdvancePayment.getRecordAdvancePaymentSid());
            atmMapper.delete(atmWrapper);
            List<FinRecordAdvancePaymentAttachment> atmList = finRecordAdvancePayment.getAtmList();
            if (CollectionUtils.isNotEmpty(atmList)) {
                atmList.forEach(o -> {
                    o.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid());
                    atmMapper.insert(o);
                });
            }
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvancePayment.getRecordAdvancePaymentSid(), BusinessType.UPDATE.getValue(), response, finRecordAdvancePayment, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商业务台账-预付
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinRecordAdvancePayment(FinRecordAdvancePayment finRecordAdvancePayment) {
        QueryWrapper<FinRecordAdvancePayment> queryWrapper = new QueryWrapper<>();
        FinRecordAdvancePayment response = finRecordAdvancePaymentMapper.selectFinRecordAdvancePaymentById(finRecordAdvancePayment.getRecordAdvancePaymentSid());
        if (!response.getRecordAdvancePaymentCode().equals(finRecordAdvancePayment.getRecordAdvancePaymentCode())) {
            queryWrapper.eq("record_advance_payment_code", finRecordAdvancePayment.getRecordAdvancePaymentCode());
            FinRecordAdvancePayment pm = finRecordAdvancePaymentMapper.selectOne(queryWrapper);
            if (pm != null) {
                throw new BaseException("已存在编码，请重新输入！");
            }
        }
        if (HandleStatus.CONFIRMED.getCode().equals(finRecordAdvancePayment.getHandleStatus())) {
            finRecordAdvancePayment.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            finRecordAdvancePayment.setConfirmDate(new Date());
        }
        int row = finRecordAdvancePaymentMapper.updateAllById(finRecordAdvancePayment);
        if (row > 0) {
            QueryWrapper<FinRecordAdvancePaymentItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("record_advance_payment_sid", finRecordAdvancePayment.getRecordAdvancePaymentSid());
            finRecordAdvancePaymentItemMapper.delete(itemWrapper);
            List<FinRecordAdvancePaymentItem> itemList = finRecordAdvancePayment.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid());
                    finRecordAdvancePaymentItemMapper.insert(o);
                });
            }
            QueryWrapper<FinRecordAdvancePaymentAttachment> atmWrapper = new QueryWrapper<>();
            atmWrapper.eq("record_advance_payment_sid", finRecordAdvancePayment.getRecordAdvancePaymentSid());
            atmMapper.delete(atmWrapper);
            List<FinRecordAdvancePaymentAttachment> atmList = finRecordAdvancePayment.getAtmList();
            if (CollectionUtils.isNotEmpty(atmList)) {
                atmList.forEach(o -> {
                    o.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid());
                    atmMapper.insert(o);
                });
            }
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvancePayment.getRecordAdvancePaymentSid(), BusinessType.CHANGE.getValue(), response, finRecordAdvancePayment, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商业务台账-预付
     *
     * @param recordAdvancePaymentSids 需要删除的供应商业务台账-预付ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinRecordAdvancePaymentByIds(List<Long> recordAdvancePaymentSids) {
        int i = finRecordAdvancePaymentMapper.deleteBatchIds(recordAdvancePaymentSids);
        if (i > 0) {
            recordAdvancePaymentSids.forEach(sid -> {
                QueryWrapper<FinRecordAdvancePaymentItem> itemWrapper = new QueryWrapper<>();
                itemWrapper.eq("record_advance_payment_sid", sid);
                finRecordAdvancePaymentItemMapper.delete(itemWrapper);
                QueryWrapper<FinRecordAdvancePaymentAttachment> atmWrapper = new QueryWrapper<>();
                atmWrapper.eq("record_advance_payment_sid", sid);
                atmMapper.delete(atmWrapper);
            });
        }
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finRecordAdvancePayment
     * @return
     */
    @Override
    public int check(FinRecordAdvancePayment finRecordAdvancePayment) {
        int row = 0;
        Long[] sids = finRecordAdvancePayment.getRecordAdvancePaymentSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                finRecordAdvancePayment.setRecordAdvancePaymentSid(id);
                row = finRecordAdvancePaymentMapper.updateById(finRecordAdvancePayment);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(finRecordAdvancePayment.getRecordAdvancePaymentSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 获取报表
     *
     * @param finRecordAdvancePayment
     * @return
     */
    @Override
    public List<FinRecordAdvancePayment> getReportForm(FinRecordAdvancePayment finRecordAdvancePayment) {
        List<FinRecordAdvancePayment> finRecordAdvancePaymentList = finRecordAdvancePaymentMapper.getReportForm(finRecordAdvancePayment);
        return finRecordAdvancePaymentList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<FinRecordAdvancePayment> responseList = new ArrayList<>();
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
                 * 供应商编码
                 */
                String vendorShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString(); //供应商简称  (必填)
                Long vendorSid = null; //表：供应商Sid
                String vendorName = null;
                Long vendorCode = null;
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
                                vendorCode = basVendor.getVendorCode();
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
                 * 预付金额,核销中金额,已核销金额
                 */
                String yingf = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                BigDecimal currencyAmountTaxYingf = null;
                String hxz = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal currencyAmountTaxHxz = null;
                String yhx = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal currencyAmountTaxYhx = null;
                if (StrUtil.isBlank(yingf)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("预付金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yingf,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("预付金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxYingf = new BigDecimal(yingf);
                        if (BigDecimal.ZERO.compareTo(currencyAmountTaxYingf) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("预付金额不能小于等于0，导入失败！");
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
                if (currencyAmountTaxHxz != null && currencyAmountTaxYhx != null && currencyAmountTaxYingf != null
                        && currencyAmountTaxHxz.add(currencyAmountTaxYhx).compareTo(currencyAmountTaxYingf) > 0){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("核销中金额加已核销金额不能大于预付金额，导入失败！");
                    errMsgList.add(errMsg);
                }
                /**
                 * 预付款结算方式
                 */
                String settleModeName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString(); //预付款结算方式  (必填)
                String settleMode = null;
                if (StrUtil.isBlank(settleModeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("预付款结算方式不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConAdvanceSettleMode advanceSettleMode = conAdvanceSettleModeMapper.selectOne(new QueryWrapper<ConAdvanceSettleMode>().lambda().eq(ConAdvanceSettleMode::getName, settleModeName));
                        if (advanceSettleMode == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(settleModeName + "预付款结算方式配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(advanceSettleMode.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(advanceSettleMode.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(settleModeName + "对应的预付款结算方式必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                settleMode = advanceSettleMode.getCode();
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(settleModeName + "预付款结算方式存在重复，请先检查该预付款结算方式，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 预付款比例
                 */
                String rate = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString(); //预付款比例  (必填)
                BigDecimal advanceRate = null;
                if (StrUtil.isBlank(rate)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("预付款比例不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(rate,3,3)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("预付款比例格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        advanceRate = new BigDecimal(rate); //预付款比例  (必填)
                        if (BigDecimal.ZERO.compareTo(advanceRate) >= 0 || BigDecimal.ONE.compareTo(advanceRate) < 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("预付款比例应大于0小于等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 合同号
                 */
                String purchaseContractCode = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();  //合同号
                if (StrUtil.isNotBlank(purchaseContractCode)){
                    if (purchaseContractCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 订单号
                 */
                String orderCode = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                Long purchaseOrderCode = null;
                if (StrUtil.isNotBlank(orderCode)){
                    try {
                        purchaseOrderCode = Long.parseLong(orderCode);
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
                String contractTax = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
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
                String order = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal currencyAmountTaxPo = null;
                if (StrUtil.isNotBlank(order)){
                    if (!JudgeFormat.isValidDouble(order,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("订单金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTaxPo =new BigDecimal(order);  //订单金额
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
                 * 采购员
                 */
                String buyer = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString(); //采购员 （填写账号）
                String buyerName = null;
                if (StrUtil.isNotBlank(buyer)){
                    try {
                        SysUser userInfo = sysUserService.selectSysUserByName(buyer);
                        if (userInfo == null){
                            buyerName = buyer;
                            //     throw new BaseException("第"+ num + "行，采购员账号：" + buyer + "不存在,请重新检查或请联系管理员！");
                        }
                        else {
                            buyerName = userInfo.getNickName();
                        }
                    } catch (Exception e){
                        buyerName = buyer;
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
                        bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_PC;
                    }
                    if (ConstantsEms.ADVANCE_SETTLE_MODE_DD.equals(settleMode)){
                        bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_PO;
                    }
                    if (ConstantsEms.ADVANCE_SETTLE_MODE_WU.equals(settleMode)){
                        bookSourceCategory = ConstantsFinance.BOOK_SOURCE_CAT_PC;
                    }
                    String clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    if (currencyAmountTaxHxz.compareTo(BigDecimal.ZERO) != 0 || currencyAmountTaxYhx.compareTo(BigDecimal.ZERO) != 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    }
                    if (currencyAmountTaxYingf.compareTo(currencyAmountTaxYhx) == 0){
                        clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    }
                    //预付主表
                    FinRecordAdvancePayment finRecordAdvancePayment = new FinRecordAdvancePayment();
                    finRecordAdvancePayment.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setAdvanceRate(advanceRate)
                            .setCurrencyAmountTaxContract(currencyAmountTaxContract)
                            .setCurrencyAmountTaxPo(currencyAmountTaxPo)
                            .setBuyer(buyer)
                            .setVendorSid(vendorSid)
                            .setCompanySid(companySid)
                            .setVendorCode(vendorCode)
                            .setVendorShortName(vendorShortName)
                            .setCompanyCode(companyCode)
                            .setVendorName(vendorName).setCompanyName(companyName)
                            .setCompanyShortName(companyShortName)
                            .setDocumentDate(new Date())
                            .setBookType(ConstantsFinance.BOOK_TYPE_YUF)
                            .setBookTypeName(ConstantsFinance.BOOK_TYPE_YUF)
                            .setBookSourceCategory(bookSourceCategory)
                            .setPaymentYear(paymentYear)
                            .setPurchaseContractCode(purchaseContractCode)
                            .setPurchaseOrderCode(purchaseOrderCode)
                            .setCurrencyAmountTaxYingf(currencyAmountTaxYingf)
                            .setClearStatus(clearStatus)
                            .setCurrencyAmountTaxHxz(currencyAmountTaxHxz)
                            .setCurrencyAmountTaxYhx(currencyAmountTaxYhx)
                            .setTaxRate(taxRateValue)
                            .setSettleMode(settleMode)
                            .setRemark(remark);
                    finRecordAdvancePayment.setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(remark);
                    finRecordAdvancePayment.setBuyerName(buyerName).setProductSeasonName(productSeasonName)
                            .setSettleModeName(settleModeName).setProductSeasonSid(productSeasonSid);
                    responseList.add(finRecordAdvancePayment);
            /*
                finRecordAdvancePaymentMapper .insert(finRecordAdvancePayment);
                //预付明细表
                FinRecordAdvancePaymentItem finRecordAdvancePaymentItem = new FinRecordAdvancePaymentItem();
                finRecordAdvancePaymentItem
                        .setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid())
                        .setItemNum(i-2)
                        .setCurrencyAmountTaxYingf(currencyAmountTaxYingf)
                        .setClearStatus(clearStatus)
                        .setCurrencyAmountTaxHxz(currencyAmountTaxHxz)
                        .setCurrencyAmountTaxYhx(currencyAmountTaxYhx)
                        .setTaxRate(new BigDecimal(taxRateValue)).setRemark(remark);
                finRecordAdvancePaymentItemMapper .insert(finRecordAdvancePaymentItem);

             */
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
    @Transactional(rollbackFor = Exception.class)
    public int addForm(List<FinRecordAdvancePayment> request) {
        if (CollectionUtils.isEmpty(request)) {
            return 0;
        }
        request.forEach(item -> {
            finRecordAdvancePaymentMapper.insert(item);
            //预付明细表
            FinRecordAdvancePaymentItem finRecordAdvancePaymentItem = new FinRecordAdvancePaymentItem();
            BigDecimal taxRate = null;
            if (StrUtil.isNotBlank(item.getTaxRate())){
                taxRate = new BigDecimal(item.getTaxRate());
            }
            finRecordAdvancePaymentItem
                    .setRecordAdvancePaymentSid(item.getRecordAdvancePaymentSid())
                    .setItemNum((long)1)
                    .setCurrencyAmountTaxYingf(item.getCurrencyAmountTaxYingf())
                    .setClearStatus(item.getClearStatus())
                    .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz())
                    .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx())
                    .setTaxRate(taxRate).setRemark(item.getRemark());
            finRecordAdvancePaymentItemMapper.insert(finRecordAdvancePaymentItem);
        });
        return request.size();
    }
}
