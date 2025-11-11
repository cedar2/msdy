package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookVendorDeductionAttachment;

/**
 * 财务流水账-附件-供应商扣款Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-02
 */
public interface FinBookVendorDeductionAttachmentMapper  extends BaseMapper<FinBookVendorDeductionAttachment> {


    FinBookVendorDeductionAttachment selectFinBookVendorDeductionAttachmentById(Long bookDeductionAttachmentSid);

    List<FinBookVendorDeductionAttachment> selectFinBookVendorDeductionAttachmentList(FinBookVendorDeductionAttachment finBookVendorDeductionAttachment);

    /**
     * 添加多个
     * @param list List FinBookVendorDeductionAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorDeductionAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorDeductionAttachment
    * @return int
    */
    int updateAllById(FinBookVendorDeductionAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookVendorDeductionAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorDeductionAttachment> list);


}
