package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConStockType;

/**
 * 库存类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConStockTypeMapper  extends BaseMapper<ConStockType> {


    ConStockType selectConStockTypeById(Long sid);

    List<ConStockType> selectConStockTypeList(ConStockType conStockType);

    /**
     * 添加多个
     * @param list List ConStockType
     * @return int
     */
    int inserts(@Param("list") List<ConStockType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConStockType
    * @return int
    */
    int updateAllById(ConStockType entity);

    /**
     * 更新多个
     * @param list List ConStockType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConStockType> list);


}
