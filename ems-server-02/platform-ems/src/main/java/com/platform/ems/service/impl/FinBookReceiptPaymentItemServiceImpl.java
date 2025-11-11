package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.FinBookReceiptPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.FinBookReceiptPaymentItem;
import com.platform.ems.mapper.FinBookReceiptPaymentItemMapper;
import com.platform.ems.service.IFinBookReceiptPaymentItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 财务流水账-明细-收款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class FinBookReceiptPaymentItemServiceImpl extends ServiceImpl<FinBookReceiptPaymentItemMapper,FinBookReceiptPaymentItem>  implements IFinBookReceiptPaymentItemService {
    @Autowired
    private FinBookReceiptPaymentItemMapper finBookReceiptPaymentItemMapper;

    private static final String TITLE = "财务流水账-明细-收款";

    /**
     * 新增收款单-明细
     * 需要注意编码重复校验
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinBookReceiptPaymentItemItem(FinBookReceiptPaymentItem finBookReceiptPaymentItem) {
        int row = finBookReceiptPaymentItemMapper.insert(finBookReceiptPaymentItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinBookReceiptPaymentItem(), finBookReceiptPaymentItem);
            MongodbUtil.insertUserLog(finBookReceiptPaymentItem.getBookReceiptPaymentItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinBookReceiptPayment payment) {
        int row = 0;
        List<FinBookReceiptPaymentItem> list = payment.getItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinBookReceiptPaymentItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setBookReceiptPaymentSid(payment.getBookReceiptPaymentSid());
                row += insertFinBookReceiptPaymentItemItem(item);
            }
        }
        return row;
    }

    /**
     * 计算核销状态
     *
     */
    private void clearStatus(FinBookReceiptPaymentItem finBookReceiptPaymentItem){
        if (finBookReceiptPaymentItem.getCurrencyAmountTaxYhx() == null ||
                finBookReceiptPaymentItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
            finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //如果已核销等于金额，就是全部核销
        else if (finBookReceiptPaymentItem.getCurrencyAmountTaxYhx().compareTo(finBookReceiptPaymentItem.getCurrencyAmountTaxSk()) == 0){
            finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        } else {
            finBookReceiptPaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
    }

    /**
     * 修改 核销中金额  已核销金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByAmountTax(FinBookReceiptPaymentItem finBookReceiptPaymentItem) {
        FinBookReceiptPaymentItem response = finBookReceiptPaymentItemMapper.selectById
                (finBookReceiptPaymentItem.getBookReceiptPaymentItemSid());
        clearStatus(finBookReceiptPaymentItem);
        int row = finBookReceiptPaymentItemMapper.updateById(finBookReceiptPaymentItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finBookReceiptPaymentItem);
            MongodbUtil.insertUserLog(finBookReceiptPaymentItem.getBookReceiptPaymentItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }
}
