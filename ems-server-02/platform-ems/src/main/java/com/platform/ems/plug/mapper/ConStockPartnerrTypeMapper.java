package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConStockPartnerrType;

/**
 * 类型_库存合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConStockPartnerrTypeMapper  extends BaseMapper<ConStockPartnerrType> {


    ConStockPartnerrType selectConStockPartnerrTypeById(Long sid);

    List<ConStockPartnerrType> selectConStockPartnerrTypeList(ConStockPartnerrType conStockPartnerrType);

    /**
     * 添加多个
     * @param list List ConStockPartnerrType
     * @return int
     */
    int inserts(@Param("list") List<ConStockPartnerrType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConStockPartnerrType
    * @return int
    */
    int updateAllById(ConStockPartnerrType entity);

    /**
     * 更新多个
     * @param list List ConStockPartnerrType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConStockPartnerrType> list);


}
