package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialBarcodeOperateLevel;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.form.BasMatBarcodeOperLvlCategorySkuForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品SKU条码-网店运营信息Service接口
 *
 * @author chenkw
 * @date 2023-01-18
 */
public interface IBasMaterialBarcodeOperateLevelService extends IService<BasMaterialBarcodeOperateLevel> {
    /**
     * 查询商品SKU条码-网店运营信息
     *
     * @param materialBarcodeOperateLevelSid 商品SKU条码-网店运营信息ID
     * @return 商品SKU条码-网店运营信息
     */
    public BasMaterialBarcodeOperateLevel selectBasMaterialBarcodeOperateLevelById(Long materialBarcodeOperateLevelSid);

    /**
     * 查询商品SKU条码-网店运营信息列表
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 商品SKU条码-网店运营信息集合
     */
    public List<BasMaterialBarcodeOperateLevel> selectBasMaterialBarcodeOperateLevelList(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 新增商品SKU条码-网店运营信息
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    public int insertBasMaterialBarcodeOperateLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 修改商品SKU条码-网店运营信息
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    public int updateBasMaterialBarcodeOperateLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 按钮设置采购状态
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    public int updatePurchaseFlag(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 变更商品SKU条码-网店运营信息
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    public int changeBasMaterialBarcodeOperateLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 批量删除商品SKU条码-网店运营信息
     *
     * @param materialBarcodeOperateLevelSids 需要删除的商品SKU条码-网店运营信息ID
     * @return 结果
     */
    public int deleteBasMaterialBarcodeOperateLevelByIds(List<Long> materialBarcodeOperateLevelSids);

    /**
     * 设置产品级别
     *
     * @param basMaterialBarcodeOperateLevel 设置产品级别
     * @return 结果
     */
    public int setProductLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 设置商品MSKU编码(ERP)
     *
     * @param basMaterialBarcodeOperateLevel 设置产品级别
     * @return 结果
     */
    public int setMskuCode(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel);

    /**
     * 导入
     * @param file
     * @return
     */
    EmsResultEntity importData(MultipartFile file);

    /**
     * 报表中心类目SKU明细报表
     *
     * @param request BasMatBarcodeOperLvlCategorySkuForm
     * @return 报表中心类目明细报表
     */
    List<BasMatBarcodeOperLvlCategorySkuForm> selectBasMaterialBarcodeOperateLevelCategorySkuForm(BasMatBarcodeOperLvlCategorySkuForm request);

    /**
     * 更新数据导入 MSKU + 产品级别 + 运营级别
     * @param file
     * @return
     */
    EmsResultEntity importUpdateData(MultipartFile file);
}
