package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocCategoryInventoryDocument;
import com.platform.ems.plug.domain.ConMovementType;

/**
 * 单据类别_库存凭证Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocCategoryInventoryDocumentService extends IService<ConDocCategoryInventoryDocument>{
    /**
     * 查询单据类别_库存凭证
     * 
     * @param sid 单据类别_库存凭证ID
     * @return 单据类别_库存凭证
     */
    public ConDocCategoryInventoryDocument selectConDocCategoryInventoryDocumentById(Long sid);
    public List<ConDocCategoryInventoryDocument> getList();
    /**
     * 查询单据类别_库存凭证列表
     * 
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 单据类别_库存凭证集合
     */
    public List<ConDocCategoryInventoryDocument> selectConDocCategoryInventoryDocumentList(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);

    /**
     * 新增单据类别_库存凭证
     * 
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 结果
     */
    public int insertConDocCategoryInventoryDocument(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);

    /**
     * 修改单据类别_库存凭证
     * 
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 结果
     */
    public int updateConDocCategoryInventoryDocument(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);

    /**
     * 变更单据类别_库存凭证
     *
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 结果
     */
    public int changeConDocCategoryInventoryDocument(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);

    /**
     * 批量删除单据类别_库存凭证
     * 
     * @param sids 需要删除的单据类别_库存凭证ID
     * @return 结果
     */
    public int deleteConDocCategoryInventoryDocumentByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocCategoryInventoryDocument
    * @return
    */
    int changeStatus(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);

    /**
     * 更改确认状态
     * @param conDocCategoryInventoryDocument
     * @return
     */
    int check(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);

}
