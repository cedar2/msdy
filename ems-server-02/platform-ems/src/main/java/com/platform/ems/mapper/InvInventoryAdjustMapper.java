package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryAdjust;

/**
 * 库存调整单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
public interface InvInventoryAdjustMapper  extends BaseMapper<InvInventoryAdjust> {


    InvInventoryAdjust selectInvInventoryAdjustById(Long inventoryAdjustSid);

    List<InvInventoryAdjust> selectInvInventoryAdjustList(InvInventoryAdjust invInventoryAdjust);

    /**
     * 添加多个
     * @param list List InvInventoryAdjust
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryAdjust
    * @return int
    */
    int updateAllById(InvInventoryAdjust entity);

    /**
     * 更新多个
     * @param list List InvInventoryAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryAdjust> list);


    int countByDomain(InvInventoryAdjust params);

    int deleteInvInventoryAdjustByIds(@Param("array") Long[] inventoryAdjustSids);

    int confirm(InvInventoryAdjust invInventoryAdjust);
}
