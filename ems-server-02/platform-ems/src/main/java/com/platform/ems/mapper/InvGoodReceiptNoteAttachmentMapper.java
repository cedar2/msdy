package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvGoodReceiptNoteAttachment;

/**
 * 收货单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvGoodReceiptNoteAttachmentMapper  extends BaseMapper<InvGoodReceiptNoteAttachment> {


    List<InvGoodReceiptNoteAttachment> selectInvGoodReceiptNoteAttachmentById(Long goodReceiptNoteSid);

    List<InvGoodReceiptNoteAttachment> selectInvGoodReceiptNoteAttachmentList(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

    /**
     * 添加多个
     * @param list List InvGoodReceiptNoteAttachment
     * @return int
     */
    int inserts(@Param("list") List<InvGoodReceiptNoteAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvGoodReceiptNoteAttachment
    * @return int
    */
    int updateAllById(InvGoodReceiptNoteAttachment entity);

    /**
     * 更新多个
     * @param list List InvGoodReceiptNoteAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<InvGoodReceiptNoteAttachment> list);


}
