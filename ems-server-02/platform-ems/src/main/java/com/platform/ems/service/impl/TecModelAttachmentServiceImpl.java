package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.TecModelAttachmentMapper;
import com.platform.ems.domain.TecModelAttachment;
import com.platform.ems.service.ITecModelAttachmentService;

/**
 * 版型-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-01-31
 */
@Service
public class TecModelAttachmentServiceImpl implements ITecModelAttachmentService {
    @Autowired
    private TecModelAttachmentMapper tecModelAttachmentMapper;

    /**
     * 查询版型-附件
     * 
     * @param clientId 版型-附件ID
     * @return 版型-附件
     */
    @Override
    public TecModelAttachment selectTecModelAttachmentById(String clientId) {
        return tecModelAttachmentMapper.selectTecModelAttachmentById(clientId);
    }

    /**
     * 查询版型-附件列表
     * 
     * @param tecModelAttachment 版型-附件
     * @return 版型-附件
     */
    @Override
    public List<TecModelAttachment> selectTecModelAttachmentList(TecModelAttachment tecModelAttachment) {
        return tecModelAttachmentMapper.selectTecModelAttachmentList(tecModelAttachment);
    }

    /**
     * 新增版型-附件
     * 
     * @param request 版型-附件
     * @return 结果
     */
    @Override
    public int insertTecModelAttachment(TecModelAttachment request) {
        return tecModelAttachmentMapper.insert(request);
    }

    /**
     * 修改版型-附件
     * 
     * @param tecModelAttachment 版型-附件
     * @return 结果
     */
    @Override
    public int updateTecModelAttachment(TecModelAttachment tecModelAttachment) {
        return tecModelAttachmentMapper.updateTecModelAttachment(tecModelAttachment);
    }

    /**
     * 批量删除版型-附件
     * 
     * @param modelAttachmentSid 需要删除的版型-附件ID
     * @return 结果
     */
    @Override
    public int deleteTecModelAttachmentByIds(String modelAttachmentSid) {
        return tecModelAttachmentMapper.deleteTecModelAttachmentByIds(modelAttachmentSid);
    }

    /**
     * 删除版型-附件信息
     * 
     * @param clientId 版型-附件ID
     * @return 结果
     */
    @Override
    public int deleteTecModelAttachmentById(String clientId) {
        return tecModelAttachmentMapper.deleteTecModelAttachmentById(clientId);
    }
}
