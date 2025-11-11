package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPriceInfor;
import com.platform.ems.domain.PurPriceInforItem;

/**
 * 采购价格记录主(报价/核价/议价)Service接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface IPurPriceInforService extends IService<PurPriceInfor>{
    /**
     * 查询采购价格记录主(报价/核价/议价)
     * 
     * @param priceInforSid 采购价格记录主(报价/核价/议价)ID
     * @return 采购价格记录主(报价/核价/议价)
     */
    public PurPriceInfor selectPurPriceInforById(Long priceInforSid);

    /**
     * 查询采购价格记录主(报价/核价/议价)列表
     * 
     * @param purPriceInfor 采购价格记录主(报价/核价/议价)
     * @return 采购价格记录主(报价/核价/议价)集合
     */
    public List<PurPriceInfor> selectPurPriceInforList(PurPriceInfor purPriceInfor);

    /**
     * 新增采购价格记录主(报价/核价/议价)
     * 
     * @param purPriceInfor 采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    public int insertPurPriceInfor(PurPriceInfor purPriceInfor);

    /**
     * 修改采购价格记录主(报价/核价/议价)
     * 
     * @param purPriceInfor 采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    public int updatePurPriceInfor(PurPriceInfor purPriceInfor);

    /**
     * 批量删除采购价格记录主(报价/核价/议价)
     * 
     * @param priceInforSids 需要删除的采购价格记录主(报价/核价/议价)ID
     * @return 结果
     */
    public int deletePurPriceInforByIds(List<Long> priceInforSids);

    /**
     * 修改采购价格记录明细或生成记录(报价/核价/议价)
     *
     * @param request 采购价格记录主(报价/核价/议价)
     * @param requestItem 采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    public int updatePriceInfor(PurPriceInfor request, PurPriceInforItem requestItem);

    /**
     * 修改采购价格记录明细或生成记录(报价/核价/议价)  全量更新
     *
     * @param request 采购价格记录主(报价/核价/议价)
     * @param requestItem 采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    public int updateAllPriceInfor(PurPriceInfor request, PurPriceInforItem requestItem);

}
