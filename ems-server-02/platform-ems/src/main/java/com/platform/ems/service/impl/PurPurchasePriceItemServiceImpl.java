package com.platform.ems.service.impl;

import java.util.List;

import com.platform.ems.domain.dto.response.PurPurchasePriceItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.PurPurchasePriceItemMapper;
import com.platform.ems.domain.PurPurchasePriceItem;
import com.platform.ems.service.IPurPurchasePriceItemService;

/**
 * 采购价信息明细Service业务层处理
 * 
 * @author ChenPinzhen
 * @date 2021-02-04
 */
@Service
public class PurPurchasePriceItemServiceImpl implements IPurPurchasePriceItemService {
    @Autowired
    private PurPurchasePriceItemMapper purPurchasePriceItemMapper;

    /**
     * 查询采购价信息明细
     * 
     * @param purchasePriceInforItemSid 采购价信息明细ID
     * @return 采购价信息明细
     */
    @Override
    public PurPurchasePriceItem selectPurPurchasePriceItemById(String purchasePriceInforItemSid) {
        return null;
    }

    /**
     * 查询采购价信息明细列表
     * 
     * @param purPurchasePriceItem 采购价信息明细
     * @return 采购价信息明细
     */
    @Override
    public List<PurPurchasePriceItemResponse> selectPurPurchasePriceItemList(PurPurchasePriceItem purPurchasePriceItem) {
        return purPurchasePriceItemMapper.selectPurPurchasePriceItemList(purPurchasePriceItem);
    }
}
