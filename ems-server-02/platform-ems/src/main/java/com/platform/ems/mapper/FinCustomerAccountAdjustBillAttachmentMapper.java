package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerAccountAdjustBillAttachment;

/**
 * 客户调账单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-26
 */
public interface FinCustomerAccountAdjustBillAttachmentMapper  extends BaseMapper<FinCustomerAccountAdjustBillAttachment> {


    FinCustomerAccountAdjustBillAttachment selectFinCustomerAccountAdjustBillAttachmentById(Long adjustBillAttachmentSid);

    List<FinCustomerAccountAdjustBillAttachment> selectFinCustomerAccountAdjustBillAttachmentList(FinCustomerAccountAdjustBillAttachment finCustomerAccountAdjustBillAttachment);

    /**
     * 添加多个
     * @param list List FinCustomerAccountAdjustBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerAccountAdjustBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerAccountAdjustBillAttachment
    * @return int
    */
    int updateAllById(FinCustomerAccountAdjustBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinCustomerAccountAdjustBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerAccountAdjustBillAttachment> list);


}
