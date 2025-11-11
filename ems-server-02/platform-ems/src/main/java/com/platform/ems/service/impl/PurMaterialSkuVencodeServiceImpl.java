package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurMaterialSkuVencodeMapper;
import com.platform.ems.domain.PurMaterialSkuVencode;
import com.platform.ems.service.IPurMaterialSkuVencodeService;

/**
 * 采购货源供方SKU编码Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-29
 */
@Service
@SuppressWarnings("all")
public class PurMaterialSkuVencodeServiceImpl extends ServiceImpl<PurMaterialSkuVencodeMapper,PurMaterialSkuVencode>  implements IPurMaterialSkuVencodeService {
    @Autowired
    private PurMaterialSkuVencodeMapper purMaterialSkuVencodeMapper;

    /**
     * 查询采购货源供方SKU编码
     * 
     * @param materialVendorSkuSid 采购货源供方SKU编码ID
     * @return 采购货源供方SKU编码
     */
    @Override
    public PurMaterialSkuVencode selectPurMaterialSkuVencodeById(Long materialVendorSkuSid) {
        return purMaterialSkuVencodeMapper.selectPurMaterialSkuVencodeById(materialVendorSkuSid);
    }

    /**
     * 查询采购货源供方SKU编码列表
     * 
     * @param purMaterialSkuVencode 采购货源供方SKU编码
     * @return 采购货源供方SKU编码
     */
    @Override
    public List<PurMaterialSkuVencode> selectPurMaterialSkuVencodeList(PurMaterialSkuVencode purMaterialSkuVencode) {
        return purMaterialSkuVencodeMapper.selectPurMaterialSkuVencodeList(purMaterialSkuVencode);
    }

    /**
     * 新增采购货源供方SKU编码
     * 需要注意编码重复校验
     * @param purMaterialSkuVencode 采购货源供方SKU编码
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurMaterialSkuVencode(PurMaterialSkuVencode purMaterialSkuVencode) {
        return purMaterialSkuVencodeMapper.insert(purMaterialSkuVencode);
    }

    /**
     * 修改采购货源供方SKU编码
     * 
     * @param purMaterialSkuVencode 采购货源供方SKU编码
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurMaterialSkuVencode(PurMaterialSkuVencode purMaterialSkuVencode) {
        return purMaterialSkuVencodeMapper.updateById(purMaterialSkuVencode);
    }

    /**
     * 批量删除采购货源供方SKU编码
     * 
     * @param materialVendorSkuSids 需要删除的采购货源供方SKU编码ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurMaterialSkuVencodeByIds(List<Long> materialVendorSkuSids) {
        return purMaterialSkuVencodeMapper.deleteBatchIds(materialVendorSkuSids);
    }


}
