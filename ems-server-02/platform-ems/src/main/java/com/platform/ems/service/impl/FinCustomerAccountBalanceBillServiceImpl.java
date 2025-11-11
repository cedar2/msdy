package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IFinBookReceiptEstimationItemService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.service.IFinCustomerAccountBalanceBillService;
import com.platform.ems.util.MongodbUtil;

/**
 * 客户账互抵单Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-25
 */
@Service
@SuppressWarnings("all")
public class FinCustomerAccountBalanceBillServiceImpl extends ServiceImpl<FinCustomerAccountBalanceBillMapper, FinCustomerAccountBalanceBill> implements IFinCustomerAccountBalanceBillService {
    @Autowired
    private FinCustomerAccountBalanceBillMapper finCustomerAccountBalanceBillMapper;
    @Autowired
    private FinCustomerAccountBalanceBillItemMapper itemMapper;
    @Autowired
    private FinCustomerAccountBalanceBillAttachmentMapper atmMapper;
    //客户互抵流水
    @Autowired
    private FinBookCustomerAccountBalanceMapper bookMapper;
    @Autowired
    private FinBookCustomerAccountBalanceItemMapper bookItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    //应收
    @Autowired
    private FinBookAccountReceivableMapper finBookAccountReceivableMapper;
    @Autowired
    private FinBookAccountReceivableItemMapper finBookAccountReceivableItemMapper;
    //应收暂估
    @Autowired
    private FinBookReceiptEstimationMapper finBookReceiptEstimationMapper;
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private IFinBookReceiptEstimationItemService finBookReceiptEstimationItemService;
    //收款
    @Autowired
    private FinBookReceiptPaymentMapper finBookReceiptPaymentMapper;
    @Autowired
    private FinBookReceiptPaymentItemMapper finBookReceiptPaymentItemMapper;
    //客户扣款
    @Autowired
    private FinBookCustomerDeductionMapper finBookCustomerDeductionMapper;
    @Autowired
    private FinBookCustomerDeductionItemMapper finBookCustomerDeductionItemMapper;
    //客户调账
    @Autowired
    private FinBookCustomerAccountAdjustMapper finBookCustomerAccountAdjustMapper;
    @Autowired
    private FinBookCustomerAccountAdjustItemMapper finBookCustomerAccountAdjustItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;

    private static final String TITLE = "客户账互抵单";

    /**
     * 查询客户账互抵单
     *
     * @param CustomerAccountBalanceBillSid 客户账互抵单ID
     * @return 客户账互抵单
     */
    @Override
    public FinCustomerAccountBalanceBill selectFinCustomerAccountBalanceBillById(Long accountBalanceBillSid) {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = finCustomerAccountBalanceBillMapper.selectFinCustomerAccountBalanceBillById(accountBalanceBillSid);
        FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem = new FinCustomerAccountBalanceBillItem();
        finCustomerAccountBalanceBillItem.setAccountBalanceBillSid(accountBalanceBillSid);
        List<FinCustomerAccountBalanceBillItem> itemList = itemMapper.selectFinCustomerAccountBalanceBillItemList(finCustomerAccountBalanceBillItem);
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (FinCustomerAccountBalanceBillItem item : itemList) {
                if (ConstantsFinance.BOOK_TYPE_YSZG.equals(item.getBookType())){
                    //应收暂估
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookReceiptEstimationItem.getCurrencyAmountTax())
                            .setCurrencyAmountTaxYhx(finBookReceiptEstimationItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookReceiptEstimationItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookReceiptEstimationItem.getCurrencyAmountTaxLeft());
                }
                if (ConstantsFinance.BOOK_TYPE_YINGS.equals(item.getBookType())){
                    //应收
                    FinBookAccountReceivableItem finBookAccountReceivableItem = finBookAccountReceivableItemMapper.selectFinBookAccountReceivableItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookAccountReceivableItem.getCurrencyAmountTaxYings())
                            .setCurrencyAmountTaxYhx(finBookAccountReceivableItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookAccountReceivableItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookAccountReceivableItem.getCurrencyAmountTaxDhx());
                }
                if (ConstantsFinance.BOOK_TYPE_SK.equals(item.getBookType())){
                    //收款
                    FinBookReceiptPaymentItem finBookReceiptPaymentItem = finBookReceiptPaymentItemMapper.selectFinBookReceiptPaymentItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookReceiptPaymentItem.getCurrencyAmountTaxSk())
                            .setCurrencyAmountTaxYhx(finBookReceiptPaymentItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookReceiptPaymentItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookReceiptPaymentItem.getCurrencyAmountTaxDhx());
                }
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())){
                    //扣款
                    FinBookCustomerDeductionItem finBookCustomerDeductionItem = finBookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookCustomerDeductionItem.getCurrencyAmountTaxKk())
                            .setCurrencyAmountTaxYhx(finBookCustomerDeductionItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookCustomerDeductionItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookCustomerDeductionItem.getCurrencyAmountTaxDhx());
                }
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())){
                    //调账
                    FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = finBookCustomerAccountAdjustItemMapper.selectFinBookCustomerAccountAdjustItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxTz())
                            .setCurrencyAmountTaxYhx(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxDhx());
                }
                item.setCurrencyAmountTaxDhx(item.getCurrencyAmountTaxDhx().add(item.getCurrencyAmountTax()));
            }
        }
        //附件
        List<FinCustomerAccountBalanceBillAttachment> attachmentList = atmMapper.selectFinCustomerAccountBalanceBillAttachmentList(
                new FinCustomerAccountBalanceBillAttachment().setAccountBalanceBillSid(accountBalanceBillSid));
        finCustomerAccountBalanceBill.setItemList(itemList);
        finCustomerAccountBalanceBill.setAttachmentList(attachmentList);
        //获取操作日志
        MongodbUtil.find(finCustomerAccountBalanceBill);
        return finCustomerAccountBalanceBill;
    }

    /**
     * 查询客户账互抵单列表
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 客户账互抵单
     */
    @Override
    public List<FinCustomerAccountBalanceBill> selectFinCustomerAccountBalanceBillList(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill) {
        List<FinCustomerAccountBalanceBill> response = finCustomerAccountBalanceBillMapper.selectFinCustomerAccountBalanceBillList(finCustomerAccountBalanceBill);
        return response;
    }

    /**
     * 新增客户账互抵单
     * 需要注意编码重复校验
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerAccountBalanceBill(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill) {
        //设置确认信息
        setConfirmInfo(finCustomerAccountBalanceBill);
        //校验金额
        judgePrice(finCustomerAccountBalanceBill);
        int row = finCustomerAccountBalanceBillMapper.insert(finCustomerAccountBalanceBill);
        if (row > 0) {
            insertChild(finCustomerAccountBalanceBill.getItemList(), finCustomerAccountBalanceBill.getAttachmentList(), finCustomerAccountBalanceBill.getAccountBalanceBillSid());
            insertBook(finCustomerAccountBalanceBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finCustomerAccountBalanceBill.getHandleStatus())) {
                finCustomerAccountBalanceBill = finCustomerAccountBalanceBillMapper.selectById(finCustomerAccountBalanceBill.getAccountBalanceBillSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_customer_account_balance_bill")
                        .setDocumentSid(finCustomerAccountBalanceBill.getAccountBalanceBillSid());
                sysTodoTask.setTitle("客户互抵单: " + finCustomerAccountBalanceBill.getAccountBalanceBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finCustomerAccountBalanceBill.getAccountBalanceBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finCustomerAccountBalanceBill.getAccountBalanceBillSid(), finCustomerAccountBalanceBill.getHandleStatus(), msgList, TITLE, null);

        }
        return row;
    }

    /**
     * 修改客户账互抵单
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerAccountBalanceBill(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill) {
        FinCustomerAccountBalanceBill response = finCustomerAccountBalanceBillMapper.selectFinCustomerAccountBalanceBillById(finCustomerAccountBalanceBill.getAccountBalanceBillSid());
        setConfirmInfo(finCustomerAccountBalanceBill);
        int row = finCustomerAccountBalanceBillMapper.updateAllById(finCustomerAccountBalanceBill);
        if (row > 0) {
            deleteItem(finCustomerAccountBalanceBill.getAccountBalanceBillSid(), response.getHandleStatus());
            //校验金额
            judgePrice(finCustomerAccountBalanceBill);
            insertChild(finCustomerAccountBalanceBill.getItemList(), finCustomerAccountBalanceBill.getAttachmentList(), finCustomerAccountBalanceBill.getAccountBalanceBillSid());
            insertBook(finCustomerAccountBalanceBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerAccountBalanceBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finCustomerAccountBalanceBill.getAccountBalanceBillSid()));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finCustomerAccountBalanceBill);
            MongodbDeal.update(finCustomerAccountBalanceBill.getAccountBalanceBillSid(), response.getHandleStatus(), finCustomerAccountBalanceBill.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 变更客户账互抵单
     *
     * @param finCustomerAccountBalanceBill 客户账互抵单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerAccountBalanceBill(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill) {
        FinCustomerAccountBalanceBill response = finCustomerAccountBalanceBillMapper.selectFinCustomerAccountBalanceBillById(finCustomerAccountBalanceBill.getAccountBalanceBillSid());
        setConfirmInfo(finCustomerAccountBalanceBill);
        int row = finCustomerAccountBalanceBillMapper.updateAllById(finCustomerAccountBalanceBill);
        if (row > 0) {
            deleteItem(finCustomerAccountBalanceBill.getAccountBalanceBillSid(), response.getHandleStatus());
            //校验金额
            judgePrice(finCustomerAccountBalanceBill);
            insertChild(finCustomerAccountBalanceBill.getItemList(), finCustomerAccountBalanceBill.getAttachmentList(), finCustomerAccountBalanceBill.getAccountBalanceBillSid());
            insertBook(finCustomerAccountBalanceBill);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finCustomerAccountBalanceBill);
            MongodbUtil.insertUserLog(finCustomerAccountBalanceBill.getAccountBalanceBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户账互抵单
     *
     * @param CustomerAccountBalanceBillSids 需要删除的客户账互抵单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerAccountBalanceBillByIds(List<Long> accountBalanceBillSids) {
        if (CollectionUtils.isEmpty(accountBalanceBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinCustomerAccountBalanceBill> finCustomerAccountBalanceBillList = finCustomerAccountBalanceBillMapper.selectList(new QueryWrapper<FinCustomerAccountBalanceBill>().lambda()
                .in(FinCustomerAccountBalanceBill::getAccountBalanceBillSid,accountBalanceBillSids)
                .notIn(FinCustomerAccountBalanceBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finCustomerAccountBalanceBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        accountBalanceBillSids.forEach(sid -> {
            FinCustomerAccountBalanceBill response = finCustomerAccountBalanceBillMapper.selectFinCustomerAccountBalanceBillById(sid);
            deleteItem(sid, response.getHandleStatus());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, accountBalanceBillSids));
        int row = finCustomerAccountBalanceBillMapper.deleteBatchIds(accountBalanceBillSids);
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param finCustomerAccountBalanceBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinCustomerAccountBalanceBill finCustomerAccountBalanceBill) {
        int row = 0;
        Long[] sids = finCustomerAccountBalanceBill.getAccountBalanceBillSidList();
        FinCustomerAccountBalanceBill params = new FinCustomerAccountBalanceBill();
        if (sids != null && sids.length > 0) {
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerAccountBalanceBill.getHandleStatus())){
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, sids));
            }
            for (Long id : sids) {
                finCustomerAccountBalanceBill.setAccountBalanceBillSid(id);
                params = this.selectFinCustomerAccountBalanceBillById(id);
                if (params.getHandleStatus().equals(finCustomerAccountBalanceBill.getHandleStatus())){
                    throw new CustomException("请不要重复操作！");
                }
                params.setHandleStatus(finCustomerAccountBalanceBill.getHandleStatus());
                this.setConfirmInfo(params);
                row = finCustomerAccountBalanceBillMapper.updateById(params);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                insertBook(params);
                //插入日志
//                List<OperMsg> msgList = new ArrayList<>();
//                MongodbDeal.check(finCustomerAccountBalanceBill.getAccountBalanceBillSid(), finCustomerAccountBalanceBill.getHandleStatus(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 是否确认操作处理
     *
     * @param finCustomerAccountBalanceBill
     * @return
     */
    private void setConfirmInfo(FinCustomerAccountBalanceBill entity) {
        BigDecimal sum = BigDecimal.ZERO;
        int flag_ys = 0,flag_zg = 0;
        for (FinCustomerAccountBalanceBillItem item : entity.getItemList()) {
            sum = sum.add(item.getCurrencyAmountTax());
            if (ConstantsFinance.BOOK_TYPE_YSZG.equals(item.getBookType())){
                flag_zg += -1;
            }
            if (ConstantsFinance.BOOK_TYPE_YINGS.equals(item.getBookType())){
                flag_ys += 1;
            }
            if (flag_zg < 0 && flag_ys > 0){
                throw new CustomException("互抵明细中不能同时存在应收和应收暂估流水");
            }
        }
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || entity.getHandleStatus().equals(ConstantsEms.SUBMIT_STATUS)) {
            if (CollectionUtils.isEmpty(entity.getItemList())) {
                throw new CustomException("此操作明细不能为空");
            }
            if (sum.compareTo(BigDecimal.ZERO) != 0){
                throw new CustomException("互抵明细中互抵金额总和应该等于 0 ");
            }
            if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                entity.setConfirmDate(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                entity.setMonthAccountPeriod(sdf.format(new Date()));
            }
        }
    }

    /**
     * 校验付款金额是否大于待核销金额
     */
    private void judgePrice(FinCustomerAccountBalanceBill entity) {
        //明细
        List<FinCustomerAccountBalanceBillItem> itemList = entity.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)){
            itemList.forEach(item->{
                BigDecimal dhx = new BigDecimal(-1);
                //获取应收暂估流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_YSZG.equals(item.getBookType())){
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper
                            .selectFinBookReceiptEstimationItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向应收暂估流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向应收暂估流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookReceiptEstimationItem.getCurrencyAmountTaxLeft().abs();
                }
                //获取应收流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_YINGS.equals(item.getBookType())){
                    FinBookAccountReceivableItem finBookAccountReceivableItem = finBookAccountReceivableItemMapper
                            .selectFinBookAccountReceivableItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向应收流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向应收流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookAccountReceivableItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取收款流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_SK.equals(item.getBookType())){
                    FinBookReceiptPaymentItem finBookReceiptPaymentItem = finBookReceiptPaymentItemMapper
                            .selectFinBookReceiptPaymentItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向收款流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向收款流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookReceiptPaymentItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取客户扣款流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())){
                    FinBookCustomerDeductionItem finBookCustomerDeductionItem = finBookCustomerDeductionItemMapper
                            .selectFinBookCustomerDeductionItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的扣款流水用来互抵的金额只能输入负数！");
                    }
                    dhx = finBookCustomerDeductionItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取客户调账流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())){
                    FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = finBookCustomerAccountAdjustItemMapper
                            .selectFinBookCustomerAccountAdjustItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向调账流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向调账流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookCustomerAccountAdjustItem.getCurrencyAmountTaxDhx().abs();
                }
                //校验
                if (dhx.compareTo(BigDecimal.ZERO) >= 0 && item.getCurrencyAmountTax().abs().compareTo(dhx) > 0){
                    throw new CustomException("明细中的互抵金额的绝对值不能大于待互抵金额的绝对值！");
                }
            });
        }
    }

    /**
     * 回退流水
     * @param finCustomerAccountBalanceBillSid
     * @param handleStatus
     */
    public void returnBack(Long finCustomerAccountBalanceBillSid, String handleStatus){
        //恢复明细表对应的流水的核销中数据
        FinCustomerAccountBalanceBill params = new FinCustomerAccountBalanceBill();
        params = this.selectFinCustomerAccountBalanceBillById(finCustomerAccountBalanceBillSid);
        if (CollectionUtils.isNotEmpty(params.getItemList())) {
            params.getItemList().forEach(item-> {
                //应收暂估
                if (ConstantsFinance.BOOK_TYPE_YSZG.equals(item.getBookType())) {
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookReceiptEstimationItem.setCurrencyAmountTaxYhx(finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                        finBookReceiptEstimationItemService.updateByAmountTax(finBookReceiptEstimationItem);
                    }else {
                        finBookReceiptEstimationItem.setCurrencyAmountTaxHxz(finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                        finBookReceiptEstimationItemService.updateByAmountTax(finBookReceiptEstimationItem);
                    }
                }
                //应收
                if (ConstantsFinance.BOOK_TYPE_YINGS.equals(item.getBookType())) {
                    FinBookAccountReceivableItem finBookAccountReceivableItem = finBookAccountReceivableItemMapper.selectById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookAccountReceivableItem.setCurrencyAmountTaxYhx(finBookAccountReceivableItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    }else {   //非确认状态下撤回 核销中金额
                        finBookAccountReceivableItem.setCurrencyAmountTaxHxz(finBookAccountReceivableItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookAccountReceivableItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookAccountReceivableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookAccountReceivableItem.getCurrencyAmountTaxHxz().add(finBookAccountReceivableItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookAccountReceivableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookAccountReceivableItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookAccountReceivableItemMapper.updateAllById(finBookAccountReceivableItem);
                }
                //收款
                if (ConstantsFinance.BOOK_TYPE_SK.equals(item.getBookType())) {
                    FinBookReceiptPaymentItem finBookReceiptPaymentItem = finBookReceiptPaymentItemMapper.selectById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookReceiptPaymentItem.setCurrencyAmountTaxYhx(finBookReceiptPaymentItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    }else {   //非确认状态下撤回 核销中金额
                        finBookReceiptPaymentItem.setCurrencyAmountTaxHxz(finBookReceiptPaymentItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookReceiptPaymentItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookReceiptPaymentItem.getCurrencyAmountTaxHxz().add(finBookReceiptPaymentItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookReceiptPaymentItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookReceiptPaymentItemMapper.updateAllById(finBookReceiptPaymentItem);
                }
                //客户扣款
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())) {
                    FinBookCustomerDeductionItem finBookCustomerDeductionItem = finBookCustomerDeductionItemMapper.selectById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookCustomerDeductionItem.setCurrencyAmountTaxYhx(finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    }else {   //非确认状态下撤回 核销中金额
                        finBookCustomerDeductionItem.setCurrencyAmountTaxHxz(finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().add(finBookCustomerDeductionItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookCustomerDeductionItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookCustomerDeductionItemMapper.updateAllById(finBookCustomerDeductionItem);
                }
                //客户调账
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())) {
                    FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = finBookCustomerAccountAdjustItemMapper.selectById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookCustomerAccountAdjustItem.setCurrencyAmountTaxYhx(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    }else {   //非确认状态下撤回 核销中金额
                        finBookCustomerAccountAdjustItem.setCurrencyAmountTaxHxz(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().add(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookCustomerAccountAdjustItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookCustomerAccountAdjustItemMapper.updateAllById(finBookCustomerAccountAdjustItem);
                }
            });
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long finCustomerAccountBalanceBillSid, String handleStatus) {
        //回退流水
        returnBack(finCustomerAccountBalanceBillSid,handleStatus);
        //明细表
        QueryWrapper<FinCustomerAccountBalanceBillItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("account_balance_bill_sid", finCustomerAccountBalanceBillSid);
        itemMapper.delete(itemQueryWrapper);
        //附件表
        QueryWrapper<FinCustomerAccountBalanceBillAttachment> atmQueryWrapper = new QueryWrapper<>();
        atmQueryWrapper.eq("account_balance_bill_sid", finCustomerAccountBalanceBillSid);
        atmMapper.delete(atmQueryWrapper);
        //流水主表
        List<FinBookCustomerAccountBalanceItem> bookItem = bookItemMapper.selectList(new QueryWrapper<FinBookCustomerAccountBalanceItem>().lambda()
                .eq(FinBookCustomerAccountBalanceItem::getReferDocSid,finCustomerAccountBalanceBillSid));
        if (CollectionUtils.isNotEmpty(bookItem)){
            bookMapper.deleteById(bookItem.get(0).getBookAccountBalanceSid());
        }
        //流水明细表
        QueryWrapper<FinBookCustomerAccountBalanceItem> bookItemQueryWrapper = new QueryWrapper<>();
        bookItemQueryWrapper.eq("refer_doc_sid", finCustomerAccountBalanceBillSid);
        bookItemMapper.delete(bookItemQueryWrapper);
    }

    /**
     * 新增明细表,附件表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertChild(List<FinCustomerAccountBalanceBillItem> itemList, List<FinCustomerAccountBalanceBillAttachment> atmList, Long sid) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinCustomerAccountBalanceBillItem item : itemList) {
                item.setItemNum((long)i++);
                if (item.getAccountBalanceBillSid() != null) {
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
                item.setAccountBalanceBillSid(sid);
                //应收暂估
                if (ConstantsFinance.BOOK_TYPE_YSZG.equals(item.getBookType())) {
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemById(item.getAccountItemSid());
                    //先暂存操作，将明细金额加入暂估流水原来核销中的金额
                    //得到商和余数
                    BigDecimal result[] = item.getCurrencyAmountTax().divideAndRemainder(finBookReceiptEstimationItem.getPriceTax());
                    if (result[1].compareTo(BigDecimal.ZERO) != 0){
                        throw new BaseException("应收暂估流水的金额应为销售价的整数倍，请核实");
                    }
                    finBookReceiptEstimationItem.setCurrencyAmountTaxHxz(finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookReceiptEstimationItemService.updateByAmountTax(finBookReceiptEstimationItem);
                }
                //应收
                if (ConstantsFinance.BOOK_TYPE_YINGS.equals(item.getBookType())) {
                    FinBookAccountReceivableItem finBookAccountReceivableItem = finBookAccountReceivableItemMapper.selectById(item.getAccountItemSid());
                    finBookAccountReceivableItem.setCurrencyAmountTaxHxz(finBookAccountReceivableItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookAccountReceivableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookAccountReceivableItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookAccountReceivableItemMapper.updateAllById(finBookAccountReceivableItem);
                }
                //收款
                if (ConstantsFinance.BOOK_TYPE_SK.equals(item.getBookType())) {
                    FinBookReceiptPaymentItem finBookReceiptPaymentItem = finBookReceiptPaymentItemMapper.selectById(item.getAccountItemSid());
                    finBookReceiptPaymentItem.setCurrencyAmountTaxHxz(finBookReceiptPaymentItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookReceiptPaymentItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookReceiptPaymentItemMapper.updateAllById(finBookReceiptPaymentItem);
                }
                //客户扣款
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())) {
                    FinBookCustomerDeductionItem finBookCustomerDeductionItem = finBookCustomerDeductionItemMapper.selectById(item.getAccountItemSid());
                    finBookCustomerDeductionItem.setCurrencyAmountTaxHxz(finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookCustomerDeductionItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookCustomerDeductionItemMapper.updateAllById(finBookCustomerDeductionItem);
                }
                //客户调账
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())) {
                    FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = finBookCustomerAccountAdjustItemMapper.selectById(item.getAccountItemSid());
                    finBookCustomerAccountAdjustItem.setCurrencyAmountTaxHxz(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookCustomerAccountAdjustItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookCustomerAccountAdjustItemMapper.updateAllById(finBookCustomerAccountAdjustItem);
                }
            }
            itemMapper.inserts(itemList);
        }
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setAccountBalanceBillSid(sid);
            });
            atmMapper.inserts(atmList);
        }
    }

    /**
     * 新增流水
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBook(FinCustomerAccountBalanceBill bill) {
        //确认则生成流水账
        if (CollectionUtils.isNotEmpty(bill.getItemList())
                && bill.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //流水主表
            FinBookCustomerAccountBalance book = new FinBookCustomerAccountBalance();
            BeanCopyUtils.copyProperties(bill, book);
            Calendar cal = Calendar.getInstance();
            book.setDocumentDate(new Date()).setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                    .setPaymentYear(cal.get(Calendar.YEAR))
                    .setPaymentMonth(cal.get(Calendar.MONTH) + 1).setBookType(ConstantsFinance.BOOK_TYPE_CZHD).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_CZHD);
            bookMapper.insert(book);
            //流水明细表(多条)
            bill.getItemList().forEach(item -> {
                FinBookCustomerAccountBalanceItem bookItem = new FinBookCustomerAccountBalanceItem();
                BeanCopyUtils.copyProperties(item, bookItem);
                bookItem.setBookAccountBalanceSid(book.getBookAccountBalanceSid())
                        .setReferDocSid(bill.getAccountBalanceBillSid())
                        .setReferDocCode(bill.getAccountBalanceBillCode())
                        .setReferDocItemSid(item.getAccountBalanceBillItemSid())
                        .setCurrencyAmountTaxHd(item.getCurrencyAmountTax())
                        .setReferBookType(item.getBookType())
                        .setReferBookSourceCategory(item.getBookSourceCategory())
                        .setReferAccountDocumentSid(item.getAccountDocumentSid())
                        .setReferAccountDocumentCode(item.getAccountDocumentCode())
                        .setReferAccountItemSid(item.getAccountItemSid());
                bookItemMapper.insert(bookItem);
                //修改流水来源的金额
                //应收暂估
                if (ConstantsFinance.BOOK_TYPE_YSZG.equals(item.getBookType())) {
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemById(item.getAccountItemSid());
                    //后确认操作，暂估流水核销中金额减去明细金额，然后将明细金额加入暂估流水原来已核销的金额
                    //得到商和余数
                    BigDecimal result[] = item.getCurrencyAmountTax().divideAndRemainder(finBookReceiptEstimationItem.getPriceTax());
                    if (result[1].compareTo(BigDecimal.ZERO) != 0){
                        throw new BaseException("应收暂估流水的金额应为销售价的整数倍，请核实");
                    }
                    finBookReceiptEstimationItem.setCurrencyAmountTaxHxz(finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    finBookReceiptEstimationItem.setCurrencyAmountTaxYhx(finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookReceiptEstimationItemService.updateByAmountTax(finBookReceiptEstimationItem);
                }
                //应收流水
                if (ConstantsFinance.BOOK_TYPE_YINGS.equals(item.getBookType())) {
                    FinBookAccountReceivableItem finBookAccountReceivableItem = finBookAccountReceivableItemMapper.selectFinBookAccountReceivableItemById(item.getAccountItemSid());
                    finBookAccountReceivableItem.setCurrencyAmountTaxYhx(finBookAccountReceivableItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookAccountReceivableItem.setCurrencyAmountTaxHxz(finBookAccountReceivableItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookAccountReceivableItem.getCurrencyAmountTaxYhx().compareTo(finBookAccountReceivableItem.getCurrencyAmountTaxYings()) == 0){
                        finBookAccountReceivableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookAccountReceivableItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0){
//                        finBookAccountReceivableItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookAccountReceivableItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookAccountReceivableItemMapper.updateAllById(finBookAccountReceivableItem);
                }
                //收款流水
                if (ConstantsFinance.BOOK_TYPE_SK.equals(item.getBookType())) {
                    FinBookReceiptPaymentItem finBookReceiptPaymentItem = finBookReceiptPaymentItemMapper.selectFinBookReceiptPaymentItemById(item.getAccountItemSid());
                    finBookReceiptPaymentItem.setCurrencyAmountTaxYhx(finBookReceiptPaymentItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookReceiptPaymentItem.setCurrencyAmountTaxHxz(finBookReceiptPaymentItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookReceiptPaymentItem.getCurrencyAmountTaxYhx().compareTo(finBookReceiptPaymentItem.getCurrencyAmountTaxSk()) == 0){
                        finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookReceiptPaymentItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0){
//                        finBookReceiptPaymentItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookReceiptPaymentItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookReceiptPaymentItemMapper.updateAllById(finBookReceiptPaymentItem);
                }
                //客户扣款流水
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())) {
                    FinBookCustomerDeductionItem finBookCustomerDeductionItem = finBookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemById(item.getAccountItemSid());
                    finBookCustomerDeductionItem.setCurrencyAmountTaxYhx(finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookCustomerDeductionItem.setCurrencyAmountTaxHxz(finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().compareTo(finBookCustomerDeductionItem.getCurrencyAmountTaxKk()) == 0){
                        finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0){
//                        finBookCustomerDeductionItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookCustomerDeductionItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookCustomerDeductionItemMapper.updateAllById(finBookCustomerDeductionItem);
                }
                //客户调账流水
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())) {
                    FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = finBookCustomerAccountAdjustItemMapper.selectFinBookCustomerAccountAdjustItemById(item.getAccountItemSid());
                    finBookCustomerAccountAdjustItem.setCurrencyAmountTaxYhx(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookCustomerAccountAdjustItem.setCurrencyAmountTaxHxz(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx().compareTo(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxTz()) == 0){
                        finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0){
//                        finBookCustomerAccountAdjustItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookCustomerAccountAdjustItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookCustomerAccountAdjustItemMapper.updateAllById(finBookCustomerAccountAdjustItem);
                }
            });
        }
    }

    @Override
    public List<FinCustomerAccountBalanceBillItem> bookList(FinCustomerAccountBalanceBillItem request) {
        List<FinCustomerAccountBalanceBillItem> responseList = new ArrayList<>();
        //应收暂估
        if (ConstantsFinance.BOOK_TYPE_YSZG.equals(request.getBookType())) {
            FinBookReceiptEstimation finBookReceiptEstimation = new FinBookReceiptEstimation();
            BeanUtil.copyProperties(request, finBookReceiptEstimation);
            List<FinBookReceiptEstimation> bookList = finBookReceiptEstimationMapper.getReportForm(finBookReceiptEstimation);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookReceiptEstimationSid())
                        .setAccountDocumentCode(item.getBookReceiptEstimationCode())
                        .setAccountItemSid(item.getBookReceiptEstimationItemSid())
                        .setCurrencyAmountTax(null)
                        .setPriceTax(item.getPriceTax())
                        .setCurrencyAmountTaxDhx(item.getCurrencyAmountTaxDhx())
                        .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz())
                        .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTax())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //应收流水
        if (ConstantsFinance.BOOK_TYPE_YINGS.equals(request.getBookType())) {
            FinBookAccountReceivable finBookAccountReceivable = new FinBookAccountReceivable();
            BeanUtil.copyProperties(request, finBookAccountReceivable);
            List<FinBookAccountReceivable> bookList = finBookAccountReceivableMapper.getReportForm(finBookAccountReceivable);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountReceivableSid())
                        .setAccountDocumentCode(item.getBookAccountReceivableCode())
                        .setAccountItemSid(item.getBookAccountReceivableItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxYings())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //收款流失
        if (ConstantsFinance.BOOK_TYPE_SK.equals(request.getBookType())) {
            FinBookReceiptPayment finBookReceiptPayment = new FinBookReceiptPayment();
            BeanUtil.copyProperties(request, finBookReceiptPayment);
            List<FinBookReceiptPayment> bookList = finBookReceiptPaymentMapper.getReportForm(finBookReceiptPayment);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookReceiptPaymentSid())
                        .setAccountDocumentCode(item.getBookReceiptPaymentCode())
                        .setAccountItemSid(item.getBookReceiptPaymentItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxSk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //客户扣款流水
        if (ConstantsFinance.BOOK_TYPE_CKK.equals(request.getBookType())) {
            FinBookCustomerDeduction finBookCustomerDeduction = new FinBookCustomerDeduction();
            BeanUtil.copyProperties(request, finBookCustomerDeduction);
            List<FinBookCustomerDeduction> bookList = finBookCustomerDeductionMapper.getReportForm(finBookCustomerDeduction);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookDeductionSid())
                        .setAccountDocumentCode(item.getBookDeductionCode())
                        .setAccountItemSid(item.getBookDeductionItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxKk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //客户调账流水
        if (ConstantsFinance.BOOK_TYPE_CTZ.equals(request.getBookType())) {
            FinBookCustomerAccountAdjust finBookCustomerAccountAdjust = new FinBookCustomerAccountAdjust();
            BeanUtil.copyProperties(request, finBookCustomerAccountAdjust);
            List<FinBookCustomerAccountAdjust> bookList = finBookCustomerAccountAdjustMapper.getReportForm(finBookCustomerAccountAdjust);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountAdjustSid())
                        .setAccountDocumentCode(item.getBookAccountAdjustCode())
                        .setAccountItemSid(item.getBookAccountAdjustItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxTz())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        return responseList;
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalid(Long accountBalanceSid){
        FinCustomerAccountBalanceBill bill = finCustomerAccountBalanceBillMapper.selectById(accountBalanceSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookCustomerAccountBalance> bookList = bookMapper.getReportForm(new FinBookCustomerAccountBalance().setReferDocSid(bill.getAccountBalanceBillSid()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            bookMapper.updateById(item);
        });
        int i = finCustomerAccountBalanceBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0){
            returnBack(accountBalanceSid,ConstantsEms.CHECK_STATUS);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getAccountBalanceBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }
}
