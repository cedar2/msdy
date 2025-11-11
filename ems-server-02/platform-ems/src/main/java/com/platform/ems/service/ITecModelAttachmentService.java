package com.platform.ems.service;

import java.util.List;
import com.platform.ems.domain.TecModelAttachment;

/**
 * 版型-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-01-31
 */
public interface ITecModelAttachmentService {
    /**
     * 查询版型-附件
     * 
     * @param clientId 版型-附件ID
     * @return 版型-附件
     */
    public TecModelAttachment selectTecModelAttachmentById(String clientId);

    /**
     * 查询版型-附件列表
     * 
     * @param tecModelAttachment 版型-附件
     * @return 版型-附件集合
     */
    public List<TecModelAttachment> selectTecModelAttachmentList(TecModelAttachment tecModelAttachment);

    /**
     * 新增版型-附件
     * 
     * @param tecModelAttachment 版型-附件
     * @return 结果
     */
    public int insertTecModelAttachment(TecModelAttachment tecModelAttachment);

    /**
     * 修改版型-附件
     * 
     * @param tecModelAttachment 版型-附件
     * @return 结果
     */
    public int updateTecModelAttachment(TecModelAttachment tecModelAttachment);

    /**
     * 批量删除版型-附件
     * 
     * @param clientIds 需要删除的版型-附件ID
     * @return 结果
     */
    public int deleteTecModelAttachmentByIds(String clientIds);

    /**
     * 删除版型-附件信息
     * 
     * @param clientId 版型-附件ID
     * @return 结果
     */
    public int deleteTecModelAttachmentById(String clientId);
}
