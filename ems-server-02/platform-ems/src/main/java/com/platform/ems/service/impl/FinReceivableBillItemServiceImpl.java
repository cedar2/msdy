package com.platform.ems.service.impl;

import java.util.*;
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
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.FinReceivableBillItemMapper;
import com.platform.ems.domain.FinReceivableBillItem;
import com.platform.ems.service.IFinReceivableBillItemService;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

/**
 * 收款单明细报表Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-22
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillItemServiceImpl extends ServiceImpl<FinReceivableBillItemMapper, FinReceivableBillItem> implements IFinReceivableBillItemService {
    @Autowired
    private FinReceivableBillItemMapper finReceivableBillItemMapper;

    private static final String TITLE = "收款单-明细";

    /**
     * 查询收款单-明细
     *
     * @param receivableBillItemSid 收款单-明细ID
     * @return 收款单-明细
     */
    @Override
    public FinReceivableBillItem selectFinReceivableBillItemById(Long receivableBillItemSid) {
        FinReceivableBillItem finReceivableBillItem = finReceivableBillItemMapper.selectFinReceivableBillItemById(receivableBillItemSid);
        MongodbUtil.find(finReceivableBillItem);
        return finReceivableBillItem;
    }

    /**
     * 查询收款单-明细列表
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 收款单-明细
     */
    @Override
    public List<FinReceivableBillItem> selectFinReceivableBillItemList(FinReceivableBillItem finReceivableBillItem) {
        return finReceivableBillItemMapper.selectFinReceivableBillItemList(finReceivableBillItem);
    }

    /**
     * 新增收款单-明细
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillItem(FinReceivableBillItem finReceivableBillItem) {
        int row = finReceivableBillItemMapper.insert(finReceivableBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBillItem(), finReceivableBillItem);
            MongodbUtil.insertUserLog(finReceivableBillItem.getReceivableBillItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改收款单-明细
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillItem(FinReceivableBillItem finReceivableBillItem) {
        FinReceivableBillItem original = finReceivableBillItemMapper.selectFinReceivableBillItemById(finReceivableBillItem.getReceivableBillItemSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBillItem);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemMapper.updateAllById(finReceivableBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItem.getReceivableBillItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 变更收款单-明细
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillItem(FinReceivableBillItem finReceivableBillItem) {
        FinReceivableBillItem response = finReceivableBillItemMapper.selectFinReceivableBillItemById(finReceivableBillItem.getReceivableBillItemSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBillItem);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItem.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemMapper.updateAllById(finReceivableBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItem.getReceivableBillItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-明细
     *
     * @param receivableBillItemSids 需要删除的收款单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillItemByIds(List<Long> receivableBillItemSids) {
        List<FinReceivableBillItem> list = finReceivableBillItemMapper.selectList(new QueryWrapper<FinReceivableBillItem>()
                .lambda().in(FinReceivableBillItem::getReceivableBillItemSid, receivableBillItemSids));
        int row = finReceivableBillItemMapper.deleteBatchIds(receivableBillItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBillItem());
                MongodbUtil.insertUserLog(o.getReceivableBillItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 设置行号
     *
     * @param order 销售意向单
     * @return 结果
     */
    private void setItemNum(List<FinReceivableBillItem> list) {
        if (CollectionUtil.isEmpty(list)){
            return;
        }
        List<FinReceivableBillItem> nullItemList = list.stream().filter(o->o.getItemNum()==null).collect(toList());
        if (CollectionUtil.isNotEmpty(nullItemList)) {
            long maxNum = 1;
            List<FinReceivableBillItem> haveItemList = list.stream().filter(o->o.getItemNum()!=null).collect(toList());
            if (CollectionUtil.isNotEmpty(haveItemList)) {
                haveItemList = haveItemList.stream().sorted(Comparator.comparing(FinReceivableBillItem::getItemNum).reversed()).collect(Collectors.toList());
                maxNum = haveItemList.get(0).getItemNum() + 1;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getItemNum() == null) {
                    list.get(i).setItemNum(maxNum++);
                }
            }
        }
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinReceivableBill bill) {
        int row = 0;
        List<FinReceivableBillItem> list = bill.getItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinReceivableBillItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setReceivableBillSid(bill.getReceivableBillSid());
                row += insertFinReceivableBillItem(item);
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
        List<FinReceivableBillItem> list = bill.getItemList();
        // 原本明细
        List<FinReceivableBillItem> oldList = finReceivableBillItemMapper.selectList(new QueryWrapper<FinReceivableBillItem>()
                .lambda().eq(FinReceivableBillItem::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinReceivableBillItem> newList = list.stream().filter(o -> o.getReceivableBillItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setItemList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinReceivableBillItem> updateList = list.stream().filter(o -> o.getReceivableBillItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinReceivableBillItem::getReceivableBillItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinReceivableBillItem> map = oldList.stream().collect(Collectors.toMap(FinReceivableBillItem::getReceivableBillItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getReceivableBillItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getReceivableBillItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finReceivableBillItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getReceivableBillItemSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinReceivableBillItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getReceivableBillItemSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinReceivableBillItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getReceivableBillItemSid() != null)
                .map(FinReceivableBillItem::getReceivableBillItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finReceivableBillItemMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getReceivableBillItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
