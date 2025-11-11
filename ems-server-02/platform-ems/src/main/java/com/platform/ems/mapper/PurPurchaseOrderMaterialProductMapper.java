package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrderMaterialProduct;

/**
 * undefinedMapper接口
 * 
 * @author yangqz
 * @date 2022-04-20
 */
public interface PurPurchaseOrderMaterialProductMapper  extends BaseMapper<PurPurchaseOrderMaterialProduct> {


    List<PurPurchaseOrderMaterialProduct> selectPurPurchaseOrderMaterialProductById(Long purchaseOrderItemSid);

    List<PurPurchaseOrderMaterialProduct> selectPurPurchaseOrderMaterialProductList(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct);

    /**
     * 添加多个
     * @param list List PurPurchaseOrderMaterialProduct
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseOrderMaterialProduct> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseOrderMaterialProduct
    * @return int
    */
    int updateAllById(PurPurchaseOrderMaterialProduct entity);

    /**
     * 更新多个
     * @param list List PurPurchaseOrderMaterialProduct
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrderMaterialProduct> list);


}
