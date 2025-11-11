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
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.DevCategoryPlanItem;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemOutsourceSettle;
import com.platform.ems.mapper.FinPayBillItemOutsourceSettleMapper;
import com.platform.ems.service.IFinPayBillItemOutsourceSettleService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 付款单-外发加工费结算单明细Service业务层处理
 *
 * @author platform
 * @date 2024-05-22
 */
@Service
@SuppressWarnings("all")
public class FinPayBillItemOutsourceSettleServiceImpl extends ServiceImpl<FinPayBillItemOutsourceSettleMapper, FinPayBillItemOutsourceSettle>
        implements IFinPayBillItemOutsourceSettleService {
    @Autowired
    private FinPayBillItemOutsourceSettleMapper outsourceSettleMapper;

    private static final String TITLE = "付款单-外发加工费结算单明细";

    /**
     * 查询付款单-外发加工费结算单明细
     *
     * @param payBillItemOutsourceSettleSid 付款单-外发加工费结算单明细ID
     * @return 付款单-外发加工费结算单明细
     */
    @Override
    public FinPayBillItemOutsourceSettle selectFinPayBillItemOutsourceSettleById(Long payBillItemOutsourceSettleSid) {
        FinPayBillItemOutsourceSettle outsourceSettle = outsourceSettleMapper.selectFinPayBillItemOutsourceSettleById(payBillItemOutsourceSettleSid);
        MongodbUtil.find(outsourceSettle);
        return outsourceSettle;
    }

    /**
     * 查询付款单-外发加工费结算单明细列表
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 付款单-外发加工费结算单明细
     */
    @Override
    public List<FinPayBillItemOutsourceSettle> selectFinPayBillItemOutsourceSettleList(FinPayBillItemOutsourceSettle outsourceSettle) {
        return outsourceSettleMapper.selectFinPayBillItemOutsourceSettleList(outsourceSettle);
    }

    /**
     * 新增付款单-外发加工费结算单明细
     * 需要注意编码重复校验
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBillItemOutsourceSettle(FinPayBillItemOutsourceSettle outsourceSettle) {
        int row = outsourceSettleMapper.insert(outsourceSettle);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinPayBillItemOutsourceSettle(), outsourceSettle);
            MongodbUtil.insertUserLog(outsourceSettle.getPayBillItemOutsourceSettleSid(),
                    BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改付款单-外发加工费结算单明细
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBillItemOutsourceSettle(FinPayBillItemOutsourceSettle outsourceSettle) {
        FinPayBillItemOutsourceSettle original = outsourceSettleMapper
                .selectFinPayBillItemOutsourceSettleById(outsourceSettle.getPayBillItemOutsourceSettleSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, outsourceSettle);
        if (CollectionUtil.isNotEmpty(msgList)) {
            outsourceSettle.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = outsourceSettleMapper.updateAllById(outsourceSettle);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(outsourceSettle.getPayBillItemOutsourceSettleSid(),
                    BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更付款单-外发加工费结算单明细
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBillItemOutsourceSettle(FinPayBillItemOutsourceSettle outsourceSettle) {
        FinPayBillItemOutsourceSettle response = outsourceSettleMapper.
                selectFinPayBillItemOutsourceSettleById(outsourceSettle.getPayBillItemOutsourceSettleSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, outsourceSettle);
        if (CollectionUtil.isNotEmpty(msgList)) {
            outsourceSettle.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = outsourceSettleMapper.updateAllById(outsourceSettle);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(outsourceSettle.getPayBillItemOutsourceSettleSid(),
                    BusinessType.CHANGE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除付款单-外发加工费结算单明细
     *
     * @param payBillItemOutsourceSettleSids 需要删除的付款单-外发加工费结算单明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillItemOutsourceSettleByIds(List<Long> outsourceSettleSids) {
        List<FinPayBillItemOutsourceSettle> list = outsourceSettleMapper.selectList(new QueryWrapper<FinPayBillItemOutsourceSettle>()
                .lambda().in(FinPayBillItemOutsourceSettle::getPayBillItemOutsourceSettleSid, outsourceSettleSids));
        int row = outsourceSettleMapper.deleteBatchIds(outsourceSettleSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinPayBillItemOutsourceSettle());
                MongodbUtil.insertUserLog(o.getPayBillItemOutsourceSettleSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinPayBillItemOutsourceSettle> list = bill.getOutsourceSettleList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinPayBillItemOutsourceSettle item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setPayBillSid(bill.getPayBillSid());
                item.setPayBillCode(bill.getPayBillCode());
                row += insertFinPayBillItemOutsourceSettle(item);
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
        List<FinPayBillItemOutsourceSettle> list = bill.getOutsourceSettleList();
        // 原本明细
        List<FinPayBillItemOutsourceSettle> oldList = outsourceSettleMapper.selectList(new QueryWrapper<FinPayBillItemOutsourceSettle>()
                .lambda().eq(FinPayBillItemOutsourceSettle::getPayBillSid, bill.getPayBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinPayBillItemOutsourceSettle> newList = list.stream().filter(o -> o.getPayBillItemOutsourceSettleSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setOutsourceSettleList(newList);
                row = row + insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinPayBillItemOutsourceSettle> updateList = list.stream().filter(o -> o.getPayBillItemOutsourceSettleSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinPayBillItemOutsourceSettle::getPayBillItemOutsourceSettleSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinPayBillItemOutsourceSettle> map = oldList.stream().collect(Collectors.toMap(FinPayBillItemOutsourceSettle::getPayBillItemOutsourceSettleSid, Function.identity()));
                    for (FinPayBillItemOutsourceSettle item : updateList) {
                        if (map.containsKey(item.getPayBillItemOutsourceSettleSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getPayBillItemOutsourceSettleSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            row = row + outsourceSettleMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getPayBillItemOutsourceSettleSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    }
                    // 删除行
                    List<FinPayBillItemOutsourceSettle> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getPayBillItemOutsourceSettleSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinPayBillItemOutsourceSettle> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getPayBillItemOutsourceSettleSid() != null)
                .map(FinPayBillItemOutsourceSettle::getPayBillItemOutsourceSettleSid).collect(Collectors.toList());

        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = outsourceSettleMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getPayBillItemOutsourceSettleSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
