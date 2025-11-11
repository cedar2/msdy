package com.platform.ems.service;

import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialSku;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

/**
 * 物料&商品-SKU明细Service接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface IBasMaterialSkuService {
    /**
     * 查询物料&商品-SKU明细
     *
     * @param materialSkuSid 物料&商品-SKU明细ID
     * @return 物料&商品-SKU明细
     */
    public BasMaterialSku selectBasMaterialSkuById(String materialSkuSid);

    /**
     * 查询物料&商品-SKU明细列表
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 物料&商品-SKU明细集合
     */
    public List<BasMaterialSku> selectBasMaterialSkuList(BasMaterialSku basMaterialSku);

    /**
     * 按 款色 查询
     *
     * @param basMaterialSku 物料&商品
     * @return 物料&商品-SKU明细集合
     */
    public List<BasMaterial> selectBasMaterialSku1List(BasMaterialSku basMaterialSku);

    /**
     * 新增物料&商品-SKU明细
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 结果
     */
    public int insertBasMaterialSku(BasMaterialSku basMaterialSku);

    /**
     * 新增物料&商品-SKU明细
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 结果
     */
    public int insertBasMaterialSkuList(Long materialSid,List<BasMaterialSku> basMaterialSku);

    /**
     * 修改物料&商品-SKU明细
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 结果
     */
    public int updateBasMaterialSku(BasMaterialSku basMaterialSku);

    /**
     * 批量删除物料&商品-SKU明细
     *
     * @param materialSkuSids 需要删除的物料&商品-SKU明细ID
     * @return 结果
     */
    public int deleteBasMaterialSkuByIds(List<String> materialSkuSids);

    /**
     * 查询物料&商品-SKU明细报表
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 物料&商品-SKU明细集合
     */
    public List<BasMaterialSku> getReportForm(BasMaterialSku basMaterialSku);

    /**
     * 导入物料/商品SKU明细档案
     *
     * @author chenkw
     * @date 2022-01-20
     */
    public HashMap<String, Object> importData(MultipartFile file, String materialCategory);

    /**
     * 设置图片上传
     * @param basMaterialSkuList
     * @return
     */
    public int setPictureList(List<BasMaterialSku> basMaterialSkuList);

    /**
     * 设置图片上传
     * @param basMaterialSku
     * @return
     */
    public int setPicture(BasMaterialSku basMaterialSku);

    /**
     * 启用停用
     * @param basMaterialSku
     * @return
     */
    public HashMap<String, Object> status(BasMaterialSku basMaterialSku);

    /**
     * 物料商品sku明细报表启用停用
     * @param basMaterialSku
     * @return
     */
    EmsResultEntity changeStatus(BasMaterialSku basMaterialSku);
}
