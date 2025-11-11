package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeInventoryDocument;

import java.util.List;

/**
 * 单据类型_库存凭证Service接口
 *
 * @author linhongwei
 * @date 2021-09-17
 */
public interface IConDocTypeInventoryDocumentService extends IService<ConDocTypeInventoryDocument> {
    /**
     * 查询单据类型_库存凭证
     *
     * @param sid 单据类型_库存凭证ID
     * @return 单据类型_库存凭证
     */
    public ConDocTypeInventoryDocument selectConDocTypeInventoryDocumentById(Long sid);

    /**
     * 查询单据类型_库存凭证列表
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 单据类型_库存凭证集合
     */
    public List<ConDocTypeInventoryDocument> selectConDocTypeInventoryDocumentList(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

    /**
     * 新增单据类型_库存凭证
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 结果
     */
    public int insertConDocTypeInventoryDocument(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

    /**
     * 修改单据类型_库存凭证
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 结果
     */
    public int updateConDocTypeInventoryDocument(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

    /**
     * 变更单据类型_库存凭证
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 结果
     */
    public int changeConDocTypeInventoryDocument(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

    /**
     * 批量删除单据类型_库存凭证
     *
     * @param sids 需要删除的单据类型_库存凭证ID
     * @return 结果
     */
    public int deleteConDocTypeInventoryDocumentByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDocTypeInventoryDocument
     * @return
     */
    int changeStatus(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

    /**
     * 更改确认状态
     *
     * @param conDocTypeInventoryDocument
     * @return
     */
    int check(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

}
