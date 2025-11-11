package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.StringUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.PayProductProcessStepItem;
import com.platform.ems.domain.PayUpdateProductProcessStep;
import com.platform.ems.domain.SalSalesIntentOrderItem;
import com.platform.ems.mapper.PayProductProcessStepItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PayUpdateProductProcessStepItemMapper;
import com.platform.ems.domain.PayUpdateProductProcessStepItem;
import com.platform.ems.service.IPayUpdateProductProcessStepItemService;

/**
 * 商品道序变更-明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-11-08
 */
@Service
@SuppressWarnings("all")
public class PayUpdateProductProcessStepItemServiceImpl extends ServiceImpl<PayUpdateProductProcessStepItemMapper, PayUpdateProductProcessStepItem> implements IPayUpdateProductProcessStepItemService {
    @Autowired
    private PayUpdateProductProcessStepItemMapper payUpdateProductProcessStepItemMapper;

    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;

    private static final String TITLE = "商品道序变更-明细";

    /**
     * 查询商品道序变更-明细
     *
     * @param updateStepItemSid 商品道序变更-明细ID
     * @return 商品道序变更-明细
     */
    @Override
    public PayUpdateProductProcessStepItem selectPayUpdateProductProcessStepItemById(Long updateStepItemSid) {
        PayUpdateProductProcessStepItem payUpdateProductProcessStepItem = payUpdateProductProcessStepItemMapper.selectPayUpdateProductProcessStepItemById(updateStepItemSid);
        MongodbUtil.find(payUpdateProductProcessStepItem);
        return payUpdateProductProcessStepItem;
    }

    /**
     * 查询商品道序变更-明细列表
     *
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 商品道序变更-明细
     */
    @Override
    public List<PayUpdateProductProcessStepItem> selectPayUpdateProductProcessStepItemList(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem) {
        return payUpdateProductProcessStepItemMapper.selectPayUpdateProductProcessStepItemList(payUpdateProductProcessStepItem);
    }

    /**
     * 新增商品道序变更-明细
     * 需要注意编码重复校验
     *
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayUpdateProductProcessStepItem(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem) {
        int row = payUpdateProductProcessStepItemMapper.insert(payUpdateProductProcessStepItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PayUpdateProductProcessStepItem(), payUpdateProductProcessStepItem);
            MongodbDeal.insert(payUpdateProductProcessStepItem.getUpdateStepItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改商品道序变更-明细
     *
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayUpdateProductProcessStepItem(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem) {
        PayUpdateProductProcessStepItem original = payUpdateProductProcessStepItemMapper.selectPayUpdateProductProcessStepItemById(payUpdateProductProcessStepItem.getUpdateStepItemSid());
        int row = payUpdateProductProcessStepItemMapper.updateById(payUpdateProductProcessStepItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, payUpdateProductProcessStepItem);
            MongodbUtil.insertUserLog(payUpdateProductProcessStepItem.getUpdateStepItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更商品道序变更-明细
     *
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayUpdateProductProcessStepItem(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem) {
        PayUpdateProductProcessStepItem response = payUpdateProductProcessStepItemMapper.selectPayUpdateProductProcessStepItemById(payUpdateProductProcessStepItem.getUpdateStepItemSid());
        int row = payUpdateProductProcessStepItemMapper.updateAllById(payUpdateProductProcessStepItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payUpdateProductProcessStepItem.getUpdateStepItemSid(), BusinessType.CHANGE.getValue(), response, payUpdateProductProcessStepItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品道序变更-明细
     *
     * @param updateStepItemSids 需要删除的商品道序变更-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayUpdateProductProcessStepItemByIds(List<Long> updateStepItemSids) {
        List<PayUpdateProductProcessStepItem> list = payUpdateProductProcessStepItemMapper.selectList(new QueryWrapper<PayUpdateProductProcessStepItem>()
                .lambda().in(PayUpdateProductProcessStepItem::getUpdateStepItemSid, updateStepItemSids));
        int row = payUpdateProductProcessStepItemMapper.deleteBatchIds(updateStepItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PayUpdateProductProcessStepItem());
                MongodbUtil.insertUserLog(o.getUpdateStepItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 查询商品道序变更-明细
     *
     * @param updateProductProcessStepSid 商品道序变更-主表ID
     * @return 商品道序变更-明细
     */
    @Override
    public List<PayUpdateProductProcessStepItem> selectPayUpdateProductProcessStepItemListById(Long updateProductProcessStepSid) {
        List<PayUpdateProductProcessStepItem> updateProductProcessStepItemList = payUpdateProductProcessStepItemMapper
                .selectPayUpdateProductProcessStepItemList(new PayUpdateProductProcessStepItem()
                .setUpdateProductProcessStepSid(updateProductProcessStepSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(updateProductProcessStepItemList)) {
            updateProductProcessStepItemList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return updateProductProcessStepItemList;
    }

    /**
     * 查询商品道序变更-明细 标志位为删除的明细
     *
     * @param updateProductProcessStepSid 商品道序变更-主表ID
     * @return 商品道序变更-明细
     */
    @Override
    public List<PayUpdateProductProcessStepItem> selectDeleteListById(Long updateProductProcessStepSid) {
        List<PayUpdateProductProcessStepItem> deleteList = payUpdateProductProcessStepItemMapper
                .selectDeleteListById(new PayUpdateProductProcessStepItem()
                        .setUpdateProductProcessStepSid(updateProductProcessStepSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(deleteList)) {
            deleteList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return deleteList;
    }

    /**
     * 商品道序-明细对象
     */
    private void addPayUpdateProductProcessStepItem(PayUpdateProductProcessStep payUpdateProductProcessStep, List<PayUpdateProductProcessStepItem> payUpdateProductProcessStepItemList) {
        payUpdateProductProcessStepItemList.forEach(o -> {
            o.setUpdateProductProcessStepSid(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
            o.setDelFlagBiangz("");
//            if (StringUtils.isNull(o.getStepItemSid())){
                o.setCreateDate(new Date());
                o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
//            }
            o.setUpdateDate(null);
            o.setUpdaterAccount("");
        });
        payUpdateProductProcessStepItemMapper.inserts(payUpdateProductProcessStepItemList);
    }

    /**
     * 批量新增商品道序变更-明细
     *
     * @param step 商品道序变更
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayUpdateProductProcessStepItemList(PayUpdateProductProcessStep step) {
        int row = 0;
        List<PayUpdateProductProcessStepItem> list = step.getUpdateItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            PayUpdateProductProcessStepItem item = null;
            //记录全部新增
            if (CollectionUtil.isNotEmpty(list)) {
                addPayUpdateProductProcessStepItem(step, list);
            }

            //增加删除行
            List<PayProductProcessStepItem> payStepItemList = payProductProcessStepItemMapper.selectPayProductProcessStepItem(
                    new PayProductProcessStepItem().setProductProcessStepSid(step.getProductProcessStepSid()));
            //所有原序列
            List<Long> updateSidList = list.stream().map(PayUpdateProductProcessStepItem::getStepItemSid).collect(Collectors.toList());
            // 删除行
            List<PayProductProcessStepItem> delList = payStepItemList.stream().filter(o -> !updateSidList.contains(o.getStepItemSid())).collect(Collectors.toList());
            PayProductProcessStepItem itemProduct = null;
            for (int i = 0; i < delList.size(); i++) {
                itemProduct = delList.get(i);
                // 写入主表的 sid
                item = new PayUpdateProductProcessStepItem();
                BeanUtils.copyProperties(itemProduct, item);

                item.setUpdateProductProcessStepSid(step.getUpdateProductProcessStepSid());
                item.setDelFlagBiangz("Y");
                row += insertPayUpdateProductProcessStepItem(item);
            }

        }
        return row;
    }

    /**
     * 批量修改商品道序变更-明细
     *
     * @param step 商品道序变更
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayUpdateProductProcessStepItemList(PayUpdateProductProcessStep step) {
        int row = 0;
        List<PayUpdateProductProcessStepItem> list = step.getUpdateItemList();
        // 原本明细
        List<PayUpdateProductProcessStepItem> oldList = payUpdateProductProcessStepItemMapper.selectList(new QueryWrapper<PayUpdateProductProcessStepItem>()
                .lambda().eq(PayUpdateProductProcessStepItem::getUpdateProductProcessStepSid, step.getUpdateProductProcessStepSid()));
        if (CollectionUtil.isNotEmpty(list)) {

            List<PayUpdateProductProcessStepItem> newList = list.stream().filter(o -> o.getUpdateStepItemSid() == null).collect(Collectors.toList());
            List<PayUpdateProductProcessStepItem> updateList = list.stream().filter(o -> o.getUpdateStepItemSid() != null).collect(Collectors.toList());
            // 新增行
            if (CollectionUtil.isNotEmpty(newList)) {
                addPayUpdateProductProcessStepItem(step, newList);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            if (CollectionUtil.isNotEmpty(updateList)) {
                if (CollectionUtil.isNotEmpty(updateList)) {
                    Map<Long, PayUpdateProductProcessStepItem> map = oldList.stream().collect(Collectors.toMap(PayUpdateProductProcessStepItem::getUpdateStepItemSid, Function.identity()));

                    updateList.forEach(o -> {
                        o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                        payUpdateProductProcessStepItemMapper.updateAllById(o);
                        //插入日志
                        MongodbUtil.updateItemUserLog(o.getUpdateStepItemSid(), step.getHandleStatus(), map.get(o.getUpdateStepItemSid()), o, TITLE);
                    });
                }
            }

            //删除行
            if (CollectionUtil.isNotEmpty(oldList)) {
                // 删除行
//                List<Long> delSidList = list.stream().map(PayUpdateProductProcessStepItem::getStepItemSid).collect(Collectors.toList());

                List<Long> delSidList = list.stream().filter(o -> o.getUpdateProductProcessStepSid() != null)
                        .map(PayUpdateProductProcessStepItem::getUpdateStepItemSid).collect(Collectors.toList());

                List<PayUpdateProductProcessStepItem> delList = oldList.stream().filter(o -> !delSidList.contains(o.getUpdateStepItemSid())).collect(Collectors.toList());
                delList.forEach(item->{
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    if (!"Y".equals(item.getDelFlagBiangz())){
                        if (StringUtils.isNotNull(item.getStepItemSid())) {        //非空的时候，更新标记
                            item.setDelFlagBiangz("Y");
                            payUpdateProductProcessStepItemMapper.updateAllById(item); // 全量更新
                        }else{  //直接删除
                            payUpdateProductProcessStepItemMapper.deleteById(item.getUpdateStepItemSid());
                        }
                    }
                });
            }
        }
        return row;
    }

    /**
     * 批量删除商品道序变更-明细
     *
     * @param itemList 需要删除的商品道序变更-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayUpdateProductProcessStepItemByList(List<PayUpdateProductProcessStepItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> updateStepItemSidList = itemList.stream().filter(o -> o.getUpdateStepItemSid() != null)
                .map(PayUpdateProductProcessStepItem::getUpdateStepItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(updateStepItemSidList)) {
            row = payUpdateProductProcessStepItemMapper.deleteBatchIds(updateStepItemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new SalSalesIntentOrderItem());
                    MongodbUtil.insertUserLog(o.getUpdateStepItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除商品道序变更-明细 根据主表sids
     *
     * @param updateProductProcessStepSidList 需要删除的商品道序变更sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayUpdateProductProcessStepItemByStep(List<Long> updateProductProcessStepSidList) {
        List<PayUpdateProductProcessStepItem> itemList = payUpdateProductProcessStepItemMapper.selectList(new QueryWrapper<PayUpdateProductProcessStepItem>()
                .lambda().in(PayUpdateProductProcessStepItem::getUpdateProductProcessStepSid, updateProductProcessStepSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deletePayUpdateProductProcessStepItemByList(itemList);
        }
        return row;
    }

}
