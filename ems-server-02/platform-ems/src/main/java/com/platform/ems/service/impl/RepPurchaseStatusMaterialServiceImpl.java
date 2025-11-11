package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepPurchaseStatusMaterialMapper;
import com.platform.ems.domain.RepPurchaseStatusMaterial;
import com.platform.ems.service.IRepPurchaseStatusMaterialService;

/**
 * 采购状况-面辅料/商品Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepPurchaseStatusMaterialServiceImpl extends ServiceImpl<RepPurchaseStatusMaterialMapper, RepPurchaseStatusMaterial> implements IRepPurchaseStatusMaterialService {
    @Autowired
    private RepPurchaseStatusMaterialMapper repPurchaseStatusMaterialMapper;

    /**
     * 查询采购状况-面辅料/商品
     *
     * @param dataRecordSid 采购状况-面辅料/商品ID
     * @return 采购状况-面辅料/商品
     */
    @Override
    public RepPurchaseStatusMaterial selectRepPurchaseStatusMaterialById(Long dataRecordSid) {
        RepPurchaseStatusMaterial repPurchaseStatusMaterial = repPurchaseStatusMaterialMapper.selectRepPurchaseStatusMaterialById(dataRecordSid);
        return repPurchaseStatusMaterial;
    }

    /**
     * 查询采购状况-面辅料/商品列表
     *
     * @param repPurchaseStatusMaterial 采购状况-面辅料/商品
     * @return 采购状况-面辅料/商品
     */
    @Override
    public List<RepPurchaseStatusMaterial> selectRepPurchaseStatusMaterialList(RepPurchaseStatusMaterial repPurchaseStatusMaterial) {
        return repPurchaseStatusMaterialMapper.selectRepPurchaseStatusMaterialList(repPurchaseStatusMaterial);
    }

    /**
     * 新增采购状况-面辅料/商品
     * 需要注意编码重复校验
     *
     * @param repPurchaseStatusMaterial 采购状况-面辅料/商品
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepPurchaseStatusMaterial(RepPurchaseStatusMaterial repPurchaseStatusMaterial) {
        int row = repPurchaseStatusMaterialMapper.insert(repPurchaseStatusMaterial);
        return row;
    }

    /**
     * 批量删除采购状况-面辅料/商品
     *
     * @param dataRecordSids 需要删除的采购状况-面辅料/商品ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepPurchaseStatusMaterialByIds(List<Long> dataRecordSids) {
        return repPurchaseStatusMaterialMapper.deleteBatchIds(dataRecordSids);
    }

}
