package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDeliveryCategory;

/**
 * 交货类别Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDeliveryCategoryMapper  extends BaseMapper<ConDeliveryCategory> {


    ConDeliveryCategory selectConDeliveryCategoryById(Long sid);

    List<ConDeliveryCategory> selectConDeliveryCategoryList(ConDeliveryCategory conDeliveryCategory);

    /**
     * 添加多个
     * @param list List ConDeliveryCategory
     * @return int
     */
    int inserts(@Param("list") List<ConDeliveryCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDeliveryCategory
    * @return int
    */
    int updateAllById(ConDeliveryCategory entity);

    /**
     * 更新多个
     * @param list List ConDeliveryCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDeliveryCategory> list);


}
