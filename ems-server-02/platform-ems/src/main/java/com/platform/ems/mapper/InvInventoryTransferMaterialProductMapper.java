package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryTransferMaterialProduct;

/**
 * 调班单-物料所用于商品信息Mapper接口
 * 
 * @author linhongwei
 * @date 2022-06-15
 */
public interface InvInventoryTransferMaterialProductMapper  extends BaseMapper<InvInventoryTransferMaterialProduct> {


    List<InvInventoryTransferMaterialProduct> selectInvInventoryTransferMaterialProductById(Long itemMaterialProductSid);

    List<InvInventoryTransferMaterialProduct> selectInvInventoryTransferMaterialProductList(InvInventoryTransferMaterialProduct invInventoryTransferMaterialProduct);

    /**
     * 添加多个
     * @param list List InvInventoryTransferMaterialProduct
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryTransferMaterialProduct> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryTransferMaterialProduct
    * @return int
    */
    int updateAllById(InvInventoryTransferMaterialProduct entity);

    /**
     * 更新多个
     * @param list List InvInventoryTransferMaterialProduct
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryTransferMaterialProduct> list);


}
