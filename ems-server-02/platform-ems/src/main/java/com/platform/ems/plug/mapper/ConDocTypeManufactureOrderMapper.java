package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeManufactureOrder;

/**
 * 单据类型_生产订单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeManufactureOrderMapper  extends BaseMapper<ConDocTypeManufactureOrder> {


    ConDocTypeManufactureOrder selectConDocTypeManufactureOrderById(Long sid);

    List<ConDocTypeManufactureOrder> selectConDocTypeManufactureOrderList(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 添加多个
     * @param list List ConDocTypeManufactureOrder
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeManufactureOrder> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeManufactureOrder
    * @return int
    */
    int updateAllById(ConDocTypeManufactureOrder entity);

    /**
     * 更新多个
     * @param list List ConDocTypeManufactureOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeManufactureOrder> list);

    /**
     * 单据类型_生产订单下拉框接口
     */
    List<ConDocTypeManufactureOrder> getList();
}
