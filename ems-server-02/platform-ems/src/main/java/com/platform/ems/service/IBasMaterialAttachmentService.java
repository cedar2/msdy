package com.platform.ems.service;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterialAttachment;

import java.util.List;

/**
 * 物料&商品-附件Service接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface IBasMaterialAttachmentService {
    /**
     * 查询物料&商品-附件
     *
     * @param materialAttachmentSid 物料&商品-附件ID
     * @return 物料&商品-附件
     */
    public BasMaterialAttachment selectBasMaterialAttachmentById(Long materialAttachmentSid);

    /**
     * 查询物料&商品-附件列表
     *
     * @param basMaterialAttachment 物料&商品-附件
     * @return 物料&商品-附件集合
     */
    public List<BasMaterialAttachment> selectBasMaterialAttachmentList(BasMaterialAttachment basMaterialAttachment);

    /**
     * 新增物料&商品-附件
     *
     * @param basMaterialAttachment 物料&商品-附件
     * @return 结果
     */
    public int insertBasMaterialAttachment(BasMaterialAttachment basMaterialAttachment);

    /**
     * 修改物料&商品-附件
     *
     * @param basMaterialAttachment 物料&商品-附件
     * @return 结果
     */
    public int updateBasMaterialAttachment(BasMaterialAttachment basMaterialAttachment);

    /**
     * 批量删除物料&商品-附件
     *
     * @param materialAttachmentSids 需要删除的物料&商品-附件ID
     * @return 结果
     */
    public int deleteBasMaterialAttachmentByIds(List<Long> materialAttachmentSids);

    /**
     * 校验
     *
     * @param basMaterialAttachment 需要删除的物料&商品-附件ID
     * @return 结果
     */
    public AjaxResult check(BasMaterialAttachment basMaterialAttachment);
}
