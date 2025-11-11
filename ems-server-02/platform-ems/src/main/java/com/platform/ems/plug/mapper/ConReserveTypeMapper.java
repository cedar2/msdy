package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConReserveType;

/**
 * 预留类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConReserveTypeMapper  extends BaseMapper<ConReserveType> {


    ConReserveType selectConReserveTypeById(Long sid);

    List<ConReserveType> selectConReserveTypeList(ConReserveType conReserveType);

    /**
     * 添加多个
     * @param list List ConReserveType
     * @return int
     */
    int inserts(@Param("list") List<ConReserveType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConReserveType
    * @return int
    */
    int updateAllById(ConReserveType entity);

    /**
     * 更新多个
     * @param list List ConReserveType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConReserveType> list);


}
