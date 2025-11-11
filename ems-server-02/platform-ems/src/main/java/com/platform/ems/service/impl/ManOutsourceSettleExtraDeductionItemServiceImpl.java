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
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManManufactureOutsourceSettle;
import com.platform.ems.domain.PrjTaskTemplateItem;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManOutsourceSettleExtraDeductionItemMapper;
import com.platform.ems.domain.ManOutsourceSettleExtraDeductionItem;
import com.platform.ems.service.IManOutsourceSettleExtraDeductionItemService;

/**
 * 外发加工费结算单-额外扣款明细Service业务层处理
 *
 * @author admin
 * @date 2023-08-10
 */
@Service
@SuppressWarnings("all")
public class ManOutsourceSettleExtraDeductionItemServiceImpl extends ServiceImpl<ManOutsourceSettleExtraDeductionItemMapper, ManOutsourceSettleExtraDeductionItem> implements IManOutsourceSettleExtraDeductionItemService {
    @Autowired
    private ManOutsourceSettleExtraDeductionItemMapper manOutsourceSettleExtraDeductionItemMapper;

    private static final String TITLE = "外发加工费结算单-额外扣款明细";

    /**
     * 查询外发加工费结算单-额外扣款明细
     *
     * @param outsourceSettleExtraDeductionItemSid 外发加工费结算单-额外扣款明细ID
     * @return 外发加工费结算单-额外扣款明细
     */
    @Override
    public ManOutsourceSettleExtraDeductionItem selectManOutsourceSettleExtraDeductionItemById(Long outsourceSettleExtraDeductionItemSid) {
        ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem = manOutsourceSettleExtraDeductionItemMapper.selectManOutsourceSettleExtraDeductionItemById(outsourceSettleExtraDeductionItemSid);
        MongodbUtil.find(manOutsourceSettleExtraDeductionItem);
        return manOutsourceSettleExtraDeductionItem;
    }

    /**
     * 查询外发加工费结算单-额外扣款明细列表
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 外发加工费结算单-额外扣款明细
     */
    @Override
    public List<ManOutsourceSettleExtraDeductionItem> selectManOutsourceSettleExtraDeductionItemList(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem) {
        return manOutsourceSettleExtraDeductionItemMapper.selectManOutsourceSettleExtraDeductionItemList(manOutsourceSettleExtraDeductionItem);
    }

    /**
     * 查询外发加工费结算单-额外扣款明细列表 根据主表
     *
     * @param manOutsourceSettleSid 外发加工费结算单-Sid
     * @return 外发加工费结算单-额外扣款明细集合
     */
    @Override
    public List<ManOutsourceSettleExtraDeductionItem> selectManOutsourceSettleExtraDeductionItemList(Long manOutsourceSettleSid) {
        List<ManOutsourceSettleExtraDeductionItem> extraDeductionItemList = manOutsourceSettleExtraDeductionItemMapper
                .selectManOutsourceSettleExtraDeductionItemList(new ManOutsourceSettleExtraDeductionItem()
                        .setManufactureOutsourceSettleSid(manOutsourceSettleSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(extraDeductionItemList)) {
            extraDeductionItemList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return extraDeductionItemList;
    }

    /**
     * 新增外发加工费结算单-额外扣款明细
     * 需要注意编码重复校验
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManOutsourceSettleExtraDeductionItem(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem) {
        int row = manOutsourceSettleExtraDeductionItemMapper.insert(manOutsourceSettleExtraDeductionItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManOutsourceSettleExtraDeductionItem(), manOutsourceSettleExtraDeductionItem);
            MongodbDeal.insert(manOutsourceSettleExtraDeductionItem.getOutsourceSettleExtraDeductionItemSid(), ConstantsEms.SAVA_STATUS, msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改外发加工费结算单-额外扣款明细 根据主表
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManOutsourceSettleExtraDeductionItemListBy(ManManufactureOutsourceSettle settle) {
        int row = 0;
        List<ManOutsourceSettleExtraDeductionItem> list = settle.getExtraDeductionItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            ManOutsourceSettleExtraDeductionItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setManufactureOutsourceSettleSid(settle.getManufactureOutsourceSettleSid());
                item.setManufactureOutsourceSettleCode(settle.getManufactureOutsourceSettleCode());
                row += insertManOutsourceSettleExtraDeductionItem(item);
            }
        }
        return row;
    }

    /**
     * 修改外发加工费结算单-额外扣款明细
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManOutsourceSettleExtraDeductionItem(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem) {
        ManOutsourceSettleExtraDeductionItem original = manOutsourceSettleExtraDeductionItemMapper.selectManOutsourceSettleExtraDeductionItemById(manOutsourceSettleExtraDeductionItem.getOutsourceSettleExtraDeductionItemSid());
        int row = manOutsourceSettleExtraDeductionItemMapper.updateById(manOutsourceSettleExtraDeductionItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manOutsourceSettleExtraDeductionItem);
            MongodbUtil.insertUserLog(manOutsourceSettleExtraDeductionItem.getOutsourceSettleExtraDeductionItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改外发加工费结算单-额外扣款明细 根据主表
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManOutsourceSettleExtraDeductionItemListBy(ManManufactureOutsourceSettle settle) {
        int row = 0;
        List<ManOutsourceSettleExtraDeductionItem> list = settle.getExtraDeductionItemList();
        // 原本明细
        List<ManOutsourceSettleExtraDeductionItem> oldList = manOutsourceSettleExtraDeductionItemMapper.selectList(new QueryWrapper<ManOutsourceSettleExtraDeductionItem>()
                .lambda().eq(ManOutsourceSettleExtraDeductionItem::getManufactureOutsourceSettleSid, settle.getManufactureOutsourceSettleSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<ManOutsourceSettleExtraDeductionItem> newList = list.stream().filter(o -> o.getOutsourceSettleExtraDeductionItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                settle.setExtraDeductionItemList(newList);
                insertManOutsourceSettleExtraDeductionItemListBy(settle);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<ManOutsourceSettleExtraDeductionItem> updateList = list.stream().filter(o -> o.getOutsourceSettleExtraDeductionItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(ManOutsourceSettleExtraDeductionItem::getOutsourceSettleExtraDeductionItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, ManOutsourceSettleExtraDeductionItem> map = oldList.stream().collect(Collectors.toMap(ManOutsourceSettleExtraDeductionItem::getOutsourceSettleExtraDeductionItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getOutsourceSettleExtraDeductionItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getOutsourceSettleExtraDeductionItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            manOutsourceSettleExtraDeductionItemMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getOutsourceSettleExtraDeductionItemSid(), settle.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<ManOutsourceSettleExtraDeductionItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getOutsourceSettleExtraDeductionItemSid())).collect(Collectors.toList());
                    deleteListByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteListByList(oldList);
            }
        }
        return row;
    }

    /**
     * 变更外发加工费结算单-额外扣款明细
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManOutsourceSettleExtraDeductionItem(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem) {
        ManOutsourceSettleExtraDeductionItem response = manOutsourceSettleExtraDeductionItemMapper.selectManOutsourceSettleExtraDeductionItemById(manOutsourceSettleExtraDeductionItem.getOutsourceSettleExtraDeductionItemSid());
        int row = manOutsourceSettleExtraDeductionItemMapper.updateAllById(manOutsourceSettleExtraDeductionItem);
        if (row <= 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, manOutsourceSettleExtraDeductionItem);
            MongodbUtil.insertUserLog(manOutsourceSettleExtraDeductionItem.getOutsourceSettleExtraDeductionItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除外发加工费结算单-额外扣款明细
     *
     * @param outsourceSettleExtraDeductionItemSids 需要删除的外发加工费结算单-额外扣款明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManOutsourceSettleExtraDeductionItemByIds(List<Long> outsourceSettleExtraDeductionItemSids) {
        List<ManOutsourceSettleExtraDeductionItem> list = manOutsourceSettleExtraDeductionItemMapper.selectList(new QueryWrapper<ManOutsourceSettleExtraDeductionItem>()
                .lambda().in(ManOutsourceSettleExtraDeductionItem::getOutsourceSettleExtraDeductionItemSid, outsourceSettleExtraDeductionItemSids));
        int row = manOutsourceSettleExtraDeductionItemMapper.deleteBatchIds(outsourceSettleExtraDeductionItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManOutsourceSettleExtraDeductionItem());
                MongodbUtil.insertUserLog(o.getOutsourceSettleExtraDeductionItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量删除外发加工费结算单-额外扣款明细
     *
     * @param itemList 需要删除的额外扣款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteListByList(List<ManOutsourceSettleExtraDeductionItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> outsourceSettleExtraDeductionItemSidList = itemList.stream().filter(o -> o.getOutsourceSettleExtraDeductionItemSid() != null)
                .map(ManOutsourceSettleExtraDeductionItem::getOutsourceSettleExtraDeductionItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(outsourceSettleExtraDeductionItemSidList)) {
            row = manOutsourceSettleExtraDeductionItemMapper.deleteBatchIds(outsourceSettleExtraDeductionItemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new PrjTaskTemplateItem());
                    MongodbUtil.insertUserLog(o.getOutsourceSettleExtraDeductionItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除外发加工费结算单-额外扣款明细 根据主表sid
     *
     * @param manufactureOutsourceSettleSids 需要删除的外发加工费结算单SID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteListBySids(List<Long> manufactureOutsourceSettleSids) {
        List<ManOutsourceSettleExtraDeductionItem> itemList = manOutsourceSettleExtraDeductionItemMapper.selectList(new QueryWrapper<ManOutsourceSettleExtraDeductionItem>()
                .lambda().in(ManOutsourceSettleExtraDeductionItem::getManufactureOutsourceSettleSid, manufactureOutsourceSettleSids));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteListByList(itemList);
        }
        return row;
    }
}
