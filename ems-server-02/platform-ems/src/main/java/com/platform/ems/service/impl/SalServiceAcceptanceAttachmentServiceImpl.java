package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SalServiceAcceptanceAttachmentMapper;
import com.platform.ems.domain.SalServiceAcceptanceAttachment;
import com.platform.ems.service.ISalServiceAcceptanceAttachmentService;

/**
 * 服务销售验收单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
@Service
@SuppressWarnings("all")
public class SalServiceAcceptanceAttachmentServiceImpl extends ServiceImpl<SalServiceAcceptanceAttachmentMapper,SalServiceAcceptanceAttachment>  implements ISalServiceAcceptanceAttachmentService {
    @Autowired
    private SalServiceAcceptanceAttachmentMapper salServiceAcceptanceAttachmentMapper;

    /**
     * 查询服务销售验收单-附件
     * 
     * @param serviceAcceptanceAttachmentSid 服务销售验收单-附件ID
     * @return 服务销售验收单-附件
     */
    @Override
    public SalServiceAcceptanceAttachment selectSalServiceAcceptanceAttachmentById(Long serviceAcceptanceAttachmentSid) {
        return salServiceAcceptanceAttachmentMapper.selectSalServiceAcceptanceAttachmentById(serviceAcceptanceAttachmentSid);
    }

    /**
     * 查询服务销售验收单-附件列表
     * 
     * @param salServiceAcceptanceAttachment 服务销售验收单-附件
     * @return 服务销售验收单-附件
     */
    @Override
    public List<SalServiceAcceptanceAttachment> selectSalServiceAcceptanceAttachmentList(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment) {
        return salServiceAcceptanceAttachmentMapper.selectSalServiceAcceptanceAttachmentList(salServiceAcceptanceAttachment);
    }

    /**
     * 新增服务销售验收单-附件
     * 需要注意编码重复校验
     * @param salServiceAcceptanceAttachment 服务销售验收单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalServiceAcceptanceAttachment(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment) {
        return salServiceAcceptanceAttachmentMapper.insert(salServiceAcceptanceAttachment);
    }

    /**
     * 修改服务销售验收单-附件
     * 
     * @param salServiceAcceptanceAttachment 服务销售验收单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalServiceAcceptanceAttachment(SalServiceAcceptanceAttachment salServiceAcceptanceAttachment) {
        return salServiceAcceptanceAttachmentMapper.updateById(salServiceAcceptanceAttachment);
    }

    /**
     * 批量删除服务销售验收单-附件
     * 
     * @param serviceAcceptanceAttachmentSids 需要删除的服务销售验收单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalServiceAcceptanceAttachmentByIds(List<Long> serviceAcceptanceAttachmentSids) {
        return salServiceAcceptanceAttachmentMapper.deleteBatchIds(serviceAcceptanceAttachmentSids);
    }


}
