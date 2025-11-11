package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.InvInventoryLocation;
import com.platform.ems.domain.InvInventorySheet;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvVenSpecialInventory;

/**
 * 供应商特殊库存（寄售/甲供料）Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvVenSpecialInventoryMapper  extends BaseMapper<InvVenSpecialInventory> {

    List<InvVenSpecialInventory>  getInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory);
    InvVenSpecialInventory selectInvVenSpecialInventoryById(Long vendorSpecialStockSid);
    InvInventoryLocation getLocationAble(InvInventoryLocation invInventoryLocation);
    List<InvVenSpecialInventory> selectInvVenSpecialInventoryList(InvVenSpecialInventory invVenSpecialInventory);

    /**
     * 添加多个
     * @param list List InvVenSpecialInventory
     * @return int
     */
    int inserts(@Param("list") List<InvVenSpecialInventory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvVenSpecialInventory
    * @return int
    */
    int updateAllById(InvVenSpecialInventory entity);

    /**
     * 更新多个
     * @param list List InvVenSpecialInventory
     * @return int
     */
    int updatesAllById(@Param("list") List<InvVenSpecialInventory> list);


}
