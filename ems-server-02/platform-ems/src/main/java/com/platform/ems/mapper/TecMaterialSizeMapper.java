package com.platform.ems.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.TecMaterialSize;
import org.apache.ibatis.annotations.Param;

/**
 * 商品尺寸Mapper接口
 *
 * @author olive
 * @date 2021-02-21
 */
public interface TecMaterialSizeMapper extends BaseMapper<TecMaterialSize> {
    /**
     * 查询商品尺寸
     *
     * @param materialSizeSid 商品尺寸ID
     * @return 商品尺寸
     */
    public TecMaterialSize selectTecMaterialSizeById(Long materialSizeSid);

    /**
     * 查询商品尺寸列表
     *
     * @param tecMaterialSize 商品尺寸
     * @return 商品尺寸集合
     */
    public List<TecMaterialSize> selectTecMaterialSizeList(TecMaterialSize tecMaterialSize);

    List<BasMaterial> selectBasMaterialList();

    /**
     * 新增商品尺寸
     *
     * @param tecMaterialSize 商品尺寸
     * @return 结果
     */
    public int insertTecMaterialSize(TecMaterialSize tecMaterialSize);

    /**
     * 修改商品尺寸
     *
     * @param tecMaterialSize 商品尺寸
     * @return 结果
     */
    public int updateTecMaterialSize(TecMaterialSize tecMaterialSize);

    /**
     * 删除商品尺寸
     *
     * @param sid 商品尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialSizeById(Long sid);

    /**
     * 批量删除商品尺寸
     *
     * @param sids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTecMaterialSizeByIds(Long[] sids);

    String getHandleStatus(@Param("sId") Long sId);

    String putHandleStatus(@Param("sId") Long sId, @Param("handleStatus") String handleStatus);

    String getStatus(@Param("sId") Long sId);

    String putStatus(@Param("sId") Long sId, @Param("status") String status);
}
