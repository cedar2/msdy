package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.SalSalesIntentOrder;
import com.platform.ems.domain.SalSalesOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SalSalesIntentOrderItemMapper;
import com.platform.ems.domain.SalSalesIntentOrderItem;
import com.platform.ems.service.ISalSalesIntentOrderItemService;

import static java.util.stream.Collectors.toList;

/**
 * 销售意向单-明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-10-17
 */
@Service
@SuppressWarnings("all" )
public class SalSalesIntentOrderItemServiceImpl extends ServiceImpl<SalSalesIntentOrderItemMapper,SalSalesIntentOrderItem> implements ISalSalesIntentOrderItemService {
    @Autowired
    private SalSalesIntentOrderItemMapper salSalesIntentOrderItemMapper;

    private static final String TITLE = "销售意向单-明细" ;

    /**
     * 查询销售意向单-明细
     *
     * @param salesIntentOrderItemSid 销售意向单-明细ID
     * @return 销售意向单-明细
     */
    @Override
    public SalSalesIntentOrderItem selectSalSalesIntentOrderItemById(Long salesIntentOrderItemSid) {
        SalSalesIntentOrderItem salSalesIntentOrderItem = salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemById(salesIntentOrderItemSid);
        MongodbUtil.find(salSalesIntentOrderItem);
        return salSalesIntentOrderItem;
    }

    /**
     * 查询销售意向单-明细  根据主表sid
     *
     * @param salesIntentOrderSid 销售意向单-明细ID
     * @return 销售意向单-明细
     */
    @Override
    public List<SalSalesIntentOrderItem> selectSalSalesIntentOrderItemByOrderId(Long salesIntentOrderSid) {
        List<SalSalesIntentOrderItem> intentOrderItemList = salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemList(new SalSalesIntentOrderItem()
                .setSalesIntentOrderSid(salesIntentOrderSid));
        if (CollectionUtil.isNotEmpty(intentOrderItemList)) {
            intentOrderItemList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return intentOrderItemList;
    }

    /**
     * 查询销售意向单-明细列表
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 销售意向单-明细
     */
    @Override
    public List<SalSalesIntentOrderItem> selectSalSalesIntentOrderItemList(SalSalesIntentOrderItem salSalesIntentOrderItem) {
        return salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemList(salSalesIntentOrderItem);
    }

    /**
     * 新增销售意向单-明细
     * 需要注意编码重复校验
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesIntentOrderItem(SalSalesIntentOrderItem salSalesIntentOrderItem) {
        int row = salSalesIntentOrderItemMapper.insert(salSalesIntentOrderItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new SalSalesIntentOrderItem(), salSalesIntentOrderItem);
            MongodbUtil.insertUserLog(salSalesIntentOrderItem.getSalesIntentOrderItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量新增销售意向单-明细
     *
     * @param salSalesIntentOrderItemList 销售意向单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesIntentOrderItemList(SalSalesIntentOrder order) {
        int row = 0;
        List<SalSalesIntentOrderItem> list = order.getIntentOrderItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            setItemNum(list);
            SalSalesIntentOrderItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setSalesIntentOrderSid(order.getSalesIntentOrderSid());
                row += insertSalSalesIntentOrderItem(item);
            }
        }
        return row;
    }

    /**
     * 修改销售意向单-明细
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesIntentOrderItem(SalSalesIntentOrderItem salSalesIntentOrderItem) {
        int row = salSalesIntentOrderItemMapper.updateAllById(salSalesIntentOrderItem);
        if (row > 0) {
            SalSalesIntentOrderItem original = salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemById(salSalesIntentOrderItem.getSalesIntentOrderItemSid());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, salSalesIntentOrderItem);
            MongodbUtil.insertUserLog(salSalesIntentOrderItem.getSalesIntentOrderItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 设置行号
     *
     * @param order 销售意向单
     * @return 结果
     */
    private void setItemNum(List<SalSalesIntentOrderItem> list) {
        List<SalSalesIntentOrderItem> nullItemList = list.stream().filter(o->o.getItemNum()==null).collect(toList());
        if (CollectionUtil.isNotEmpty(nullItemList)) {
            long maxNum = 1;
            if (CollectionUtil.isNotEmpty(list)){
                List<SalSalesIntentOrderItem> haveItemList = list.stream().filter(o->o.getItemNum()!=null).collect(toList());
                if (CollectionUtil.isNotEmpty(haveItemList)) {
                    haveItemList = haveItemList.stream().sorted(Comparator.comparing(SalSalesIntentOrderItem::getItemNum).reversed()).collect(Collectors.toList());
                    maxNum = haveItemList.get(0).getItemNum() + 1;
                }
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getItemNum() == null) {
                    list.get(i).setItemNum(maxNum++);
                }
            }
        }
    }

    /**
     * 批量修改销售意向单-明细
     *
     * @param order 销售意向单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesIntentOrderItemList(SalSalesIntentOrder order) {
        int row = 0;
        List<SalSalesIntentOrderItem> list = order.getIntentOrderItemList();
        setItemNum(list);
        // 原本明细
        List<SalSalesIntentOrderItem> oldList = salSalesIntentOrderItemMapper.selectList(new QueryWrapper<SalSalesIntentOrderItem>()
                .lambda().eq(SalSalesIntentOrderItem::getSalesIntentOrderSid, order.getSalesIntentOrderSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<SalSalesIntentOrderItem> newList = list.stream().filter(o -> o.getSalesIntentOrderItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                order.setIntentOrderItemList(newList);
                insertSalSalesIntentOrderItemList(order);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<SalSalesIntentOrderItem> updateList = list.stream().filter(o -> o.getSalesIntentOrderItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(SalSalesIntentOrderItem::getSalesIntentOrderItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, SalSalesIntentOrderItem> map = oldList.stream().collect(Collectors.toMap(SalSalesIntentOrderItem::getSalesIntentOrderItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getSalesIntentOrderItemSid())) {
                            salSalesIntentOrderItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getSalesIntentOrderItemSid(), order.getHandleStatus(), map.get(item.getSalesIntentOrderItemSid()), item, TITLE);
                        }
                    });
                    // 删除行
                    List<SalSalesIntentOrderItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getSalesIntentOrderItemSid())).collect(Collectors.toList());
                    deleteSalSalesIntentOrderItemList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteSalSalesIntentOrderItemList(oldList);
            }
        }
        return row;
    }

    /**
     * 变更销售意向单-明细
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalSalesIntentOrderItem(SalSalesIntentOrderItem salSalesIntentOrderItem) {
        int row = salSalesIntentOrderItemMapper.updateAllById(salSalesIntentOrderItem);
        if (row > 0) {
            SalSalesIntentOrderItem response = salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemById(salSalesIntentOrderItem.getSalesIntentOrderItemSid());
            //插入日志
            MongodbUtil.insertUserLog(salSalesIntentOrderItem.getSalesIntentOrderItemSid(), BusinessType.CHANGE.getValue(), response, salSalesIntentOrderItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售意向单-明细
     *
     * @param salesIntentOrderItemSids 需要删除的销售意向单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesIntentOrderItemByIds(List<Long> salesIntentOrderItemSids) {
        List<SalSalesIntentOrderItem> list = salSalesIntentOrderItemMapper.selectList(new QueryWrapper<SalSalesIntentOrderItem>()
                .lambda().in(SalSalesIntentOrderItem::getSalesIntentOrderItemSid, salesIntentOrderItemSids));
        int row = salSalesIntentOrderItemMapper.deleteBatchIds(salesIntentOrderItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new SalSalesIntentOrderItem());
                MongodbUtil.insertUserLog(o.getSalesIntentOrderItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量删除销售意向单-明细
     *
     * @param itemList 需要删除的销售意向单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesIntentOrderItemList(List<SalSalesIntentOrderItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> salesIntentOrderItemSids = itemList.stream().filter(o -> o.getSalesIntentOrderItemSid() != null)
                .map(SalSalesIntentOrderItem::getSalesIntentOrderItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(salesIntentOrderItemSids)) {
            row = salSalesIntentOrderItemMapper.deleteBatchIds(salesIntentOrderItemSids);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new SalSalesIntentOrderItem());
                    MongodbUtil.insertUserLog(o.getSalesIntentOrderItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除销售意向单-明细 根据主表sids
     *
     * @param orderSids 需要删除的销售意向单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesIntentOrderItemListByOrder(List<Long> orderSids) {
        List<SalSalesIntentOrderItem> itemList = salSalesIntentOrderItemMapper.selectList(new QueryWrapper<SalSalesIntentOrderItem>()
                .lambda().in(SalSalesIntentOrderItem::getSalesIntentOrderSid, orderSids));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteSalSalesIntentOrderItemList(itemList);
        }
        return row;
    }

    /**
     *  按照“商品/物料编码+SKU1序号+SKU1名称+SKU2序号+SKU2名称”升序排列
     * （SKU1序号、SKU2序号，取对应商品/物料档案的“SKU1”、“SKU2”页签中的“序号”清单列的值）
     */
    @Override
    public List<SalSalesIntentOrderItem> newSort(List<SalSalesIntentOrderItem> itemList){
        if (CollectionUtil.isEmpty(itemList)) {
            return itemList;
        }
        List<SalSalesIntentOrderItem> itemMat = itemList.stream().sorted(
                Comparator.comparing(SalSalesIntentOrderItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(SalSalesIntentOrderItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(SalSalesIntentOrderItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(SalSalesIntentOrderItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(SalSalesIntentOrderItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
        ).collect(toList());
        return itemMat;
    }

}
