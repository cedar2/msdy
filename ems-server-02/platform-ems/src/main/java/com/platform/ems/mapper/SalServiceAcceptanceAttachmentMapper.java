package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalServiceAcceptanceAttachment;

/**
 * 服务销售验收单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface SalServiceAcceptanceAttachmentMapper  extends BaseMapper<SalServiceAcceptanceAttachment> {


    SalServiceAcceptanceAttachment selectSalServiceAcceptanceAttachmentById(Long serviceAcceptanceAttachmentSid);

    List<SalServiceAcceptanceAttachment> selectSalServiceAcceptanceAttachmentList(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment);

    /**
     * 添加多个
     * @param list List SalServiceAcceptanceAttachment
     * @return int
     */
    int inserts(@Param("list") List<SalServiceAcceptanceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalServiceAcceptanceAttachment
    * @return int
    */
    int updateAllById(SalServiceAcceptanceAttachment entity);

    /**
     * 更新多个
     * @param list List SalServiceAcceptanceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<SalServiceAcceptanceAttachment> list);


    void deleteServiceAcceptanceAttachmentByIds(@Param("array")Long[] serviceAcceptanceSids);
}
