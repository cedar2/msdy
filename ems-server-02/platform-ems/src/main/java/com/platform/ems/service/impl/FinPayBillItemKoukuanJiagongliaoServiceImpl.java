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
import com.platform.ems.domain.FinPayBillItemKoukuanJiagongliao;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinPayBillItemKoukuanJiagongliaoMapper;
import com.platform.ems.service.IFinPayBillItemKoukuanJiagongliaoService;

/**
 * 付款单-核销甲供料扣款明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all" )
public class FinPayBillItemKoukuanJiagongliaoServiceImpl extends ServiceImpl<FinPayBillItemKoukuanJiagongliaoMapper,FinPayBillItemKoukuanJiagongliao> implements IFinPayBillItemKoukuanJiagongliaoService {
    @Autowired
    private FinPayBillItemKoukuanJiagongliaoMapper finPayBillItemKoukuanJiagongliaoMapper;

    private static final String TITLE = "付款单-核销甲供料扣款明细表" ;

    /**
     * 查询付款单-核销甲供料扣款明细表
     *
     * @param payBillItemKoukuanJiagongliaoSid 付款单-核销甲供料扣款明细表ID
     * @return 付款单-核销甲供料扣款明细表
     */
    @Override
    public FinPayBillItemKoukuanJiagongliao selectFinPayBillItemKoukuanJiagongliaoById(Long payBillItemKoukuanJiagongliaoSid) {
        FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao =finPayBillItemKoukuanJiagongliaoMapper.selectFinPayBillItemKoukuanJiagongliaoById(payBillItemKoukuanJiagongliaoSid);
        MongodbUtil.find(finPayBillItemKoukuanJiagongliao);
        return finPayBillItemKoukuanJiagongliao;
    }

    /**
     * 查询付款单-核销甲供料扣款明细表列表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 付款单-核销甲供料扣款明细表
     */
    @Override
    public List<FinPayBillItemKoukuanJiagongliao> selectFinPayBillItemKoukuanJiagongliaoList(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao) {
        return finPayBillItemKoukuanJiagongliaoMapper.selectFinPayBillItemKoukuanJiagongliaoList(finPayBillItemKoukuanJiagongliao);
    }

    /**
     * 新增付款单-核销甲供料扣款明细表
     * 需要注意编码重复校验
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBillItemKoukuanJiagongliao(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao) {
        int row = finPayBillItemKoukuanJiagongliaoMapper.insert(finPayBillItemKoukuanJiagongliao);
        if (row > 0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinPayBillItemKoukuanJiagongliao(), finPayBillItemKoukuanJiagongliao);
            MongodbUtil.insertUserLog(finPayBillItemKoukuanJiagongliao.getPayBillItemKoukuanJiagongliaoSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改付款单-核销甲供料扣款明细表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBillItemKoukuanJiagongliao(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao) {
        FinPayBillItemKoukuanJiagongliao original = finPayBillItemKoukuanJiagongliaoMapper.selectFinPayBillItemKoukuanJiagongliaoById(finPayBillItemKoukuanJiagongliao.getPayBillItemKoukuanJiagongliaoSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finPayBillItemKoukuanJiagongliao);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItemKoukuanJiagongliao.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemKoukuanJiagongliaoMapper.updateAllById(finPayBillItemKoukuanJiagongliao);
        if (row > 0){
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItemKoukuanJiagongliao.getPayBillItemKoukuanJiagongliaoSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更付款单-核销甲供料扣款明细表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBillItemKoukuanJiagongliao(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao) {
        FinPayBillItemKoukuanJiagongliao response = finPayBillItemKoukuanJiagongliaoMapper.selectFinPayBillItemKoukuanJiagongliaoById(finPayBillItemKoukuanJiagongliao.getPayBillItemKoukuanJiagongliaoSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finPayBillItemKoukuanJiagongliao);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItemKoukuanJiagongliao.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemKoukuanJiagongliaoMapper.updateAllById(finPayBillItemKoukuanJiagongliao);
        if (row > 0){
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItemKoukuanJiagongliao.getPayBillItemKoukuanJiagongliaoSid(), BusinessType.CHANGE.getValue(), response, finPayBillItemKoukuanJiagongliao, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款单-核销甲供料扣款明细表
     *
     * @param payBillItemKoukuanJiagongliaoSids 需要删除的付款单-核销甲供料扣款明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillItemKoukuanJiagongliaoByIds(List<Long> payBillItemKoukuanJiagongliaoSids) {
        List<FinPayBillItemKoukuanJiagongliao> list = finPayBillItemKoukuanJiagongliaoMapper.selectList(new QueryWrapper<FinPayBillItemKoukuanJiagongliao>()
                .lambda().in(FinPayBillItemKoukuanJiagongliao::getPayBillItemKoukuanJiagongliaoSid, payBillItemKoukuanJiagongliaoSids));
        int row = finPayBillItemKoukuanJiagongliaoMapper.deleteBatchIds(payBillItemKoukuanJiagongliaoSids);
        if (row > 0){
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinPayBillItemKoukuanJiagongliao());
                MongodbUtil.insertUserLog(o.getPayBillItemKoukuanJiagongliaoSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinPayBillItemKoukuanJiagongliao> list = bill.getJiagongliaoList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinPayBillItemKoukuanJiagongliao item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setPayBillSid(bill.getPayBillSid());
                row += insertFinPayBillItemKoukuanJiagongliao(item);
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
        List<FinPayBillItemKoukuanJiagongliao> list = bill.getJiagongliaoList();
        // 原本明细
        List<FinPayBillItemKoukuanJiagongliao> oldList = finPayBillItemKoukuanJiagongliaoMapper.selectList(new QueryWrapper<FinPayBillItemKoukuanJiagongliao>()
                .lambda().eq(FinPayBillItemKoukuanJiagongliao::getPayBillSid, bill.getPayBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinPayBillItemKoukuanJiagongliao> newList = list.stream().filter(o -> o.getPayBillItemKoukuanJiagongliaoSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setJiagongliaoList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinPayBillItemKoukuanJiagongliao> updateList = list.stream().filter(o -> o.getPayBillItemKoukuanJiagongliaoSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinPayBillItemKoukuanJiagongliao::getPayBillItemKoukuanJiagongliaoSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinPayBillItemKoukuanJiagongliao> map = oldList.stream().collect(Collectors.toMap(FinPayBillItemKoukuanJiagongliao::getPayBillItemKoukuanJiagongliaoSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getPayBillItemKoukuanJiagongliaoSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getPayBillItemKoukuanJiagongliaoSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finPayBillItemKoukuanJiagongliaoMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getPayBillItemKoukuanJiagongliaoSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinPayBillItemKoukuanJiagongliao> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getPayBillItemKoukuanJiagongliaoSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinPayBillItemKoukuanJiagongliao> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getPayBillItemKoukuanJiagongliaoSid() != null)
                .map(FinPayBillItemKoukuanJiagongliao::getPayBillItemKoukuanJiagongliaoSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finPayBillItemKoukuanJiagongliaoMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getPayBillItemKoukuanJiagongliaoSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
