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
import com.platform.ems.domain.FinReceivableBillItemKoukuanTuihuo;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinReceivableBillItemKoukuanTuihuoMapper;
import com.platform.ems.service.IFinReceivableBillItemKoukuanTuihuoService;

/**
 * 收款单-核销退货扣款明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillItemKoukuanTuihuoServiceImpl extends ServiceImpl<FinReceivableBillItemKoukuanTuihuoMapper, FinReceivableBillItemKoukuanTuihuo> implements IFinReceivableBillItemKoukuanTuihuoService {
    @Autowired
    private FinReceivableBillItemKoukuanTuihuoMapper finReceivableBillItemKoukuanTuihuoMapper;

    private static final String TITLE = "收款单-核销退货扣款明细表";

    /**
     * 查询收款单-核销退货扣款明细表
     *
     * @param receivableBillItemKoukuanTuihuoSid 收款单-核销退货扣款明细表ID
     * @return 收款单-核销退货扣款明细表
     */
    @Override
    public FinReceivableBillItemKoukuanTuihuo selectFinReceivableBillItemKoukuanTuihuoById(Long receivableBillItemKoukuanTuihuoSid) {
        FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo = finReceivableBillItemKoukuanTuihuoMapper.selectFinReceivableBillItemKoukuanTuihuoById(receivableBillItemKoukuanTuihuoSid);
        MongodbUtil.find(finReceivableBillItemKoukuanTuihuo);
        return finReceivableBillItemKoukuanTuihuo;
    }

    /**
     * 查询收款单-核销退货扣款明细表列表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 收款单-核销退货扣款明细表
     */
    @Override
    public List<FinReceivableBillItemKoukuanTuihuo> selectFinReceivableBillItemKoukuanTuihuoList(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo) {
        return finReceivableBillItemKoukuanTuihuoMapper.selectFinReceivableBillItemKoukuanTuihuoList(finReceivableBillItemKoukuanTuihuo);
    }

    /**
     * 新增收款单-核销退货扣款明细表
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillItemKoukuanTuihuo(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo) {
        int row = finReceivableBillItemKoukuanTuihuoMapper.insert(finReceivableBillItemKoukuanTuihuo);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBillItemKoukuanTuihuo(), finReceivableBillItemKoukuanTuihuo);
            MongodbUtil.insertUserLog(finReceivableBillItemKoukuanTuihuo.getReceivableBillItemKoukuanTuihuoSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改收款单-核销退货扣款明细表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillItemKoukuanTuihuo(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo) {
        FinReceivableBillItemKoukuanTuihuo original = finReceivableBillItemKoukuanTuihuoMapper.selectFinReceivableBillItemKoukuanTuihuoById(finReceivableBillItemKoukuanTuihuo.getReceivableBillItemKoukuanTuihuoSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBillItemKoukuanTuihuo);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemKoukuanTuihuo.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemKoukuanTuihuoMapper.updateAllById(finReceivableBillItemKoukuanTuihuo);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemKoukuanTuihuo.getReceivableBillItemKoukuanTuihuoSid(), BusinessType.SAVE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收款单-核销退货扣款明细表
     *
     * @param finReceivableBillItemKoukuanTuihuo 收款单-核销退货扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillItemKoukuanTuihuo(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo) {
        FinReceivableBillItemKoukuanTuihuo response = finReceivableBillItemKoukuanTuihuoMapper.selectFinReceivableBillItemKoukuanTuihuoById(finReceivableBillItemKoukuanTuihuo.getReceivableBillItemKoukuanTuihuoSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBillItemKoukuanTuihuo);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBillItemKoukuanTuihuo.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillItemKoukuanTuihuoMapper.updateAllById(finReceivableBillItemKoukuanTuihuo);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillItemKoukuanTuihuo.getReceivableBillItemKoukuanTuihuoSid(), BusinessType.CHANGE.getValue(), response, finReceivableBillItemKoukuanTuihuo, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-核销退货扣款明细表
     *
     * @param receivableBillItemKoukuanTuihuoSids 需要删除的收款单-核销退货扣款明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillItemKoukuanTuihuoByIds(List<Long> receivableBillItemKoukuanTuihuoSids) {
        List<FinReceivableBillItemKoukuanTuihuo> list = finReceivableBillItemKoukuanTuihuoMapper.selectList(new QueryWrapper<FinReceivableBillItemKoukuanTuihuo>()
                .lambda().in(FinReceivableBillItemKoukuanTuihuo::getReceivableBillItemKoukuanTuihuoSid, receivableBillItemKoukuanTuihuoSids));
        int row = finReceivableBillItemKoukuanTuihuoMapper.deleteBatchIds(receivableBillItemKoukuanTuihuoSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBillItemKoukuanTuihuo());
                MongodbUtil.insertUserLog(o.getReceivableBillItemKoukuanTuihuoSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinReceivableBillItemKoukuanTuihuo> list = bill.getTuihuoList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinReceivableBillItemKoukuanTuihuo item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setReceivableBillSid(bill.getReceivableBillSid());
                row += insertFinReceivableBillItemKoukuanTuihuo(item);
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
        List<FinReceivableBillItemKoukuanTuihuo> list = bill.getTuihuoList();
        // 原本明细
        List<FinReceivableBillItemKoukuanTuihuo> oldList = finReceivableBillItemKoukuanTuihuoMapper.selectList(new QueryWrapper<FinReceivableBillItemKoukuanTuihuo>()
                .lambda().eq(FinReceivableBillItemKoukuanTuihuo::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinReceivableBillItemKoukuanTuihuo> newList = list.stream().filter(o -> o.getReceivableBillItemKoukuanTuihuoSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setTuihuoList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinReceivableBillItemKoukuanTuihuo> updateList = list.stream().filter(o -> o.getReceivableBillItemKoukuanTuihuoSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinReceivableBillItemKoukuanTuihuo::getReceivableBillItemKoukuanTuihuoSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinReceivableBillItemKoukuanTuihuo> map = oldList.stream().collect(Collectors.toMap(FinReceivableBillItemKoukuanTuihuo::getReceivableBillItemKoukuanTuihuoSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getReceivableBillItemKoukuanTuihuoSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getReceivableBillItemKoukuanTuihuoSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finReceivableBillItemKoukuanTuihuoMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getReceivableBillItemKoukuanTuihuoSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinReceivableBillItemKoukuanTuihuo> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getReceivableBillItemKoukuanTuihuoSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinReceivableBillItemKoukuanTuihuo> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getReceivableBillItemKoukuanTuihuoSid() != null)
                .map(FinReceivableBillItemKoukuanTuihuo::getReceivableBillItemKoukuanTuihuoSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finReceivableBillItemKoukuanTuihuoMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getReceivableBillItemKoukuanTuihuoSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

}
