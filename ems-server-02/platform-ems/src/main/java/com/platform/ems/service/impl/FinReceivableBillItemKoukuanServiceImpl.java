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
import com.platform.ems.domain.FinReceivableBillItemKoukuan;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinReceivableBillItemKoukuanMapper;
import com.platform.ems.service.IFinReceivableBillItemKoukuanService;

/**
 * 收款单-核销扣款明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillItemKoukuanServiceImpl extends ServiceImpl<FinReceivableBillItemKoukuanMapper, FinReceivableBillItemKoukuan> implements IFinReceivableBillItemKoukuanService {
    @Autowired
    private FinReceivableBillItemKoukuanMapper finReceivableBillItemKoukuanMapper;

    private static final String TITLE = "收款单-核销扣款明细表";

    /**
     * 查询收款单-核销扣款明细表
     *
     * @param receivableBillItemKoukuanSid 收款单-核销扣款明细表ID
     * @return 收款单-核销扣款明细表
     */
    @Override
    public FinReceivableBillItemKoukuan selectFinReceivableBillItemKoukuanById(Long receivableBillItemKoukuanSid) {
        FinReceivableBillItemKoukuan finReceivableBillItemKoukuan = finReceivableBillItemKoukuanMapper.selectFinReceivableBillItemKoukuanById(receivableBillItemKoukuanSid);
        MongodbUtil.find(finReceivableBillItemKoukuan);
        return finReceivableBillItemKoukuan;
    }

    /**
     * 查询收款单-核销扣款明细表列表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 收款单-核销扣款明细表
     */
    @Override
    public List<FinReceivableBillItemKoukuan> selectFinReceivableBillItemKoukuanList(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan) {
        return finReceivableBillItemKoukuanMapper.selectFinReceivableBillItemKoukuanList(finReceivableBillItemKoukuan);
    }

    /**
     * 新增收款单-核销扣款明细表
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillItemKoukuan(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan) {
        int row = finReceivableBillItemKoukuanMapper.insert(finReceivableBillItemKoukuan);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBillItemKoukuan(), finReceivableBillItemKoukuan);
            MongodbUtil.insertUserLog(finReceivableBillItemKoukuan.getReceivableBillItemKoukuanSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改收款单-核销扣款明细表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillItemKoukuan(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan) {
        FinReceivableBillItemKoukuan original = finReceivableBillItemKoukuanMapper.selectFinReceivableBillItemKoukuanById(finReceivableBillItemKoukuan.getReceivableBillItemKoukuanSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBillItemKoukuan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemKoukuan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemKoukuanMapper.updateAllById(finReceivableBillItemKoukuan);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemKoukuan.getReceivableBillItemKoukuanSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收款单-核销扣款明细表
     *
     * @param finReceivableBillItemKoukuan 收款单-核销扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillItemKoukuan(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan) {
        FinReceivableBillItemKoukuan response = finReceivableBillItemKoukuanMapper.selectFinReceivableBillItemKoukuanById(finReceivableBillItemKoukuan.getReceivableBillItemKoukuanSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBillItemKoukuan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemKoukuan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemKoukuanMapper.updateAllById(finReceivableBillItemKoukuan);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemKoukuan.getReceivableBillItemKoukuanSid(), BusinessType.CHANGE.getValue(), response, finReceivableBillItemKoukuan, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-核销扣款明细表
     *
     * @param receivableBillItemKoukuanSids 需要删除的收款单-核销扣款明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillItemKoukuanByIds(List<Long> receivableBillItemKoukuanSids) {
        List<FinReceivableBillItemKoukuan> list = finReceivableBillItemKoukuanMapper.selectList(new QueryWrapper<FinReceivableBillItemKoukuan>()
                .lambda().in(FinReceivableBillItemKoukuan::getReceivableBillItemKoukuanSid, receivableBillItemKoukuanSids));
        int row = finReceivableBillItemKoukuanMapper.deleteBatchIds(receivableBillItemKoukuanSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBillItemKoukuan());
                MongodbUtil.insertUserLog(o.getReceivableBillItemKoukuanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinReceivableBillItemKoukuan> list = bill.getKoukuanList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinReceivableBillItemKoukuan item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setReceivableBillSid(bill.getReceivableBillSid());
                row += insertFinReceivableBillItemKoukuan(item);
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
        List<FinReceivableBillItemKoukuan> list = bill.getKoukuanList();
        // 原本明细
        List<FinReceivableBillItemKoukuan> oldList = finReceivableBillItemKoukuanMapper.selectList(new QueryWrapper<FinReceivableBillItemKoukuan>()
                .lambda().eq(FinReceivableBillItemKoukuan::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinReceivableBillItemKoukuan> newList = list.stream().filter(o -> o.getReceivableBillItemKoukuanSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setKoukuanList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinReceivableBillItemKoukuan> updateList = list.stream().filter(o -> o.getReceivableBillItemKoukuanSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinReceivableBillItemKoukuan::getReceivableBillItemKoukuanSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinReceivableBillItemKoukuan> map = oldList.stream().collect(Collectors.toMap(FinReceivableBillItemKoukuan::getReceivableBillItemKoukuanSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getReceivableBillItemKoukuanSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getReceivableBillItemKoukuanSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finReceivableBillItemKoukuanMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getReceivableBillItemKoukuanSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinReceivableBillItemKoukuan> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getReceivableBillItemKoukuanSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinReceivableBillItemKoukuan> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getReceivableBillItemKoukuanSid() != null)
                .map(FinReceivableBillItemKoukuan::getReceivableBillItemKoukuanSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finReceivableBillItemKoukuanMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getReceivableBillItemKoukuanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
