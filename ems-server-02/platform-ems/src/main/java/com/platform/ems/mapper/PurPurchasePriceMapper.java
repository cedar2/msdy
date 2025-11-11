package com.platform.ems.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.PurPurchasePriceItem;
import org.apache.ibatis.annotations.Param;

/**
 * 采购价信息主Mapper接口
 *
 * @author ChenPinzhen
 * @date 2021-02-04
 */
public interface PurPurchasePriceMapper extends BaseMapper<PurPurchasePrice>  {
    /**
     * 查询采购价信息主
     *
     * @param clientId 采购价信息主ID
     * @return 采购价信息主
     */
    public PurPurchasePrice selectPurPurchasePriceById(Long clientId);
    public Long selectStartDateDesc(@Param("barcodeSid") Long barcodeSid);
    /**
     * 按色获取采购价
     */
    public PurPurchasePriceItem getPurchaseTaxK1(PurPurchasePrice purPurchasePrice);
    /**
     * 按款获取采购价
     */
    public PurPurchasePriceItem getPurchaseTaxK(PurPurchasePrice purPurchasePrice);
    /**
     * 查询采购价信息主列表
     *
     * @param purPurchasePrice 采购价信息主
     * @return 采购价信息主集合
     */
    public List<PurPurchasePrice> selectPurPurchasePriceList(PurPurchasePrice purPurchasePrice);

    /**
     * 新增采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public int insertPurPurchasePrice(PurPurchasePrice purPurchasePrice);

    /**
     * 修改采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    public int updatePurPurchasePrice(PurPurchasePrice purPurchasePrice);

    /**
     * 删除采购价信息主
     *
     * @param clientId 采购价信息主ID
     * @return 结果
     */
    public int deletePurPurchasePriceById(String clientId);

    /**
     * 批量删除采购价信息主
     *
     * @param purchasePriceInforSid 需要删除的数据ID
     * @return 结果
     */
    public int deletePurPurchasePriceByIds(String[] purchasePriceInforSid);

    /**
     * 全量更新
     *
     * @param
     * @return 结果
     */
    public int updateAllById(PurPurchasePrice purPurchasePrice);
}
