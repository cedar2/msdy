package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProductProcessStepAttach;

import java.util.List;

/**
 * 商品道序-附件Service接口
 *
 * @author c
 * @date 2021-09-08
 */
public interface IPayProductProcessStepAttachService extends IService<PayProductProcessStepAttach> {
    /**
     * 查询商品道序-附件
     *
     * @param attachmentSid 商品道序-附件ID
     * @return 商品道序-附件
     */
    public PayProductProcessStepAttach selectPayProductProcessStepAttachById(Long attachmentSid);

    /**
     * 查询商品道序-附件列表
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 商品道序-附件集合
     */
    public List<PayProductProcessStepAttach> selectPayProductProcessStepAttachList(PayProductProcessStepAttach payProductProcessStepAttach);

    /**
     * 新增商品道序-附件
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 结果
     */
    public int insertPayProductProcessStepAttach(PayProductProcessStepAttach payProductProcessStepAttach);

    /**
     * 修改商品道序-附件
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 结果
     */
    public int updatePayProductProcessStepAttach(PayProductProcessStepAttach payProductProcessStepAttach);

    /**
     * 变更商品道序-附件
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 结果
     */
    public int changePayProductProcessStepAttach(PayProductProcessStepAttach payProductProcessStepAttach);

    /**
     * 批量删除商品道序-附件
     *
     * @param attachmentSids 需要删除的商品道序-附件ID
     * @return 结果
     */
    public int deletePayProductProcessStepAttachByIds(List<Long> attachmentSids);

}
