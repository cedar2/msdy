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
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemYufu;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinPayBillItemYufuMapper;
import com.platform.ems.service.IFinPayBillItemYufuService;

/**
 * 付款单-核销预付明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinPayBillItemYufuServiceImpl extends ServiceImpl<FinPayBillItemYufuMapper, FinPayBillItemYufu> implements IFinPayBillItemYufuService {
    @Autowired
    private FinPayBillItemYufuMapper finPayBillItemYufuMapper;

    private static final String TITLE = "付款单-核销预付明细表";

    /**
     * 查询付款单-核销预付明细表
     *
     * @param payBillItemYufuSid 付款单-核销预付明细表ID
     * @return 付款单-核销预付明细表
     */
    @Override
    public FinPayBillItemYufu selectFinPayBillItemYufuById(Long payBillItemYufuSid) {
        FinPayBillItemYufu finPayBillItemYufu = finPayBillItemYufuMapper.selectFinPayBillItemYufuById(payBillItemYufuSid);
        MongodbUtil.find(finPayBillItemYufu);
        return finPayBillItemYufu;
    }

    /**
     * 查询付款单-核销预付明细表列表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 付款单-核销预付明细表
     */
    @Override
    public List<FinPayBillItemYufu> selectFinPayBillItemYufuList(FinPayBillItemYufu finPayBillItemYufu) {
        return finPayBillItemYufuMapper.selectFinPayBillItemYufuList(finPayBillItemYufu);
    }

    /**
     * 新增付款单-核销预付明细表
     * 需要注意编码重复校验
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBillItemYufu(FinPayBillItemYufu finPayBillItemYufu) {
        int row = finPayBillItemYufuMapper.insert(finPayBillItemYufu);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinPayBillItemYufu(), finPayBillItemYufu);
            MongodbUtil.insertUserLog(finPayBillItemYufu.getPayBillItemYufuSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改付款单-核销预付明细表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBillItemYufu(FinPayBillItemYufu finPayBillItemYufu) {
        FinPayBillItemYufu original = finPayBillItemYufuMapper.selectFinPayBillItemYufuById(finPayBillItemYufu.getPayBillItemYufuSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finPayBillItemYufu);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItemYufu.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemYufuMapper.updateAllById(finPayBillItemYufu);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItemYufu.getPayBillItemYufuSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更付款单-核销预付明细表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBillItemYufu(FinPayBillItemYufu finPayBillItemYufu) {
        FinPayBillItemYufu response = finPayBillItemYufuMapper.selectFinPayBillItemYufuById(finPayBillItemYufu.getPayBillItemYufuSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finPayBillItemYufu);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItemYufu.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemYufuMapper.updateAllById(finPayBillItemYufu);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItemYufu.getPayBillItemYufuSid(), BusinessType.CHANGE.getValue(), response, finPayBillItemYufu, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款单-核销预付明细表
     *
     * @param payBillItemYufuSids 需要删除的付款单-核销预付明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillItemYufuByIds(List<Long> payBillItemYufuSids) {
        List<FinPayBillItemYufu> list = finPayBillItemYufuMapper.selectList(new QueryWrapper<FinPayBillItemYufu>()
                .lambda().in(FinPayBillItemYufu::getPayBillItemYufuSid, payBillItemYufuSids));
        int row = finPayBillItemYufuMapper.deleteBatchIds(payBillItemYufuSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinPayBillItemYufu());
                MongodbUtil.insertUserLog(o.getPayBillItemYufuSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinPayBillItemYufu> list = bill.getYufuList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinPayBillItemYufu item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setPayBillSid(bill.getPayBillSid());
                row += insertFinPayBillItemYufu(item);
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
        List<FinPayBillItemYufu> list = bill.getYufuList();
        // 原本明细
        List<FinPayBillItemYufu> oldList = finPayBillItemYufuMapper.selectList(new QueryWrapper<FinPayBillItemYufu>()
                .lambda().eq(FinPayBillItemYufu::getPayBillSid, bill.getPayBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinPayBillItemYufu> newList = list.stream().filter(o -> o.getPayBillItemYufuSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setYufuList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinPayBillItemYufu> updateList = list.stream().filter(o -> o.getPayBillItemYufuSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinPayBillItemYufu::getPayBillItemYufuSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinPayBillItemYufu> map = oldList.stream().collect(Collectors.toMap(FinPayBillItemYufu::getPayBillItemYufuSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getPayBillItemYufuSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getPayBillItemYufuSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finPayBillItemYufuMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getPayBillItemYufuSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinPayBillItemYufu> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getPayBillItemYufuSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinPayBillItemYufu> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getPayBillItemYufuSid() != null)
                .map(FinPayBillItemYufu::getPayBillItemYufuSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finPayBillItemYufuMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getPayBillItemYufuSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
