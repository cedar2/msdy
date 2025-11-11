package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;

/**
 * 单据类型_采购订单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypePurchaseOrderMapper  extends BaseMapper<ConDocTypePurchaseOrder> {


    ConDocTypePurchaseOrder selectConDocTypePurchaseOrderById(Long sid);

    List<ConDocTypePurchaseOrder> selectConDocTypePurchaseOrderList(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 添加多个
     * @param list List ConDocTypePurchaseOrder
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypePurchaseOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypePurchaseOrder
    * @return int
    */
    int updateAllById(ConDocTypePurchaseOrder entity);

    /**
     * 更新多个
     * @param list List ConDocTypePurchaseOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypePurchaseOrder> list);

    /**
     * 单据类型_采购订单下拉框
     */
    List<ConDocTypePurchaseOrder> getList();

    /**
     * 单据类型_采购订单下拉框(有参数)
     */
    List<ConDocTypePurchaseOrder> getDocList(ConDocTypePurchaseOrder conDocTypePurchaseOrder);
}
