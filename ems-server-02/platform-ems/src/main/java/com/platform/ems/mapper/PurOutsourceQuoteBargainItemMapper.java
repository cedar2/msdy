package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.PurOutsourceQuotationRequest;
import com.platform.ems.domain.dto.request.PurOutsourceQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainResponse;
import org.apache.ibatis.annotations.Param;

/**
 * 加工询报议价单明细(询价/报价/核价/议价)Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-10
 */
public interface PurOutsourceQuoteBargainItemMapper extends BaseMapper<PurOutsourceQuoteBargainItem> {

    List<PurOutsourceQuoteBargainItem> selectPurOutsourceRequestQuotationItemById(Long outsourceQuoteBargainSid);

    PurOutsourceQuoteBargainItem selectPurOutsourceRequestQuotationItemByItemId(Long outsourceQuoteBargainItemSid);

    List<PurOutsourceQuoteBargainItem> selectPurOutsourceRequestQuotationItemList(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);
    /**
     * 加工询价议价明细报表
     */
    List<PurOutsourceQuoteBargainReportResponse> reportOutPurRequest(PurOutsourceQuotationRequest purOutsourceQuotationRequest);

   List<PurOutsourceQuoteBargainResponse> reportNew(PurOutsourceQuoteBargainRequest request);
    /**
     * 添加多个
     * @param list List PurOutsourceQuoteBargainItem
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceQuoteBargainItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourceQuoteBargainItem
    * @return int
    */
    int updateAllById(PurOutsourceQuoteBargainItem entity);

    /**
     * 更新多个
     * @param list List PurOutsourceQuoteBargainItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceQuoteBargainItem> list);

    /**
     * 查询加工采购价明细
     *
     * @param purOutsourceQuoteBargainItem purOutsourceQuoteBargainItem
     * @return int
     */
    List<PurOutsourcePurchasePriceItem> selectPriceItemList(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);

}
