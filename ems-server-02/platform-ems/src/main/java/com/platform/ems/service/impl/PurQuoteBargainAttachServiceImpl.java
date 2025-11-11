package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.PurQuoteBargainAttach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurQuoteBargainAttachMapper;
import com.platform.ems.service.IPurQuoteBargainAttachService;

/**
 * 询报议价单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class PurQuoteBargainAttachServiceImpl extends ServiceImpl<PurQuoteBargainAttachMapper, PurQuoteBargainAttach>  implements IPurQuoteBargainAttachService {
    @Autowired
    private PurQuoteBargainAttachMapper purQuoteBargainAttachMapper;

    /**
     * 查询询报议价单-附件
     * 
     * @param quoteBargainAttachSid 询报议价单-附件ID
     * @return 询报议价单-附件
     */
    @Override
    public PurQuoteBargainAttach selectPurRequestQuotationAttachmentById(Long quoteBargainAttachSid) {
        return purQuoteBargainAttachMapper.selectPurRequestQuotationAttachmentById(quoteBargainAttachSid);
    }

    /**
     * 查询询报议价单-附件列表
     * 
     * @param purQuoteBargainAttach 询报议价单-附件
     * @return 询报议价单-附件
     */
    @Override
    public List<PurQuoteBargainAttach> selectPurRequestQuotationAttachmentList(PurQuoteBargainAttach purQuoteBargainAttach) {
        return purQuoteBargainAttachMapper.selectPurRequestQuotationAttachmentList(purQuoteBargainAttach);
    }

    /**
     * 新增询报议价单-附件
     * 需要注意编码重复校验
     * @param purQuoteBargainAttach 询报议价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurRequestQuotationAttachment(PurQuoteBargainAttach purQuoteBargainAttach) {
        return purQuoteBargainAttachMapper.insert(purQuoteBargainAttach);
    }

    /**
     * 修改询报议价单-附件
     * 
     * @param purQuoteBargainAttach 询报议价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurRequestQuotationAttachment(PurQuoteBargainAttach purQuoteBargainAttach) {
        return purQuoteBargainAttachMapper.updateById(purQuoteBargainAttach);
    }

    /**
     * 批量删除询报议价单-附件
     * 
     * @param quoteBargainAttachSids 需要删除的询报议价单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurRequestQuotationAttachmentByIds(List<Long> quoteBargainAttachSids) {
        return purQuoteBargainAttachMapper.deleteBatchIds(quoteBargainAttachSids);
    }


}
