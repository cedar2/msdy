package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaProductCheckAttach;

import java.util.List;

/**
 * 成衣检测单-附件Service接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface IQuaProductCheckAttachService extends IService<QuaProductCheckAttach> {
    /**
     * 查询成衣检测单-附件
     *
     * @param attachmentSid 成衣检测单-附件ID
     * @return 成衣检测单-附件
     */
    public QuaProductCheckAttach selectQuaProductCheckAttachById(Long attachmentSid);

    /**
     * 查询成衣检测单-附件列表
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 成衣检测单-附件集合
     */
    public List<QuaProductCheckAttach> selectQuaProductCheckAttachList(QuaProductCheckAttach quaProductCheckAttach);

    /**
     * 新增成衣检测单-附件
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 结果
     */
    public int insertQuaProductCheckAttach(QuaProductCheckAttach quaProductCheckAttach);

    /**
     * 修改成衣检测单-附件
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 结果
     */
    public int updateQuaProductCheckAttach(QuaProductCheckAttach quaProductCheckAttach);

    /**
     * 变更成衣检测单-附件
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 结果
     */
    public int changeQuaProductCheckAttach(QuaProductCheckAttach quaProductCheckAttach);

    /**
     * 批量删除成衣检测单-附件
     *
     * @param attachmentSids 需要删除的成衣检测单-附件ID
     * @return 结果
     */
    public int deleteQuaProductCheckAttachByIds(List<Long> attachmentSids);

}
