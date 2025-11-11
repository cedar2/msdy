package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterialPackage;
import com.platform.ems.domain.BasMaterialPackageItem;
import com.platform.ems.domain.dto.request.MaterialPackageAcitonRequest;


/**
 * 常规辅料包-主Service接口
 *
 * @author linhongwei
 * @date 2021-03-14
 */
public interface IBasMaterialPackageService extends IService<BasMaterialPackage>{
    /**
     * 查询常规辅料包-主
     *
     * @param sid 常规辅料包-主ID
     * @return 常规辅料包-主
     */
    public BasMaterialPackage selectBasMaterialPackageById(Long sid);

    /**
     * 查询常规辅料包-主列表
     *
     * @param basMaterialPackage 常规辅料包-主
     * @return 常规辅料包-主集合
     */
    public List<BasMaterialPackage> selectBasMaterialPackageList(BasMaterialPackage basMaterialPackage);

    /**
     * 新增常规辅料包-主
     *
     * @param basMaterialPackage 常规辅料包-主
     * @return 结果
     */
    public AjaxResult insertBasMaterialPackage(BasMaterialPackage basMaterialPackage);

    /**
     * 修改常规辅料包-主
     *
     * @param basMaterialPackage
     * @return 结果
     */
    public AjaxResult updateBasMaterialPackage(BasMaterialPackage basMaterialPackage);
    /**
     * 变更常规辅料包-主
     *
     * @param basMaterialPackage
     * @return 结果
     */
    public AjaxResult changeBasMaterialPackage(BasMaterialPackage basMaterialPackage);
    /**
     * 批量删除常规辅料包-主
     *
     * @param sids 需要删除的常规辅料包-主ID
     * @return 结果
     */
    public AjaxResult deleteBasMaterialPackageByIds(List<Long> sids);

    /**
     * 确认常规辅料包-主信息
     *
     * @param materialPackageAcitonRequest 常规辅料包-主ID
     * @return 结果
     */
    public AjaxResult confirmBasMaterialPackage(MaterialPackageAcitonRequest materialPackageAcitonRequest);
    /**
     * 启用/停用 常规辅料包-信息
     *
     * @param materialPackageAcitonRequest 常规辅料包-主ID
     * @return 结果
     */
    public AjaxResult status(MaterialPackageAcitonRequest materialPackageAcitonRequest);

    /**
     * 查询所有辅料包sid、name用于下拉框
     * @return
     */
    List<BasMaterialPackage> getMaterialPackageList();

    /**
     * 根据主表sid查询辅料包list
     * @param materialPackageSids
     * @return
     */
    List<BasMaterialPackageItem> getMaterialPackageItemList(List<Long> materialPackageSids);

    /**
     * 复制常规辅料包-主
     *
     * @param sid 常规辅料包-主ID
     * @return 常规辅料包-主
     */
    BasMaterialPackage copyBasMaterialPackageById(Long sid);
}
