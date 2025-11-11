package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.CosProductCost;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.PurPurchasePriceItem;
import com.platform.ems.domain.PurQuoteBargainItem;
import com.platform.ems.domain.dto.request.PurQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurQuoteBargainResponse;
import org.apache.ibatis.annotations.Param;

/**
 * 报议价单明细(报价/核价/议价)Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-26
 */
public interface PurQuoteBargainItemMapper extends BaseMapper<PurQuoteBargainItem> {


    List<PurQuoteBargainItem> selectPurRequestQuotationItemById(Long quoteBargainSid);

    /**
     * 获取明细详情
     *
     * @param quoteBargainItemSid Long
     * @return int
     * @author chenkw
     */
    PurQuoteBargainItem selectPurRequestQuotationItemByItemId(Long quoteBargainItemSid);

    List<PurQuoteBargainItem> selectPurRequestQuotationItemList(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 采购报核议价查询页面
     *
     * @param purQuoteBargainReportResponse List PurQuoteBargainReportResponse
     * @return int
     */
    List<PurQuoteBargainReportResponse> report(PurQuoteBargainReportResponse purQuoteBargainReportResponse);

    List<PurQuoteBargainResponse> reportNew(PurQuoteBargainRequest request);

    /**
     * 添加多个
     *
     * @param list List PurQuoteBargainItem
     * @return int
     */
    int inserts(@Param("list") List<PurQuoteBargainItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurQuoteBargainItem
     * @return int
     */
    int updateAllById(PurQuoteBargainItem entity);

    /**
     * 更新多个
     *
     * @param list List PurQuoteBargainItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurQuoteBargainItem> list);

    /**
     * 查询采购价明细
     *
     * @param purQuoteBargainItem PurQuoteBargainItem
     * @return int
     */
    List<PurPurchasePriceItem> selectPriceItemList(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 查询采购成本核算
     *
     * @param purQuoteBargainItem PurQuoteBargainItem
     * @return int
     */
    List<CosProductCost> selectProductCostList(PurQuoteBargainItem purQuoteBargainItem);
}
