package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.DevCategoryPlanItem;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItem;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinPayBillItemMapper;
import com.platform.ems.service.IFinPayBillItemService;

/**
 * 付款单明细报表Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Service
@SuppressWarnings("all")
public class FinPayBillItemServiceImpl extends ServiceImpl<FinPayBillItemMapper, FinPayBillItem> implements IFinPayBillItemService {
    @Autowired
    private FinPayBillItemMapper finPayBillItemMapper;

    private static final String TITLE = "付款单-明细";

    /**
     * 查询付款单-明细
     *
     * @param payBillItemSid 付款单-明细ID
     * @return 付款单-明细
     */
    @Override
    public FinPayBillItem selectFinPayBillItemById(Long payBillItemSid) {
        FinPayBillItem finPayBillItem = finPayBillItemMapper.selectFinPayBillItemById(payBillItemSid);
        MongodbUtil.find(finPayBillItem);
        return finPayBillItem;
    }

    /**
     * 查询付款单-明细列表
     *
     * @param finPayBillItem 付款单-明细
     * @return 付款单-明细
     */
    @Override
    public List<FinPayBillItem> selectFinPayBillItemList(FinPayBillItem finPayBillItem) {
        return finPayBillItemMapper.selectFinPayBillItemList(finPayBillItem);
    }

    /**
     * 新增付款单-明细
     * 需要注意编码重复校验
     *
     * @param finPayBillItem 付款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBillItem(FinPayBillItem finPayBillItem) {
        int row = finPayBillItemMapper.insert(finPayBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinPayBillItem(), finPayBillItem);
            MongodbUtil.insertUserLog(finPayBillItem.getPayBillItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改付款单-明细
     *
     * @param finPayBillItem 付款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBillItem(FinPayBillItem finPayBillItem) {
        FinPayBillItem original = finPayBillItemMapper.selectFinPayBillItemById(finPayBillItem.getPayBillItemSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finPayBillItem);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemMapper.updateAllById(finPayBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItem.getPayBillItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更付款单-明细
     *
     * @param finPayBillItem 付款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBillItem(FinPayBillItem finPayBillItem) {
        FinPayBillItem response = finPayBillItemMapper.selectFinPayBillItemById(finPayBillItem.getPayBillItemSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finPayBillItem);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemMapper.updateAllById(finPayBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItem.getPayBillItemSid(), BusinessType.CHANGE.getValue(), response, finPayBillItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款单-明细
     *
     * @param payBillItemSids 需要删除的付款单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillItemByIds(List<Long> payBillItemSids) {
        List<FinPayBillItem> list = finPayBillItemMapper.selectList(new QueryWrapper<FinPayBillItem>()
                .lambda().in(FinPayBillItem::getPayBillItemSid, payBillItemSids));
        int row = finPayBillItemMapper.deleteBatchIds(payBillItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinPayBillItem());
                MongodbUtil.insertUserLog(o.getPayBillItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinPayBill bill) {
        int row = 0;
        List<FinPayBillItem> list = bill.getItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinPayBillItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setPayBillSid(bill.getPayBillSid());
                row += insertFinPayBillItem(item);
            }
        }
        return row;
    }

    /**
     * 批量修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByList(FinPayBill bill) {
        int row = 0;
        List<FinPayBillItem> list = bill.getItemList();
        // 原本明细
        List<FinPayBillItem> oldList = finPayBillItemMapper.selectList(new QueryWrapper<FinPayBillItem>()
                .lambda().eq(FinPayBillItem::getPayBillSid, bill.getPayBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinPayBillItem> newList = list.stream().filter(o -> o.getPayBillItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setItemList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinPayBillItem> updateList = list.stream().filter(o -> o.getPayBillItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinPayBillItem::getPayBillItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinPayBillItem> map = oldList.stream().collect(Collectors.toMap(FinPayBillItem::getPayBillItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getPayBillItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getPayBillItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finPayBillItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getPayBillItemSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinPayBillItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getPayBillItemSid())).collect(Collectors.toList());
                    deleteByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteByList(oldList);
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
    public int deleteByList(List<FinPayBillItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getPayBillItemSid() != null)
                .map(FinPayBillItem::getPayBillItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finPayBillItemMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getPayBillItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
