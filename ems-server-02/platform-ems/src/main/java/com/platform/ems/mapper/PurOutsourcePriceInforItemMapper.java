package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePriceInforItem;

/**
 * 加工采购价格记录明细(报价/核价/议价)Mapper接口
 *
 * @author c
 * @date 2022-04-01
 */
public interface PurOutsourcePriceInforItemMapper extends BaseMapper<PurOutsourcePriceInforItem> {


    PurOutsourcePriceInforItem selectPurOutsourcePriceInforItemById(Long outsourcePriceInforItemSid);

    List<PurOutsourcePriceInforItem> selectPurOutsourcePriceInforItemList(PurOutsourcePriceInforItem purOutsourcePriceInforItem);

    /**
     * 添加多个
     *
     * @param list List PurOutsourcePriceInforItem
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePriceInforItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurOutsourcePriceInforItem
     * @return int
     */
    int updateAllById(PurOutsourcePriceInforItem entity);

    /**
     * 更新多个
     *
     * @param list List PurOutsourcePriceInforItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePriceInforItem> list);


}
