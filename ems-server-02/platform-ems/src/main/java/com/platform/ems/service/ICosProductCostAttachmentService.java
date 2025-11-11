package com.platform.ems.service;

import com.platform.ems.domain.CosProductCostAttachment;

import java.util.List;

/**
 * 商品成本核算-附件Service接口
 *
 * @author linhongwei
 * @date 2021-02-26
 */
public interface ICosProductCostAttachmentService {
    /**
     * 查询商品成本核算-附件
     *
     * @param productCostAttachmentSid 商品成本核算-附件ID
     * @return 商品成本核算-附件
     */
    public CosProductCostAttachment selectCosProductCostAttachmentById(String productCostAttachmentSid);

    /**
     * 查询商品成本核算-附件列表
     *
     * @param cosProductCostAttachment 商品成本核算-附件
     * @return 商品成本核算-附件集合
     */
    public List<CosProductCostAttachment> selectCosProductCostAttachmentList(CosProductCostAttachment cosProductCostAttachment);

    /**
     * 新增商品成本核算-附件
     *
     * @param cosProductCostAttachment 商品成本核算-附件
     * @return 结果
     */
    public int insertCosProductCostAttachment(CosProductCostAttachment cosProductCostAttachment);

    /**
     * 修改商品成本核算-附件
     *
     * @param cosProductCostAttachment 商品成本核算-附件
     * @return 结果
     */
    public int updateCosProductCostAttachment(CosProductCostAttachment cosProductCostAttachment);

    /**
     * 批量删除商品成本核算-附件
     *
     * @param productCostAttachmentSids 需要删除的商品成本核算-附件ID
     * @return 结果
     */
    public int deleteCosProductCostAttachmentByIds(String[] productCostAttachmentSids);

    /**
     * 删除商品成本核算-附件信息
     *
     * @param productCostAttachmentSid 商品成本核算-附件ID
     * @return 结果
     */
    public int deleteCosProductCostAttachmentById(String productCostAttachmentSid);
}
