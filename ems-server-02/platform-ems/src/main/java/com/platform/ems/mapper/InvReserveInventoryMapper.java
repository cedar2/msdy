package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvReserveInventoryRequest;
import com.platform.ems.domain.dto.response.InvReserveInventoryResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvReserveInventory;

/**
 * 预留库存Mapper接口
 * 
 * @author linhongwei
 * @date 2022-04-01
 */
public interface InvReserveInventoryMapper  extends BaseMapper<InvReserveInventory> {


    InvReserveInventory selectInvReserveInventoryById(Long reserveStockSid);
;
    List<InvReserveInventoryResponse> report(InvReserveInventoryRequest request);


    /**
     * 添加多个
     * @param list List InvReserveInventory
     * @return int
     */
    int inserts(@Param("list") List<InvReserveInventory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvReserveInventory
    * @return int
    */
    int updateAllById(InvReserveInventory entity);

    /**
     * 更新多个
     * @param list List InvReserveInventory
     * @return int
     */
    int updatesAllById(@Param("list") List<InvReserveInventory> list);


}
