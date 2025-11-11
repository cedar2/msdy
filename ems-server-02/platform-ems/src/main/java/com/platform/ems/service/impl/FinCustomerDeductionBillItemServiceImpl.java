package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FinCustomerDeductionBillItem;
import com.platform.ems.mapper.FinCustomerDeductionBillItemMapper;
import com.platform.ems.service.IFinCustomerDeductionBillItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 客户扣款单明细报表Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinCustomerDeductionBillItemServiceImpl extends ServiceImpl<FinCustomerDeductionBillItemMapper,FinCustomerDeductionBillItem>  implements IFinCustomerDeductionBillItemService {
    @Autowired
    private FinCustomerDeductionBillItemMapper finCustomerDeductionBillItemMapper;


    private static final String TITLE = "客户扣款单明细报表";
    /**
     * 查询客户扣款单明细报表
     *
     * @param deductionBillItemSid 客户扣款单明细报表ID
     * @return 客户扣款单明细报表
     */
    @Override
    public FinCustomerDeductionBillItem selectFinCustomerDeductionBillItemById(Long deductionBillItemSid) {
        FinCustomerDeductionBillItem finCustomerDeductionBillItem = finCustomerDeductionBillItemMapper.selectFinCustomerDeductionBillItemById(deductionBillItemSid);
        MongodbUtil.find(finCustomerDeductionBillItem);
        return  finCustomerDeductionBillItem;
    }

    /**
     * 查询客户扣款单明细报表列表
     *
     * @param finCustomerDeductionBillItem 客户扣款单明细报表
     * @return 客户扣款单明细报表
     */
    @Override
    public List<FinCustomerDeductionBillItem> selectFinCustomerDeductionBillItemList(FinCustomerDeductionBillItem finCustomerDeductionBillItem) {
        return finCustomerDeductionBillItemMapper.selectFinCustomerDeductionBillItemList(finCustomerDeductionBillItem);
    }


}
