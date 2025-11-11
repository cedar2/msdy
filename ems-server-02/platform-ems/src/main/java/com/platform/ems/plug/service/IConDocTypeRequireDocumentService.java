package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeRequireDocument;

/**
 * 单据类型_需求单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeRequireDocumentService extends IService<ConDocTypeRequireDocument>{
    /**
     * 查询单据类型_需求单
     * 
     * @param sid 单据类型_需求单ID
     * @return 单据类型_需求单
     */
    public ConDocTypeRequireDocument selectConDocTypeRequireDocumentById(Long sid);

    /**
     * 查询单据类型_需求单列表
     * 
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 单据类型_需求单集合
     */
    public List<ConDocTypeRequireDocument> selectConDocTypeRequireDocumentList(ConDocTypeRequireDocument conDocTypeRequireDocument);

    /**
     * 新增单据类型_需求单
     * 
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 结果
     */
    public int insertConDocTypeRequireDocument(ConDocTypeRequireDocument conDocTypeRequireDocument);

    /**
     * 修改单据类型_需求单
     * 
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 结果
     */
    public int updateConDocTypeRequireDocument(ConDocTypeRequireDocument conDocTypeRequireDocument);

    /**
     * 变更单据类型_需求单
     *
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 结果
     */
    public int changeConDocTypeRequireDocument(ConDocTypeRequireDocument conDocTypeRequireDocument);

    /**
     * 批量删除单据类型_需求单
     * 
     * @param sids 需要删除的单据类型_需求单ID
     * @return 结果
     */
    public int deleteConDocTypeRequireDocumentByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeRequireDocument
    * @return
    */
    int changeStatus(ConDocTypeRequireDocument conDocTypeRequireDocument);

    /**
     * 更改确认状态
     * @param conDocTypeRequireDocument
     * @return
     */
    int check(ConDocTypeRequireDocument conDocTypeRequireDocument);

}
