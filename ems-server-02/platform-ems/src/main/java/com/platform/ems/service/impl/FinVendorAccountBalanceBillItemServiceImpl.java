package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinVendorAccountBalanceBillItem;
import com.platform.ems.mapper.FinVendorAccountBalanceBillItemMapper;
import com.platform.ems.service.IFinVendorAccountBalanceBillItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商账互抵单明细报表Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
@Service
@SuppressWarnings("all")
public class FinVendorAccountBalanceBillItemServiceImpl extends ServiceImpl<FinVendorAccountBalanceBillItemMapper,FinVendorAccountBalanceBillItem>  implements IFinVendorAccountBalanceBillItemService {
    @Autowired
    private FinVendorAccountBalanceBillItemMapper finVendorAccountBalanceBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商账互抵单明细报表";
    /**
     * 查询供应商账互抵单明细报表
     * 
     * @param vendorAccountBalanceBillItemSid 供应商账互抵单明细报表ID
     * @return 供应商账互抵单明细报表
     */
    @Override
    public FinVendorAccountBalanceBillItem selectFinVendorAccountBalanceBillItemById(Long vendorAccountBalanceBillItemSid) {
        FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem = finVendorAccountBalanceBillItemMapper.selectFinVendorAccountBalanceBillItemById(vendorAccountBalanceBillItemSid);
        MongodbUtil.find(finVendorAccountBalanceBillItem);
        return  finVendorAccountBalanceBillItem;
    }

    /**
     * 查询供应商账互抵单明细报表列表
     * 
     * @param finVendorAccountBalanceBillItem 供应商账互抵单明细报表
     * @return 供应商账互抵单明细报表
     */
    @Override
    public List<FinVendorAccountBalanceBillItem> selectFinVendorAccountBalanceBillItemList(FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem) {
        return finVendorAccountBalanceBillItemMapper.selectFinVendorAccountBalanceBillItemList(finVendorAccountBalanceBillItem);
    }



}
