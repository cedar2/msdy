package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookCustomerAccountBalanceAttachment;

/**
 * 财务流水账-附件-客户账互抵Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-11
 */
public interface FinBookCustomerAccountBalanceAttachmentMapper  extends BaseMapper<FinBookCustomerAccountBalanceAttachment> {


    FinBookCustomerAccountBalanceAttachment selectFinBookCustomerAccountBalanceAttachmentById(Long bookAccountBalanceAttachmentSid);

    List<FinBookCustomerAccountBalanceAttachment> selectFinBookCustomerAccountBalanceAttachmentList(FinBookCustomerAccountBalanceAttachment finBookCustomerAccountBalanceAttachment);

    /**
     * 添加多个
     * @param list List FinBookCustomerAccountBalanceAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerAccountBalanceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerAccountBalanceAttachment
    * @return int
    */
    int updateAllById(FinBookCustomerAccountBalanceAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookCustomerAccountBalanceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerAccountBalanceAttachment> list);


}
