package com.platform.ems.service;

import com.platform.ems.domain.TecMaterialPosInfor;

import java.util.List;

/**
 * 商品尺寸-部位Service接口
 *
 * @author olive
 * @date 2021-02-21
 */
public interface ITecMaterialPosInforService {
    /**
     * 查询商品尺寸-部位
     *
     * @param clientId 商品尺寸-部位ID
     * @return 商品尺寸-部位
     */
    public TecMaterialPosInfor selectTecMaterialPosInforById(Long clientId);

    /**
     * 查询商品尺寸-部位列表
     *
     * @param tecMaterialPosInfor 商品尺寸-部位
     * @return 商品尺寸-部位集合
     */
    public List<TecMaterialPosInfor> selectTecMaterialPosInforList(TecMaterialPosInfor tecMaterialPosInfor);

    /**
     * 新增商品尺寸-部位
     *
     * @param tecMaterialPosInfor 商品尺寸-部位
     * @return 结果
     */
    public int insertTecMaterialPosInfor(TecMaterialPosInfor tecMaterialPosInfor);

    /**
     * 修改商品尺寸-部位
     *
     * @param tecMaterialPosInfor 商品尺寸-部位
     * @return 结果
     */
    public int updateTecMaterialPosInfor(TecMaterialPosInfor tecMaterialPosInfor);

    /**
     * 批量删除商品尺寸-部位
     *
     * @param sids 需要删除的商品尺寸-部位ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforByIds(Long[] sids);

    /**
     * 删除商品尺寸-部位信息
     *
     * @param sid 商品尺寸-部位ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforById(Long sid);

    /**
     * 删除商品尺寸-部位信息
     *
     * @param materialSizeSid 商品尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforBySizeId(List<Long> materialSizeSid);

    /**
     * 根据尺寸表id查找所有部位信息id
     */
    List<Long> selectSidByParentids(Long id);
}
