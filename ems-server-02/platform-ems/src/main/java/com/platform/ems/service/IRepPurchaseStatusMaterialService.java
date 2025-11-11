package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepPurchaseStatusMaterial;

/**
 * 采购状况-面辅料/商品Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepPurchaseStatusMaterialService extends IService<RepPurchaseStatusMaterial> {
    /**
     * 查询采购状况-面辅料/商品
     *
     * @param dataRecordSid 采购状况-面辅料/商品ID
     * @return 采购状况-面辅料/商品
     */
    public RepPurchaseStatusMaterial selectRepPurchaseStatusMaterialById(Long dataRecordSid);

    /**
     * 查询采购状况-面辅料/商品列表
     *
     * @param repPurchaseStatusMaterial 采购状况-面辅料/商品
     * @return 采购状况-面辅料/商品集合
     */
    public List<RepPurchaseStatusMaterial> selectRepPurchaseStatusMaterialList(RepPurchaseStatusMaterial repPurchaseStatusMaterial);

    /**
     * 新增采购状况-面辅料/商品
     *
     * @param repPurchaseStatusMaterial 采购状况-面辅料/商品
     * @return 结果
     */
    public int insertRepPurchaseStatusMaterial(RepPurchaseStatusMaterial repPurchaseStatusMaterial);

    /**
     * 批量删除采购状况-面辅料/商品
     *
     * @param dataRecordSids 需要删除的采购状况-面辅料/商品ID
     * @return 结果
     */
    public int deleteRepPurchaseStatusMaterialByIds(List<Long> dataRecordSids);

}
