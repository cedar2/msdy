package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPurPurchasePriceService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.plug.domain.ConAccountMethodGroup;
import com.platform.ems.plug.mapper.ConAccountMethodGroupMapper;
import com.platform.ems.service.IFinPurchaseInvoiceService;
import com.platform.ems.util.MongodbUtil;

import cn.hutool.core.bean.BeanUtil;

/**
 * 采购发票Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseInvoiceServiceImpl extends ServiceImpl<FinPurchaseInvoiceMapper, FinPurchaseInvoice> implements IFinPurchaseInvoiceService {
    @Autowired
    private FinPurchaseInvoiceMapper finPurchaseInvoiceMapper;
    @Autowired
    private FinPurchaseInvoiceItemMapper finPurchaseInvoiceItemMapper;
    @Autowired
    private FinPurchaseInvoiceAttachmentMapper finPurchaseInvoiceAttachmentMapper;
    @Autowired
    private FinPurchaseInvoiceDiscountMapper finPurchaseInvoiceDiscountMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;
    @Autowired
    private FinBookAccountPayableMapper finBookAccountPayableMapper;
    @Autowired
    private FinBookAccountPayableItemMapper finBookAccountPayableItemMapper;
    @Autowired
    private FinBookVendorDeductionMapper bookVendorDeductionMapper;
    @Autowired
    private FinBookVendorDeductionItemMapper bookVendorDeductionItemMapper;
    @Autowired
    private FinBookVendorAccountAdjustMapper bookVendorAccountAdjustMapper;
    @Autowired
    private FinBookVendorAccountAdjustItemMapper bookVendorAccountAdjustItemMapper;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private PurPurchaseContractMapper purPurchaseContractMapper;
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;

    @Autowired
    private FinBookVendorDeductionMapper finBookVendorDeductionMapper;
    @Autowired
    private FinBookVendorAccountAdjustMapper finBookVendorAccountAdjustMapper;
    @Autowired
    private IPurPurchasePriceService purPurchasePriceService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;

    private static final String TITLE = "采购发票";

    /**
     * 查询采购发票
     *
     * @param purchaseInvoiceSid 采购发票ID
     * @return 采购发票
     */
    @Override
    public FinPurchaseInvoice selectFinPurchaseInvoiceById(Long purchaseInvoiceSid) {
        FinPurchaseInvoice finPurchaseInvoice = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceById(purchaseInvoiceSid);
        if (finPurchaseInvoice == null) {
            return null;
        }
        //采购发票-明细对象
        FinPurchaseInvoiceItem finPurchaseInvoiceItem = new FinPurchaseInvoiceItem();
        finPurchaseInvoiceItem.setPurchaseInvoiceSid(purchaseInvoiceSid);
        List<FinPurchaseInvoiceItem> finPurchaseInvoiceItemList =
                finPurchaseInvoiceItemMapper.selectFinPurchaseInvoiceItemList(finPurchaseInvoiceItem);
        for (FinPurchaseInvoiceItem item : finPurchaseInvoiceItemList){
            //获取当前采购价
            if (item.getPurchaseOrderSid() != null){
                PurPurchasePrice price = new PurPurchasePrice();
                price.setVendorSid(item.getVendorSid()).setCompanySid(item.getCompanySid()).setMaterialSid(item.getMaterialSid()).setSku2Sid(item.getSku2Sid())
                        .setSku1Sid(item.getSku1Sid()).setRawMaterialMode(item.getRawMaterialMode()).setPurchaseMode(item.getPurchaseMode());
                PurPurchasePriceItem priceItem = purPurchasePriceService.getNewPurchase(price);
                if (priceItem != null){
                    if (priceItem.getPurchasePriceTax() == null){
                        priceItem.setPurchasePriceTax(BigDecimal.ZERO);
                    }
                    if (priceItem.getTaxRate() == null){
                        priceItem.setTaxRate(BigDecimal.ZERO);
                    }
                    item.setCurrentPrice(priceItem.getPurchasePriceTax().divide(priceItem.getTaxRate().add(BigDecimal.ONE),4, BigDecimal.ROUND_HALF_UP)).setCurrentPriceTax(priceItem.getPurchasePriceTax());
                }
            }
            item.setCurrencyAmountTaxLeft(item.getCurrencyAmountTaxLeft().add(item.getCurrencyAmountTax()))
                    .setQuantityLeft(item.getQuantityLeft().add(item.getQuantity()));
        }
        //采购发票-附件对象
        FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment = new FinPurchaseInvoiceAttachment();
        finPurchaseInvoiceAttachment.setPurchaseInvoiceSid(purchaseInvoiceSid);
        List<FinPurchaseInvoiceAttachment> finPurchaseInvoiceAttachmentList =
                finPurchaseInvoiceAttachmentMapper.selectFinPurchaseInvoiceAttachmentList(finPurchaseInvoiceAttachment);
        //采购发票-折扣对象
        FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount = new FinPurchaseInvoiceDiscount();
        finPurchaseInvoiceDiscount.setPurchaseInvoiceSid(purchaseInvoiceSid);
        List<FinPurchaseInvoiceDiscount> finPurchaseInvoiceDiscountList =
                finPurchaseInvoiceDiscountMapper.selectFinPurchaseInvoiceDiscountList(finPurchaseInvoiceDiscount);
        if (finPurchaseInvoiceDiscountList != null) {
            finPurchaseInvoiceDiscountList.forEach(discount -> {
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(discount.getBookType())) {
                    FinBookVendorDeduction finBookVendor = finBookVendorDeductionMapper.selectFinBookVendorDeductionById(discount.getAccountDocumentSid());
                    discount.setVendorName(finBookVendor.getVendorName())
                            .setProductSeasonName(finBookVendor.getProductSeasonName());
                    discount.setCurrencyAmountTaxHxz(finBookVendor.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxYhx(finBookVendor.getCurrencyAmountTaxYhx());
                    discount.setCurrencyAmountTaxYingD(finBookVendor.getCurrencyAmountTaxKk())
                            .setCurrencyAmountTaxDaiD(finBookVendor.getCurrencyAmountTaxDhx().add(discount.getCurrencyAmountTax()));
                }
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(discount.getBookType())) {
                    FinBookVendorAccountAdjust finBookVendor = finBookVendorAccountAdjustMapper.selectFinBookVendorAccountAdjustById(discount.getAccountDocumentSid());
                    discount.setVendorName(finBookVendor.getVendorName())
                            .setProductSeasonName(finBookVendor.getProductSeasonName());
                    discount.setCurrencyAmountTaxHxz(finBookVendor.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxYhx(finBookVendor.getCurrencyAmountTaxYhx());
                    discount.setCurrencyAmountTaxYingD(finBookVendor.getCurrencyAmountTaxTz())
                            .setCurrencyAmountTaxDaiD(finBookVendor.getCurrencyAmountTaxDhx().add(discount.getCurrencyAmountTax()));
                }
            });
        }
        finPurchaseInvoice.setFinPurchaseInvoiceItemList(finPurchaseInvoiceItemList);
        finPurchaseInvoice.setAttachmentList(finPurchaseInvoiceAttachmentList);
        finPurchaseInvoice.setFinPurchaseInvoiceDiscountList(finPurchaseInvoiceDiscountList);
        //查询日志信息
        MongodbUtil.find(finPurchaseInvoice);
        return finPurchaseInvoice;
    }

    /**
     * 查询采购发票列表
     *
     * @param finPurchaseInvoice 采购发票
     * @return 采购发票
     */
    @Override
    public List<FinPurchaseInvoice> selectFinPurchaseInvoiceList(FinPurchaseInvoice finPurchaseInvoice) {
        List<FinPurchaseInvoice> response = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceList(finPurchaseInvoice);
        return response;
    }

    /**
     * 新增采购发票
     * 需要注意编码重复校验
     *
     * @param finPurchaseInvoice 采购发票
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPurchaseInvoice(FinPurchaseInvoice finPurchaseInvoice) {
        //设置确认信息
        setConfirmInfo(finPurchaseInvoice);
        //校验金额
        judgePrice(finPurchaseInvoice);
        finPurchaseInvoice.setIsFinanceVerify(ConstantsEms.NO);
        int row = finPurchaseInvoiceMapper.insert(finPurchaseInvoice);
        FinPurchaseInvoice invoice = finPurchaseInvoiceMapper.selectById(finPurchaseInvoice.getPurchaseInvoiceSid());
        finPurchaseInvoice.setPurchaseInvoiceCode(invoice.getPurchaseInvoiceCode());
        if (row > 0) {
            insertChild(finPurchaseInvoice.getFinPurchaseInvoiceItemList(),
                    finPurchaseInvoice.getFinPurchaseInvoiceDiscountList(),
                    finPurchaseInvoice.getAttachmentList(),
                    finPurchaseInvoice.getPurchaseInvoiceSid(), finPurchaseInvoice.getHandleStatus());
            insertBook(finPurchaseInvoice,finPurchaseInvoice.getHandleStatus());
            updateBook(finPurchaseInvoice);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finPurchaseInvoice.getHandleStatus())) {
                finPurchaseInvoice = finPurchaseInvoiceMapper.selectById(finPurchaseInvoice.getPurchaseInvoiceSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_purchase_invoice")
                        .setDocumentSid(finPurchaseInvoice.getPurchaseInvoiceSid());
                sysTodoTask.setTitle("采购发票: " + finPurchaseInvoice.getPurchaseInvoiceCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finPurchaseInvoice.getPurchaseInvoiceCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            if (ConstantsEms.CHECK_STATUS.equals(finPurchaseInvoice.getHandleStatus())) {
                finPurchaseInvoice = finPurchaseInvoiceMapper.selectById(finPurchaseInvoice.getPurchaseInvoiceSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_purchase_invoice")
                        .setDocumentSid(finPurchaseInvoice.getPurchaseInvoiceSid());
                sysTodoTask.setTitle("采购发票: " + finPurchaseInvoice.getPurchaseInvoiceCode() + " 的发票签收状态为“未签收”，请及时处理！")
                        .setDocumentCode(String.valueOf(finPurchaseInvoice.getPurchaseInvoiceCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //操作日志
            MongodbDeal.insert(finPurchaseInvoice.getPurchaseInvoiceSid(), finPurchaseInvoice.getHandleStatus(), null, TITLE,null);
        }
        return row;
    }

    /**
     * 修改采购发票
     *
     * @param finPurchaseInvoice 采购发票
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPurchaseInvoice(FinPurchaseInvoice finPurchaseInvoice) {
        Long purchaseInvoiceSid = finPurchaseInvoice.getPurchaseInvoiceSid();
        FinPurchaseInvoice invoice = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceById(purchaseInvoiceSid);
        String handleStatus = invoice.getHandleStatus();
        //设置确认信息
        setConfirmInfo(finPurchaseInvoice);
        int row = finPurchaseInvoiceMapper.updateAllById(finPurchaseInvoice);
        if (row > 0) {
            deleteItem(finPurchaseInvoice.getPurchaseInvoiceSid(), handleStatus);
            judgePrice(finPurchaseInvoice);
            insertChild(finPurchaseInvoice.getFinPurchaseInvoiceItemList(), finPurchaseInvoice.getFinPurchaseInvoiceDiscountList(),
                    finPurchaseInvoice.getAttachmentList(), finPurchaseInvoice.getPurchaseInvoiceSid(), finPurchaseInvoice.getHandleStatus());
            insertBook(finPurchaseInvoice,finPurchaseInvoice.getHandleStatus());
            updateBook(finPurchaseInvoice);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finPurchaseInvoice.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finPurchaseInvoice.getPurchaseInvoiceSid()));
            }
            if (ConstantsEms.CHECK_STATUS.equals(finPurchaseInvoice.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_purchase_invoice")
                        .setDocumentSid(finPurchaseInvoice.getPurchaseInvoiceSid());
                sysTodoTask.setTitle("采购发票: " + finPurchaseInvoice.getPurchaseInvoiceCode() + " 的发票签收状态为“未签收”，请及时处理！")
                        .setDocumentCode(String.valueOf(finPurchaseInvoice.getPurchaseInvoiceCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(invoice, finPurchaseInvoice);
            MongodbDeal.update(finPurchaseInvoice.getPurchaseInvoiceSid(), invoice.getHandleStatus(),finPurchaseInvoice.getHandleStatus(),msgList,TITLE,null);
        }
        return row;
    }

    /**
     * 采购发票变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(FinPurchaseInvoice finPurchaseInvoice) {
        Long purchaseInvoiceSid = finPurchaseInvoice.getPurchaseInvoiceSid();
        FinPurchaseInvoice invoice = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceById(purchaseInvoiceSid);
        String handleStatus = invoice.getHandleStatus();
        //验证是否确认状态
        if (!(ConstantsEms.CHECK_STATUS).equals(invoice.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }
        //设置确认信息
        setConfirmInfo(finPurchaseInvoice);
        int row = finPurchaseInvoiceMapper.updateAllById(finPurchaseInvoice);
        if (row > 0) {
            deleteItem(finPurchaseInvoice.getPurchaseInvoiceSid(), handleStatus);
            judgePrice(finPurchaseInvoice);
            insertChild(finPurchaseInvoice.getFinPurchaseInvoiceItemList(), finPurchaseInvoice.getFinPurchaseInvoiceDiscountList(),
                    finPurchaseInvoice.getAttachmentList(), finPurchaseInvoice.getPurchaseInvoiceSid(), finPurchaseInvoice.getHandleStatus());
            insertBook(finPurchaseInvoice,finPurchaseInvoice.getHandleStatus());
            updateBook(finPurchaseInvoice);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(invoice, finPurchaseInvoice);
            MongodbUtil.insertUserLog(finPurchaseInvoice.getPurchaseInvoiceSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return 1;
    }

    /**
     * 采购发票批量确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(FinPurchaseInvoice finPurchaseInvoice) {
        String handleStatus = finPurchaseInvoice.getHandleStatus();
        //采购发票sids
        int row = 0;
        Long[] purchaseInvoiceSids = finPurchaseInvoice.getPurchaseInvoiceSids();
        //       FinPurchaseInvoice params = new FinPurchaseInvoice();
        if (purchaseInvoiceSids != null && purchaseInvoiceSids.length > 0) {
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finPurchaseInvoice.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, purchaseInvoiceSids));
            }
            for (Long sid : purchaseInvoiceSids) {
                finPurchaseInvoice.setPurchaseInvoiceSid(sid);
                //获取主表明细表内容并生成流水
                finPurchaseInvoice = this.selectFinPurchaseInvoiceById(sid);
                if (finPurchaseInvoice.getHandleStatus().equals(handleStatus)){
                    throw new CustomException("请不要重复操作！");
                }
                finPurchaseInvoice.setHandleStatus(handleStatus);
                this.setConfirmInfo(finPurchaseInvoice);
                int i = finPurchaseInvoiceMapper.updateById(finPurchaseInvoice);
                row += i;
                if (i == 0) {
                    throw new CustomException(sid + "确认失败,请联系管理员");
                }
                //明细表
                insertBook(finPurchaseInvoice,finPurchaseInvoice.getHandleStatus());
                //折扣表
                updateBook(finPurchaseInvoice);
                //确认待办
                if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                    SysUser user = userService.selectSysUserByName(finPurchaseInvoice.getCreatorAccount());
                    //确认待办
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName("s_fin_purchase_invoice")
                            .setNoticeDate(new Date())
                            .setUserId(user.getUserId());
                    sysTodoTask.setDocumentSid(finPurchaseInvoice.getPurchaseInvoiceSid());
                    sysTodoTask.setTitle("采购发票: " + finPurchaseInvoice.getPurchaseInvoiceCode() + " 的发票签收状态为“未签收”，请及时处理！")
                            .setDocumentCode(String.valueOf(finPurchaseInvoice.getPurchaseInvoiceCode()));
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
                //插入日志
//                List<OperMsg> msgList = new ArrayList<>();
//                MongodbDeal.check(finPurchaseInvoice.getPurchaseInvoiceSid(), finPurchaseInvoice.getHandleStatus(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 批量删除采购发票
     *
     * @param purchaseInvoiceSids 需要删除的采购发票ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPurchaseInvoiceByIds(List<Long> purchaseInvoiceSids) {
        if (purchaseInvoiceSids.size() == 0){
            throw new BaseException("请选择单据！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectList(new QueryWrapper<FinPurchaseInvoice>().lambda()
                .in(FinPurchaseInvoice::getPurchaseInvoiceSid,purchaseInvoiceSids)
                .notIn(FinPurchaseInvoice::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(invoiceList)){
            throw new BaseException("仅保存状态或者已退回状态才可删除！");
        }
        purchaseInvoiceSids.forEach(sid -> {
            FinPurchaseInvoice invoice = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceById(sid);
            String handleStatus = invoice.getHandleStatus();
            deleteItem(sid, handleStatus);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, purchaseInvoiceSids));
        int row = finPurchaseInvoiceMapper.deleteBatchIds(purchaseInvoiceSids);
        return row;
    }

    /**
     * 设置确认信息
     */
    @Override
    public void setConfirmInfo(FinPurchaseInvoice entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || entity.getHandleStatus().equals(ConstantsEms.SUBMIT_STATUS)) {
            if (CollectionUtils.isEmpty(entity.getFinPurchaseInvoiceItemList())) {
                throw new CustomException("此操作明细不能为空！");
            }
            if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                entity.setConfirmDate(new Date());
            }
            BigDecimal sum = BigDecimal.ZERO; //求明细+折扣明细的所有和
            sum = entity.getFinPurchaseInvoiceItemList().parallelStream().map(FinPurchaseInvoiceItem::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
            sum = sum.add(entity.getFinPurchaseInvoiceDiscountList().parallelStream().map(FinPurchaseInvoiceDiscount::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimal::add));
            if (entity.getInvoiceType().equals(ConstantsFinance.INVOICE_TYPE_BLUE)){
                if (sum.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new CustomException("发票类型为蓝票的票面总金额不能小于等于0！");
                }
            }
            entity.setTotalCurrencyAmountTax(sum);
        }
        if (HandleStatus.CONFIRMED.getCode().equals(entity.getHandleStatus()) ||
                HandleStatus.REDDASHED.getCode().equals(entity.getHandleStatus())){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            entity.setMonthAccountPeriod(sdf.format(date));
        }
    }

    /**
     * 校验金额是否大于待开票金额
     */
    private void judgePrice(FinPurchaseInvoice entity) {
        //明细
        List<FinPurchaseInvoiceItem> itemList = entity.getFinPurchaseInvoiceItemList();
        if (CollectionUtils.isNotEmpty(itemList)){
            Long methodGroup = null;
            for (FinPurchaseInvoiceItem item : itemList) {
                //获取应付暂估中的待核销金额
                FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper
                        .selectFinBookPaymentEstimationItemById(item.getAccountItemSid() == null ? item.getBookPaymentEstimationItemSid() : item.getAccountItemSid());
                if (item.getQuantityLeft().compareTo(BigDecimal.ZERO) < 0 && item.getQuantity().compareTo(BigDecimal.ZERO) >= 0){
                    throw new CustomException("明细中的负向应付暂估流水用来开票的数量只能输入负数！");
                }
                if (item.getQuantityLeft().compareTo(BigDecimal.ZERO) > 0 && item.getQuantity().compareTo(BigDecimal.ZERO) <= 0){
                    throw new CustomException("明细中的正向应付暂估流水用来开票的数量只能输入正数！");
                }
                //校验
                if (item.getQuantity().abs().compareTo(finBookPaymentEstimationItem.getQuantityLeft().abs()) > 0 ){
                    throw new CustomException("明细中的开票量的绝对值不能大于待开票量的绝对值！");
                }
                //校验收付款方式组合
                if (item.getAccountsMethodGroup() != null) {
                    if (methodGroup != null) {
                        if (!methodGroup.equals(item.getAccountsMethodGroup())) {
                            throw new CustomException("明细中的收付款方式组合必须一致，请核实");
                        }
                    }
                    methodGroup = item.getAccountsMethodGroup();
                }
                if (item.getTaxRate().compareTo(entity.getTaxRate()) != 0){
                    throw new BaseException("存在明细的税率与发票的税率不一致，请核实！");
                }
            }
            if (methodGroup == null) {
                throw new CustomException("操作失败,请确保明细中有收付款结算方式！");
            }
        }
        //折扣
        List<FinPurchaseInvoiceDiscount> discountList = entity.getFinPurchaseInvoiceDiscountList();
        if (CollectionUtils.isNotEmpty(discountList)){
            discountList.forEach(item->{
                BigDecimal dhx = new BigDecimal(-1);
                //获取供应商扣款流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())){
                    FinBookVendorDeductionItem finBookVendorDeductionItem = bookVendorDeductionItemMapper
                            .selectFinBookVendorDeductionItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("折扣中的扣款流水用来抵扣的金额只能输入负数！");
                    }
                    dhx = finBookVendorDeductionItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取供应商调账流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())){
                    FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = bookVendorAccountAdjustItemMapper
                            .selectFinBookVendorAccountAdjustItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYingD().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("折扣中的负向调账流水用来抵扣的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYingD().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("折扣中的正向调账流水用来抵扣的金额只能输入正数！");
                    }
                    dhx = finBookVendorAccountAdjustItem.getCurrencyAmountTaxDhx().abs();
                }
                //校验
                if (dhx.compareTo(BigDecimal.ZERO) >= 0 && item.getCurrencyAmountTax().abs().compareTo(dhx) > 0){
                    throw new CustomException("折扣中的折扣金额的绝对值不能大于待折扣金额的绝对值！");
                }
            });
        }
    }


    /**
     * 删除明细表
     *
     * @param finPurchaseInvoiceSid 采购发票的sid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long finPurchaseInvoiceSid, String handleStatus) {
        //回退来源流水
        returnBack(finPurchaseInvoiceSid, handleStatus);
        //明细表
        QueryWrapper<FinPurchaseInvoiceItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("purchase_invoice_sid", finPurchaseInvoiceSid);
        finPurchaseInvoiceItemMapper.delete(itemQueryWrapper);
        //折扣表
        QueryWrapper<FinPurchaseInvoiceDiscount> discountQueryWrapper = new QueryWrapper<>();
        discountQueryWrapper.eq("purchase_invoice_sid", finPurchaseInvoiceSid);
        finPurchaseInvoiceDiscountMapper.delete(discountQueryWrapper);
        //附件表
        QueryWrapper<FinPurchaseInvoiceAttachment> atmQueryWrapper = new QueryWrapper<>();
        atmQueryWrapper.eq("purchase_invoice_sid", finPurchaseInvoiceSid);
        finPurchaseInvoiceAttachmentMapper.delete(atmQueryWrapper);
        //应付流水表
        List<FinBookAccountPayableItem> finBookAccountPayableItemList = finBookAccountPayableItemMapper.selectList(
                new QueryWrapper<FinBookAccountPayableItem>().lambda().eq(FinBookAccountPayableItem::getReferDocSid, finPurchaseInvoiceSid));
        if (CollectionUtils.isNotEmpty(finBookAccountPayableItemList)) {
            finBookAccountPayableItemList.forEach(finBookAccountPayableItem -> {
                finBookAccountPayableItemMapper.deleteById(finBookAccountPayableItem.getBookAccountPayableItemSid());
                finBookAccountPayableMapper.deleteById(finBookAccountPayableItem.getBookAccountPayableSid());
            });
        }
    }

    /**
     * 新增明细表,折扣表,附件表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertChild(List<FinPurchaseInvoiceItem> itemList, List<FinPurchaseInvoiceDiscount> discountList, List<FinPurchaseInvoiceAttachment> atmList, Long sid, String handleStatus) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinPurchaseInvoiceItem item : itemList) {
                if (item.getCurrencyAmountTax() == null){
                    item.setCurrencyAmountTax(BigDecimal.ZERO);
                }
                if (item.getCurrencyAmount() == null){
                    item.setCurrencyAmount(BigDecimal.ZERO);
                }
                item.setItemNum((long)i++).setTaxAmount(item.getCurrencyAmountTax().subtract(item.getCurrencyAmount()));
                if (item.getPurchaseInvoiceItemSid() != null) {
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
                item.setPurchaseInvoiceSid(sid)
                        .setAccountDocumentSid(item.getBookPaymentEstimationSid() == null ? item.getAccountDocumentSid() : item.getBookPaymentEstimationSid())
                        .setAccountDocumentCode(item.getBookPaymentEstimationCode() == null ? item.getAccountDocumentCode() : item.getBookPaymentEstimationCode())
                        .setAccountItemSid(item.getBookPaymentEstimationItemSid() == null ? item.getAccountItemSid() : item.getBookPaymentEstimationItemSid());
                if (!HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                    //修改应付暂估
                    //应付暂估
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectById(item.getAccountItemSid());
                    finBookPaymentEstimationItem.setBookPaymentEstimationItemSid(item.getAccountItemSid())
                            .setQuantityHxz(finBookPaymentEstimationItem.getQuantityHxz().add(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销数量
                            .setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()))   //核销金额（含税）
                            .setUpdateDate(new Date())
                            .setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    finBookPaymentEstimationItem = setClearStatus(finBookPaymentEstimationItem);
                    finBookPaymentEstimationItemMapper.updateAllById(finBookPaymentEstimationItem); //修改
                }
            }
            finPurchaseInvoiceItemMapper.inserts(itemList);
        }
        if (CollectionUtils.isNotEmpty(discountList)) {
            int j = 1;
            for (FinPurchaseInvoiceDiscount item : discountList) {
                item.setItemNum(j++);
                if (item.getPurchaseInvoiceDiscountSid() != null) {
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
                item.setPurchaseInvoiceSid(sid)
                        .setAccountDocumentSid(item.getAccountDocumentSid())
                        .setAccountDocumentCode(item.getAccountDocumentCode())
                        .setAccountItemSid(item.getAccountItemSid());
                if (!HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                    if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())) {
                        FinBookVendorDeductionItem finBookVendorDeductionItem = bookVendorDeductionItemMapper.selectById(item.getAccountItemSid());
                        finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                        finBookVendorDeductionItem.setCurrencyAmountTaxHxz(finBookVendorDeductionItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                        finBookVendorDeductionItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        bookVendorDeductionItemMapper.updateAllById(finBookVendorDeductionItem);
                    }
                    if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())) {
                        FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = bookVendorAccountAdjustItemMapper.selectById(item.getAccountItemSid());
                        finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                        finBookVendorAccountAdjustItem.setCurrencyAmountTaxHxz(finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                        finBookVendorAccountAdjustItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        bookVendorAccountAdjustItemMapper.updateAllById(finBookVendorAccountAdjustItem);
                    }
                }
            }
            finPurchaseInvoiceDiscountMapper.inserts(discountList);
        }
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setPurchaseInvoiceSid(sid);
            });
            finPurchaseInvoiceAttachmentMapper.inserts(atmList);
        }
    }

    /**
     * 新增应付流水并修改应付暂估
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBook(FinPurchaseInvoice finPurchaseInvoice, String handleStatus) {
        //确认则生成流水账
        if (CollectionUtils.isNotEmpty(finPurchaseInvoice.getFinPurchaseInvoiceItemList())
                && (finPurchaseInvoice.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)
                || finPurchaseInvoice.getHandleStatus().equals(HandleStatus.REDDASHED.getCode()))) {
            //如果是红冲的
            //红冲流水的“参考应付流水”为原发票生成的应付流水号；
            Long referAdvance = null;
            Long referMiddle = null;
            Long referRemain = null;
            if (HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                FinBookAccountPayableItem bookItem = new FinBookAccountPayableItem();
                bookItem.setReferDocSid(finPurchaseInvoice.getReferenceInvoice());
                List<FinBookAccountPayableItem> bookItemList = finBookAccountPayableItemMapper.selectList(new QueryWrapper<FinBookAccountPayableItem>()
                        .lambda().eq(FinBookAccountPayableItem::getReferDocSid,finPurchaseInvoice.getReferenceInvoice()));
                if (CollectionUtils.isNotEmpty(bookItemList)){
                    //预期流水，中期流水，尾期流水
                    for (FinBookAccountPayableItem item : bookItemList) {
                        if (ConstantsFinance.BOOK_SOURCE_CAT_PFPYF.equals(item.getBookSourceCategory())) {
                            referAdvance = item.getBookAccountPayableSid();
                        }
                        if (ConstantsFinance.BOOK_SOURCE_CAT_PFPZQ.equals(item.getBookSourceCategory())) {
                            referMiddle = item.getBookAccountPayableSid();
                        }
                        if (ConstantsFinance.BOOK_SOURCE_CAT_PFPWQ.equals(item.getBookSourceCategory())) {
                            referRemain = item.getBookAccountPayableSid();
                        }
                    }
                }
            }
            BigDecimal advance = BigDecimal.ZERO; //预付款
            BigDecimal middle = BigDecimal.ZERO;  // 中期款
            BigDecimal remain = BigDecimal.ZERO;  //尾款
            ConAccountMethodGroup conAccountMethodGroup = new ConAccountMethodGroup();
            Long contractSid = null;
            Long orderSid = null;
            Long orderCode = null;
            String contractCode = null;
            int i = 1, flag = 1;
            int size = finPurchaseInvoice.getFinPurchaseInvoiceItemList().size();
            for (FinPurchaseInvoiceItem item : finPurchaseInvoice.getFinPurchaseInvoiceItemList()) {
                //如果不是红冲才处理
                if (!HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                    //修改应付暂估
                    //应付暂估
                    //减去核销中，增加已核销
                    FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectById(item.getAccountItemSid());
                    finBookPaymentEstimationItem.setBookPaymentEstimationItemSid(item.getAccountItemSid())
                            .setQuantityYhx(finBookPaymentEstimationItem.getQuantityYhx().add(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销数量
                            .setCurrencyAmountTaxYhx(finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()))   //核销金额（含税）
                            .setQuantityHxz(finBookPaymentEstimationItem.getQuantityHxz().subtract(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销数量
                            .setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()))   //核销金额（含税）
                            .setUpdateDate(new Date())
                            .setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    finBookPaymentEstimationItem = setClearStatus(finBookPaymentEstimationItem);
                    finBookPaymentEstimationItemMapper.updateAllById(finBookPaymentEstimationItem); //修改
                }
                //得到第一笔有合同数据的应付暂估的合同
                if (contractSid == null && item.getPurchaseContractSid() != null){
                    contractSid = item.getPurchaseContractSid();
                    contractCode = item.getPurchaseContractCode();
                }
                //得到第一笔有订单数据的应付暂估的订单
                if (orderSid == null && item.getPurchaseOrderSid() != null){
                    orderSid = item.getPurchaseOrderSid();
                    orderCode = item.getPurchaseOrderCode();
                }
                //如果本次循环不是最后一个实例，没有获取到对应的结果就跳出本次循环继续下一循环，
                //如果本次循环是最后一个实例了，则提示不存在相应结果的信息
                if (item.getAccountsMethodGroup() == null){
                    if (i != size){
                        i++;
                        continue;
                    }
                    throw new CustomException("确认失败,获取不到明细中收付款结算方式！");
                }
                //获取到第一个存在的付款方式组合
                if (flag == 1){
                    conAccountMethodGroup = conAccountMethodGroupMapper.selectById(item.getAccountsMethodGroup());
                    flag = -1;
                }
            }
            BigDecimal sum = finPurchaseInvoice.getTotalCurrencyAmountTax();
            advance = sum.multiply(new BigDecimal(conAccountMethodGroup.getAdvanceRate()));
            middle = sum.multiply(new BigDecimal(conAccountMethodGroup.getMiddleRate()));
            remain = sum.multiply(new BigDecimal(conAccountMethodGroup.getRemainRate()));
            if (finPurchaseInvoice.getInvoiceType().equals(ConstantsFinance.INVOICE_TYPE_BLUE) && !HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                if (sum.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BaseException("确认失败,蓝票发票金额不能小于0！");
                }
            }
            FinBookAccountPayable payable = new FinBookAccountPayable();
            BeanCopyUtils.copyProperties(finPurchaseInvoice, payable);
            Calendar cal = Calendar.getInstance();
            payable.setDocumentDate(new Date())
                    .setPaymentYear(Long.parseLong(String.valueOf(cal.get(Calendar.YEAR))))
                    .setPaymentMonth(cal.get(Calendar.MONTH) + 1);
            payable.setBookType(ConstantsFinance.BOOK_TYPE_YINGF);
            int num = 1;
            PurPurchaseContract contract = new PurPurchaseContract();
            if (contractSid != null) {
                contract = purPurchaseContractMapper.selectById(contractSid);
            }
            if (advance.compareTo(BigDecimal.ZERO) != 0) {
                payable.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_PFPYF);
                payable.setBookAccountPayableSid(IdWorker.getId());
                payable.setReferAccountPayableSid(referAdvance);
                finBookAccountPayableMapper.insert(payable);
                //明细表流水账
                FinBookAccountPayableItem bookItemAdvance = new FinBookAccountPayableItem();
                BeanCopyUtils.copyProperties(finPurchaseInvoice, bookItemAdvance);
                bookItemAdvance.setReferDocSid(finPurchaseInvoice.getPurchaseInvoiceSid())
                        .setReferDocCode(finPurchaseInvoice.getPurchaseInvoiceCode())
                        .setBookAccountPayableSid(payable.getBookAccountPayableSid())
                        .setCurrencyAmountTaxYingf(advance)
                        .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setTaxRate(finPurchaseInvoice.getTaxRate())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                        .setPurchaseContractSid(contractSid).setPurchaseContractCode(contractCode).setPurchaseOrderSid(orderSid).setPurchaseOrderCode(orderCode);
                bookItemAdvance.setAccountValidDays(new Long(0));
                if (contract != null) {
                    if (contract.getYfAccountValidDays() != null) {
                        bookItemAdvance.setAccountValidDays(new Long(contract.getYfAccountValidDays()));
                    }
                }
                bookItemAdvance.setDayType(conAccountMethodGroup.getDayType()).setItemNum((long)num)
                        .setAccountValidDate(new Date());
                bookItemAdvance.setIsFinanceVerify(ConstantsEms.NO);
                finBookAccountPayableItemMapper.insert(bookItemAdvance);
            }
            if (middle.compareTo(BigDecimal.ZERO) != 0) {
                payable.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_PFPZQ);
                payable.setBookAccountPayableSid(IdWorker.getId());
                payable.setReferAccountPayableSid(referMiddle);
                finBookAccountPayableMapper.insert(payable);
                //明细表流水账
                FinBookAccountPayableItem bookItemMiddle = new FinBookAccountPayableItem();
                BeanCopyUtils.copyProperties(finPurchaseInvoice, bookItemMiddle);
                bookItemMiddle.setReferDocSid(finPurchaseInvoice.getPurchaseInvoiceSid())
                        .setReferDocCode(finPurchaseInvoice.getPurchaseInvoiceCode())
                        .setBookAccountPayableSid(payable.getBookAccountPayableSid())
                        .setCurrencyAmountTaxYingf(middle)
                        .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setTaxRate(finPurchaseInvoice.getTaxRate())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                        .setPurchaseContractSid(contractSid).setPurchaseContractCode(contractCode).setPurchaseOrderSid(orderSid).setPurchaseOrderCode(orderCode);
                if (contract != null){
                    if (contract.getZqAccountValidDays() != null){
                        Date dateValid = new Date();
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(dateValid);
                        calendar.add(calendar.DATE,contract.getZqAccountValidDays()); //把日期往后增加i天,整数  往后推,负数往前移动
                        dateValid = calendar.getTime(); //这个时间就是日期往后推i天的结果
                        bookItemMiddle.setAccountValidDays(new Long(contract.getZqAccountValidDays()));
                        bookItemMiddle.setAccountValidDate(dateValid);
                    }
                }
                if (bookItemMiddle.getAccountValidDate() == null){
                    bookItemMiddle.setAccountValidDate(new Date());
                    bookItemMiddle.setAccountValidDays(new Long(0));
                }
                bookItemMiddle.setDayType(conAccountMethodGroup.getDayType()).setItemNum((long)num);
                bookItemMiddle.setIsFinanceVerify(ConstantsEms.NO);
                finBookAccountPayableItemMapper.insert(bookItemMiddle);
            }
            if (remain.compareTo(BigDecimal.ZERO) != 0) {
                payable.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_PFPWQ);
                payable.setBookAccountPayableSid(IdWorker.getId());
                payable.setReferAccountPayableSid(referRemain);
                finBookAccountPayableMapper.insert(payable);
                //明细表流水账
                FinBookAccountPayableItem bookItemRemain = new FinBookAccountPayableItem();
                BeanCopyUtils.copyProperties(finPurchaseInvoice, bookItemRemain);
                bookItemRemain.setReferDocSid(finPurchaseInvoice.getPurchaseInvoiceSid())
                        .setReferDocCode(finPurchaseInvoice.getPurchaseInvoiceCode())
                        .setBookAccountPayableSid(payable.getBookAccountPayableSid())
                        .setCurrencyAmountTaxYingf(remain)
                        .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setTaxRate(finPurchaseInvoice.getTaxRate())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                        .setPurchaseContractSid(contractSid).setPurchaseContractCode(contractCode).setPurchaseOrderSid(orderSid).setPurchaseOrderCode(orderCode);
                if (contract != null){
                    if (contract.getWqAccountValidDays() != null){
                        Date dateValid = new Date();
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(dateValid);
                        calendar.add(calendar.DATE,contract.getWqAccountValidDays()); //把日期往后增加i天,整数  往后推,负数往前移动
                        dateValid = calendar.getTime(); //这个时间就是日期往后推i天的结果
                        bookItemRemain.setAccountValidDays(new Long(contract.getWqAccountValidDays()));
                        bookItemRemain.setAccountValidDate(dateValid);
                    }
                }
                if (bookItemRemain.getAccountValidDate() == null){
                    bookItemRemain.setAccountValidDate(new Date());
                    bookItemRemain.setAccountValidDays(new Long(0));
                }
                bookItemRemain.setDayType(conAccountMethodGroup.getDayType()).setItemNum((long)num);
                bookItemRemain.setIsFinanceVerify(ConstantsEms.NO);
                finBookAccountPayableItemMapper.insert(bookItemRemain);
            }
        }
    }

    /**
     * 新增折扣明细时查询流水账
     *
     * @param request 请求实体
     * @return
     */
    @Override
    public List<FinPurchaseInvoiceDiscount> bookList(FinPurchaseInvoiceDiscount request) {
        String bookType = request.getBookType();
        //转为折扣返回实体列表
        List<FinPurchaseInvoiceDiscount> responses = new ArrayList<>();
        //供应商扣款流水
        if (ConstantsFinance.BOOK_TYPE_VKK.equals(bookType)) {
            FinBookVendorDeduction book = new FinBookVendorDeduction();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            //查找供应商扣款报表
            List<FinBookVendorDeduction> bookList = finBookVendorDeductionMapper.getReportForm(book);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinPurchaseInvoiceDiscount response = new FinPurchaseInvoiceDiscount();
                BeanUtil.copyProperties(bookItem, response);
                //处理字段不一致的参数
                response.setAccountDocumentSid(bookItem.getBookDeductionSid()).setAccountDocumentCode(bookItem.getBookDeductionCode())
                        .setAccountItemSid(bookItem.getBookDeductionItemSid());
                //避免空指针
                if (bookItem.getCurrencyAmountTaxKk() == null) {
                    bookItem.setCurrencyAmountTaxKk(BigDecimal.ZERO);
                }
                response.setCurrencyAmountTaxYingD(bookItem.getCurrencyAmountTaxKk())
                        .setCurrencyAmountTaxDaiD(bookItem.getCurrencyAmountTaxDhx());
                responses.add(response);
            });
        }
        //供应商调账流水
        if (ConstantsFinance.BOOK_TYPE_VTZ.equals(bookType)) {
            FinBookVendorAccountAdjust book = new FinBookVendorAccountAdjust();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            //查找供应商调账报表
            List<FinBookVendorAccountAdjust> bookList = finBookVendorAccountAdjustMapper.getReportForm(book);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinPurchaseInvoiceDiscount response = new FinPurchaseInvoiceDiscount();
                BeanUtil.copyProperties(bookItem, response);
                //处理字段不一致的参数
                response.setAccountDocumentSid(bookItem.getBookAccountAdjustSid()).setAccountDocumentCode(bookItem.getBookAccountAdjustCode())
                        .setAccountItemSid(bookItem.getBookAccountAdjustItemSid());
                //避免空指针
                if (bookItem.getCurrencyAmountTaxTz() == null) {
                    bookItem.setCurrencyAmountTaxTz(BigDecimal.ZERO);
                }
                response.setCurrencyAmountTaxYingD(bookItem.getCurrencyAmountTaxTz())
                        .setCurrencyAmountTaxDaiD(bookItem.getCurrencyAmountTaxDhx());
                responses.add(response);
            });
        }
        responses.stream().filter(o -> o.getHandleStatus() != HandleStatus.INVALID.getCode()).collect(Collectors.toList());
        return responses;
    }

    /**
     * 根据折扣修改流水账核销中金额
     *
     * @param finPurchaseInvoice
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(FinPurchaseInvoice finPurchaseInvoice) {
        if (finPurchaseInvoice.getFinPurchaseInvoiceDiscountList() != null
                && finPurchaseInvoice.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            List<FinPurchaseInvoiceDiscount> discountList = finPurchaseInvoice.getFinPurchaseInvoiceDiscountList();
            discountList.forEach(discount -> {
                if (ConstantsFinance.BOOK_TYPE_VKK.equals(discount.getBookType())) {
                    FinBookVendorDeductionItem book = bookVendorDeductionItemMapper.selectFinBookVendorDeductionItemById(discount.getAccountItemSid());
                    book.setCurrencyAmountTaxYhx(book.getCurrencyAmountTaxYhx().add(discount.getCurrencyAmountTax()))
                            .setCurrencyAmountTaxHxz(book.getCurrencyAmountTaxHxz().subtract(discount.getCurrencyAmountTax()))
                            .setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
//                    if (book.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0) {
//                        book.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    if (book.getCurrencyAmountTaxYhx().compareTo(book.getCurrencyAmountTaxKk()) == 0) {
                        book.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
                    book.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    int row = bookVendorDeductionItemMapper.updateById(book);
                    if (row == 0) {
                        throw new CustomException(discount.getAccountDocumentCode() + "确认失败,请联系管理员");
                    }
                }
                if (ConstantsFinance.BOOK_TYPE_VTZ.equals(discount.getBookType())) {
                    FinBookVendorAccountAdjustItem book = bookVendorAccountAdjustItemMapper.selectFinBookVendorAccountAdjustItemById(discount.getAccountItemSid());
                    book.setCurrencyAmountTaxYhx(book.getCurrencyAmountTaxYhx().add(discount.getCurrencyAmountTax()))
                            .setCurrencyAmountTaxHxz(book.getCurrencyAmountTaxHxz().subtract(discount.getCurrencyAmountTax()))
                            .setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
//                    if (book.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0) {
//                        book.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    if (book.getCurrencyAmountTaxYhx().compareTo(book.getCurrencyAmountTaxTz()) == 0) {
                        book.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
                    book.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    int row = bookVendorAccountAdjustItemMapper.updateById(book);
                    if (row == 0) {
                        throw new CustomException(discount.getAccountDocumentCode() + "确认失败,请联系管理员");
                    }
                }
            });
        }
    }

    /**
     * 设置应付暂估的核销状态
     *
     * @param finPurchaseInvoice
     */
    private FinBookPaymentEstimationItem setClearStatus(FinBookPaymentEstimationItem finBookPaymentEstimationItem){
        //金额：部分核销
        if (finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) != 0 || finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) != 0){
            finBookPaymentEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        //金额：如果已核销等于 应付金额 就修改状态为全部
        if (finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().compareTo(finBookPaymentEstimationItem.getCurrencyAmountTax()) == 0){
            finBookPaymentEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        }
        //如果核销中和已核销加起来也等于 0 就修改状态为待核销
        if ((finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().add(finBookPaymentEstimationItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
            finBookPaymentEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //数量：部分核销
        if (finBookPaymentEstimationItem.getQuantityYhx().compareTo(BigDecimal.ZERO) !=  0 || finBookPaymentEstimationItem.getQuantityHxz().compareTo(BigDecimal.ZERO) !=  0){
            finBookPaymentEstimationItem.setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        //数量：如果已核销等于 应开票量 就修改状态为全部
        if (finBookPaymentEstimationItem.getQuantityYhx().compareTo(finBookPaymentEstimationItem.getQuantity()) == 0){
            finBookPaymentEstimationItem.setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_QHX);
        }
        //如果核销中和已核销加起来也等于 0 就修改状态为待核销
        if ((finBookPaymentEstimationItem.getQuantityHxz().add(finBookPaymentEstimationItem.getQuantityYhx())).compareTo(BigDecimal.ZERO) == 0) {
            finBookPaymentEstimationItem.setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //核销状态
        if (finBookPaymentEstimationItem.getClearStatusQuantity().equals(finBookPaymentEstimationItem.getClearStatusMoney())){
            finBookPaymentEstimationItem.setClearStatus(finBookPaymentEstimationItem.getClearStatusMoney());
        }
        if (finBookPaymentEstimationItem.getClearStatusQuantity().equals(ConstantsFinance.CLEAR_STATUS_BFHX)
                || finBookPaymentEstimationItem.getClearStatusMoney().equals(ConstantsFinance.CLEAR_STATUS_BFHX)){
            finBookPaymentEstimationItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        return finBookPaymentEstimationItem;
    }

    /*回退来源流失的数据*/
    @Transactional(rollbackFor = Exception.class)
    public void returnBack(Long finPurchaseInvoiceSid, String handleStatus){
        List<FinPurchaseInvoiceItem> finPurchaseInvoiceItemList = finPurchaseInvoiceItemMapper.selectList(
                new QueryWrapper<FinPurchaseInvoiceItem>().lambda().eq(FinPurchaseInvoiceItem::getPurchaseInvoiceSid, finPurchaseInvoiceSid));
        finPurchaseInvoiceItemList.forEach(item -> {
            //修改应付暂估
            //应付暂估
            FinBookPaymentEstimationItem finBookPaymentEstimationItem = finBookPaymentEstimationItemMapper.selectById(item.getAccountItemSid());
            finBookPaymentEstimationItem.setBookPaymentEstimationItemSid(item.getAccountItemSid())
                    .setUpdateDate(new Date())
                    .setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            //如果是确认状态就撤回已核销金额，已核销数量
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                finBookPaymentEstimationItem.setQuantityYhx(finBookPaymentEstimationItem.getQuantityYhx().subtract(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))
                        .setCurrencyAmountTaxYhx(finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()));
            } else { //如果不是确认状态就撤回核销中金额，核销中数量
                finBookPaymentEstimationItem.setQuantityHxz(finBookPaymentEstimationItem.getQuantityHxz().subtract(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销中数量
                        .setCurrencyAmountTaxHxz(finBookPaymentEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()));  //核销中金额（含税）
            }
            //设置应付暂估的核销状态
            finBookPaymentEstimationItem = setClearStatus(finBookPaymentEstimationItem);
            finBookPaymentEstimationItemMapper.updateAllById(finBookPaymentEstimationItem); //修改
        });
        //恢复折扣表对应的流水的核销中数据
        List<FinPurchaseInvoiceDiscount> finPurchaseInvoiceDiscountsList = finPurchaseInvoiceDiscountMapper.selectList(
                new QueryWrapper<FinPurchaseInvoiceDiscount>().lambda().eq(FinPurchaseInvoiceDiscount::getPurchaseInvoiceSid, finPurchaseInvoiceSid));
        finPurchaseInvoiceDiscountsList.forEach(item -> {
            if (ConstantsFinance.BOOK_TYPE_VKK.equals(item.getBookType())) {
                FinBookVendorDeductionItem finBookVendorDeductionItem = bookVendorDeductionItemMapper.selectById(item.getAccountItemSid());
                //确认状态下修改撤回 已核销金额
                if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                    finBookVendorDeductionItem.setCurrencyAmountTaxYhx(finBookVendorDeductionItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                } else {   //非确认状态下撤回 核销中金额
                    finBookVendorDeductionItem.setCurrencyAmountTaxHxz(finBookVendorDeductionItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                }
                //核销状态
                finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                if (finBookVendorDeductionItem.getCurrencyAmountTaxYhx().compareTo(finBookVendorDeductionItem.getCurrencyAmountTaxKk()) == 0) {
                    finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                if ((finBookVendorDeductionItem.getCurrencyAmountTaxHxz().add(finBookVendorDeductionItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                    finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                }
                finBookVendorDeductionItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                bookVendorDeductionItemMapper.updateAllById(finBookVendorDeductionItem);
            }
            if (ConstantsFinance.BOOK_TYPE_VTZ.equals(item.getBookType())) {
                FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = bookVendorAccountAdjustItemMapper.selectById(item.getAccountItemSid());
                //确认状态下修改撤回 已核销金额
                if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                    finBookVendorAccountAdjustItem.setCurrencyAmountTaxYhx(finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                } else {   //非确认状态下撤回 核销中金额
                    finBookVendorAccountAdjustItem.setCurrencyAmountTaxHxz(finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                }
                //核销状态
                finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                if (finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx().compareTo(finBookVendorAccountAdjustItem.getCurrencyAmountTaxTz()) == 0) {
                    finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                if ((finBookVendorAccountAdjustItem.getCurrencyAmountTaxHxz().add(finBookVendorAccountAdjustItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                    finBookVendorAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                }
                finBookVendorAccountAdjustItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                bookVendorAccountAdjustItemMapper.updateAllById(finBookVendorAccountAdjustItem);
            }
        });
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalidInvoice(Long purchaseInvoiceSid){
        FinPurchaseInvoice invoice = finPurchaseInvoiceMapper.selectById(purchaseInvoiceSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(invoice.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookAccountPayable> bookList = finBookAccountPayableMapper.getReportForm(new FinBookAccountPayable().setPurchaseInvoiceCode(invoice.getPurchaseInvoiceCode()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            finBookAccountPayableMapper.updateById(item);
        });
        int i = finPurchaseInvoiceMapper.updateAllById(invoice.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0){
            returnBack(purchaseInvoiceSid,ConstantsEms.CHECK_STATUS);
            //删除待办：未签收
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, purchaseInvoiceSid));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(invoice.getPurchaseInvoiceSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }

    /**
     * 红冲
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int redDashed(Long purchaseInvoiceSid){
        FinPurchaseInvoice invoice = finPurchaseInvoiceMapper.selectById(purchaseInvoiceSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(invoice.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        if (!ConstantsFinance.INVOICE_CATE_BZ.equals(invoice.getInvoiceCategory())){
            throw new BaseException("请选择发票类别是“标准发票”的发票!");
        }
        //得到原发票
        FinPurchaseInvoice origin = this.selectFinPurchaseInvoiceById(purchaseInvoiceSid);
        FinPurchaseInvoice newInvoice = new FinPurchaseInvoice();
        BeanCopyUtils.copyProperties(origin,newInvoice);
        //设置新主表反向金额
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        newInvoice.setReferenceInvoice(origin.getPurchaseInvoiceSid())
                .setPurchaseInvoiceSid(IdWorker.getId())
                .setPurchaseInvoiceCode(null)
                .setDocumentDate(new Date()).setMonthAccountPeriod(sdf.format(date))
                .setInvoiceCategory(ConstantsFinance.INVOICE_CATE_HX)
                .setTotalCurrencyAmount(origin.getTotalCurrencyAmount().multiply(new BigDecimal(-1)))
                .setTotalCurrencyAmountTax(origin.getTotalCurrencyAmountTax().multiply(new BigDecimal(-1)));
        newInvoice.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                .setUpdaterAccount(null).setUpdateDate(null)
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        //设置新明细列表的反向金额
        List<FinPurchaseInvoiceItem> newItemList = BeanCopyUtils.copyListProperties(
                origin.getFinPurchaseInvoiceItemList(), FinPurchaseInvoiceItem::new);
        newItemList.forEach(item->{
            item.setPurchaseInvoiceItemSid(IdWorker.getId());
            item.setCurrencyAmountTax(item.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
            item.setCurrencyAmount(item.getCurrencyAmount().multiply(new BigDecimal(-1)));
            item.setQuantity(item.getQuantity().multiply(new BigDecimal(-1)));
            item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
        });
        //设置新折扣列表的反向金额
        List<FinPurchaseInvoiceDiscount> newDiscountList = BeanCopyUtils.copyListProperties(
                origin.getFinPurchaseInvoiceDiscountList(), FinPurchaseInvoiceDiscount::new);
        newDiscountList.forEach(item->{
            item.setPurchaseInvoiceDiscountSid(IdWorker.getId());
            item.setCurrencyAmountTax(item.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
            item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
            //       item.setCurrencyAmount(item.getCurrencyAmount().multiply(new BigDecimal(-1)));
        });
        //写入新明细
        newInvoice.setFinPurchaseInvoiceItemList(newItemList)
                .setFinPurchaseInvoiceDiscountList(newDiscountList)
                .setAttachmentList(null);
        //释放原发票中引用的所有流水数据
        returnBack(purchaseInvoiceSid,origin.getHandleStatus());
        finPurchaseInvoiceMapper.update(null,new UpdateWrapper<FinPurchaseInvoice>().lambda().eq(FinPurchaseInvoice::getPurchaseInvoiceSid,purchaseInvoiceSid)
                .set(FinPurchaseInvoice::getHandleStatus,HandleStatus.REDDASHED.getCode()));
        //新建红冲发票
        int row = finPurchaseInvoiceMapper.insert(newInvoice);
        //操作日志
        MongodbDeal.insert(newInvoice.getPurchaseInvoiceSid(),newInvoice.getHandleStatus(),null,TITLE,null);
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(purchaseInvoiceSid, BusinessType.REDDASHED.getValue(), msgList, TITLE);
        //红冲明细
        insertChild(newInvoice.getFinPurchaseInvoiceItemList(),
                newInvoice.getFinPurchaseInvoiceDiscountList(),
                newInvoice.getAttachmentList(),
                newInvoice.getPurchaseInvoiceSid(), HandleStatus.REDDASHED.getCode());
        //获取原发票的应付流水,然后生成负向对应的流水
        List<FinBookAccountPayable> payableList = new ArrayList<>();
        List<FinBookAccountPayableItem> payableItemList = new ArrayList<>();
        payableItemList = finBookAccountPayableItemMapper.selectList(new QueryWrapper<FinBookAccountPayableItem>()
                .lambda().eq(FinBookAccountPayableItem::getReferDocSid, purchaseInvoiceSid));
        for (FinBookAccountPayableItem item : payableItemList) {
            //获取对应主表
            FinBookAccountPayable payable = finBookAccountPayableMapper.selectOne(new QueryWrapper<FinBookAccountPayable>()
                    .lambda().eq(FinBookAccountPayable::getBookAccountPayableSid,item.getBookAccountPayableSid()));
            payable.setReferAccountPayableSid(payable.getBookAccountPayableSid()).setBookAccountPayableSid(null).setBookAccountPayableCode(null);
            payable.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
            payable.setMonthAccountPeriod(newInvoice.getMonthAccountPeriod()).setDocumentDate(newInvoice.getDocumentDate());
            //插入红冲单据的流水主表
            finBookAccountPayableMapper.insert(payable);
            //插入红冲单据的流水明细表
            item.setBookAccountPayableItemSid(null).setBookAccountPayableSid(payable.getBookAccountPayableSid());
            item.setCurrencyAmountTaxYingf(item.getCurrencyAmountTaxYingf().multiply(new BigDecimal(-1)))
                    .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx().multiply(new BigDecimal(-1)))
                    .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz().multiply(new BigDecimal(-1)));
            item.setReferDocSid(newInvoice.getPurchaseInvoiceSid())
                    .setReferDocCode(newInvoice.getPurchaseInvoiceCode());
            item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
            item.setIsFinanceVerify(ConstantsEms.NO);
            finBookAccountPayableItemMapper.insert(item);
        }
        //删除待办：未签收
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, purchaseInvoiceSid));
        return row;
    }


    /**
     * 纸质发票签收
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSignFlag(List<Long> purchaseInvoiceSids){
        List<FinPurchaseInvoice> billList = finPurchaseInvoiceMapper.selectList(new QueryWrapper<FinPurchaseInvoice>().lambda()
                .in(FinPurchaseInvoice::getPurchaseInvoiceSid,purchaseInvoiceSids)
                .ne(FinPurchaseInvoice::getHandleStatus,ConstantsEms.CHECK_STATUS));
        if (CollectionUtils.isNotEmpty(billList)){
            throw new BaseException("请选择确认状态的发票单据！");
        }
        //
        LambdaUpdateWrapper<FinPurchaseInvoice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinPurchaseInvoice::getPurchaseInvoiceSid,purchaseInvoiceSids).set(FinPurchaseInvoice::getSignFlag, ConstantsFinance.SIGN_FLAG_YQS);
        int row = finPurchaseInvoiceMapper.update(null, updateWrapper);
        if (row > 0){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, purchaseInvoiceSids));
        }
        return row;
    }
}
