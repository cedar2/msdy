package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelOutsourceDeliveryNoteAttachment;

/**
 * 外发加工交货单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface DelOutsourceDeliveryNoteAttachmentMapper  extends BaseMapper<DelOutsourceDeliveryNoteAttachment> {


    DelOutsourceDeliveryNoteAttachment selectDelOutsourceDeliveryNoteAttachmentById(Long deliveryNoteAttachmentSid);

    List<DelOutsourceDeliveryNoteAttachment> selectDelOutsourceDeliveryNoteAttachmentList(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment);

    /**
     * 添加多个
     * @param list List DelOutsourceDeliveryNoteAttachment
     * @return int
     */
    int inserts(@Param("list") List<DelOutsourceDeliveryNoteAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelOutsourceDeliveryNoteAttachment
    * @return int
    */
    int updateAllById(DelOutsourceDeliveryNoteAttachment entity);

    /**
     * 更新多个
     * @param list List DelOutsourceDeliveryNoteAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<DelOutsourceDeliveryNoteAttachment> list);


    void deleteOutsourceDeliveryNoteAttachmentByIds(@Param("list") List<Long> outsourceDeliveryNoteSids);
}
