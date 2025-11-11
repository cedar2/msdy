package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurServiceAcceptanceAttachment;

/**
 * 服务采购验收单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
public interface PurServiceAcceptanceAttachmentMapper  extends BaseMapper<PurServiceAcceptanceAttachment> {


    PurServiceAcceptanceAttachment selectPurServiceAcceptanceAttachmentById(Long serviceAcceptanceAttachmentSid);

    List<PurServiceAcceptanceAttachment> selectPurServiceAcceptanceAttachmentList(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment);

    /**
     * 添加多个
     * @param list List PurServiceAcceptanceAttachment
     * @return int
     */
    int inserts(@Param("list") List<PurServiceAcceptanceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurServiceAcceptanceAttachment
    * @return int
    */
    int updateAllById(PurServiceAcceptanceAttachment entity);

    /**
     * 更新多个
     * @param list List PurServiceAcceptanceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<PurServiceAcceptanceAttachment> list);


    void deletePurServiceAcceptanceAttachmentByIds(@Param("array")Long[] serviceAcceptanceSids);
}
