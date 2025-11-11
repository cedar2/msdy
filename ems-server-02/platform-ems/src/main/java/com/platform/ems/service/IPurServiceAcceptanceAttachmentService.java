package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurServiceAcceptanceAttachment;

/**
 * 服务采购验收单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
public interface IPurServiceAcceptanceAttachmentService extends IService<PurServiceAcceptanceAttachment>{
    /**
     * 查询服务采购验收单-附件
     * 
     * @param purchaseServiceAcceptanceAttachmentSid 服务采购验收单-附件ID
     * @return 服务采购验收单-附件
     */
    public PurServiceAcceptanceAttachment selectPurServiceAcceptanceAttachmentById(Long purchaseServiceAcceptanceAttachmentSid);

    /**
     * 查询服务采购验收单-附件列表
     * 
     * @param purServiceAcceptanceAttachment 服务采购验收单-附件
     * @return 服务采购验收单-附件集合
     */
    public List<PurServiceAcceptanceAttachment> selectPurServiceAcceptanceAttachmentList(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment);

    /**
     * 新增服务采购验收单-附件
     * 
     * @param purServiceAcceptanceAttachment 服务采购验收单-附件
     * @return 结果
     */
    public int insertPurServiceAcceptanceAttachment(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment);

    /**
     * 修改服务采购验收单-附件
     * 
     * @param purServiceAcceptanceAttachment 服务采购验收单-附件
     * @return 结果
     */
    public int updatePurServiceAcceptanceAttachment(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment);

    /**
     * 批量删除服务采购验收单-附件
     * 
     * @param purchaseServiceAcceptanceAttachmentSids 需要删除的服务采购验收单-附件ID
     * @return 结果
     */
    public int deletePurServiceAcceptanceAttachmentByIds(List<Long> purchaseServiceAcceptanceAttachmentSids);

}
