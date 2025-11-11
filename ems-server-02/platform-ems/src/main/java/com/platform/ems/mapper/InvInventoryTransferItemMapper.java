package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvInventoryTransferRequest;
import com.platform.ems.domain.dto.response.InvInventoryTransferResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryTransferItem;

/**
 * 调拨单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-04
 */
public interface InvInventoryTransferItemMapper  extends BaseMapper<InvInventoryTransferItem> {


    List<InvInventoryTransferItem>  selectInvInventoryTransferItemById(Long inventoryTransferItemSid);

    List<InvInventoryTransferItem> selectInvInventoryTransferItemList(InvInventoryTransferItem invInventoryTransferItem);

    /**
     * 获取调拨单明细报表
     */
    List<InvInventoryTransferResponse> reportInvInventoryTransfer(InvInventoryTransferRequest invInventoryTransferRequest);

    /**
     * 添加多个
     * @param list List InvInventoryTransferItem
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryTransferItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryTransferItem
    * @return int
    */
    int updateAllById(InvInventoryTransferItem entity);

    /**
     * 更新多个
     * @param list List InvInventoryTransferItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryTransferItem> list);


}
