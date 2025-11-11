package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPurchaseOrderCategory;

/**
 * 采购订单类别Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConPurchaseOrderCategoryMapper  extends BaseMapper<ConPurchaseOrderCategory> {


    ConPurchaseOrderCategory selectConPurchaseOrderCategoryById(Long sid);

    List<ConPurchaseOrderCategory> selectConPurchaseOrderCategoryList(ConPurchaseOrderCategory conPurchaseOrderCategory);

    /**
     * 添加多个
     * @param list List ConPurchaseOrderCategory
     * @return int
     */
    int inserts(@Param("list") List<ConPurchaseOrderCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPurchaseOrderCategory
    * @return int
    */
    int updateAllById(ConPurchaseOrderCategory entity);

    /**
     * 更新多个
     * @param list List ConPurchaseOrderCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPurchaseOrderCategory> list);


}
