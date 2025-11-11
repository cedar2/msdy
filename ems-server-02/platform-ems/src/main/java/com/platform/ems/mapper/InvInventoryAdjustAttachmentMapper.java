package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryAdjustAttachment;

/**
 * 库存调整单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
public interface InvInventoryAdjustAttachmentMapper  extends BaseMapper<InvInventoryAdjustAttachment> {


    InvInventoryAdjustAttachment selectInvInventoryAdjustAttachmentById(Long inventoryAdjustAttachmentSid);

    List<InvInventoryAdjustAttachment> selectInvInventoryAdjustAttachmentList(InvInventoryAdjustAttachment invInventoryAdjustAttachment);

    /**
     * 添加多个
     * @param list List InvInventoryAdjustAttachment
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryAdjustAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryAdjustAttachment
    * @return int
    */
    int updateAllById(InvInventoryAdjustAttachment entity);

    /**
     * 更新多个
     * @param list List InvInventoryAdjustAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryAdjustAttachment> list);


    void deleteInvInventoryAdjustAttachmentByIds(@Param("array") Long[] inventoryAdjustSids);
}
