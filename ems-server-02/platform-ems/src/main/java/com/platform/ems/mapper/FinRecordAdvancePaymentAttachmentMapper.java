package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinRecordAdvancePaymentAttachment;

/**
 * 供应商业务台账-附件-预付Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-29
 */
public interface FinRecordAdvancePaymentAttachmentMapper  extends BaseMapper<FinRecordAdvancePaymentAttachment> {


    FinRecordAdvancePaymentAttachment selectFinRecordAdvancePaymentAttachmentById(Long recordAdvancePaymentAttachmentSid);

    List<FinRecordAdvancePaymentAttachment> selectFinRecordAdvancePaymentAttachmentList(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

    /**
     * 添加多个
     * @param list List FinRecordAdvancePaymentAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinRecordAdvancePaymentAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinRecordAdvancePaymentAttachment
    * @return int
    */
    int updateAllById(FinRecordAdvancePaymentAttachment entity);

    /**
     * 更新多个
     * @param list List FinRecordAdvancePaymentAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinRecordAdvancePaymentAttachment> list);


}
