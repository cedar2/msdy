package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPriceInforItem;

/**
 * 采购价格记录明细(报价/核价/议价)Service接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface IPurPriceInforItemService extends IService<PurPriceInforItem>{
    /**
     * 查询采购价格记录明细(报价/核价/议价)
     * 
     * @param priceInforItemSid 采购价格记录明细(报价/核价/议价)ID
     * @return 采购价格记录明细(报价/核价/议价)
     */
    public PurPriceInforItem selectPurPriceInforItemById(Long priceInforItemSid);

    /**
     * 查询采购价格记录明细(报价/核价/议价)列表
     * 
     * @param purPriceInforItem 采购价格记录明细(报价/核价/议价)
     * @return 采购价格记录明细(报价/核价/议价)集合
     */
    public List<PurPriceInforItem> selectPurPriceInforItemList(PurPriceInforItem purPriceInforItem);

    /**
     * 新增采购价格记录明细(报价/核价/议价)
     * 
     * @param purPriceInforItem 采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    public int insertPurPriceInforItem(PurPriceInforItem purPriceInforItem);

    /**
     * 修改采购价格记录明细(报价/核价/议价)
     * 
     * @param purPriceInforItem 采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    public int updatePurPriceInforItem(PurPriceInforItem purPriceInforItem);

    /**
     * 批量删除采购价格记录明细(报价/核价/议价)
     * 
     * @param priceInforItemSids 需要删除的采购价格记录明细(报价/核价/议价)ID
     * @return 结果
     */
    public int deletePurPriceInforItemByIds(List<Long> priceInforItemSids);

}
