package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.DevCategoryPlanItem;
import com.platform.ems.mapper.FinCustomerInvoiceRecordMapper;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemInvoice;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinReceivableBillItemInvoiceMapper;
import com.platform.ems.service.IFinReceivableBillItemInvoiceService;

/**
 * 收款单-发票台账明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillItemInvoiceServiceImpl extends ServiceImpl<FinReceivableBillItemInvoiceMapper, FinReceivableBillItemInvoice> implements IFinReceivableBillItemInvoiceService {
    @Autowired
    private FinReceivableBillItemInvoiceMapper finReceivableBillItemInvoiceMapper;
    @Autowired
    private FinCustomerInvoiceRecordMapper finCustomerInvoiceRecordMapper;

    private static final String TITLE = "收款单-发票台账明细表";

    /**
     * 查询收款单-发票台账明细表
     *
     * @param receivableBillItemInvoiceSid 收款单-发票台账明细表ID
     * @return 收款单-发票台账明细表
     */
    @Override
    public FinReceivableBillItemInvoice selectFinReceivableBillItemInvoiceById(Long receivableBillItemInvoiceSid) {
        FinReceivableBillItemInvoice finReceivableBillItemInvoice = finReceivableBillItemInvoiceMapper.selectFinReceivableBillItemInvoiceById(receivableBillItemInvoiceSid);
        MongodbUtil.find(finReceivableBillItemInvoice);
        return finReceivableBillItemInvoice;
    }

    /**
     * 查询收款单-发票台账明细表列表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 收款单-发票台账明细表
     */
    @Override
    public List<FinReceivableBillItemInvoice> selectFinReceivableBillItemInvoiceList(FinReceivableBillItemInvoice finReceivableBillItemInvoice) {
        return finReceivableBillItemInvoiceMapper.selectFinReceivableBillItemInvoiceList(finReceivableBillItemInvoice);
    }

    /**
     * 新增收款单-发票台账明细表
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillItemInvoice(FinReceivableBillItemInvoice finReceivableBillItemInvoice) {
        int row = finReceivableBillItemInvoiceMapper.insert(finReceivableBillItemInvoice);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBillItemInvoice(), finReceivableBillItemInvoice);
            MongodbUtil.insertUserLog(finReceivableBillItemInvoice.getReceivableBillItemInvoiceSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改收款单-发票台账明细表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillItemInvoice(FinReceivableBillItemInvoice finReceivableBillItemInvoice) {
        FinReceivableBillItemInvoice original = finReceivableBillItemInvoiceMapper.selectFinReceivableBillItemInvoiceById(finReceivableBillItemInvoice.getReceivableBillItemInvoiceSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBillItemInvoice);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemInvoice.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemInvoiceMapper.updateAllById(finReceivableBillItemInvoice);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemInvoice.getReceivableBillItemInvoiceSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收款单-发票台账明细表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillItemInvoice(FinReceivableBillItemInvoice finReceivableBillItemInvoice) {
        FinReceivableBillItemInvoice response = finReceivableBillItemInvoiceMapper.selectFinReceivableBillItemInvoiceById(finReceivableBillItemInvoice.getReceivableBillItemInvoiceSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBillItemInvoice);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemInvoice.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemInvoiceMapper.updateAllById(finReceivableBillItemInvoice);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemInvoice.getReceivableBillItemInvoiceSid(), BusinessType.CHANGE.getValue(), response, finReceivableBillItemInvoice, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-发票台账明细表
     *
     * @param receivableBillItemInvoiceSids 需要删除的收款单-发票台账明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillItemInvoiceByIds(List<Long> receivableBillItemInvoiceSids) {
        List<FinReceivableBillItemInvoice> list = finReceivableBillItemInvoiceMapper.selectList(new QueryWrapper<FinReceivableBillItemInvoice>()
                .lambda().in(FinReceivableBillItemInvoice::getReceivableBillItemInvoiceSid, receivableBillItemInvoiceSids));
        int row = finReceivableBillItemInvoiceMapper.deleteBatchIds(receivableBillItemInvoiceSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBillItemInvoice());
                MongodbUtil.insertUserLog(o.getReceivableBillItemInvoiceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinReceivableBill bill) {
        int row = 0;
        List<FinReceivableBillItemInvoice> list = bill.getInvoiceList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinReceivableBillItemInvoice item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setReceivableBillSid(bill.getReceivableBillSid());
                row += insertFinReceivableBillItemInvoice(item);
            }
        }
        return row;
    }

    /**
     * 批量修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByList(FinReceivableBill bill) {
        int row = 0;
        List<FinReceivableBillItemInvoice> list = bill.getInvoiceList();
        // 原本明细
        List<FinReceivableBillItemInvoice> oldList = finReceivableBillItemInvoiceMapper.selectList(new QueryWrapper<FinReceivableBillItemInvoice>()
                .lambda().eq(FinReceivableBillItemInvoice::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinReceivableBillItemInvoice> newList = list.stream().filter(o -> o.getReceivableBillItemInvoiceSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setInvoiceList(newList);
                row = row + insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinReceivableBillItemInvoice> updateList = list.stream().filter(o -> o.getReceivableBillItemInvoiceSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinReceivableBillItemInvoice::getReceivableBillItemInvoiceSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinReceivableBillItemInvoice> map = oldList.stream().collect(Collectors.toMap(FinReceivableBillItemInvoice::getReceivableBillItemInvoiceSid, Function.identity()));
                    for (FinReceivableBillItemInvoice item : updateList) {
                        if (map.containsKey(item.getReceivableBillItemInvoiceSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getReceivableBillItemInvoiceSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            row = row + finReceivableBillItemInvoiceMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getReceivableBillItemInvoiceSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    }
                    // 删除行
                    List<FinReceivableBillItemInvoice> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getReceivableBillItemInvoiceSid())).collect(Collectors.toList());
                    row = row + deleteByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                row = deleteByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除品类规划-明细
     *
     * @param itemList 需要删除的品类规划-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByList(List<FinReceivableBillItemInvoice> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getReceivableBillItemInvoiceSid() != null)
                .map(FinReceivableBillItemInvoice::getReceivableBillItemInvoiceSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finReceivableBillItemInvoiceMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getReceivableBillItemInvoiceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
