package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventorySheetAttachment;

/**
 * 盘点单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface InvInventorySheetAttachmentMapper  extends BaseMapper<InvInventorySheetAttachment> {


    InvInventorySheetAttachment selectInvInventorySheetAttachmentById(Long inventorySheetAttachmentSid);

    List<InvInventorySheetAttachment> selectInvInventorySheetAttachmentList(InvInventorySheetAttachment invInventorySheetAttachment);

    /**
     * 添加多个
     * @param list List InvInventorySheetAttachment
     * @return int
     */
    int inserts(@Param("list") List<InvInventorySheetAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventorySheetAttachment
    * @return int
    */
    int updateAllById(InvInventorySheetAttachment entity);

    /**
     * 更新多个
     * @param list List InvInventorySheetAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventorySheetAttachment> list);


    void deleteInvInventorySheetAttachmentByIds(@Param("array") Long[] inventorySheetSids);
}
