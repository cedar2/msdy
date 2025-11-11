package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConAccountMethodGroup;
import com.platform.ems.plug.mapper.ConAccountMethodGroupMapper;
import com.platform.ems.service.IFinSaleInvoiceService;
import com.platform.ems.service.ISalSalePriceService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 销售发票Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinSaleInvoiceServiceImpl extends ServiceImpl<FinSaleInvoiceMapper, FinSaleInvoice> implements IFinSaleInvoiceService {

    @Autowired
    private FinSaleInvoiceMapper finSaleInvoiceMapper;
    @Autowired
    private FinSaleInvoiceItemMapper finSaleInvoiceItemMapper;
    @Autowired
    private FinSaleInvoiceAttachmentMapper finSaleInvoiceAttachmentMapper;
    @Autowired
    private FinSaleInvoiceDiscountMapper finSaleInvoiceDiscountMapper;
    @Autowired
    private FinBookCustomerDeductionMapper finBookCustomerDeductionMapper;
    @Autowired
    private FinBookCustomerAccountAdjustMapper finBookCustomerAccountAdjustMapper;
    @Autowired
    private FinBookAccountReceivableMapper finBookAccountReceivableMapper;
    @Autowired
    private FinBookAccountReceivableItemMapper finBookAccountReceivableItemMapper;
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private FinBookCustomerDeductionItemMapper bookCustomerDeductionItemMapper;
    @Autowired
    private FinBookCustomerAccountAdjustItemMapper bookCustomerAccountAdjustItemMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;
    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;

    private static final String TITLE = "销售发票";

    /**
     * 查询销售发票
     *
     * @param SaleInvoiceSid 销售发票ID
     * @return 销售发票
     */
    @Override
    public FinSaleInvoice selectFinSaleInvoiceById(Long SaleInvoiceSid) {
        FinSaleInvoice finSaleInvoice = finSaleInvoiceMapper.selectFinSaleInvoiceById(SaleInvoiceSid);
        if (finSaleInvoice == null) {
            return null;
        }
        //销售发票-明细对象
        FinSaleInvoiceItem finSaleInvoiceItem = new FinSaleInvoiceItem();
        finSaleInvoiceItem.setSaleInvoiceSid(SaleInvoiceSid);
        List<FinSaleInvoiceItem> finSaleInvoiceItemList =
                finSaleInvoiceItemMapper.selectFinSaleInvoiceItemList(finSaleInvoiceItem);
        for (FinSaleInvoiceItem item : finSaleInvoiceItemList){
            if (item.getSalesOrderSid() != null){
                SalSalePrice price = new SalSalePrice();
                price.setCustomerSid(item.getCustomerSid()).setCompanySid(item.getCompanySid()).setMaterialSid(item.getMaterialSid())
                        .setSku1Sid(item.getSku1Sid()).setRawMaterialMode(item.getRawMaterialMode()).setSaleMode(item.getSaleMode());
                SalSalePriceItem priceItem = salSalePriceService.getSalePrice(price);
                if (priceItem != null){
                    if (priceItem.getSalePriceTax() == null){
                        priceItem.setSalePriceTax(BigDecimal.ZERO);
                    }
                    if (priceItem.getTaxRate() == null){
                        priceItem.setTaxRate(BigDecimal.ZERO);
                    }
                    item.setCurrentPrice(priceItem.getSalePriceTax().divide(priceItem.getTaxRate().add(BigDecimal.ONE),4, BigDecimal.ROUND_HALF_UP)).setCurrentPriceTax(priceItem.getSalePriceTax());
                }
            }
            item.setCurrencyAmountTaxLeft(item.getCurrencyAmountTaxLeft().add(item.getCurrencyAmountTax()))
                    .setQuantityLeft(item.getQuantityLeft().add(item.getQuantity()));
        }
        //销售发票-附件对象
        FinSaleInvoiceAttachment finSaleInvoiceAttachment = new FinSaleInvoiceAttachment();
        finSaleInvoiceAttachment.setSaleInvoiceSid(SaleInvoiceSid);
        List<FinSaleInvoiceAttachment> finSaleInvoiceAttachmentList =
                finSaleInvoiceAttachmentMapper.selectFinSaleInvoiceAttachmentList(finSaleInvoiceAttachment);
        //销售发票-折扣对象
        FinSaleInvoiceDiscount finSaleInvoiceDiscount = new FinSaleInvoiceDiscount();
        finSaleInvoiceDiscount.setSaleInvoiceSid(SaleInvoiceSid);
        List<FinSaleInvoiceDiscount> finSaleInvoiceDiscountList =
                finSaleInvoiceDiscountMapper.selectFinSaleInvoiceDiscountList(finSaleInvoiceDiscount);
        if (finSaleInvoiceDiscountList != null) {
            finSaleInvoiceDiscountList.forEach(discount -> {
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(discount.getBookType())) {
                    FinBookCustomerDeduction finBookCustomerDeduction = finBookCustomerDeductionMapper.selectFinBookCustomerDeductionById(discount.getAccountDocumentSid());
                    discount.setCustomerName(finBookCustomerDeduction.getCustomerName())
                            .setProductSeasonName(finBookCustomerDeduction.getProductSeasonName());
                    discount.setCurrencyAmountTaxHxz(finBookCustomerDeduction.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxYhx(finBookCustomerDeduction.getCurrencyAmountTaxYhx());
                    discount.setCurrencyAmountTaxYingD(finBookCustomerDeduction.getCurrencyAmountTaxKk())
                            .setCurrencyAmountTaxDaiD(finBookCustomerDeduction.getCurrencyAmountTaxDhx().add(discount.getCurrencyAmountTax()));
                }
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(discount.getBookType())) {
                    FinBookCustomerAccountAdjust finBookCustomer = finBookCustomerAccountAdjustMapper.selectFinBookCustomerAccountAdjustById(discount.getAccountDocumentSid());
                    discount.setCustomerName(finBookCustomer.getCustomerName())
                            .setProductSeasonName(finBookCustomer.getProductSeasonName());
                    discount.setCurrencyAmountTaxHxz(finBookCustomer.getCurrencyAmountTaxHxz())
                            .setCurrencyAmountTaxYhx(finBookCustomer.getCurrencyAmountTaxYhx());
                    discount.setCurrencyAmountTaxYingD(finBookCustomer.getCurrencyAmountTaxTz())
                            .setCurrencyAmountTaxDaiD(finBookCustomer.getCurrencyAmountTaxDhx().add(discount.getCurrencyAmountTax()));
                }
            });
        }
        finSaleInvoice.setFinSaleInvoiceItemList(finSaleInvoiceItemList);
        finSaleInvoice.setAttachmentList(finSaleInvoiceAttachmentList);
        finSaleInvoice.setFinSaleInvoiceDiscountList(finSaleInvoiceDiscountList);
        //查询日志信息
        MongodbUtil.find(finSaleInvoice);
        return finSaleInvoice;
    }

    /**
     * 查询销售发票列表
     *
     * @param finSaleInvoice 销售发票
     * @return 销售发票
     */
    @Override
    public List<FinSaleInvoice> selectFinSaleInvoiceList(FinSaleInvoice finSaleInvoice) {
        List<FinSaleInvoice> response = finSaleInvoiceMapper.selectFinSaleInvoiceList(finSaleInvoice);
        return response;
    }

    /**
     * 新增销售发票
     * 需要注意编码重复校验
     *
     * @param finSaleInvoice 销售发票
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinSaleInvoice(FinSaleInvoice finSaleInvoice) {
        //设置确认信息
        setConfirmInfo(finSaleInvoice);
        //校验金额
        judgePrice(finSaleInvoice);
        finSaleInvoice.setIsFinanceVerify(ConstantsEms.NO);
        int row = finSaleInvoiceMapper.insert(finSaleInvoice);
        FinSaleInvoice invoice = finSaleInvoiceMapper.selectById(finSaleInvoice.getSaleInvoiceSid());
        finSaleInvoice.setSaleInvoiceCode(invoice.getSaleInvoiceCode());
        if (row > 0) {
            insertChild(finSaleInvoice.getFinSaleInvoiceItemList(),
                    finSaleInvoice.getFinSaleInvoiceDiscountList(),
                    finSaleInvoice.getAttachmentList(),
                    finSaleInvoice.getSaleInvoiceSid(), finSaleInvoice.getHandleStatus());
            insertBook(finSaleInvoice,finSaleInvoice.getHandleStatus());
            updateBook(finSaleInvoice);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finSaleInvoice.getHandleStatus())) {
                finSaleInvoice = finSaleInvoiceMapper.selectById(finSaleInvoice.getSaleInvoiceSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_sale_invoice")
                        .setDocumentSid(finSaleInvoice.getSaleInvoiceSid());
                sysTodoTask.setTitle("销售发票: " + finSaleInvoice.getSaleInvoiceCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finSaleInvoice.getSaleInvoiceCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            if (ConstantsEms.CHECK_STATUS.equals(finSaleInvoice.getHandleStatus())) {
                finSaleInvoice = finSaleInvoiceMapper.selectById(finSaleInvoice.getSaleInvoiceSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_sale_invoice")
                        .setDocumentSid(finSaleInvoice.getSaleInvoiceSid());
                sysTodoTask.setTitle("销售发票: " + finSaleInvoice.getSaleInvoiceCode() + " 的发票签收状态为“未签收”，请及时处理！")
                        .setDocumentCode(String.valueOf(finSaleInvoice.getSaleInvoiceCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //操作日志
            MongodbDeal.insert(finSaleInvoice.getSaleInvoiceSid(), finSaleInvoice.getHandleStatus(), null, TITLE,null);
        }
        return row;
    }

    /**
     * 修改销售发票
     *
     * @param finSaleInvoice 销售发票
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinSaleInvoice(FinSaleInvoice finSaleInvoice) {
        FinSaleInvoice invoice = finSaleInvoiceMapper.selectFinSaleInvoiceById(finSaleInvoice.getSaleInvoiceSid());
        String handleStatus = invoice.getHandleStatus();
        //设置确认信息
        setConfirmInfo(finSaleInvoice);
        int row = finSaleInvoiceMapper.updateAllById(finSaleInvoice);
        if (row > 0) {
            deleteItem(finSaleInvoice.getSaleInvoiceSid(), handleStatus);
            judgePrice(finSaleInvoice);
            insertChild(finSaleInvoice.getFinSaleInvoiceItemList(), finSaleInvoice.getFinSaleInvoiceDiscountList(),
                    finSaleInvoice.getAttachmentList(), finSaleInvoice.getSaleInvoiceSid(), finSaleInvoice.getHandleStatus());
            insertBook(finSaleInvoice,finSaleInvoice.getHandleStatus());
            updateBook(finSaleInvoice);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finSaleInvoice.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finSaleInvoice.getSaleInvoiceSid()));
            }
            if (ConstantsEms.CHECK_STATUS.equals(finSaleInvoice.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_fin_sale_invoice")
                        .setDocumentSid(finSaleInvoice.getSaleInvoiceSid());
                sysTodoTask.setTitle("销售发票: " + finSaleInvoice.getSaleInvoiceCode() + " 的发票签收状态为“未签收”，请及时处理！")
                        .setDocumentCode(String.valueOf(finSaleInvoice.getSaleInvoiceCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(invoice, finSaleInvoice);
            MongodbDeal.update(finSaleInvoice.getSaleInvoiceSid(), invoice.getHandleStatus(),finSaleInvoice.getHandleStatus(),msgList,TITLE,null);

        }
        return row;
    }

    /**
     * 销售发票变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(FinSaleInvoice finSaleInvoice) {
        Long SaleInvoiceSid = finSaleInvoice.getSaleInvoiceSid();
        FinSaleInvoice invoice = finSaleInvoiceMapper.selectFinSaleInvoiceById(SaleInvoiceSid);
        //验证是否确认状态
        if (!(ConstantsEms.CHECK_STATUS).equals(invoice.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }
        //设置确认信息
        setConfirmInfo(finSaleInvoice);
        int row = finSaleInvoiceMapper.updateAllById(finSaleInvoice);
        if (row > 0) {
            deleteItem(finSaleInvoice.getSaleInvoiceSid(), invoice.getHandleStatus());
            judgePrice(finSaleInvoice);
            insertChild(finSaleInvoice.getFinSaleInvoiceItemList(), finSaleInvoice.getFinSaleInvoiceDiscountList(),
                    finSaleInvoice.getAttachmentList(), finSaleInvoice.getSaleInvoiceSid(), finSaleInvoice.getHandleStatus());
            insertBook(finSaleInvoice,finSaleInvoice.getHandleStatus());
            updateBook(finSaleInvoice);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(invoice, finSaleInvoice);
            MongodbUtil.insertUserLog(finSaleInvoice.getSaleInvoiceSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return 1;
    }

    /**
     * 销售发票批量确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(FinSaleInvoice finSaleInvoice) {
        String handleStatus = finSaleInvoice.getHandleStatus();
        //销售发票sids
        int row = 0;
        Long[] saleInvoiceSids = finSaleInvoice.getSaleInvoiceSids();
//        FinSaleInvoice params = new FinSaleInvoice();
        if (saleInvoiceSids != null && saleInvoiceSids.length > 0) {
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finSaleInvoice.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, saleInvoiceSids));
            }
            for (Long sid : saleInvoiceSids) {
                finSaleInvoice.setSaleInvoiceSid(sid);
                //获取主表明细表内容并生成流水
                finSaleInvoice = this.selectFinSaleInvoiceById(sid);
                if (finSaleInvoice.getHandleStatus().equals(handleStatus)){
                    throw new CustomException("请不要重复操作！");
                }
                finSaleInvoice.setHandleStatus(handleStatus);
                this.setConfirmInfo(finSaleInvoice);
                int i = finSaleInvoiceMapper.updateById(finSaleInvoice);
                row += i;
                if (i == 0) {
                    throw new CustomException(sid + "确认失败,请联系管理员");
                }
                //明细表
                insertBook(finSaleInvoice,finSaleInvoice.getHandleStatus());
                //折扣表
                updateBook(finSaleInvoice);
                //确认待办
                if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                    SysUser user = userService.selectSysUserByName(finSaleInvoice.getCreatorAccount());
                    //确认待办
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName("s_fin_sale_invoice")
                            .setNoticeDate(new Date())
                            .setUserId(user.getUserId());
                    sysTodoTask.setDocumentSid(finSaleInvoice.getSaleInvoiceSid());
                    sysTodoTask.setTitle("销售发票: " + finSaleInvoice.getSaleInvoiceCode() + " 的发票签收状态为“未签收”，请及时处理！")
                            .setDocumentCode(String.valueOf(finSaleInvoice.getSaleInvoiceCode()));
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
                //插入日志
//                List<OperMsg> msgList = new ArrayList<>();
//                MongodbDeal.check(finSaleInvoice.getSaleInvoiceSid(), finSaleInvoice.getHandleStatus(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 批量删除销售发票
     *
     * @param SaleInvoiceSids 需要删除的销售发票ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinSaleInvoiceByIds(List<Long> saleInvoiceSids) {
        if (saleInvoiceSids.size() == 0){
            throw new BaseException("请选择单据！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinSaleInvoice> invoiceList = finSaleInvoiceMapper.selectList(new QueryWrapper<FinSaleInvoice>().lambda()
                .in(FinSaleInvoice::getSaleInvoiceSid,saleInvoiceSids)
                .notIn(FinSaleInvoice::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(invoiceList)){
            throw new BaseException("仅保存状态或者已退回状态才可删除！");
        }
        saleInvoiceSids.forEach(sid -> {
            FinSaleInvoice invoice = finSaleInvoiceMapper.selectFinSaleInvoiceById(sid);
            deleteItem(sid, invoice.getHandleStatus());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, saleInvoiceSids));
        int row = finSaleInvoiceMapper.deleteBatchIds(saleInvoiceSids);
        return row;
    }

    /**
     * 设置确认信息
     */
    @Override
    public void setConfirmInfo(FinSaleInvoice entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || entity.getHandleStatus().equals(ConstantsEms.SUBMIT_STATUS)) {
            if (CollectionUtils.isEmpty(entity.getFinSaleInvoiceItemList())) {
                throw new CustomException("此操作明细不能为空");
            }
            if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                entity.setConfirmDate(new Date());
            }
            BigDecimal sum = BigDecimal.ZERO; //求明细+折扣明细的所有和
            sum = entity.getFinSaleInvoiceItemList().parallelStream().map(FinSaleInvoiceItem::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
            sum = sum.add(entity.getFinSaleInvoiceDiscountList().parallelStream().map(FinSaleInvoiceDiscount::getCurrencyAmountTax)
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
    private void judgePrice(FinSaleInvoice entity) {
        //明细
        List<FinSaleInvoiceItem> itemList = entity.getFinSaleInvoiceItemList();
        if (CollectionUtils.isNotEmpty(itemList)){
            Long methodGroup = null;
            for (FinSaleInvoiceItem item : itemList) {
                //获取应付暂估中的待核销金额
                FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper
                        .selectFinBookReceiptEstimationItemById(item.getAccountItemSid() == null ? item.getBookReceiptEstimationItemSid() : item.getAccountItemSid());
                if (item.getQuantityLeft().compareTo(BigDecimal.ZERO) < 0 && item.getQuantity().compareTo(BigDecimal.ZERO) >= 0){
                    throw new CustomException("明细中的负向应收暂估流水用来开票的数量只能输入负数！");
                }
                if (item.getQuantityLeft().compareTo(BigDecimal.ZERO) > 0 && item.getQuantity().compareTo(BigDecimal.ZERO) <= 0){
                    throw new CustomException("明细中的正向应收暂估流水用来开票的数量只能输入正数！");
                }
                //校验
                if (item.getQuantity().abs().compareTo(finBookReceiptEstimationItem.getQuantityLeft().abs()) > 0 ){
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
        List<FinSaleInvoiceDiscount> discountList = entity.getFinSaleInvoiceDiscountList();
        if (CollectionUtils.isNotEmpty(discountList)){
            discountList.forEach(item->{
                BigDecimal dhx = new BigDecimal(-1);
                //获取客户扣款流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())){
                    FinBookCustomerDeductionItem finBookCustomerDeductionItem = bookCustomerDeductionItemMapper
                            .selectFinBookCustomerDeductionItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("折扣中的扣款流水用来抵扣的金额只能输入负数！");
                    }
                    dhx = finBookCustomerDeductionItem.getCurrencyAmountTaxDhx().abs();
                }
                //获取客户调账流水明细中的待核销金额
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())){
                    FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = bookCustomerAccountAdjustItemMapper
                            .selectFinBookCustomerAccountAdjustItemById(item.getAccountItemSid());
                    if (item.getCurrencyAmountTaxYingD().compareTo(BigDecimal.ZERO) < 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) >= 0){
                        throw new CustomException("折扣中的负向调账流水用来抵扣的金额只能输入负数！");
                    }
                    if (item.getCurrencyAmountTaxYingD().compareTo(BigDecimal.ZERO) > 0 && item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                        throw new CustomException("折扣中的正向调账流水用来抵扣的金额只能输入正数！");
                    }
                    dhx = finBookCustomerAccountAdjustItem.getCurrencyAmountTaxDhx().abs();
                }
                //校验
                if (dhx.compareTo(BigDecimal.ZERO) >= 0 && item.getCurrencyAmountTax().abs().compareTo(dhx) > 0){
                    throw new CustomException("折扣中的折扣金额的绝对值不能大于待折扣金额的绝对值！");
                }
            });
        }
    }

    /**
     * 设置应收暂估的核销状态
     *
     * @param finPurchaseInvoice
     */
    private FinBookReceiptEstimationItem setClearStatus(FinBookReceiptEstimationItem finBookReceiptEstimationItem){
        //金额：部分核销
        if (finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) != 0 || finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) != 0){
            finBookReceiptEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        //金额：如果已核销等于 应付金额 就修改状态为全部
        if (finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().compareTo(finBookReceiptEstimationItem.getCurrencyAmountTax()) == 0){
            finBookReceiptEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        }
        //如果核销中和已核销加起来也等于 0 就修改状态为待核销
        if ((finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().add(finBookReceiptEstimationItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
            finBookReceiptEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //数量：部分核销
        if (finBookReceiptEstimationItem.getQuantityYhx().compareTo(BigDecimal.ZERO) != 0 || finBookReceiptEstimationItem.getQuantityHxz().compareTo(BigDecimal.ZERO) != 0){
            finBookReceiptEstimationItem.setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        //数量：如果已核销等于 应开票量 就修改状态为全部
        if (finBookReceiptEstimationItem.getQuantityYhx().compareTo(finBookReceiptEstimationItem.getQuantity()) == 0){
            finBookReceiptEstimationItem.setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_QHX);
        }
        //如果核销中和已核销加起来也等于 0 就修改状态为待核销
        if ((finBookReceiptEstimationItem.getQuantityHxz().add(finBookReceiptEstimationItem.getQuantityYhx())).compareTo(BigDecimal.ZERO) == 0) {
            finBookReceiptEstimationItem.setClearStatusQuantity(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //核销状态
        if (finBookReceiptEstimationItem.getClearStatusQuantity().equals(finBookReceiptEstimationItem.getClearStatusMoney())){
            finBookReceiptEstimationItem.setClearStatus(finBookReceiptEstimationItem.getClearStatusMoney());
        }
        if (finBookReceiptEstimationItem.getClearStatusQuantity().equals(ConstantsFinance.CLEAR_STATUS_BFHX)
                || finBookReceiptEstimationItem.getClearStatusMoney().equals(ConstantsFinance.CLEAR_STATUS_BFHX)){
            finBookReceiptEstimationItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        return finBookReceiptEstimationItem;
    }

    /*回退来源流失的数据*/
    @Transactional(rollbackFor = Exception.class)
    public void returnBack(Long finSaleInvoiceSid, String handleStatus){
        //恢复明细表对应的应收暂估流水的核销中数据
        List<FinSaleInvoiceItem> finSaleInvoiceItemList = finSaleInvoiceItemMapper.selectList(
                new QueryWrapper<FinSaleInvoiceItem>().lambda().eq(FinSaleInvoiceItem::getSaleInvoiceSid,finSaleInvoiceSid));
        finSaleInvoiceItemList.forEach(item->{
            //修改应付暂估
            //应付暂估
            FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectById(item.getAccountItemSid());
            finBookReceiptEstimationItem.setBookReceiptEstimationItemSid(item.getAccountItemSid())
                    .setUpdateDate(new Date())
                    .setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus)){
                finBookReceiptEstimationItem.setQuantityYhx(finBookReceiptEstimationItem.getQuantityYhx().subtract(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))
                        .setCurrencyAmountTaxYhx(finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()));
            }else{
                finBookReceiptEstimationItem.setQuantityHxz(finBookReceiptEstimationItem.getQuantityHxz().subtract(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销中数量
                        .setCurrencyAmountTaxHxz(finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()));  //核销中金额（含税）
            }
            //设置应收暂估的核销状态
            finBookReceiptEstimationItem = setClearStatus(finBookReceiptEstimationItem);
            finBookReceiptEstimationItemMapper.updateAllById(finBookReceiptEstimationItem); //修改
        });
        //恢复折扣表对应的流水的核销中数据
        List<FinSaleInvoiceDiscount> finSaleInvoiceDiscountsList = finSaleInvoiceDiscountMapper.selectList(
                new QueryWrapper<FinSaleInvoiceDiscount>().lambda().eq(FinSaleInvoiceDiscount::getSaleInvoiceSid,finSaleInvoiceSid));
        finSaleInvoiceDiscountsList.forEach(item->{
            if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())) {
                FinBookCustomerDeductionItem finBookCustomerDeductionItem = bookCustomerDeductionItemMapper.selectById(item.getAccountItemSid());
                //确认状态下修改撤回 已核销金额
                if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                    finBookCustomerDeductionItem.setCurrencyAmountTaxYhx(finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                }else {   //非确认状态下撤回 核销中金额
                    finBookCustomerDeductionItem.setCurrencyAmountTaxHxz(finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                }
                //核销状态
                finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                if (finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().compareTo(finBookCustomerDeductionItem.getCurrencyAmountTaxKk()) == 0) {
                    finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                if ((finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().add(finBookCustomerDeductionItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                    finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                }
                finBookCustomerDeductionItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                bookCustomerDeductionItemMapper.updateAllById(finBookCustomerDeductionItem);
            }
            if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())) {
                FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = bookCustomerAccountAdjustItemMapper.selectById(item.getAccountItemSid());
                //确认状态下修改撤回 已核销金额
                if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                    finBookCustomerAccountAdjustItem.setCurrencyAmountTaxYhx(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx().subtract(item.getCurrencyAmountTax()));
                }else {   //非确认状态下撤回 核销中金额
                    finBookCustomerAccountAdjustItem.setCurrencyAmountTaxHxz(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().subtract(item.getCurrencyAmountTax()));
                }
                //核销状态
                finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                if (finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx().compareTo(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxTz()) == 0) {
                    finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                }  //如果核销中和已核销加起来也等于 0 就修改状态为待核销
                if ((finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().add(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxYhx())).compareTo(BigDecimal.ZERO) == 0) {
                    finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                }
                finBookCustomerAccountAdjustItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                bookCustomerAccountAdjustItemMapper.updateAllById(finBookCustomerAccountAdjustItem);
            }
        });
    }

    /**
     * 删除明细表
     *
     * @param finSaleInvoiceSid 销售发票的sid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long finSaleInvoiceSid, String handleStatus) {
        //回退来源流水
        returnBack(finSaleInvoiceSid, handleStatus);
        //明细表
        QueryWrapper<FinSaleInvoiceItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("sale_invoice_sid", finSaleInvoiceSid);
        finSaleInvoiceItemMapper.delete(itemQueryWrapper);
        //折扣表
        QueryWrapper<FinSaleInvoiceDiscount> discountQueryWrapper = new QueryWrapper<>();
        discountQueryWrapper.eq("sale_invoice_sid", finSaleInvoiceSid);
        finSaleInvoiceDiscountMapper.delete(discountQueryWrapper);
        //附件表
        QueryWrapper<FinSaleInvoiceAttachment> atmQueryWrapper = new QueryWrapper<>();
        atmQueryWrapper.eq("sale_invoice_sid", finSaleInvoiceSid);
        finSaleInvoiceAttachmentMapper.delete(atmQueryWrapper);
        //应付流水表
        List<FinBookAccountReceivableItem> finBookAccountReceivableItemList = finBookAccountReceivableItemMapper.selectList(
                new QueryWrapper<FinBookAccountReceivableItem>().lambda().eq(FinBookAccountReceivableItem::getReferDocSid, finSaleInvoiceSid));
        if (CollectionUtils.isNotEmpty(finBookAccountReceivableItemList)){
            finBookAccountReceivableItemList.forEach(finBookAccountReceivableItem->{
                finBookAccountReceivableItemMapper.deleteById(finBookAccountReceivableItem.getBookAccountReceivableItemSid());
                finBookAccountReceivableMapper.deleteById(finBookAccountReceivableItem.getBookAccountReceivableSid());
            });
        }
    }

    /**
     * 新增明细表,折扣表,附件表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertChild(List<FinSaleInvoiceItem> itemList, List<FinSaleInvoiceDiscount> discountList, List<FinSaleInvoiceAttachment> atmList, Long sid, String handleStatus) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinSaleInvoiceItem item : itemList) {
                if (item.getCurrencyAmountTax() == null){
                    item.setCurrencyAmountTax(BigDecimal.ZERO);
                }
                if (item.getCurrencyAmount() == null){
                    item.setCurrencyAmount(BigDecimal.ZERO);
                }
                item.setItemNum((long)i++).setTaxAmount(item.getCurrencyAmountTax().subtract(item.getCurrencyAmount()));
                if (item.getSaleInvoiceItemSid() != null) {
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
                item.setSaleInvoiceSid(sid)
                        .setAccountDocumentSid(item.getBookReceiptEstimationSid() == null ? item.getAccountDocumentSid() : item.getBookReceiptEstimationSid())
                        .setAccountDocumentCode(item.getBookReceiptEstimationCode() == null ? item.getAccountDocumentCode() : item.getBookReceiptEstimationCode())
                        .setAccountItemSid(item.getBookReceiptEstimationItemSid() == null ? item.getAccountItemSid() : item.getBookReceiptEstimationItemSid());
                if (!HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                    //修改应收暂估
                    //应收暂估
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemById(item.getAccountItemSid());
                    finBookReceiptEstimationItem.setBookReceiptEstimationItemSid(item.getAccountItemSid())
                            .setQuantityHxz(finBookReceiptEstimationItem.getQuantityHxz().add(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销数量
                            .setCurrencyAmountTaxHxz(finBookReceiptEstimationItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()))   //核销金额（含税）
                            .setUpdateDate(new Date())
                            .setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    finBookReceiptEstimationItem = setClearStatus(finBookReceiptEstimationItem);
                    finBookReceiptEstimationItemMapper.updateAllById(finBookReceiptEstimationItem); //修改
                }
            }
            finSaleInvoiceItemMapper.inserts(itemList);
        }
        if (CollectionUtils.isNotEmpty(discountList)) {
            int j = 1;
            for (FinSaleInvoiceDiscount item : discountList) {
                item.setItemNum(j++);
                if (item.getSaleInvoiceDiscountSid() != null) {
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
                item.setSaleInvoiceSid(sid)
                        .setAccountDocumentSid(item.getAccountDocumentSid())
                        .setAccountDocumentCode(item.getAccountDocumentCode())
                        .setAccountItemSid(item.getAccountItemSid());
                if (!HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                    //客户扣款
                    if (ConstantsFinance.BOOK_TYPE_CKK.equals(item.getBookType())) {
                        FinBookCustomerDeductionItem finBookCustomerDeductionItem = bookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemById(item.getAccountItemSid());
                        finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                        finBookCustomerDeductionItem.setCurrencyAmountTaxHxz(finBookCustomerDeductionItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                        finBookCustomerDeductionItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        bookCustomerDeductionItemMapper.updateAllById(finBookCustomerDeductionItem);
                    }
                    //客户调账
                    if (ConstantsFinance.BOOK_TYPE_CTZ.equals(item.getBookType())) {
                        FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem = bookCustomerAccountAdjustItemMapper.selectFinBookCustomerAccountAdjustItemById(item.getAccountItemSid());
                        finBookCustomerAccountAdjustItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
                        finBookCustomerAccountAdjustItem.setCurrencyAmountTaxHxz(finBookCustomerAccountAdjustItem.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTax()));
                        finBookCustomerAccountAdjustItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        bookCustomerAccountAdjustItemMapper.updateAllById(finBookCustomerAccountAdjustItem);
                    }
                }
            }
            finSaleInvoiceDiscountMapper.inserts(discountList);
        }
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setSaleInvoiceSid(sid);
            });
            finSaleInvoiceAttachmentMapper.inserts(atmList);
        }
    }

    /**
     * 新增应付流水并修改应付暂估
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBook(FinSaleInvoice finSaleInvoice, String handleStatus) {
        //确认则生成流水账
        if (CollectionUtils.isNotEmpty(finSaleInvoice.getFinSaleInvoiceItemList())
                && (finSaleInvoice.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) ||
                finSaleInvoice.getHandleStatus().equals(HandleStatus.REDDASHED.getCode()))) {
            //如果是红冲的
            //红冲流水的“参考应付流水”为原发票生成的应付流水号；
            Long referAdvance = null;
            Long referMiddle = null;
            Long referRemain = null;
            if (HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                FinBookAccountReceivableItem bookItem = new FinBookAccountReceivableItem();
                bookItem.setReferDocSid(finSaleInvoice.getReferenceInvoice());
                List<FinBookAccountReceivableItem> bookItemList = finBookAccountReceivableItemMapper.selectList(new QueryWrapper<FinBookAccountReceivableItem>()
                        .lambda().eq(FinBookAccountReceivableItem::getReferDocSid,finSaleInvoice.getReferenceInvoice()));
                if (CollectionUtils.isNotEmpty(bookItemList)){
                    //预期流水，中期流水，尾期流水
                    for (FinBookAccountReceivableItem item : bookItemList) {
                        if (ConstantsFinance.BOOK_SOURCE_CAT_SFPYS.equals(item.getBookSourceCategory())) {
                            referAdvance = item.getBookAccountReceivableSid();
                        }
                        if (ConstantsFinance.BOOK_SOURCE_CAT_SFPZQ.equals(item.getBookSourceCategory())) {
                            referMiddle = item.getBookAccountReceivableSid();
                        }
                        if (ConstantsFinance.BOOK_SOURCE_CAT_SFPWQ.equals(item.getBookSourceCategory())) {
                            referRemain = item.getBookAccountReceivableSid();
                        }
                    }
                }
            }
            //正常流水
            BigDecimal advance = BigDecimal.ZERO; //预付款
            BigDecimal middle = BigDecimal.ZERO;  // 中期款
            BigDecimal remain = BigDecimal.ZERO;  //尾款
            ConAccountMethodGroup conAccountMethodGroup = new ConAccountMethodGroup();
            Long contractSid = null;
            Long orderSid = null;
            Long orderCode = null;
            String contractCode = null;
            int i = 1, flag = 1;
            int size = finSaleInvoice.getFinSaleInvoiceItemList().size();
            for (FinSaleInvoiceItem item: finSaleInvoice.getFinSaleInvoiceItemList()){
                //如果不是红冲才处理
                if (!HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                    //修改应收暂估
                    //应收暂估
                    FinBookReceiptEstimationItem finBookReceiptEstimationItem = finBookReceiptEstimationItemMapper.selectById(item.getAccountItemSid());
                    finBookReceiptEstimationItem.setBookReceiptEstimationItemSid(item.getAccountItemSid())
                            .setQuantityYhx(finBookReceiptEstimationItem.getQuantityYhx().add(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销数量
                            .setCurrencyAmountTaxYhx(finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().add(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax()))   //核销金额（含税）
                            .setQuantityHxz(finBookReceiptEstimationItem.getQuantityHxz().subtract(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity()))   //核销数量
                            .setUpdateDate(new Date())
                            .setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    finBookReceiptEstimationItem = setClearStatus(finBookReceiptEstimationItem);
                    finBookReceiptEstimationItemMapper.updateAllById(finBookReceiptEstimationItem); //修改
                }
                //得到第一笔有合同数据的应收暂估的合同
                if (contractSid == null && item.getSaleContractSid() != null){
                    contractSid = item.getSaleContractSid();
                    contractCode = item.getSaleContractCode();
                }
                //得到第一笔有订单数据的应收暂估的订单
                if (orderSid == null && item.getSalesOrderSid() != null){
                    orderSid = item.getSalesOrderSid();
                    orderCode = item.getSalesOrderCode();
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
            BigDecimal sum = finSaleInvoice.getTotalCurrencyAmountTax();
            advance = sum.multiply(new BigDecimal(conAccountMethodGroup.getAdvanceRate()));
            middle = sum.multiply(new BigDecimal(conAccountMethodGroup.getMiddleRate()));
            remain = sum.multiply(new BigDecimal(conAccountMethodGroup.getRemainRate()));
            if (finSaleInvoice.getInvoiceType().equals(ConstantsFinance.INVOICE_TYPE_BLUE) && !HandleStatus.REDDASHED.getCode().equals(handleStatus)){
                if (sum.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BaseException("确认失败,蓝票发票金额不能小于0！");
                }
            }
            FinBookAccountReceivable receivable = new FinBookAccountReceivable();
            BeanCopyUtils.copyProperties(finSaleInvoice, receivable);
            receivable.setBookType(ConstantsFinance.BOOK_TYPE_YINGS);
            Calendar cal = Calendar.getInstance();
            receivable.setDocumentDate(new Date())
                    .setPaymentYear(Long.parseLong(String.valueOf(cal.get(Calendar.YEAR))))
                    .setPaymentMonth(cal.get(Calendar.MONTH) + 1);
            int num = 1;
            SalSaleContract contract = new SalSaleContract();
            if (contractSid != null) {
                contract = salSaleContractMapper.selectById(contractSid);
            }
            if (advance.compareTo(BigDecimal.ZERO) != 0){
                receivable.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_SFPYS);
                receivable.setBookAccountReceivableSid(IdWorker.getId());
                receivable.setReferAccountReceivableSid(referAdvance);
                finBookAccountReceivableMapper.insert(receivable);
                //明细表流水账
                FinBookAccountReceivableItem bookItemAdvance = new FinBookAccountReceivableItem();
                BeanCopyUtils.copyProperties(finSaleInvoice, bookItemAdvance);
                bookItemAdvance.setReferDocSid(finSaleInvoice.getSaleInvoiceSid())
                        .setReferDocCode(finSaleInvoice.getSaleInvoiceCode())
                        .setBookAccountReceivableSid(receivable.getBookAccountReceivableSid())
                        .setCurrencyAmountTaxYings(advance)
                        .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setTaxRate(finSaleInvoice.getTaxRate())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                        .setSaleContractSid(contractSid).setSaleContractCode(contractCode).setSaleOrderSid(orderSid).setSaleOrderCode(orderCode);
                bookItemAdvance.setAccountValidDays(new Long(0));
                if (contract != null) {
                    if (contract.getYsAccountValidDays() != null) {
                        bookItemAdvance.setAccountValidDays(new Long(contract.getYsAccountValidDays()));
                    }
                }
                bookItemAdvance.setDayType(conAccountMethodGroup.getDayType()).setItemNum((long)num)
                        .setAccountValidDate(new Date());
                bookItemAdvance.setIsFinanceVerify(ConstantsEms.NO);
                finBookAccountReceivableItemMapper.insert(bookItemAdvance);
            }
            if (middle.compareTo(BigDecimal.ZERO) != 0){
                receivable.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_SFPZQ);
                receivable.setBookAccountReceivableSid(IdWorker.getId());
                receivable.setReferAccountReceivableSid(referMiddle);
                finBookAccountReceivableMapper.insert(receivable);
                //明细表流水账
                FinBookAccountReceivableItem bookItemMiddle = new FinBookAccountReceivableItem();
                BeanCopyUtils.copyProperties(finSaleInvoice, bookItemMiddle);
                bookItemMiddle.setReferDocSid(finSaleInvoice.getSaleInvoiceSid())
                        .setReferDocCode(finSaleInvoice.getSaleInvoiceCode())
                        .setBookAccountReceivableSid(receivable.getBookAccountReceivableSid())
                        .setCurrencyAmountTaxYings(middle)
                        .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setTaxRate(finSaleInvoice.getTaxRate())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                        .setSaleContractSid(contractSid).setSaleContractCode(contractCode).setSaleOrderSid(orderSid).setSaleOrderCode(orderCode);
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
                finBookAccountReceivableItemMapper.insert(bookItemMiddle);
            }
            if (remain.compareTo(BigDecimal.ZERO) != 0){
                receivable.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_SFPWQ);
                receivable.setBookAccountReceivableSid(IdWorker.getId());
                receivable.setReferAccountReceivableSid(referRemain);
                finBookAccountReceivableMapper.insert(receivable);
                //明细表流水账
                FinBookAccountReceivableItem bookItemRemain = new FinBookAccountReceivableItem();
                BeanCopyUtils.copyProperties(finSaleInvoice, bookItemRemain);
                bookItemRemain.setReferDocSid(finSaleInvoice.getSaleInvoiceSid())
                        .setReferDocCode(finSaleInvoice.getSaleInvoiceCode())
                        .setBookAccountReceivableSid(receivable.getBookAccountReceivableSid())
                        .setCurrencyAmountTaxYings(remain)
                        .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setTaxRate(finSaleInvoice.getTaxRate())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                        .setSaleContractSid(contractSid).setSaleContractCode(contractCode).setSaleOrderSid(orderSid).setSaleOrderCode(orderCode);
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
                finBookAccountReceivableItemMapper.insert(bookItemRemain);
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
    public List<FinSaleInvoiceDiscount> bookList(FinSaleInvoiceDiscount request) {
        String bookType = request.getBookType();
        //转为折扣返回实体列表
        List<FinSaleInvoiceDiscount> responses = new ArrayList<>();
        //供应商扣款流水
        if (ConstantsFinance.BOOK_TYPE_CKK.equals(bookType)) {
            FinBookCustomerDeduction book = new FinBookCustomerDeduction();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            //查找供应商扣款报表
            List<FinBookCustomerDeduction> bookList = finBookCustomerDeductionMapper.getReportForm(book);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinSaleInvoiceDiscount response = new FinSaleInvoiceDiscount();
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
        if (ConstantsFinance.BOOK_TYPE_CTZ.equals(bookType)) {
            FinBookCustomerAccountAdjust book = new FinBookCustomerAccountAdjust();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            //查找供应商调账报表
            List<FinBookCustomerAccountAdjust> bookList = finBookCustomerAccountAdjustMapper.getReportForm(book);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinSaleInvoiceDiscount response = new FinSaleInvoiceDiscount();
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
     * @param finSaleInvoice
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(FinSaleInvoice finSaleInvoice) {
        if (finSaleInvoice.getFinSaleInvoiceDiscountList() != null
                && (finSaleInvoice.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)
                || finSaleInvoice.getHandleStatus().equals(HandleStatus.REDDASHED.getCode()))) {
            List<FinSaleInvoiceDiscount> discountList = finSaleInvoice.getFinSaleInvoiceDiscountList();
            discountList.forEach(discount -> {
                if (ConstantsFinance.BOOK_TYPE_CKK.equals(discount.getBookType())) {
                    FinBookCustomerDeductionItem book = bookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemById(discount.getAccountItemSid());
                    book.setCurrencyAmountTaxYhx(book.getCurrencyAmountTaxYhx().add(discount.getCurrencyAmountTax()))
                            .setCurrencyAmountTaxHxz(book.getCurrencyAmountTaxHxz().subtract(discount.getCurrencyAmountTax()))
                            .setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
//                    if (book.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0){
//                        book.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    if (book.getCurrencyAmountTaxYhx().compareTo(book.getCurrencyAmountTaxKk()) == 0){
                        book.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
                    book.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    int row = bookCustomerDeductionItemMapper.updateById(book);
                    if (row == 0) {
                        throw new CustomException(discount.getAccountDocumentCode() + "确认失败,请联系管理员");
                    }
                }
                if (ConstantsFinance.BOOK_TYPE_CTZ.equals(discount.getBookType())) {
                    FinBookCustomerAccountAdjustItem book = bookCustomerAccountAdjustItemMapper.selectFinBookCustomerAccountAdjustItemById(discount.getAccountItemSid());
                    book.setCurrencyAmountTaxYhx(book.getCurrencyAmountTaxYhx().add(discount.getCurrencyAmountTax()))
                            .setCurrencyAmountTaxHxz(book.getCurrencyAmountTaxHxz().subtract(discount.getCurrencyAmountTax()))
                            .setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
//                    if (book.getCurrencyAmountTaxHxz().compareTo(BigDecimal.ZERO) < 0){
//                        book.setCurrencyAmountTaxHxz(BigDecimal.ZERO);
//                    }
                    if (book.getCurrencyAmountTaxYhx().compareTo(book.getCurrencyAmountTaxTz()) == 0){
                        book.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                    }
                    book.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    int row = bookCustomerAccountAdjustItemMapper.updateById(book);
                    if (row == 0) {
                        throw new CustomException(discount.getAccountDocumentCode() + "确认失败,请联系管理员");
                    }
                }
            });
        }
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalidInvoice(Long saleInvoiceSid){
        FinSaleInvoice invoice = finSaleInvoiceMapper.selectById(saleInvoiceSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(invoice.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookAccountReceivable> bookList = finBookAccountReceivableMapper.getReportForm(new FinBookAccountReceivable().setSaleInvoiceCode(invoice.getSaleInvoiceCode()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            finBookAccountReceivableMapper.updateById(item);
        });
        int i = finSaleInvoiceMapper.updateAllById(invoice.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0){
            returnBack(saleInvoiceSid,ConstantsEms.CHECK_STATUS);
            //删除待办：未寄出
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, saleInvoiceSid));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(invoice.getSaleInvoiceSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }

    /**
     * 红冲
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int redDashed(Long saleInvoiceSid){
        FinSaleInvoice invoice = finSaleInvoiceMapper.selectById(saleInvoiceSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(invoice.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        if (!ConstantsFinance.INVOICE_CATE_BZ.equals(invoice.getInvoiceCategory())){
            throw new BaseException("请选择发票类别是“标准发票”的发票!");
        }
        //得到原发票
        FinSaleInvoice origin = this.selectFinSaleInvoiceById(saleInvoiceSid);
        FinSaleInvoice newInvoice = new FinSaleInvoice();
        BeanCopyUtils.copyProperties(origin,newInvoice);
        //设置反向金额
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        newInvoice.setReferenceInvoice(origin.getSaleInvoiceSid())
                .setSaleInvoiceSid(IdWorker.getId())
                .setInvoiceCategory(ConstantsFinance.INVOICE_CATE_HX)
                .setSaleInvoiceCode(null)
                .setDocumentDate(new Date()).setMonthAccountPeriod(sdf.format(date))
                .setTotalCurrencyAmount(origin.getTotalCurrencyAmount().multiply(new BigDecimal(-1)))
                .setTotalCurrencyAmountTax(origin.getTotalCurrencyAmountTax().multiply(new BigDecimal(-1)));
        newInvoice.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                .setUpdaterAccount(null).setUpdateDate(null)
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        //设置新明细列表的反向金额
        List<FinSaleInvoiceItem> newItemList = BeanCopyUtils.copyListProperties(
                origin.getFinSaleInvoiceItemList(), FinSaleInvoiceItem::new);
        newItemList.forEach(item->{
            item.setSaleInvoiceItemSid(null);
            item.setCurrencyAmountTax(item.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
            item.setCurrencyAmount(item.getCurrencyAmount().multiply(new BigDecimal(-1)));
            item.setQuantity(item.getQuantity().multiply(new BigDecimal(-1)));
            item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
        });
        //设置新折扣列表的反向金额
        List<FinSaleInvoiceDiscount> newDiscountList = BeanCopyUtils.copyListProperties(
                origin.getFinSaleInvoiceDiscountList(), FinSaleInvoiceDiscount::new);
        newDiscountList.forEach(item->{
            item.setSaleInvoiceDiscountSid(null);
            item.setCurrencyAmountTax(item.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
            item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
           // item.setCurrencyAmount(item.getCurrencyAmount().multiply(new BigDecimal(-1)));
        });
        //写入新明细
        newInvoice.setFinSaleInvoiceItemList(newItemList)
                .setFinSaleInvoiceDiscountList(newDiscountList)
                .setAttachmentList(null);
        //释放原发票中引用的所有流水数据
        returnBack(saleInvoiceSid,origin.getHandleStatus());
        finSaleInvoiceMapper.update(null,new UpdateWrapper<FinSaleInvoice>().lambda().eq(FinSaleInvoice::getSaleInvoiceSid,saleInvoiceSid)
                .set(FinSaleInvoice::getHandleStatus,HandleStatus.REDDASHED.getCode()));
        //新建红冲发票
        int row = finSaleInvoiceMapper.insert(newInvoice);
        //操作日志
        MongodbDeal.insert(newInvoice.getSaleInvoiceSid(),newInvoice.getHandleStatus(),null,TITLE,null);
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(saleInvoiceSid, BusinessType.REDDASHED.getValue(), msgList, TITLE);
        //红冲明细
        insertChild(newInvoice.getFinSaleInvoiceItemList(),
                newInvoice.getFinSaleInvoiceDiscountList(),
                newInvoice.getAttachmentList(),
                newInvoice.getSaleInvoiceSid(), HandleStatus.REDDASHED.getCode());
        //获取原发票的应付流水,然后生成负向对应的流水
        List<FinBookAccountReceivable> receivableList = new ArrayList<>();
        List<FinBookAccountReceivableItem> receivableItemList = new ArrayList<>();
        receivableItemList = finBookAccountReceivableItemMapper.selectList(new QueryWrapper<FinBookAccountReceivableItem>()
                .lambda().eq(FinBookAccountReceivableItem::getReferDocSid, saleInvoiceSid));
        for (FinBookAccountReceivableItem item : receivableItemList) {
            //获取对应主表
            FinBookAccountReceivable receivable = finBookAccountReceivableMapper.selectOne(new QueryWrapper<FinBookAccountReceivable>()
                    .lambda().eq(FinBookAccountReceivable::getBookAccountReceivableSid, item.getBookAccountReceivableSid()));
            receivable.setReferAccountReceivableSid(receivable.getBookAccountReceivableSid()).setBookAccountReceivableSid(null).setBookAccountReceivableCode(null);
            receivable.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
            receivable.setMonthAccountPeriod(newInvoice.getMonthAccountPeriod()).setDocumentDate(newInvoice.getDocumentDate());
            //插入红冲单据的流水主表
            finBookAccountReceivableMapper.insert(receivable);
            //插入红冲单据的流水明细表
            item.setBookAccountReceivableItemSid(null).setBookAccountReceivableSid(receivable.getBookAccountReceivableSid());
            item.setCurrencyAmountTaxYings(item.getCurrencyAmountTaxYings().multiply(new BigDecimal(-1)))
                    .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx().multiply(new BigDecimal(-1)))
                    .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz().multiply(new BigDecimal(-1)));
            item.setReferDocSid(newInvoice.getSaleInvoiceSid())
                    .setReferDocCode(newInvoice.getSaleInvoiceCode());
            item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null);
            item.setIsFinanceVerify(ConstantsEms.NO);
            finBookAccountReceivableItemMapper.insert(item);
        }
        //删除待办：未寄出
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, saleInvoiceSid));
        return row;
    }


    /**
     * 纸质发票签收
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSignFlag(List<Long> saleInvoiceSids){
        List<FinSaleInvoice> billList = finSaleInvoiceMapper.selectList(new QueryWrapper<FinSaleInvoice>().lambda()
                .in(FinSaleInvoice::getSaleInvoiceSid,saleInvoiceSids)
                .ne(FinSaleInvoice::getHandleStatus,ConstantsEms.CHECK_STATUS));
        if (CollectionUtils.isNotEmpty(billList)){
            throw new BaseException("请选择确认状态的发票单据！");
        }
        LambdaUpdateWrapper<FinSaleInvoice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinSaleInvoice::getSaleInvoiceSid,saleInvoiceSids).set(FinSaleInvoice::getSendFlag, ConstantsFinance.SEND_FLAG_YJC);
        int row = finSaleInvoiceMapper.update(null, updateWrapper);
        if (row > 0){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, saleInvoiceSids));
        }
        return row;
    }

}
