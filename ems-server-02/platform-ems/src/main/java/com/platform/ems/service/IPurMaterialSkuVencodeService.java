package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurMaterialSkuVencode;

/**
 * 采购货源供方SKU编码Service接口
 * 
 * @author linhongwei
 * @date 2021-03-29
 */
public interface IPurMaterialSkuVencodeService extends IService<PurMaterialSkuVencode>{
    /**
     * 查询采购货源供方SKU编码
     * 
     * @param materialVendorSkuSid 采购货源供方SKU编码ID
     * @return 采购货源供方SKU编码
     */
    public PurMaterialSkuVencode selectPurMaterialSkuVencodeById(Long materialVendorSkuSid);

    /**
     * 查询采购货源供方SKU编码列表
     * 
     * @param purMaterialSkuVencode 采购货源供方SKU编码
     * @return 采购货源供方SKU编码集合
     */
    public List<PurMaterialSkuVencode> selectPurMaterialSkuVencodeList(PurMaterialSkuVencode purMaterialSkuVencode);

    /**
     * 新增采购货源供方SKU编码
     * 
     * @param purMaterialSkuVencode 采购货源供方SKU编码
     * @return 结果
     */
    public int insertPurMaterialSkuVencode(PurMaterialSkuVencode purMaterialSkuVencode);

    /**
     * 修改采购货源供方SKU编码
     * 
     * @param purMaterialSkuVencode 采购货源供方SKU编码
     * @return 结果
     */
    public int updatePurMaterialSkuVencode(PurMaterialSkuVencode purMaterialSkuVencode);

    /**
     * 批量删除采购货源供方SKU编码
     * 
     * @param materialVendorSkuSids 需要删除的采购货源供方SKU编码ID
     * @return 结果
     */
    public int deletePurMaterialSkuVencodeByIds(List<Long> materialVendorSkuSids);

}
