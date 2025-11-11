package com.platform.ems.service;

import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasSku;
import com.platform.ems.domain.TecMaterialSize;

import java.util.List;

/**
 * 商品尺寸Service接口
 *
 * @author olive
 * @date 2021-02-21
 */
public interface ITecMaterialSizeService {
    /**
     * 查询商品尺寸
     *
     * @param sid 商品尺寸ID
     * @return 商品尺寸
     */
    public TecMaterialSize selectTecMaterialSizeById(Long sid);

    /**
     * 查询商品尺寸列表
     *
     * @param tecMaterialSize 商品尺寸
     * @return 商品尺寸集合
     */
    public List<TecMaterialSize> selectTecMaterialSizeList(TecMaterialSize tecMaterialSize);


    public List<BasMaterial> selectBasMaterialList();
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
     * 批量删除商品尺寸
     *
     * @param materialSizeSid 需要删除的商品尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialSizeByIds(List<Long> materialSizeSid);

    /**
     * 删除商品尺寸信息
     *
     * @param sid 商品尺寸ID
     * @return 结果
     */
    public int deleteTecMaterialSizeById(Long sid);

    boolean isExist(Long materialSid);

    String getHandleStatus(Long sId);

    String putHandleStatus(Long sId, String handleStatus);

    String getStatus(Long sId);

    String putStatus(Long sId, String validStatus);

    int changeStatus(TecMaterialSize materialSize);

    int check(TecMaterialSize materialSize);


}
