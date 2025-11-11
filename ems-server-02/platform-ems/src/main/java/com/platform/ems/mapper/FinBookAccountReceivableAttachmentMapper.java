package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookAccountReceivableAttachment;

/**
 * 财务流水账-附件-应收Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-11
 */
public interface FinBookAccountReceivableAttachmentMapper  extends BaseMapper<FinBookAccountReceivableAttachment> {


    FinBookAccountReceivableAttachment selectFinBookAccountReceivableAttachmentById(Long bookAccountReceivableAttachmentSid);

    List<FinBookAccountReceivableAttachment> selectFinBookAccountReceivableAttachmentList(FinBookAccountReceivableAttachment finBookAccountReceivableAttachment);

    /**
     * 添加多个
     * @param list List FinBookAccountReceivableAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinBookAccountReceivableAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookAccountReceivableAttachment
    * @return int
    */
    int updateAllById(FinBookAccountReceivableAttachment entity);

    /**
     * 更新多个
     * @param list List FinBookAccountReceivableAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookAccountReceivableAttachment> list);


}
