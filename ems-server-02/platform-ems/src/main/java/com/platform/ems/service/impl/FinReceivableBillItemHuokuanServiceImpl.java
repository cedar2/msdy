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
import com.platform.ems.domain.FinReceivableBillItemHuokuan;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinReceivableBillItemHuokuanMapper;
import com.platform.ems.service.IFinReceivableBillItemHuokuanService;

/**
 * 收款单-核销货款明细表Service业务层处理
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillItemHuokuanServiceImpl extends ServiceImpl<FinReceivableBillItemHuokuanMapper, FinReceivableBillItemHuokuan> implements IFinReceivableBillItemHuokuanService {
    @Autowired
    private FinReceivableBillItemHuokuanMapper finReceivableBillItemHuokuanMapper;

    private static final String TITLE = "收款单-核销货款明细表";

    /**
     * 查询收款单-核销货款明细表
     *
     * @param receivableBillItemHuokuanSid 收款单-核销货款明细表ID
     * @return 收款单-核销货款明细表
     */
    @Override
    public FinReceivableBillItemHuokuan selectFinReceivableBillItemHuokuanById(Long receivableBillItemHuokuanSid) {
        FinReceivableBillItemHuokuan finReceivableBillItemHuokuan = finReceivableBillItemHuokuanMapper.selectFinReceivableBillItemHuokuanById(receivableBillItemHuokuanSid);
        MongodbUtil.find(finReceivableBillItemHuokuan);
        return finReceivableBillItemHuokuan;
    }

    /**
     * 查询收款单-核销货款明细表列表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 收款单-核销货款明细表
     */
    @Override
    public List<FinReceivableBillItemHuokuan> selectFinReceivableBillItemHuokuanList(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan) {
        return finReceivableBillItemHuokuanMapper.selectFinReceivableBillItemHuokuanList(finReceivableBillItemHuokuan);
    }

    /**
     * 新增收款单-核销货款明细表
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillItemHuokuan(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan) {
        int row = finReceivableBillItemHuokuanMapper.insert(finReceivableBillItemHuokuan);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBillItemHuokuan(), finReceivableBillItemHuokuan);
            MongodbUtil.insertUserLog(finReceivableBillItemHuokuan.getReceivableBillItemHuokuanSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改收款单-核销货款明细表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillItemHuokuan(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan) {
        FinReceivableBillItemHuokuan original = finReceivableBillItemHuokuanMapper.selectFinReceivableBillItemHuokuanById(finReceivableBillItemHuokuan.getReceivableBillItemHuokuanSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBillItemHuokuan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemHuokuan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemHuokuanMapper.updateAllById(finReceivableBillItemHuokuan);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemHuokuan.getReceivableBillItemHuokuanSid(), BusinessType.SAVE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收款单-核销货款明细表
     *
     * @param finReceivableBillItemHuokuan 收款单-核销货款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillItemHuokuan(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan) {
        FinReceivableBillItemHuokuan response = finReceivableBillItemHuokuanMapper.selectFinReceivableBillItemHuokuanById(finReceivableBillItemHuokuan.getReceivableBillItemHuokuanSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBillItemHuokuan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemHuokuan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemHuokuanMapper.updateAllById(finReceivableBillItemHuokuan);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemHuokuan.getReceivableBillItemHuokuanSid(), BusinessType.CHANGE.getValue(), response, finReceivableBillItemHuokuan, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-核销货款明细表
     *
     * @param receivableBillItemHuokuanSids 需要删除的收款单-核销货款明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillItemHuokuanByIds(List<Long> receivableBillItemHuokuanSids) {
        List<FinReceivableBillItemHuokuan> list = finReceivableBillItemHuokuanMapper.selectList(new QueryWrapper<FinReceivableBillItemHuokuan>()
                .lambda().in(FinReceivableBillItemHuokuan::getReceivableBillItemHuokuanSid, receivableBillItemHuokuanSids));
        int row = finReceivableBillItemHuokuanMapper.deleteBatchIds(receivableBillItemHuokuanSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBillItemHuokuan());
                MongodbUtil.insertUserLog(o.getReceivableBillItemHuokuanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinReceivableBillItemHuokuan> list = bill.getHuokuanList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinReceivableBillItemHuokuan item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setReceivableBillSid(bill.getReceivableBillSid());
                row += insertFinReceivableBillItemHuokuan(item);
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
        List<FinReceivableBillItemHuokuan> list = bill.getHuokuanList();
        // 原本明细
        List<FinReceivableBillItemHuokuan> oldList = finReceivableBillItemHuokuanMapper.selectList(new QueryWrapper<FinReceivableBillItemHuokuan>()
                .lambda().eq(FinReceivableBillItemHuokuan::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinReceivableBillItemHuokuan> newList = list.stream().filter(o -> o.getReceivableBillItemHuokuanSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setHuokuanList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinReceivableBillItemHuokuan> updateList = list.stream().filter(o -> o.getReceivableBillItemHuokuanSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinReceivableBillItemHuokuan::getReceivableBillItemHuokuanSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinReceivableBillItemHuokuan> map = oldList.stream().collect(Collectors.toMap(FinReceivableBillItemHuokuan::getReceivableBillItemHuokuanSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getReceivableBillItemHuokuanSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getReceivableBillItemHuokuanSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finReceivableBillItemHuokuanMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getReceivableBillItemHuokuanSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinReceivableBillItemHuokuan> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getReceivableBillItemHuokuanSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinReceivableBillItemHuokuan> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getReceivableBillItemHuokuanSid() != null)
                .map(FinReceivableBillItemHuokuan::getReceivableBillItemHuokuanSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finReceivableBillItemHuokuanMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getReceivableBillItemHuokuanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

}
