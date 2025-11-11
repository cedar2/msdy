package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConSaleChannel;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;

/**
 * 单据类型_销售订单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeSalesOrderMapper  extends BaseMapper<ConDocTypeSalesOrder> {


    ConDocTypeSalesOrder selectConDocTypeSalesOrderById(Long sid);

    List<ConDocTypeSalesOrder> selectConDocTypeSalesOrderList(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**
     * 添加多个
     * @param list List ConDocTypeSalesOrder
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeSalesOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeSalesOrder
    * @return int
    */
    int updateAllById(ConDocTypeSalesOrder entity);

    /**
     * 更新多个
     * @param list List ConDocTypeSalesOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeSalesOrder> list);

    /** 获取下拉列表 */
    List<ConDocTypeSalesOrder> getConDocTypeSalesOrderList();

    List<ConDocTypeSalesOrder> getList(ConDocTypeSalesOrder conDocTypeSalesOrder);
}
