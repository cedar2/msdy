package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinCustomerAccountAdjustBillItem;
import com.platform.ems.mapper.FinCustomerAccountAdjustBillItemMapper;
import com.platform.ems.service.IFinCustomerAccountAdjustBillItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 客户调账单明细报表Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-05-26
 */
@Service
@SuppressWarnings("all")
public class FinCustomerAccountAdjustBillItemServiceImpl extends ServiceImpl<FinCustomerAccountAdjustBillItemMapper,FinCustomerAccountAdjustBillItem>  implements IFinCustomerAccountAdjustBillItemService {
    @Autowired
    private FinCustomerAccountAdjustBillItemMapper finCustomerAccountAdjustBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户调账单明细报表";
    /**
     * 查询客户调账单明细报表
     * 
     * @param adjustBillItemSid 客户调账单明细报表ID
     * @return 客户调账单明细报表
     */
    @Override
    public FinCustomerAccountAdjustBillItem selectFinCustomerAccountAdjustBillItemById(Long adjustBillItemSid) {
        FinCustomerAccountAdjustBillItem finCustomerAccountAdjustBillItem = finCustomerAccountAdjustBillItemMapper.selectFinCustomerAccountAdjustBillItemById(adjustBillItemSid);
        MongodbUtil.find(finCustomerAccountAdjustBillItem);
        return  finCustomerAccountAdjustBillItem;
    }

    /**
     * 查询客户调账单明细报表列表
     * 
     * @param finCustomerAccountAdjustBillItem 客户调账单明细报表
     * @return 客户调账单明细报表
     */
    @Override
    public List<FinCustomerAccountAdjustBillItem> selectFinCustomerAccountAdjustBillItemList(FinCustomerAccountAdjustBillItem finCustomerAccountAdjustBillItem) {
        return finCustomerAccountAdjustBillItemMapper.selectFinCustomerAccountAdjustBillItemList(finCustomerAccountAdjustBillItem);
    }


}
