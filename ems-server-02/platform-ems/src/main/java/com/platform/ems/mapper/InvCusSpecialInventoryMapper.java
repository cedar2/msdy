package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.InvInventoryLocation;
import com.platform.ems.domain.InvInventorySheet;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvCusSpecialInventory;

/**
 * 客户特殊库存（寄售/客供料）Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvCusSpecialInventoryMapper  extends BaseMapper<InvCusSpecialInventory> {

    List<InvCusSpecialInventory> getInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory);
    InvCusSpecialInventory selectInvCusSpecialInventoryById(Long customerSpecialStockSid);
    InvInventoryLocation getLocationAble(InvInventoryLocation invInventoryLocation);
    List<InvCusSpecialInventory> selectInvCusSpecialInventoryList(InvCusSpecialInventory invCusSpecialInventory);

    /**
     * 添加多个
     * @param list List InvCusSpecialInventory
     * @return int
     */
    int inserts(@Param("list") List<InvCusSpecialInventory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvCusSpecialInventory
    * @return int
    */
    int updateAllById(InvCusSpecialInventory entity);

    /**
     * 更新多个
     * @param list List InvCusSpecialInventory
     * @return int
     */
    int updatesAllById(@Param("list") List<InvCusSpecialInventory> list);


}
