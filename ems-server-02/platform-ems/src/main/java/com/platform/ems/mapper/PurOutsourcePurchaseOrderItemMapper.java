package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePurchaseOrderItem;

/**
 * 外发加工单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface PurOutsourcePurchaseOrderItemMapper  extends BaseMapper<PurOutsourcePurchaseOrderItem> {


    PurOutsourcePurchaseOrderItem selectPurOutsourcePurchaseOrderItemById(Long outsourcePurchaseOrderItemSid);

    List<PurOutsourcePurchaseOrderItem> selectPurOutsourcePurchaseOrderItemList(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);

    /**
     * 添加多个
     * @param list List PurOutsourcePurchaseOrderItem
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePurchaseOrderItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourcePurchaseOrderItem
    * @return int
    */
    int updateAllById(PurOutsourcePurchaseOrderItem entity);

    /**
     * 更新多个
     * @param list List PurOutsourcePurchaseOrderItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePurchaseOrderItem> list);


    void deleteOutsourcePurchaseOrderItemByIds(@Param("list") List<Long> outsourcePurchaseOrderSids);

    /**
     * 外发加工单明细报表
     */
    List<PurOutsourcePurchaseOrderItem> getItemList(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);
}
