package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorDeductionBillAttachment;

/**
 * 供应商扣款单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-31
 */
public interface FinVendorDeductionBillAttachmentMapper  extends BaseMapper<FinVendorDeductionBillAttachment> {


    FinVendorDeductionBillAttachment selectFinVendorDeductionBillAttachmentById(Long deductionBillAttachmentSid);

    List<FinVendorDeductionBillAttachment> selectFinVendorDeductionBillAttachmentList(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

    /**
     * 添加多个
     * @param list List FinVendorDeductionBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinVendorDeductionBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorDeductionBillAttachment
    * @return int
    */
    int updateAllById(FinVendorDeductionBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinVendorDeductionBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorDeductionBillAttachment> list);


}
