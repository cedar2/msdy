package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerDeductionBillAttachment;

/**
 * 客户扣款单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinCustomerDeductionBillAttachmentMapper  extends BaseMapper<FinCustomerDeductionBillAttachment> {


    FinCustomerDeductionBillAttachment selectFinCustomerDeductionBillAttachmentById(Long deductionBillAttachmentSid);

    List<FinCustomerDeductionBillAttachment> selectFinCustomerDeductionBillAttachmentList(FinCustomerDeductionBillAttachment finCustomerDeductionBillAttachment);

    /**
     * 添加多个
     * @param list List FinCustomerDeductionBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerDeductionBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerDeductionBillAttachment
    * @return int
    */
    int updateAllById(FinCustomerDeductionBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinCustomerDeductionBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerDeductionBillAttachment> list);


}
