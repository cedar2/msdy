package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.FinBookPayment;
import com.platform.ems.domain.FinBookPaymentItem;
import com.platform.ems.mapper.FinBookPaymentItemMapper;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.service.IFinBookPaymentItemService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 财务流水账-明细-付款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-07
 */
@Service
@SuppressWarnings("all")
public class FinBookPaymentItemServiceImpl extends ServiceImpl<FinBookPaymentItemMapper,FinBookPaymentItem>  implements IFinBookPaymentItemService {
    @Autowired
    private FinBookPaymentItemMapper finBookPaymentItemMapper;

    private static final String TITLE = "财务流水账-明细-付款";

    /**
     * 新增付款单-明细
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItem 付款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinBookPaymentItemItem(FinBookPaymentItem finBookPaymentItem) {
        int row = finBookPaymentItemMapper.insert(finBookPaymentItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinBookPaymentItem(), finBookPaymentItem);
            MongodbUtil.insertUserLog(finBookPaymentItem.getBookPaymentItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinBookPayment payment) {
        int row = 0;
        List<FinBookPaymentItem> list = payment.getItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinBookPaymentItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setBookPaymentSid(payment.getBookPaymentSid());
                row += insertFinBookPaymentItemItem(item);
            }
        }
        return row;
    }

    /**
     * 计算核销状态
     *
     */
    private void clearStatus(FinBookPaymentItem finBookPaymentItem){
        if (finBookPaymentItem.getCurrencyAmountTaxYhx() == null ||
                finBookPaymentItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
            finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //如果已核销等于金额，就是全部核销
        else if (finBookPaymentItem.getCurrencyAmountTaxYhx().compareTo(finBookPaymentItem.getCurrencyAmountTaxFk()) == 0){
            finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        } else {
            finBookPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
    }

    /**
     * 修改 核销中金额  已核销金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByAmountTax(FinBookPaymentItem finBookPaymentItem) {
        FinBookPaymentItem response = finBookPaymentItemMapper.selectById
                (finBookPaymentItem.getBookPaymentItemSid());
        clearStatus(finBookPaymentItem);
        int row = finBookPaymentItemMapper.updateById(finBookPaymentItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finBookPaymentItem);
            MongodbUtil.insertUserLog(finBookPaymentItem.getBookPaymentItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }


}
