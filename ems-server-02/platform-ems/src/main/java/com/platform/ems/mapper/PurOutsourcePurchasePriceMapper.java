package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.PurOutsourcePurchasePriceItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePurchasePrice;

/**
 * 加工采购价主Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-12
 */
public interface PurOutsourcePurchasePriceMapper  extends BaseMapper<PurOutsourcePurchasePrice> {


    PurOutsourcePurchasePrice selectPurOutsourcePurchasePriceById(Long outsourcePurchasePriceSid);
    //共价模板 获取成本价
    List<PurOutsourcePurchasePriceItem> getCostPrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    List<PurOutsourcePurchasePrice> selectPurOutsourcePurchasePriceList(PurOutsourcePurchasePrice purOutsourcePurchasePrice);

    /**
     * 添加多个
     * @param list List PurOutsourcePurchasePrice
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePurchasePrice> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourcePurchasePrice
    * @return int
    */
    int updateAllById(PurOutsourcePurchasePrice entity);

    /**
     * 更新多个
     * @param list List PurOutsourcePurchasePrice
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePurchasePrice> list);


}
