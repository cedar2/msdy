package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.SalCustomerMonthAccountBillMapper;
import com.platform.ems.mapper.SalCustomerMonthAccountBillZanguMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.mapper.FinBookReceiptEstimationItemMapper;
import com.platform.ems.service.IFinBookReceiptEstimationItemService;
import com.platform.ems.util.MongodbUtil;

import static java.util.stream.Collectors.toList;

/**
 * 财务流水账-明细-应收暂估Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinBookReceiptEstimationItemServiceImpl extends ServiceImpl<FinBookReceiptEstimationItemMapper, FinBookReceiptEstimationItem> implements IFinBookReceiptEstimationItemService {
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private SalCustomerMonthAccountBillZanguMapper salCustomerMonthAccountBillZanguMapper;
    @Autowired
    private SalCustomerMonthAccountBillMapper salCustomerMonthAccountBillMapper;


    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "财务流水账-明细-应收暂估";


    /**
     * 查询财务流水账-明细-应收暂估列表
     *
     * @param finBookReceiptEstimationItem 财务流水账-明细-应收暂估
     * @return 财务流水账-明细-应收暂估
     */
    @Override
    public List<FinBookReceiptEstimationItem> selectFinBookReceiptEstimationItemList(FinBookReceiptEstimationItem finBookReceiptEstimationItem) {
        List<FinBookReceiptEstimationItem> itemList = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemList(finBookReceiptEstimationItem);
        return itemList;
    }

    /**
     * 修改财务流水账-明细-应收暂估 核销中
     *
     * @param finBookReceiptEstimationItem 财务流水账-明细-应收暂估
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByAmountTax(FinBookReceiptEstimationItem finBookReceiptEstimationItem) {
        FinBookReceiptEstimationItem response = finBookReceiptEstimationItemMapper.selectFinBookReceiptEstimationItemById(finBookReceiptEstimationItem.getBookReceiptEstimationItemSid());
        //计算核销状态
        clearStatus(finBookReceiptEstimationItem);
        int row = finBookReceiptEstimationItemMapper.updateById(finBookReceiptEstimationItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finBookReceiptEstimationItem);
            MongodbUtil.insertUserLog(finBookReceiptEstimationItem.getBookReceiptEstimationItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setBusinessVerify(FinBookReceiptEstimationItem finBookReceiptEstimationItem){
        int row = 0;
        if (finBookReceiptEstimationItem.getBookReceiptEstimationItemSidList() == null
                || finBookReceiptEstimationItem.getBookReceiptEstimationItemSidList().length == 0){
            if (finBookReceiptEstimationItem.getBookReceiptEstimationItemSid() != null){
                finBookReceiptEstimationItem.setBookReceiptEstimationItemSidList(new Long[]{finBookReceiptEstimationItem.getBookReceiptEstimationItemSid()});
            }else {
                throw new BaseException("请选择行");
            }
        }
        if (finBookReceiptEstimationItem.getIsBusinessVerify() == null){
            throw new BaseException("请选择是否已业务对账");
        }
        List<SalCustomerMonthAccountBillZangu> salCustomerMonthAccountBillZanguList = salCustomerMonthAccountBillZanguMapper.selectList(
                new QueryWrapper<SalCustomerMonthAccountBillZangu>().lambda()
                        .in(SalCustomerMonthAccountBillZangu::getBookReceiptEstimationItemSid,
                                finBookReceiptEstimationItem.getBookReceiptEstimationItemSidList()));
        for (SalCustomerMonthAccountBillZangu item:salCustomerMonthAccountBillZanguList) {
            SalCustomerMonthAccountBill salCustomerMonthAccountBill = salCustomerMonthAccountBillMapper
                    .selectSalCustomerMonthAccountBillById(item.getCustomerMonthAccountBillSid());
            if(salCustomerMonthAccountBill != null && ConstantsEms.CHECK_STATUS.equals(salCustomerMonthAccountBill.getHandleStatus())) {
                FinBookReceiptEstimationItem mx = finBookReceiptEstimationItemMapper
                        .selectFinBookReceiptEstimationItemById(item.getBookReceiptEstimationItemSid());
                if (finBookReceiptEstimationItem.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_N)
                        && mx.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_Y)) {
                    throw new BaseException("应收暂估流水" + mx.getBookReceiptEstimationCode() + "已被对账单"
                            + salCustomerMonthAccountBill.getCustomerMonthAccountBillCode() + "引用!");
                }
            }
        }
        LambdaUpdateWrapper<FinBookReceiptEstimationItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, finBookReceiptEstimationItem.getBookReceiptEstimationItemSidList())
                .set(FinBookReceiptEstimationItem::getIsBusinessVerify, finBookReceiptEstimationItem.getIsBusinessVerify());

        row = finBookReceiptEstimationItemMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置对账账期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setBusinessVerifyPeriod(FinBookReceiptEstimationItem estimationItem) {
        int row = 0;
        Long[] sids = estimationItem.getBookReceiptEstimationItemSidList();
        if (sids != null && sids.length > 0) {
            if (StrUtil.isBlank(estimationItem.getBusinessVerifyPeriod())) {
                throw new BaseException("对账账期不能为空！");
            }
            List<FinBookReceiptEstimationItem> estimationItemList = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper
                    <FinBookReceiptEstimationItem>().lambda()
                    .in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, sids)
                    .and(que -> que.ne(FinBookReceiptEstimationItem::getBusinessVerifyPeriod, estimationItem.getBusinessVerifyPeriod())
                            .or().isNull(FinBookReceiptEstimationItem::getBusinessVerifyPeriod)));
            if (CollectionUtil.isEmpty(estimationItemList)) {
                return row;
            }
            sids = estimationItemList.stream().map(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid).toArray(Long[]::new);
            LambdaUpdateWrapper<FinBookReceiptEstimationItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid,sids)
                    .set(FinBookReceiptEstimationItem::getBusinessVerifyPeriod, estimationItem.getBusinessVerifyPeriod());
            row = finBookReceiptEstimationItemMapper.update(new FinBookReceiptEstimationItem(), updateWrapper);
            if (row > 0) {
                for (FinBookReceiptEstimationItem item : estimationItemList) {
                    // 操作日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(item, "businessVerifyPeriod", item.getBusinessVerifyPeriod(), estimationItem.getBusinessVerifyPeriod(), msgList);
                    MongodbUtil.insertUserLog(item.getBookReceiptEstimationItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
                }
            }
        }
        return row;
    }

    /**
     * 得到 金额除以单价后的数量
     *
     */
    private BigDecimal divide(BigDecimal divisor, BigDecimal dividend){
        //得到约等于开票的量
        BigDecimal quantity = BigDecimal.ZERO;
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) ==0 ||
                dividend == null || dividend.compareTo(BigDecimal.ZERO) ==0){
            return quantity;
        }
        boolean flag = divisor.compareTo(BigDecimal.ZERO) < 0 ? true : false;
        divisor = divisor.abs();
        dividend = dividend.abs();
        //得到商和余数
        BigDecimal result[] = divisor.divideAndRemainder(dividend);
        if (result[0].compareTo(BigDecimal.ZERO) >= 0){
            if (result[1].compareTo(BigDecimal.ZERO) > 0){
                quantity = result[0].add(BigDecimal.ONE);
            }
            else {
                quantity = result[0];
            }
        }
        if (flag) { quantity = quantity.multiply(new BigDecimal(-1)); }
        return quantity;
    }

    /**
     * 计算核销状态
     *
     */
    private void clearStatus(FinBookReceiptEstimationItem finBookReceiptEstimationItem){
        if (finBookReceiptEstimationItem.getCurrencyAmountTaxYhx() == null ||
                finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
            finBookReceiptEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //如果已核销等于金额，就是全部核销
        else if (finBookReceiptEstimationItem.getCurrencyAmountTaxYhx().compareTo(finBookReceiptEstimationItem.getCurrencyAmountTax()) == 0){
            finBookReceiptEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        } else {
            finBookReceiptEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        finBookReceiptEstimationItem.setClearStatus(finBookReceiptEstimationItem.getClearStatusMoney());
    }


    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinBookReceiptEstimationItem(FinBookReceiptEstimationItem estimationItem) {
        int row = finBookReceiptEstimationItemMapper.insert(estimationItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinBookReceiptEstimationItem(), estimationItem);
            MongodbUtil.insertUserLog(estimationItem.getBookReceiptEstimationItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置行号
     *
     * @param order 销售意向单
     * @return 结果
     */
    public void setItemNum(List<FinBookReceiptEstimationItem> list) {
        List<FinBookReceiptEstimationItem> nullItemList = list.stream().filter(o->o.getItemNum()==null).collect(toList());
        if (CollectionUtil.isNotEmpty(nullItemList)) {
            long maxNum = getMaxItemNum(list);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getItemNum() == null) {
                    list.get(i).setItemNum(maxNum++);
                }
            }
        }
    }

    /**
     * 获取最大行号
     */
    private long getMaxItemNum(List<FinBookReceiptEstimationItem> list) {
        long maxNum = 1;
        List<FinBookReceiptEstimationItem> haveItemList = list.stream().filter(o -> o.getItemNum() != null).collect(toList());
        if (CollectionUtil.isNotEmpty(haveItemList)) {
            haveItemList = haveItemList.stream().sorted(Comparator.comparing(FinBookReceiptEstimationItem::getItemNum).reversed()).collect(Collectors.toList());
            maxNum = haveItemList.get(0).getItemNum() + 1;
        }
        return maxNum;
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinBookReceiptEstimation estimation) {
        int row = 0;
        List<FinBookReceiptEstimationItem> list = estimation.getItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinBookReceiptEstimationItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setBookReceiptEstimationSid(estimation.getBookReceiptEstimationSid());
                row += insertFinBookReceiptEstimationItem(item);
            }
        }
        return row;
    }

    /**
     * 批量修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByList(FinBookReceiptEstimation bill) {
        int row = 0;
        List<FinBookReceiptEstimationItem> list = bill.getItemList();
        // 原本明细
        List<FinBookReceiptEstimationItem> oldList = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>()
                .lambda().eq(FinBookReceiptEstimationItem::getBookReceiptEstimationSid, bill.getBookReceiptEstimationSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 设置行号
            setItemNum(list);
            // 新增行
            List<FinBookReceiptEstimationItem> newList = list.stream().filter(o -> o.getBookReceiptEstimationItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setItemList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinBookReceiptEstimationItem> updateList = list.stream().filter(o -> o.getBookReceiptEstimationItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinBookReceiptEstimationItem> map = oldList.stream().collect(Collectors.toMap(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getBookReceiptEstimationItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getBookReceiptEstimationItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finBookReceiptEstimationItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getBookReceiptEstimationItemSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinBookReceiptEstimationItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getBookReceiptEstimationItemSid())).collect(Collectors.toList());
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
    public int deleteByList(List<FinBookReceiptEstimationItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getBookReceiptEstimationItemSid() != null)
                .map(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finBookReceiptEstimationItemMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getBookReceiptEstimationItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
