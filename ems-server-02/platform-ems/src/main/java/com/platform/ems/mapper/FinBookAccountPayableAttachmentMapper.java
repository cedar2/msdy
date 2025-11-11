package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookAccountPayableAttachment;

/**
 * 财务流水账-附件-应付Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-03
 */
public interface FinBookAccountPayableAttachmentMapper  extends BaseMapper<FinBookAccountPayableAttachment> {


    FinBookAccountPayableAttachment selectFinBookAccountPayableAttachmentById(Long bookAccountPayableAttachmentSid);

    List<FinBookAccountPayableAttachment> selectFinBookAccountPayableAttachmentList(FinBookAccountPayableAttachment finBookAccountPayableAttachment);

    /**
     * 添加多个
     * @param list List FinBookAccountPayableAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookAccountPayableAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookAccountPayableAttachment
    * @return int
    */
    int updateAllById(FinBookAccountPayableAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookAccountPayableAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookAccountPayableAttachment> list);


}
