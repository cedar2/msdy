package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecMaterialPosInforDown;

/**
 * 商品尺寸-部位（套装的下装）Service接口
 * 
 * @author linhongwei
 * @date 2021-04-27
 */
public interface ITecMaterialPosInforDownService extends IService<TecMaterialPosInforDown>{
    /**
     * 查询商品尺寸-部位（套装的下装）
     * 
     * @param clientId 商品尺寸-部位（套装的下装）ID
     * @return 商品尺寸-部位（套装的下装）
     */
    public TecMaterialPosInforDown selectTecMaterialPosInforDownById(String clientId);

    /**
     * 查询商品尺寸-部位（套装的下装）列表
     * 
     * @param tecMaterialPosInforDown 商品尺寸-部位（套装的下装）
     * @return 商品尺寸-部位（套装的下装）集合
     */
    public List<TecMaterialPosInforDown> selectTecMaterialPosInforDownList(TecMaterialPosInforDown tecMaterialPosInforDown);

    /**
     * 新增商品尺寸-部位（套装的下装）
     * 
     * @param tecMaterialPosInforDown 商品尺寸-部位（套装的下装）
     * @return 结果
     */
    public int insertTecMaterialPosInforDown(TecMaterialPosInforDown tecMaterialPosInforDown);

    /**
     * 修改商品尺寸-部位（套装的下装）
     * 
     * @param tecMaterialPosInforDown 商品尺寸-部位（套装的下装）
     * @return 结果
     */
    public int updateTecMaterialPosInforDown(TecMaterialPosInforDown tecMaterialPosInforDown);

    /**
     * 批量删除商品尺寸-部位（套装的下装）
     * 
     * @param clientIds 需要删除的商品尺寸-部位（套装的下装）ID
     * @return 结果
     */
    public int deleteTecMaterialPosInforDownByIds(List<String> clientIds);

}
