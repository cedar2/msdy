package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvMaterialRequisitionItem;

/**
 * 领退料单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IInvMaterialRequisitionItemService extends IService<InvMaterialRequisitionItem>{
    /**
     * 查询领退料单-明细
     * 
     * @param materialRequisitionItemSid 领退料单-明细ID
     * @return 领退料单-明细
     */
    public InvMaterialRequisitionItem selectInvMaterialRequisitionItemById(Long materialRequisitionItemSid);

    /**
     * 查询领退料单-明细列表
     * 
     * @param invMaterialRequisitionItem 领退料单-明细
     * @return 领退料单-明细集合
     */
    public List<InvMaterialRequisitionItem> selectInvMaterialRequisitionItemList(InvMaterialRequisitionItem invMaterialRequisitionItem);

    /**
     * 新增领退料单-明细
     * 
     * @param invMaterialRequisitionItem 领退料单-明细
     * @return 结果
     */
    public int insertInvMaterialRequisitionItem(InvMaterialRequisitionItem invMaterialRequisitionItem);

    /**
     * 修改领退料单-明细
     * 
     * @param invMaterialRequisitionItem 领退料单-明细
     * @return 结果
     */
    public int updateInvMaterialRequisitionItem(InvMaterialRequisitionItem invMaterialRequisitionItem);

    /**
     * 批量删除领退料单-明细
     * 
     * @param materialRequisitionItemSids 需要删除的领退料单-明细ID
     * @return 结果
     */
    public int deleteInvMaterialRequisitionItemByIds(List<Long> materialRequisitionItemSids);

}
