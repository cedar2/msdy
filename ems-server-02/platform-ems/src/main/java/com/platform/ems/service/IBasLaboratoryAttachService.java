package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasLaboratoryAttach;

import java.util.List;

/**
 * 实验室-附件Service接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface IBasLaboratoryAttachService extends IService<BasLaboratoryAttach> {
    /**
     * 查询实验室-附件
     *
     * @param attachmentSid 实验室-附件ID
     * @return 实验室-附件
     */
    public BasLaboratoryAttach selectBasLaboratoryAttachById(Long attachmentSid);

    /**
     * 查询实验室-附件列表
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 实验室-附件集合
     */
    public List<BasLaboratoryAttach> selectBasLaboratoryAttachList(BasLaboratoryAttach basLaboratoryAttach);

    /**
     * 新增实验室-附件
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 结果
     */
    public int insertBasLaboratoryAttach(BasLaboratoryAttach basLaboratoryAttach);

    /**
     * 修改实验室-附件
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 结果
     */
    public int updateBasLaboratoryAttach(BasLaboratoryAttach basLaboratoryAttach);

    /**
     * 变更实验室-附件
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 结果
     */
    public int changeBasLaboratoryAttach(BasLaboratoryAttach basLaboratoryAttach);

    /**
     * 批量删除实验室-附件
     *
     * @param attachmentSids 需要删除的实验室-附件ID
     * @return 结果
     */
    public int deleteBasLaboratoryAttachByIds(List<Long> attachmentSids);

}
