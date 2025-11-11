package com.platform.ems.service;

import com.platform.ems.domain.TecMaterialPosSize;

import java.util.List;

/**
 * 商品尺寸-部位-尺码-尺寸Service接口
 *
 * @author olive
 * @date 2021-02-21
 */
public interface ITecMaterialPosSizeService {
    /**
     * 查询商品尺寸-部位-尺码-尺寸
     *
     * @param clientId 商品尺寸-部位-尺码-尺寸ID
     * @return 商品尺寸-部位-尺码-尺寸
     */
    public TecMaterialPosSize selectTecMaterialPosSizeById(String clientId);

    /**
     * 查询商品尺寸-部位-尺码-尺寸列表
     *
     * @param tecMaterialPosSize 商品尺寸-部位-尺码-尺寸
     * @return 商品尺寸-部位-尺码-尺寸集合
     */
    public List<TecMaterialPosSize> selectTecMaterialPosSizeList(TecMaterialPosSize tecMaterialPosSize);

    /**
     * 新增商品尺寸-部位-尺码-尺寸
     *
     * @param tecMaterialPosSize 商品尺寸-部位-尺码-尺寸
     * @return 结果
     */
    public int insertTecMaterialPosSize(TecMaterialPosSize tecMaterialPosSize);

    /**
     * 修改商品尺寸-部位-尺码-尺寸
     *
     * @param tecMaterialPosSize 商品尺寸-部位-尺码-尺寸
     * @return 结果
     */
    public int updateTecMaterialPosSize(TecMaterialPosSize tecMaterialPosSize);

    /**
     * 批量删除商品尺寸-部位-尺码-尺寸
     *
     * @param clientIds 需要删除的商品尺寸-部位-尺码-尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialPosSizeByIds(String[] clientIds);

    /**
     * 删除商品尺寸-部位-尺码-尺寸信息
     *
     * @param clientId 商品尺寸-部位-尺码-尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialPosSizeById(String clientId);

    /**
     * 删除商品尺寸-部位-尺码-尺寸信息
     *
     * @param infoId 商品尺寸-部位-尺码-尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialPosSizeByInfoId(List<String> infoId);

}
