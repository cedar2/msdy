package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPurchaseOrderMaterialProduct;
import com.platform.ems.domain.dto.request.PurPurchaseOrderMaterialProductAddRequest;

/**
 * undefinedService接口
 * 
 * @author yangqz
 * @date 2022-04-20
 */
public interface IPurPurchaseOrderMaterialProductService extends IService<PurPurchaseOrderMaterialProduct>{
    /**
     * 查询undefined
     * 
     * @param purchaseOrderItemSid undefinedID
     * @return undefined
     */
    public List<PurPurchaseOrderMaterialProduct> selectPurPurchaseOrderMaterialProductById(Long purchaseOrderItemSid);

    /**
     * 查询undefined列表
     * 
     * @param purPurchaseOrderMaterialProduct undefined
     * @return undefined集合
     */
    public List<PurPurchaseOrderMaterialProduct> selectPurPurchaseOrderMaterialProductList(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct);

    /**
     * 新增undefined
     * 
     * @param
     * @return 结果
     */
    public int insertPurPurchaseOrderMaterialProduct(PurPurchaseOrderMaterialProductAddRequest request);

    /**
     * 修改undefined
     * 
     * @param purPurchaseOrderMaterialProduct undefined
     * @return 结果
     */
    public int updatePurPurchaseOrderMaterialProduct(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct);

    /**
     * 变更undefined
     *
     * @param purPurchaseOrderMaterialProduct undefined
     * @return 结果
     */
    public int changePurPurchaseOrderMaterialProduct(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct);

    /**
     * 批量删除undefined
     * 
     * @param itemMaterialProductSids 需要删除的undefinedID
     * @return 结果
     */
    public int deletePurPurchaseOrderMaterialProductByIds(List<Long> itemMaterialProductSids);

    /**
    * 启用/停用
    * @param purPurchaseOrderMaterialProduct
    * @return
    */
    int changeStatus(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct);

    /**
     * 更改确认状态
     * @param purPurchaseOrderMaterialProduct
     * @return
     */
    int check(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct);

}
