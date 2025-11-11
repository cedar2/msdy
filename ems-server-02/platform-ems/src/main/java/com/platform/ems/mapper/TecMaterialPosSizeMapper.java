package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecMaterialPosSize;

import java.util.List;

/**
 * 商品尺寸-部位-尺码-尺寸Mapper接口
 *
 * @author olive
 * @date 2021-02-21
 */
public interface TecMaterialPosSizeMapper extends BaseMapper<TecMaterialPosSize> {
    /**
     * 查询商品尺寸-部位-尺码-尺寸
     *
     * @param materialPosSizeSid 商品尺寸-部位-尺码-尺寸ID
     * @return 商品尺寸-部位-尺码-尺寸
     */
    public TecMaterialPosSize selectTecMaterialPosSizeById(String materialPosSizeSid);

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
     * 删除商品尺寸-部位-尺码-尺寸
     *
     * @param materialPosSizeSid 商品尺寸-部位-尺码-尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialPosSizeById(String materialPosSizeSid);

    /**
     * 批量删除商品尺寸-部位-尺码-尺寸
     *
     * @param clientIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteTecMaterialPosSizeByIds(String[] clientIds);

    /**
     * 根据部位id删除记录
     * @param materialPosInforSids
     * @return
     */
    int deleteTecMaterialPosSizeByInfoId(List<String> materialPosInforSids);
}
