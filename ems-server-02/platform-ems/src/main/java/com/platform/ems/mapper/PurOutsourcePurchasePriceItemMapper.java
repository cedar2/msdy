package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import com.platform.ems.domain.dto.response.PurOutsourceReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePurchasePriceItem;

/**
 * 加工采购价明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-12
 */
public interface PurOutsourcePurchasePriceItemMapper  extends BaseMapper<PurOutsourcePurchasePriceItem> {


    PurOutsourcePurchasePriceItem selectPurOutsourcePurchasePriceItemById(Long outsourcePurchasePriceItemSid);

    List<PurOutsourcePurchasePriceItem> selectPurOutsourcePurchasePriceItemList(PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem);

    List<PurOutsourceReportResponse> reportPurOutsourcePurchasePrice(PurOutsourceReportResponse purOutsourceReportResponse);
    /**
     * 添加多个
     * @param list List PurOutsourcePurchasePriceItem
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePurchasePriceItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourcePurchasePriceItem
    * @return int
    */
    int updateAllById(PurOutsourcePurchasePriceItem entity);

    /**
     * 更新多个
     * @param list List PurOutsourcePurchasePriceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePurchasePriceItem> list);


}
