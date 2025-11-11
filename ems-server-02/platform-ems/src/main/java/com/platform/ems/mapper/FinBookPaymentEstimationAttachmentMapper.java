package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookPaymentEstimationAttachment;

/**
 * 财务流水账-附件-应付暂估Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-31
 */
public interface FinBookPaymentEstimationAttachmentMapper  extends BaseMapper<FinBookPaymentEstimationAttachment> {


    FinBookPaymentEstimationAttachment selectFinBookPaymentEstimationAttachmentById(Long bookPaymentEstimationAttachmentSid);

    List<FinBookPaymentEstimationAttachment> selectFinBookPaymentEstimationAttachmentList(FinBookPaymentEstimationAttachment finBookPaymentEstimationAttachment);

    /**
     * 添加多个
     * @param list List FinBookPaymentEstimationAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookPaymentEstimationAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookPaymentEstimationAttachment
    * @return int
    */
    int updateAllById(FinBookPaymentEstimationAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookPaymentEstimationAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookPaymentEstimationAttachment> list);


}
