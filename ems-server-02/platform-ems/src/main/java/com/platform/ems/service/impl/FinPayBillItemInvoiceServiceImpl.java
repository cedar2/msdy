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
import com.platform.ems.domain.FinPayBillItemInvoice;
import com.platform.ems.mapper.FinVendorInvoiceRecordMapper;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinPayBillItemInvoiceMapper;
import com.platform.ems.service.IFinPayBillItemInvoiceService;

/**
 * 付款单-发票台账明细表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all" )
public class FinPayBillItemInvoiceServiceImpl extends ServiceImpl<FinPayBillItemInvoiceMapper,FinPayBillItemInvoice> implements IFinPayBillItemInvoiceService {
    @Autowired
    private FinPayBillItemInvoiceMapper finPayBillItemInvoiceMapper;
    @Autowired
    private FinVendorInvoiceRecordMapper finVendorInvoiceRecordMapper;

    private static final String TITLE = "付款单-发票台账明细表" ;

    /**
     * 查询付款单-发票台账明细表
     *
     * @param payBillItemInvoiceSid 付款单-发票台账明细表ID
     * @return 付款单-发票台账明细表
     */
    @Override
    public FinPayBillItemInvoice selectFinPayBillItemInvoiceById(Long payBillItemInvoiceSid) {
        FinPayBillItemInvoice finPayBillItemInvoice =finPayBillItemInvoiceMapper.selectFinPayBillItemInvoiceById(payBillItemInvoiceSid);
        MongodbUtil.find(finPayBillItemInvoice);
        return finPayBillItemInvoice;
    }

    /**
     * 查询付款单-发票台账明细表列表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 付款单-发票台账明细表
     */
    @Override
    public List<FinPayBillItemInvoice> selectFinPayBillItemInvoiceList(FinPayBillItemInvoice finPayBillItemInvoice) {
        return finPayBillItemInvoiceMapper.selectFinPayBillItemInvoiceList(finPayBillItemInvoice);
    }

    /**
     * 新增付款单-发票台账明细表
     * 需要注意编码重复校验
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBillItemInvoice(FinPayBillItemInvoice finPayBillItemInvoice) {
        int row = finPayBillItemInvoiceMapper.insert(finPayBillItemInvoice);
        if (row > 0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinPayBillItemInvoice(), finPayBillItemInvoice);
            MongodbUtil.insertUserLog(finPayBillItemInvoice.getPayBillItemInvoiceSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改付款单-发票台账明细表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBillItemInvoice(FinPayBillItemInvoice finPayBillItemInvoice) {
        FinPayBillItemInvoice original = finPayBillItemInvoiceMapper.selectFinPayBillItemInvoiceById(finPayBillItemInvoice.getPayBillItemInvoiceSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finPayBillItemInvoice);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItemInvoice.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemInvoiceMapper.updateAllById(finPayBillItemInvoice);
        if (row > 0){
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItemInvoice.getPayBillItemInvoiceSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更付款单-发票台账明细表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBillItemInvoice(FinPayBillItemInvoice finPayBillItemInvoice) {
        FinPayBillItemInvoice response = finPayBillItemInvoiceMapper.selectFinPayBillItemInvoiceById(finPayBillItemInvoice.getPayBillItemInvoiceSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finPayBillItemInvoice);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBillItemInvoice.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillItemInvoiceMapper.updateAllById(finPayBillItemInvoice);
        if (row > 0){
            //插入日志
            MongodbUtil.insertUserLog(finPayBillItemInvoice.getPayBillItemInvoiceSid(), BusinessType.CHANGE.getValue(), response, finPayBillItemInvoice, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款单-发票台账明细表
     *
     * @param payBillItemInvoiceSids 需要删除的付款单-发票台账明细表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillItemInvoiceByIds(List<Long> payBillItemInvoiceSids) {
        List<FinPayBillItemInvoice> list = finPayBillItemInvoiceMapper.selectList(new QueryWrapper<FinPayBillItemInvoice>()
                .lambda().in(FinPayBillItemInvoice::getPayBillItemInvoiceSid, payBillItemInvoiceSids));
        int row = finPayBillItemInvoiceMapper.deleteBatchIds(payBillItemInvoiceSids);
        if (row > 0){
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinPayBillItemInvoice());
                MongodbUtil.insertUserLog(o.getPayBillItemInvoiceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
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
        List<FinPayBillItemInvoice> list = bill.getInvoiceList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinPayBillItemInvoice item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setPayBillSid(bill.getPayBillSid());
                row += insertFinPayBillItemInvoice(item);
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
        List<FinPayBillItemInvoice> list = bill.getInvoiceList();
        // 原本明细
        List<FinPayBillItemInvoice> oldList = finPayBillItemInvoiceMapper.selectList(new QueryWrapper<FinPayBillItemInvoice>()
                .lambda().eq(FinPayBillItemInvoice::getPayBillSid, bill.getPayBillSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FinPayBillItemInvoice> newList = list.stream().filter(o -> o.getPayBillItemInvoiceSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setInvoiceList(newList);
                row = row + insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinPayBillItemInvoice> updateList = list.stream().filter(o -> o.getPayBillItemInvoiceSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinPayBillItemInvoice::getPayBillItemInvoiceSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinPayBillItemInvoice> map = oldList.stream().collect(Collectors.toMap(FinPayBillItemInvoice::getPayBillItemInvoiceSid, Function.identity()));
                    for (FinPayBillItemInvoice item : updateList) {
                        if (map.containsKey(item.getPayBillItemInvoiceSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getPayBillItemInvoiceSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            row = row + finPayBillItemInvoiceMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getPayBillItemInvoiceSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    }
                    // 删除行
                    List<FinPayBillItemInvoice> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getPayBillItemInvoiceSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinPayBillItemInvoice> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getPayBillItemInvoiceSid() != null)
                .map(FinPayBillItemInvoice::getPayBillItemInvoiceSid).collect(Collectors.toList());

        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finPayBillItemInvoiceMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getPayBillItemInvoiceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
