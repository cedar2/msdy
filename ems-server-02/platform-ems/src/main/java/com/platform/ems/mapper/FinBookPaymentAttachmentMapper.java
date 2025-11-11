package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookPaymentAttachment;

/**
 * 财务流水账-附件-付款Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-07
 */
public interface FinBookPaymentAttachmentMapper  extends BaseMapper<FinBookPaymentAttachment> {


    FinBookPaymentAttachment selectFinBookPaymentAttachmentById(Long bookPaymentAttachmentSid);

    List<FinBookPaymentAttachment> selectFinBookPaymentAttachmentList(FinBookPaymentAttachment finBookPaymentAttachment);

    /**
     * 添加多个
     * @param list List FinBookPaymentAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookPaymentAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookPaymentAttachment
    * @return int
    */
    int updateAllById(FinBookPaymentAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookPaymentAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookPaymentAttachment> list);


}
