package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialPackageItem;

import java.util.List;

/**
 * 常规辅料包-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-03-14
 */
public interface IBasMaterialPackageItemService extends IService<BasMaterialPackageItem>{
    /**
     * 查询常规辅料包-明细
     * 
     * @param materialPackItemSid 常规辅料包-明细ID
     * @return 常规辅料包-明细
     */
    public BasMaterialPackageItem selectBasMaterialPackageItemById(String materialPackItemSid);

    /**
     * 查询常规辅料包-明细列表
     * 
     * @param basMaterialPackageItem 常规辅料包-明细
     * @return 常规辅料包-明细集合
     */
    public List<BasMaterialPackageItem> selectBasMaterialPackageItemList(BasMaterialPackageItem basMaterialPackageItem);

    /**
     * 新增常规辅料包-明细
     * 
     * @param basMaterialPackageItem 常规辅料包-明细
     * @return 结果
     */
    public int insertBasMaterialPackageItem(BasMaterialPackageItem basMaterialPackageItem);

    /**
     * 修改常规辅料包-明细
     * 
     * @param basMaterialPackageItem 常规辅料包-明细
     * @return 结果
     */
    public int updateBasMaterialPackageItem(BasMaterialPackageItem basMaterialPackageItem);

    /**
     * 批量删除常规辅料包-明细
     * 
     * @param materialPackItemSid 需要删除的常规辅料包-明细ID
     * @return 结果
     */
    public int deleteBasMaterialPackageItemByIds(List<String> materialPackItemSid);

    /**
     * 查询常规辅料包-明细报表
     *
     * @param basMaterialPackageItem 常规辅料包-明细报表
     * @return 常规辅料包-明细集合
     */
    List<BasMaterialPackageItem> getReportForm(BasMaterialPackageItem basMaterialPackageItem);

}
