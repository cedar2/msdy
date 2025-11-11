package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.TecBomAttachment;
import com.platform.ems.mapper.TecBomAttachmentMapper;
import com.platform.ems.service.ITecBomAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * BOM附件Service业务层处理
 *
 * @author qhq
 * @date 2021-03-15
 */
@Service
@SuppressWarnings("all")
public class TecBomAttachmentServiceImpl extends ServiceImpl<TecBomAttachmentMapper,TecBomAttachment>  implements ITecBomAttachmentService {
    @Autowired
    private TecBomAttachmentMapper tecBomAttachmentMapper;

    /**
     * 查询BOM附件
     *
     * @param bomAttachmentSid BOM附件ID
     * @return BOM附件
     */
    @Override
    public TecBomAttachment selectTecBomAttachmentById(String bomAttachmentSid) {
        return tecBomAttachmentMapper.selectTecBomAttachmentById(bomAttachmentSid);
    }

    /**
     * 查询BOM附件列表
     *
     * @param tecBomAttachment BOM附件
     * @return BOM附件
     */
    @Override
    public List<TecBomAttachment> selectTecBomAttachmentList(TecBomAttachment tecBomAttachment) {
        return tecBomAttachmentMapper.selectTecBomAttachmentList(tecBomAttachment);
    }

    /**
     * 新增BOM附件
     * 需要注意编码重复校验
     * @param tecBomAttachment BOM附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecBomAttachment(TecBomAttachment tecBomAttachment) {
        return tecBomAttachmentMapper.insert(tecBomAttachment);
    }

    /**
     * 修改BOM附件
     *
     * @param tecBomAttachment BOM附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecBomAttachment(TecBomAttachment tecBomAttachment) {
        return tecBomAttachmentMapper.updateById(tecBomAttachment);
    }

    /**
     * 批量删除BOM附件
     *
     * @param bomAttachmentSids 需要删除的BOM附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecBomAttachmentByIds(List<String> bomAttachmentSids) {
        return tecBomAttachmentMapper.deleteBatchIds(bomAttachmentSids);
    }

    @Override
    public int deleteTecBomAttachmentById(String bomAttachmentSid) {
        return 0;
    }


}
