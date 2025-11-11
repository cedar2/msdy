package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookReceiptEstimationAttachment;

/**
 * 财务流水账-附件-应收暂估Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookReceiptEstimationAttachmentMapper  extends BaseMapper<FinBookReceiptEstimationAttachment> {


    FinBookReceiptEstimationAttachment selectFinBookReceiptEstimationAttachmentById(Long bookReceiptEstimationAttachmentSid);

    List<FinBookReceiptEstimationAttachment> selectFinBookReceiptEstimationAttachmentList(FinBookReceiptEstimationAttachment finBookReceiptEstimationAttachment);

    /**
     * 添加多个
     * @param list List FinBookReceiptEstimationAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookReceiptEstimationAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookReceiptEstimationAttachment
    * @return int
    */
    int updateAllById(FinBookReceiptEstimationAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookReceiptEstimationAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookReceiptEstimationAttachment> list);


}
