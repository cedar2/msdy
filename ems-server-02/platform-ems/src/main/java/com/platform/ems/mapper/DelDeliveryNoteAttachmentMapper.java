package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelDeliveryNoteAttachment;

/**
 * 交货单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface DelDeliveryNoteAttachmentMapper  extends BaseMapper<DelDeliveryNoteAttachment> {


    DelDeliveryNoteAttachment selectDelDeliveryNoteAttachmentById(Long deliveryNoteAttachmentSid);

    List<DelDeliveryNoteAttachment> selectDelDeliveryNoteAttachmentList(DelDeliveryNoteAttachment delDeliveryNoteAttachment);

    /**
     * 添加多个
     * @param list List DelDeliveryNoteAttachment
     * @return int
     */
    int inserts(@Param("list") List<DelDeliveryNoteAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelDeliveryNoteAttachment
    * @return int
    */
    int updateAllById(DelDeliveryNoteAttachment entity);

    /**
     * 更新多个
     * @param list List DelDeliveryNoteAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<DelDeliveryNoteAttachment> list);


    void deleteDelDeliveryNoteAttachmentByIds(@Param("array") Long[] deliveryNoteSids);
}
