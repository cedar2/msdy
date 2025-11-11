package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaRawmatCheckAttach;

import java.util.List;

/**
 * 面辅料检测单-附件Service接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface IQuaRawmatCheckAttachService extends IService<QuaRawmatCheckAttach> {
    /**
     * 查询面辅料检测单-附件
     *
     * @param attachmentSid 面辅料检测单-附件ID
     * @return 面辅料检测单-附件
     */
    public QuaRawmatCheckAttach selectQuaRawmatCheckAttachById(Long attachmentSid);

    /**
     * 查询面辅料检测单-附件列表
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 面辅料检测单-附件集合
     */
    public List<QuaRawmatCheckAttach> selectQuaRawmatCheckAttachList(QuaRawmatCheckAttach quaRawmatCheckAttach);

    /**
     * 新增面辅料检测单-附件
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 结果
     */
    public int insertQuaRawmatCheckAttach(QuaRawmatCheckAttach quaRawmatCheckAttach);

    /**
     * 修改面辅料检测单-附件
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 结果
     */
    public int updateQuaRawmatCheckAttach(QuaRawmatCheckAttach quaRawmatCheckAttach);

    /**
     * 变更面辅料检测单-附件
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 结果
     */
    public int changeQuaRawmatCheckAttach(QuaRawmatCheckAttach quaRawmatCheckAttach);

    /**
     * 批量删除面辅料检测单-附件
     *
     * @param attachmentSids 需要删除的面辅料检测单-附件ID
     * @return 结果
     */
    public int deleteQuaRawmatCheckAttachByIds(List<Long> attachmentSids);

}
