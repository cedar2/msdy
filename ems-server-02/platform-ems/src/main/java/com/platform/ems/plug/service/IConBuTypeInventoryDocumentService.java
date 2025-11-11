package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeInventoryDocument;

/**
 * 业务类型_库存凭证Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeInventoryDocumentService extends IService<ConBuTypeInventoryDocument>{
    /**
     * 查询业务类型_库存凭证
     * 
     * @param sid 业务类型_库存凭证ID
     * @return 业务类型_库存凭证
     */
    public ConBuTypeInventoryDocument selectConBuTypeInventoryDocumentById(Long sid);

    /**
     * 查询业务类型_库存凭证列表
     * 
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 业务类型_库存凭证集合
     */
    public List<ConBuTypeInventoryDocument> selectConBuTypeInventoryDocumentList(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

    /**
     * 新增业务类型_库存凭证
     * 
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 结果
     */
    public int insertConBuTypeInventoryDocument(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

    /**
     * 修改业务类型_库存凭证
     * 
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 结果
     */
    public int updateConBuTypeInventoryDocument(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

    /**
     * 变更业务类型_库存凭证
     *
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 结果
     */
    public int changeConBuTypeInventoryDocument(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

    /**
     * 批量删除业务类型_库存凭证
     * 
     * @param sids 需要删除的业务类型_库存凭证ID
     * @return 结果
     */
    public int deleteConBuTypeInventoryDocumentByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeInventoryDocument
    * @return
    */
    int changeStatus(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

    /**
     * 更改确认状态
     * @param conBuTypeInventoryDocument
     * @return
     */
    int check(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

}
