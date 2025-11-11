package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryDocumentAttach;

/**
 * 库存凭证-附件Mapper接口
 * 
 * @author c
 * @date 2021-09-22
 */
public interface InvInventoryDocumentAttachMapper  extends BaseMapper<InvInventoryDocumentAttach> {


    InvInventoryDocumentAttach selectInvInventoryDocumentAttachById(Long inventoryDocumentAttachSid);

    List<InvInventoryDocumentAttach> selectInvInventoryDocumentAttachList(InvInventoryDocumentAttach invInventoryDocumentAttach);

    /**
     * 添加多个
     * @param list List InvInventoryDocumentAttach
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryDocumentAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryDocumentAttach
    * @return int
    */
    int updateAllById(InvInventoryDocumentAttach entity);

    /**
     * 更新多个
     * @param list List InvInventoryDocumentAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryDocumentAttach> list);


}
