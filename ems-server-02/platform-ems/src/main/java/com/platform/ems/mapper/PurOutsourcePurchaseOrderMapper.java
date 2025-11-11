package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePurchaseOrder;

/**
 * 外发加工单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface PurOutsourcePurchaseOrderMapper  extends BaseMapper<PurOutsourcePurchaseOrder> {


    PurOutsourcePurchaseOrder selectPurOutsourcePurchaseOrderById(Long outsourcePurchaseOrderSid);

    List<PurOutsourcePurchaseOrder> selectPurOutsourcePurchaseOrderList(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder);

    /**
     * 添加多个
     * @param list List PurOutsourcePurchaseOrder
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePurchaseOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourcePurchaseOrder
    * @return int
    */
    int updateAllById(PurOutsourcePurchaseOrder entity);

    /**
     * 更新多个
     * @param list List PurOutsourcePurchaseOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePurchaseOrder> list);


    int countByDomain(PurOutsourcePurchaseOrder params);
}
