package com.platform.ems.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 财务报表： 预警、警示
 *
 * @author chenkw
 */
@Service
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class FinBookWarningTask {

    @Autowired
    private FinPayBillMapper finPayBillMapper;
    @Autowired
    private FinReceivableBillMapper finReceivableBillMapper;
    @Autowired
    private FinVendorInvoiceRecordMapper finVendorInvoiceRecordMapper;
    @Autowired
    private FinCustomerInvoiceRecordMapper finCustomerInvoiceRecordMapper;
    @Autowired
    private FinPayBillItemInvoiceMapper finPayBillItemInvoiceMapper;
    @Autowired
    private FinReceivableBillItemInvoiceMapper finReceivableBillItemInvoiceMapper;
    @Autowired
    private FinBookPaymentItemMapper bookPaymentItemMapper;
    @Autowired
    private FinPayBillItemYufuMapper payBillItemYufuMapper;
    @Autowired
    private FinBookReceiptPaymentItemMapper bookReceiptPaymentItemMapper;
    @Autowired
    private FinReceivableBillItemYushouMapper receivableBillItemYushouMapper;
    @Autowired
    private FinBookVendorDeductionItemMapper bookVendorDeductionItemMapper;
    @Autowired
    private FinPayBillItemKoukuanMapper payBillItemKoukuanMapper;
    @Autowired
    private FinBookCustomerDeductionItemMapper bookCustomerDeductionItemMapper;
    @Autowired
    private FinReceivableBillItemKoukuanMapper receivableBillItemKoukuanMapper;

    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;

    @Scheduled(cron = "00 00 07 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void sentWeiDaopiao() {

        List<SysTodoTask> todoTaskList = new ArrayList<>();

        // 删除旧数据
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask()
                .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                .setTableNameList(new String[]{ConstantsTable.TABLE_FIN_PAY_BILL, ConstantsTable.TABLE_FIN_RECEIVABLE_BILL})
                .setTitle("票，请及时跟进！"));

        /**
         * 定时时间：每天早上七点
         * 找到”是否到票提醒“为”是“的付款单，给经办人发送待办消息：付款单XXXX未到票，请及时跟进！
         * 其中，XXXX是付款单号，点击待办可以跳转到对应的付款单详情页面
         * 注意：发送此定时任务前要先清空之前的同类消息
         */
        List<FinPayBill> payBills = finPayBillMapper.selectIsRemainDaopiao();

        if (CollectionUtils.isNotEmpty(payBills)) {

            SysTodoTask sysTodoTask = new SysTodoTask();

            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_FIN_PAY_BILL)
                    .setNoticeDate(new Date());
            // 获取菜单id
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_FIN_PAY_BILL);
            menu = remoteMenuService.getInfoByName(menu).getData();
            if (menu != null && menu.getMenuId() != null) {
                sysTodoTask.setMenuId(menu.getMenuId());
            }

            for (FinPayBill payBill : payBills) {
                SysTodoTask base = new SysTodoTask();
                BeanUtil.copyProperties(sysTodoTask, base);

                base.setClientId(payBill.getClientId())
                        .setTitle("付款单" + payBill.getPayBillCode() + "未到票，请及时跟进！")
                        .setDocumentCode(String.valueOf(payBill.getPayBillCode()))
                        .setDocumentSid(payBill.getPayBillSid());

                base.setUserId(payBill.getAgentId());
                todoTaskList.add(base);
            }
        }

        /**
         * 四、新增定时任务
         * 定时时间：每天早上七点
         * 找到”是否到票提醒“为”是“的收款单，给经办人发送待办消息：收款单XXXX未开票，请及时跟进！
         * 其中，XXXX是收款单号，点击待办可以跳转到对应的收款单详情页面
         * 注意：发送此定时任务前要先清空之前的同类消息
         */

        List<FinReceivableBill> receivableBills = finReceivableBillMapper.selectIsRemainDaopiao();

        if (CollectionUtils.isNotEmpty(receivableBills)) {

            SysTodoTask sysTodoTask = new SysTodoTask();

            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsWorkbench.TODO_FIN_RECEIVALE_BILL)
                    .setNoticeDate(new Date());
            // 获取菜单id
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsTable.TABLE_FIN_RECEIVABLE_BILL);
            menu = remoteMenuService.getInfoByName(menu).getData();
            if (menu != null && menu.getMenuId() != null) {
                sysTodoTask.setMenuId(menu.getMenuId());
            }

            for (FinReceivableBill receivableBill : receivableBills) {
                SysTodoTask base = new SysTodoTask();
                BeanUtil.copyProperties(sysTodoTask, base);

                base.setClientId(receivableBill.getClientId())
                        .setTitle("收款单" + receivableBill.getReceivableBillCode() + "未开票，请及时跟进！")
                        .setDocumentCode(String.valueOf(receivableBill.getReceivableBillCode()))
                        .setDocumentSid(receivableBill.getReceivableBillSid());

                base.setUserId(receivableBill.getAgentId());
                todoTaskList.add(base);
            }
        }

        // 写入待办
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.inserts(todoTaskList);
        }
    }

    @Scheduled(cron = "30 00 07 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateBook() {
        // 获取昨天的日期
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        /**
         * 付款流水报表，定时作业：上午七点
         * 1）找到流水来源类别为“预付款”、最近被付款单引用日期为昨天的付款流水
         * 2）根据1）得到的付款流水号，找到付款单-预付款明细表中引用到此流水，且对应付款单的处理状态为“已确认”的所有明细的金额，并计算金额总和
         * 3）根据2）得到的金额总和，找到对应付款流水的付款金额(含税)，更新数据如下：
         * 》若付款金额(含税)=金额总和，更新付款流水的核销状态为全部核销
         * 》若付款金额(含税)>金额总和且金额总和不为0，更新付款流水的核销状态为部分核销
         * 》若金额总和为0，更新付款流水的核销状态为未核销
         */
        List<FinBookPaymentItem> payments = bookPaymentItemMapper.selectFinBookPaymentItemList(new FinBookPaymentItem()
                .setNewPaymentUseDate(yesterday).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YFK));
        if (CollectionUtils.isNotEmpty(payments)) {
            Long[] sids = payments.stream().map(FinBookPaymentItem::getBookPaymentSid).distinct().toArray(Long[]::new);

            List<FinPayBillItemYufu> bills = payBillItemYufuMapper.selectFinPayBillItemYufuList(new FinPayBillItemYufu()
                    .setBookPaymentSidList(sids).setHandleStatus(ConstantsEms.CHECK_STATUS));
            Map<Long, BigDecimal> map = bills.stream()
                    .collect(Collectors.groupingBy(FinPayBillItemYufu::getBookPaymentSid,
                            Collectors.reducing(BigDecimal.ZERO, FinPayBillItemYufu::getCurrencyAmountTax, BigDecimalSum::sum)));

            for (FinBookPaymentItem book : payments) {

                BigDecimal fk = book.getCurrencyAmountTaxFk();
                BigDecimal sumCount = map.get(book.getBookPaymentSid());

                String clearStatus = null, clearStatusName = "";
                if (sumCount == null || sumCount.compareTo(BigDecimal.ZERO) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    clearStatusName = ":未核销";
                }
                else if (fk != null && fk.compareTo(sumCount) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    clearStatusName = ":全部核销";
                }
                else if (fk != null && sumCount.compareTo(BigDecimal.ZERO) != 0 && fk.compareTo(sumCount) > 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    clearStatusName = ":部分核销";
                }

                if (clearStatus != null && !clearStatus.equals(book.getClearStatus())) {
                    bookPaymentItemMapper.update(null, new UpdateWrapper<FinBookPaymentItem>().lambda()
                            .eq(FinBookPaymentItem::getBookPaymentItemSid, book.getBookPaymentItemSid())
                            .set(FinBookPaymentItem::getClearStatus, clearStatus));
                    // 更新人更新日期
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(book, "clearStatus", book.getClearStatus(), clearStatus, msgList);
                    MongodbUtil.insertUserLogAdmin(book.getBookPaymentItemSid(), BusinessType.CHANGE.getValue(), msgList, "付款流水账预收款",
                            "系统更新核销状态" + clearStatusName);
                }
            }
        }

        /**
         * 收款流水报表，定时作业：上午七点半
         * 1）找到流水来源类别为“预收款”、最近被收款单引用日期为昨天的收款流水
         * 2）根据1）得到的收款流水号，找到收款单-预收款明细表中引用到此流水，且对应收款单的处理状态为“已确认”的所有明细的金额，并计算金额总和
         * 3）根据2）得到的金额总和，找到对应收款流水的收款金额(含税)，更新数据如下：
         * 》若收款金额(含税)=金额总和，更新收款流水的核销状态为全部核销
         * 》若收款金额(含税)>金额总和且金额总和不为0，更新收款流水的核销状态为部分核销
         * 》若金额总和为0，更新收款流水的核销状态为未核销
         */
        List<FinBookReceiptPaymentItem> receipts = bookReceiptPaymentItemMapper.selectFinBookReceiptPaymentItemList(new FinBookReceiptPaymentItem()
                .setNewReceivableUseDate(yesterday).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YSK));
        if (CollectionUtils.isNotEmpty(receipts)) {
            Long[] sids = receipts.stream().map(FinBookReceiptPaymentItem::getBookReceiptPaymentSid).distinct().toArray(Long[]::new);

            List<FinReceivableBillItemYushou> bills = receivableBillItemYushouMapper.selectFinReceivableBillItemYushouList(new FinReceivableBillItemYushou()
                    .setBookReceiptPaymentSidList(sids).setHandleStatus(ConstantsEms.CHECK_STATUS));
            Map<Long, BigDecimal> map = bills.stream()
                    .collect(Collectors.groupingBy(FinReceivableBillItemYushou::getBookReceiptPaymentSid,
                            Collectors.reducing(BigDecimal.ZERO, FinReceivableBillItemYushou::getCurrencyAmountTax, BigDecimalSum::sum)));

            for (FinBookReceiptPaymentItem book : receipts) {

                BigDecimal sk = book.getCurrencyAmountTaxSk();
                BigDecimal sumCount = map.get(book.getBookReceiptPaymentSid());

                String clearStatus = null, clearStatusName = "";
                if (sumCount == null || sumCount.compareTo(BigDecimal.ZERO) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    clearStatusName = ":未核销";
                }
                else if (sk != null && sk.compareTo(sumCount) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    clearStatusName = ":全部核销";
                }
                else if (sk != null && sumCount.compareTo(BigDecimal.ZERO) != 0 && sk.compareTo(sumCount) > 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    clearStatusName = ":部分核销";
                }

                if (clearStatus != null && !clearStatus.equals(book.getClearStatus())) {
                    bookReceiptPaymentItemMapper.update(null, new UpdateWrapper<FinBookReceiptPaymentItem>().lambda()
                            .eq(FinBookReceiptPaymentItem::getBookReceiptPaymentItemSid, book.getBookReceiptPaymentItemSid())
                            .set(FinBookReceiptPaymentItem::getClearStatus, clearStatus));
                    // 更新人更新日期
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(book, "clearStatus", book.getClearStatus(), clearStatus, msgList);
                    MongodbUtil.insertUserLogAdmin(book.getBookReceiptPaymentItemSid(), BusinessType.CHANGE.getValue(), msgList, "收款流水账预收款",
                            "系统更新核销状态" + clearStatusName);
                }
            }
        }

        /**
         * 供应商扣款流水报表，定时作业：上午六点
         * 1）找到最近被付款单引用日期为昨天的供应商扣款流水
         * 2）根据1）得到的扣款流水号，找到付款单-扣款流水明细表中引用到此扣款流水，且对应付款单的处理状态为“已确认”的所有明细的金额，并计算金额总和
         * 3）根据2）得到的金额总和，找到对应扣款流水的扣款金额(含税)，更新数据如下：
         * 》若扣款金额(含税)=金额总和，更新扣款流水的核销状态为全部核销
         * 》若扣款金额(含税)>金额总和且金额总和不为0，更新扣款流水的核销状态为部分核销
         * 》若金额总和为0，更新扣款流水的核销状态为未核销
         */
        List<FinBookVendorDeductionItem> vendorDeductionItems = bookVendorDeductionItemMapper.selectFinBookVendorDeductionItemList
                (new FinBookVendorDeductionItem().setNewPaymentUseDate(yesterday).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_VKKD));
        if (CollectionUtils.isNotEmpty(vendorDeductionItems)) {
            Long[] sids = vendorDeductionItems.stream().map(FinBookVendorDeductionItem::getBookDeductionSid).distinct().toArray(Long[]::new);

            List<FinPayBillItemKoukuan> koukuans = payBillItemKoukuanMapper.selectFinPayBillItemKoukuanList(new FinPayBillItemKoukuan()
                    .setBookDeductionSidList(sids).setHandleStatus(ConstantsEms.CHECK_STATUS));
            Map<Long, BigDecimal> map = koukuans.stream()
                    .collect(Collectors.groupingBy(FinPayBillItemKoukuan::getBookDeductionSid,
                            Collectors.reducing(BigDecimal.ZERO, FinPayBillItemKoukuan::getCurrencyAmountTax, BigDecimalSum::sum)));

            for (FinBookVendorDeductionItem book : vendorDeductionItems) {

                BigDecimal fk = book.getCurrencyAmountTaxKk();
                BigDecimal sumCount = map.get(book.getBookDeductionSid());

                String clearStatus = null, clearStatusName = "";
                if (sumCount == null || sumCount.compareTo(BigDecimal.ZERO) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    clearStatusName = ":未核销";
                }
                else if (fk != null && fk.compareTo(sumCount) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    clearStatusName = ":全部核销";
                }
                else if (fk != null && sumCount.compareTo(BigDecimal.ZERO) != 0 && fk.compareTo(sumCount) > 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    clearStatusName = ":部分核销";
                }

                if (clearStatus != null && !clearStatus.equals(book.getClearStatus())) {
                    bookVendorDeductionItemMapper.update(null, new UpdateWrapper<FinBookVendorDeductionItem>().lambda()
                            .eq(FinBookVendorDeductionItem::getBookDeductionItemSid, book.getBookDeductionItemSid())
                            .set(FinBookVendorDeductionItem::getClearStatus, clearStatus));
                    // 更新人更新日期
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(book, "clearStatus", book.getClearStatus(), clearStatus, msgList);
                    MongodbUtil.insertUserLogAdmin(book.getBookDeductionItemSid(), BusinessType.CHANGE.getValue(), msgList, "付款流水账预收款",
                            "系统更新核销状态" + clearStatusName);
                }
            }
        }

        /**
         * 客户扣款流水报表，定时作业：上午六点半
         * 1）找到最近被收款单引用日期为昨天的客户扣款流水
         * 2）根据1）得到的扣款流水号，找到收款单-扣款流水明细表中引用到此扣款流水，且对应收款单的处理状态为“已确认”的所有明细的金额，并计算金额总和
         * 3）根据2）得到的金额总和，找到对应扣款流水的扣款金额(含税)，更新数据如下：
         * 》若扣款金额(含税)=金额总和，更新扣款流水的核销状态为全部核销
         * 》若扣款金额(含税)>金额总和且金额总和不为0，更新扣款流水的核销状态为部分核销
         * 》若金额总和为0，更新扣款流水的核销状态为未核销
         */
        List<FinBookCustomerDeductionItem> customerDeductionItems = bookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemList
                (new FinBookCustomerDeductionItem().setNewReceivableUseDate(yesterday).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_CKKD));
        if (CollectionUtils.isNotEmpty(customerDeductionItems)) {
            Long[] sids = customerDeductionItems.stream().map(FinBookCustomerDeductionItem::getBookDeductionSid).distinct().toArray(Long[]::new);

            List<FinReceivableBillItemKoukuan> koukuans = receivableBillItemKoukuanMapper.selectFinReceivableBillItemKoukuanList(new FinReceivableBillItemKoukuan()
                    .setBookDeductionSidList(sids).setHandleStatus(ConstantsEms.CHECK_STATUS));
            Map<Long, BigDecimal> map = koukuans.stream()
                    .collect(Collectors.groupingBy(FinReceivableBillItemKoukuan::getBookDeductionSid,
                            Collectors.reducing(BigDecimal.ZERO, FinReceivableBillItemKoukuan::getCurrencyAmountTax, BigDecimalSum::sum)));

            for (FinBookCustomerDeductionItem book : customerDeductionItems) {

                BigDecimal fk = book.getCurrencyAmountTaxKk();
                BigDecimal sumCount = map.get(book.getBookDeductionSid());

                String clearStatus = null, clearStatusName = "";

                if (sumCount == null || sumCount.compareTo(BigDecimal.ZERO) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_WHX;
                    clearStatusName = ":未核销";
                }
                else if (fk != null && fk.compareTo(sumCount) == 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_QHX;
                    clearStatusName = ":全部核销";
                }
                else if (fk != null && sumCount.compareTo(BigDecimal.ZERO) != 0 && fk.compareTo(sumCount) > 0) {
                    clearStatus = ConstantsFinance.CLEAR_STATUS_BFHX;
                    clearStatusName = ":部分核销";
                }
                if (clearStatus != null && !clearStatus.equals(book.getClearStatus())) {
                    bookCustomerDeductionItemMapper.update(null, new UpdateWrapper<FinBookCustomerDeductionItem>().lambda()
                            .eq(FinBookCustomerDeductionItem::getBookDeductionItemSid, book.getBookDeductionItemSid())
                            .set(FinBookCustomerDeductionItem::getClearStatus, clearStatus));
                    // 更新人更新日期
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(book, "clearStatus", book.getClearStatus(), clearStatus, msgList);
                    MongodbUtil.insertUserLogAdmin(book.getBookDeductionItemSid(), BusinessType.CHANGE.getValue(), msgList, "收款流水账预收款",
                            "系统更新核销状态" + clearStatusName);
                }
            }
        }
    }

    @Scheduled(cron = "00 00 08 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoice() {
        // 获取昨天的日期
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化为字符串
        String formattedDate = yesterdayDate.format(formatter);
        /**
         * 一、供应商发票台账定时作业：上午八点
         * 1）找到更新日期为昨天的供应商发票台账
         * 2）根据1）得到的发票台账号，找到付款单-发票台账明细表中引用到此发票台账，且对应付款单的处理状态为“已确认” 的所有明细的金额，并计算金额总和
         * 3）根据2）得到的金额总和，找到对应发票台账的票面总金额(含税)，更新数据如下：
         * 》若票面总金额(含税)=金额总和，更新发票台账的付款状态为全部付款
         * 》若票面总金额(含税)>金额总和且金额总和不为0，更新发票台账的付款状态为部分付款
         * 》若金额总和为0，更新发票台账的付款状态为未付款
         */
        List<FinVendorInvoiceRecord> vendorInvoiceRecords = finVendorInvoiceRecordMapper.selectList(new QueryWrapper<FinVendorInvoiceRecord>()
                .lambda().eq(FinVendorInvoiceRecord::getNewPaymentUseDate, formattedDate));
        if (CollectionUtils.isNotEmpty(vendorInvoiceRecords)) {
            Long[] sids = vendorInvoiceRecords.stream().map(FinVendorInvoiceRecord::getVendorInvoiceRecordSid).toArray(Long[]::new);
            List<FinPayBillItemInvoice> bills = finPayBillItemInvoiceMapper.selectFinPayBillItemInvoiceList(new FinPayBillItemInvoice()
                    .setVendorInvoiceRecordSidList(sids).setBillHandleStatus(ConstantsEms.CHECK_STATUS));
            Map<Long, BigDecimal> map = bills.stream()
                    .collect(Collectors.groupingBy(FinPayBillItemInvoice::getVendorInvoiceRecordSid,
                            Collectors.reducing(BigDecimal.ZERO, FinPayBillItemInvoice::getCurrencyAmountTax, BigDecimalSum::sum)));

            for (FinVendorInvoiceRecord invoiceRecord : vendorInvoiceRecords) {
                String fukuanStatus = invoiceRecord.getFukuanStatus();
                BigDecimal sumCount = map.get(invoiceRecord.getVendorInvoiceRecordSid());

                String fukuan = "";
                if (sumCount == null || sumCount.compareTo(BigDecimal.ZERO) == 0) {
                    invoiceRecord.setFukuanStatus(ConstantsFinance.FUKUAN_STATUS_WFK);
                    fukuan = ":" + "未付款";
                }
                else if (invoiceRecord.getTotalCurrencyAmountTax() != null
                        && invoiceRecord.getTotalCurrencyAmountTax().compareTo(sumCount) == 0) {
                    invoiceRecord.setFukuanStatus(ConstantsFinance.FUKUAN_STATUS_QBFK);
                    fukuan = ":" + "全部付款";
                }
                else if (invoiceRecord.getTotalCurrencyAmountTax() != null && sumCount != null && sumCount.compareTo(BigDecimal.ZERO) != 0
                        && invoiceRecord.getTotalCurrencyAmountTax().compareTo(sumCount) > 0) {
                    invoiceRecord.setFukuanStatus(ConstantsFinance.FUKUAN_STATUS_BFFK);
                    fukuan = ":" + "部分付款";
                }
                if (!fukuanStatus.equals(invoiceRecord.getFukuanStatus())) {
                    finVendorInvoiceRecordMapper.update(null, new UpdateWrapper<FinVendorInvoiceRecord>().lambda()
                            .eq(FinVendorInvoiceRecord::getVendorInvoiceRecordSid, invoiceRecord.getVendorInvoiceRecordSid())
                            .set(FinVendorInvoiceRecord::getFukuanStatus, invoiceRecord.getFukuanStatus()));
                    // 更新人更新日期
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(invoiceRecord, "fukuanStatus", fukuanStatus, invoiceRecord.getFukuanStatus(), msgList);
                    MongodbUtil.insertUserLogAdmin(invoiceRecord.getVendorInvoiceRecordSid(), BusinessType.CHANGE.getValue(), msgList, "供应商发票台账表",
                            "系统更新收款状态" + fukuan);
                }
            }
        }
        /**
         * 二、客户发票台账定时作业：上午八点
         * 1）找到更新日期为昨天的客户发票台账
         * 2）根据1）得到的发票台账号，找到收款单-发票台账明细表中引用到此发票台账，且对应付款单的处理状态为“已确认” 的所有明细的金额，并计算金额总和
         * 3）根据2）得到的金额总和，找到对应发票台账的票面总金额(含税)，更新数据如下：
         * 》若票面总金额(含税)=金额总和，更新发票台账的收款状态为全部收款
         * 》若票面总金额(含税)>金额总和且金额总和不为0，更新发票台账的收款状态为部分收款
         * 》若金额总和为0，更新发票台账的收款状态为未收款
         */
        List<FinCustomerInvoiceRecord> customerInvoiceRecords = finCustomerInvoiceRecordMapper.selectList(new QueryWrapper<FinCustomerInvoiceRecord>()
                .lambda().eq(FinCustomerInvoiceRecord::getNewReceivableUseDate, formattedDate));
        if (CollectionUtils.isNotEmpty(customerInvoiceRecords)) {
            Long[] sids = customerInvoiceRecords.stream().map(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid).toArray(Long[]::new);
            List<FinReceivableBillItemInvoice> bills = finReceivableBillItemInvoiceMapper.selectFinReceivableBillItemInvoiceList(new FinReceivableBillItemInvoice()
                    .setCustomerInvoiceRecordSidList(sids).setBillHandleStatus(ConstantsEms.CHECK_STATUS));
            Map<Long, BigDecimal> map = bills.stream()
                    .collect(Collectors.groupingBy(FinReceivableBillItemInvoice::getCustomerInvoiceRecordSid,
                            Collectors.reducing(BigDecimal.ZERO, FinReceivableBillItemInvoice::getCurrencyAmountTax, BigDecimalSum::sum)));

            for (FinCustomerInvoiceRecord invoiceRecord : customerInvoiceRecords) {
                String shoukuanStatus = invoiceRecord.getShoukuanStatus();
                BigDecimal sumCount = map.get(invoiceRecord.getCustomerInvoiceRecordSid());

                String shoukuan = "";
                if (sumCount == null || sumCount.compareTo(BigDecimal.ZERO) == 0) {
                    invoiceRecord.setShoukuanStatus(ConstantsFinance.SHOUKUAN_STATUS_WSK);
                    shoukuan = ":" + "未收款";
                }
                else if (invoiceRecord.getTotalCurrencyAmountTax() != null
                        && invoiceRecord.getTotalCurrencyAmountTax().compareTo(sumCount) == 0) {
                    invoiceRecord.setShoukuanStatus(ConstantsFinance.SHOUKUAN_STATUS_QBSK);
                    shoukuan = ":" + "全部收款";
                }
                else if (invoiceRecord.getTotalCurrencyAmountTax() != null && sumCount != null && sumCount.compareTo(BigDecimal.ZERO) != 0
                        && invoiceRecord.getTotalCurrencyAmountTax().compareTo(sumCount) > 0) {
                    invoiceRecord.setShoukuanStatus(ConstantsFinance.SHOUKUAN_STATUS_BFSK);
                    shoukuan = ":" + "部分收款";
                }

                if (!shoukuanStatus.equals(invoiceRecord.getShoukuanStatus())) {
                    finCustomerInvoiceRecordMapper.update(null, new UpdateWrapper<FinCustomerInvoiceRecord>().lambda()
                            .eq(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, invoiceRecord.getCustomerInvoiceRecordSid())
                            .set(FinCustomerInvoiceRecord::getShoukuanStatus, invoiceRecord.getShoukuanStatus()));
                    // 更新人更新日期
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(invoiceRecord, "shoukuanStatus", shoukuanStatus, invoiceRecord.getShoukuanStatus(), msgList);
                    MongodbUtil.insertUserLogAdmin(invoiceRecord.getCustomerInvoiceRecordSid(), BusinessType.CHANGE.getValue(), msgList, "客户发票台账表",
                            "系统更新收款状态" + shoukuan);
                }
            }
        }
    }
}
