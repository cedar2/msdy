package com.platform.ems.service.impl;

import com.platform.ems.domain.TecMaterialPosSize;
import com.platform.ems.mapper.TecMaterialPosSizeMapper;
import com.platform.ems.service.ITecMaterialPosSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品尺寸-部位-尺码-尺寸Service业务层处理
 *
 * @author olive
 * @date 2021-02-21
 */
@Service
public class TecMaterialPosSizeServiceImpl implements ITecMaterialPosSizeService {
    @Autowired
    private TecMaterialPosSizeMapper tecMaterialPosSizeMapper;

    /**
     * 查询商品尺寸-部位-尺码-尺寸
     *
     * @param clientId 商品尺寸-部位-尺码-尺寸ID
     * @return 商品尺寸-部位-尺码-尺寸
     */
    @Override
    public TecMaterialPosSize selectTecMaterialPosSizeById(String clientId) {
        return tecMaterialPosSizeMapper.selectTecMaterialPosSizeById(clientId);
    }

    /**
     * 查询商品尺寸-部位-尺码-尺寸列表
     *
     * @param tecMaterialPosSize 商品尺寸-部位-尺码-尺寸
     * @return 商品尺寸-部位-尺码-尺寸
     */
    @Override
    public List<TecMaterialPosSize> selectTecMaterialPosSizeList(TecMaterialPosSize tecMaterialPosSize) {
        return tecMaterialPosSizeMapper.selectTecMaterialPosSizeList(tecMaterialPosSize);
    }

    /**
     * 新增商品尺寸-部位-尺码-尺寸
     *
     * @param tecMaterialPosSize 商品尺寸-部位-尺码-尺寸
     * @return 结果
     */
    @Override
    public int insertTecMaterialPosSize(TecMaterialPosSize tecMaterialPosSize) {
        return tecMaterialPosSizeMapper.insert(tecMaterialPosSize);
    }

    /**
     * 修改商品尺寸-部位-尺码-尺寸
     *
     * @param tecMaterialPosSize 商品尺寸-部位-尺码-尺寸
     * @return 结果
     */
    @Override
    public int updateTecMaterialPosSize(TecMaterialPosSize tecMaterialPosSize) {
        return tecMaterialPosSizeMapper.updateTecMaterialPosSize(tecMaterialPosSize);
    }

    /**
     * 批量删除商品尺寸-部位-尺码-尺寸
     *
     * @param clientIds 需要删除的商品尺寸-部位-尺码-尺寸ID
     * @return 结果
     */
    @Override
    public int deleteTecMaterialPosSizeByIds(String[] materialPosSizeSids) {
        return tecMaterialPosSizeMapper.deleteTecMaterialPosSizeByIds(materialPosSizeSids);
    }

    /**
     * 删除商品尺寸-部位-尺码-尺寸信息
     *
     * @param clientId 商品尺寸-部位-尺码-尺寸ID
     * @return 结果
     */
    @Override
    public int deleteTecMaterialPosSizeById(String clientId) {
        return tecMaterialPosSizeMapper.deleteTecMaterialPosSizeById(clientId);
    }

    @Override
    public int deleteTecMaterialPosSizeByInfoId(List<String> infoId) {
        return tecMaterialPosSizeMapper.deleteTecMaterialPosSizeByInfoId(infoId);
    }


}
