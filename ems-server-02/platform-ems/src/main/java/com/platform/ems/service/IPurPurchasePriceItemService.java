package com.platform.ems.service;

import java.util.List;
import com.platform.ems.domain.PurPurchasePriceItem;
import com.platform.ems.domain.dto.response.PurPurchasePriceItemResponse;

/**
 * 采购价信息明细Service接口
 * 
 * @author ChenPinzhen
 * @date 2021-02-04
 */
public interface IPurPurchasePriceItemService {
    /**
     * 查询采购价信息明细
     * 
     * @param purchasePriceInforItemSid 采购价信息明细ID
     * @return 采购价信息明细
     */
    public PurPurchasePriceItem selectPurPurchasePriceItemById(String purchasePriceInforItemSid);

    /**
     * 查询采购价信息明细列表
     * 
     * @param purPurchasePriceItem 采购价信息明细
     * @return 采购价信息明细集合
     */
    public List<PurPurchasePriceItemResponse> selectPurPurchasePriceItemList(PurPurchasePriceItem purPurchasePriceItem);

}
