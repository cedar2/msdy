package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysDocument;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 文档管理Service接口
 *
 * @author chenkw
 * @date 2023-02-13
 */
public interface ISysDocumentService extends IService<SysDocument> {
    /**
     * 查询文档管理
     *
     * @param documentSid 文档管理ID
     * @return 文档管理
     */
    public SysDocument selectSysDocumentById(Long documentSid);

    /**
     * 查询文档管理列表
     *
     * @param sysDocument 文档管理
     * @return 文档管理集合
     */
    public List<SysDocument> selectSysDocumentList(SysDocument sysDocument);

    /**
     * 新增文档管理
     *
     * @param sysDocument 文档管理
     * @return 结果
     */
    public EmsResultEntity insertSysDocument(SysDocument sysDocument);

    /**
     * 修改文档管理
     *
     * @param sysDocument 文档管理
     * @return 结果
     */
    public EmsResultEntity updateSysDocument(SysDocument sysDocument);

    /**
     * 变更文档管理
     *
     * @param sysDocument 文档管理
     * @return 结果
     */
    public int changeSysDocument(SysDocument sysDocument);

    /**
     * 批量删除文档管理
     *
     * @param documentSids 需要删除的文档管理ID
     * @return 结果
     */
    public int deleteSysDocumentByIds(List<Long> documentSids);

}
