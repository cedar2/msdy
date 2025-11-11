package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaSpecraftCheckAttach;

import java.util.List;

/**
 * 特殊工艺检测单-附件Service接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface IQuaSpecraftCheckAttachService extends IService<QuaSpecraftCheckAttach> {
    /**
     * 查询特殊工艺检测单-附件
     *
     * @param attachmentSid 特殊工艺检测单-附件ID
     * @return 特殊工艺检测单-附件
     */
    public QuaSpecraftCheckAttach selectQuaSpecraftCheckAttachById(Long attachmentSid);

    /**
     * 查询特殊工艺检测单-附件列表
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 特殊工艺检测单-附件集合
     */
    public List<QuaSpecraftCheckAttach> selectQuaSpecraftCheckAttachList(QuaSpecraftCheckAttach quaSpecraftCheckAttach);

    /**
     * 新增特殊工艺检测单-附件
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 结果
     */
    public int insertQuaSpecraftCheckAttach(QuaSpecraftCheckAttach quaSpecraftCheckAttach);

    /**
     * 修改特殊工艺检测单-附件
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 结果
     */
    public int updateQuaSpecraftCheckAttach(QuaSpecraftCheckAttach quaSpecraftCheckAttach);

    /**
     * 变更特殊工艺检测单-附件
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 结果
     */
    public int changeQuaSpecraftCheckAttach(QuaSpecraftCheckAttach quaSpecraftCheckAttach);

    /**
     * 批量删除特殊工艺检测单-附件
     *
     * @param attachmentSids 需要删除的特殊工艺检测单-附件ID
     * @return 结果
     */
    public int deleteQuaSpecraftCheckAttachByIds(List<Long> attachmentSids);

}
