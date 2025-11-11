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
import com.platform.ems.service.IFinBookPaymentEstimationItemService;
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
import com.platform.ems.service.IFinVendorAccountBalanceBillService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商账互抵单Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-25
 */
@Service
@SuppressWarnings("all")
public class FinVendorAccountBalanceBillServiceImpl extends ServiceImpl<FinVendorAccountBalanceBillMapper, FinVendorAccountBalanceBill> implements IFinVendorAccountBalanceBillService {
    @Autowired
    private FinVendorAccountBalanceBillMapper finVendorAccountBalanceBillMapper;
    @Autowired
    private FinVendorAccountBalanceBillItemMapper itemMapper;
    @Autowired
    private FinVendorAccountBalanceBillAttachmentMapper atmMapper;
    @Autowired
    private FinBookVendorAccountBalanceMapper bookMapper;
    @Autowired
    private FinBookVendorAccountBalanceItemMapper bookItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    //应付
    @Autowired
    private FinBookAccountPayableMapper finBookAccountPayableMapper;
    @Autowired
    private FinBookAccountPayableItemMapper finBookAccountPayableItemMapper;
    //应付暂估
    @Autowired
    private FinBookPaymentEstimationMapper finBookPaymentEstimationMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;
    @Autowired
    private IFinBookPaymentEstimationItemService finBookPaymentEstimationItemService;
    //付款
    @Autowired
    private FinBookPaymentMapper finBookPaymentMapper;
    @Autowired
    private FinBookPaymentItemMapper finBookPaymentItemMapper;
    //供应商扣款
    @Autowired
    private FinBookVendorDeductionMapper finBookVendorDeductionMapper;
    @Autowired
    private FinBookVendorDeductionItemMapper finBookVendorDeductionItemMapper;
    //供应商调账
    @Autowired
    private FinBookVendorAccountAdjustMapper finBookVendorAccountAdjustMapper;
    @Autowired
    private FinBookVendorAccountAdjustItemMapper finBookVendorAccountAdjustItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;

    private static final String TITLE = "供应商账互抵单";

    /**
     * 查询供应商账互抵单
     *
     * @param vendorAccountBalanceBillSid 供应商账互抵单ID
     * @return 供应商账互抵单
     */
    @Override
    public FinVendorAccountBalanceBill selectFinVendorAccountBalanceBillById(Long vendorAccountBalanceBillSid) {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = finVendorAccountBalanceBillMapper.selectFinVendorAccountBalanceBillById(vendorAccountBalanceBillSid);
        FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem = new FinVendorAccountBalanceBillItem();
        finVendorAccountBalanceBillItem.setAccountBalanceBillSid(vendorAccountBalanceBillSid);
        List<FinVendorAccountBalanceBillItem> itemList = itemMapper.selectFinVendorAccountBalanceBillItemList(finVendorAccountBalanceBillItem);
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (FinVendorAccountBalanceBillItem item : itemList) {
                if (ConstantsFinance.BOOK_TYPE_YFZG.equals(item.getBookType())) {
                    //应付暂估
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectFinBookPaymentEstimationItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookPaymentEstimationItem.getCurrencyAmountTax())
                            .setCurrencyAmountTaxYhx(finBookPaymentEstimationItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookPaymentEstimationItem.getCurrencyAmountTaxLeft());
                }
                if (ConstantsFinance.BOOK_TYPE_YINGF.equals(item.getBookType())) {
                    //应付
                    FinBookAccountPayableItem finBookAccountPayableItem = finBookAccountPayableItemMapper.selectFinBookAccountPayableItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookAccountPayableItem.getCurrencyAmountTaxYingf())
                            .setCurrencyAmountTaxYhx(finBookAccountPayableItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookAccountPayableItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookAccountPayableItem.getCurrencyAmountTaxDhx());
                }
                if (ConstantsFinance.BOOK_TYPE_FK.equals(item.getBookType())) {
                    //付款
                    FinBookPaymentItem finBookPaymentItem = finBookPaymentItemMapper.selectFinBookPaymentItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookPaymentItem.getCurrencyAmountTaxFk())
                            .setCurrencyAmountTaxYhx(finBookPaymentItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookPaymentItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookPaymentItem.getCurrencyAmountTaxDhx());
                }
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())) {
                    //扣款
                    FinBookVendorDeductionItem finBookVendorDeductionItem = finBookVendorDeductionItemMapper.selectFinBookVendorDeductionItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookVendorDeductionItem.getCurrencyAmountTaxKk())
                            .setCurrencyAmountTaxYhx(finBookVendorDeductionItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookVendorDeductionItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookVendorDeductionItem.getCurrencyAmountTaxDhx());
                }
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())) {
                    //调账
                    FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = finBookVendorAccountAdjustItemMapper.selectFinBookVendorAccountAdjustItemById(item.getAccountItemSid());
                    item.setCurrencyAmountTaxYhd(finBookVendorAccountAdjustItem.getCurrencyAmountTaxTz())
                            .setCurrencyAmountTaxYhx(finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx())
                            .setCurrencyAmountTaxHxz(finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxDhx(finBookVendorAccountAdjustItem.getCurrencyAmountTaxDhx());
                }
                item.setCurrencyAmountTaxDhx(item.getCurrencyAmountTaxDhx().add(item.getCurrencyAmountTax()));
            }
        }
        //附件
        List<FinVendorAccountBalanceBillAttachment> attachmentList = atmMapper.selectFinVendorAccountBalanceBillAttachmentList(
                new FinVendorAccountBalanceBillAttachment().setAccountBalanceBillSid(vendorAccountBalanceBillSid));
        finVendorAccountBalanceBill.setItemList(itemList);
        finVendorAccountBalanceBill.setAttachmentList(attachmentList);
        //获取操作日志
        MongodbUtil.find(finVendorAccountBalanceBill);
        return finVendorAccountBalanceBill;
    }

    /**
     * 查询供应商账互抵单列表
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 供应商账互抵单
     */
    @Override
    public List<FinVendorAccountBalanceBill> selectFinVendorAccountBalanceBillList(FinVendorAccountBalanceBill finVendorAccountBalanceBill) {
        List<FinVendorAccountBalanceBill> response = finVendorAccountBalanceBillMapper.selectFinVendorAccountBalanceBillList(finVendorAccountBalanceBill);
        return response;
    }

    /**
     * 新增供应商账互抵单
     * 需要注意编码重复校验
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorAccountBalanceBill(FinVendorAccountBalanceBill finVendorAccountBalanceBill) {
        //设置确认信息
        setConfirmInfo(finVendorAccountBalanceBill);
        //校验金额
        judgePrice(finVendorAccountBalanceBill);
        int row = finVendorAccountBalanceBillMapper.insert(finVendorAccountBalanceBill);
        if (row > 0) {
            insertChild(finVendorAccountBalanceBill.getItemList(), finVendorAccountBalanceBill.getAttachmentList(), finVendorAccountBalanceBill.getAccountBalanceBillSid());
            insertBook(finVendorAccountBalanceBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finVendorAccountBalanceBill.getHandleStatus())) {
                finVendorAccountBalanceBill = finVendorAccountBalanceBillMapper.selectById(finVendorAccountBalanceBill.getAccountBalanceBillSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_vendor_account_balance_bill")
                        .setDocumentSid(finVendorAccountBalanceBill.getAccountBalanceBillSid());
                sysTodoTask.setTitle("供应商互抵单: " + finVendorAccountBalanceBill.getAccountBalanceBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finVendorAccountBalanceBill.getAccountBalanceBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finVendorAccountBalanceBill.getAccountBalanceBillSid(), finVendorAccountBalanceBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改供应商账互抵单
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorAccountBalanceBill(FinVendorAccountBalanceBill finVendorAccountBalanceBill) {
        FinVendorAccountBalanceBill response = finVendorAccountBalanceBillMapper.selectFinVendorAccountBalanceBillById(finVendorAccountBalanceBill.getAccountBalanceBillSid());
        setConfirmInfo(finVendorAccountBalanceBill);
        int row = finVendorAccountBalanceBillMapper.updateAllById(finVendorAccountBalanceBill);
        if (row > 0) {
            deleteItem(finVendorAccountBalanceBill.getAccountBalanceBillSid(), response.getHandleStatus());
            //校验金额
            judgePrice(finVendorAccountBalanceBill);
            insertChild(finVendorAccountBalanceBill.getItemList(), finVendorAccountBalanceBill.getAttachmentList(), finVendorAccountBalanceBill.getAccountBalanceBillSid());
            insertBook(finVendorAccountBalanceBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorAccountBalanceBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finVendorAccountBalanceBill.getAccountBalanceBillSid()));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finVendorAccountBalanceBill);
            MongodbDeal.update(finVendorAccountBalanceBill.getAccountBalanceBillSid(), response.getHandleStatus(), finVendorAccountBalanceBill.getHandleStatus(), msgList, TITLE,null);

        }
        return row;
    }

    /**
     * 变更供应商账互抵单
     *
     * @param finVendorAccountBalanceBill 供应商账互抵单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorAccountBalanceBill(FinVendorAccountBalanceBill finVendorAccountBalanceBill) {
        FinVendorAccountBalanceBill response = finVendorAccountBalanceBillMapper.selectFinVendorAccountBalanceBillById(finVendorAccountBalanceBill.getAccountBalanceBillSid());
        setConfirmInfo(finVendorAccountBalanceBill);
        int row = finVendorAccountBalanceBillMapper.updateAllById(finVendorAccountBalanceBill);
        if (row > 0) {
            deleteItem(finVendorAccountBalanceBill.getAccountBalanceBillSid(), response.getHandleStatus());
            //校验金额
            judgePrice(finVendorAccountBalanceBill);
            insertChild(finVendorAccountBalanceBill.getItemList(), finVendorAccountBalanceBill.getAttachmentList(), finVendorAccountBalanceBill.getAccountBalanceBillSid());
            insertBook(finVendorAccountBalanceBill);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finVendorAccountBalanceBill);
            MongodbUtil.insertUserLog(finVendorAccountBalanceBill.getAccountBalanceBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商账互抵单
     *
     * @param vendorAccountBalanceBillSids 需要删除的供应商账互抵单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorAccountBalanceBillByIds(List<Long> accountBalanceBillSids) {
        if (CollectionUtils.isEmpty(accountBalanceBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinVendorAccountBalanceBill> finVendorAccountBalanceBillList = finVendorAccountBalanceBillMapper.selectList(new QueryWrapper<FinVendorAccountBalanceBill>().lambda()
                .in(FinVendorAccountBalanceBill::getAccountBalanceBillSid,accountBalanceBillSids)
                .notIn(FinVendorAccountBalanceBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finVendorAccountBalanceBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        accountBalanceBillSids.forEach(sid -> {
            FinVendorAccountBalanceBill response = finVendorAccountBalanceBillMapper.selectFinVendorAccountBalanceBillById(sid);
            deleteItem(sid, response.getHandleStatus());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, accountBalanceBillSids));
        int row = finVendorAccountBalanceBillMapper.deleteBatchIds(accountBalanceBillSids);
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param finVendorAccountBalanceBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinVendorAccountBalanceBill finVendorAccountBalanceBill) {
        int row = 0;
        Long[] sids = finVendorAccountBalanceBill.getAccountBalanceBillSidList();
        FinVendorAccountBalanceBill params = new FinVendorAccountBalanceBill();
        if (sids != null && sids.length > 0) {
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorAccountBalanceBill.getHandleStatus())){
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, sids));
            }
            for (Long id : sids) {
                finVendorAccountBalanceBill.setAccountBalanceBillSid(id);
                params = this.selectFinVendorAccountBalanceBillById(id);
                if (params.getHandleStatus().equals(finVendorAccountBalanceBill.getHandleStatus())){
                    throw new CustomException("请不要重复操作！");
                }
                params.setHandleStatus(finVendorAccountBalanceBill.getHandleStatus());
                this.setConfirmInfo(params);
                row = finVendorAccountBalanceBillMapper.updateById(params);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                insertBook(params);
                //插入日志
//                List<OperMsg> msgList = new ArrayList<>();
//                MongodbDeal.check(finVendorAccountBalanceBill.getAccountBalanceBillSid(), finVendorAccountBalanceBill.getHandleStatus(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 是否确认操作处理
     *
     * @param finVendorAccountBalanceBill
     * @return
     */
    private void setConfirmInfo(FinVendorAccountBalanceBill entity) {
        BigDecimal sum = BigDecimal.ZERO;
        int flag_yf = 0,flag_zg = 0;
        for (FinVendorAccountBalanceBillItem item : entity.getItemList()) {
            sum = sum.add(item.getCurrencyAmountTax());
            if (ConstantsFinance.BOOK_TYPE_YFZG.equals(item.getBookType())){
                flag_zg += -1;
            }
            if (ConstantsFinance.BOOK_TYPE_YINGF.equals(item.getBookType())){
                flag_yf += 1;
            }
            if (flag_zg < 0 && flag_yf > 0){
                throw new CustomException("互抵明细中不能同时存在应付和应付暂估流水");
            }
        }
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || entity.getHandleStatus().equals(ConstantsEms.SUBMIT_STATUS)) {
            if (CollectionUtils.isEmpty(entity.getItemList())) {
                throw new CustomException("此操作明细不能为空");
            }
            if (sum.compareTo(BigDecimal.ZERO) != 0) {
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
    private void judgePrice(FinVendorAccountBalanceBill entity) {
        //明细
        List<FinVendorAccountBalanceBillItem> itemList = entity.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)){
            itemList.forEach(item->{
                BigDecimal dhx = new BigDecimal(-1);
                //获取应付暂估流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_YFZG.equals(item.getBookType())){
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper
                            .selectFinBookPaymentEstimationItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向应付暂估流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向应付暂估流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookPaymentEstimationItem.getCurrencyAmountTaxLeft().abs();
                }
                //获取应付流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_YINGF.equals(item.getBookType())){
                    FinBookAccountPayableItem finBookAccountPayableItem = finBookAccountPayableItemMapper
                            .selectFinBookAccountPayableItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向应付流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向应付流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookAccountPayableItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取付款流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_FK.equals(item.getBookType())){
                    FinBookPaymentItem finBookPaymentItem = finBookPaymentItemMapper
                            .selectFinBookPaymentItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向付款流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向付款流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookPaymentItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取供应商扣款流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())){
                    FinBookVendorDeductionItem finBookVendorDeductionItem = finBookVendorDeductionItemMapper
                            .selectFinBookVendorDeductionItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的扣款流水用来互抵的金额只能输入负数！");
                    }
                    dhx = finBookVendorDeductionItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取供应商调账流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())){
                    FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = finBookVendorAccountAdjustItemMapper
                            .selectFinBookVendorAccountAdjustItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("明细中的负向调账流水用来互抵的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYhd().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("明细中的正向调账流水用来互抵的金额只能输入正数！");
                    }
                    dhx = finBookVendorAccountAdjustItem.getCurrencyAmountTaxDhx().abs();
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
     * @param finVendorAccountBalanceBillSid
     * @param handleStatus
     */
    public void returnBack(Long finVendorAccountBalanceBillSid, String handleStatus){
        //恢复明细表对应的流水的核销中数据
        FinVendorAccountBalanceBill params = new FinVendorAccountBalanceBill();
        params = this.selectFinVendorAccountBalanceBillById(finVendorAccountBalanceBillSid);
        if (CollectionUtils.isNotEmpty(params.getItemList())) {
            params.getItemList().forEach(item -> {
                //应付暂估
                if (ConstantsFinance.BOOK_TYPE_YFZG.equals(item.getBookType())) {
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectFinBookPaymentEstimationItemById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookPaymentEstimationItem.setCurrencyAmountTaxYhx(finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                        finBookPaymentEstimationItemService.updateByAmountTax(finBookPaymentEstimationItem);
                    }else {
                        finBookPaymentEstimationItem.setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                        finBookPaymentEstimationItemService.updateByAmountTax(finBookPaymentEstimationItem);
                    }
                }
                //应付
                if (ConstantsFinance.BOOK_TYPE_YINGF.equals(item.getBookType())) {
                    FinBookAccountPayableItem finBookAccountPayableItem = finBookAccountPayableItemMapper.selectFinBookAccountPayableItemById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookAccountPayableItem.setCurrencyAmountTaxYhx(finBookAccountPayableItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    } else {   //非确认状态下撤回 核销中金额
                        finBookAccountPayableItem.setCurrencyAmountTaxHxz(finBookAccountPayableItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookAccountPayableItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookAccountPayableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookAccountPayableItem.getCurrencyAmountTaxHxz().add(finBookAccountPayableItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookAccountPayableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookAccountPayableItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookAccountPayableItemMapper.updateAllById(finBookAccountPayableItem);
                }
                //付款
                if (ConstantsFinance.BOOK_TYPE_FK.equals(item.getBookType())) {
                    FinBookPaymentItem finBookPaymentItem = finBookPaymentItemMapper.selectFinBookPaymentItemById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookPaymentItem.setCurrencyAmountTaxYhx(finBookPaymentItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    } else {   //非确认状态下撤回 核销中金额
                        finBookPaymentItem.setCurrencyAmountTaxHxz(finBookPaymentItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookPaymentItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookPaymentItem.getCurrencyAmountTaxHxz().add(finBookPaymentItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookPaymentItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookPaymentItemMapper.updateAllById(finBookPaymentItem);
                }
                //供应商扣款
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())) {
                    FinBookVendorDeductionItem finBookVendorDeductionItem = finBookVendorDeductionItemMapper.selectFinBookVendorDeductionItemById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookVendorDeductionItem.setCurrencyAmountTaxYhx(finBookVendorDeductionItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    } else {   //非确认状态下撤回 核销中金额
                        finBookVendorDeductionItem.setCurrencyAmountTaxHxz(finBookVendorDeductionItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookVendorDeductionItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookVendorDeductionItem.getCurrencyAmountTaxHxz().add(finBookVendorDeductionItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookVendorDeductionItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookVendorDeductionItemMapper.updateAllById(finBookVendorDeductionItem);
                }
                //供应商调账
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())) {
                    FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = finBookVendorAccountAdjustItemMapper.selectFinBookVendorAccountAdjustItemById(item.getAccountItemSid());
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                        finBookVendorAccountAdjustItem.setCurrencyAmountTaxYhx(finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                    } else {   //非确认状态下撤回 核销中金额
                        finBookVendorAccountAdjustItem.setCurrencyAmountTaxHxz(finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    }
                    //如果已核销等于 0 就修改状态为部分核销
                    if (finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
                        finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                    if ((finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().add(finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                        finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                    }
                    finBookVendorAccountAdjustItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookVendorAccountAdjustItemMapper.updateAllById(finBookVendorAccountAdjustItem);
                }
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long finVendorAccountBalanceBillSid, String handleStatus) {
        //回退流水
        returnBack(finVendorAccountBalanceBillSid,handleStatus);
        //明细表
        QueryWrapper<FinVendorAccountBalanceBillItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("account_balance_bill_sid", finVendorAccountBalanceBillSid);
        itemMapper.delete(itemQueryWrapper);
        //附件表
        QueryWrapper<FinVendorAccountBalanceBillAttachment> atmQueryWrapper = new QueryWrapper<>();
        atmQueryWrapper.eq("account_balance_bill_sid", finVendorAccountBalanceBillSid);
        atmMapper.delete(atmQueryWrapper);
        //流水主表
        List<FinBookVendorAccountBalanceItem> bookItem = bookItemMapper.selectList(new QueryWrapper<FinBookVendorAccountBalanceItem>().lambda()
                .eq(FinBookVendorAccountBalanceItem::getReferDocSid, finVendorAccountBalanceBillSid));
        if (CollectionUtils.isNotEmpty(bookItem)) {
            bookMapper.deleteById(bookItem.get(0).getBookAccountBalanceSid());
        }
        //流水明细表
        QueryWrapper<FinBookVendorAccountBalanceItem> bookItemQueryWrapper = new QueryWrapper<>();
        bookItemQueryWrapper.eq("refer_doc_sid", finVendorAccountBalanceBillSid);
        bookItemMapper.delete(bookItemQueryWrapper);
    }

    /**
     * 得到约等于开票的量
     *
     */
    private BigDecimal divide(BigDecimal divisor, BigDecimal dividend){
        //得到约等于开票的量
        BigDecimal quantity = BigDecimal.ZERO;
        if (divisor.compareTo(BigDecimal.ZERO) ==0 || dividend.compareTo(BigDecimal.ZERO) ==0){
            return quantity;
        }
        divisor = divisor.abs();
        dividend = dividend.abs();
        BigDecimal result[] = divisor.divideAndRemainder(dividend);
        if (result[0].compareTo(BigDecimal.ZERO) >= 0){
            if (result[1].compareTo(BigDecimal.ZERO) > 0){
                quantity = result[0].add(BigDecimal.ONE);
            }
            else {
                quantity = result[0];
            }
        }
        return quantity;
    }

    /**
     * 新增明细表,附件表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertChild(List<FinVendorAccountBalanceBillItem> itemList, List<FinVendorAccountBalanceBillAttachment> atmList, Long sid) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinVendorAccountBalanceBillItem item : itemList) {
                item.setItemNum((long)i++);
                if (item.getAccountBalanceBillSid() != null) {
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
                item.setAccountBalanceBillSid(sid);
                //应付暂估
                if (ConstantsFinance.BOOK_TYPE_YFZG.equals(item.getBookType())) {
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectFinBookPaymentEstimationItemById(item.getAccountItemSid());
                    //先暂存操作，将明细金额加入暂估流水原来核销中的金额
                    //得到商和余数
                    BigDecimal result[] = item.getCurrencyAmountTax().divideAndRemainder(finBookPaymentEstimationItem.getPriceTax());
                    if (result[1].compareTo(BigDecimal.ZERO) != 0){
                        throw new BaseException("应付暂估流水的金额应为采购价的整数倍，请核实");
                    }
                    finBookPaymentEstimationItem.setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookPaymentEstimationItemService.updateByAmountTax(finBookPaymentEstimationItem);
                }
                //应付
                if (ConstantsFinance.BOOK_TYPE_YINGF.equals(item.getBookType())) {
                    FinBookAccountPayableItem finBookAccountPayableItem = finBookAccountPayableItemMapper.selectFinBookAccountPayableItemById(item.getAccountItemSid());
                    finBookAccountPayableItem.setCurrencyAmountTaxHxz(finBookAccountPayableItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookAccountPayableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookAccountPayableItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookAccountPayableItemMapper.updateAllById(finBookAccountPayableItem);
                }
                //付款
                if (ConstantsFinance.BOOK_TYPE_FK.equals(item.getBookType())) {
                    FinBookPaymentItem finBookPaymentItem = finBookPaymentItemMapper.selectById(item.getAccountItemSid());
                    finBookPaymentItem.setCurrencyAmountTaxHxz(finBookPaymentItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookPaymentItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookPaymentItemMapper.updateAllById(finBookPaymentItem);
                }
                //供应商扣款
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())) {
                    FinBookVendorDeductionItem finBookVendorDeductionItem = finBookVendorDeductionItemMapper.selectFinBookVendorDeductionItemById(item.getAccountItemSid());
                    finBookVendorDeductionItem.setCurrencyAmountTaxHxz(finBookVendorDeductionItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookVendorDeductionItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookVendorDeductionItemMapper.updateAllById(finBookVendorDeductionItem);
                }
                //供应商调账
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())) {
                    FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = finBookVendorAccountAdjustItemMapper.selectFinBookVendorAccountAdjustItemById(item.getAccountItemSid());
                    finBookVendorAccountAdjustItem.setCurrencyAmountTaxHxz(finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                    finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                    finBookVendorAccountAdjustItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookVendorAccountAdjustItemMapper.updateAllById(finBookVendorAccountAdjustItem);
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
    public void insertBook(FinVendorAccountBalanceBill bill) {
        //确认则生成流水账
        if (CollectionUtils.isNotEmpty(bill.getItemList())
                && bill.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //流水主表
            FinBookVendorAccountBalance book = new FinBookVendorAccountBalance();
            BeanCopyUtils.copyProperties(bill, book);
            Calendar cal = Calendar.getInstance();
            book.setDocumentDate(new Date()).setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                    .setPaymentYear(cal.get(Calendar.YEAR))
                    .setPaymentMonth(cal.get(Calendar.MONTH) + 1).setBookType(ConstantsFinance.BOOK_TYPE_VZHD).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_VZHD);
            bookMapper.insert(book);
            //流水明细表(多条)
            bill.getItemList().forEach(item -> {
                FinBookVendorAccountBalanceItem bookItem = new FinBookVendorAccountBalanceItem();
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
                ;
                bookItemMapper.insert(bookItem);
                //应付暂估
                if (ConstantsFinance.BOOK_TYPE_YFZG.equals(item.getBookType())) {
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectFinBookPaymentEstimationItemById(item.getAccountItemSid());
                    //后确认操作，暂估流水核销中金额减去明细金额，然后将明细金额加入暂估流水原来已核销的金额
                    //得到商和余数
                    BigDecimal result[] = item.getCurrencyAmountTax().divideAndRemainder(finBookPaymentEstimationItem.getPriceTax());
                    if (result[1].compareTo(BigDecimal.ZERO) != 0){
                        throw new BaseException("应付暂估流水的金额应为采购价的整数倍，请核实");
                    }
                    finBookPaymentEstimationItem.setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    finBookPaymentEstimationItem.setCurrencyAmountTaxYhx(finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookPaymentEstimationItemService.updateByAmountTax(finBookPaymentEstimationItem);
                }
                //应付
                if (ConstantsFinance.BOOK_TYPE_YINGF.equals(item.getBookType())) {
                    FinBookAccountPayableItem finBookAccountPayableItem = finBookAccountPayableItemMapper.selectFinBookAccountPayableItemById(item.getAccountItemSid());
                    finBookAccountPayableItem.setCurrencyAmountTaxYhx(finBookAccountPayableItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookAccountPayableItem.setCurrencyAmountTaxHxz(finBookAccountPayableItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookAccountPayableItem.getCurrencyAmountTaxYhx().compareTo(finBookAccountPayableItem.getCurrencyAmountTaxYingf()) == 0) {
                        finBookAccountPayableItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookAccountPayableItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0) {
//                        finBookAccountPayableItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookAccountPayableItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookAccountPayableItemMapper.updateAllById(finBookAccountPayableItem);
                }
                //付款
                if (ConstantsFinance.BOOK_TYPE_FK.equals(item.getBookType())) {
                    FinBookPaymentItem finBookPaymentItem = finBookPaymentItemMapper.selectFinBookPaymentItemById(item.getAccountItemSid());
                    finBookPaymentItem.setCurrencyAmountTaxYhx(finBookPaymentItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookPaymentItem.setCurrencyAmountTaxHxz(finBookPaymentItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookPaymentItem.getCurrencyAmountTaxYhx().compareTo(finBookPaymentItem.getCurrencyAmountTaxFk()) == 0) {
                        finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookPaymentItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0) {
//                        finBookPaymentItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookPaymentItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookPaymentItemMapper.updateAllById(finBookPaymentItem);
                }
                //供应商扣款
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())) {
                    FinBookVendorDeductionItem finBookVendorDeductionItem = finBookVendorDeductionItemMapper.selectFinBookVendorDeductionItemById(item.getAccountItemSid());
                    finBookVendorDeductionItem.setCurrencyAmountTaxYhx(finBookVendorDeductionItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookVendorDeductionItem.setCurrencyAmountTaxHxz(finBookVendorDeductionItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookVendorDeductionItem.getCurrencyAmountTaxYhx().compareTo(finBookVendorDeductionItem.getCurrencyAmountTaxKk()) == 0) {
                        finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookVendorDeductionItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0) {
//                        finBookVendorDeductionItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookVendorDeductionItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookVendorDeductionItemMapper.updateAllById(finBookVendorDeductionItem);
                }
                //供应商调账
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())) {
                    FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = finBookVendorAccountAdjustItemMapper.selectFinBookVendorAccountAdjustItemById(item.getAccountItemSid());
                    finBookVendorAccountAdjustItem.setCurrencyAmountTaxYhx(finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax()));
                    finBookVendorAccountAdjustItem.setCurrencyAmountTaxHxz(finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                    if (finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx().compareTo(finBookVendorAccountAdjustItem.getCurrencyAmountTaxTz()) == 0) {
                        finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
//                    if (finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0) {
//                        finBookVendorAccountAdjustItem.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    finBookVendorAccountAdjustItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    finBookVendorAccountAdjustItemMapper.updateAllById(finBookVendorAccountAdjustItem);
                }
            });
        }
    }

    @Override
    public List<FinVendorAccountBalanceBillItem> bookList(FinVendorAccountBalanceBillItem request) {
        List<FinVendorAccountBalanceBillItem> responseList = new ArrayList<>();
        //应付暂估
        if (ConstantsFinance.BOOK_TYPE_YFZG.equals(request.getBookType())) {
            FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
            BeanUtil.copyProperties(request, finBookPaymentEstimation);
            List<FinBookPaymentEstimation> bookList = finBookPaymentEstimationMapper.getReportForm(finBookPaymentEstimation);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookPaymentEstimationSid())
                        .setAccountDocumentCode(item.getBookPaymentEstimationCode())
                        .setAccountItemSid(item.getBookPaymentEstimationItemSid())
                        .setCurrencyAmountTax(null)
                        .setItemNum(item.getItemNum())
                        .setPriceTax(item.getPriceTax())
                        .setCurrencyAmountTaxDhx(item.getCurrencyAmountTaxDhx() == null ? BigDecimal.ZERO : item.getCurrencyAmountTaxDhx())
                        .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz() == null ? BigDecimal.ZERO : item.getCurrencyAmountTaxHxz())
                        .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx() == null ? BigDecimal.ZERO : item.getCurrencyAmountTaxYhx())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax());
                responseList.add(bill);
            });
        }
        //应付
        if (ConstantsFinance.BOOK_TYPE_YINGF.equals(request.getBookType())) {
            FinBookAccountPayable finBookAccountPayable = new FinBookAccountPayable();
            BeanUtil.copyProperties(request, finBookAccountPayable);
            List<FinBookAccountPayable> bookList = finBookAccountPayableMapper.getReportForm(finBookAccountPayable);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountPayableSid())
                        .setAccountDocumentCode(item.getBookAccountPayableCode())
                        .setAccountItemSid(item.getBookAccountPayableItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxYingf())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //付款
        if (ConstantsFinance.BOOK_TYPE_FK.equals(request.getBookType())) {
            FinBookPayment finBookPayment = new FinBookPayment();
            BeanUtil.copyProperties(request, finBookPayment);
            List<FinBookPayment> bookList = finBookPaymentMapper.getReportForm(finBookPayment);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookPaymentSid())
                        .setAccountDocumentCode(item.getBookPaymentCode())
                        .setAccountItemSid(item.getBookPaymentItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxFk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //供应商扣款
        if (ConstantsFinance.BOOK_TYPE_VKK.equals(request.getBookType())) {
            FinBookVendorDeduction finBookVendorDeduction = new FinBookVendorDeduction();
            BeanUtil.copyProperties(request, finBookVendorDeduction);
            List<FinBookVendorDeduction> bookList = finBookVendorDeductionMapper.getReportForm(finBookVendorDeduction);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookDeductionSid())
                        .setAccountDocumentCode(item.getBookDeductionCode())
                        .setAccountItemSid(item.getBookDeductionItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxKk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //供应商调账
        if (ConstantsFinance.BOOK_TYPE_VTZ.equals(request.getBookType())) {
            FinBookVendorAccountAdjust finBookVendorAccountAdjust = new FinBookVendorAccountAdjust();
            BeanUtil.copyProperties(request, finBookVendorAccountAdjust);
            List<FinBookVendorAccountAdjust> bookList = finBookVendorAccountAdjustMapper.getReportForm(finBookVendorAccountAdjust);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
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
        FinVendorAccountBalanceBill bill = finVendorAccountBalanceBillMapper.selectById(accountBalanceSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookVendorAccountBalance> bookList = bookMapper.getReportForm(new FinBookVendorAccountBalance().setReferDocSid(bill.getAccountBalanceBillSid()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            bookMapper.updateById(item);
        });
        int i = finVendorAccountBalanceBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0){
            returnBack(accountBalanceSid,ConstantsEms.CHECK_STATUS);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getAccountBalanceBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }
}
