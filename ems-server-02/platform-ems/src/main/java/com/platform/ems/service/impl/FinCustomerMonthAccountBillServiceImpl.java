package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.CommonUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.IFinCustomerMonthAccountBillService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户月对账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinCustomerMonthAccountBillServiceImpl extends ServiceImpl<FinCustomerMonthAccountBillMapper, FinCustomerMonthAccountBill> implements IFinCustomerMonthAccountBillService {
    @Autowired
    private FinCustomerMonthAccountBillMapper finCustomerMonthAccountBillMapper;
    @Autowired
    private FinCustomerMonthAccountBillAttachMapper finCustomerMonthAccountBillAttachMapper;
    //应收暂估
    @Autowired
    private FinBookReceiptEstimationMapper finBookReceiptEstimationMapper;
    //发票
    @Autowired
    private FinSaleInvoiceMapper finSaleInvoiceMapper;
    //应收
    @Autowired
    private FinBookAccountReceivableMapper finBookAccountReceivableMapper;
    //收款
    @Autowired
    private FinBookReceiptPaymentMapper finBookReceiptPaymentMapper;
    //收款明细
    @Autowired
    private FinBookReceiptPaymentItemMapper finBookReceiptPaymentItemMapper;
    //扣款
    @Autowired
    private FinBookCustomerDeductionMapper finBookCustomerDeductionMapper;
    //调账
    @Autowired
    private FinBookCustomerAccountAdjustMapper finBookCustomerAccountAdjustMapper;
    //押金
    @Autowired
    private FinCustomerCashPledgeBillItemMapper finCustomerCashPledgeBillItemMapper;
    //暂押款
    @Autowired
    private FinCustomerFundsFreezeBillItemMapper finCustomerFundsFreezeBillItemMapper;
    //付款单
    @Autowired
    private FinPayBillMapper finPayBillMapper;
    //收款单
    @Autowired
    private FinReceivableBillMapper finReceivableBillMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户月对账单";

    /**
     * 查询客户月对账单
     *
     * @param customerMonthAccountBillSid 客户月对账单ID
     * @return 客户月对账单
     */
    @Override
    public FinCustomerMonthAccountBill selectFinCustomerMonthAccountBillById(Long customerMonthAccountBillSid) {
        FinCustomerMonthAccountBill finCustomerMonthAccountBill = finCustomerMonthAccountBillMapper.selectFinCustomerMonthAccountBillById(customerMonthAccountBillSid);
        //总览
        FinCustomerMonthAccountBillInfo info = new FinCustomerMonthAccountBillInfo();
        BeanCopyUtils.copyProperties(finCustomerMonthAccountBill, info);
        List<FinCustomerMonthAccountBillInfo> list = new ArrayList<>();
        list.add(info);
        finCustomerMonthAccountBill = selectItemList(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setInfo(list);
        //附件
        List<FinCustomerMonthAccountBillAttach> finCustomerMonthAccountBillAttachList = finCustomerMonthAccountBillAttachMapper.selectList(new QueryWrapper<FinCustomerMonthAccountBillAttach>()
                .lambda().eq(FinCustomerMonthAccountBillAttach::getCustomerMonthAccountBillSid, customerMonthAccountBillSid));
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBillAttachList)){
            finCustomerMonthAccountBill.setAttachmentList(finCustomerMonthAccountBillAttachList);
        }
        MongodbUtil.find(finCustomerMonthAccountBill);
        return finCustomerMonthAccountBill;
    }

    /**
     * 查询客户月对账单列表
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 客户月对账单
     */
    @Override
    public List<FinCustomerMonthAccountBill> selectFinCustomerMonthAccountBillList(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        return finCustomerMonthAccountBillMapper.selectFinCustomerMonthAccountBillList(finCustomerMonthAccountBill);
    }

    /**
     * 新增客户月对账单
     * 需要注意编码重复校验
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerMonthAccountBill(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        List<FinCustomerMonthAccountBill> one = finCustomerMonthAccountBillMapper.selectList(new QueryWrapper<FinCustomerMonthAccountBill>()
                .lambda().eq(FinCustomerMonthAccountBill::getCustomerSid,finCustomerMonthAccountBill.getCustomerSid())
                .eq(FinCustomerMonthAccountBill::getCompanySid,finCustomerMonthAccountBill.getCompanySid())
                .eq(FinCustomerMonthAccountBill::getYearMonths,finCustomerMonthAccountBill.getYearMonths()));
        if (CollectionUtil.isNotEmpty(one)){
            throw new BaseException("该月账单已存在，请核实");
        }
        //设置确认信息，校验
        setConfirmedInfo(finCustomerMonthAccountBill);
        int row = finCustomerMonthAccountBillMapper.insert(finCustomerMonthAccountBill);
        if (row > 0) {
            //插入子表，附件表
            insertChild(finCustomerMonthAccountBill.getAttachmentList(), finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //待办通知
            if (ConstantsEms.SAVA_STATUS.equals(finCustomerMonthAccountBill.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_customer_month_account_bill")
                        .setDocumentSid(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
                sysTodoTask.setTitle("客户月对账单: " + finCustomerMonthAccountBill.getCustomerMonthAccountBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finCustomerMonthAccountBill.getCustomerMonthAccountBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改客户月对账单
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerMonthAccountBill(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(finCustomerMonthAccountBill);
        FinCustomerMonthAccountBill response = finCustomerMonthAccountBillMapper.selectFinCustomerMonthAccountBillById(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
        int row = finCustomerMonthAccountBillMapper.updateById(finCustomerMonthAccountBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入子表，附件表
            insertChild(finCustomerMonthAccountBill.getAttachmentList(), finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerMonthAccountBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getDocumentSid, finCustomerMonthAccountBill.getCustomerMonthAccountBillSid()));
            }
            //插入日志
            MongodbUtil.insertUserLog(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.UPDATE.ordinal(), response, finCustomerMonthAccountBill, TITLE);
        }
        return row;
    }

    /**
     * 变更客户月对账单
     *
     * @param finCustomerMonthAccountBill 客户月对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerMonthAccountBill(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(finCustomerMonthAccountBill);
        FinCustomerMonthAccountBill response = finCustomerMonthAccountBillMapper.selectFinCustomerMonthAccountBillById(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
        int row = finCustomerMonthAccountBillMapper.updateAllById(finCustomerMonthAccountBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入子表，附件表
            insertChild(finCustomerMonthAccountBill.getAttachmentList(), finCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入日志
            MongodbUtil.insertUserLog(finCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.CHANGE.ordinal(), response, finCustomerMonthAccountBill, TITLE);
        }
        return row;
    }


    private void updateIsFinanceVerify(FinCustomerMonthAccountBill entity) {
        if (CollectionUtils.isNotEmpty(entity.getReceivableBillList())) {
            List<Long> sidList = entity.getReceivableBillList().stream().map(FinReceivableBill::getReceivableBillSid).collect(Collectors.toList());
            finReceivableBillMapper.update(null, new UpdateWrapper<FinReceivableBill>().lambda()
                    .set(FinReceivableBill::getIsFinanceVerify, ConstantsEms.YES).in(FinReceivableBill::getReceivableBillSid, sidList));
        }
        if (CollectionUtils.isNotEmpty(entity.getInvoiceList())) {
            List<Long> sidList = entity.getInvoiceList().stream().map(FinSaleInvoice::getSaleInvoiceSid).collect(Collectors.toList());
            finSaleInvoiceMapper.update(null, new UpdateWrapper<FinSaleInvoice>().lambda()
                    .set(FinSaleInvoice::getIsFinanceVerify, ConstantsEms.YES).in(FinSaleInvoice::getSaleInvoiceSid, sidList));
        }
    }

    /**
     * 批量删除客户月对账单
     *
     * @param customerMonthAccountBillSids 需要删除的客户月对账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerMonthAccountBillByIds(List<Long> customerMonthAccountBillSids) {
        int i = finCustomerMonthAccountBillMapper.deleteBatchIds(customerMonthAccountBillSids);
        if (i > 0) {
            customerMonthAccountBillSids.forEach(sid -> {
                //删除明细
                deleteItem(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .in(SysTodoTask::getDocumentSid, customerMonthAccountBillSids));
        }
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finCustomerMonthAccountBill
     * @return
     */
    @Override
    public int check(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        int row = 0;
        Long[] sids = finCustomerMonthAccountBill.getCustomerMonthAccountBillSidList();
        if (sids != null && sids.length > 0) {
            row = finCustomerMonthAccountBillMapper.update(null, new UpdateWrapper<FinCustomerMonthAccountBill>().lambda().set(FinCustomerMonthAccountBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(FinCustomerMonthAccountBill::getCustomerMonthAccountBillSid, sids));
            for (Long id : sids) {
                FinCustomerMonthAccountBill bill = finCustomerMonthAccountBillMapper.selectById(id);
                if (bill != null) {
                    selectInvoiceAndReceiavbleBillList(bill, new FinCustomerMonthAccountBill().setCustomerSid(bill.getCustomerSid()).setCompanySid(bill.getCompanySid())
                            .setYearMonths(bill.getYearMonths()));
                    updateIsFinanceVerify(bill);
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList, TITLE);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerMonthAccountBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB).in(SysTodoTask::getDocumentSid, sids));
            }
        }
        return row;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public FinCustomerMonthAccountBill setConfirmedInfo(FinCustomerMonthAccountBill entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
            updateIsFinanceVerify(entity);
        }
        return entity;
    }

    /**
     * 删除子表
     */
    public void deleteItem(Long sid) {
        //附件表
        QueryWrapper<FinCustomerMonthAccountBillAttach> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("customer_month_account_bill_sid", sid);
        finCustomerMonthAccountBillAttachMapper.delete(atmWrapper);
    }

    /**
     * 添加子表
     *
     * @param atmList
     * @param sid
     */
    public void insertChild(List<FinCustomerMonthAccountBillAttach> atmList, Long sid) {
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setCustomerMonthAccountBillSid(sid);
            });
            finCustomerMonthAccountBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 客户月对账单新建入口
     *
     * @param finCustomerMonthAccountBill
     * @return 客户月对账单
     */
    @Override
    public FinCustomerMonthAccountBill entrance(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        //查询客户月对账单明细信息
        finCustomerMonthAccountBill = selectItemList(finCustomerMonthAccountBill);
        //计算账单总览金额
        finCustomerMonthAccountBill = calculationAmount(finCustomerMonthAccountBill);
        return finCustomerMonthAccountBill;
    }

    /**
     * 查询客户月对账单明细信息 : 本期到票(发票)  +  本期收款
     *
     * @param finVendorMonthAccountBill
     * @return 供应商月对账单
     */
    private void selectInvoiceAndReceiavbleBillList(FinCustomerMonthAccountBill request, FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        //本期到票(发票):显示此客户的应收财务流水的“月账单所属期间”等于月账单的”所属年月”的应收财务流水的明细
        FinSaleInvoice invoice = new FinSaleInvoice().setCustomerSid(finCustomerMonthAccountBill.getCustomerSid()).setCompanySid(finCustomerMonthAccountBill.getCompanySid())
                .setMonthAccountPeriod(finCustomerMonthAccountBill.getYearMonths());
        invoice.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.REDDASHED.getCode()});
        List<FinSaleInvoice> invoiceList = finSaleInvoiceMapper.selectFinSaleInvoiceList(invoice);
        request.setInvoiceList(invoiceList);
        if (invoiceList == null) {
            request.setInvoiceList(new ArrayList<>());
        }

        // 本期收款
        FinReceivableBill receivableBill = new FinReceivableBill().setCompanySid(finCustomerMonthAccountBill.getCompanySid()).setCustomerSid(finCustomerMonthAccountBill.getCustomerSid())
                .setReceiptPaymentStatus(ConstantsFinance.RECEIPT_PAYMENT_STATUS_YDZ).setIsFinanceVerify(ConstantsEms.NO)
                .setMonthAccountPeriod(finCustomerMonthAccountBill.getYearMonths());
        List<FinReceivableBill> billList = finReceivableBillMapper.selectFinReceivableBillList(receivableBill);
        request.setReceivableBillList(billList);
        if (billList == null) {
            request.setReceivableBillList(new ArrayList<>());
        }
    }

    /**
     * 查询客户月对账单明细信息
     *
     * @param finCustomerMonthAccountBill
     * @return 客户月对账单
     */
    @Override
    public FinCustomerMonthAccountBill selectItemList(FinCustomerMonthAccountBill request) {
        FinCustomerMonthAccountBill finCustomerMonthAccountBill = new FinCustomerMonthAccountBill();
        finCustomerMonthAccountBill.setCustomerSid(request.getCustomerSid()).setCompanySid(request.getCompanySid())
                .setYearMonths(request.getYearMonths());

        //应收暂估:显示此客户的核销状态不是”全部核销“的应收暂估流水明细
        String[] clearStatus = new String[]{ConstantsFinance.CLEAR_STATUS_BFHX,ConstantsFinance.CLEAR_STATUS_WHX};
        FinBookReceiptEstimation estimation = new FinBookReceiptEstimation().setCustomerSid(finCustomerMonthAccountBill.getCustomerSid()).setCompanySid(finCustomerMonthAccountBill.getCompanySid())
                .setClearStatusMoneyList(clearStatus);
        List<FinBookReceiptEstimation> finBookReceiptEstimationList = finBookReceiptEstimationMapper.getReportForm(estimation);
        request.setBookReceiptEstimationList(finBookReceiptEstimationList);
        if (finBookReceiptEstimationList == null) {
            request.setBookReceiptEstimationList(new ArrayList<>());
        }

        //本期到票(发票)  +  收款
        selectInvoiceAndReceiavbleBillList(request, finCustomerMonthAccountBill);

        //本期扣款:显示客户的收款的“月账单所属期间”等于月账单的”所属年月”的收款中类型是“扣款”的扣款明细
        List<FinCustomerMonthAccountBillKkInfo> finBookCustomerDeductionList = new ArrayList<>();
        finCustomerMonthAccountBill.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.REDDASHED.getCode()});  // 已确认和已红冲
        finCustomerMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        List<FinCustomerMonthAccountBillKkInfo> finBookCustomerDeductionListFp = finCustomerMonthAccountBillMapper.selectDeductionItemListFp(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setHandleStatusList(null);
        finCustomerMonthAccountBill.setReceiptPaymentStatus(ConstantsFinance.RECEIPT_PAYMENT_STATUS_YDZ); // 已到账
        List<FinCustomerMonthAccountBillKkInfo> finBookCustomerDeductionListSk = finCustomerMonthAccountBillMapper.selectDeductionItemListSk(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        finCustomerMonthAccountBill.setReceiptPaymentStatus(null);
        finCustomerMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);  //已确认
        List<FinCustomerMonthAccountBillKkInfo> finBookCustomerDeductionListHd = finCustomerMonthAccountBillMapper.selectDeductionItemListHd(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setHandleStatus(null);
        if (finBookCustomerDeductionListFp != null) {
            finBookCustomerDeductionList.addAll(finBookCustomerDeductionListFp);
        }
        if (finBookCustomerDeductionListSk != null) {
            finBookCustomerDeductionList.addAll(finBookCustomerDeductionListSk);
        }
        if (finBookCustomerDeductionListHd != null) {
            finBookCustomerDeductionList.addAll(finBookCustomerDeductionListHd);
        }
        request.setDeductionList(finBookCustomerDeductionList);

        //本期调账:显示客户的收款的“月账单所属期间”等于月账单的”所属年月”的收款中类型是“调账”的调账明细
        List<FinCustomerMonthAccountBillTzInfo> finBookCustomerAdjustList = new ArrayList<>();
        finCustomerMonthAccountBill.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.REDDASHED.getCode()});  // 已确认和已红冲
        finCustomerMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        List<FinCustomerMonthAccountBillTzInfo> finBookCustomerAdjustListFp = finCustomerMonthAccountBillMapper.selectAdjustItemListFp(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setHandleStatusList(null);
        finCustomerMonthAccountBill.setReceiptPaymentStatus(ConstantsFinance.RECEIPT_PAYMENT_STATUS_YDZ); // 已到账
        List<FinCustomerMonthAccountBillTzInfo> finBookCustomerAdjustListSk = finCustomerMonthAccountBillMapper.selectAdjustItemListSk(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        finCustomerMonthAccountBill.setReceiptPaymentStatus(null);
        finCustomerMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);  //已确认
        List<FinCustomerMonthAccountBillTzInfo> finBookCustomerAdjustListHd = finCustomerMonthAccountBillMapper.selectAdjustItemListHd(finCustomerMonthAccountBill);
        finCustomerMonthAccountBill.setHandleStatus(null);
        if (finBookCustomerAdjustListFp != null) {
            finBookCustomerAdjustList.addAll(finBookCustomerAdjustListFp);
        }
        if (finBookCustomerAdjustListSk != null) {
            finBookCustomerAdjustList.addAll(finBookCustomerAdjustListSk);
        }
        if (finBookCustomerAdjustListHd != null) {
            finBookCustomerAdjustList.addAll(finBookCustomerAdjustListHd);
        }
        request.setAdjustList(finBookCustomerAdjustList);

        //押金:显示客户的退回状态不是“全部退回”的押金单的明细（支付）
        String[] returnStatusList = new String[]{ConstantsFinance.RETURN_STATUS_BFTH,ConstantsFinance.RETURN_STATUS_WTH};
        List<FinCustomerCashPledgeBillItem> finCustomerCashPledgeBillItemListZf = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList(
                new FinCustomerCashPledgeBillItem().setReturnStatusList(returnStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCustomerSid(finCustomerMonthAccountBill.getCustomerSid())
                        .setCompanySid(finCustomerMonthAccountBill.getCompanySid()));
        request.setCashPledgeListZf(finCustomerCashPledgeBillItemListZf);
        if (finCustomerCashPledgeBillItemListZf == null) {
            request.setCashPledgeListZf(new ArrayList<>());
        }

        //押金:显示客户的退回状态不是“全部退回”的押金单的明细（收取）
        List<FinCustomerCashPledgeBillItem> finCustomerCashPledgeBillItemListSq = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList(
                new FinCustomerCashPledgeBillItem().setReturnStatusList(returnStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCustomerSid(finCustomerMonthAccountBill.getCustomerSid())
                        .setCompanySid(finCustomerMonthAccountBill.getCompanySid()));
        request.setCashPledgeListSq(finCustomerCashPledgeBillItemListSq);
        if (finCustomerCashPledgeBillItemListSq == null) {
            request.setCashPledgeListSq(new ArrayList<>());
        }
        request.getCashPledgeListZf().addAll(request.getCashPledgeListSq());

        //暂押款:显示客户的释放状态不是“全部释放”的暂押款单的明细
        String[] unfreezeStatusList = new String[]{ConstantsFinance.UNFREEZE_STATUS_BFJD,ConstantsFinance.UNFREEZE_STATUS_WJD};
        List<FinCustomerFundsFreezeBillItem> finCustomerFundsFreezeBillItemList = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemList(
                new FinCustomerFundsFreezeBillItem().setUnfreezeStatusList(unfreezeStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_FREEZE_BZYK)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCustomerSid(finCustomerMonthAccountBill.getCustomerSid())
                        .setCompanySid(finCustomerMonthAccountBill.getCompanySid()));
        request.setFundsFreezeList(finCustomerFundsFreezeBillItemList);
        if (finCustomerFundsFreezeBillItemList == null) {
            request.setFundsFreezeList(new ArrayList<>());
        }
        return request;
    }

    /**
     * 计算账单总览金额
     *
     * @param finCustomerMonthAccountBill
     * @return 客户月对账单
     */
    @Override
    public FinCustomerMonthAccountBill calculationAmount(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        //本期到票:显示此客户的应收财务流水的“月账单所属期间”等于月账单的”所属年月”的应收财务流水的“应收金额”之和
        BigDecimal daopiao = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getInvoiceList())){
            daopiao = finCustomerMonthAccountBill.getInvoiceList().parallelStream().map(FinSaleInvoice::getTotalCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        //本期收款:显示收款财务流水中的“月账单所属期间”等于月账单的”所属年月”的收款流水的“收款金额”之和
        BigDecimal shoukuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getBookReceiptPaymentList())){
            shoukuan = finCustomerMonthAccountBill.getBookReceiptPaymentList().parallelStream().map(FinBookReceiptPayment::getCurrencyAmountTaxSk)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
        }
        //本期扣款:显示客户的收款的“月账单所属期间”等于月账单的”所属年月”的收款中类型是“扣款”的“金额”之和
        BigDecimal koukuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getDeductionList())){
            koukuan = finCustomerMonthAccountBill.getDeductionList().parallelStream().map(FinCustomerMonthAccountBillKkInfo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
        }
        //本期调账:显示客户的收款的“月账单所属期间”等于月账单的”所属年月”的收款中类型是“调账”的“金额”之和；
        BigDecimal tiaozhang = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getAdjustList())){
            tiaozhang = finCustomerMonthAccountBill.getAdjustList().parallelStream().map(FinCustomerMonthAccountBillTzInfo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
        }
        //上期余额
        BigDecimal yueQichu = BigDecimal.ZERO;
        String lastYearMonth = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date orgin = sdf.parse(finCustomerMonthAccountBill.getYearMonths());  //得到当前年月的Date型
            Calendar cal =Calendar.getInstance();
            cal.setTime(orgin);
            cal.add(Calendar.MONTH, -1);  //得到当前年月的上一个月
            Date last = cal.getTime();
            lastYearMonth = sdf.format(last); //得到当前年月上一个月的String型
        } catch (Exception e){
            throw new BaseException("系统未知错误，请联系管理员");
        }
        FinCustomerMonthAccountBill lastBill = null;
        try {
            lastBill = finCustomerMonthAccountBillMapper.selectOne(new QueryWrapper<FinCustomerMonthAccountBill>()
                    .lambda().eq(FinCustomerMonthAccountBill::getCustomerSid,finCustomerMonthAccountBill.getCustomerSid())
                    .eq(FinCustomerMonthAccountBill::getCompanySid,finCustomerMonthAccountBill.getCompanySid())
                    .eq(FinCustomerMonthAccountBill::getYearMonths,lastYearMonth));
        }catch (Exception e){
            throw new BaseException("汇总上期余额时出现问题，请联系管理员");
        }
        if (lastBill != null && ConstantsEms.CHECK_STATUS.equals(lastBill.getHandleStatus())){
            yueQichu = lastBill.getYueQimo();
        }
        //本期余额:等于：上期余额 + 本期到票 - 本期付款 + 本期扣款 + 本期调账
        BigDecimal yueQimo = BigDecimal.ZERO;
        yueQimo = yueQichu.add(daopiao).subtract(shoukuan);
        //押金:显示客户的退回状态不是“全部退回”的押金单的“押金金额-已退回金额”之和
        BigDecimal yajinZf = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getCashPledgeListZf())){
            yajinZf = finCustomerMonthAccountBill.getCashPledgeListZf().parallelStream().map(FinCustomerCashPledgeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            yajinZf = yajinZf.subtract(finCustomerMonthAccountBill.getCashPledgeListZf().parallelStream().map(FinCustomerCashPledgeBillItem::getCurrencyAmountYth)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }
        BigDecimal yajinSq = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getCashPledgeListSq())){
            yajinSq = finCustomerMonthAccountBill.getCashPledgeListSq().parallelStream().map(FinCustomerCashPledgeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            yajinSq = yajinSq.subtract(finCustomerMonthAccountBill.getCashPledgeListSq().parallelStream().map(FinCustomerCashPledgeBillItem::getCurrencyAmountYth)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }
        yajinZf = yajinZf.subtract(yajinSq.multiply(new BigDecimal("2")));
        //暂押款:显示客户的释放状态不是“全部释放”的暂押款单的“暂压金额-已释放金额”之和
        BigDecimal zanyakuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getFundsFreezeList())){
            zanyakuan = finCustomerMonthAccountBill.getFundsFreezeList().parallelStream().map(FinCustomerFundsFreezeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            zanyakuan = zanyakuan.subtract(finCustomerMonthAccountBill.getFundsFreezeList().parallelStream().map(FinCustomerFundsFreezeBillItem::getCurrencyAmountYsf)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }
        //实际结欠余额:等于：本期余额 + 押金 + 暂押款
        BigDecimal yueShijijieqian = BigDecimal.ZERO;
        yueShijijieqian = yueQimo .add(yajinZf).add(zanyakuan);
        //应收暂估:显示此客户档案中的客户编码的核销状态不是”全部核销“的应收暂估流水明细“金额- 已核销金额”之和
        BigDecimal yingfuzangu = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finCustomerMonthAccountBill.getBookReceiptEstimationList())){
            yingfuzangu = finCustomerMonthAccountBill.getBookReceiptEstimationList().parallelStream().map(FinBookReceiptEstimation::getCurrencyAmountTax).reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            yingfuzangu = yingfuzangu.subtract(finCustomerMonthAccountBill.getBookReceiptEstimationList().parallelStream().map(FinBookReceiptEstimation::getCurrencyAmountTaxYhx).reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }

        finCustomerMonthAccountBill.setYueQichu(yueQichu)  //期初余额/上期余额金额
                .setYueQimo(yueQimo)                    //本期余额/期末余额
                .setDaopiaoBenqi(daopiao)               //本期到票
                .setFukuanBenqi(BigDecimal.ZERO)        //本期付款
                .setShoukuanBenqi(shoukuan)             //本期收款
                .setCaigoudikouBenqi(BigDecimal.ZERO)   //本期采购抵扣
                .setKoukuanBenqi(koukuan)               //本期扣款
                .setTiaozhangBenqi(tiaozhang)           //本期调账
                .setYajin(yajinZf)                      //押金
                .setZanyakuan(zanyakuan)                //暂押款
                .setYueShijijieqian(yueShijijieqian)    //实际结欠余额金额
                .setYingfuzangu(yingfuzangu)            //应收暂估
                .setYingshouzangu(BigDecimal.ZERO);     //应收暂估

        //将账单总览信息也存到一张列表里，方便前端读取数据
        FinCustomerMonthAccountBillInfo info = new FinCustomerMonthAccountBillInfo();
        BeanCopyUtils.copyProperties(finCustomerMonthAccountBill, info);
        List<FinCustomerMonthAccountBillInfo> list = new ArrayList<>();
        list.add(info);
        finCustomerMonthAccountBill.setInfo(list);
        return finCustomerMonthAccountBill;
    }


    /**
     * 变更所属账期
     *
     * @param list
     * @return 客户台账
     */
    @Override
    public int changeYearMonth(FinCustomerMonthAccountBill list) {
        /** 验证目的账期是否存在且已确认 */
        FinCustomerMonthAccountBill to = finCustomerMonthAccountBillMapper.selectOne(new QueryWrapper<FinCustomerMonthAccountBill>()
                .lambda().eq(FinCustomerMonthAccountBill::getCustomerSid,list.getCustomerSid())
                .eq(FinCustomerMonthAccountBill::getCompanySid,list.getCompanySid())
                .eq(FinCustomerMonthAccountBill::getYearMonths,list.getYearMonths()));
        if (to != null ){
            if (ConstantsEms.CHECK_STATUS.equals(to.getHandleStatus()) || ConstantsEms.SUBMIT_STATUS.equals(to.getHandleStatus())){
                throw new BaseException("选择月份的月账单的处理状态为已确认或审批中，不允许变更账期");
            }
        }
        int row = 0;
        /** 本期到票 */
        if (FormType.SaleInvoice.getCode().equals(list.getFormType())){
            List<FinSaleInvoice> invoiceList = finSaleInvoiceMapper.selectList(new QueryWrapper<FinSaleInvoice>()
                    .lambda().in(FinSaleInvoice::getSaleInvoiceSid,list.getSidList()));
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            invoiceList.forEach(item->{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date orgin = sdf.parse(item.getMonthAccountPeriod());
                    Date toDate = sdf.parse(list.getYearMonths());
                    start.setTime(orgin);
                    end.setTime(toDate);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    if (Math.abs(result+month) > 1){
                        throw new BaseException("变更账期仅能选择当前月或当前月的前一个月或者下个月");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            LambdaUpdateWrapper<FinSaleInvoice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinSaleInvoice::getSaleInvoiceSid,list.getSidList()).set(FinSaleInvoice::getMonthAccountPeriod, list.getYearMonths());
            row = finSaleInvoiceMapper.update(null, updateWrapper);
        }
        /** 本期付款 */
        if (FormType.PayBill.getCode().equals(list.getFormType())){
            List<FinPayBill> payBillList = finPayBillMapper.selectList(new QueryWrapper<FinPayBill>()
                    .lambda().in(FinPayBill::getPayBillSid,list.getSidList()));
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            payBillList.forEach(item->{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date orgin = sdf.parse(item.getMonthAccountPeriod());
                    Date toDate = sdf.parse(list.getYearMonths());
                    start.setTime(orgin);
                    end.setTime(toDate);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    if (Math.abs(result+month) > 1){
                        throw new BaseException("变更账期仅能选择当前月或当前月的前一个月或者下个月");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPayBill::getPayBillSid,list.getSidList()).set(FinPayBill::getMonthAccountPeriod, list.getYearMonths());
            row = finPayBillMapper.update(null, updateWrapper);
        }
        /** 本期收款 */
        if (FormType.ReceivableBill.getCode().equals(list.getFormType())){
            List<FinReceivableBill> receivableBillList = finReceivableBillMapper.selectList(new QueryWrapper<FinReceivableBill>()
                    .lambda().in(FinReceivableBill::getReceivableBillSid,list.getSidList()));
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            receivableBillList.forEach(item->{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date orgin = sdf.parse(item.getMonthAccountPeriod());
                    Date toDate = sdf.parse(list.getYearMonths());
                    start.setTime(orgin);
                    end.setTime(toDate);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    if (Math.abs(result+month) > 1){
                        throw new BaseException("变更账期仅能选择当前月或当前月的前一个月或者下个月");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinReceivableBill::getReceivableBillSid,list.getSidList()).set(FinReceivableBill::getMonthAccountPeriod, list.getYearMonths());
            row = finReceivableBillMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 查询客户台账
     *
     * @param finCustomerMonthAccountBill
     * @return 客户台账
     */
    @Override
    public TableDataInfo selectReportList(FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        finCustomerMonthAccountBill.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        finCustomerMonthAccountBill.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        finCustomerMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        //待核销待付预付款
        finCustomerMonthAccountBill.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YSK);
        List<FinCustomerMonthAccountBillInfo> yushoukuanList = finCustomerMonthAccountBillMapper.getBookYSk(finCustomerMonthAccountBill);
        //待核销应付暂估
        List<FinCustomerMonthAccountBillInfo> yingshouzanguList = finCustomerMonthAccountBillMapper.getReceiptEstimation(finCustomerMonthAccountBill);
        //待核销应付款
        List<FinCustomerMonthAccountBillInfo> yingshoukuanList = finCustomerMonthAccountBillMapper.getAccountReceivable(finCustomerMonthAccountBill);
        //待核销扣款
        List< FinCustomerMonthAccountBillInfo> koukuanList = finCustomerMonthAccountBillMapper.getBookDeduction(finCustomerMonthAccountBill);
        //待核销调账
        List<FinCustomerMonthAccountBillInfo> tiaozhangList = finCustomerMonthAccountBillMapper.getBookAdjust(finCustomerMonthAccountBill);
        //待核销特殊付款
        finCustomerMonthAccountBill.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_TSSK);
        List<FinCustomerMonthAccountBillInfo> teshushoukuanList = finCustomerMonthAccountBillMapper.getBookTsSk(finCustomerMonthAccountBill);
        //押金（分组组合：公司+供应商+单据类型(收取，支付)）
        finCustomerMonthAccountBill.setDocumentTypeList(new String[]{ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ,ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF})
                .setReturnStatusList(new String[]{ConstantsFinance.RETURN_STATUS_WTH,ConstantsFinance.RETURN_STATUS_BFTH})
                .setHandleStatusList(new String[]{ConstantsEms.CHECK_STATUS});
        List<FinCustomerMonthAccountBillInfo> yajinList = finCustomerMonthAccountBillMapper.getCashPledge(finCustomerMonthAccountBill);
        //押金=押金收取待退回明细+押金收取退回中明细-押金支付待退回明细-押金支付退回中明细
        yajinList = yajinList.stream().collect(Collectors.toMap(FinCustomerMonthAccountBillInfo::getOneKey, a -> a, (o1,o2)-> {
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(o1.getDocumentType())){ o1.setYajin(o1.getYajin().subtract(o2.getYajin())); }
            else if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(o2.getDocumentType())){ o1.setYajin(o2.getYajin().subtract(o1.getYajin())); }
            return o1;
        })).values().stream().collect(Collectors.toList());
        //暂押款（分组组合：公司+供应商+单据类型(暂押款，被暂押款)）
        finCustomerMonthAccountBill.setDocumentTypeList(new String[]{ConstantsFinance.DOC_TYPE_FREEZE_BZYK,ConstantsFinance.DOC_TYPE_FREEZE_ZYK})
                .setUnfreezeStatusList(new String[]{ConstantsFinance.UNFREEZE_STATUS_WJD,ConstantsFinance.UNFREEZE_STATUS_BFJD})
                .setHandleStatusList(new String[]{ConstantsEms.CHECK_STATUS});
        List<FinCustomerMonthAccountBillInfo> zanyakuanList = finCustomerMonthAccountBillMapper.getFundsFreeze(finCustomerMonthAccountBill);
        //暂押款=暂押款待释放明细+暂押款释放中明细-被暂押款待释放明细-被暂押款释放中明细
        zanyakuanList = zanyakuanList.stream().collect(Collectors.toMap(FinCustomerMonthAccountBillInfo::getOneKey, a -> a, (o1,o2)-> {
            if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(o1.getDocumentType())){ o1.setYajin(o1.getZanyakuan().subtract(o2.getZanyakuan())); }
            else if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(o2.getDocumentType())){ o1.setYajin(o2.getZanyakuan().subtract(o1.getZanyakuan())); }
            return o1;
        })).values().stream().collect(Collectors.toList());
        Map<String,FinCustomerMonthAccountBillInfo> map = new HashMap<String,FinCustomerMonthAccountBillInfo>();
        if (CollectionUtils.isNotEmpty(yushoukuanList)){
            yushoukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYushoukuan(item.getYushoukuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yingshouzanguList)){
            yingshouzanguList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYingshouzangu(item.getYingshouzangu());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yingshoukuanList)){
            yingshoukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYingshoukuan(item.getYingshoukuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(koukuanList)){
            koukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setKoukuan(item.getKoukuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(tiaozhangList)){
            tiaozhangList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setTiaozhang(item.getTiaozhang());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(teshushoukuanList)){
            teshushoukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setTeshushoukuan(item.getTeshushoukuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yajinList)){
            yajinList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYajin(item.getYajin());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(zanyakuanList)){
            zanyakuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinCustomerMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setZanyakuan(item.getZanyakuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        List<FinCustomerMonthAccountBillInfo> response = map.values().stream().collect(Collectors.toList());
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(response.size());
        if (finCustomerMonthAccountBill.getPageNum() != null && finCustomerMonthAccountBill.getPageSize() != null){
            response = CommonUtil.startPage(response,finCustomerMonthAccountBill.getPageNum(),finCustomerMonthAccountBill.getPageSize());
        }
        tableDataInfo.setRows(response);
        if (response == null){
            tableDataInfo.setRows(Collections.EMPTY_LIST);
        }
        return tableDataInfo;
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<FinCustomerMonthAccountBill> responseList = new ArrayList<>();
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
            Map<String, String> yearMaps = yearDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> currencyDict = sysDictDataService.selectDictData("s_currency"); //币种
            Map<String, String> currencyMaps = currencyDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> currencyUnitDict = sysDictDataService.selectDictData("s_currency_unit"); //货币单位
            Map<String, String> currencyUnitMaps = currencyUnitDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            HashMap<String, String> map = new HashMap<>();
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
                String customerName = null;
                String customerCode = null;
                Long customerSid = null; //表：客户Sid
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
                 * 所属年月
                 */
                String yearMonth = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString(); //所属年月  (必填)
                if (StrUtil.isBlank(yearMonth)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("所属年月不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isYearMonth(yearMonth)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("所属年月格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        String month_s = yearMonth.substring(5, 7);
                        Calendar cal = Calendar.getInstance();
                        int currentMonth = cal.get(Calendar.MONTH)+1;
                        int month = Integer.parseInt(month_s);
                        if (!(currentMonth == month || currentMonth == month+1)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("所属年月必须为当月或上月，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            yearMonth = yearMonth.replace("/","-");
                        }
                    }
                }
                if (companySid != null && customerSid != null && yearMonth != null){
                    if (map.get(companySid+customerSid+yearMonth)==null){
                        map.put(companySid+customerSid+yearMonth,String.valueOf(num));
                        List<FinCustomerMonthAccountBill> list = finCustomerMonthAccountBillMapper.selectList(new QueryWrapper<FinCustomerMonthAccountBill>()
                                .lambda().eq(FinCustomerMonthAccountBill::getCompanySid,companySid)
                                .eq(FinCustomerMonthAccountBill::getCustomerSid,customerSid)
                                .eq(FinCustomerMonthAccountBill::getYearMonths,yearMonth));
                        if (CollectionUtil.isNotEmpty(list)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，已存在该月账单，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，已存在该月账单，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 *  上期余额
                 */
                String yueQichu_s = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString(); //金额  (必填)
                BigDecimal yueQichu = null;
                if (StrUtil.isBlank(yueQichu_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期期初余额金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yueQichu_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期期初余额金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yueQichu = new BigDecimal(yueQichu_s);
                    }
                }
                /**
                 *  本期到票
                 */
                String daopiaoBenqi_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString(); //金额  (必填)
                BigDecimal daopiaoBenqi = null;
                if (StrUtil.isBlank(daopiaoBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期到票金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(daopiaoBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期到票金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        daopiaoBenqi = new BigDecimal(daopiaoBenqi_s); //金额  (必填)
                    }
                }
                /**
                 *  本期收款
                 */
                String shoukuanBenqi_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString(); //金额  (必填)
                BigDecimal shoukuanBenqi = null;
                if (StrUtil.isBlank(shoukuanBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期收款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(shoukuanBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期收款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        shoukuanBenqi = new BigDecimal(shoukuanBenqi_s);//金额  (必填)
                        //不能小于0
                        if (BigDecimal.ZERO.compareTo(shoukuanBenqi) > 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("本期收款金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  本期采购抵扣
                 */
                String caigoudikouBenqi_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString(); //金额  (必填)
                BigDecimal caigoudikouBenqi = null;
                if (StrUtil.isBlank(caigoudikouBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期采购抵扣金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(caigoudikouBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期采购抵扣金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        caigoudikouBenqi = new BigDecimal(caigoudikouBenqi_s); //金额  (必填)
                    }
                }
                /**
                 *  本期扣款
                 */
                String koukuanBenqi_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString(); //金额  (必填)
                BigDecimal koukuanBenqi = null;
                if (StrUtil.isBlank(koukuanBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期扣款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(objects.get(7).toString(),11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期扣款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        koukuanBenqi = new BigDecimal(koukuanBenqi_s); //金额  (必填)
                        if (koukuanBenqi.compareTo(BigDecimal.ZERO) > 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("本期扣款金额不能大于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  本期调账
                 */
                String tiaozhangBenqi_s = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString(); //金额  (必填)
                BigDecimal tiaozhangBenqi = null;
                if (StrUtil.isBlank(tiaozhangBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期调账金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(tiaozhangBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期调账金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        tiaozhangBenqi = new BigDecimal(tiaozhangBenqi_s); //金额  (必填)
                    }
                }
                /**
                 *  本期余额
                 */
                String yueQimo_s = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString(); //金额  (必填)
                BigDecimal yueQimo = null;
                if (StrUtil.isBlank(yueQimo_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期期末余额金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yueQimo_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期期末余额金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yueQimo = new BigDecimal(yueQimo_s);
                    }
                }
                /**
                 *  押金
                 */
                String yajin_s = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal yajin = null;
                if (StrUtil.isBlank(yajin_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("押金金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yajin_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("押金金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yajin = new BigDecimal(yajin_s); //金额  (必填)
                    }

                }
                /**
                 *  暂押款
                 */
                String zanyakuan_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString(); //金额  (必填)
                BigDecimal zanyakuan = null;
                if (StrUtil.isBlank(zanyakuan_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("暂押款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(zanyakuan_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("暂押款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        zanyakuan = new BigDecimal(zanyakuan_s);
                        //不能小于0
                        if (BigDecimal.ZERO.compareTo(zanyakuan) > 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("暂押款金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  应收暂估
                 */
                String yingshouzangu_s = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString(); //金额  (必填)
                BigDecimal yingshouzangu = null;
                if (StrUtil.isBlank(yingshouzangu_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应收暂估金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yingshouzangu_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应收暂估金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yingshouzangu = new BigDecimal(yingshouzangu_s); //金额  (必填)
                    }
                }
                /**
                 *  本期付款
                 */
                String fukuanBenqi_s = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString(); //金额  (必填)
                BigDecimal fukuanBenqi = null;
                if (StrUtil.isNotBlank(fukuanBenqi_s)){
                    if (!JudgeFormat.isValidDouble(fukuanBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期付款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        fukuanBenqi = new BigDecimal(fukuanBenqi_s);
                        //不能小于0
                        if (BigDecimal.ZERO.compareTo(fukuanBenqi) > 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("本期付款金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  应付暂估
                 */
                String yingfuzangu_s = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString(); //金额  (必填)
                BigDecimal yingfuzangu = null;
                if (StrUtil.isNotBlank(yingfuzangu_s)){
                    if (!JudgeFormat.isValidDouble(yingfuzangu_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应付暂估金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yingfuzangu = new BigDecimal(yingfuzangu_s);
                    }
                }
                /**
                 * 备注
                 */
                String remark =  objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString(); //备注
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtils.isEmpty(errMsgList)){
                    FinCustomerMonthAccountBill finCustomerMonthAccountBill = new FinCustomerMonthAccountBill();
                    finCustomerMonthAccountBill.setYueQichu(yueQichu)  //期初余额/上期余额金额
                            .setYueQimo(yueQimo)            //本期余额/期末余额
                            .setDaopiaoBenqi(daopiaoBenqi)               //本期到票
                            .setFukuanBenqi(fukuanBenqi)                 //本期付款
                            .setShoukuanBenqi(shoukuanBenqi)      //本期收款
                            .setCaigoudikouBenqi(caigoudikouBenqi) //本期采购抵扣
                            .setKoukuanBenqi(koukuanBenqi)               //本期扣款
                            .setTiaozhangBenqi(tiaozhangBenqi)           //本期调账
                            .setYajin(yajin)                      //押金
                            .setZanyakuan(zanyakuan)                //暂押款
                            .setYueShijijieqian(BigDecimal.ZERO)    //实际结欠余额金额
                            .setYingfuzangu(yingfuzangu)            //应付暂估
                            .setYingshouzangu(yingshouzangu);     //应收暂估
                    finCustomerMonthAccountBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setYearMonths(yearMonth);
                    finCustomerMonthAccountBill.setCompanySid(companySid).setCustomerSid(customerSid).setCustomerCode(customerCode).setCompanyCode(companyCode)
                            .setCustomerShortName(customerShortName).setCompanyShortName(companyShortName).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    finCustomerMonthAccountBill.setCompanyName(companyName).setCustomerName(customerName).setRemark(remark);
                    responseList.add(finCustomerMonthAccountBill);
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
    public int addForm(List<FinCustomerMonthAccountBill> list) {
        list.forEach(item->{
            finCustomerMonthAccountBillMapper.insert(item);
        });
        return list.size();
    }

}
