package com.platform.ems.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosProductCostAttachment;

/**
 * 商品成本核算-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-02-26
 */
public interface CosProductCostAttachmentMapper extends BaseMapper<CosProductCostAttachment>{
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
     * 删除商品成本核算-附件
     *
     * @param productCostAttachmentSid 商品成本核算-附件ID
     * @return 结果
     */
    public int deleteCosProductCostAttachmentById(String productCostAttachmentSid);

    /**
     * 批量删除商品成本核算-附件
     *
     * @param productCostAttachmentSids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCosProductCostAttachmentByIds(String[] productCostAttachmentSids);

    /**
     * 根据商品成本核算id删除
     *
     * @param productCostSid 商品成本核算id
     * @return 结果
     */
    int deleteCosProductCostAttachmentByProductCostId(Long productCostSid);
}
