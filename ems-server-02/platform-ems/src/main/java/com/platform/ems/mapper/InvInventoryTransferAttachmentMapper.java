package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryTransferAttachment;

/**
 * 调拨单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-04
 */
public interface InvInventoryTransferAttachmentMapper  extends BaseMapper<InvInventoryTransferAttachment> {


    List<InvInventoryTransferAttachment>  selectInvInventoryTransferAttachmentById(Long inventoryTransferSid);

    List<InvInventoryTransferAttachment> selectInvInventoryTransferAttachmentList(InvInventoryTransferAttachment invInventoryTransferAttachment);

    /**
     * 添加多个
     * @param list List InvInventoryTransferAttachment
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryTransferAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryTransferAttachment
    * @return int
    */
    int updateAllById(InvInventoryTransferAttachment entity);

    /**
     * 更新多个
     * @param list List InvInventoryTransferAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryTransferAttachment> list);


}
