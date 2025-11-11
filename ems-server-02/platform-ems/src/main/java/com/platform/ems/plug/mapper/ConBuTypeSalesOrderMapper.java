package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDocTypeSalesOrder;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeSalesOrder;

/**
 * 业务类型_销售订单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeSalesOrderMapper  extends BaseMapper<ConBuTypeSalesOrder> {


    ConBuTypeSalesOrder selectConBuTypeSalesOrderById(Long sid);

    List<ConBuTypeSalesOrder> selectConBuTypeSalesOrderList(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**
     * 添加多个
     * @param list List ConBuTypeSalesOrder
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeSalesOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeSalesOrder
    * @return int
    */
    int updateAllById(ConBuTypeSalesOrder entity);

    /**
     * 更新多个
     * @param list List ConBuTypeSalesOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeSalesOrder> list);

    /** 获取下拉列表 */
    List<ConBuTypeSalesOrder> getConBuTypeSalesOrderList();

    /**  根据单据类型获取关联业务类型 */
    List<ConBuTypeSalesOrder> getList(ConDocTypeSalesOrder conDocTypeSalesOrder);
}
