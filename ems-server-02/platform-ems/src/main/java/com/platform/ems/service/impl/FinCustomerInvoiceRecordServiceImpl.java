package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMaterialType;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConMaterialTypeMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.api.service.RemoteMenuService;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IFinCustomerInvoiceRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户发票台账表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinCustomerInvoiceRecordServiceImpl extends ServiceImpl<FinCustomerInvoiceRecordMapper, FinCustomerInvoiceRecord> implements IFinCustomerInvoiceRecordService {
    @Autowired
    private FinCustomerInvoiceRecordMapper finCustomerInvoiceRecordMapper;
    @Autowired
    private FinCustomerInvoiceRecordAttachmentMapper attachMapper;
    @Autowired
    private BasCustomerMapper customerMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private SysTodoTaskMapper todoTaskMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteMenuService remoteMenuService;

    @Autowired
    private FinReceivableBillItemInvoiceMapper finReceivableBillItemInvoiceMapper;

    private static final String TITLE = "客户发票台账表";

    /**
     * 查询客户发票台账表
     *
     * @param customerInvoiceRecordSid 客户发票台账表ID
     * @return 客户发票台账表
     */
    @Override
    public FinCustomerInvoiceRecord selectFinCustomerInvoiceRecordById(Long customerInvoiceRecordSid) {
        FinCustomerInvoiceRecord finCustomerInvoiceRecord = finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordById(customerInvoiceRecordSid);
        // 附件清单
        finCustomerInvoiceRecord.setAttachmentList(new ArrayList<>());
        List<FinCustomerInvoiceRecordAttachment> attachmentList = attachMapper.selectFinCustomerInvoiceRecordAttachmentList
                (new FinCustomerInvoiceRecordAttachment().setCustomerInvoiceRecordSid(customerInvoiceRecordSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            finCustomerInvoiceRecord.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(finCustomerInvoiceRecord);
        return finCustomerInvoiceRecord;
    }

    /**
     * 查询客户发票台账表列表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 客户发票台账表
     */
    @Override
    public List<FinCustomerInvoiceRecord> selectFinCustomerInvoiceRecordList(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        return finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordList(finCustomerInvoiceRecord);
    }

    /**
     * 校验
     */
    public void judge(FinCustomerInvoiceRecord record) {
        if (ConstantsEms.CHECK_STATUS.equals(record.getHandleStatus())) {

        }
    }

    /**
     * 填写关联编码
     */
    public void setData(FinCustomerInvoiceRecord record) {
        if (record.getCustomerInvoiceRecordSid() == null) {
            // 新建时
            record.setShoukuanStatus("WSK");
        }
        // 客户
        record.setCustomerCode(null);
        if (record.getCustomerSid() != null) {
            BasCustomer customer = customerMapper.selectById(record.getCustomerSid());
            if (customer != null) {
                record.setCustomerCode(customer.getCustomerCode());
            }
        }
        // 公司
        record.setCompanyCode(null);
        if (record.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(record.getCompanySid());
            if (company != null) {
                record.setCompanyCode(company.getCompanyCode());
            }
        }
    }

    /**
     * 新增客户发票台账表
     * 需要注意编码重复校验
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerInvoiceRecord(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        // 校验
        judge(finCustomerInvoiceRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
            finCustomerInvoiceRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            // 对账账期
            if (finCustomerInvoiceRecord.getInvoiceDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                finCustomerInvoiceRecord.setBusinessVerifyPeriod(sdf.format(finCustomerInvoiceRecord.getInvoiceDate()));
            }
        }
        // 默认字段值
        setData(finCustomerInvoiceRecord);
        int row = finCustomerInvoiceRecordMapper.insert(finCustomerInvoiceRecord);
        if (row > 0) {
            // 回写编码
            FinCustomerInvoiceRecord record = finCustomerInvoiceRecordMapper.selectById(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid());
            finCustomerInvoiceRecord.setCustomerInvoiceRecordCode(record.getCustomerInvoiceRecordCode());
            // 附件清单
            if (CollectionUtil.isNotEmpty(finCustomerInvoiceRecord.getAttachmentList())) {
                finCustomerInvoiceRecord.getAttachmentList().forEach(item -> {
                    item.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setCustomerInvoiceRecordSid(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid());
                });
                attachMapper.inserts(finCustomerInvoiceRecord.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_CUSTOMER_INVOICE_RECORD)
                        .setDocumentSid(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid());
                sysTodoTask.setTitle("客户发票台账" + finCustomerInvoiceRecord.getCustomerInvoiceRecordCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(finCustomerInvoiceRecord.getCustomerInvoiceRecordCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_CUS_INV_REC_MENU_NAME);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinCustomerInvoiceRecord(), finCustomerInvoiceRecord);
            MongodbDeal.insert(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid(), finCustomerInvoiceRecord.getHandleStatus(),
                    msgList, TITLE, null, finCustomerInvoiceRecord.getImportType());
        }
        return row;
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(FinCustomerInvoiceRecord record) {
        // 先删后加
        attachMapper.delete(new QueryWrapper<FinCustomerInvoiceRecordAttachment>().lambda()
                .eq(FinCustomerInvoiceRecordAttachment::getCustomerInvoiceRecordSid, record.getCustomerInvoiceRecordSid()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getCustomerInvoiceRecordSid() == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setCustomerInvoiceRecordSid(record.getCustomerInvoiceRecordSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            attachMapper.inserts(record.getAttachmentList());
        }
    }

    /**
     * 修改客户发票台账表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerInvoiceRecord(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        // 校验
        judge(finCustomerInvoiceRecord);
        setData(finCustomerInvoiceRecord);
        FinCustomerInvoiceRecord original = finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordById(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
            finCustomerInvoiceRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            // 对账账期
            if (finCustomerInvoiceRecord.getInvoiceDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                finCustomerInvoiceRecord.setBusinessVerifyPeriod(sdf.format(finCustomerInvoiceRecord.getInvoiceDate()));
            }
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finCustomerInvoiceRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finCustomerInvoiceRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finCustomerInvoiceRecordMapper.updateAllById(finCustomerInvoiceRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(finCustomerInvoiceRecord);
            // 删除待办
            Long[] sids = new Long[]{finCustomerInvoiceRecord.getCustomerInvoiceRecordSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, finCustomerInvoiceRecord.getHandleStatus(), null);
            }
            //插入日志
            MongodbDeal.update(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid(), original.getHandleStatus(),
                    finCustomerInvoiceRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更客户发票台账表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerInvoiceRecord(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        // 校验
        judge(finCustomerInvoiceRecord);
        setData(finCustomerInvoiceRecord);
        FinCustomerInvoiceRecord response = finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordById(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid());
        // 若此发票台账已被付款单引用，且付款单引用总金额小于修改后的票面金额，提示：发票台账金额小于被付款单核销金额，无法修改！
        if (finCustomerInvoiceRecord.getTotalCurrencyAmountTax() != null && (response.getTotalCurrencyAmountTax() == null ||
                finCustomerInvoiceRecord.getTotalCurrencyAmountTax().compareTo(response.getTotalCurrencyAmountTax()) != 0)) {
            List<FinReceivableBillItemInvoice> invoiceList = finReceivableBillItemInvoiceMapper.selectList(new QueryWrapper<FinReceivableBillItemInvoice>().lambda()
                    .eq(FinReceivableBillItemInvoice::getCustomerInvoiceRecordSid, finCustomerInvoiceRecord.getCustomerInvoiceRecordSid())
                    .isNotNull(FinReceivableBillItemInvoice::getCurrencyAmountTax));
            if (CollectionUtil.isNotEmpty(invoiceList)) {
                BigDecimal countSum = invoiceList.stream().map(FinReceivableBillItemInvoice::getCurrencyAmountTax).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                if (finCustomerInvoiceRecord.getTotalCurrencyAmountTax().compareTo(countSum) < 0) {
                    throw new BaseException("发票台账金额小于被收款单核销金额，无法修改！");
                }
            }
            finCustomerInvoiceRecord.setNewReceivableUseDate(new Date());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finCustomerInvoiceRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finCustomerInvoiceRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finCustomerInvoiceRecordMapper.updateAllById(finCustomerInvoiceRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(finCustomerInvoiceRecord);
            //插入日志
            MongodbUtil.insertUserLog(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid(), BusinessType.CHANGE.getValue(), response, finCustomerInvoiceRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户发票台账表
     *
     * @param customerInvoiceRecordSids 需要删除的客户发票台账表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerInvoiceRecordByIds(List<Long> customerInvoiceRecordSids) {
        List<FinCustomerInvoiceRecord> list = finCustomerInvoiceRecordMapper.selectList(new QueryWrapper<FinCustomerInvoiceRecord>()
                .lambda().in(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, customerInvoiceRecordSids));
        int row = finCustomerInvoiceRecordMapper.deleteBatchIds(customerInvoiceRecordSids);
        if (row > 0) {
            // 删除待办
            Long[] sids = customerInvoiceRecordSids.toArray(new Long[customerInvoiceRecordSids.size()]);
            // 删除附件
            attachMapper.delete(new QueryWrapper<FinCustomerInvoiceRecordAttachment>().lambda()
                    .in(FinCustomerInvoiceRecordAttachment::getCustomerInvoiceRecordSid, sids));
            // 删除待办
            sysTodoTaskService.deleteSysTodoTaskList(sids, null, null);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinCustomerInvoiceRecord());
                MongodbUtil.insertUserLog(o.getCustomerInvoiceRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 红冲处理逻辑
     */
    public int hongchongHandle(List<FinCustomerInvoiceRecord> recordList) {
        for (FinCustomerInvoiceRecord record : recordList) {
            Long referInvoiceRecordSid = record.getCustomerInvoiceRecordSid();
            Long referInvoiceRecordCode = record.getCustomerInvoiceRecordCode();
            record.setCustomerInvoiceRecordSid(null).setCustomerInvoiceRecordCode(null)
                    .setHandleStatus(ConstantsEms.CHECK_STATUS)
                    .setCreateDate(new Date()).setCreatorAccount(null)
                    .setUpdateDate(null).setUpdaterAccount(null).setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getSysUser().getUserName());
            record.setInvoiceType("RI").setInvoiceCategory("CI")
                    .setReferInvoiceRecordSid(referInvoiceRecordSid)
                    .setReferInvoiceRecordCode(referInvoiceRecordCode);
        }
        return finCustomerInvoiceRecordMapper.inserts(recordList);
    }

    /**
     * 更改确认状态 前校验
     *
     * @param finCustomerInvoiceRecord
     * @return
     */
    @Override
    public void checkJudge(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        Long[] sids = finCustomerInvoiceRecord.getCustomerInvoiceRecordSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            // 作废
            if (HandleStatus.INVALID.getCode().equals(finCustomerInvoiceRecord.getHandleStatus())) {
                if (CollectionUtil.isNotEmpty(finReceivableBillItemInvoiceMapper.selectList(new QueryWrapper<FinReceivableBillItemInvoice>().lambda()
                        .in(FinReceivableBillItemInvoice::getCustomerInvoiceRecordSid, sids)))) {
                    throw new BaseException("发票台账已被收款单引用，无法作废！");
                }
            }
            // 红冲
            if (HandleStatus.REDDASHED.getCode().equals(finCustomerInvoiceRecord.getHandleStatus())) {
                if (CollectionUtil.isNotEmpty(finReceivableBillItemInvoiceMapper.selectList(new QueryWrapper<FinReceivableBillItemInvoice>().lambda()
                        .in(FinReceivableBillItemInvoice::getCustomerInvoiceRecordSid, sids)))) {
                    throw new BaseException("发票台账已被收款单引用，无法红冲！");
                }
            }
        }
    }

    /**
     * 更改确认状态
     *
     * @param finCustomerInvoiceRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        Long[] sids = finCustomerInvoiceRecord.getCustomerInvoiceRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        // 校验
        checkJudge(finCustomerInvoiceRecord);
        String remark = null;
        // 原来的数据
        List<FinCustomerInvoiceRecord> recordList = finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordList
                (new FinCustomerInvoiceRecord().setCustomerInvoiceRecordSidList(sids));
        // 修改
        LambdaUpdateWrapper<FinCustomerInvoiceRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, sids);
        updateWrapper.set(FinCustomerInvoiceRecord::getHandleStatus, finCustomerInvoiceRecord.getHandleStatus());
        // 确认
        if (ConstantsEms.CHECK_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
            // 确认人
            updateWrapper.set(FinCustomerInvoiceRecord::getConfirmDate, new Date());
            updateWrapper.set(FinCustomerInvoiceRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        // 作废
        if (HandleStatus.INVALID.getCode().equals(finCustomerInvoiceRecord.getHandleStatus())) {
            if (StrUtil.isBlank(finCustomerInvoiceRecord.getCancelRemark())) {
                throw new BaseException("作废说明不能为空！");
            }
            updateWrapper.set(FinCustomerInvoiceRecord::getCancelRemark, finCustomerInvoiceRecord.getCancelRemark());
            remark = finCustomerInvoiceRecord.getCancelRemark();
        }
        // 红冲
        if (HandleStatus.REDDASHED.getCode().equals(finCustomerInvoiceRecord.getHandleStatus())) {
            if (StrUtil.isBlank(finCustomerInvoiceRecord.getHongchongRemark())) {
                throw new BaseException("红冲说明不能为空！");
            }
            updateWrapper.set(FinCustomerInvoiceRecord::getHongchongRemark, finCustomerInvoiceRecord.getHongchongRemark());
            remark = finCustomerInvoiceRecord.getHongchongRemark();
        }
        int row = finCustomerInvoiceRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())
                    || HandleStatus.RETURNED.getCode().equals(finCustomerInvoiceRecord.getHandleStatus())
                    || ConstantsEms.SUBMIT_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, finCustomerInvoiceRecord.getHandleStatus(), null);
            }
            if (ConstantsEms.CHECK_STATUS.equals(finCustomerInvoiceRecord.getHandleStatus())) {
                // 设置对账日期
                List<FinCustomerInvoiceRecord> dateList = recordList.stream().filter(o->o.getInvoiceDate() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(dateList)) {
                    for (FinCustomerInvoiceRecord record : dateList) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                        String formattedDate = sdf.format(record.getInvoiceDate());
                        finCustomerInvoiceRecordMapper.update(null, new UpdateWrapper<FinCustomerInvoiceRecord>().lambda()
                                .eq(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, record.getCustomerInvoiceRecordSid())
                                .set(FinCustomerInvoiceRecord::getBusinessVerifyPeriod, formattedDate));
                    }
                }
            }
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, finCustomerInvoiceRecord.getHandleStatus(), null, TITLE, remark);
            }
            // 红冲后的逻辑处理
            if (HandleStatus.REDDASHED.getCode().equals(finCustomerInvoiceRecord.getHandleStatus())) {
                hongchongHandle(recordList);
            }
        }
        return row;
    }

    /**
     * 更改对账账期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePeriod(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        Long[] sids = finCustomerInvoiceRecord.getCustomerInvoiceRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        // 原来的数据
        List<FinCustomerInvoiceRecord> recordList = finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordList(
                (new FinCustomerInvoiceRecord().setCustomerInvoiceRecordSidList(sids)));
        if (recordList.stream().anyMatch(o -> !ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus()))) {
            throw new BaseException("仅能操作处理状态为“已确认”的数据！");
        }
        if (StrUtil.isBlank(finCustomerInvoiceRecord.getBusinessVerifyPeriod())) {
            throw new BaseException("对账账期不能为空！");
        }
        // 修改
        LambdaUpdateWrapper<FinCustomerInvoiceRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, sids);
        updateWrapper.set(FinCustomerInvoiceRecord::getBusinessVerifyPeriod, finCustomerInvoiceRecord.getBusinessVerifyPeriod());
        int row = finCustomerInvoiceRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (FinCustomerInvoiceRecord record : recordList) {
                String periods = record.getBusinessVerifyPeriod() == null ? "" : record.getBusinessVerifyPeriod();
                String remark = "设置对账账期，更新前： " + periods + "；更新后：" + finCustomerInvoiceRecord.getBusinessVerifyPeriod();
                //插入日志
                MongodbUtil.insertUserLog(record.getCustomerInvoiceRecordSid(), BusinessType.QITA.getValue(), null, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 更改发票寄出状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSendFlag(FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        Long[] sids = finCustomerInvoiceRecord.getCustomerInvoiceRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        // 原来的数据
        List<FinCustomerInvoiceRecord> recordList = finCustomerInvoiceRecordMapper.selectFinCustomerInvoiceRecordList
                (new FinCustomerInvoiceRecord().setCustomerInvoiceRecordSidList(sids));
        if (recordList.stream().anyMatch(o -> !ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus()))) {
            throw new BaseException("仅能操作处理状态为“已确认”的数据！");
        }
        if (StrUtil.isBlank(finCustomerInvoiceRecord.getSendFlag())) {
            throw new BaseException("发票寄出状态不能为空！");
        }
        // 修改
        LambdaUpdateWrapper<FinCustomerInvoiceRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, sids);
        updateWrapper.set(FinCustomerInvoiceRecord::getSendFlag, finCustomerInvoiceRecord.getSendFlag());
        int row = finCustomerInvoiceRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbUtil.insertUserLog(id, BusinessType.CHANGE.getValue(), null, TITLE, "设置发票寄出状态");
            }
        }
        return row;
    }

    /**
     * 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importRecord(MultipartFile file) {
        int num = 0;
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

            // 数据字典判定结果
            List<DictData> signStatusList = sysDictDataService.selectDictData("s_invoice_send_status");
            signStatusList = signStatusList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> signStatusMaps = signStatusList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();

            // 列表
            List<FinCustomerInvoiceRecord> recordList = new ArrayList<>();

            if (readAll.size() > 100) {
                throw new BaseException("导入表格中数据请不要超过100行！");
            }

            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                // 主表
                FinCustomerInvoiceRecord record = new FinCustomerInvoiceRecord();
                /*
                 * 发票日期 必填
                 */
                String invoiceDateS = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString().trim();
                /*
                 * 客户简称 必填
                 */
                String customerShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString().trim();
                /*
                 * 公司简称  必填
                 */
                String companyShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString().trim();
                /*
                 * 票面金额(含税) 必填
                 */
                String totalCurrencyAmountTaxS = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString().trim();
                /*
                 * 税率 必填
                 */
                String taxRateS = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                /*
                 * 发票签收状态 字典 必填
                 */
                String signStatus = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString().trim();
                /*
                 * 发票号 选填
                 */
                String invoiceNum = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString().trim();
                /*
                 * 发票代码 选填
                 */
                String inoviceCode = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString().trim();
                /*
                 * 下单季 选填
                 */
                String productSeasonName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString().trim();
                /*
                 * 物料类型 选填
                 */
                String materialTypeName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString().trim();
                /*
                 * 纸质合同号 选填
                 */
                String paperContractCode = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString().trim();
                /*
                 * 总账日期 选填
                 */
                String generalLedgerDateS = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();
                /*
                 * 备注 选填
                 */
                String remark = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString().trim();

                // 发票日期
                Date invoiceDate = null;
                if (StrUtil.isBlank(invoiceDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("发票日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(invoiceDateS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("发票日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        invoiceDate = new Date();
                        try {
                            invoiceDate = DateUtil.parse(invoiceDateS);
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("发票日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            invoiceDate = null;
                        }
                    }
                }

                // 客户简称
                Long customerSid = null;
                String customerCode = null;
                if (StrUtil.isBlank(customerShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客户简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCustomer basCustomer = customerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getShortName, customerShortName));
                        if (basCustomer == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户" + customerShortName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(customerShortName + "对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                customerSid = basCustomer.getCustomerSid();
                                customerCode = String.valueOf(basCustomer.getCustomerCode());
                            }
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客户简称“" + companyShortName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("发票台账Service业务层处理->导入功能->客户简称查询报错");
                        e.printStackTrace();
                    }
                }

                // 公司简称
                Long companySid = null;
                String companyCode = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCompany basCompany = companyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName, companyShortName));
                        if (basCompany == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司" + companyShortName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "对应的公司必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                companySid = basCompany.getCompanySid();
                                companyCode = basCompany.getCompanyCode();
                            }
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司简称“" + companyShortName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("发票台账Service业务层处理->导入功能->公司简称查询报错");
                        e.printStackTrace();
                    }
                }

                // 票面金额(含税)
                BigDecimal totalCurrencyAmountTax = null;
                BigDecimal totalCurrencyAmount = null;
                if (StrUtil.isBlank(totalCurrencyAmountTaxS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("票面金额(含税)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(totalCurrencyAmountTaxS, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("票面金额(含税)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        totalCurrencyAmountTax = new BigDecimal(totalCurrencyAmountTaxS);
                        if (totalCurrencyAmountTax.compareTo(BigDecimal.ZERO) <= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("票面金额(含税)只能输入正数，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                // 税率
                BigDecimal taxRate = null;
                if (StrUtil.isBlank(taxRateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("税率不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        taxRate = new BigDecimal(taxRateS);
                        //
                        ConTaxRate conTaxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                                .eq(ConTaxRate::getTaxRateValue, taxRateS)
                                .eq(ConTaxRate::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConTaxRate::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (conTaxRate == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("税率填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            taxRate = conTaxRate.getTaxRateValue();
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("税率填写错误，导入失败！");
                        errMsgList.add(errMsg);
                        taxRate = null;
                    }
                }

                BigDecimal totalTaxAmount = null;
                if (taxRate != null && totalCurrencyAmountTax != null) {
                    // 含税 = 不含税 X （1+税率）
                    // 不含税 = 含税 / （1+税率）
                    totalCurrencyAmount = totalCurrencyAmountTax
                            .divide(BigDecimal.ONE.add(taxRate), 2, BigDecimal.ROUND_HALF_UP);
                    // 票面税额
                    totalTaxAmount = totalCurrencyAmountTax.subtract(totalCurrencyAmount);
                }

                // 发票寄出状态
                if (StrUtil.isBlank(signStatus)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("发票寄出状态不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    signStatus = signStatusMaps.get(signStatus);
                    if (StrUtil.isBlank(signStatus)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("发票寄出状态填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 发票号
                if (StrUtil.isNotBlank(invoiceNum)) {
                    if (invoiceNum.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("发票号最大只能输入20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 发票代码
                if (StrUtil.isNotBlank(inoviceCode)) {
                    if (inoviceCode.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("发票代码最大只能输入20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 下单季
                Long productSeasonSid = null;
                if (StrUtil.isNotBlank(productSeasonName)) {
                    try {
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>().lambda()
                                .eq(BasProductSeason::getProductSeasonName, productSeasonName));
                        if (productSeason == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("下单季" + productSeasonName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(productSeason.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(productSeasonName + "对应的下单季必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                productSeasonSid = productSeason.getProductSeasonSid();
                            }
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("下单季“" + productSeasonName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("发票台账Service业务层处理->导入功能->下单季名称查询报错");
                        e.printStackTrace();
                    }
                }

                // 物料类型
                String materialType = null;
                if (StrUtil.isNotBlank(materialTypeName)) {
                    ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>()
                            .lambda().eq(ConMaterialType::getName, materialTypeName)
                            .eq(ConMaterialType::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .eq(ConMaterialType::getStatus, ConstantsEms.ENABLE_STATUS));
                    if (conMaterialType == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        materialType = conMaterialType.getCode();
                    }
                }

                // 纸质合同
                if (StrUtil.isNotBlank(paperContractCode)) {
                    if (paperContractCode.length() > 60) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("纸质合同最大只能输入60位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 总账日期
                Date generalLedgerDate = null;
                if (StrUtil.isNotBlank(generalLedgerDateS)) {
                    if (!JudgeFormat.isValidDate(generalLedgerDateS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("总账日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        generalLedgerDate = new Date();
                        try {
                            generalLedgerDate = DateUtil.parse(generalLedgerDateS);
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("总账日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            generalLedgerDate = null;
                        }
                    }
                }

                // 备注
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注最大只能输入600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)) {
                    record.setInvoiceDate(invoiceDate).setCustomerSid(customerSid)
                            .setCustomerCode(customerCode).setCompanySid(companySid)
                            .setCompanyCode(companyCode).setProductSeasonSid(productSeasonSid)
                            .setTotalCurrencyAmountTax(totalCurrencyAmountTax)
                            .setTotalCurrencyAmount(totalCurrencyAmount)
                            .setTotalTaxAmount(totalTaxAmount)
                            .setTaxRate(taxRate).setSendFlag(signStatus)
                            .setInvoiceNum(invoiceNum).setInoviceCode(inoviceCode)
                            .setMaterialType(materialType).setPaperContractCode(paperContractCode)
                            .setGeneralLedgerDate(generalLedgerDate)
                            .setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN)
                            .setInvoiceType(ConstantsFinance.INVOICE_TYPE_BLUE)
                            .setInvoiceCategory(ConstantsFinance.INVOICE_CATE_BZ)
                            .setShoukuanStatus(ConstantsFinance.SHOUKUAN_STATUS_WSK)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setImportType(BusinessType.IMPORT.getValue())
                            .setRemark(remark);
                    recordList.add(record);
                }
            }

            // 报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            } else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.warning(recordList, warnMsgList, infoMsgList, message);
            } else if (CollectionUtil.isNotEmpty(recordList)) {
                for (FinCustomerInvoiceRecord record : recordList) {
                    insertFinCustomerInvoiceRecord(record);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.success(recordList.size(), null, infoMsgList, message);
            }

        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success();
    }

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
}
