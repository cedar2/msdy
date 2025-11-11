package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecMaterialPosSizeDown;

/**
 * 商品尺寸-部位-尺码-尺寸（下装）Service接口
 * 
 * @author linhongwei
 * @date 2021-04-27
 */
public interface ITecMaterialPosSizeDownService extends IService<TecMaterialPosSizeDown>{
    /**
     * 查询商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param materialPosSizeSid 商品尺寸-部位-尺码-尺寸（下装）ID
     * @return 商品尺寸-部位-尺码-尺寸（下装）
     */
    public TecMaterialPosSizeDown selectTecMaterialPosSizeDownById(Long materialPosSizeSid);

    /**
     * 查询商品尺寸-部位-尺码-尺寸（下装）列表
     * 
     * @param tecMaterialPosSizeDown 商品尺寸-部位-尺码-尺寸（下装）
     * @return 商品尺寸-部位-尺码-尺寸（下装）集合
     */
    public List<TecMaterialPosSizeDown> selectTecMaterialPosSizeDownList(TecMaterialPosSizeDown tecMaterialPosSizeDown);

    /**
     * 新增商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param tecMaterialPosSizeDown 商品尺寸-部位-尺码-尺寸（下装）
     * @return 结果
     */
    public int insertTecMaterialPosSizeDown(TecMaterialPosSizeDown tecMaterialPosSizeDown);

    /**
     * 修改商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param tecMaterialPosSizeDown 商品尺寸-部位-尺码-尺寸（下装）
     * @return 结果
     */
    public int updateTecMaterialPosSizeDown(TecMaterialPosSizeDown tecMaterialPosSizeDown);

    /**
     * 批量删除商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param materialPosSizeSids 需要删除的商品尺寸-部位-尺码-尺寸（下装）ID
     * @return 结果
     */
    public int deleteTecMaterialPosSizeDownByIds(List<Long> materialPosSizeSids);

}
