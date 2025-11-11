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
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemYushou;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinReceivableBillItemYushouMapper;
import com.platform.ems.service.IFinReceivableBillItemYushouService;

/**
 * 收款单-核销预收明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillItemYushouServiceImpl extends ServiceImpl<FinReceivableBillItemYushouMapper, FinReceivableBillItemYushou> implements IFinReceivableBillItemYushouService {
    @Autowired
    private FinReceivableBillItemYushouMapper finReceivableBillItemYushouMapper;

    private static final String TITLE = "收款单-核销预收明细表";

    /**
     * 查询收款单-核销预收明细表
     *
     * @param receivableBillItemYushouSid 收款单-核销预收明细表ID
     * @return 收款单-核销预收明细表
     */
    @Override
    public FinReceivableBillItemYushou selectFinReceivableBillItemYushouById(Long receivableBillItemYushouSid) {
        FinReceivableBillItemYushou finReceivableBillItemYushou = finReceivableBillItemYushouMapper.selectFinReceivableBillItemYushouById(receivableBillItemYushouSid);
        MongodbUtil.find(finReceivableBillItemYushou);
        return finReceivableBillItemYushou;
    }

    /**
     * 查询收款单-核销预收明细表列表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 收款单-核销预收明细表
     */
    @Override
    public List<FinReceivableBillItemYushou> selectFinReceivableBillItemYushouList(FinReceivableBillItemYushou finReceivableBillItemYushou) {
        return finReceivableBillItemYushouMapper.selectFinReceivableBillItemYushouList(finReceivableBillItemYushou);
    }

    /**
     * 新增收款单-核销预收明细表
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillItemYushou(FinReceivableBillItemYushou finReceivableBillItemYushou) {
        int row = finReceivableBillItemYushouMapper.insert(finReceivableBillItemYushou);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBillItemYushou(), finReceivableBillItemYushou);
            MongodbUtil.insertUserLog(finReceivableBillItemYushou.getReceivableBillItemYushouSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改收款单-核销预收明细表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillItemYushou(FinReceivableBillItemYushou finReceivableBillItemYushou) {
        FinReceivableBillItemYushou original = finReceivableBillItemYushouMapper.selectFinReceivableBillItemYushouById(finReceivableBillItemYushou.getReceivableBillItemYushouSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBillItemYushou);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemYushou.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemYushouMapper.updateAllById(finReceivableBillItemYushou);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemYushou.getReceivableBillItemYushouSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收款单-核销预收明细表
     *
     * @param finReceivableBillItemYushou 收款单-核销预收明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillItemYushou(FinReceivableBillItemYushou finReceivableBillItemYushou) {
        FinReceivableBillItemYushou response = finReceivableBillItemYushouMapper.selectFinReceivableBillItemYushouById(finReceivableBillItemYushou.getReceivableBillItemYushouSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBillItemYushou);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemYushou.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemYushouMapper.updateAllById(finReceivableBillItemYushou);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemYushou.getReceivableBillItemYushouSid(), BusinessType.CHANGE.getValue(), response, finReceivableBillItemYushou, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-核销预收明细表
     *
     * @param receivableBillItemYushouSids 需要删除的收款单-核销预收明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillItemYushouByIds(List<Long> receivableBillItemYushouSids) {
        List<FinReceivableBillItemYushou> list = finReceivableBillItemYushouMapper.selectList(new QueryWrapper<FinReceivableBillItemYushou>()
                .lambda().in(FinReceivableBillItemYushou::getReceivableBillItemYushouSid, receivableBillItemYushouSids));
        int row = finReceivableBillItemYushouMapper.deleteBatchIds(receivableBillItemYushouSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBillItemYushou());
                MongodbUtil.insertUserLog(o.getReceivableBillItemYushouSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinReceivableBillItemYushou> list = bill.getYushouList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinReceivableBillItemYushou item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setReceivableBillSid(bill.getReceivableBillSid());
                row += insertFinReceivableBillItemYushou(item);
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
        List<FinReceivableBillItemYushou> list = bill.getYushouList();
        // 原本明细
        List<FinReceivableBillItemYushou> oldList = finReceivableBillItemYushouMapper.selectList(new QueryWrapper<FinReceivableBillItemYushou>()
                .lambda().eq(FinReceivableBillItemYushou::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinReceivableBillItemYushou> newList = list.stream().filter(o -> o.getReceivableBillItemYushouSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setYushouList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinReceivableBillItemYushou> updateList = list.stream().filter(o -> o.getReceivableBillItemYushouSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinReceivableBillItemYushou::getReceivableBillItemYushouSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinReceivableBillItemYushou> map = oldList.stream().collect(Collectors.toMap(FinReceivableBillItemYushou::getReceivableBillItemYushouSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getReceivableBillItemYushouSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getReceivableBillItemYushouSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finReceivableBillItemYushouMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getReceivableBillItemYushouSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinReceivableBillItemYushou> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getReceivableBillItemYushouSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinReceivableBillItemYushou> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getReceivableBillItemYushouSid() != null)
                .map(FinReceivableBillItemYushou::getReceivableBillItemYushouSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finReceivableBillItemYushouMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getReceivableBillItemYushouSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

}
