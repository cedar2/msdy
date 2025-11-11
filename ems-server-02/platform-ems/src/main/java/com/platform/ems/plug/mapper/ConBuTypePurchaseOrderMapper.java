package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypePurchaseOrder;

/**
 * 业务类型_采购订单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypePurchaseOrderMapper  extends BaseMapper<ConBuTypePurchaseOrder> {


    ConBuTypePurchaseOrder selectConBuTypePurchaseOrderById(Long sid);

    List<ConBuTypePurchaseOrder> selectConBuTypePurchaseOrderList(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 添加多个
     * @param list List ConBuTypePurchaseOrder
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypePurchaseOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypePurchaseOrder
    * @return int
    */
    int updateAllById(ConBuTypePurchaseOrder entity);

    /**
     * 更新多个
     * @param list List ConBuTypePurchaseOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypePurchaseOrder> list);

    /**
     * 业务类型_采购订单下拉框
     */
    List<ConBuTypePurchaseOrder> getList();

    List<ConBuTypePurchaseOrder> getRelevancyBuList(ConDocTypePurchaseOrder conDocTypePurchaseOrder);
}
