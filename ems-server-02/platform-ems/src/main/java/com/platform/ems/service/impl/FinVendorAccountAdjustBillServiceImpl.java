package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISysTodoTaskService;
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
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.service.IFinVendorAccountAdjustBillService;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商调账单Service业务层处理
 *
 * @author qhq
 * @date 2021-05-26
 */
@Service
@SuppressWarnings("all")
public class FinVendorAccountAdjustBillServiceImpl extends ServiceImpl<FinVendorAccountAdjustBillMapper, FinVendorAccountAdjustBill> implements IFinVendorAccountAdjustBillService {
    @Autowired
    private FinVendorAccountAdjustBillMapper finVendorAccountAdjustBillMapper;
    @Autowired
    private FinBookVendorAccountAdjustMapper bookMapper;
    @Autowired
    private FinVendorAccountAdjustBillItemMapper itemMapper;
    @Autowired
    private FinVendorAccountAdjustBillAttachmentMapper atmMapper;
    @Autowired
    private FinBookVendorAccountAdjustItemMapper bookItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;

    private static final String TITLE = "供应商调账单";

    /**
     * 查询供应商调账单
     *
     * @param adjustBillSid 供应商调账单ID
     * @return 供应商调账单
     */
    @Override
    public FinVendorAccountAdjustBill selectFinVendorAccountAdjustBillById(Long adjustBillSid) {
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = finVendorAccountAdjustBillMapper.selectFinVendorAccountAdjustBillById(adjustBillSid);
        FinVendorAccountAdjustBillItem item = new FinVendorAccountAdjustBillItem();
        item.setAdjustBillSid(adjustBillSid);
        List<FinVendorAccountAdjustBillItem> itemList = itemMapper.selectFinVendorAccountAdjustBillItemList(item);
        if (itemList.size() > 0) {
            finVendorAccountAdjustBill.setItemList(itemList);
        }
        FinVendorAccountAdjustBillAttachment atm = new FinVendorAccountAdjustBillAttachment();
        atm.setAdjustBillSid(adjustBillSid);
        List<FinVendorAccountAdjustBillAttachment> atmList = atmMapper.selectFinVendorAccountAdjustBillAttachmentList(atm);
        if (atmList.size() > 0) {
            finVendorAccountAdjustBill.setAttachmentList(atmList);
        }
        MongodbUtil.find(finVendorAccountAdjustBill);
        return finVendorAccountAdjustBill;
    }

    /**
     * 查询供应商调账单列表
     *
     * @param finVendorAccountAdjustBill 供应商调账单
     * @return 供应商调账单
     */
    @Override
    public List<FinVendorAccountAdjustBill> selectFinVendorAccountAdjustBillList(FinVendorAccountAdjustBill finVendorAccountAdjustBill) {
        List<FinVendorAccountAdjustBill> response = finVendorAccountAdjustBillMapper.selectFinVendorAccountAdjustBillList(finVendorAccountAdjustBill);
        return response;
    }

    /**
     * 新增客户调账单
     * 需要注意编码重复校验
     *
     * @param finVendorAccountAdjustBill 客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorAccountAdjustBill(FinVendorAccountAdjustBill finVendorAccountAdjustBill) {
        //设置确认信息
        confirmedInfo(finVendorAccountAdjustBill);
        finVendorAccountAdjustBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
        int row = finVendorAccountAdjustBillMapper.insert(finVendorAccountAdjustBill);
        if (row > 0) {
            //新增明细表，附件表
            insertChild(finVendorAccountAdjustBill.getItemList(), finVendorAccountAdjustBill.getAttachmentList(), finVendorAccountAdjustBill.getAdjustBillSid());
            //新增流水报表
            insertBookAccount(finVendorAccountAdjustBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finVendorAccountAdjustBill.getHandleStatus())) {
                finVendorAccountAdjustBill = finVendorAccountAdjustBillMapper.selectById(finVendorAccountAdjustBill.getAdjustBillSid());
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_VENDOR_ACCOUNT_ADJUST_BILL)
                        .setDocumentSid(finVendorAccountAdjustBill.getAdjustBillSid());
                sysTodoTask.setTitle("供应商调账单: " + finVendorAccountAdjustBill.getAdjustBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finVendorAccountAdjustBill.getAdjustBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_VEN_ADJ_INFO);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finVendorAccountAdjustBill.getAdjustBillSid(), finVendorAccountAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }


    /**
     * 修改客户调账单
     *
     * @param finVendorAccountAdjustBill 客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorAccountAdjustBill(FinVendorAccountAdjustBill finVendorAccountAdjustBill) {
        FinVendorAccountAdjustBill old = selectFinVendorAccountAdjustBillById(finVendorAccountAdjustBill.getAdjustBillSid());
        //设置确认信息
        confirmedInfo(finVendorAccountAdjustBill);
        int row = finVendorAccountAdjustBillMapper.updateById(finVendorAccountAdjustBill);
        if (row > 0) {
            deleteItem(finVendorAccountAdjustBill.getAdjustBillSid());
            insertChild(finVendorAccountAdjustBill.getItemList(), finVendorAccountAdjustBill.getAttachmentList(), finVendorAccountAdjustBill.getAdjustBillSid());
            insertBookAccount(finVendorAccountAdjustBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorAccountAdjustBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finVendorAccountAdjustBill.getAdjustBillSid()));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, finVendorAccountAdjustBill);
            MongodbDeal.update(finVendorAccountAdjustBill.getAdjustBillSid(), old.getHandleStatus(), finVendorAccountAdjustBill.getHandleStatus(), msgList, TITLE,null);

        }
        return row;
    }

    /**
     * 变更客户调账单
     *
     * @param finVendorAccountAdjustBill 客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorAccountAdjustBill(FinVendorAccountAdjustBill finVendorAccountAdjustBill) {
        FinVendorAccountAdjustBill old = selectFinVendorAccountAdjustBillById(finVendorAccountAdjustBill.getAdjustBillSid());
        confirmedInfo(finVendorAccountAdjustBill);
        int row = finVendorAccountAdjustBillMapper.updateAllById(finVendorAccountAdjustBill);
        if (row > 0) {
            deleteItem(finVendorAccountAdjustBill.getAdjustBillSid());
            insertChild(finVendorAccountAdjustBill.getItemList(), finVendorAccountAdjustBill.getAttachmentList(), finVendorAccountAdjustBill.getAdjustBillSid());
            //生成流水账
            insertBookAccount(finVendorAccountAdjustBill);
            //插入日志
            MongodbUtil.insertUserLog(finVendorAccountAdjustBill.getAdjustBillSid(), BusinessType.CHANGE.getValue(), old, finVendorAccountAdjustBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户调账单
     *
     * @param adjustBillSids 需要删除的客户调账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorAccountAdjustBillByIds(List<Long> adjustBillSids) {
        if (CollectionUtils.isEmpty(adjustBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinVendorAccountAdjustBill> finVendorDeductionBillList = finVendorAccountAdjustBillMapper.selectList(new QueryWrapper<FinVendorAccountAdjustBill>().lambda()
                .in(FinVendorAccountAdjustBill::getAdjustBillSid,adjustBillSids)
                .notIn(FinVendorAccountAdjustBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finVendorDeductionBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        int i = finVendorAccountAdjustBillMapper.deleteBatchIds(adjustBillSids);
        if (i > 0) {
            adjustBillSids.forEach(sid -> {
                //删除明细
                deleteItem(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            //删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, adjustBillSids));

        }
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finVendorAccountAdjustBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinVendorAccountAdjustBill finVendorAccountAdjustBill) {
        int row = 0;
        Long[] sids = finVendorAccountAdjustBill.getAdjustBillSidList();
        FinVendorAccountAdjustBill bill = new FinVendorAccountAdjustBill();
        if (sids != null && sids.length > 0) {
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorAccountAdjustBill.getHandleStatus())){
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, sids));
            }
            for (Long id : sids) {
                finVendorAccountAdjustBill.setAdjustBillSid(id);
                bill = this.selectFinVendorAccountAdjustBillById(id);
                if (bill.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                    throw new CustomException("所选数据存在已确认状态，请不要重复确认！:" + bill.getAdjustBillCode());
                }
                bill.setHandleStatus(finVendorAccountAdjustBill.getHandleStatus());
                confirmedInfo(bill);
                row = finVendorAccountAdjustBillMapper.updateById(bill);
                if (row == 0) {
                    throw new CustomException(bill.getAdjustBillCode() + "确认失败,请联系管理员");
                }
                //获取主表明细表内容并生成流水
                if (bill.getItemList() != null) {
                    this.insertBookAccount(bill);
                }
                //插入日志
//                List<OperMsg> msgList = new ArrayList<>();
//                MongodbDeal.check(finVendorAccountAdjustBill.getAdjustBillSid(), finVendorAccountAdjustBill.getHandleStatus(), msgList, TITLE);
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
    public FinVendorAccountAdjustBill confirmedInfo(FinVendorAccountAdjustBill entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || entity.getHandleStatus().equals(ConstantsEms.SUBMIT_STATUS)) {
            if (entity.getItemList() == null){
                throw new CustomException("此操作明细不能为空");
            }
            for (FinVendorAccountAdjustBillItem item : entity.getItemList()){
                if (item.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) == 0){
                    throw new CustomException("此操作明细中调账金额不能等于0！");
                }
            }
            if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                entity.setConfirmDate(new Date());
            }
        }
        return entity;
    }

    /**
     * 添加子表
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertChild(List<FinVendorAccountAdjustBillItem> itemList, List<FinVendorAccountAdjustBillAttachment> atmList, Long sid) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinVendorAccountAdjustBillItem item : itemList){
                item.setAdjustBillSid(sid).setItemNum(i++);
                itemMapper.insert(item);
            }
        }
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(o -> {
                o.setAdjustBillSid(sid);
            });
            atmMapper.inserts(atmList);
        }
    }

    /**
     * 删除子表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long sid) {
        //明细表
        QueryWrapper<FinVendorAccountAdjustBillItem> itemwrapper = new QueryWrapper<>();
        itemwrapper.eq("adjust_bill_sid", sid);
        itemMapper.delete(itemwrapper);
        //附件表
        QueryWrapper<FinVendorAccountAdjustBillAttachment> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("adjust_bill_sid", sid);
        atmMapper.delete(atmWrapper);
        //流水账
        FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem = bookItemMapper.selectOne(new QueryWrapper<FinBookVendorAccountAdjustItem>().lambda().eq(FinBookVendorAccountAdjustItem::getReferDocSid, sid));
        if (finBookVendorAccountAdjustItem != null) {
            bookMapper.deleteById(finBookVendorAccountAdjustItem.getBookAccountAdjustSid());
            bookItemMapper.deleteById(finBookVendorAccountAdjustItem.getBookAccountAdjustItemSid());
        }
    }

    /**
     * 新增调账单流水
     *
     * @param entity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBookAccount(FinVendorAccountAdjustBill entity) {
        if (CollectionUtils.isNotEmpty(entity.getItemList()) && entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认后需要添加一笔流水
            FinVendorAccountAdjustBill bill = entity;
            FinBookVendorAccountAdjust book = new FinBookVendorAccountAdjust();
            BeanCopyUtils.copyProperties(bill, book);
            book.setBookAccountAdjustSid(IdWorker.getId());
            //年份月份
            Calendar cal = Calendar.getInstance();
            book.setPaymentYear(cal.get(Calendar.MONTH) + 1)
                    .setPaymentMonth(cal.get(Calendar.YEAR));
            //流水类型，流水来源类别
            book.setBookType(ConstantsFinance.BOOK_TYPE_VTZ).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_VTZD);
            bookMapper.insert(book);
            //得到调账明细表列表
            List<FinVendorAccountAdjustBillItem> billItemList = bill.getItemList();
            //生成一笔调账流水明细
            FinBookVendorAccountAdjustItem bookItem = new FinBookVendorAccountAdjustItem();
            BeanCopyUtils.copyProperties(book, bookItem);
            bookItem.setBookAccountAdjustSid(book.getBookAccountAdjustSid()).setItemNum(1)
                    .setReferDocSid(bill.getAdjustBillSid()).setReferDocCode(bill.getAdjustBillCode());
            bookItem.setCurrencyAmountTaxTz(BigDecimal.ZERO).setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                    .setCurrencyAmountTaxYhx(BigDecimal.ZERO).setCurrencyAmountTaxHxz(BigDecimal.ZERO);
            List<FinBookVendorAccountAdjustItem> bookItemList = new ArrayList<>();
            //调账明细中的调账金额相加写入流水明细的调账金额
            billItemList.forEach(item -> {
                bookItem.setCurrencyAmountTaxTz(bookItem.getCurrencyAmountTaxTz().add(item.getCurrencyAmountTax()));
            });
            //如果调账金额总和等于0，则有几笔明细就生成几笔流水
            if (bookItem.getCurrencyAmountTaxTz().compareTo(BigDecimal.ZERO) == 0){
                billItemList.forEach(item -> {
                    FinBookVendorAccountAdjustItem temp = new FinBookVendorAccountAdjustItem();
                    BeanCopyUtils.copyProperties(bookItem, temp);
                    temp.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX).setCurrencyAmountTaxYhx(item.getCurrencyAmountTax())
                            .setCurrencyAmountTaxTz(item.getCurrencyAmountTax())
                            .setReferDocItemSid(item.getAdjustBillItemSid()).setAdjustType(item.getAdjustType()).setItemNum(item.getItemNum());
                    bookItemList.add(temp);
                });
                bookItemMapper.inserts(bookItemList);
            }
            else{
                bookItemMapper.insert(bookItem);
            }
        }
    }

    @Override
    public int invalid(Long adjustBillSid){
        FinVendorAccountAdjustBill bill = finVendorAccountAdjustBillMapper.selectById(adjustBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookVendorAccountAdjust> bookList = bookMapper.getReportForm(new FinBookVendorAccountAdjust().setReferDocSid(bill.getAdjustBillSid()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            bookMapper.updateById(item);
        });
        int i = finVendorAccountAdjustBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getAdjustBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }
}
