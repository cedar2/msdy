package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryTransfer;

/**
 * 调拨单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-04
 */
public interface InvInventoryTransferMapper  extends BaseMapper<InvInventoryTransfer> {


    InvInventoryTransfer selectInvInventoryTransferById(Long inventoryTransferSid);

    List<InvInventoryTransfer> selectInvInventoryTransferList(InvInventoryTransfer invInventoryTransfer);


    /**
     * 添加多个
     * @param list List InvInventoryTransfer
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryTransfer> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryTransfer
    * @return int
    */
    int updateAllById(InvInventoryTransfer entity);

    /**
     * 更新多个
     * @param list List InvInventoryTransfer
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryTransfer> list);


}
