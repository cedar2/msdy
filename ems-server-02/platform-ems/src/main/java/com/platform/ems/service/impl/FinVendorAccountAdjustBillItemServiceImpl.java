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
import com.platform.ems.domain.FinVendorAccountAdjustBillItem;
import com.platform.ems.mapper.FinVendorAccountAdjustBillItemMapper;
import com.platform.ems.service.IFinVendorAccountAdjustBillItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商调账单明细报表Service业务层处理
 *
 * @author qhq
 * @date 2021-05-26
 */
@Service
@SuppressWarnings("all")
public class FinVendorAccountAdjustBillItemServiceImpl extends ServiceImpl<FinVendorAccountAdjustBillItemMapper,FinVendorAccountAdjustBillItem>  implements IFinVendorAccountAdjustBillItemService {
    @Autowired
    private FinVendorAccountAdjustBillItemMapper finVendorAccountAdjustBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商调账单明细报表";
    /**
     * 查询供应商调账单明细报表
     *
     * @param adjustBillItemSid 供应商调账单明细报表ID
     * @return 供应商调账单明细报表
     */
    @Override
    public FinVendorAccountAdjustBillItem selectFinVendorAccountAdjustBillItemById(Long adjustBillItemSid) {
        FinVendorAccountAdjustBillItem finVendorAccountAdjustBillItem = finVendorAccountAdjustBillItemMapper.selectFinVendorAccountAdjustBillItemById(adjustBillItemSid);
        MongodbUtil.find(finVendorAccountAdjustBillItem);
        return  finVendorAccountAdjustBillItem;
    }

    /**
     * 查询供应商调账单明细报表列表
     *
     * @param finVendorAccountAdjustBillItem 供应商调账单明细报表
     * @return 供应商调账单明细报表
     */
    @Override
    public List<FinVendorAccountAdjustBillItem> selectFinVendorAccountAdjustBillItemList(FinVendorAccountAdjustBillItem finVendorAccountAdjustBillItem) {
        return finVendorAccountAdjustBillItemMapper.selectFinVendorAccountAdjustBillItemList(finVendorAccountAdjustBillItem);
    }


}
