package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecBomAttachment;

import java.util.List;

/**
 * BOM附件Service接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface ITecBomAttachmentService extends IService<TecBomAttachment>{
    /**
     * 查询BOM附件
     *
     * @param bomAttachmentSid BOM附件ID
     * @return BOM附件
     */
    public TecBomAttachment selectTecBomAttachmentById(String bomAttachmentSid);

    /**
     * 查询BOM附件列表
     *
     * @param tecBomAttachment BOM附件
     * @return BOM附件集合
     */
    public List<TecBomAttachment> selectTecBomAttachmentList(TecBomAttachment tecBomAttachment);

    /**
     * 新增BOM附件
     *
     * @param tecBomAttachment BOM附件
     * @return 结果
     */
    public int insertTecBomAttachment(TecBomAttachment tecBomAttachment);

    /**
     * 修改BOM附件
     *
     * @param tecBomAttachment BOM附件
     * @return 结果
     */
    public int updateTecBomAttachment(TecBomAttachment tecBomAttachment);

    /**
     * 批量删除BOM附件
     *
     * @param bomAttachmentSids 需要删除的BOM附件ID
     * @return 结果
     */
    public int deleteTecBomAttachmentByIds(List<String>  bomAttachmentSids);

    /**
     * 删除BOM附件信息
     *
     * @param bomAttachmentSid BOM附件ID
     * @return 结果
     */
    public int deleteTecBomAttachmentById(String bomAttachmentSid);
}
