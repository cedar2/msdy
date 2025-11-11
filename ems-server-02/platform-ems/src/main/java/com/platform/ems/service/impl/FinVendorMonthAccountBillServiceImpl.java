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
import com.platform.ems.service.IFinVendorMonthAccountBillService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商月对账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinVendorMonthAccountBillServiceImpl extends ServiceImpl<FinVendorMonthAccountBillMapper, FinVendorMonthAccountBill> implements IFinVendorMonthAccountBillService {
    @Autowired
    private FinVendorMonthAccountBillMapper finVendorMonthAccountBillMapper;
    @Autowired
    private FinVendorMonthAccountBillAttachMapper finVendorMonthAccountBillAttachMapper;
    //应付暂估
    @Autowired
    private FinBookPaymentEstimationMapper finBookPaymentEstimationMapper;
    //预付款
    @Autowired
    private FinRecordAdvancePaymentMapper finRecordAdvancePaymentMapper;
    //发票
    @Autowired
    private FinPurchaseInvoiceMapper finPurchaseInvoiceMapper;
    //应付
    @Autowired
    private FinBookAccountPayableMapper finBookAccountPayableMapper;
    //付款
    @Autowired
    private FinBookPaymentMapper finBookPaymentMapper;
    //付款明细
    @Autowired
    private FinBookPaymentItemMapper finBookPaymentItemMapper;
    //扣款
    @Autowired
    private FinBookVendorDeductionMapper finBookVendorDeductionMapper;
    //调账
    @Autowired
    private FinBookVendorAccountAdjustMapper finBookVendorAccountAdjustMapper;
    //押金
    @Autowired
    private FinVendorCashPledgeBillItemMapper finVendorCashPledgeBillItemMapper;
    //暂押款
    @Autowired
    private FinVendorFundsFreezeBillItemMapper finVendorFundsFreezeBillItemMapper;
    //付款单
    @Autowired
    private FinPayBillMapper finPayBillMapper;
    //收款单
    @Autowired
    private FinReceivableBillMapper finReceivableBillMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商月对账单";

    /**
     * 查询供应商月对账单
     *
     * @param vendorMonthAccountBillSid 供应商月对账单ID
     * @return 供应商月对账单
     */
    @Override
    public FinVendorMonthAccountBill selectFinVendorMonthAccountBillById(Long vendorMonthAccountBillSid) {
        FinVendorMonthAccountBill finVendorMonthAccountBill = finVendorMonthAccountBillMapper.selectFinVendorMonthAccountBillById(vendorMonthAccountBillSid);
        //总览
        FinVendorMonthAccountBillInfo info = new FinVendorMonthAccountBillInfo();
        BeanCopyUtils.copyProperties(finVendorMonthAccountBill, info);
        List<FinVendorMonthAccountBillInfo> list = new ArrayList<>();
        list.add(info);
        finVendorMonthAccountBill = selectItemList(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setInfo(list);
        //附件
        List<FinVendorMonthAccountBillAttach> finVendorMonthAccountBillAttachList = finVendorMonthAccountBillAttachMapper.selectList(new QueryWrapper<FinVendorMonthAccountBillAttach>()
                .lambda().eq(FinVendorMonthAccountBillAttach::getVendorMonthAccountBillSid, vendorMonthAccountBillSid));
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBillAttachList)) {
            finVendorMonthAccountBill.setAttachmentList(finVendorMonthAccountBillAttachList);
        }
        MongodbUtil.find(finVendorMonthAccountBill);
        return finVendorMonthAccountBill;
    }

    /**
     * 查询供应商月对账单列表
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 供应商月对账单
     */
    @Override
    public List<FinVendorMonthAccountBill> selectFinVendorMonthAccountBillList(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        return finVendorMonthAccountBillMapper.selectFinVendorMonthAccountBillList(finVendorMonthAccountBill);
    }

    /**
     * 新增供应商月对账单
     * 需要注意编码重复校验
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorMonthAccountBill(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        List<FinVendorMonthAccountBill> one = finVendorMonthAccountBillMapper.selectList(new QueryWrapper<FinVendorMonthAccountBill>()
                .lambda().eq(FinVendorMonthAccountBill::getVendorSid,finVendorMonthAccountBill.getVendorSid())
                .eq(FinVendorMonthAccountBill::getCompanySid,finVendorMonthAccountBill.getCompanySid())
                .eq(FinVendorMonthAccountBill::getYearMonths,finVendorMonthAccountBill.getYearMonths()));
        if (CollectionUtil.isNotEmpty(one)){
            throw new BaseException("该月账单已存在，请核实");
        }
        //设置确认信息，校验
        setConfirmedInfo(finVendorMonthAccountBill);
        int row = finVendorMonthAccountBillMapper.insert(finVendorMonthAccountBill);
        if (row > 0) {
            //插入子表，附件表
            insertChild(finVendorMonthAccountBill.getAttachmentList(), finVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //待办通知
            if (ConstantsEms.SAVA_STATUS.equals(finVendorMonthAccountBill.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_vendor_month_account_bill")
                        .setDocumentSid(finVendorMonthAccountBill.getVendorMonthAccountBillSid());
                sysTodoTask.setTitle("供应商月对账单: " + finVendorMonthAccountBill.getVendorMonthAccountBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finVendorMonthAccountBill.getVendorMonthAccountBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商月对账单
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorMonthAccountBill(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(finVendorMonthAccountBill);
        FinVendorMonthAccountBill response = finVendorMonthAccountBillMapper.selectFinVendorMonthAccountBillById(finVendorMonthAccountBill.getVendorMonthAccountBillSid());
        int row = finVendorMonthAccountBillMapper.updateById(finVendorMonthAccountBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(finVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入子表，附件表
            insertChild(finVendorMonthAccountBill.getAttachmentList(), finVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorMonthAccountBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getDocumentSid, finVendorMonthAccountBill.getVendorMonthAccountBillSid()));
            }
            //插入日志
            MongodbUtil.insertUserLog(finVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.UPDATE.getValue(), response, finVendorMonthAccountBill, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商月对账单
     *
     * @param finVendorMonthAccountBill 供应商月对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorMonthAccountBill(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(finVendorMonthAccountBill);
        FinVendorMonthAccountBill response = finVendorMonthAccountBillMapper.selectFinVendorMonthAccountBillById(finVendorMonthAccountBill.getVendorMonthAccountBillSid());
        int row = finVendorMonthAccountBillMapper.updateAllById(finVendorMonthAccountBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(finVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入子表，附件表
            insertChild(finVendorMonthAccountBill.getAttachmentList(), finVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入日志
            MongodbUtil.insertUserLog(finVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.CHANGE.getValue(), response, finVendorMonthAccountBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商月对账单
     *
     * @param vendorMonthAccountBillSids 需要删除的供应商月对账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorMonthAccountBillByIds(List<Long> vendorMonthAccountBillSids) {
        int i = finVendorMonthAccountBillMapper.deleteBatchIds(vendorMonthAccountBillSids);
        if (i > 0) {
            vendorMonthAccountBillSids.forEach(sid -> {
                //删除明细
                deleteItem(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .in(SysTodoTask::getDocumentSid, vendorMonthAccountBillSids));
        }
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finVendorMonthAccountBill
     * @return
     */
    @Override
    public int check(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        int row = 0;
        Long[] sids = finVendorMonthAccountBill.getVendorMonthAccountBillSidList();
        if (sids != null && sids.length > 0) {
            row = finVendorMonthAccountBillMapper.update(null, new UpdateWrapper<FinVendorMonthAccountBill>().lambda().set(FinVendorMonthAccountBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(FinVendorMonthAccountBill::getVendorMonthAccountBillSid, sids));
            for (Long id : sids) {
                FinVendorMonthAccountBill bill = finVendorMonthAccountBillMapper.selectById(id);
                if (bill != null) {
                    selectInvoiceAndPayBillList(bill, new FinVendorMonthAccountBill().setVendorSid(bill.getVendorSid()).setCompanySid(bill.getCompanySid())
                            .setYearMonths(bill.getYearMonths()));
                    updateIsFinanceVerify(bill);
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorMonthAccountBill.getHandleStatus())){
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
    public FinVendorMonthAccountBill setConfirmedInfo(FinVendorMonthAccountBill entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
            updateIsFinanceVerify(entity);
        }
        return entity;
    }

    private void updateIsFinanceVerify(FinVendorMonthAccountBill entity) {
        if (CollectionUtils.isNotEmpty(entity.getPayBillList())) {
            List<Long> sidList = entity.getPayBillList().stream().map(FinPayBill::getPayBillSid).collect(Collectors.toList());
            finPayBillMapper.update(null, new UpdateWrapper<FinPayBill>().lambda()
                    .set(FinPayBill::getIsFinanceVerify, ConstantsEms.YES).in(FinPayBill::getPayBillSid, sidList));
        }
        if (CollectionUtils.isNotEmpty(entity.getInvoiceList())) {
            List<Long> sidList = entity.getInvoiceList().stream().map(FinPurchaseInvoice::getPurchaseInvoiceSid).collect(Collectors.toList());
            finPurchaseInvoiceMapper.update(null, new UpdateWrapper<FinPurchaseInvoice>().lambda()
                    .set(FinPurchaseInvoice::getIsFinanceVerify, ConstantsEms.YES).in(FinPurchaseInvoice::getPurchaseInvoiceSid, sidList));
        }
    }

    /**
     * 删除子表
     */
    public void deleteItem(Long sid) {
        //附件表
        QueryWrapper<FinVendorMonthAccountBillAttach> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("vendor_month_account_bill_sid", sid);
        finVendorMonthAccountBillAttachMapper.delete(atmWrapper);
    }

    /**
     * 添加子表
     *
     * @param atmList
     * @param sid
     */
    public void insertChild(List<FinVendorMonthAccountBillAttach> atmList, Long sid) {
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setVendorMonthAccountBillSid(sid);
            });
            finVendorMonthAccountBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 供应商月对账单新建入口
     *
     * @param finVendorMonthAccountBill
     * @return 供应商月对账单
     */
    @Override
    public FinVendorMonthAccountBill entrance(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        //查询供应商月对账单明细信息
        finVendorMonthAccountBill = selectItemList(finVendorMonthAccountBill);
        //计算账单总览金额
        finVendorMonthAccountBill = calculationAmount(finVendorMonthAccountBill);
        return finVendorMonthAccountBill;
    }

    /**
     * 查询供应商月对账单明细信息 : 本期到票(发票)  +  本期付款
     *
     * @param finVendorMonthAccountBill
     * @return 供应商月对账单
     */
    private void selectInvoiceAndPayBillList(FinVendorMonthAccountBill request, FinVendorMonthAccountBill finVendorMonthAccountBill) {
        //本期到票(发票):显示此供应商的应付财务流水的“月账单所属期间”等于月账单的”所属年月”的应付财务流水的明细
        FinPurchaseInvoice invoice = new FinPurchaseInvoice().setVendorSid(finVendorMonthAccountBill.getVendorSid()).setCompanySid(finVendorMonthAccountBill.getCompanySid())
                .setMonthAccountPeriod(finVendorMonthAccountBill.getYearMonths()).setIsFinanceVerify(ConstantsEms.NO);
        invoice.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.REDDASHED.getCode()});
        List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceList(invoice);
        request.setInvoiceList(invoiceList);
        if (invoiceList == null) {
            request.setInvoiceList(new ArrayList<>());
        }
        // 本期付款
        FinPayBill payBill = new FinPayBill().setCompanySid(finVendorMonthAccountBill.getCompanySid()).setVendorSid(finVendorMonthAccountBill.getVendorSid())
                .setPaymentStatus(ConstantsFinance.PAYMENT_STATUS_YZF).setIsFinanceVerify(ConstantsEms.NO)
                .setMonthAccountPeriod(finVendorMonthAccountBill.getYearMonths());
        List<FinPayBill> billList = finPayBillMapper.selectFinPayBillList(payBill);
        request.setPayBillList(billList);
        if (billList == null) {
            request.setPayBillList(new ArrayList<>());
        }
    }

    /**
     * 查询供应商月对账单明细信息
     *
     * @param finVendorMonthAccountBill
     * @return 供应商月对账单
     */
    @Override
    public FinVendorMonthAccountBill selectItemList(FinVendorMonthAccountBill request) {
        FinVendorMonthAccountBill finVendorMonthAccountBill = new FinVendorMonthAccountBill();
        finVendorMonthAccountBill.setVendorSid(request.getVendorSid()).setCompanySid(request.getCompanySid())
                .setYearMonths(request.getYearMonths());

        //应付暂估:显示此供应商的核销状态不是”全部核销“的应付暂估流水明细
        String[] clearStatus = new String[]{ConstantsFinance.CLEAR_STATUS_BFHX,ConstantsFinance.CLEAR_STATUS_WHX};
        FinBookPaymentEstimation estimation = new FinBookPaymentEstimation().setVendorSid(finVendorMonthAccountBill.getVendorSid()).setCompanySid(finVendorMonthAccountBill.getCompanySid())
                .setClearStatusMoneyList(clearStatus);
        List<FinBookPaymentEstimation> finBookPaymentEstimationList = finBookPaymentEstimationMapper.getReportForm(estimation);
        request.setBookPaymentEstimationList(finBookPaymentEstimationList);
        if (finBookPaymentEstimationList == null) {
            request.setBookPaymentEstimationList(new ArrayList<>());
        }
        //本期到票(发票)  +  付款
        selectInvoiceAndPayBillList(request, finVendorMonthAccountBill);

        //本期扣款:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“扣款”的扣款明细
        List<FinVendorMonthAccountBillKkInfo> finBookVendorDeductionList = new ArrayList<>();
        finVendorMonthAccountBill.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.REDDASHED.getCode()});  // 已确认和已红冲
        finVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        List<FinVendorMonthAccountBillKkInfo> finBookVendorDeductionListFp = finVendorMonthAccountBillMapper.selectDeductionItemListFp(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setHandleStatusList(null);
        finVendorMonthAccountBill.setPaymentStatus(ConstantsFinance.PAYMENT_STATUS_YZF); // 已支付
        List<FinVendorMonthAccountBillKkInfo> finBookVendorDeductionListFk = finVendorMonthAccountBillMapper.selectDeductionItemListFk(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        finVendorMonthAccountBill.setPaymentStatus(null);
        finVendorMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);  //已确认
        List<FinVendorMonthAccountBillKkInfo> finBookVendorDeductionListHd = finVendorMonthAccountBillMapper.selectDeductionItemListHd(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setHandleStatus(null);
        if (finBookVendorDeductionListFp != null) {
            finBookVendorDeductionList.addAll(finBookVendorDeductionListFp);
        }
        if (finBookVendorDeductionListFk != null) {
            finBookVendorDeductionList.addAll(finBookVendorDeductionListFk);
        }
        if (finBookVendorDeductionListHd != null) {
            finBookVendorDeductionList.addAll(finBookVendorDeductionListHd);
        }
        request.setDeductionList(finBookVendorDeductionList);

        //本期调账:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“调账”的调账明细
        List<FinVendorMonthAccountBillTzInfo> finBookVendorAdjustList = new ArrayList<>();
        finVendorMonthAccountBill.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.REDDASHED.getCode()});  // 已确认和已红冲
        finVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        List<FinVendorMonthAccountBillTzInfo> finBookVendorAdjustListFp = finVendorMonthAccountBillMapper.selectAdjustItemListFp(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setHandleStatusList(null);
        finVendorMonthAccountBill.setPaymentStatus(ConstantsFinance.PAYMENT_STATUS_YZF); // 已支付
        List<FinVendorMonthAccountBillTzInfo> finBookVendorAdjustListFk = finVendorMonthAccountBillMapper.selectAdjustItemListFk(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        finVendorMonthAccountBill.setPaymentStatus(null);
        finVendorMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);  //已确认
        List<FinVendorMonthAccountBillTzInfo> finBookVendorAdjustListHd = finVendorMonthAccountBillMapper.selectAdjustItemListHd(finVendorMonthAccountBill);
        finVendorMonthAccountBill.setHandleStatus(null);
        if (finBookVendorAdjustListFp != null) {
            finBookVendorAdjustList.addAll(finBookVendorAdjustListFp);
        }
        if (finBookVendorAdjustListFk != null) {
            finBookVendorAdjustList.addAll(finBookVendorAdjustListFk);
        }
        if (finBookVendorAdjustListHd != null) {
            finBookVendorAdjustList.addAll(finBookVendorAdjustListHd);
        }
        request.setAdjustList(finBookVendorAdjustList);

        String[] returnStatusList = new String[]{ConstantsFinance.RETURN_STATUS_BFTH,ConstantsFinance.RETURN_STATUS_WTH};
        //押金:显示供应商的退回状态不是“全部退回”的押金单的明细（支付）
        List<FinVendorCashPledgeBillItem> finVendorCashPledgeBillItemListZf = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                new FinVendorCashPledgeBillItem().setReturnStatusList(returnStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(finVendorMonthAccountBill.getVendorSid())
                        .setCompanySid(finVendorMonthAccountBill.getCompanySid()));
        request.setCashPledgeListZf(finVendorCashPledgeBillItemListZf);
        if (finVendorCashPledgeBillItemListZf == null) {
            request.setCashPledgeListZf(new ArrayList<>());
        }

        //押金:显示供应商的退回状态不是“全部退回”的押金单的明细（收取）
        List<FinVendorCashPledgeBillItem> finVendorCashPledgeBillItemListSq = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                new FinVendorCashPledgeBillItem().setReturnStatusList(returnStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(finVendorMonthAccountBill.getVendorSid())
                        .setCompanySid(finVendorMonthAccountBill.getCompanySid()));
        request.setCashPledgeListSq(finVendorCashPledgeBillItemListSq);
        if (finVendorCashPledgeBillItemListSq == null) {
            request.setCashPledgeListSq(new ArrayList<>());
        }
        request.getCashPledgeListSq().addAll(request.getCashPledgeListZf());

        //暂押款:显示供应商的释放状态不是“全部释放”的暂押款单的明细
        String[] unfreezeStatusList = new String[]{ConstantsFinance.UNFREEZE_STATUS_BFJD,ConstantsFinance.UNFREEZE_STATUS_WJD};
        List<FinVendorFundsFreezeBillItem> finVendorFundsFreezeBillItemList = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(
                new FinVendorFundsFreezeBillItem().setUnfreezeStatusList(unfreezeStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_FREEZE_ZYK)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(finVendorMonthAccountBill.getVendorSid())
                        .setCompanySid(finVendorMonthAccountBill.getCompanySid()));
        request.setFundsFreezeList(finVendorFundsFreezeBillItemList);
        if (finVendorFundsFreezeBillItemList == null) {
            request.setFundsFreezeList(new ArrayList<>());
        }
        return request;
    }

    /**
     * 计算账单总览金额
     *
     * @param finVendorMonthAccountBill
     * @return 供应商月对账单
     */
    @Override
    public FinVendorMonthAccountBill calculationAmount(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        //本期到票:显示此供应商的应付财务流水的“月账单所属期间”等于月账单的”所属年月”的应付财务流水的“应付金额”之和
        BigDecimal daopiao = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getInvoiceList())){
            daopiao = finVendorMonthAccountBill.getInvoiceList().parallelStream().map(FinPurchaseInvoice::getTotalCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        //本期付款:显示付款财务流水中的“月账单所属期间”等于月账单的”所属年月”的付款流水的“付款金额”之和
        BigDecimal fukuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getPayBillList())){
            fukuan = finVendorMonthAccountBill.getPayBillList().parallelStream().map(FinPayBill::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
        }
        //本期扣款:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“扣款”的“金额”之和
        BigDecimal koukuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getDeductionList())){
            koukuan = finVendorMonthAccountBill.getDeductionList().parallelStream().map(FinVendorMonthAccountBillKkInfo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
        }
        //本期调账:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“调账”的“金额”之和；
        BigDecimal tiaozhang = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getAdjustList())){
            tiaozhang = finVendorMonthAccountBill.getAdjustList().parallelStream().map(FinVendorMonthAccountBillTzInfo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
        }
        //上期余额
        BigDecimal yueQichu = BigDecimal.ZERO;
        String lastYearMonth = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date orgin = sdf.parse(finVendorMonthAccountBill.getYearMonths());  //得到当前年月的Date型
            Calendar cal =Calendar.getInstance();
            cal.setTime(orgin);
            cal.add(Calendar.MONTH, -1);  //得到当前年月的上一个月
            Date last = cal.getTime();
            lastYearMonth = sdf.format(last); //得到当前年月上一个月的String型
        } catch (Exception e){
            throw new BaseException("系统未知错误，请联系管理员");
        }
        FinVendorMonthAccountBill lastBill = null;
        try {
            lastBill = finVendorMonthAccountBillMapper.selectOne(new QueryWrapper<FinVendorMonthAccountBill>()
                    .lambda().eq(FinVendorMonthAccountBill::getVendorSid,finVendorMonthAccountBill.getVendorSid())
                    .eq(FinVendorMonthAccountBill::getCompanySid,finVendorMonthAccountBill.getCompanySid())
                    .eq(FinVendorMonthAccountBill::getYearMonths,lastYearMonth));
        }catch (Exception e) {
            throw new BaseException("汇总上期余额时出现问题，请联系管理员");
        }
        if (lastBill != null && ConstantsEms.CHECK_STATUS.equals(lastBill.getHandleStatus())){
            yueQichu = lastBill.getYueQimo();
        }

        //本期余额:等于：上期余额 + 本期到票 - 本期付款
        BigDecimal yueQimo = BigDecimal.ZERO;
        yueQimo = yueQichu.add(daopiao).subtract(fukuan);

        //押金:显示供应商的退回状态不是“全部退回”的押金单的“押金金额-已退回金额”之和
        BigDecimal yajinSq = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getCashPledgeListSq())){
            yajinSq = finVendorMonthAccountBill.getCashPledgeListSq().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            yajinSq = yajinSq.subtract(finVendorMonthAccountBill.getCashPledgeListSq().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmountYth)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }
        BigDecimal yajinZf = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getCashPledgeListZf())){
            yajinZf = finVendorMonthAccountBill.getCashPledgeListZf().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            yajinZf = yajinZf.subtract(finVendorMonthAccountBill.getCashPledgeListZf().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmountYth)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }
        yajinSq = yajinSq.subtract(yajinZf.multiply(new BigDecimal("2")));

        //暂押款:显示供应商的释放状态不是“全部释放”的暂押款单的“暂压金额-已释放金额”之和
        BigDecimal zanyakuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getFundsFreezeList())){
            zanyakuan = finVendorMonthAccountBill.getFundsFreezeList().parallelStream().map(FinVendorFundsFreezeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            zanyakuan = zanyakuan.subtract(finVendorMonthAccountBill.getFundsFreezeList().parallelStream().map(FinVendorFundsFreezeBillItem::getCurrencyAmountYsf)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }

        //实际结欠余额:等于：本期余额 + 押金 + 暂押款
        BigDecimal yueShijijieqian = BigDecimal.ZERO;
        yueShijijieqian = yueQimo.add(yajinSq).add(zanyakuan);

        //应收暂估:显示此供应商档案中的客户编码的核销状态不是”全部核销“的应收暂估流水明细“金额- 已核销金额”之和
        BigDecimal yingfuzangu = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(finVendorMonthAccountBill.getBookPaymentEstimationList())){
            yingfuzangu = finVendorMonthAccountBill.getBookPaymentEstimationList().parallelStream().map(FinBookPaymentEstimation::getCurrencyAmountTax).reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            yingfuzangu = yingfuzangu.subtract(finVendorMonthAccountBill.getBookPaymentEstimationList().parallelStream().map(FinBookPaymentEstimation::getCurrencyAmountTaxYhx).reduce(BigDecimal.ZERO,BigDecimalSum::sum));
        }

        finVendorMonthAccountBill.setYueQichu(yueQichu)  //期初余额/上期余额金额
                .setYueQimo(yueQimo)                    //本期余额/期末余额
                .setDaopiaoBenqi(daopiao)               //本期到票
                .setFukuanBenqi(fukuan)                 //本期付款
                .setShoukuanBenqi(BigDecimal.ZERO)      //本期收款
                .setXiaoshoudikouBenqi(BigDecimal.ZERO) //本期销售抵扣
                .setKoukuanBenqi(koukuan)               //本期扣款
                .setTiaozhangBenqi(tiaozhang)           //本期调账
                .setYajin(yajinSq)                      //押金
                .setZanyakuan(zanyakuan)                //暂押款
                .setYueShijijieqian(yueShijijieqian)    //实际结欠余额金额
                .setYingfuzangu(yingfuzangu)            //应付暂估
                .setYingshouzangu(BigDecimal.ZERO);     //应收暂估

        //将账单总览信息也存到一张列表里，方便前端读取数据
        FinVendorMonthAccountBillInfo info = new FinVendorMonthAccountBillInfo();
        BeanCopyUtils.copyProperties(finVendorMonthAccountBill, info);
        List<FinVendorMonthAccountBillInfo> list = new ArrayList<>();
        list.add(info);
        finVendorMonthAccountBill.setInfo(list);
        return finVendorMonthAccountBill;
    }



    /**
     * 变更所属账期
     *
     * @param finVendorMonthAccountBill
     * @return 供应商台账
     */
    @Override
    public int changeYearMonth(FinVendorMonthAccountBill list) {
        /** 验证目的账期是否存在且已确认 */
        FinVendorMonthAccountBill to = finVendorMonthAccountBillMapper.selectOne(new QueryWrapper<FinVendorMonthAccountBill>()
                .lambda().eq(FinVendorMonthAccountBill::getVendorSid,list.getVendorSid())
                .eq(FinVendorMonthAccountBill::getCompanySid,list.getCompanySid())
                .eq(FinVendorMonthAccountBill::getYearMonths,list.getYearMonths()));
        if (to != null ){
            if (ConstantsEms.CHECK_STATUS.equals(to.getHandleStatus()) || ConstantsEms.SUBMIT_STATUS.equals(to.getHandleStatus())){
                throw new BaseException("选择月份的月账单的处理状态为已确认或审批中，不允许变更账期");
            }
        }
        int row = 0;
        /** 本期到票 */
        if (FormType.PurchaseInvoice.getCode().equals(list.getFormType())){
            List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectList(new QueryWrapper<FinPurchaseInvoice>()
                    .lambda().in(FinPurchaseInvoice::getPurchaseInvoiceSid,list.getSidList()));
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
            LambdaUpdateWrapper<FinPurchaseInvoice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPurchaseInvoice::getPurchaseInvoiceSid,list.getSidList()).set(FinPurchaseInvoice::getMonthAccountPeriod, list.getYearMonths());
            row = finPurchaseInvoiceMapper.update(null, updateWrapper);
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
     * 查询供应商台账
     *
     * @param finVendorMonthAccountBill
     * @return 供应商台账
     */
    @Override
    public TableDataInfo selectReportList(FinVendorMonthAccountBill finVendorMonthAccountBill) {
        finVendorMonthAccountBill.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        finVendorMonthAccountBill.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        finVendorMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        //待核销待付预付款
        List<FinVendorMonthAccountBillInfo> yufukuanList = new ArrayList<>();
        finVendorMonthAccountBill.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YFK);
        yufukuanList = finVendorMonthAccountBillMapper.getBookYFk(finVendorMonthAccountBill);
        //待核销应付暂估
        List<FinVendorMonthAccountBillInfo> yingfuzanguList = new ArrayList<>();
        yingfuzanguList = finVendorMonthAccountBillMapper.getPaymentEstimation(finVendorMonthAccountBill);
        //待核销应付款
        List<FinVendorMonthAccountBillInfo> yingfukuanList = new ArrayList<>();
        yingfukuanList = finVendorMonthAccountBillMapper.getAccountPayable(finVendorMonthAccountBill);
        //待核销扣款
        List<FinVendorMonthAccountBillInfo> koukuanList = new ArrayList<>();
        koukuanList = finVendorMonthAccountBillMapper.getBookDeduction(finVendorMonthAccountBill);
        //待核销调账
        List<FinVendorMonthAccountBillInfo> tiaozhangList = new ArrayList<>();
        tiaozhangList = finVendorMonthAccountBillMapper.getBookAdjust(finVendorMonthAccountBill);
        //待核销特殊付款
        List<FinVendorMonthAccountBillInfo> teshufukuanList = new ArrayList<>();
        finVendorMonthAccountBill.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_TSFK);
        teshufukuanList = finVendorMonthAccountBillMapper.getBookTsFk(finVendorMonthAccountBill);
        //押金（分组组合：公司+供应商+单据类型(收取，支付)）
        List<FinVendorMonthAccountBillInfo> yajinList = new ArrayList<>();
        finVendorMonthAccountBill.setDocumentTypeList(new String[]{ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ,ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF})
                .setReturnStatusList(new String[]{ConstantsFinance.RETURN_STATUS_WTH,ConstantsFinance.RETURN_STATUS_BFTH})
                .setHandleStatusList(new String[]{ConstantsEms.CHECK_STATUS});
        yajinList = finVendorMonthAccountBillMapper.getCashPledge(finVendorMonthAccountBill);
        //押金=押金收取待退回明细+押金收取退回中明细-押金支付待退回明细-押金支付退回中明细
        yajinList = yajinList.stream().collect(Collectors.toMap(FinVendorMonthAccountBillInfo::getOneKey, a -> a, (o1,o2)-> {
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(o1.getDocumentType())){ o1.setYajin(o1.getYajin().subtract(o2.getYajin())); }
            else if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(o2.getDocumentType())){ o1.setYajin(o2.getYajin().subtract(o1.getYajin())); }
            return o1;
        })).values().stream().collect(Collectors.toList());
        //暂押款（分组组合：公司+供应商+单据类型(暂押款，被暂押款)）
        List<FinVendorMonthAccountBillInfo> zanyakuanList = new ArrayList<>();
        finVendorMonthAccountBill.setDocumentTypeList(new String[]{ConstantsFinance.DOC_TYPE_FREEZE_ZYK,ConstantsFinance.DOC_TYPE_FREEZE_BZYK})
                .setUnfreezeStatusList(new String[]{ConstantsFinance.UNFREEZE_STATUS_WJD,ConstantsFinance.UNFREEZE_STATUS_BFJD})
                .setHandleStatusList(new String[]{ConstantsEms.CHECK_STATUS});
        zanyakuanList = finVendorMonthAccountBillMapper.getFundsFreeze(finVendorMonthAccountBill);
        //暂押款=暂押款待释放明细+暂押款释放中明细-被暂押款待释放明细-被暂押款释放中明细
        zanyakuanList = zanyakuanList.stream().collect(Collectors.toMap(FinVendorMonthAccountBillInfo::getOneKey, a -> a, (o1,o2)-> {
            if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(o1.getDocumentType())){ o1.setYajin(o1.getZanyakuan().subtract(o2.getZanyakuan())); }
            else if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(o2.getDocumentType())){ o1.setYajin(o2.getZanyakuan().subtract(o1.getZanyakuan())); }
            return o1;
        })).values().stream().collect(Collectors.toList());
        Map<String,FinVendorMonthAccountBillInfo> map = new HashMap<String,FinVendorMonthAccountBillInfo>();
        if (CollectionUtils.isNotEmpty(yufukuanList)){
            yufukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYufukuan(item.getYufukuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yingfuzanguList)){
            yingfuzanguList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYingfuzangu(item.getYingfuzangu());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yingfukuanList)){
            yingfukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYingfukuan(item.getYingfukuan());
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
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
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
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setTiaozhang(item.getTiaozhang());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(teshufukuanList)){
            teshufukuanList.forEach(item->{
                if (map.get(item.getOneKey()) == null){
                    map.put(item.getOneKey(),item);
                }
                else {
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setTeshufukuan(item.getTeshufukuan());
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
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
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
                    FinVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setZanyakuan(item.getZanyakuan());
                    map.put(item.getOneKey(),e);
                }
            });
        }
        List<FinVendorMonthAccountBillInfo> response = map.values().stream().collect(Collectors.toList());
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(response.size());
        if (finVendorMonthAccountBill.getPageNum() != null && finVendorMonthAccountBill.getPageSize() != null){
            response = CommonUtil.startPage(response,finVendorMonthAccountBill.getPageNum(),finVendorMonthAccountBill.getPageSize());
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
        List<FinVendorMonthAccountBill> responseList = new ArrayList<>();
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
                 * 供应商编码
                 */
                String vendorShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString(); //供应商编码  (必填)
                Long vendorCode = null;
                String vendorName = null;
                Long vendorSid = null; //表：供应商Sid
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
                 * 所属年月
                 */
                String yearMonth_s = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString(); //所属年月  (必填)
                String yearMonth= null;
                if (StrUtil.isBlank(yearMonth_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("所属年月不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isYearMonth(yearMonth_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("所属年月格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        String month_s = yearMonth_s.substring(5, 7);
                        Calendar cal = Calendar.getInstance();
                        int currentMonth = cal.get(Calendar.MONTH)+1;
                        int month = Integer.parseInt(month_s);
                        if (!(currentMonth == month || currentMonth == month+1)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("所属年月必须为当月或上月，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            yearMonth = yearMonth_s.replace("/","-");
                        }
                    }
                }
                if (companySid != null && vendorSid != null && yearMonth != null){
                    if (map.get(companySid+vendorSid+yearMonth)==null){
                        map.put(companySid+vendorSid+yearMonth,String.valueOf(num));
                        List<FinVendorMonthAccountBill> list = finVendorMonthAccountBillMapper.selectList(new QueryWrapper<FinVendorMonthAccountBill>()
                                .lambda().eq(FinVendorMonthAccountBill::getCompanySid,companySid)
                                .eq(FinVendorMonthAccountBill::getVendorSid,vendorSid)
                                .eq(FinVendorMonthAccountBill::getYearMonths,yearMonth));
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
                String daopiaoBenqi_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
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
                 *  本期付款
                 */
                String fukuanBenqi_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal fukuanBenqi = null;
                if (StrUtil.isBlank(fukuanBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期付款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(fukuanBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期付款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        fukuanBenqi = new BigDecimal(fukuanBenqi_s);//金额  (必填)
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
                 *  本期销售抵扣
                 */
                String xiaoshoudikouBenqi_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal xiaoshoudikouBenqi = null;
                if (StrUtil.isBlank(xiaoshoudikouBenqi_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期销售抵扣金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(xiaoshoudikouBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期销售抵扣金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        xiaoshoudikouBenqi = new BigDecimal(xiaoshoudikouBenqi_s); //金额  (必填)
                    }
                }
                /**
                 *  本期扣款
                 */
                String koukuanBenqi_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
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
                String tiaozhangBenqi_s = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
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
                 *  应付暂估
                 */
                String yingfuzangu_s = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                BigDecimal yingfuzangu = null;
                if (StrUtil.isBlank(yingfuzangu_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应付暂估金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(yingfuzangu_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应付暂估金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yingfuzangu = new BigDecimal(yingfuzangu_s); //金额  (必填)
                    }
                }
                /**
                 *  本期收款
                 */
                String shoukuanBenqi_s = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString(); //金额  (必填)
                BigDecimal shoukuanBenqi = null;
                if (StrUtil.isNotBlank(shoukuanBenqi_s)){
                    if (!JudgeFormat.isValidDouble(shoukuanBenqi_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期收款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        shoukuanBenqi = new BigDecimal(shoukuanBenqi_s);
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
                 *  应收暂估
                 */
                String yingshouzangu_s = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString(); //金额  (必填)
                BigDecimal yingshouzangu = null;
                if (StrUtil.isNotBlank(yingshouzangu_s)){
                    if (!JudgeFormat.isValidDouble(yingshouzangu_s,11,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应收暂估金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        yingshouzangu = new BigDecimal(yingshouzangu_s);
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
                    FinVendorMonthAccountBill finVendorMonthAccountBill = new FinVendorMonthAccountBill();
                    finVendorMonthAccountBill.setYueQichu(yueQichu)  //期初余额/上期余额金额
                            .setYueQimo(yueQimo)            //本期余额/期末余额
                            .setDaopiaoBenqi(daopiaoBenqi)               //本期到票
                            .setFukuanBenqi(fukuanBenqi)                 //本期付款
                            .setShoukuanBenqi(shoukuanBenqi)      //本期收款
                            .setXiaoshoudikouBenqi(xiaoshoudikouBenqi) //本期销售抵扣
                            .setKoukuanBenqi(koukuanBenqi)               //本期扣款
                            .setTiaozhangBenqi(tiaozhangBenqi)           //本期调账
                            .setYajin(yajin)                      //押金
                            .setZanyakuan(zanyakuan)                //暂押款
                            .setYueShijijieqian(BigDecimal.ZERO)    //实际结欠余额金额
                            .setYingfuzangu(yingfuzangu)            //应付暂估
                            .setYingshouzangu(yingshouzangu);     //应收暂估
                    finVendorMonthAccountBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setYearMonths(yearMonth);
                    finVendorMonthAccountBill.setCompanySid(companySid).setVendorSid(vendorSid).setVendorCode(vendorCode).setCompanyCode(companyCode)
                            .setVendorShortName(vendorShortName).setCompanyShortName(companyShortName).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    finVendorMonthAccountBill.setCompanyName(companyName).setVendorName(vendorName).setRemark(remark);
                    responseList.add(finVendorMonthAccountBill);
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
    public int addForm(List<FinVendorMonthAccountBill> list) {
        list.forEach(item->{
            finVendorMonthAccountBillMapper.insert(item);
        });
        return list.size();
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        Date orgin = sdf.parse("2021-12");
        Date toDate = sdf.parse("2021-08");
        start.setTime(orgin);
        end.setTime(toDate);
        int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
        System.out.println("result:" + Math.abs(result));
        System.out.println("month:" + Math.abs(month));
        System.out.println(":" + Math.abs(month + result));
    }

}


