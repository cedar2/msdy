package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerAccountBalanceBillAttachment;

/**
 * 客户账互抵单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface FinCustomerAccountBalanceBillAttachmentMapper  extends BaseMapper<FinCustomerAccountBalanceBillAttachment> {


    FinCustomerAccountBalanceBillAttachment selectFinCustomerAccountBalanceBillAttachmentById(Long customerAccountBalanceBillAttachmentSid);

    List<FinCustomerAccountBalanceBillAttachment> selectFinCustomerAccountBalanceBillAttachmentList(FinCustomerAccountBalanceBillAttachment finCustomerAccountBalanceBillAttachment);

    /**
     * 添加多个
     * @param list List FinCustomerAccountBalanceBillAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerAccountBalanceBillAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerAccountBalanceBillAttachment
    * @return int
    */
    int updateAllById(FinCustomerAccountBalanceBillAttachment entity);

    /**
     * 更新多个
     * @param list List FinCustomerAccountBalanceBillAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerAccountBalanceBillAttachment> list);


}
