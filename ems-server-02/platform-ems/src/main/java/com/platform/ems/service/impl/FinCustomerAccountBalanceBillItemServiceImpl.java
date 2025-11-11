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
import com.platform.ems.domain.FinCustomerAccountBalanceBillItem;
import com.platform.ems.mapper.FinCustomerAccountBalanceBillItemMapper;
import com.platform.ems.service.IFinCustomerAccountBalanceBillItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 客户账互抵单明细报表Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-27
 */
@Service
@SuppressWarnings("all")
public class FinCustomerAccountBalanceBillItemServiceImpl extends ServiceImpl<FinCustomerAccountBalanceBillItemMapper,FinCustomerAccountBalanceBillItem>  implements IFinCustomerAccountBalanceBillItemService {
    @Autowired
    private FinCustomerAccountBalanceBillItemMapper finCustomerAccountBalanceBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户账互抵单明细报表";
    /**
     * 查询客户账互抵单明细报表
     *
     * @param customerAccountBalanceBillItemSid 客户账互抵单明细报表ID
     * @return 客户账互抵单明细报表
     */
    @Override
    public FinCustomerAccountBalanceBillItem selectFinCustomerAccountBalanceBillItemById(Long customerAccountBalanceBillItemSid) {
        FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem = finCustomerAccountBalanceBillItemMapper.selectFinCustomerAccountBalanceBillItemById(customerAccountBalanceBillItemSid);
        MongodbUtil.find(finCustomerAccountBalanceBillItem);
        return  finCustomerAccountBalanceBillItem;
    }

    /**
     * 查询客户账互抵单明细报表列表
     *
     * @param finCustomerAccountBalanceBillItem 客户账互抵单明细报表
     * @return 客户账互抵单明细报表
     */
    @Override
    public List<FinCustomerAccountBalanceBillItem> selectFinCustomerAccountBalanceBillItemList(FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem) {
        return finCustomerAccountBalanceBillItemMapper.selectFinCustomerAccountBalanceBillItemList(finCustomerAccountBalanceBillItem);
    }


}
