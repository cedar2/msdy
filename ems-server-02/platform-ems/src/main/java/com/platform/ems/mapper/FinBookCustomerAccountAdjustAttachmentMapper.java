package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookCustomerAccountAdjustAttachment;

/**
 * 财务流水账-附件-客户调账Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookCustomerAccountAdjustAttachmentMapper  extends BaseMapper<FinBookCustomerAccountAdjustAttachment> {


    FinBookCustomerAccountAdjustAttachment selectFinBookCustomerAccountAdjustAttachmentById(Long bookAccountAdjustAttachmentSid);

    List<FinBookCustomerAccountAdjustAttachment> selectFinBookCustomerAccountAdjustAttachmentList(FinBookCustomerAccountAdjustAttachment finBookCustomerAccountAdjustAttachment);

    /**
     * 添加多个
     * @param list List FinBookCustomerAccountAdjustAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerAccountAdjustAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerAccountAdjustAttachment
    * @return int
    */
    int updateAllById(FinBookCustomerAccountAdjustAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookCustomerAccountAdjustAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerAccountAdjustAttachment> list);


}
