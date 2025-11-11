package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPriceInforItem;

/**
 * 采购价格记录明细(报价/核价/议价)Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-26
 */
public interface PurPriceInforItemMapper  extends BaseMapper<PurPriceInforItem> {


    PurPriceInforItem selectPurPriceInforItemById(Long priceInforItemSid);

    List<PurPriceInforItem> selectPurPriceInforItemList(PurPriceInforItem purPriceInforItem);

    /**
     * 添加多个
     * @param list List PurPriceInforItem
     * @return int
     */
    int inserts(@Param("list") List<PurPriceInforItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPriceInforItem
    * @return int
    */
    int updateAllById(PurPriceInforItem entity);

    /**
     * 更新多个
     * @param list List PurPriceInforItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPriceInforItem> list);

    /**
     * 根据物料sid及sku1sid查询报价及采购价
     * @param materialSid
     * @param sku1Sid
     * @return
     */
    PurPriceInforItem selectQuantityByMaterialAndSku(@Param("materialSid") Long materialSid,@Param("sku1Sid") Long sku1Sid);

    int deleteByPriceInfoSids(@Param("array")Long[] priceInforSids);

}
