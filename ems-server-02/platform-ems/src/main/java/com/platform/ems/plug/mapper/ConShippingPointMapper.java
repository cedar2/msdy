package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConShippingPoint;

/**
 * 装运点Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConShippingPointMapper  extends BaseMapper<ConShippingPoint> {


    ConShippingPoint selectConShippingPointById(Long sid);

    List<ConShippingPoint> selectConShippingPointList(ConShippingPoint conShippingPoint);

    /**
     * 添加多个
     * @param list List ConShippingPoint
     * @return int
     */
    int inserts(@Param("list") List<ConShippingPoint> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConShippingPoint
    * @return int
    */
    int updateAllById(ConShippingPoint entity);

    /**
     * 更新多个
     * @param list List ConShippingPoint
     * @return int
     */
    int updatesAllById(@Param("list") List<ConShippingPoint> list);


}
