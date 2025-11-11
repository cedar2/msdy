package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookCustomerDeductionAttachment;

/**
 * 财务流水账-附件-客户扣款Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookCustomerDeductionAttachmentMapper  extends BaseMapper<FinBookCustomerDeductionAttachment> {


    FinBookCustomerDeductionAttachment selectFinBookCustomerDeductionAttachmentById(Long bookDeductionAttachmentSid);

    List<FinBookCustomerDeductionAttachment> selectFinBookCustomerDeductionAttachmentList(FinBookCustomerDeductionAttachment finBookCustomerDeductionAttachment);

    /**
     * 添加多个
     * @param list List FinBookCustomerDeductionAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerDeductionAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerDeductionAttachment
    * @return int
    */
    int updateAllById(FinBookCustomerDeductionAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookCustomerDeductionAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerDeductionAttachment> list);


}
