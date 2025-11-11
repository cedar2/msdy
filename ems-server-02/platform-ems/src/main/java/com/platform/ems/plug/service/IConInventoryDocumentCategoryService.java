package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInventoryDocumentCategory;

/**
 * 库存凭证类别Service接口
 * 
 * @author c
 * @date 2021-07-29
 */
public interface IConInventoryDocumentCategoryService extends IService<ConInventoryDocumentCategory>{
    /**
     * 查询库存凭证类别
     * 
     * @param sid 库存凭证类别ID
     * @return 库存凭证类别
     */
    public ConInventoryDocumentCategory selectConInventoryDocumentCategoryById(Long sid);

    /**
     * 查询库存凭证下拉列表
     *
     */
    public List<ConInventoryDocumentCategory>  getList();

    /**
     * 查询库存凭证类别列表
     * 
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 库存凭证类别集合
     */
    public List<ConInventoryDocumentCategory> selectConInventoryDocumentCategoryList(ConInventoryDocumentCategory conInventoryDocumentCategory);

    /**
     * 新增库存凭证类别
     * 
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 结果
     */
    public int insertConInventoryDocumentCategory(ConInventoryDocumentCategory conInventoryDocumentCategory);

    /**
     * 修改库存凭证类别
     * 
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 结果
     */
    public int updateConInventoryDocumentCategory(ConInventoryDocumentCategory conInventoryDocumentCategory);

    /**
     * 变更库存凭证类别
     *
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 结果
     */
    public int changeConInventoryDocumentCategory(ConInventoryDocumentCategory conInventoryDocumentCategory);

    /**
     * 批量删除库存凭证类别
     * 
     * @param sids 需要删除的库存凭证类别ID
     * @return 结果
     */
    public int deleteConInventoryDocumentCategoryByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conInventoryDocumentCategory
    * @return
    */
    int changeStatus(ConInventoryDocumentCategory conInventoryDocumentCategory);

    /**
     * 更改确认状态
     * @param conInventoryDocumentCategory
     * @return
     */
    int check(ConInventoryDocumentCategory conInventoryDocumentCategory);

}
