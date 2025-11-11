package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeRequireDocument;

/**
 * 业务类型_需求单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeRequireDocumentService extends IService<ConBuTypeRequireDocument>{
    /**
     * 查询业务类型_需求单
     * 
     * @param sid 业务类型_需求单ID
     * @return 业务类型_需求单
     */
    public ConBuTypeRequireDocument selectConBuTypeRequireDocumentById(Long sid);

    /**
     * 查询业务类型_需求单列表
     * 
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 业务类型_需求单集合
     */
    public List<ConBuTypeRequireDocument> selectConBuTypeRequireDocumentList(ConBuTypeRequireDocument conBuTypeRequireDocument);

    /**
     * 新增业务类型_需求单
     * 
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 结果
     */
    public int insertConBuTypeRequireDocument(ConBuTypeRequireDocument conBuTypeRequireDocument);

    /**
     * 修改业务类型_需求单
     * 
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 结果
     */
    public int updateConBuTypeRequireDocument(ConBuTypeRequireDocument conBuTypeRequireDocument);

    /**
     * 变更业务类型_需求单
     *
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 结果
     */
    public int changeConBuTypeRequireDocument(ConBuTypeRequireDocument conBuTypeRequireDocument);

    /**
     * 批量删除业务类型_需求单
     * 
     * @param sids 需要删除的业务类型_需求单ID
     * @return 结果
     */
    public int deleteConBuTypeRequireDocumentByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeRequireDocument
    * @return
    */
    int changeStatus(ConBuTypeRequireDocument conBuTypeRequireDocument);

    /**
     * 更改确认状态
     * @param conBuTypeRequireDocument
     * @return
     */
    int check(ConBuTypeRequireDocument conBuTypeRequireDocument);

}
