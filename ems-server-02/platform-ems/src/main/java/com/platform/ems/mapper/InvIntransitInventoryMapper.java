package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvIntransitInventory;

/**
 * 调拨在途库存Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-04
 */
public interface InvIntransitInventoryMapper  extends BaseMapper<InvIntransitInventory> {


    InvIntransitInventory selectInvIntransitInventoryById(Long intransitStockSid);

    List<InvIntransitInventory> selectInvIntransitInventoryList(InvIntransitInventory invIntransitInventory);

    /**
     * 添加多个
     * @param list List InvIntransitInventory
     * @return int
     */
    int inserts(@Param("list") List<InvIntransitInventory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvIntransitInventory
    * @return int
    */
    int updateAllById(InvIntransitInventory entity);

    /**
     * 更新多个
     * @param list List InvIntransitInventory
     * @return int
     */
    int updatesAllById(@Param("list") List<InvIntransitInventory> list);


}
