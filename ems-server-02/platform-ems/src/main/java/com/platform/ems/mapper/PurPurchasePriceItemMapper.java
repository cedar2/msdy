package com.platform.ems.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PurPurchasePriceAttachment;
import com.platform.ems.domain.PurPurchasePriceItem;
import com.platform.ems.domain.dto.response.PurPurchasePriceItemResponse;
import com.platform.ems.domain.dto.response.PurPurchasePriceReportResponse;
import org.apache.ibatis.annotations.Param;

/**
 * 采购价信息明细Mapper接口
 * 
 * @author ChenPinzhen
 * @date 2021-02-04
 */
public interface PurPurchasePriceItemMapper extends BaseMapper<PurPurchasePriceItem>  {
    /**
     * 查询采购价信息明细
     * 
     * @param purchasePriceSid 采购价信息明细ID
     * @return 采购价信息明细
     */
     public List<PurPurchasePriceItem>selectPurPurchasePriceItemById(Long purchasePriceSid);
    public List<PurPurchasePriceItem> selectPurPurchasePriceById(@Param("sids") List<Long> sids);
    /**

     * 全量更新

     * null字段也会进行更新，慎用

     * @param entity PurPurchasePriceItem

     * @return int

     */
    int updateAllById(PurPurchasePriceItem entity);

    int updateRe(PurPurchasePriceItem entity);

    /**
     * 查询采购价信息报表明细
     * @param purPurchasePriceReportResponse 查询采购价信息报表明细
     * @return 采购价信息明细
     */
    public List<PurPurchasePriceReportResponse>  purPurchasePriceReport(PurPurchasePriceReportResponse purPurchasePriceReportResponse);

    /**
     * 查询采购价信息明细列表
     * 
     * @param purPurchasePriceItem 采购价信息明细
     * @return 采购价信息明细集合
     */
    public List<PurPurchasePriceItemResponse> selectPurPurchasePriceItemList(PurPurchasePriceItem purPurchasePriceItem);

    /**
     * 添加多个
     * @param list List PurPurchasePriceItem
     * @return int
     */
    int inserts(@Param("list") List<PurPurchasePriceItem> list);

}
