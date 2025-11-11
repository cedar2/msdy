package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecMaterialPosInfor;

import java.util.List;

/**
 * 商品尺寸-部位Mapper接口
 *
 * @author olive
 * @date 2021-02-21
 */
public interface TecMaterialPosInforMapper extends BaseMapper<TecMaterialPosInfor> {
    /**
     * 查询商品尺寸-部位
     *
     * @param modelPositionInforSid 商品尺寸-部位ID
     * @return 商品尺寸-部位
     */
    public TecMaterialPosInfor selectTecMaterialPosInforById(Long modelPositionInforSid);

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
     * 删除商品尺寸-部位
     *
     * @param modelPositionInforSid 商品尺寸-部位ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforById(Long modelPositionInforSid);

    /**
     * 删除商品尺寸-部位
     *
     * @param modelPositionInforSids 商品尺寸-部位ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforByIds(Long[] modelPositionInforSids);

    /**
     * 批量删除商品尺寸-部位
     *
     * @param materialSizeSids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforBySizeId(List<Long> materialSizeSids);

    /**
     * 根据尺寸表id查找部位信息id
     * @param materialSizeSid
     * @return
     */
    List<Long>  selectPosInforIds(Long materialSizeSid);
}
