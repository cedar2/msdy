package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSaleOrderCategory;

/**
 * 销售订单类别Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConSaleOrderCategoryMapper  extends BaseMapper<ConSaleOrderCategory> {


    ConSaleOrderCategory selectConSaleOrderCategoryById(Long sid);

    List<ConSaleOrderCategory> selectConSaleOrderCategoryList(ConSaleOrderCategory conSaleOrderCategory);

    /**
     * 添加多个
     * @param list List ConSaleOrderCategory
     * @return int
     */
    int inserts(@Param("list") List<ConSaleOrderCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConSaleOrderCategory
    * @return int
    */
    int updateAllById(ConSaleOrderCategory entity);

    /**
     * 更新多个
     * @param list List ConSaleOrderCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSaleOrderCategory> list);


}
