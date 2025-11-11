package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsFinance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.FinBookCustomerDeductionItem;
import com.platform.ems.mapper.FinBookCustomerDeductionItemMapper;
import com.platform.ems.service.IFinBookCustomerDeductionItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * s_fin_book_customer_deduction_itemService业务层处理
 *
 * @author linhongwei
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinBookCustomerDeductionItemServiceImpl extends ServiceImpl<FinBookCustomerDeductionItemMapper,FinBookCustomerDeductionItem>  implements IFinBookCustomerDeductionItemService {
    @Autowired
    private FinBookCustomerDeductionItemMapper finBookCustomerDeductionItemMapper;

    private static final String TITLE = "s_fin_book_customer_deduction_item";

    /**
     * 计算核销状态
     *
     */
    private void clearStatus(FinBookCustomerDeductionItem finBookCustomerDeductionItem){
        if (finBookCustomerDeductionItem.getCurrencyAmountTaxYhx() == null ||
                finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
            finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //如果已核销等于金额，就是全部核销
        else if (finBookCustomerDeductionItem.getCurrencyAmountTaxYhx().compareTo(finBookCustomerDeductionItem.getCurrencyAmountTaxKk()) == 0){
            finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        } else {
            finBookCustomerDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
    }

    /**
     * 修改 核销中金额  已核销金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByAmountTax(FinBookCustomerDeductionItem finBookCustomerDeductionItem) {
        FinBookCustomerDeductionItem response = finBookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemById
                (finBookCustomerDeductionItem.getBookDeductionItemSid());
        clearStatus(finBookCustomerDeductionItem);
        int row = finBookCustomerDeductionItemMapper.updateById(finBookCustomerDeductionItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finBookCustomerDeductionItem);
            MongodbUtil.insertUserLog(finBookCustomerDeductionItem.getBookDeductionItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

}
