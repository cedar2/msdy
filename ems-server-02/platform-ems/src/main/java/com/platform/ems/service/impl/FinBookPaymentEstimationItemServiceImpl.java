package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.FinBookPaymentEstimationItemMapper;
import com.platform.ems.mapper.PurVendorMonthAccountBillMapper;
import com.platform.ems.mapper.PurVendorMonthAccountBillZanguMapper;
import com.platform.ems.service.IFinBookPaymentEstimationItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 财务流水账-明细-应付暂估Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-31
 */
@Service
@SuppressWarnings("all")
public class FinBookPaymentEstimationItemServiceImpl extends ServiceImpl<FinBookPaymentEstimationItemMapper, FinBookPaymentEstimationItem> implements IFinBookPaymentEstimationItemService {
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;
    @Autowired
    private PurVendorMonthAccountBillZanguMapper purVendorMonthAccountBillZanguMapper;
    @Autowired
    private PurVendorMonthAccountBillMapper purVendorMonthAccountBillMapper;

    private static final String TITLE = "财务流水账-明细-应付暂估";

    /**
     * 查询财务流水账-明细-应付暂估列表
     *
     * @param finBookPaymentEstimationItem 财务流水账-明细-应付暂估
     * @return 财务流水账-明细-应付暂估
     */
    @Override
    public List<FinBookPaymentEstimationItem> selectFinBookPaymentEstimationItemList(FinBookPaymentEstimationItem finBookPaymentEstimationItem) {
        List<FinBookPaymentEstimationItem> itemList = finBookPaymentEstimationItemMapper.selectFinBookPaymentEstimationItemList(finBookPaymentEstimationItem);
        return itemList;
    }

    /**
     * 修改财务流水账-明细-应收暂估 核销中
     *
     * @param finBookPaymentEstimationItem 财务流水账-明细-应收暂估
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByAmountTax(FinBookPaymentEstimationItem finBookPaymentEstimationItem) {
        FinBookPaymentEstimationItem response = finBookPaymentEstimationItemMapper.selectFinBookPaymentEstimationItemById(finBookPaymentEstimationItem.getBookPaymentEstimationItemSid());
        //计算核销状态
        clearStatus(finBookPaymentEstimationItem);
        int row = finBookPaymentEstimationItemMapper.updateById(finBookPaymentEstimationItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finBookPaymentEstimationItem);
            MongodbUtil.insertUserLog(finBookPaymentEstimationItem.getBookPaymentEstimationItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 得到 金额除以单价后的数量
     */
    private BigDecimal divide(BigDecimal divisor, BigDecimal dividend) {
        //得到约等于开票的量
        BigDecimal quantity = BigDecimal.ZERO;
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0 ||
                dividend == null || dividend.compareTo(BigDecimal.ZERO) == 0) {
            return quantity;
        }
        boolean flag = divisor.compareTo(BigDecimal.ZERO) < 0 ? true : false;
        divisor = divisor.abs();
        dividend = dividend.abs();
        //得到商和余数
        BigDecimal result[] = divisor.divideAndRemainder(dividend);
        if (result[0].compareTo(BigDecimal.ZERO) >= 0) {
            if (result[1].compareTo(BigDecimal.ZERO) > 0) {
                quantity = result[0].add(BigDecimal.ONE);
            } else {
                quantity = result[0];
            }
        }
        if (flag) {
            quantity = quantity.multiply(new BigDecimal(-1));
        }
        return quantity;
    }

    /**
     * 计算核销状态
     */
    private void clearStatus(FinBookPaymentEstimationItem finBookPaymentEstimationItem) {
        if (finBookPaymentEstimationItem.getCurrencyAmountTaxYhx() == null ||
                finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
            finBookPaymentEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //如果已核销等于金额，就是全部核销
        else if (finBookPaymentEstimationItem.getCurrencyAmountTaxYhx().compareTo(finBookPaymentEstimationItem.getCurrencyAmountTax()) == 0) {
            finBookPaymentEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        } else {
            finBookPaymentEstimationItem.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
        finBookPaymentEstimationItem.setClearStatus(finBookPaymentEstimationItem.getClearStatusMoney());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setBusinessVerify(FinBookPaymentEstimationItem finBookPaymentEstimationItem) {
        int row = 0;
        if (finBookPaymentEstimationItem.getBookPaymentEstimationItemSidList() == null
                || finBookPaymentEstimationItem.getBookPaymentEstimationItemSidList().length == 0) {
            if (finBookPaymentEstimationItem.getBookPaymentEstimationItemSid() != null) {
                finBookPaymentEstimationItem.setBookPaymentEstimationItemSidList(new Long[]{finBookPaymentEstimationItem.getBookPaymentEstimationItemSid()});
            } else {
                throw new BaseException("请选择行");
            }
        }
        if (finBookPaymentEstimationItem.getIsBusinessVerify() == null) {
            throw new BaseException("请选择是否已业务对账");
        }
        List<PurVendorMonthAccountBillZangu> purVendorMonthAccountBillZanguList = purVendorMonthAccountBillZanguMapper.selectList(
                new QueryWrapper<PurVendorMonthAccountBillZangu>().lambda()
                        .in(PurVendorMonthAccountBillZangu::getBookPaymentEstimationItemSid,
                                finBookPaymentEstimationItem.getBookPaymentEstimationItemSidList()));
        for (PurVendorMonthAccountBillZangu item : purVendorMonthAccountBillZanguList) {
            PurVendorMonthAccountBill purVendorMonthAccountBill = purVendorMonthAccountBillMapper
                    .selectPurVendorMonthAccountBillById(item.getVendorMonthAccountBillSid());
            if (purVendorMonthAccountBill != null && ConstantsEms.CHECK_STATUS.equals(purVendorMonthAccountBill.getHandleStatus())) {
                FinBookPaymentEstimationItem mx = finBookPaymentEstimationItemMapper
                        .selectFinBookPaymentEstimationItemById(item.getBookPaymentEstimationItemSid());
                if (finBookPaymentEstimationItem.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_N)
                        && mx.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_Y)) {
                    throw new BaseException("应付暂估流水" + mx.getBookPaymentEstimationCode() + "已被对账单"
                            + purVendorMonthAccountBill.getVendorMonthAccountBillCode() + "引用!");
                }
            }
        }


        LambdaUpdateWrapper<FinBookPaymentEstimationItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, finBookPaymentEstimationItem.getBookPaymentEstimationItemSidList())
                .set(FinBookPaymentEstimationItem::getIsBusinessVerify, finBookPaymentEstimationItem.getIsBusinessVerify());

        row = finBookPaymentEstimationItemMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置对账账期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setBusinessVerifyPeriod(FinBookPaymentEstimationItem estimationItem) {
        int row = 0;
        Long[] sids = estimationItem.getBookPaymentEstimationItemSidList();
        if (sids != null && sids.length > 0) {
            if (StrUtil.isBlank(estimationItem.getBusinessVerifyPeriod())) {
                throw new BaseException("对账账期不能为空！");
            }
            List<FinBookPaymentEstimationItem> estimationItemList = finBookPaymentEstimationItemMapper.selectList(new QueryWrapper
                    <FinBookPaymentEstimationItem>().lambda()
                    .in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, sids)
                    .and(que -> que.ne(FinBookPaymentEstimationItem::getBusinessVerifyPeriod, estimationItem.getBusinessVerifyPeriod())
                            .or().isNull(FinBookPaymentEstimationItem::getBusinessVerifyPeriod)));
            if (CollectionUtil.isEmpty(estimationItemList)) {
                return row;
            }
            sids = estimationItemList.stream().map(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid).toArray(Long[]::new);
            LambdaUpdateWrapper<FinBookPaymentEstimationItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, sids)
                    .set(FinBookPaymentEstimationItem::getBusinessVerifyPeriod, estimationItem.getBusinessVerifyPeriod());
            row = finBookPaymentEstimationItemMapper.update(new FinBookPaymentEstimationItem(), updateWrapper);
            if (row > 0) {
                for (FinBookPaymentEstimationItem item : estimationItemList) {
                    // 操作日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(item, "businessVerifyPeriod", item.getBusinessVerifyPeriod(), estimationItem.getBusinessVerifyPeriod(), msgList);
                    MongodbUtil.insertUserLog(item.getBookPaymentEstimationItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
                }
            }
        }
        return row;
    }

    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinBookPaymentEstimationItem(FinBookPaymentEstimationItem estimationItem) {
        int row = finBookPaymentEstimationItemMapper.insert(estimationItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinBookPaymentEstimationItem(), estimationItem);
            MongodbUtil.insertUserLog(estimationItem.getBookPaymentEstimationItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置行号
     *
     * @param order 销售意向单
     * @return 结果
     */
    @Override
    public void setItemNum(List<FinBookPaymentEstimationItem> list) {
        List<FinBookPaymentEstimationItem> nullItemList = list.stream().filter(o -> o.getItemNum() == null).collect(toList());
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
    private long getMaxItemNum(List<FinBookPaymentEstimationItem> list) {
        long maxNum = 1;
        List<FinBookPaymentEstimationItem> haveItemList = list.stream().filter(o -> o.getItemNum() != null).collect(toList());
        if (CollectionUtil.isNotEmpty(haveItemList)) {
            haveItemList = haveItemList.stream().sorted(Comparator.comparing(FinBookPaymentEstimationItem::getItemNum).reversed()).collect(Collectors.toList());
            maxNum = haveItemList.get(0).getItemNum() + 1;
        }
        return maxNum;
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinBookPaymentEstimation estimation) {
        int row = 0;
        List<FinBookPaymentEstimationItem> list = estimation.getItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinBookPaymentEstimationItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setBookPaymentEstimationSid(estimation.getBookPaymentEstimationSid());
                row += insertFinBookPaymentEstimationItem(item);
            }
        }
        return row;
    }

    /**
     * 批量修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByList(FinBookPaymentEstimation bill) {
        int row = 0;
        List<FinBookPaymentEstimationItem> list = bill.getItemList();
        // 原本明细
        List<FinBookPaymentEstimationItem> oldList = finBookPaymentEstimationItemMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>()
                .lambda().eq(FinBookPaymentEstimationItem::getBookPaymentEstimationSid, bill.getBookPaymentEstimationSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 设置行号
            setItemNum(list);
            // 新增行
            List<FinBookPaymentEstimationItem> newList = list.stream().filter(o -> o.getBookPaymentEstimationItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                bill.setItemList(newList);
                insertByList(bill);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinBookPaymentEstimationItem> updateList = list.stream().filter(o -> o.getBookPaymentEstimationItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinBookPaymentEstimationItem> map = oldList.stream().collect(Collectors.toMap(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, Function.identity()));
                    updateList.forEach(item -> {
                        if (map.containsKey(item.getBookPaymentEstimationItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getBookPaymentEstimationItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            finBookPaymentEstimationItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getBookPaymentEstimationItemSid(), bill.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FinBookPaymentEstimationItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getBookPaymentEstimationItemSid())).collect(Collectors.toList());
                    deleteByList(delList);
                }
            }
        } else {
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
    public int deleteByList(List<FinBookPaymentEstimationItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getBookPaymentEstimationItemSid() != null)
                .map(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finBookPaymentEstimationItemMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getBookPaymentEstimationItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }
}
