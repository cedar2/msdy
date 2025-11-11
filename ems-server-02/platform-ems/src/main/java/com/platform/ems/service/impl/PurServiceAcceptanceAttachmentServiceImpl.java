package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurServiceAcceptanceAttachmentMapper;
import com.platform.ems.domain.PurServiceAcceptanceAttachment;
import com.platform.ems.service.IPurServiceAcceptanceAttachmentService;

/**
 * 服务采购验收单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
@Service
@SuppressWarnings("all")
public class PurServiceAcceptanceAttachmentServiceImpl extends ServiceImpl<PurServiceAcceptanceAttachmentMapper,PurServiceAcceptanceAttachment>  implements IPurServiceAcceptanceAttachmentService {
    @Autowired
    private PurServiceAcceptanceAttachmentMapper purServiceAcceptanceAttachmentMapper;

    /**
     * 查询服务采购验收单-附件
     * 
     * @param purchaseServiceAcceptanceAttachmentSid 服务采购验收单-附件ID
     * @return 服务采购验收单-附件
     */
    @Override
    public PurServiceAcceptanceAttachment selectPurServiceAcceptanceAttachmentById(Long purchaseServiceAcceptanceAttachmentSid) {
        return purServiceAcceptanceAttachmentMapper.selectPurServiceAcceptanceAttachmentById(purchaseServiceAcceptanceAttachmentSid);
    }

    /**
     * 查询服务采购验收单-附件列表
     * 
     * @param purServiceAcceptanceAttachment 服务采购验收单-附件
     * @return 服务采购验收单-附件
     */
    @Override
    public List<PurServiceAcceptanceAttachment> selectPurServiceAcceptanceAttachmentList(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment) {
        return purServiceAcceptanceAttachmentMapper.selectPurServiceAcceptanceAttachmentList(purServiceAcceptanceAttachment);
    }

    /**
     * 新增服务采购验收单-附件
     * 需要注意编码重复校验
     * @param purServiceAcceptanceAttachment 服务采购验收单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurServiceAcceptanceAttachment(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment) {
        return purServiceAcceptanceAttachmentMapper.insert(purServiceAcceptanceAttachment);
    }

    /**
     * 修改服务采购验收单-附件
     * 
     * @param purServiceAcceptanceAttachment 服务采购验收单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurServiceAcceptanceAttachment(PurServiceAcceptanceAttachment purServiceAcceptanceAttachment) {
        return purServiceAcceptanceAttachmentMapper.updateById(purServiceAcceptanceAttachment);
    }

    /**
     * 批量删除服务采购验收单-附件
     * 
     * @param purchaseServiceAcceptanceAttachmentSids 需要删除的服务采购验收单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurServiceAcceptanceAttachmentByIds(List<Long> purchaseServiceAcceptanceAttachmentSids) {
        return purServiceAcceptanceAttachmentMapper.deleteBatchIds(purchaseServiceAcceptanceAttachmentSids);
    }


}
