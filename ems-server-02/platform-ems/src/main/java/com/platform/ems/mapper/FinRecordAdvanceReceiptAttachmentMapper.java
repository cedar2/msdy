package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinRecordAdvanceReceiptAttachment;

/**
 * 客户业务台账-附件-预收Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-16
 */
public interface FinRecordAdvanceReceiptAttachmentMapper  extends BaseMapper<FinRecordAdvanceReceiptAttachment> {


    FinRecordAdvanceReceiptAttachment selectFinRecordAdvanceReceiptAttachmentById(Long recordAdvanceReceiptAttachmentSid);

    List<FinRecordAdvanceReceiptAttachment> selectFinRecordAdvanceReceiptAttachmentList(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

    /**
     * 添加多个
     * @param list List FinRecordAdvanceReceiptAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinRecordAdvanceReceiptAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinRecordAdvanceReceiptAttachment
    * @return int
    */
    int updateAllById(FinRecordAdvanceReceiptAttachment entity);

    /**
     * 更新多个
     * @param list List FinRecordAdvanceReceiptAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinRecordAdvanceReceiptAttachment> list);


}
