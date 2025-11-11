package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeManufactureOrder;

/**
 * 业务类型_生产订单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeManufactureOrderMapper  extends BaseMapper<ConBuTypeManufactureOrder> {


    ConBuTypeManufactureOrder selectConBuTypeManufactureOrderById(Long sid);

    List<ConBuTypeManufactureOrder> selectConBuTypeManufactureOrderList(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

    /**
     * 添加多个
     * @param list List ConBuTypeManufactureOrder
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeManufactureOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeManufactureOrder
    * @return int
    */
    int updateAllById(ConBuTypeManufactureOrder entity);

    /**
     * 更新多个
     * @param list List ConBuTypeManufactureOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeManufactureOrder> list);


}
