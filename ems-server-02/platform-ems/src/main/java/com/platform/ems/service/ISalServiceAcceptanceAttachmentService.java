package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalServiceAcceptanceAttachment;

/**
 * 服务销售验收单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface ISalServiceAcceptanceAttachmentService extends IService<SalServiceAcceptanceAttachment>{
    /**
     * 查询服务销售验收单-附件
     * 
     * @param serviceAcceptanceAttachmentSid 服务销售验收单-附件ID
     * @return 服务销售验收单-附件
     */
    public SalServiceAcceptanceAttachment selectSalServiceAcceptanceAttachmentById(Long serviceAcceptanceAttachmentSid);

    /**
     * 查询服务销售验收单-附件列表
     * 
     * @param salServiceAcceptanceAttachment 服务销售验收单-附件
     * @return 服务销售验收单-附件集合
     */
    public List<SalServiceAcceptanceAttachment> selectSalServiceAcceptanceAttachmentList(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment);

    /**
     * 新增服务销售验收单-附件
     * 
     * @param salServiceAcceptanceAttachment 服务销售验收单-附件
     * @return 结果
     */
    public int insertSalServiceAcceptanceAttachment(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment);

    /**
     * 修改服务销售验收单-附件
     * 
     * @param salServiceAcceptanceAttachment 服务销售验收单-附件
     * @return 结果
     */
    public int updateSalServiceAcceptanceAttachment(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment);

    /**
     * 批量删除服务销售验收单-附件
     * 
     * @param serviceAcceptanceAttachmentSids 需要删除的服务销售验收单-附件ID
     * @return 结果
     */
    public int deleteSalServiceAcceptanceAttachmentByIds(List<Long> serviceAcceptanceAttachmentSids);

}
