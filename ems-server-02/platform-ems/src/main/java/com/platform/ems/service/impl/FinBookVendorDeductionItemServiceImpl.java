package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.FinBookVendorDeductionItem;
import com.platform.ems.mapper.FinBookVendorDeductionItemMapper;
import com.platform.ems.service.IFinBookVendorDeductionItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 财务流水账-供应商扣款Service业务层处理
 */
@Service
@SuppressWarnings("all")
public class FinBookVendorDeductionItemServiceImpl extends ServiceImpl<FinBookVendorDeductionItemMapper, FinBookVendorDeductionItem> implements IFinBookVendorDeductionItemService {

    @Autowired
    private FinBookVendorDeductionItemMapper finBookVendorDeductionItemMapper;

    private static final String TITLE = "s_fin_book_vendor_deduction_item";

    /**
     * 计算核销状态
     *
     */
    private void clearStatus(FinBookVendorDeductionItem finBookVendorDeductionItem){
        if (finBookVendorDeductionItem.getCurrencyAmountTaxYhx() == null ||
                finBookVendorDeductionItem.getCurrencyAmountTaxYhx().compareTo(BigDecimal.ZERO) == 0) {
            finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
        }
        //如果已核销等于金额，就是全部核销
        else if (finBookVendorDeductionItem.getCurrencyAmountTaxYhx().compareTo(finBookVendorDeductionItem.getCurrencyAmountTaxKk()) == 0){
            finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        } else {
            finBookVendorDeductionItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_BFHX);
        }
    }

    /**
     * 修改 核销中金额  已核销金额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByAmountTax(FinBookVendorDeductionItem finBookVendorDeductionItem) {
        FinBookVendorDeductionItem response = finBookVendorDeductionItemMapper.selectFinBookVendorDeductionItemById
                (finBookVendorDeductionItem.getBookDeductionItemSid());
        clearStatus(finBookVendorDeductionItem);
        int row = finBookVendorDeductionItemMapper.updateById(finBookVendorDeductionItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finBookVendorDeductionItem);
            MongodbUtil.insertUserLog(finBookVendorDeductionItem.getBookDeductionItemSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

}
